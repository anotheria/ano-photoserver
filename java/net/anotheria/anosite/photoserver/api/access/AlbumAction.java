package net.anotheria.anosite.photoserver.api.access;

/**
 * Action's on photo album for managing access.
 * 
 * @author Alexandr Bolbat
 */
public enum AlbumAction {

	/**
	 * View album.
	 */
	VIEW,

	/**
	 * Create album.
	 */
	CREATE,

	/**
	 * Edit album.
	 */
	EDIT,

	/**
	 * Remove album.
	 */
	REMOVE,

	/**
	 * Add photo to album.
	 */
	ADD_PHOTO,

	/**
	 * Remove photo from album.
	 */
	REMOVE_PHOTO,

	/**
	 * Comment photo album.
	 */
	COMMENT;

}
