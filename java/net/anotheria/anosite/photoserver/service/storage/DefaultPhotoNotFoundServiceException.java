package net.anotheria.anosite.photoserver.service.storage;

/**
 * Default photo not found exception.
 *
 * @author h3ll
 */
public class DefaultPhotoNotFoundServiceException extends StorageServiceException {
	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = -1684046269855808921L;

	/**
	 * Constructor.
	 *
	 * @param albumId album id itself
	 * @param msg	 additional message string
	 */
	public DefaultPhotoNotFoundServiceException(long albumId, String msg) {
		super("Default photo not found in album[" + albumId + "] " + msg);
	}

}
