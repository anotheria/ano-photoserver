package net.anotheria.anosite.photoserver.service.blur;

import net.anotheria.anosite.photoserver.service.blur.persistence.PictureIsBlurredPersistenceException;

/**
 * PictureIsBlurred exception.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class PictureIsBlurredException extends BlurSettingsServiceException {

	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = -8276443556692069404L;

	/**
	 * Constructor.
	 *
	 * @param albumId   id of album
	 * @param pictureId id of picture
	 * @param userId	id of user
	 * @param e		 cause exception
	 */
	public PictureIsBlurredException(long albumId, long pictureId, String userId, PictureIsBlurredPersistenceException e) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is already blurred for user[" + userId + "].", e);
	}

	/**
	 * Constructor.
	 *
	 * @param albumId   id of album
	 * @param pictureId id of picture
	 * @param userId	id of user
	 */
	public PictureIsBlurredException(long albumId, long pictureId, String userId) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is already blurred for user[" + userId + "].");
	}

	/**
	 * Constructor.
	 *
	 * @param albumId   id of album
	 * @param pictureId id of picture
	 * @param e		 cause exception
	 */
	public PictureIsBlurredException(long albumId, long pictureId, PictureIsBlurredPersistenceException e) {
		super("Image/Picture [" + pictureId + "] from album[" + albumId + "] is already blurred for all users.", e);
	}


}
