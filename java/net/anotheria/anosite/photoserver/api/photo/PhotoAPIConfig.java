package net.anotheria.anosite.photoserver.api.photo;

import java.io.Serializable;

import net.anotheria.util.StringUtils;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.LoggerFactory;

/**
 * Configuration bean for PhotoAPI.
 * 
 * @author Oliver Toense
 */
@ConfigureMe(name = "ano-site-photoserver-photoapi-config")
public final class PhotoAPIConfig implements Serializable {

	/**
	 * Basic serialVersionUID variable.
	 */
	@DontConfigure
	private static final long serialVersionUID = 1880129668539548059L;

	/**
	 * Configuration instance.
	 */
	@DontConfigure
	private static PhotoAPIConfig INSTANCE;

	/**
	 * List of allowed sizes.
	 */
	@Configure
	private String allowedSizes = "100,200,300";

	/**
	 * Ignores allowed sizes and generate whatever size is requested.
	 */
	@Configure
	private boolean ignoreAllowedSizes = false;

	/**
	 * JPEG Quality in percent.
	 */
	@Configure
	private int JpegQuality = 85;

	/**
	 * Method checks if 'size' is allowed by configuration.
	 * 
	 * @param size
	 * @return true if size is allowed by config, false - otherwise.
	 */
	public boolean isAllowedSize(int size) {
		String[] sizes = StringUtils.tokenize(getAllowedSizes(), ',');
		for (int i = 0; i < sizes.length; i++) {
			if (sizes[i].equals(size + "")) {
				return true;
			}
		}
		return false;
	}

	public String getAllowedSizes() {
		return allowedSizes;
	}

	public void setAllowedSizes(String allowedSizes) {
		this.allowedSizes = allowedSizes;
	}

	public boolean isIgnoreAllowedSizes() {
		return ignoreAllowedSizes;
	}

	public void setIgnoreAllowedSizes(boolean ignoreAllowedSizes) {
		this.ignoreAllowedSizes = ignoreAllowedSizes;
	}

	public int getJpegQuality() {
		return JpegQuality;
	}

	public void setJpegQuality(int jpegQuality) {
		JpegQuality = jpegQuality;
	}

	/**
	 * Get instance method.
	 * 
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAPIConfig}
	 */
	public static synchronized PhotoAPIConfig getInstance() {
		if (INSTANCE == null)
			INSTANCE = new PhotoAPIConfig();

		return INSTANCE;
	}

	/**
	 * Default constructor.
	 */
	private PhotoAPIConfig() {
		try {
			ConfigurationManager.INSTANCE.configure(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(PhotoAPIConfig.class).error("PhotoAPIConfig() Configuration failed. Configuring with defaults.", e);
		}
	}
}
