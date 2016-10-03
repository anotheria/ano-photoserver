package net.anotheria.anosite.photoserver.service.blur.persistence;

/**
 * AlbumIsBlurred exception.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class AlbumIsBlurredPersistenceException extends BlurSettingsPersistenceServiceException {

	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = 8967705927482388280L;

	/**
	 * Constructor.
	 *
	 * @param albumId id of album
	 */
	public AlbumIsBlurredPersistenceException(long albumId) {
		super("Album " + albumId + " is already blurred for all users ");
	}

	/**
	 * Constructor.
	 *
	 * @param albumId id of album
	 * @param userId  id of user
	 */
	public AlbumIsBlurredPersistenceException(long albumId, String userId) {
		super("Album " + albumId + " is already blurred for user{" + userId + "} ");
	}
}
