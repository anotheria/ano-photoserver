package net.anotheria.anosite.photoserver.service.storage;

/**
 * Storage service exception.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class StorageServiceException extends Exception {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 6285824568530937160L;

	/**
	 * Public constructor.
	 *
	 * @param message
	 *            - exception message
	 */
	public StorageServiceException(String message) {
		super(message);
	}

	/**
	 * Public constructor.
	 *
	 * @param cause
	 *            - exception cause
	 */
	public StorageServiceException(Throwable cause) {
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
	public StorageServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}
