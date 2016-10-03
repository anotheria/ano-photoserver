package net.anotheria.anosite.photoserver.api.blur;

/**
 * BlurSettingsAPI exception.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class BlurSettingsAPIException extends Exception {

	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = -810691914697537498L;

	/**
	 * Constructor.
	 *
	 * @param message
	 *            cause message
	 */
	public BlurSettingsAPIException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message
	 *            cause message
	 * @param cause
	 *            cause
	 */
	public BlurSettingsAPIException(String message, Throwable cause) {
		super(message, cause);
	}
}
