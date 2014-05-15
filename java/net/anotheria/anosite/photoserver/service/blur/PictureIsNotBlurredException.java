package net.anotheria.anosite.photoserver.service.blur;

import net.anotheria.anosite.photoserver.service.blur.persistence.PictureIsNotBlurredPersistenceException;

/**
 * PictureIsNotBlurred exception.
 *
 * @author h3ll
 */
public class PictureIsNotBlurredException extends BlurSettingsServiceException {
	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = -7758325061741661227L;

	/**
	 * Constructor.
	 *
	 * @param albumId   album id
	 * @param pictureId picture id
	 * @param userId	user id
	 * @param e		 {@link PictureIsNotBlurredPersistenceException}
	 */
	public PictureIsNotBlurredException(long albumId, long pictureId, String userId, PictureIsNotBlurredPersistenceException e) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is not blurred for user[" + userId + "].", e);
	}

	/**
	 * Constructor.
	 *
	 * @param albumId   album id
	 * @param pictureId picture id
	 * @param userId	user id
	 */
	public PictureIsNotBlurredException(long albumId, long pictureId, String userId) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is not blurred for user[" + userId + "].");
	}

	/**
	 * Constructor.
	 *
	 * @param albumId   album id
	 * @param pictureId picture id
	 * @param e		 {@link PictureIsNotBlurredPersistenceException}
	 */
	public PictureIsNotBlurredException(long albumId, long pictureId, PictureIsNotBlurredPersistenceException e) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is not blurred.", e);
	}

	/**
	 * Constructor.
	 *
	 * @param albumId   album id
	 * @param pictureId picture id
	 */
	public PictureIsNotBlurredException(long albumId, long pictureId) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is not blurred.");
	}
}
