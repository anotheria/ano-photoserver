package net.anotheria.anosite.photoserver.service.blur;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.photoserver.service.blur.cache.BlurSettingsCache;
import net.anotheria.anosite.photoserver.service.blur.persistence.AlbumIsBlurredPersistenceException;
import net.anotheria.anosite.photoserver.service.blur.persistence.AlbumIsNotBlurredPersistenceException;
import net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceService;
import net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceException;
import net.anotheria.anosite.photoserver.service.blur.persistence.PictureIsBlurredPersistenceException;
import net.anotheria.anosite.photoserver.service.blur.persistence.PictureIsNotBlurredPersistenceException;
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
 * BlurSettingsService implementation.
 *
 * @author h3ll
 * @version $Id: $Id
 */
@Monitor(category = "service", subsystem = "blursettings")
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
public class BlurSettingsServiceImpl implements BlurSettingsService {
	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(BlurSettingsServiceImpl.class);
	/**
	 * {@link BlurSettingsCache}
	 */
	private BlurSettingsCache cache;
	/**
	 * {@link net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceService} instance.
	 */
	private BlurSettingsPersistenceService blurSettingsPersistenceService;
	/**
	 * {@link IdBasedLockManager} instance.
	 */
	private static final IdBasedLockManager LOCK_MANAGER = new SafeIdBasedLockManager();

	/**
	 * Constructor.
	 */
	protected BlurSettingsServiceImpl() {
		try {
			blurSettingsPersistenceService = MetaFactory.get(BlurSettingsPersistenceService.class);
		} catch (MetaFactoryException e) {
			LOG.error(MarkerFactory.getMarker("FATAL"), "BlurSettingsServiceImpl init failure", e);
			throw new RuntimeException("BlurSettingsServiceImpl init failure", e);
		}
		cache = BlurSettingsCache.getInstance();
	}

	/** {@inheritDoc} */
	@Override
	public Map<Long, BlurSettingBO> readBlurSettings(long albumId, List<Long> pictureIds, String userId) throws BlurSettingsServiceException {
		if (albumId <= 0)
			throw new IllegalArgumentException("Invalid album id{" + albumId + "}");
		if (StringUtils.isEmpty(userId))
			throw new IllegalArgumentException("Invalid userId {" + userId + "}");
		if (pictureIds == null || pictureIds.isEmpty())
			throw new IllegalArgumentException("Invalid pictureId's collection");
		Map<Long, BlurSettingBO> result = new HashMap<Long, BlurSettingBO>(pictureIds.size());
		for (Long pictureId : pictureIds) {
			if (pictureId == null)
				throw new IllegalArgumentException("Invalid pictureId");
			result.put(pictureId, readBlurSetting(albumId, pictureId, userId));
		}
		return result;

	}

	/** {@inheritDoc} */
	@Override
	public Map<Long, BlurSettingBO> readBlurSettings(long albumId, List<Long> pictureIds) throws BlurSettingsServiceException {
		if (albumId <= 0)
			throw new IllegalArgumentException("Invalid album id{" + albumId + "}");
		if (pictureIds == null || pictureIds.isEmpty())
			throw new IllegalArgumentException("Invalid pictureId's collection");

		Map<Long, BlurSettingBO> result = new HashMap<Long, BlurSettingBO>(pictureIds.size());
		for (Long pictureId : pictureIds) {
			if (pictureId == null)
				throw new IllegalArgumentException("Invalid pictureId");
			result.put(pictureId, readBlurSetting(albumId, pictureId, BlurSettingBO.ALL_USERS_DEFAULT_CONSTANT));
		}
		return result;
	}

	/**
	 * Read picture blur settings, for specified user.
	 * 
	 * @param albumId
	 *            id of album (picture belongs to this album)
	 * @param pictureId
	 *            id of picture
	 * @param userId
	 *            id of user
	 * @return {@link BlurSettingBO}
	 * @throws BlurSettingsServiceException
	 *             on errors
	 */
	private BlurSettingBO readBlurSetting(long albumId, long pictureId, String userId) throws BlurSettingsServiceException {
		if (pictureId <= 0)
			throw new IllegalArgumentException("Invalid picture id{" + pictureId + "}");

		BlurSettingBO result = cache.getCachedSetting(albumId, pictureId, userId);
		if (result != null)
			return result.clone();

		try {
			result = blurSettingsPersistenceService.readBlurSetting(albumId, pictureId, userId);
			if (result == null)
				// not blurred by default
				result = new BlurSettingBO(albumId, pictureId, userId, false);

			cache.updateCacheableItem(albumId, pictureId, userId, result);
			return result;
		} catch (BlurSettingsPersistenceServiceException e) {
			LOG.error("readBlurSetting(" + albumId + "," + pictureId + "," + userId + ")", e);
			throw new BlurSettingsServiceException("BlurSettingsPersistence service  failed.");
		}
	}

