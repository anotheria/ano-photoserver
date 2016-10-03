package net.anotheria.anosite.photoserver.api.blur;

/**
 * AlbumIsBlurredAPI exception.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class AlbumIsBlurredAPIException extends BlurSettingsAPIException {
	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = 2038209945184415242L;

	/**
	 * Constructor.
	 *
	 * @param albumId
	 *            id of album
	 * @param e
	 *            cause
	 */
	public AlbumIsBlurredAPIException(long albumId, Throwable e) {
		super("Album[" + albumId + "] is already blurred.", e);
	}

	/**
	 * Constructor.
	 *
	 * @param albumId
	 *            id of album
	 * @param userId
	 *            id of user
	 * @param e
	 *            cause
	 */
	public AlbumIsBlurredAPIException(long albumId, String userId, Throwable e) {
		super("Album[" + albumId + "] is already blurred for user[" + userId + "].", e);
	}
}
