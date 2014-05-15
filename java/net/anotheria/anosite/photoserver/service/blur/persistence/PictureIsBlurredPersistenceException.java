package net.anotheria.anosite.photoserver.service.blur.persistence;

/**
 * PictureIsBlurred exception.
 *
 * @author h3ll
 */
public class PictureIsBlurredPersistenceException extends BlurSettingsPersistenceServiceException {
	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = 1779599640116715985L;

	/**
	 * Constructor.
	 *
	 * @param albumId   id of album
	 * @param pictureId id of picture
	 * @param userId	id of user
	 */
	public PictureIsBlurredPersistenceException(long albumId, long pictureId, String userId) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is already blurred for user[" + userId + "].");
	}

	/**
	 * Constructor.
	 *
	 * @param albumId   id of album
	 * @param pictureId id of picture
	 */
	public PictureIsBlurredPersistenceException(long albumId, long pictureId) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is already blurred for all users.");
	}

}
