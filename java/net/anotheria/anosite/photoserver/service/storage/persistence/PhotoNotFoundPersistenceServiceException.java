package net.anotheria.anosite.photoserver.service.storage.persistence;

/**
 * Exception indicating that photo was not found in persistence when trying to
 * operate with it by specifying explicit ID. 
 * @author dzhmud
 */
public class PhotoNotFoundPersistenceServiceException extends StoragePersistenceServiceException {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 8848638456738149818L;

	/**
	 * Public constructor.
	 * @param photoId - ID of photo that was not found
	 */
	public PhotoNotFoundPersistenceServiceException(long photoId) {
		super("Photo[" + photoId + "] not found.");
	}

}
