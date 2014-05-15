package net.anotheria.anosite.photoserver.service.storage;

/**
 * Storage service exception for throwing if requested photo not found.
 * 
 * @author Alexandr Bolbat
 */
public class PhotoNotFoundServiceException extends StorageServiceException {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -796017339444419109L;

	/**
	 * Public constructor.
	 * 
	 * @param photoId
	 *            - photo id
	 */
	public PhotoNotFoundServiceException(long photoId) {
		super("Photo[" + photoId + "] not found.");
	}

}
