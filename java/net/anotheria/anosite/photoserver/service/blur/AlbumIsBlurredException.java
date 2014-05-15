package net.anotheria.anosite.photoserver.service.blur;

import net.anotheria.anosite.photoserver.service.blur.persistence.AlbumIsBlurredPersistenceException;

/**
 * AlbumIsBlurred exception.
 *
 * @author h3ll
 */
public class AlbumIsBlurredException extends BlurSettingsServiceException {

	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = -6487748077669148920L;


	/**
	 * Constructor.
	 *
	 * @param albumId id of album
	 * @param e	   {@link AlbumIsBlurredPersistenceException} reason itself
	 */
	public AlbumIsBlurredException(long albumId, AlbumIsBlurredPersistenceException e) {
		super("Album[" + albumId + "] is already blurred.", e);
	}

	/**
	 * Constructor.
	 *
	 * @param albumId id of album
	 * @param userId  id of user
	 * @param e	   {@link AlbumIsBlurredPersistenceException} reason itself
	 */
	public AlbumIsBlurredException(long albumId, String userId, AlbumIsBlurredPersistenceException e) {
		super("Album[" + albumId + "] is already blurred for user[" + userId + "].", e);
	}
}
