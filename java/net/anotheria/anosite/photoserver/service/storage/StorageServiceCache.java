package net.anotheria.anosite.photoserver.service.storage;

import net.anotheria.anoprise.cache.Cache;
import net.anotheria.anoprise.cache.CacheProducerWrapper;
import net.anotheria.anoprise.cache.Caches;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;
import net.anotheria.moskito.core.logging.DefaultStatsLogger;
import net.anotheria.moskito.core.logging.IntervalStatsLogger;
import net.anotheria.moskito.core.logging.SL4JLogOutput;
import net.anotheria.moskito.core.stats.DefaultIntervals;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * StorageService cache.
 * Cache for StorageService. Contains 3 caches . - albumsCache - maps id of album to id of album owner (user); - photosCache - maps id of photo to id of photo
 * owner (user); - userAlbums - maps id of User to internal UserMediaData object which holds all required properties, methods, etc..
 * NOTE : each cache method will return cloned result!
 *
 * @author h3ll
 * @version $Id: $Id
 */
public final class StorageServiceCache {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(StorageServiceCache.class);

	/**
	 * Cache start size constant.
	 */
	private static final int CACHE_START_SIZE = 1000;

	/**
	 * Cache max size constant.
	 */
	private static final int CACHE_MAX_SIZE = 5000;

	/**
	 * Photo cache. <a>Photo.id to userId</a>.
	 */
	private Cache<Long, String> photosCache;

	/**
	 * Albums cache. <a>Album id to userId</a>
	 */
	private Cache<Long, String> albumsCache;

	/**
	 * User data cache - UserId to userMediaData.
	 */
	private Cache<String, UserMediaData> userDataCache;

	/**
	 * Lock.
	 */
	private static Object lock = new Object();

	/**
	 * Constructor.
	 */
	protected StorageServiceCache() {
		photosCache = createCache("ano-site-photoserver-storageservice-photo-cache");
		albumsCache = createCache("ano-site-photoserver-storageservice-albums-cache");
		userDataCache = createCache("ano-site-photoserver-storageservice-userdata-cache");
	}

	/**
	 * Generic cache creation with attaching moskito statistic and loggers.
	 * 
	 * @param <K>
	 *            - key
	 * @param <V>
	 *            - value
	 * @param configFileName
	 *            configuration file name
	 * @return create cache instance
	 */
	private <K, V> Cache<K, V> createCache(String configFileName) {
		Cache<K, V> cache;
		if (StringUtils.isEmpty(configFileName)) {
			LOG.warn("Illegal fileName - relying on defaults.");
			cache = Caches.createHardwiredCache(configFileName, CACHE_START_SIZE, CACHE_MAX_SIZE);
		} else
			try {
				cache = Caches.createConfigurableHardwiredCache(configFileName);
			} catch (IllegalArgumentException e) {
				LOG.warn("Can't find cache configuration for " + configFileName + ", falling back to min cache.");
				cache = Caches.createHardwiredCache(configFileName, CACHE_START_SIZE, CACHE_MAX_SIZE);
			}

		CacheProducerWrapper cacheWrapper = new CacheProducerWrapper(cache, configFileName, "cache", "default");
		new DefaultStatsLogger(cacheWrapper, new SL4JLogOutput(LoggerFactory.getLogger("MoskitoDefault")));
		new IntervalStatsLogger(cacheWrapper, DefaultIntervals.FIVE_MINUTES, new SL4JLogOutput(LoggerFactory.getLogger("Moskito5m")));
		new IntervalStatsLogger(cacheWrapper, DefaultIntervals.FIFTEEN_MINUTES, new SL4JLogOutput(LoggerFactory.getLogger("Moskito15m")));
		new IntervalStatsLogger(cacheWrapper, DefaultIntervals.ONE_HOUR, new SL4JLogOutput(LoggerFactory.getLogger("Moskito1h")));
		new IntervalStatsLogger(cacheWrapper, DefaultIntervals.ONE_DAY, new SL4JLogOutput(LoggerFactory.getLogger("Moskito1d")));

		return cache;
	}

	// ###### Album related methods!!! -- START

	/**
	 * Returns cached {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO} , with selected id.
	 *
	 * @param albumId
	 *            id of album
	 * @return {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO}
	 */
	protected AlbumBO getCachedAlbumById(final long albumId) {
		String albumOwnerId = albumsCache.get(albumId);
		if (StringUtils.isEmpty(albumOwnerId))
			// nothing is cached
			return null;

		return getCachedAlbum(albumOwnerId, albumId);
	}

