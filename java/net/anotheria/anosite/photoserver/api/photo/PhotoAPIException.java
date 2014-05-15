package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.anoplass.api.APIException;

/**
 * Main upload API exception.
 * 
 * @author Alexandr Bolbat
 */
public class PhotoAPIException extends APIException {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -6461757115965530893L;

	/**
	 * Public constructor.
	 * 
	 * @param message
	 *            exception message
	 */
	public PhotoAPIException(String message) {
		super(message);
	}

	/**
	 * Public constructor.
	 * 
	 * @param message
	 *            exception message
	 * @param cause
	 *            exception cause
	 */
	public PhotoAPIException(String message, Exception cause) {
		super(message, cause);
	}

}
