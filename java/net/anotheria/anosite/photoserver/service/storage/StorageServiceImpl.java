package net.anotheria.anosite.photoserver.service.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.photoserver.service.storage.event.EventAnnouncer;
import net.anotheria.anosite.photoserver.service.storage.persistence.PhotoNotFoundPersistenceServiceException;
import net.anotheria.anosite.photoserver.service.storage.persistence.StoragePersistenceService;
import net.anotheria.anosite.photoserver.service.storage.persistence.StoragePersistenceServiceException;
import net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumNotFoundPersistenceServiceException;
import net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceService;
import net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceException;
import net.anotheria.anosite.photoserver.service.storage.persistence.album.DefaultAlbumNotFoundPersistenceServiceException;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;
import net.anotheria.anosite.photoserver.shared.PhotoServerConfig;
import net.anotheria.moskito.aop.annotation.Accumulate;
import net.anotheria.moskito.aop.annotation.Accumulates;
import net.anotheria.moskito.aop.annotation.Monitor;
import net.anotheria.util.StringUtils;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * {@link StorageService} implementation.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
@Monitor(category = "service", subsystem = "photostorage")
@Accumulates({
		@Accumulate(valueName = "Avg", intervalName = "5m"),
		@Accumulate(valueName = "Avg", intervalName = "1h"),
		@Accumulate(valueName = "Req", intervalName = "5m"),
		@Accumulate(valueName = "Req", intervalName = "1h"),
		@Accumulate(valueName = "Err", intervalName = "5m"),
		@Accumulate(valueName = "Err", intervalName = "1h"),
		@Accumulate(valueName = "Time", intervalName = "5m"),
		@Accumulate(valueName = "Time", intervalName = "1h")
})
public class StorageServiceImpl implements StorageService {

	/**
	 * Logger instance for StorageServiceImpl class.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(StorageServiceImpl.class);

	/**
	 * Lock manager for safe operations.
	 */
	private static final IdBasedLockManager<String> LOCK_MANAGER = new SafeIdBasedLockManager<>();

	/**
	 * Album lock suffix.
	 */
	private static final String ALBUM = "_ALBUM";
	/**
	 * User lock suffix.
	 */
	private static final String USER = "_USER";
	/**
	 * Photo lock suffix.
	 */
	private static final String PHOTO = "_PHOTO";

	/**
	 * {@link StoragePersistenceService} instance.
	 */
	private final StoragePersistenceService persistenceService;

	/**
	 * {@link AlbumPersistenceService} instance.
	 */
	private final AlbumPersistenceService albumPersistenceService;

	/**
	 * {@link StorageServiceCache} cache.
	 */
	private final StorageServiceCache cache;

	/**
	 * {@link PhotoServerConfig} instance.
	 */
	private final PhotoServerConfig configuration;

	/**
	 * Events announcer.
	 */
	private final EventAnnouncer announcer;

	/**
	 * Constructor.
	 */
	protected StorageServiceImpl() {
		try {
			persistenceService = MetaFactory.get(StoragePersistenceService.class);
			albumPersistenceService = MetaFactory.get(AlbumPersistenceService.class);
		} catch (MetaFactoryException mfe) {
			LOG.error(MarkerFactory.getMarker("FATAL"), "Can't init StoragePersistenceService", mfe);
			throw new RuntimeException("Can't init StoragePersistenceService", mfe);
		}
		cache = new StorageServiceCache();
		announcer = new EventAnnouncer();
		configuration = PhotoServerConfig.getInstance();
	}