	/**
	 * Return album with selected id if such was cached.
	 * 
	 * @param userId
	 *            id of album owner
	 * @param albumId
	 *            id of album
	 * @return AlbumBO
	 */
	private AlbumBO getCachedAlbum(final String userId, final long albumId) {
		UserMediaData data = userDataCache.get(userId);
		if (data == null)
			return null;

		return data.getAlbumById(albumId);
	}

	/**
	 * Put/update {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO} in cache.
	 *
	 * @param toCache
	 *            {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO}
	 */
	protected void updateItem(final AlbumBO toCache) {
		if (toCache == null)
			return;

		albumsCache.put(toCache.getId(), toCache.getUserId());
		updateUserData(toCache);
	}

	/**
	 * Update/Add {@link AlbumBO} to cached user data. If no data exists - it will be created.
	 * 
	 * @param toCache
	 *            {@link AlbumBO} item to cache
	 */
	private void updateUserData(final AlbumBO toCache) {
		final String userId = toCache.getUserId();
		if (StringUtils.isEmpty(userId))
			return;
		UserMediaData data = getUserMediaData(userId);
		data.addDataItem(toCache);
	}

	/**
	 * Remove {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO} from all caches!
	 *
	 * @param toRemove
	 *            {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO}
	 */
	protected void removeItem(final AlbumBO toRemove) {
		if (toRemove == null)
			return;

		albumsCache.remove(toRemove.getId());
		removeItemFromCachedUserData(toRemove);
	}

	/**
	 * Remove {@link AlbumBO} from cached UserData if it's present. If album is default - it's reference also will be removed!
	 * 
	 * @param toRemove
	 *            {@link AlbumBO}
	 */
	private void removeItemFromCachedUserData(final AlbumBO toRemove) {
		final String userId = toRemove.getUserId();
		if (StringUtils.isEmpty(userId))
			return;
		UserMediaData data = userDataCache.get(userId);
		if (data == null)
			return;

		data.removeItem(toRemove);
	}

	/**
	 * Returns default album {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO} for selected user, if such was cached.
	 *
	 * @param userId
	 *            id of user
	 * @return default {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO}
	 */
	protected AlbumBO getDefaultAlbum(String userId) {
		if (StringUtils.isEmpty(userId))
			return null;
		UserMediaData data = userDataCache.get(userId);
		if (data == null)
			return null;

		return data.getDefaultAlbum();
	}

	/**
	 * Return all user albums - if such data was cached.
	 *
	 * @param userId
	 *            id of user
	 * @return {@link java.util.List} albums collection
	 */
	protected List<AlbumBO> getAllAlbums(String userId) {
		if (StringUtils.isEmpty(userId))
			return null;
		UserMediaData data = userDataCache.get(userId);
		if (data == null)
			return null;
		// call to all albums!
		return data.getCachedAlbums();
	}

	/**
	 * Cache all user albums!
	 *
	 * @param userId
	 *            id of user
	 * @param allAlbums
	 *            all user albums collection
	 */
	protected void cacheAlbums(String userId, List<AlbumBO> allAlbums) {
		if (StringUtils.isEmpty(userId))
			return;
		if (allAlbums == null || allAlbums.isEmpty())
			return;

		UserMediaData data = getUserMediaData(userId);
		data.addAllAlbums(allAlbums);

		// updating Albums cache!!!
		for (AlbumBO album : allAlbums)
			albumsCache.put(album.getId(), album.getUserId());
	}

	// ###### Album related methods!!! -- END

	// ###### PHOTO related methods!!! -- START

	/**
	 * Returns cached {@link net.anotheria.anosite.photoserver.service.storage.PhotoBO} , with selected id.
	 *
	 * @param photoId
	 *            id of photo
	 * @return {@link net.anotheria.anosite.photoserver.service.storage.PhotoBO}
	 */
	protected PhotoBO getPhotoById(final long photoId) {
		String photoOwnerId = photosCache.get(photoId);
		if (StringUtils.isEmpty(photoOwnerId))
			// nothing is cached
			return null;

		return getCachedPhoto(photoOwnerId, photoId);
	}

