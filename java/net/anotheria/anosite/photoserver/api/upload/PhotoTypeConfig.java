package net.anotheria.anosite.photoserver.api.upload;

import net.anotheria.util.StringUtils;
import org.configureme.annotations.Configure;
import org.configureme.annotations.DontConfigure;

import java.io.Serializable;

/**
 * Config of the photo type, which specifies some restrictions and settings for the photo which can be uploaded.
 * Photo type is just a kind of photo, like the cover photo or the profile photo.
 *
 * @author Illya Bogatyrchuk
 * @version $Id: $Id
 */
public class PhotoTypeConfig implements Serializable {
	/**
	 * Basic serial version UID.
	 */
	@DontConfigure
	private static final long serialVersionUID = -5714838601372111181L;
	/**
	 * Name of the photo type.
	 */
	@Configure
	private String type = "default";
	/**
	 * Maximal size of uploaded file.
	 */
	@Configure
	private long maxUploadFileSize = 1024 * 1024 * 10;
	/**
	 * Maximal width of uploaded photo (if larger, it will be scaled to fit into limits).
	 */
	@Configure
	private int maxWidth = 1024;
	/**
	 * Maximal height of uploaded photo (if larger, it will be scaled to fit into limits).
	 */
	@Configure
	private int maxHeight = 1024;
	/**
	 * Width of the photo workbench (size of preview image (width=height), must be squared because of rotation).
	 */
	@Configure
	private int workbenchWidth = 400;
	/**
	 * Allowed MIME types.
	 */
	@Configure
	private String allowedMimeTypes = "image/pjpeg,image/jpeg,image/tiff,image/png,image/gif,image/x-png";
	/**
	 * JPEG Quality in percent.
	 */
	@Configure
	private int jpegQuality = 85;
	/**
	 * If {@code true} - transparent background color will be allowed, otherwise - Color.WHITE will be used as background color.
	 */
	@Configure
	private boolean allowTransparentBackground = false;
	/**
	 * Minimal width of upload photo.
	 * If photo with less width was provided then error status will be returned.
	 */
	@Configure
	private int minWidth = 0;
	/**
	 * Minimal height of uploaded photo.
	 * If photo with less height was provided then error status will be returned.
	 */
	@Configure
	private int minHeight = 0;

	/**
	 * <p>Getter for the field <code>type</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getType() {
		return type;
	}

	/**
	 * <p>Setter for the field <code>type</code>.</p>
	 *
	 * @param type a {@link java.lang.String} object.
	 */
	public void setType(String type) {
		this.type = type;
	}

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
	 * <p>Getter for the field <code>jpegQuality</code>.</p>
	 *
	 * @return a int.
	 */
	public int getJpegQuality() {
		return jpegQuality;
	}

	/**
	 * <p>Setter for the field <code>jpegQuality</code>.</p>
	 *
	 * @param jpegQuality a int.
	 */
	public void setJpegQuality(int jpegQuality) {
		this.jpegQuality = jpegQuality;
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
	 * <p>Getter for the field <code>minWidth</code>.</p>
	 *
	 * @return a int.
	 */
	public int getMinWidth() {
		return minWidth;
	}

	/**
	 * <p>Setter for the field <code>minWidth</code>.</p>
	 *
	 * @param minWidth a int.
	 */
	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	/**
	 * <p>Getter for the field <code>minHeight</code>.</p>
	 *
	 * @return a int.
	 */
	public int getMinHeight() {
		return minHeight;
	}

	/**
	 * <p>Setter for the field <code>minHeight</code>.</p>
	 *
	 * @param minHeight a int.
	 */
	public void setMinHeight(int minHeight) {
		this.minHeight = minHeight;
	}

	/**
	 * Checks that given MIME type allowed.
	 *
	 * @param mimeType MIME type
	 * @return {@code true} - if MIME type is allowed, {@code false } - otherwise
	 */
	public boolean isAllowedMimeType(final String mimeType) {
		String[] mimeTypes = StringUtils.tokenize(allowedMimeTypes, ',');
		for (String aMimeType : mimeTypes) {
			if (mimeType.equalsIgnoreCase(aMimeType.trim()))
				return true;
		}
		return false;
	}

	/**
	 * Check link contains allowed MIME type.
	 *
	 * @param link URL string
	 * @return {@code true} - if link contains allowed MIME type, {@code false } - otherwise
	 */
	public boolean isAllowedLinkEndType(final String link) {
		String[] mimeTypes = StringUtils.tokenize(allowedMimeTypes, ',');
		for (String mimeType : mimeTypes) {
			if (mimeType.contains("/") && link.contains(mimeType.substring(mimeType.indexOf("/") + 1, mimeType.length())))
				return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "PhotoTypeConfig{" +
				"type='" + type + '\'' +
				", maxUploadFileSize=" + maxUploadFileSize +
				", maxWidth=" + maxWidth +
				", maxHeight=" + maxHeight +
				", workbenchWidth=" + workbenchWidth +
				", allowedMimeTypes='" + allowedMimeTypes + '\'' +
				", jpegQuality=" + jpegQuality +
				", allowTransparentBackground=" + allowTransparentBackground +
				", minWidth=" + minWidth +
				", minHeight=" + minHeight +
				'}';
	}
}
