package net.anotheria.anosite.photoserver.api.photo;

/**
 * PhotoAPI exception which is thrown if requested photo not found.. 
 * @author dzhmud
 */
public class PhotoNotFoundPhotoAPIException extends PhotoAPIException {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 1722565648067860500L;
	
	/**
	 * Public constructor.
	 * @param photoId - id of the photo that wasn't found
	 */
	public PhotoNotFoundPhotoAPIException(long photoId) {
		super("Photo[" + photoId + "] not found.");
	}

}
