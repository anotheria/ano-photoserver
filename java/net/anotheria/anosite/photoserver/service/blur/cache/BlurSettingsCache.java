package net.anotheria.anosite.photoserver.service.blur.cache;

import net.anotheria.anoprise.cache.Cache;
import net.anotheria.anoprise.cache.CacheProducerWrapper;
import net.anotheria.anoprise.cache.Caches;
import net.anotheria.anosite.photoserver.service.blur.BlurSettingBO;
import net.anotheria.moskito.core.logging.DefaultStatsLogger;
import net.anotheria.moskito.core.logging.IntervalStatsLogger;
import net.anotheria.moskito.core.logging.SL4JLogOutput;
import net.anotheria.moskito.core.stats.DefaultIntervals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache for BlurSettingsService. Current Cache maps albumId to internal
 * {@link net.anotheria.anosite.photoserver.service.blur.cache.BlurSettingsCache.AlbumBlurSetting}, and represented by
 * {@link net.anotheria.anoprise.cache.RoundRobinHardwiredCache}. Inside - internal
 * {@link net.anotheria.anosite.photoserver.service.blur.cache.BlurSettingsCache.AlbumBlurSetting} class - there is another mapping picture - to
 * {@link net.anotheria.anosite.photoserver.service.blur.cache.BlurSettingsCache.PictureBlurSetting}.
 * {@link net.anotheria.anosite.photoserver.service.blur.cache.BlurSettingsCache.PictureBlurSetting} internally contains mapping for userId -
 * {@link net.anotheria.anosite.photoserver.service.blur.BlurSettingBO}. Each cache update - will replace {@link net.anotheria.anosite.photoserver.service.blur.BlurSettingBO} in selected hierarchy object. If some object does not exists in cache - it will
 * be created on update operation. Etc.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class BlurSettingsCache {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(BlurSettingsCache.class);

	/**
	 * Cache container instance.
	 */
	private static BlurSettingsCache INSTANCE;

	/**
	 * Cache start size constant.
	 */
	private static final int CACHE_START_SIZE = 1000;

	/**
	 * Cache max size constant.
	 */
	private static final int CACHE_MAX_SIZE = 5000;

	/**
	 * Cache itself.
	 */
	private Cache<Long, AlbumBlurSetting> albumCache;

	/**
	 * Lock.
	 */
	private static Object lock = new Object();

	/**
	 * Get instance method.
	 *
	 * @return {@link net.anotheria.anosite.photoserver.service.blur.cache.BlurSettingsCache}
	 */
	public static synchronized BlurSettingsCache getInstance() {
		if (INSTANCE == null)
			INSTANCE = new BlurSettingsCache();

		return INSTANCE;
	}

	/**
	 * Private constructor.
	 */
	private BlurSettingsCache() {
		albumCache = createCache();
	}

	/**
	 * Return cached {@link net.anotheria.anosite.photoserver.service.blur.BlurSettingBO} if such exists. Null otherwise.
	 *
	 * @param albumId
	 *            id of album
	 * @param pictureId
	 *            id of picture
	 * @param userId
	 *            id of user
	 * @return {@link net.anotheria.anosite.photoserver.service.blur.BlurSettingBO} if exists
	 */
	public BlurSettingBO getCachedSetting(long albumId, long pictureId, String userId) {
		if (albumCache.get(albumId) == null)
			return null;

		return albumCache.get(albumId).getBlurSettingForUser(pictureId, userId);
	}

	/**
	 * Update cached value.
	 *
	 * @param albumId
	 *            id of album
	 * @param pictureId
	 *            id of picture
	 * @param userId
	 *            id of user
	 * @param cacheableValue
	 *            {@link net.anotheria.anosite.photoserver.service.blur.BlurSettingBO}
	 */
	public void updateCacheableItem(long albumId, long pictureId, String userId, BlurSettingBO cacheableValue) {
		if (cacheableValue == null) {
			LOG.warn("Illegal - null incoming value!");
			return;
		}

		AlbumBlurSetting albumSetting = albumCache.get(albumId);
		synchronized (lock) {
			albumSetting = albumCache.get(albumId);
			if (albumSetting == null) {
				albumSetting = new AlbumBlurSetting();
				albumCache.put(albumId, albumSetting);
			}
		}

		albumSetting.updateUserBlurSettingForPicture(pictureId, userId, cacheableValue);
	}

	/**
	 * Invalidating cached data for selected album.
	 *
	 * @param albumId
	 *            id of album
	 */
	public void cleanAlbumCachedData(long albumId) {
		albumCache.remove(albumId);
	}

	/**
	 * Remove picture cached data.
	 *
	 * @param albumId
	 *            id of album
	 * @param pictureId
	 *            id of picture
	 */
	public void cleanPictureCachedData(long albumId, long pictureId) {
		AlbumBlurSetting album = albumCache.get(albumId);
		if (album == null)
			return;
		album.removePictureSetting(pictureId);
	}

	/**
	 * Cache clean!
	 */
	public void resetCache() {
		synchronized (this) {
			albumCache.clear();
		}
	}

	/**
	 * Generic cache creation with attaching moskito statistic and loggers.
	 * 
	 * @param <K>
	 *            - key
	 * @param <V>
	 *            - value
	 * @return create cache instance
	 */
	private <K, V> Cache<K, V> createCache() {
		Cache<K, V> cache;
		String configFile = "ano-site-photoserver-blursettingsservice-cache";
		try {
			cache = Caches.createConfigurableHardwiredCache(configFile);
		} catch (IllegalArgumentException e) {
			LOG.warn("Can't find cache configuration for " + configFile + ", falling back to min cache.");
			cache = Caches.createHardwiredCache(configFile, CACHE_START_SIZE, CACHE_MAX_SIZE);
		}

		CacheProducerWrapper cacheWrapper = new CacheProducerWrapper(cache, configFile, "cache", "default");
		new DefaultStatsLogger(cacheWrapper, new SL4JLogOutput(LoggerFactory.getLogger("MoskitoDefault")));
		new IntervalStatsLogger(cacheWrapper, DefaultIntervals.FIVE_MINUTES, new SL4JLogOutput(LoggerFactory.getLogger("Moskito5m")));
		new IntervalStatsLogger(cacheWrapper, DefaultIntervals.FIFTEEN_MINUTES, new SL4JLogOutput(LoggerFactory.getLogger("Moskito15m")));
		new IntervalStatsLogger(cacheWrapper, DefaultIntervals.ONE_HOUR, new SL4JLogOutput(LoggerFactory.getLogger("Moskito1h")));
		new IntervalStatsLogger(cacheWrapper, DefaultIntervals.ONE_DAY, new SL4JLogOutput(LoggerFactory.getLogger("Moskito1d")));

		return cache;
	}

	/**
	 * Inner cache object.
	 */
	private static class AlbumBlurSetting implements Serializable {
		/**
		 * Basic serial version UID.
		 */
		private static final long serialVersionUID = -5010187812084087745L;
		/**
		 * AlbumBlurSetting 'pictureBlurSettingMap'.
		 */
		private Map<Long, PictureBlurSetting> pictureBlurSettingMap = new ConcurrentHashMap<Long, PictureBlurSetting>();

		/**
		 * Return {@link net.anotheria.anosite.photoserver.service.blur.cache.BlurSettingsCache.PictureBlurSetting} for selected picture.
		 * 
		 * @param pictureId
		 *            id of picture
		 * @return {@link net.anotheria.anosite.photoserver.service.blur.cache.BlurSettingsCache.PictureBlurSetting}
		 */
		public PictureBlurSetting getPictureBlurSetting(long pictureId) {
			return pictureBlurSettingMap.get(pictureId);
		}

		/**
		 * Return {@link BlurSettingBO} for selected user, based on selected picture...
		 * 
		 * @param pictureId
		 *            id of picture
		 * @param userId
		 *            id of user
		 * @return {@link BlurSettingBO}
		 */
		private BlurSettingBO getBlurSettingForUser(long pictureId, String userId) {
			PictureBlurSetting pictureBlur = getPictureBlurSetting(pictureId);
			if (pictureBlur != null)
				return pictureBlur.getUserSetting(userId);
			return null;
		}

		/**
		 * Update {@link BlurSettingBO} for selected picture.
		 * 
		 * @param pictureId
		 *            id of picture
		 * @param userId
		 *            id of user to which {@link BlurSettingBO} should be updated
		 * @param setting
		 *            {@link BlurSettingBO}
		 */
		public void updateUserBlurSettingForPicture(long pictureId, String userId, BlurSettingBO setting) {
			PictureBlurSetting pictureBlur = getPictureBlurSetting(pictureId);
			synchronized (lock) {
				pictureBlur = getPictureBlurSetting(pictureId);
				if (pictureBlur == null) {
					pictureBlur = new PictureBlurSetting();
					pictureBlurSettingMap.put(pictureId, pictureBlur);
				}
			}

			pictureBlur.updateUserSetting(userId, setting);
		}

		/**
		 * Remove pictureSetting.
		 * 
		 * @param pictureId
		 *            id of picture
		 */
		public void removePictureSetting(long pictureId) {
			pictureBlurSettingMap.remove(pictureId);
		}
	}

	/**
	 * Inner object for {@link net.anotheria.anosite.photoserver.service.blur.cache.BlurSettingsCache.AlbumBlurSetting}.
	 */
	private static class PictureBlurSetting implements Serializable {
		/**
		 * Basic serial version UID.
		 */
		private static final long serialVersionUID = -7993011785948325720L;
		/**
		 * PictureBlurSetting 'userBlurSetting'. Cached user permissions.. etc.
		 */
		private Map<String, BlurSettingBO> userBlurSetting = new ConcurrentHashMap<String, BlurSettingBO>();

		/**
		 * Return {@link BlurSettingBO} for user with selected id.
		 * 
		 * @param userId
		 *            id of selected user
		 * @return {@link BlurSettingBO}
		 */
		public BlurSettingBO getUserSetting(String userId) {
			return userBlurSetting.get(userId);
		}

		/**
		 * Update {@link BlurSettingBO} for selected user.
		 * 
		 * @param userId
		 *            id of user
		 * @param setting
		 *            {@link BlurSettingBO}
		 */
		public void updateUserSetting(String userId, BlurSettingBO setting) {
			userBlurSetting.put(userId, setting);
		}

	}
}