	/** {@inheritDoc} */
	@Override
	public AlbumBO getAlbum(final long albumId) throws StorageServiceException {
		try {
			AlbumBO album = cache.getCachedAlbumById(albumId);
			if (album != null)
				return album;

			album = albumPersistenceService.getAlbum(albumId);
			// put to cache
			cache.updateItem(album);
			return album;
		} catch (AlbumNotFoundPersistenceServiceException e) {
			throw new AlbumNotFoundServiceException(albumId);
		} catch (AlbumPersistenceServiceException e) {
			String message = "getAlbum(" + albumId + ") fail.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		}
	}

	@Override
	public String getAlbumOwnerId(long albumId) throws StorageServiceException {
		return cache.getAlbumOwnerId(albumId);
	}

	/** {@inheritDoc} */
	@Override
	public List<AlbumBO> getAlbums(final String userId) throws StorageServiceException {
		try {
			List<AlbumBO> result = cache.getAllAlbums(userId);
			if (result != null)
				return result;

			result = albumPersistenceService.getAlbums(userId);

			// cache data
			cache.cacheAlbums(userId, result);
			return result;
		} catch (AlbumPersistenceServiceException e) {
			String message = "getAlbums(" + userId + ") fail.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		}
	}

	@Override
	public boolean hasPhotos(String userId) {
		if (!cache.isAllUserAlbumsLoaded(userId)) {
			List<AlbumBO> result = new ArrayList<>();
			try {
				result = albumPersistenceService.getAlbums(userId);
			} catch (AlbumPersistenceServiceException e) {
				String message = "hasPhotos(" + userId + ") fail.";
				LOG.warn(message, e);
			}
			cache.cacheAlbums(userId, result);
		}
		return cache.hasPhotos(userId);
	}

	/** {@inheritDoc} */
	@Override
	public AlbumBO getDefaultAlbum(final String userId) throws StorageServiceException {
		IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(userId + USER);
		lock.lock();
		try {
			AlbumBO defaultAlbum = cache.getDefaultAlbum(userId);
			if (defaultAlbum != null)
				return defaultAlbum;

			return albumPersistenceService.getDefaultAlbum(userId);
		} catch (DefaultAlbumNotFoundPersistenceServiceException e) {
			AlbumBO defaultAlbum = new AlbumBO();
			defaultAlbum.setUserId(userId);
			defaultAlbum.setDefault(true);

			return createAlbumInternally(defaultAlbum);
		} catch (AlbumPersistenceServiceException e) {
			String message = "getDefaultAlbum(" + userId + ") fail.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public AlbumBO createAlbum(final AlbumBO album) throws StorageServiceException {
		if (album == null)
			throw new IllegalArgumentException("Null album");
		if (album.isDefault())
			throw new StorageServiceException("For retrieving default album use getDefaultAlbum(userId) method.");

		return createAlbumInternally(album);
	}

	/**
	 * Creates album.
	 *
	 * @param album
	 *            {@link AlbumBO}
	 * @return created {@link AlbumBO}
	 * @throws StorageServiceException
	 *             on errors
	 */
	private AlbumBO createAlbumInternally(final AlbumBO album) throws StorageServiceException {
		IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(album.getId() + ALBUM);
		lock.lock();
		try {
			AlbumBO created = albumPersistenceService.createAlbum(album);
			// cache
			cache.updateItem(created);
			return created;
		} catch (AlbumPersistenceServiceException e) {
			String message = "createAlbumInternally(" + album + ") fail.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public AlbumBO updateAlbum(final AlbumBO album) throws StorageServiceException {
		if (album == null)
			throw new IllegalArgumentException("Null album");

		IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(album.getId() + ALBUM);
		lock.lock();
		try {
			albumPersistenceService.updateAlbum(album);

			// removing album from cache! -- afterwards it will be placed back!
			cache.removeItem(album);
			return getAlbum(album.getId());
		} catch (AlbumPersistenceServiceException e) {
			String message = "updateAlbum(" + album + ") fail.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public AlbumBO removeAlbum(final long albumId) throws StorageServiceException {
		AlbumBO albumToRemove = getAlbum(albumId);
		List<PhotoBO> albumPhotos = getPhotos(albumToRemove.getUserId(), albumToRemove.getId());
		if (!albumPhotos.isEmpty())
			throw new AlbumWithPhotosServiceException(albumId);

		IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(albumId + ALBUM);
		lock.lock();
		try {
			albumPersistenceService.deleteAlbum(albumId);

			// removing album from cache!
			cache.removeItem(albumToRemove);
			return albumToRemove;
		} catch (AlbumPersistenceServiceException e) {
			String message = "removeAlbum(" + albumId + ") fail.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public PhotoBO getPhoto(final long photoId) throws StorageServiceException {
		try {
			PhotoBO photo = cache.getPhotoById(photoId);
			if (photo != null)
				return photo;

			photo = persistenceService.getPhoto(photoId);

			// put to cache
			cache.updateItem(photo);
			return photo;
		} catch (PhotoNotFoundPersistenceServiceException e) {
			LOG.warn("getPhoto(" + photoId + ") failed. Photo not found in persistence.", e);
			throw new PhotoNotFoundServiceException(photoId);
		} catch (StoragePersistenceServiceException e) {
			String message = "getPhoto(" + photoId + ") failed. Underlying StoragePersistenceService thrown exception.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public PhotoBO getDefaultPhoto(String ownerId) throws StorageServiceException {
		return getDefaultPhoto(getDefaultAlbum(ownerId));
	}

	/** {@inheritDoc} */
	@Override
	public PhotoBO getDefaultPhoto(String userId, long albumId) throws StorageServiceException {
		AlbumBO album = getAlbum(albumId);
		if (album.getUserId().equals(userId))
			return getDefaultPhoto(album);
		// this is actually more runtime exception! then Checked!
		throw new StorageServiceException("Album[" + albumId + "] does not belongs to User[" + userId + "]");

	}

	/** {@inheritDoc} */
	@Override
	public List<PhotoBO> getPhotos(final String userId, final long albumId) throws StorageServiceException {
		try {
			AlbumBO album = getAlbum(albumId);
			if (album == null)
				return Collections.emptyList();

			List<PhotoBO> result = cache.getAllAlbumPhotos(userId, albumId);
			if (result != null)
				return result;

			result = persistenceService.getUserPhotos(userId, albumId);
			cache.addAlbumPhotosToCache(userId, albumId, result);
			return result;
		} catch (StoragePersistenceServiceException e) {
			String message = "getPhotos(" + userId + ", " + albumId + ") failed.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<PhotoBO> getPhotos(final String userId, final List<Long> photoIDs) throws StorageServiceException {
		if (photoIDs == null || photoIDs.isEmpty())
			throw new IllegalArgumentException("Null photos id's");

		try {
			return persistenceService.getUserPhotos(userId, photoIDs);
		} catch (StoragePersistenceServiceException e) {
			String message = "getPhotos" + userId + ", " + photoIDs + ") failed.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<PhotoBO> getWaitingApprovalPhotos(int photosAmount) throws StorageServiceException {
		if (photosAmount < 0)
			throw new IllegalArgumentException("Illegal photos amount selected amount[" + photosAmount + "]");
		try {
			List<PhotoBO> result = new ArrayList<PhotoBO>(photosAmount);
			List<PhotoBO> fromService = persistenceService.getPhotosWithStatus(photosAmount, ApprovalStatus.WAITING_APPROVAL);

			// sort by user
			while (fromService.size() > 0 && result.size() < photosAmount) {
				PhotoBO photo = fromService.remove(0);
				String userID = photo.getUserId();
				result.add(photo);
				// get all photos of the same user
				for (Iterator<PhotoBO> i = fromService.iterator(); i.hasNext() && result.size() < photosAmount;) {
					PhotoBO toCheck = i.next();
					if (toCheck.getUserId().equals(userID)) {
						result.add(toCheck);
						i.remove();
					}
				}
			}

			return result;
		} catch (StoragePersistenceServiceException e) {
			String message = "getWaitingApprovalPhotos(" + photosAmount + ") failed.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public int getWaitingApprovalPhotosCount() throws StorageServiceException {
		try {
			return persistenceService.getPhotosWithStatusCount(ApprovalStatus.WAITING_APPROVAL);
		} catch (StoragePersistenceServiceException e) {
			String message = "getWaitingApprovalPhotosCount() failed.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public PhotoBO createPhoto(final PhotoBO photo) throws StorageServiceException {
		if (photo == null)
			throw new IllegalArgumentException("Null photo");

		AlbumBO photoAlbum = getAlbum(photo.getAlbumId());

		IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(photo.getId() + PHOTO);
		lock.lock();
		try {
			PhotoBO clonedPhoto = photo.clone();
			clonedPhoto.setModificationTime(System.currentTimeMillis());
			clonedPhoto.setApprovalStatus(ApprovalStatus.WAITING_APPROVAL);

			PhotoBO result = persistenceService.createPhoto(clonedPhoto);

			// put to cache
			cache.updateItem(result);
			announcer.photoCreated(result);

			// updating album photos order - adding new photo to end of the order and saving album
			photoAlbum.addPhotoToPhotoOrder(result.getId());
			updateAlbum(photoAlbum);
			return result;
		} catch (StoragePersistenceServiceException e) {
			String message = "createPhoto(" + photo + ") failed.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public PhotoBO updatePhoto(final PhotoBO photo) throws StorageServiceException {
		if (photo == null)
			throw new IllegalArgumentException("Null photo");

		IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(photo.getId() + PHOTO);
		lock.lock();
		try {
			PhotoBO oldPhoto = getPhoto(photo.getId());
			photo.setModificationTime(System.currentTimeMillis());
			persistenceService.updatePhoto(photo);

			// remove photo from cache!
			cache.removeItem(photo);

			PhotoBO result = getPhoto(photo.getId());
			announcer.photoUpdated(result, oldPhoto);
			return result;
		} catch (PhotoNotFoundPersistenceServiceException e) {
			LOG.warn("updatePhoto(" + photo + ") failed. Photo not found in persistence.", e);
			throw new PhotoNotFoundServiceException(photo.getId());
		} catch (StoragePersistenceServiceException e) {
			String message = "updatePhoto(" + photo + ") failed. Underlying StoragePersistenceService failed.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public synchronized void updatePhotoApprovalStatuses(final Map<Long, ApprovalStatus> statuses) throws StorageServiceException {
		if (statuses == null || statuses.isEmpty())
			throw new IllegalArgumentException("Illegal statuses, incoming param");
		try {
			// map with photo id to UserId mapping!
			Map<Long, String> photoIdToUserId = new HashMap<>();
			// map with current statuses of photos!
			Map<Long, ApprovalStatus> previousStatuses = new HashMap<>();
			// real status update map! We should not execute update - if status which should be set - is already there :)
			Map<Long, ApprovalStatus> statusesToUpdate = new HashMap<>();

			for (Map.Entry<Long, ApprovalStatus> entry : statuses.entrySet()) {
				PhotoBO photo = getPhoto(entry.getKey());
				if (photo.getApprovalStatus() != entry.getValue()) {
					photoIdToUserId.put(entry.getKey(), photo.getUserId());
					previousStatuses.put(entry.getKey(), photo.getApprovalStatus());
					statusesToUpdate.put(entry.getKey(), entry.getValue());
				}
			}
			if (statusesToUpdate.isEmpty())
				return;

			persistenceService.updatePhotoApprovalStatuses(statusesToUpdate);

			// update data in cache!!!
			cache.updatePhotoApprovalsStatuses(statusesToUpdate);

			// sending events!
			for (Map.Entry<Long, ApprovalStatus> entry : statuses.entrySet()) {
				String userId = photoIdToUserId.get(entry.getKey());
				ApprovalStatus previous = previousStatuses.get(entry.getKey());
				if (!StringUtils.isEmpty(userId) && previous != null && entry.getValue() != null)
					announcer.photoStatusChanged(userId, entry.getKey(), entry.getValue(), previous);
			}

		} catch (StoragePersistenceServiceException e) {
			String message = "updatePhotoApprovalStatuses(" + statuses + ") failed. Underlying StoragePersistenceService failed.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public Map<Long, ApprovalStatus> getAlbumPhotosApprovalStatus(long albumId) throws StorageServiceException {
		try {
			// try from cache!
			AlbumBO album = getAlbum(albumId);

			List<PhotoBO> albumPhotos = cache.getAllAlbumPhotos(album.getUserId(), albumId);
			if (albumPhotos != null) {
				// build result
				Map<Long, ApprovalStatus> result = new HashMap<>();
				for (PhotoBO photo : albumPhotos)
					result.put(photo.getId(), photo.getApprovalStatus());

				return result;
			}

			return persistenceService.getAlbumPhotosApprovalStatus(albumId);
		} catch (StoragePersistenceServiceException e) {
			String message = "getAlbumPhotosApprovalStatus(" + albumId + ") failed. Underlying StoragePersistenceService failed.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void removePhoto(final long photoId) throws StorageServiceException {
		IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(photoId + PHOTO);
		lock.lock();
		try {
			final PhotoBO photo = persistenceService.getPhoto(photoId);
			persistenceService.deletePhoto(photoId);

			cache.removeItem(photo);
			announcer.photoDeleted(photoId, photo.getUserId());

			// removing photo from photo album photos order list
			AlbumBO photoAlbum = getAlbum(photo.getAlbumId());
			photoAlbum.removePhotofromPhotoOrder(photoId);
			updateAlbum(photoAlbum);
		} catch (PhotoNotFoundPersistenceServiceException e) {
			LOG.warn("removePhoto(" + photoId + ") failed. Photo not found in persistence.", e);
			throw new PhotoNotFoundServiceException(photoId);
		} catch (StoragePersistenceServiceException e) {
			String message = "removePhoto(" + photoId + ") failed. Underlying StoragePersistenceService failed.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public PhotoBO movePhoto(long photoId, long newAlbumId) throws StorageServiceException {
		IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(photoId + PHOTO);
		lock.lock();
		try {
			PhotoBO oldPhoto = getPhoto(photoId);
			AlbumBO newAlbum = getAlbum(newAlbumId);

			persistenceService.movePhoto(photoId, newAlbum.getId(), System.currentTimeMillis());

			// remove photo from cache!
			cache.removeItem(oldPhoto);

			PhotoBO result = getPhoto(photoId);
			announcer.photoUpdated(result, oldPhoto);
			return result;
		} catch (PhotoNotFoundPersistenceServiceException e) {
			LOG.warn("movePhoto(" + photoId + ") failed. Photo not found in persistence.", e);
			throw new PhotoNotFoundServiceException(photoId);
		} catch (StoragePersistenceServiceException e) {
			String message = "movePhoto(" + photoId + ") failed. Underlying StoragePersistenceService failed.";
			LOG.warn(message, e);
			throw new StorageServiceException(message, e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Return id of the Default photo, which belongs to selected album.
	 *
	 * @param album
	 *            {@link AlbumBO}
	 * @return id of the default photo if such exists
	 * @throws StorageServiceException
	 *             DefaultPhotoNotFoundServiceException - if no such photo found, or on other errors
	 */
	private PhotoBO getDefaultPhoto(AlbumBO album) throws StorageServiceException {
		List<Long> orderedPhotoIdsList = album.getPhotosOrder();

		// reading ordered photos! Searching and returning Default photo - if such will be found!
		for (Long photoId : orderedPhotoIdsList) {
			// just an additional NULL check!
			if (photoId == null) {
				LOG.warn("NULL photo id detected inside ordered photos list! album[" + album.getId() + "]");
				continue;
			}
			try {
				PhotoBO photo = getPhoto(photoId);
				// if approving is disabled - returning photo!
				if (!configuration.isPhotoApprovingEnabled())
					return photo;
				// if Approving is Enabled and photo is Approved returning it
				if (configuration.isPhotoApprovingEnabled() && ApprovalStatus.APPROVED == photo.getApprovalStatus())
					return photo;

			} catch (PhotoNotFoundServiceException phfSe) {
				LOG.warn("Corrupted data detected! photo[" + photoId + "] does not exists! But present inside photos order. SKIPPING!" + phfSe.getMessage());
			}
		}

		List<PhotoBO> allPhotos = getPhotos(album.getUserId(), album.getId());
		// checking that some photos really exists!
		if (allPhotos.isEmpty())
			throw new DefaultPhotoNotFoundServiceException(album.getId(), " Album does not contains photos.");

		if (!configuration.isPhotoApprovingEnabled()) {
			LOG.debug("PhotoApproving is disabled! there is nothing to filter out!");
			return allPhotos.get(0);
		}

		// looking for first approved photo!
		for (PhotoBO photo : allPhotos)
			if (ApprovalStatus.APPROVED == photo.getApprovalStatus())
				return photo;
		// Exception in case - When there is no any approved photo found!
		throw new DefaultPhotoNotFoundServiceException(album.getId(), " Album does not contains approved photos.");
	}

}
