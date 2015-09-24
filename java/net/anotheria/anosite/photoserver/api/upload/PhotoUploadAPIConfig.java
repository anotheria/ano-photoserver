package net.anotheria.anosite.photoserver.api.upload;

import net.anotheria.util.StringUtils;
import org.configureme.ConfigurationManager;
import org.configureme.annotations.AfterReConfiguration;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * {@link PhotoUploadAPI} config.
 */
@ConfigureMe(name = "ano-site-photoserver-uploadapi-config")
public final class PhotoUploadAPIConfig implements Serializable {
	/**
	 * Basic serialVersionUID variable.
	 */
	@DontConfigure
	private static final long serialVersionUID = 3404063525038350935L;
	/**
	 * Configuration instance.
	 */
	@DontConfigure
	private static PhotoUploadAPIConfig instance;
	/**
	 * Image format of the uploaded photo, which will be used during saving it on disk.
	 * Currently supports only jpg and png formats.
	 */
	@Configure
	private String imageWriteFormat = "jpg";
	/**
	 * {@link PhotoTypeConfig} collection.
	 */
	@Configure
	private PhotoTypeConfig[] photoTypes;
	/**
	 * Photo type name to {@link PhotoTypeConfig} instance mappings.
	 */
	private ConcurrentMap<String, PhotoTypeConfig> photoTypeNamesToPhotoTypeConfigs = new ConcurrentHashMap<String, PhotoTypeConfig>();
	/**
	 * {@link PhotoTypeConfig} default config.
	 */
	private static final PhotoTypeConfig defaultPhotoTypeConfig = new PhotoTypeConfig();

	/**
	 * Get instance method.
	 *
	 * @return {@link PhotoUploadAPIConfig}
	 */
	public static synchronized PhotoUploadAPIConfig getInstance() {
		if (instance == null)
			instance = new PhotoUploadAPIConfig();

		return instance;
	}

	public String getImageWriteFormat() {
		return imageWriteFormat;
	}

	public void setImageWriteFormat(String imageWriteFormat) {
		this.imageWriteFormat = imageWriteFormat;
	}

	public PhotoTypeConfig[] getPhotoTypes() {
		return photoTypes;
	}

	public void setPhotoTypes(PhotoTypeConfig[] photoTypes) {
		this.photoTypes = photoTypes;
	}

	/**
	 * Default constructor.
	 */
	private PhotoUploadAPIConfig() {
		try {
			ConfigurationManager.INSTANCE.configure(this);
			configurePhotoTypeConfigMappings();
		} catch (Exception e) {
			LoggerFactory.getLogger(PhotoUploadAPIConfig.class).error("PhotoUploadAPIConfig() Configuration failed. Configuring with defaults.", e);
		}
	}

	/**
	 * Used for configuring mappings - photo type name to {@link PhotoTypeConfig} instance.
	 */
	@AfterReConfiguration
	public void configurePhotoTypeConfigMappings() {
		photoTypeNamesToPhotoTypeConfigs.clear();

		if (photoTypes == null)
			return;

		for (PhotoTypeConfig photoTypeConfig : photoTypes)
			photoTypeNamesToPhotoTypeConfigs.put(photoTypeConfig.getType().toUpperCase(), photoTypeConfig);
	}

	/**
	 * Get file prefix.
	 *
	 * @return file prefix
	 */
	public String getFilePrefix() {
		return "." + imageWriteFormat;
	}

	/**
	 * Resolves {@link PhotoTypeConfig} by given photo type name.
	 * If {@link PhotoTypeConfig} was not found then {@link #defaultPhotoTypeConfig} will be returned.
	 *
	 * @param photoType name of the photo type
	 * @return {@link PhotoTypeConfig}
	 */
	public PhotoTypeConfig resolvePhotoTypeConfig(final String photoType) {
		if (StringUtils.isEmpty(photoType) || !photoTypeNamesToPhotoTypeConfigs.containsKey(photoType.toUpperCase()))
			return defaultPhotoTypeConfig;

		return photoTypeNamesToPhotoTypeConfigs.get(photoType.toUpperCase());
	}

	@Override
	public String toString() {
		return "PhotoUploadAPIConfig{" +
				"imageWriteFormat='" + imageWriteFormat + '\'' +
				", photoTypes=" + Arrays.toString(photoTypes) +
				'}';
	}
}
