package net.anotheria.anosite.photoserver.api.upload;

import net.anotheria.util.StringUtils;
import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * <p>PhotoUploadAPIConfig class.</p>
 *
 * @author another
 * @version $Id: $Id
 */
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

	/**
	 * <p>Getter for the field <code>maxUploadFileSize</code>.</p>
	 *
	 * @return a long.
	 */
	public long getMaxUploadFileSize() {
		return maxUploadFileSize;
	}

	/**
	 * <p>Setter for the field <code>maxUploadFileSize</code>.</p>
	 *
	 * @param maxUploadFileSize a long.
	 */
	public void setMaxUploadFileSize(long maxUploadFileSize) {
		this.maxUploadFileSize = maxUploadFileSize;
	}

	/**
	 * <p>Getter for the field <code>maxWidth</code>.</p>
	 *
	 * @return a int.
	 */
	public int getMaxWidth() {
		return maxWidth;
	}

	/**
	 * <p>Setter for the field <code>maxWidth</code>.</p>
	 *
	 * @param maxWidth a int.
	 */
	public void setMaxWidth(int maxWidth) {
		this.maxWidth = maxWidth;
	}

	/**
	 * <p>Getter for the field <code>maxHeight</code>.</p>
	 *
	 * @return a int.
	 */
	public int getMaxHeight() {
		return maxHeight;
	}

	/**
	 * <p>Setter for the field <code>maxHeight</code>.</p>
	 *
	 * @param maxHeight a int.
	 */
	public void setMaxHeight(int maxHeight) {
		this.maxHeight = maxHeight;
	}

	/**
	 * <p>Getter for the field <code>allowedMimeTypes</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getAllowedMimeTypes() {
		return allowedMimeTypes;
	}

	/**
	 * <p>Setter for the field <code>allowedMimeTypes</code>.</p>
	 *
	 * @param allowedMimeTypes a {@link java.lang.String} object.
	 */
	public void setAllowedMimeTypes(String allowedMimeTypes) {
		this.allowedMimeTypes = allowedMimeTypes;
	}

	/**
	 * <p>getJpegQuality.</p>
	 *
	 * @return a int.
	 */
	public int getJpegQuality() {
		return JpegQuality;
	}

	/**
	 * <p>setJpegQuality.</p>
	 *
	 * @param jpegQuality a int.
	 */
	public void setJpegQuality(int jpegQuality) {
		JpegQuality = jpegQuality;
	}

	/**
	 * <p>Getter for the field <code>workbenchWidth</code>.</p>
	 *
	 * @return a int.
	 */
	public int getWorkbenchWidth() {
		return workbenchWidth;
	}

	/**
	 * <p>Setter for the field <code>workbenchWidth</code>.</p>
	 *
	 * @param workbenchWidth a int.
	 */
	public void setWorkbenchWidth(int workbenchWidth) {
		this.workbenchWidth = workbenchWidth;
	}

	/**
	 * <p>isAllowedMimeType.</p>
	 *
	 * @param mimeType a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public boolean isAllowedMimeType(String mimeType) {
		String[] types = StringUtils.tokenize(allowedMimeTypes, ',');
        for (String type : types){
            if (mimeType.equalsIgnoreCase(type.trim()))
                return true;
        }
		return false;
	}

    /**
     * <p>isAllowedLinkEndType.</p>
     *
     * @param link a {@link java.lang.String} object.
     * @return a boolean.
     */
    public boolean isAllowedLinkEndType(String link) {
        String[] types = StringUtils.tokenize(allowedMimeTypes, ',');
        for (String type : types){
            if (type.contains("/") && link.contains(type.substring(type.indexOf("/")+1, type.length())))
                return true;
        }
        return false;
    }

	/**
	 * <p>isAllowTransparentBackground.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isAllowTransparentBackground() {
		return allowTransparentBackground;
	}

	/**
	 * <p>Setter for the field <code>allowTransparentBackground</code>.</p>
	 *
	 * @param allowTransparentBackground a boolean.
	 */
	public void setAllowTransparentBackground(boolean allowTransparentBackground) {
		this.allowTransparentBackground = allowTransparentBackground;
	}

	/**
	 * <p>Getter for the field <code>imageWriteFormat</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getImageWriteFormat() {
		return imageWriteFormat;
	}

	/**
	 * <p>Setter for the field <code>imageWriteFormat</code>.</p>
	 *
	 * @param imageWriteFormat a {@link java.lang.String} object.
	 */
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

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "PhotoUploadAPIConfig [maxUploadFileSize=" + maxUploadFileSize + "]";
	}

}
