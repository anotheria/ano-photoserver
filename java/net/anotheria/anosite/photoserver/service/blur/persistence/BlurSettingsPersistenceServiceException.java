package net.anotheria.anosite.photoserver.service.blur.persistence;

/**
 * BlurSettingsPersistenceService exception.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class BlurSettingsPersistenceServiceException extends Exception {

	/**
	 * Basic serial version uid.
	 */
	private static final long serialVersionUID = -3512585255665711807L;

	/**
	 * Constructor.
	 *
	 * @param message message cause
	 */
	public BlurSettingsPersistenceServiceException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 *
	 * @param message message cause
	 * @param cause   {@link java.lang.Throwable} reason
	 */
	public BlurSettingsPersistenceServiceException(String message, Throwable cause) {
		super(message, cause);
	}
}
