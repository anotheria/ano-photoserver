package net.anotheria.anosite.photoserver.api.upload;

import net.anotheria.util.StringUtils;
import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

@ConfigureMe(name = "ano-site-photoserver-uploadapi-config")
public class PhotoUploadAPIConfig implements Serializable {

	/**
	 * Basic serialVersionUID variable.
	 */
	@DontConfigure
	private static final long serialVersionUID = 3404063525038350935L;

	/**
	 * Configuration instance.
	 */
	@DontConfigure
	private static PhotoUploadAPIConfig INSTANCE;

	/**
	 * Image format of the uploaded photo, which will be used during saving it on disk.
	 * Currently supports only jpg and png formats.
	 */
	@Configure
	private String imageWriteFormat = "jpg";

	/**
	 * Maximal size of uploaded file
	 */
	@Configure
	private long maxUploadFileSize = 1024 * 1024 * 10;

	/**
	 * Maximal width of uploaded photo (if larger, it will be scaled to fit into limits)
	 */
	@Configure
	private int maxWidth = 1024;

	/**
	 * Maximal height of uploaded photo (if larger, it will be scaled to fit into limits)
	 */
	@Configure
	private int maxHeight = 1024;

	/**
	 * Width of the photo workbench (size of preview image (width=height), must be squared because of rotation)
	 */
	@Configure
	private int workbenchWidth = 400;

	/**
	 * Allowed mimetypes
	 */
	@Configure
	private String allowedMimeTypes = "image/pjpeg,image/jpeg,image/tiff,image/png,image/gif,image/x-png";

	/**
	 * JPEG Quality in percent
	 */
	@Configure
	private int JpegQuality = 85;

	/**
	 * If {@code true} - transparent background color will be allowed, otherwise - Color.WHITE will be used as background color.
	 */
	@Configure
	private boolean allowTransparentBackground = false;

	public long getMaxUploadFileSize() {
		return maxUploadFileSize;
	}

	public void setMaxUploadFileSize(long maxUploadFileSize) {
		this.maxUploadFileSize = maxUploadFileSize;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	public int getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	public String getAllowedMimeTypes() {
		return allowedMimeTypes;
	}

	public void setAllowedMimeTypes(String allowedMimeTypes) {
		this.allowedMimeTypes = allowedMimeTypes;
	}

	public int getJpegQuality() {
		return JpegQuality;
	}

	public void setJpegQuality(int jpegQuality) {
		JpegQuality = jpegQuality;
	}

	public int getWorkbenchWidth() {
		return workbenchWidth;
	}

	public void setWorkbenchWidth(int workbenchWidth) {
		this.workbenchWidth = workbenchWidth;
	}

	public boolean isAllowedMimeType(String mimeType) {
		String[] types = StringUtils.tokenize(allowedMimeTypes, ',');
        for (String type : types){
            if (mimeType.equalsIgnoreCase(type.trim()))
                return true;
        }
		return false;
	}

    public boolean isAllowedLinkEndType(String link) {
        String[] types = StringUtils.tokenize(allowedMimeTypes, ',');
        for (String type : types){
            if (type.contains("/") && link.contains(type.substring(type.indexOf("/")+1, type.length())))
                return true;
        }
        return false;
    }

	public boolean isAllowTransparentBackground() {
		return allowTransparentBackground;
	}

	public void setAllowTransparentBackground(boolean allowTransparentBackground) {
		this.allowTransparentBackground = allowTransparentBackground;
	}

	public String getImageWriteFormat() {
		return imageWriteFormat;
	}

	public void setImageWriteFormat(String imageWriteFormat) {
		this.imageWriteFormat = imageWriteFormat;
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
	 * Get instance method.
	 * 
	 * @return {@link net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPIConfig}
	 */
	public static synchronized PhotoUploadAPIConfig getInstance() {
		if (INSTANCE == null)
			INSTANCE = new PhotoUploadAPIConfig();

		return INSTANCE;
	}

	/**
	 * Default constructor.
	 */
	private PhotoUploadAPIConfig() {
		try {
			ConfigurationManager.INSTANCE.configure(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(PhotoUploadAPIConfig.class).error("PhotoUploadAPIConfig() Configuration failed. Configuring with defaults.", e);
		}
	}

	@Override
	public String toString() {
		return "PhotoUploadAPIConfig [maxUploadFileSize=" + maxUploadFileSize + "]";
	}

}
