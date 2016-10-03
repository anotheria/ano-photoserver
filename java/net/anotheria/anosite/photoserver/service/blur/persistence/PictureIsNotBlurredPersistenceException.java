package net.anotheria.anosite.photoserver.service.blur.persistence;

/**
 * PictureIsNotBlurred exception.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class PictureIsNotBlurredPersistenceException extends BlurSettingsPersistenceServiceException {
	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = 7817095357510488754L;

	/**
	 * Constructor.
	 *
	 * @param albumId   id of album
	 * @param pictureId id of picture
	 * @param userId    id of user
	 */
	public PictureIsNotBlurredPersistenceException(long albumId, long pictureId, String userId) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is not blurred for user[" + userId + "].");
	}

	/**
	 * Constructor.
	 *
	 * @param albumId   id of album
	 * @param pictureId id of picture
	 */
	public PictureIsNotBlurredPersistenceException(long albumId, long pictureId) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is not blurred.");
	}
}
