package net.anotheria.anosite.photoserver.api.blur;

/**
 * PictureIsNotBlurredAPI exception.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class PictureIsNotBlurredAPIException extends BlurSettingsAPIException {

	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = -4415367078844383367L;

	/**
	 * Constructor.
	 *
	 * @param albumId
	 *            album id
	 * @param pictureId
	 *            picture id
	 * @param userId
	 *            user id
	 * @param e
	 *            cause
	 */
	public PictureIsNotBlurredAPIException(long albumId, long pictureId, String userId, Throwable e) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is not blurred for user[" + userId + "].", e);
	}

	/**
	 * Constructor.
	 *
	 * @param albumId
	 *            album id
	 * @param pictureId
	 *            picture id
	 * @param e
	 *            cause
	 */
	public PictureIsNotBlurredAPIException(long albumId, long pictureId, Throwable e) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is not blurred.", e);
	}

}
