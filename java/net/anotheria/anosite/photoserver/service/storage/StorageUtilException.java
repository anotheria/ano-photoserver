package net.anotheria.anosite.photoserver.service.storage;

/**
 * Storage utility exception.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class StorageUtilException extends Exception {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 8425554746173884202L;

	/**
	 * Public constructor.
	 *
	 * @param message
	 *            - exception message
	 */
	public StorageUtilException(String message) {
		super(message);
	}

	/**
	 * Public constructor.
	 *
	 * @param cause
	 *            - exception cause
	 */
	public StorageUtilException(Throwable cause) {
		super(cause);
	}

	/**
	 * Public constructor.
	 *
	 * @param message
	 *            - exception message
	 * @param cause
	 *            - exception cause
	 */
	public StorageUtilException(String message, Throwable cause) {
		super(message, cause);
	}

}
