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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

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

	public int getWorkbenchWidth() {
		return workbenchWidth;
	}

	public void setWorkbenchWidth(int workbenchWidth) {
		this.workbenchWidth = workbenchWidth;
	}

	public String getAllowedMimeTypes() {
		return allowedMimeTypes;
	}

	public void setAllowedMimeTypes(String allowedMimeTypes) {
		this.allowedMimeTypes = allowedMimeTypes;
	}

	public int getJpegQuality() {
		return jpegQuality;
	}

	public void setJpegQuality(int jpegQuality) {
		this.jpegQuality = jpegQuality;
	}

	public boolean isAllowTransparentBackground() {
		return allowTransparentBackground;
	}

	public void setAllowTransparentBackground(boolean allowTransparentBackground) {
		this.allowTransparentBackground = allowTransparentBackground;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public void setMinWidth(int minWidth) {
		this.minWidth = minWidth;
	}

	public int getMinHeight() {
		return minHeight;
	}

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
