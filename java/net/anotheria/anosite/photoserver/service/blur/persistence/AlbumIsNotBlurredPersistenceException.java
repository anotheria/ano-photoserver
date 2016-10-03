package net.anotheria.anosite.photoserver.service.blur.persistence;

/**
 * AlbumIsNotBlurred exception.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class AlbumIsNotBlurredPersistenceException extends BlurSettingsPersistenceServiceException {

	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = -5653946297882707331L;

	/**
	 * Constructor.
	 *
	 * @param albumId id of album
	 */
	public AlbumIsNotBlurredPersistenceException(long albumId) {
		super("Album{" + albumId + "} is not Blurred, UnBlurring is impossible.");
	}
}
