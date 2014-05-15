package net.anotheria.anosite.photoserver.service.blur;

/**
 * BlurSettingsService exception.
 *
 * @author h3ll
 */
public class BlurSettingsServiceException extends Exception {

	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = -8202192757709523004L;

	/**
	 * Constructor.
	 *
	 * @param message cause message
	 */
	public BlurSettingsServiceException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message cause message
	 * @param cause   {@link Throwable} reason
	 */
	public BlurSettingsServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
