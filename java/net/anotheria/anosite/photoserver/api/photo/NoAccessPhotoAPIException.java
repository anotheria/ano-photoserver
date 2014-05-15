package net.anotheria.anosite.photoserver.api.photo;

/**
 * Security {@link PhotoAPI} exception.
 * 
 * @author Alexandr Bolbat
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
