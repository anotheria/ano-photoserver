package net.anotheria.anosite.photoserver.api.access;

import net.anotheria.anoplass.api.API;
import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;

import java.util.Map;

/**
 * {@link API} for checking access.
 *
 * @author Alexandr Bolbat
 */
public interface AccessAPI extends API {

	/**
	 * Is allowed for me (currently logged user) do some action on photo.
	 *
	 * @param action
	 * 		- action
	 * @param photo
	 * 		- photo
	 * @return <code>true</code> if can or <code>false</code>
	 */
	boolean isAllowedForMe(PhotoAction action, long photoId);

	/**
	 * Is allowed for me (currently logged user) do some action on photo album.
	 *
	 * @param userId
	 * 		- user id
	 * @param album
	 * 		- photo album
	 * @return <code>true</code> if can or <code>false</code>
	 */
	boolean isAllowedForMe(AlbumAction action, long albumId);

	/**
	 * Is views allowed for photo with given id.
	 *
	 * @param photoId
	 * 		id of the photo
	 * @param parameters
	 * 		optional params
	 * @return {@link ViewAccessResponse}
	 */
	ViewAccessResponse isViewAllowed(long photoId, Map<AccessParameter, String> parameters);

	/**
	 * Is views allowed for given photo.
	 *
	 * @param photo
	 * 		{@link PhotoVO}
	 * @param parameters
	 * 		optional params
	 * @return {@link ViewAccessResponse}
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
