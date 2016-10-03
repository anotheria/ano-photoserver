package net.anotheria.anosite.photoserver.api.photo;

/**
 * Exception for throwing if requested album not found.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class AlbumNotFoundPhotoAPIException extends PhotoAPIException {

	/**
	 * Basic serialVersionUID variable.
	 */

	private static final long serialVersionUID = -7351269107510616038L;

	/**
	 * Public constructor.
	 *
	 * @param albumId
	 *            - album id
	 */
	public AlbumNotFoundPhotoAPIException(long albumId) {
		super("Album[" + albumId + "] not found.");
	}

}
