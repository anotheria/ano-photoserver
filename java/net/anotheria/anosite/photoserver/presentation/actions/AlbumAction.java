package net.anotheria.anosite.photoserver.presentation.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Actions on album's.
 * 
 * @author Alexandr Bolbat
 */
public enum AlbumAction {

	/**
	 * Update album.
	 */
	UPDATE,

	/**
	 * Set photos order.
	 */
	SETORDER,

	/**
	 * Blur album.
	 */
	BLUR,

	/**
	 * Unblur album.
	 */
	UNBLUR,

	/**
	 * Remove album.
	 */
	REMOVE,

	/**
	 * List album information.
	 */
	LIST,

	/**
	 * List all albums information.
	 */
	LISTALL;

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AlbumAction.class);

	/**
	 * Default action.
	 */
	public static final AlbumAction DEFAULT = LIST;

	public static AlbumAction getAction(String name) {
		for (AlbumAction action : AlbumAction.values())
			if (action.name().equalsIgnoreCase(name))
				return action;

		LOG.warn("getAction(" + name + ") Wrong AlbumAction name. Returning default AlbumAction[" + DEFAULT + "].");
		return DEFAULT;
	}

}