	/**
	 * Return photo with selected id if such was cached.
	 * 
	 * @param userId
	 *            id of album owner
	 * @param photoId
	 *            id of photo
	 * @return PhotoBO
	 */
	private PhotoBO getCachedPhoto(final String userId, final long photoId) {
		if (StringUtils.isEmpty(userId))
			return null;
		if (photoId <= 0)
			return null;

		UserMediaData data = userDataCache.get(userId);
		if (data == null)
			return null;

		return data.getPhotoById(photoId);
	}

	/**
	/**
	 * Put/update {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO} in cache.
	 *
	 * @param toCache
	 *            {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO}
	 */
	protected void updateItem(PhotoBO toCache) {
		if (toCache == null)
			return;

		photosCache.put(toCache.getId(), toCache.getUserId());
		updateUserData(toCache);
	}

	/**
	 * Update/Add {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO} to cached user data. If no data exists - it will be created.
	 *
	 * @param toCache
	 *            {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO} item to cache
	 */
	protected void updateUserData(final PhotoBO toCache) {
		final String userId = toCache.getUserId();
		if (StringUtils.isEmpty(userId))
			return;
		UserMediaData data = getUserMediaData(userId);
		data.addDataItem(toCache);
	}

	/**
	 * Remove {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO} from all caches!
	 *
	 * @param toRemove
	 *            {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO}
	 */
	protected void removeItem(final PhotoBO toRemove) {
		if (toRemove == null)
			return;

		photosCache.remove(toRemove.getId());
		removeItemFromCachedUserData(toRemove);
	}

	/**
	 * Remove {@link PhotoBO} from cached UserData if it's present.
	 * 
	 * @param toRemove
	 *            {@link PhotoBO}
	 */
	private void removeItemFromCachedUserData(final PhotoBO toRemove) {
		final String userId = toRemove.getUserId();
		if (StringUtils.isEmpty(userId))
			return;
		UserMediaData data = userDataCache.get(userId);
		if (data == null)
			return;

		data.removeItem(toRemove);
	}

	/**
	 * Update {@link net.anotheria.anosite.photoserver.shared.ApprovalStatus} for selected photos, in case when those photos are cached.
	 *
	 * @param statuses
	 *            Photo id to ApprovalStatus collection
	 */
	protected void updatePhotoApprovalsStatuses(final Map<Long, ApprovalStatus> statuses) {
		for (Long photoId : statuses.keySet()) {
			ApprovalStatus status = statuses.get(photoId);
			if (photoId != null && status != null) {
				String ownerId = photosCache.get(photoId);
				// only if user exists
				if (!StringUtils.isEmpty(ownerId))
					updatePhotoApprovalsStatuses(ownerId, photoId, status);
			}
		}
	}

	/**
	 * Update PhotoApproval status in cache.
	 * 
	 * @param userId
	 *            id of user
	 * @param photoId
	 *            id of photo
	 * @param status
	 *            {@link ApprovalStatus} status to set
	 */
	private void updatePhotoApprovalsStatuses(String userId, long photoId, ApprovalStatus status) {
		if (StringUtils.isEmpty(userId)) {
			LOG.debug("Illegal incoming parameters userId[" + userId + "]");
			return;
		}
		UserMediaData data = userDataCache.get(userId);
		if (data == null)
			return;
		if (photoId <= 0) {
			LOG.debug("Illegal incoming parameters photoId[" + userId + "]");
			return;
		}
		PhotoBO photo = data.getPhotoById(photoId);

		// if status is current - leaving !
		if (photo.getApprovalStatus() == status)
			return;

		// remove photo!
		data.removeItem(photo);
		// status update;
		photo.setApprovalStatus(status);
		data.addDataItem(photo);
	}

	/**
	 * Return {@link net.anotheria.anosite.photoserver.service.storage.PhotoBO} with selected id's.
	 *
	 * @param userId - id of the user
	 * @param albumId - id's of photos
	 * @return List with selected PhotoBOs.
	 */
	protected List<PhotoBO> getAllAlbumPhotos(final String userId, final long albumId) {
		if (StringUtils.isEmpty(userId)) {
			LOG.debug("Illegal incoming parameters userId[" + userId + "]");
			return null;
		}
		if (albumId <= 0) {
			LOG.warn("Illegal incoming parameters albumId[" + albumId + "]");
			return null;
		}
		UserMediaData data = userDataCache.get(userId);
		if (data == null)
			return null;

		return data.getAllPhotosFromAlbum(albumId);
	}

