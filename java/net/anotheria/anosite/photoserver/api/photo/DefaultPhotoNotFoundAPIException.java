package net.anotheria.anosite.photoserver.api.photo;

/**
 * Exception which indicates that for some reason Default photo was not found.
 *
 * @author h3ll
 */
public class DefaultPhotoNotFoundAPIException extends PhotoAPIException {
	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = 7813716476769011896L;

	/**
	 * Constructor.
	 *
	 * @param message message string
	 */
	public DefaultPhotoNotFoundAPIException(String message) {
		super(message);
	}
}
