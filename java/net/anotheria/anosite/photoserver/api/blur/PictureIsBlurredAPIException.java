package net.anotheria.anosite.photoserver.api.blur;

/**
 * PictureIsBlurredAPI exception.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class PictureIsBlurredAPIException extends BlurSettingsAPIException {

	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = -9126104266719229948L;

	/**
	 * Constructor.
	 *
	 * @param albumId
	 *            album id
	 * @param pictureId
	 *            d of picture
	 * @param userId
	 *            id of user
	 * @param e
	 *            cause
	 */
	public PictureIsBlurredAPIException(long albumId, long pictureId, String userId, Throwable e) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is already blurred for user[" + userId + "].", e);
	}

	/**
	 * Constructor.
	 *
	 * @param albumId
	 *            album id
	 * @param pictureId
	 *            d of picture
	 * @param e
	 *            cause
	 */
	public PictureIsBlurredAPIException(long albumId, long pictureId, Throwable e) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is already blurred for all users.", e);
	}

}
