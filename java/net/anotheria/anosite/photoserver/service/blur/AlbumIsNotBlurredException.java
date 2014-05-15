package net.anotheria.anosite.photoserver.service.blur;

import net.anotheria.anosite.photoserver.service.blur.persistence.AlbumIsNotBlurredPersistenceException;

/**
 * AlbumIsNotBlurred exception.
 *
 * @author h3ll
 */
public class AlbumIsNotBlurredException extends BlurSettingsServiceException {

	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = -3717556314908955895L;

	/**
	 * Constructor.
	 *
	 * @param albumId id of album
	 * @param e	   {@link AlbumIsNotBlurredPersistenceException} reason
	 */
	public AlbumIsNotBlurredException(long albumId, AlbumIsNotBlurredPersistenceException e) {
		super("Album[" + albumId + "] is not blurred", e);
	}


	/**
	 * Constructor.
	 *
	 * @param albumId id of album
	 * @param userId  id of user
	 * @param e	   {@link AlbumIsNotBlurredPersistenceException} reason
	 */
	public AlbumIsNotBlurredException(long albumId, String userId, AlbumIsNotBlurredPersistenceException e) {
		super("Album[" + albumId + "] is not blurred for user[" + userId + "]", e);
	}


}
