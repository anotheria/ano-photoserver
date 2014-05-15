package net.anotheria.anosite.photoserver.presentation.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Actions on photos.
 * 
 * @author Alexandr Bolbat
 */
public enum PhotoAction {

	/**
	 * Update photo.
	 */
	UPDATE,

	/**
	 * Remove photo.
	 */
	REMOVE,

	/**
	 * List photo information.
	 */
	LIST,

	/**
	 * List album photos information.
	 */
	LISTALL;

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(PhotoAction.class);

	/**
	 * Default action.
	 */
	public static final PhotoAction DEFAULT = LIST;

	public static PhotoAction getAction(String name) {
		for (PhotoAction action : PhotoAction.values())
			if (action.name().equalsIgnoreCase(name))
				return action;

		LOG.warn("getAction(" + name + ") Wrong PhotoAction name. Returning default PhotoAction[" + DEFAULT + "].");
		return DEFAULT;
	}

}
