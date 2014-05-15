package net.anotheria.anosite.photoserver.api.access;

/**
 * View Access Response type.
 *
 * @author Alex Osadchy
 */
public enum ViewAccessResponse {

	/**
	 * View of the photo is fully allowed.
	 */
	VIEW_ALLOWED,

	/**
	 * View of the blurred photo is allowed.
	 */
	BLURRED_VIEW_ALLOWED,

	/**
	 * View is denied.
	 */
	VIEW_DENIED

}
