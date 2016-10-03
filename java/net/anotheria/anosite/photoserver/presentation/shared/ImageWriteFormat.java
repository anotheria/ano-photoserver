package net.anotheria.anosite.photoserver.presentation.shared;

import org.slf4j.LoggerFactory;

/**
 * Supported image write formats.
 *
 * @author another
 * @version $Id: $Id
 */
public enum ImageWriteFormat {
	/**
	 * JPEG image format.
	 */
	JPEG("JPEG", "image/jpeg"),
	/**
	 * PNG image format.
	 */
	PNG("PNG", "image/png");
	/**
	 * Default value.
	 */
	public static final ImageWriteFormat DEFAULT = JPEG;
	/**
	 * Format string value.
	 */
	private String value;
	/**
	 * Image content type.
	 */
	private String contentType;

	/**
	 * Constructor.
	 *
	 * @param value       format value
	 * @param contentType image content type
	 */
	ImageWriteFormat(final String value, final String contentType) {
		this.value = value;
		this.contentType = contentType;
	}

	/**
	 * <p>Getter for the field <code>value</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * <p>Getter for the field <code>contentType</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * Returns image format by incoming string value.
	 * If image format was not found, {@link net.anotheria.anosite.photoserver.presentation.shared.ImageWriteFormat#DEFAULT} will be returned.
	 *
	 * @param value image format string value
	 * @return {@link net.anotheria.anosite.photoserver.presentation.shared.ImageWriteFormat}
	 */
	public static ImageWriteFormat getByValue(final String value) {
		for (ImageWriteFormat format : ImageWriteFormat.values())
			if (format.getValue().equalsIgnoreCase(value))
				return format;

		LoggerFactory.getLogger(ImageWriteFormat.class).error("ImageWriteFormat with value[" + value + "] not found. Relying on defaults.");
		return DEFAULT;
	}
}