	/**
	 * Add to cache all photos from selected album! NOTE - if all this photos are already in cache - they will be replaced!
	 *
	 * @param userId
	 *            id of user
	 * @param albumId
	 *            id of album
	 * @param allPhotos
	 *            all user photos collection
	 */
	protected void addAlbumPhotosToCache(final String userId, final long albumId, final List<PhotoBO> allPhotos) {
		if (StringUtils.isEmpty(userId)) {
			LOG.debug("Illegal incoming parameters userId[" + userId + "]");
			return;
		}
		if (albumId <= 0) {
			LOG.debug("Illegal incoming parameters albumId[" + albumId + "]");
			return;
		}

		if (allPhotos == null || allPhotos.isEmpty()) {
			LOG.debug("Illegal incoming parameters photosCollection[" + allPhotos + "]");
			return;
		}

		UserMediaData data = getUserMediaData(userId);
		data.putPhotosPhotosToCache(albumId, allPhotos);

		// updating Albums cache!!!
		for (PhotoBO photo : allPhotos)
			photosCache.put(photo.getId(), photo.getUserId());
	}

	// ###### PHoTO related methods!!! -- END

	private UserMediaData getUserMediaData(final String userId) {
		if (StringUtils.isEmpty(userId))
			return null;
		UserMediaData data = userDataCache.get(userId);
		synchronized (lock) {
			data = userDataCache.get(userId);
			if (data == null) {
				data = new UserMediaData(userId);
				userDataCache.put(userId, data);
			}
		}

		return data;
	}

	/**
	 * User media data class. Internal bean for User Albums, Photos, and few other properties hold.
	 */
	private static class UserMediaData {
		/**
		 * Id of the user.
		 */
		private String userId;
		/**
		 * Default user album.
		 */
		private Long defaultAlbumId;
		/**
		 * User albums.
		 */
		private Map<Long, AlbumBO> userAlbums;
		/**
		 * User photos.
		 */
		private Map<Long, PhotoBO> userPhotos;
		/**
		 * True - if all albums for some user were loaded from storage.
		 */
		private boolean isAllUserAlbumsLoaded;
		/**
		 * Contains album id to Boolean mapping. If value for some album is TRUE- this means that all pictures where load to cache!
		 */
		private Map<Long, Boolean> readAllAlbumPhotosPermission = new ConcurrentHashMap<Long, Boolean>();

		/**
		 * Constructor.
		 * 
		 * @param userId
		 *            id of the user
		 */
		public UserMediaData(String userId) {
			this.userId = userId;
		}

		public String getUserId() {
			return userId;
		}

		/**
		 * Add album to User cached data. Album well be cloned inside.
		 * 
		 * @param album
		 *            {@link AlbumBO}
		 */
		protected void addDataItem(AlbumBO album) {
			if (userAlbums == null)
				userAlbums = new ConcurrentHashMap<Long, AlbumBO>();

			userAlbums.put(album.getId(), album.clone());
			if (album.isDefault())
				defaultAlbumId = album.getId();
		}

		/**
		 * Add photo to User cached data. Photo will be cloned inside.
		 * 
		 * @param photo
		 *            {@link PhotoBO}
		 */
		protected void addDataItem(PhotoBO photo) {
			if (userPhotos == null)
				userPhotos = new ConcurrentHashMap<Long, PhotoBO>();

			userPhotos.put(photo.getId(), photo.clone());
		}

		/**
		 * Return album with selected id - if such exists. If result exists - it will be cloned before return.
		 * 
		 * @param albumId
		 *            id of album
		 * @return {@link AlbumBO}
		 */
		protected AlbumBO getAlbumById(long albumId) {
			if (userAlbums == null)
				return null;

			AlbumBO result = userAlbums.get(albumId);
			// clone before return
			if (result != null)
				return result.clone();

			return null;
		}

		/**
		 * Return photo with selected id - if such exists. If result exists - it will be cloned before return.
		 * 
		 * @param photoId
		 *            id of photo
		 * @return {@link PhotoBO}
		 */
		protected PhotoBO getPhotoById(long photoId) {
			if (userPhotos == null)
				return null;

			PhotoBO result = userPhotos.get(photoId);
			// clone before return
			if (result != null)
				return result.clone();

			return null;
		}

