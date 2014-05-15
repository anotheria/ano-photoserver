package net.anotheria.anosite.photoserver.api.photo;

/**
 * Exception for throwing if we try to remove album with photos..
 * 
 * @author Alexandr Bolbat
 */
public class AlbumWithPhotosPhotoAPIException extends PhotoAPIException {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -7356723249112786817L;

	/**
	 * Public constructor.
	 * 
	 * @param albumId
	 *            - album id
	 */
	public AlbumWithPhotosPhotoAPIException(long albumId) {
		super("Album[" + albumId + "] contain photos.");
	}

}
