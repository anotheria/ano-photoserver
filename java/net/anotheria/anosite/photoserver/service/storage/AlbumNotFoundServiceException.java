package net.anotheria.anosite.photoserver.service.storage;

/**
 * Storage service exception for throwing if requested album not found.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class AlbumNotFoundServiceException extends StorageServiceException {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -2306527553927426680L;

	/**
	 * Public constructor.
	 *
	 * @param albumId
	 *            - album id
	 */
	public AlbumNotFoundServiceException(long albumId) {
		super("Album[" + albumId + "] not found.");
	}

}