		/**
		 * Remove {@link AlbumBO} from Albums. If current Album is default, defaultAlbumId will be reset.
		 * 
		 * @param toRemove
		 *            {@link AlbumBO}
		 */
		protected void removeItem(AlbumBO toRemove) {
			// removing if default
			if (toRemove.isDefault())
				defaultAlbumId = null;

			if (userAlbums == null)
				return;

			userAlbums.remove(toRemove.getId());
			// remove read all photos permission
			readAllAlbumPhotosPermission.remove(toRemove.getId());
		}

		/**
		 * Remove {@link PhotoBO} from cached photos.
		 * 
		 * @param toRemove
		 *            {@link PhotoBO}
		 */
		protected void removeItem(PhotoBO toRemove) {
			if (userPhotos == null)
				return;

			userPhotos.remove(toRemove.getId());
		}

		/**
		 * Returns default user album {@link AlbumBO}.
		 * 
		 * @return {@link AlbumBO}
		 */
		protected AlbumBO getDefaultAlbum() {
			if (defaultAlbumId == null)
				return null;

			return getAlbumById(defaultAlbumId);
		}

		/**
		 * Return all cached user albums "all data is cloned!" - if all was cached! In case if "isAllUserAlbumsLoaded" is false - null will be returned. NOTE :
		 * Collection of cloned elements will be returned.
		 * 
		 * @return List<AlbumBO>
		 */
		protected List<AlbumBO> getCachedAlbums() {
			if (userAlbums == null || !isAllUserAlbumsLoaded)
				return null;

			List<AlbumBO> result = new ArrayList<AlbumBO>();
			for (AlbumBO cachedAlbum : userAlbums.values())
				result.add(cachedAlbum.clone());

			return result;
		}

		/**
		 * Return {@link java.util.List<PhotoBO>} all photos for selected album. If readAllAlbumPhotosPermission does not contain selected AlbumId, or
		 * permissions - is false, then null will be returned. NUll will be also returned in case when UserPhotos were not yet initialized!
		 * 
		 * @param albumId
		 *            id of album
		 * @return {@link java.util.List<PhotoBO>}
		 */
		protected List<PhotoBO> getAllPhotosFromAlbum(final long albumId) {
			boolean isLoaded = readAllAlbumPhotosPermission.get(albumId) != null && readAllAlbumPhotosPermission.get(albumId);
			// if photos for selected album were not loaded to cache -- returning null!

			if (!isLoaded || userPhotos == null)
				return null;

			List<PhotoBO> albumPhotos = new ArrayList<PhotoBO>();
			List<PhotoBO> allPhotos = new ArrayList<PhotoBO>(userPhotos.values());
			for (PhotoBO photo : allPhotos)
				if (albumId == photo.getAlbumId())
					albumPhotos.add(photo.clone());

			return albumPhotos;
		}

		/**
		 * Add all user albums to cache.
		 * 
		 * @param allAlbums
		 *            all user albums collection
		 */
		protected void addAllAlbums(final List<AlbumBO> allAlbums) {
			for (AlbumBO album : allAlbums)
				addDataItem(album);

			isAllUserAlbumsLoaded = true;
		}

		/**
		 * Put to cache all photos from some selected album.
		 * 
		 * @param albumId
		 *            id of album
		 * @param albumPhotos
		 *            all photos form album
		 */
		protected void putPhotosPhotosToCache(long albumId, final List<PhotoBO> albumPhotos) {
			for (PhotoBO photo : albumPhotos)
				addDataItem(photo);

			readAllAlbumPhotosPermission.put(albumId, true);
		}

		@Override
		public boolean equals(Object o) {
			return this == o || (o instanceof UserMediaData) && UserMediaData.class.cast(o).getUserId().equals(getUserId());
		}

		@Override
		public int hashCode() {
			return userId != null ? userId.hashCode() : 0;
		}

		@Override
		public String toString() {
			return "UserMediaData{" + "userId=" + userId + ", defaultAlbumId=" + defaultAlbumId + ", userAlbums=" + userAlbums + ", userPhotos=" + userPhotos
					+ ", isAllUserAlbumsLoaded=" + isAllUserAlbumsLoaded + ", readAllAlbumPhotosPermission=" + readAllAlbumPhotosPermission + '}';
		}

	}

}
