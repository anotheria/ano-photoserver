package net.anotheria.anosite.photoserver.service.storage.persistence.album;

/**
 * AlbumPersistenceService exception.
 *
 * @author dzhmud
 * @version $Id: $Id
 */
public class AlbumPersistenceServiceException extends Exception {

    /**
	 * Generated serial version UID.
	 */
	private static final long serialVersionUID = 4631170826204480568L;

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param   message   the detail message.
     */
    public AlbumPersistenceServiceException(String message) {
    	super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param  message the detail message.
     * @param  cause the cause.
     */
    public AlbumPersistenceServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param  cause the cause.
     */
    public AlbumPersistenceServiceException(Throwable cause) {
        super(cause);
    }
}
