package net.anotheria.anosite.photoserver.api.access;

import net.anotheria.anoplass.api.API;
import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;

import java.util.Map;

/**
 * {@link net.anotheria.anoplass.api.API} for checking access.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public interface AccessAPI extends API {

	/**
	 * Is allowed for me (currently logged user) do some action on photo.
	 *
	 * @param action
	 * 		- action
	 * @return <code>true</code> if can or <code>false</code>
	 * @param photoId a long.
	 */
	boolean isAllowedForMe(PhotoAction action, long photoId);

	/**
	 * Is allowed for me (currently logged user) do some action on photo album.
	 *
	 * @return <code>true</code> if can or <code>false</code>
	 * @param action a {@link net.anotheria.anosite.photoserver.api.access.AlbumAction} object.
	 * @param albumId a long.
	 */
	boolean isAllowedForMe(AlbumAction action, long albumId);

	/**
	 * Is views allowed for photo with given id.
	 *
	 * @param photoId
	 * 		id of the photo
	 * @param parameters
	 * 		optional params
	 * @return {@link net.anotheria.anosite.photoserver.api.access.ViewAccessResponse}
	 */
	ViewAccessResponse isViewAllowed(long photoId, Map<AccessParameter, String> parameters);

	/**
	 * Is views allowed for given photo.
	 *
	 * @param photo
	 * 		{@link net.anotheria.anosite.photoserver.shared.vo.PhotoVO}
	 * @param parameters
	 * 		optional params
	 * @return {@link net.anotheria.anosite.photoserver.api.access.ViewAccessResponse}
	 */
	ViewAccessResponse isViewAllowed(PhotoVO photo, Map<AccessParameter, String> parameters);

	/**
	 * Register custom access provider.
	 *
	 * @param accessProvider
	 * 		- access provider instance
	 */
	void registerAccessProvider(AccessProvider accessProvider);
}
