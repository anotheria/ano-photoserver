package net.anotheria.anosite.photoserver.service.storage;

/**
 * Storage service exception for throwing if we try to remove album with photos.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class AlbumWithPhotosServiceException extends StorageServiceException {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 2310810696287767703L;

	/**
	 * Public constructor.
	 *
	 * @param albumId
	 *            - album id
	 */
	public AlbumWithPhotosServiceException(long albumId) {
		super("Album[" + albumId + "] contain photos.");
	}

}
