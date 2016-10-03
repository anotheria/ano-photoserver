package net.anotheria.anosite.photoserver.api.access;

import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;

import java.util.Map;

/**
 * Access provider interface.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public interface AccessProvider {

	/**
	 * Checks whether view of the photo is allowed.
	 *
	 * @param photoId id of the photo
	 * @param parameters optional params
	 * @return {@link net.anotheria.anosite.photoserver.api.access.ViewAccessResponse}
	 */
	ViewAccessResponse isViewAllowed(long photoId,  Map<AccessParameter, String> parameters);

	/**
	 * Checks whether view of the photo is allowed.
	 *
	 * @param photo {@link net.anotheria.anosite.photoserver.shared.vo.PhotoVO}
	 * @param parameters optional params
	 * @return {@link net.anotheria.anosite.photoserver.api.access.ViewAccessResponse}
	 */
	ViewAccessResponse isViewAllowed(PhotoVO photo, Map<AccessParameter, String> parameters);

}
