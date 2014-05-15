package net.anotheria.anosite.photoserver.service.storage.persistence;

/**
 * StoragePersistenceServiceException exception.
 * @author dzhmud
 */
public class StoragePersistenceServiceException extends Exception {

    /**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 4631170826204480568L;

	/**
     * Constructs a new exception with the specified detail message. 
     * @param   message   the detail message.
     */
    public StoragePersistenceServiceException(String message) {
    	super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause. 
     * @param  message the detail message.
     * @param  cause the cause.
     */
    public StoragePersistenceServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     * @param  cause the cause.
     */
    public StoragePersistenceServiceException(Throwable cause) {
        super(cause);
    }
}
