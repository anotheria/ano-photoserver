package net.anotheria.anosite.photoserver.api.blur;

/**
 * AlbumIsNotBlurredAPI exception.
 * 
 * @author h3ll
 */
public class AlbumIsNotBlurredAPIException extends BlurSettingsAPIException {

	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = 8864356380915511403L;

	/**
	 * Constructor.
	 * 
	 * @param albumId
	 *            id of album
	 * @param e
	 *            cause
	 */
	public AlbumIsNotBlurredAPIException(long albumId, Throwable e) {
		super("Album[" + albumId + "] is not blurred", e);
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
	public AlbumIsNotBlurredAPIException(long albumId, String userId, Throwable e) {
		super("Album[" + albumId + "] is not blurred for user[" + userId + "]", e);
	}

}
