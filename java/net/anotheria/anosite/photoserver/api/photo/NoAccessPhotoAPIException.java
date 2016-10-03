package net.anotheria.anosite.photoserver.api.photo;

/**
 * Security {@link net.anotheria.anosite.photoserver.api.photo.PhotoAPI} exception.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class NoAccessPhotoAPIException extends PhotoAPIException {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 1900038863305004664L;

	/**
	 * Public constructor.
	 *
	 * @param message
	 *            exception message
	 */
	public NoAccessPhotoAPIException(String message) {
		super(message);
	}

}