	/** {@inheritDoc} */
	@Override
	public void blurAlbum(long albumId) throws BlurSettingsServiceException {
		if (albumId <= 0)
			throw new IllegalArgumentException("Illegal albumId");

		IdBasedLock lock = LOCK_MANAGER.obtainLock(String.valueOf(albumId) + "_A");
		lock.lock();
		try {
			blurSettingsPersistenceService.blurAlbum(albumId);
			cache.cleanAlbumCachedData(albumId);
		} catch (AlbumIsBlurredPersistenceException e) {
			throw new AlbumIsBlurredException(albumId, e);
		} catch (BlurSettingsPersistenceServiceException e) {
			LOG.error("blurAlbum(" + albumId + ")", e);
			throw new BlurSettingsServiceException("Persistence failed", e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void blurAlbum(long albumId, String userId) throws BlurSettingsServiceException {
		if (albumId <= 0)
			throw new IllegalArgumentException("Illegal albumId");
		if (StringUtils.isEmpty(userId))
			throw new IllegalArgumentException("Invalid userId {" + userId + "}");

		IdBasedLock lock = LOCK_MANAGER.obtainLock(String.valueOf(albumId) + "_A");
		lock.lock();
		try {
			blurSettingsPersistenceService.blurAlbum(albumId, userId);
			cache.cleanAlbumCachedData(albumId);

		} catch (AlbumIsBlurredPersistenceException e) {
			throw new AlbumIsBlurredException(albumId, userId, e);
		} catch (BlurSettingsPersistenceServiceException e) {
			LOG.error("blurAlbum(" + albumId + "," + userId + ")", e);
			throw new BlurSettingsServiceException("Persistence failed", e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void blurPicture(long albumId, long pictureId, String userId) throws BlurSettingsServiceException {

		if (albumId <= 0)
			throw new IllegalArgumentException("Invalid album id{" + albumId + "}");
		if (pictureId <= 0)
			throw new IllegalArgumentException("Invalid picture id{" + pictureId + "}");
		if (StringUtils.isEmpty(userId))
			throw new IllegalArgumentException("Invalid userId {" + userId + "}");

		BlurSettingBO blur = cache.getCachedSetting(albumId, pictureId, userId);
		if (blur != null && blur.isBlurred())
			throw new PictureIsBlurredException(albumId, pictureId, userId);

		IdBasedLock lock = LOCK_MANAGER.obtainLock(String.valueOf(pictureId) + "_P");
		lock.lock();
		try {
			blurSettingsPersistenceService.blurPicture(albumId, pictureId, userId);

			// Updating cache! - cause now picture is blurred! for sure!
			cache.updateCacheableItem(albumId, pictureId, userId, new BlurSettingBO(albumId, pictureId, userId, true));
		} catch (PictureIsBlurredPersistenceException e) {
			throw new PictureIsBlurredException(albumId, pictureId, userId, e);
		} catch (BlurSettingsPersistenceServiceException e) {
			LOG.error("blurPicture(" + albumId + ", " + pictureId + "," + userId + ")", e);
			throw new BlurSettingsServiceException("Persistence failed", e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void blurPicture(long albumId, long pictureId) throws BlurSettingsServiceException {
		if (albumId <= 0)
			throw new IllegalArgumentException("Invalid album id{" + albumId + "}");
		if (pictureId <= 0)
			throw new IllegalArgumentException("Invalid picture id{" + pictureId + "}");

		IdBasedLock lock = LOCK_MANAGER.obtainLock(String.valueOf(pictureId) + "_P");
		lock.lock();
		try {
			blurSettingsPersistenceService.blurPicture(albumId, pictureId);
			// cache invalidate!
			cache.cleanPictureCachedData(albumId, pictureId);
		} catch (PictureIsBlurredPersistenceException e) {
			throw new PictureIsBlurredException(albumId, pictureId, e);
		} catch (BlurSettingsPersistenceServiceException e) {
			LOG.error("blurPicture(" + albumId + ", " + pictureId + ")", e);
			throw new BlurSettingsServiceException("Persistence failed", e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void unBlurAlbum(long albumId) throws BlurSettingsServiceException {
		if (albumId <= 0)
			throw new IllegalArgumentException("Invalid album id{" + albumId + "}");

		IdBasedLock lock = LOCK_MANAGER.obtainLock(String.valueOf(albumId) + "_A");
		lock.lock();
		try {
			blurSettingsPersistenceService.unBlurAlbum(albumId);
			// cache invalidate!
			cache.cleanAlbumCachedData(albumId);
		} catch (AlbumIsNotBlurredPersistenceException e) {
			throw new AlbumIsNotBlurredException(albumId, e);
		} catch (BlurSettingsPersistenceServiceException e) {
			LOG.error("unBlurAlbum(" + albumId + ")", e);
			throw new BlurSettingsServiceException("Persistence failed", e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void unBlurAlbum(long albumId, String userId) throws BlurSettingsServiceException {
		if (albumId <= 0)
			throw new IllegalArgumentException("Invalid album id{" + albumId + "}");
		if (StringUtils.isEmpty(userId))
			throw new IllegalArgumentException("Invalid userId {" + userId + "}");

		IdBasedLock lock = LOCK_MANAGER.obtainLock(String.valueOf(albumId) + "_A");
		lock.lock();
		try {
			blurSettingsPersistenceService.unBlurAlbum(albumId, userId);

			// cache invalidate!
			cache.cleanAlbumCachedData(albumId);
		} catch (AlbumIsNotBlurredPersistenceException e) {
			throw new AlbumIsNotBlurredException(albumId, userId, e);
		} catch (BlurSettingsPersistenceServiceException e) {
			LOG.error("unBlurAlbum(" + albumId + ")", e);
			throw new BlurSettingsServiceException("Persistence failed", e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void unBlurPicture(long albumId, long pictureId, String userId) throws BlurSettingsServiceException {
		if (albumId <= 0)
			throw new IllegalArgumentException("Invalid album id{" + albumId + "}");
		if (pictureId <= 0)
			throw new IllegalArgumentException("Invalid picture id{" + pictureId + "}");
		if (StringUtils.isEmpty(userId))
			throw new IllegalArgumentException("Invalid userId {" + userId + "}");

		BlurSettingBO blur = cache.getCachedSetting(albumId, pictureId, userId);
		if (blur != null && !blur.isBlurred())
			throw new PictureIsNotBlurredException(albumId, pictureId, userId);

		IdBasedLock lock = LOCK_MANAGER.obtainLock(String.valueOf(pictureId) + "_P");
		lock.lock();
		try {
			blurSettingsPersistenceService.unBlurPicture(albumId, pictureId, userId);
			// Updating cache! - cause now picture is not blurred! for sure!
			cache.updateCacheableItem(albumId, pictureId, userId, new BlurSettingBO(albumId, pictureId, userId, false));

		} catch (PictureIsNotBlurredPersistenceException e) {
			throw new PictureIsNotBlurredException(albumId, pictureId, userId, e);
		} catch (BlurSettingsPersistenceServiceException e) {
			LOG.error("blurPicture(" + albumId + ", " + pictureId + "," + userId + ")", e);
			throw new BlurSettingsServiceException("Persistence failed", e);
		} finally {
			lock.unlock();
		}

	}

	/** {@inheritDoc} */
	@Override
	public void unBlurPicture(long albumId, long pictureId) throws BlurSettingsServiceException {
		if (albumId <= 0)
			throw new IllegalArgumentException("Invalid album id{" + albumId + "}");
		if (pictureId <= 0)
			throw new IllegalArgumentException("Invalid picture id{" + pictureId + "}");

		IdBasedLock lock = LOCK_MANAGER.obtainLock(String.valueOf(pictureId) + "_P");
		lock.lock();
		try {
			blurSettingsPersistenceService.unBlurPicture(albumId, pictureId);
			// cache invalidate!
			cache.cleanPictureCachedData(albumId, pictureId);
		} catch (PictureIsNotBlurredPersistenceException e) {
			throw new PictureIsNotBlurredException(albumId, pictureId, e);
		} catch (BlurSettingsPersistenceServiceException e) {
			LOG.error("blurPicture(" + albumId + ", " + pictureId + ")", e);
			throw new BlurSettingsServiceException("Persistence failed", e);
		} finally {
			lock.unlock();
		}
	}

	/** {@inheritDoc} */
	@Override
	public void removeBlurSettings(long albumId) throws BlurSettingsServiceException {
		if (albumId <= 0)
			throw new IllegalArgumentException("Invalid album id{" + albumId + "}");

		IdBasedLock lock = LOCK_MANAGER.obtainLock(String.valueOf(albumId) + "_A");
		lock.lock();
		try {
			blurSettingsPersistenceService.removeBlurSettings(albumId);
			// invalidate
			cache.cleanAlbumCachedData(albumId);
		} catch (BlurSettingsPersistenceServiceException e) {
			LOG.error("removeBlurSettings(" + albumId + ")", e);
			throw new BlurSettingsServiceException("Persistence failed", e);
		} finally {
			lock.unlock();
		}
	}

}
