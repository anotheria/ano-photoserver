package net.anotheria.anosite.photoserver.service.blur.persistence;

import net.anotheria.anoprise.metafactory.Service;
import net.anotheria.anosite.photoserver.service.blur.BlurSettingBO;

/**
 * Album blur settings persistence service interface.
 *
 * @author h3ll
 */
public interface BlurSettingsPersistenceService extends Service {

	/**
	 * Returns {@link net.anotheria.anosite.photoserver.service.blur.BlurSettingBO} for selected album.
	 *
	 * @param albumId   id of album
	 * @param pictureId id of picture
	 * @param userId	id of user
	 * @return {@link net.anotheria.anosite.photoserver.service.blur.BlurSettingBO}
	 * @throws BlurSettingsPersistenceServiceException
	 *
	 */

	BlurSettingBO readBlurSetting(long albumId, long pictureId, String userId) throws BlurSettingsPersistenceServiceException;

	/**
	 * Blur selected album for all  users.
	 * If album is  blurred ( blurred for all users) then {@link AlbumIsBlurredPersistenceException} will be thrown. Otherwise
	 * album will be blurred, all settings for some users and  pictures will be removed!!!
	 *
	 * @param albumId id of album
	 * @throws BlurSettingsPersistenceServiceException
	 *
	 */
	void blurAlbum(long albumId) throws BlurSettingsPersistenceServiceException;

	/**
	 * Blur selected album for selected user.
	 * If album is already blurred for this  user, or blurred for all users -  then {@link AlbumIsBlurredPersistenceException} will be thrown. Otherwise
	 * album will be blurred for selected user, and all other  options like (spec permissions to  some Image from this album to current user) will be
	 * removed.
	 *
	 * @param albumId id of album
	 * @param userId  id of user
	 * @throws BlurSettingsPersistenceServiceException
	 *
	 */
	void blurAlbum(long albumId, String userId) throws BlurSettingsPersistenceServiceException;

	/**
	 * Un-blur selected album for all users.
	 * If album is not blurred then {@link AlbumIsNotBlurredPersistenceException}  will be thrown. Otherwise  album will be unBlurred for all user,
	 * all restrictions and permission will be removed!
	 *
	 * @param albumId id of album
	 * @throws BlurSettingsPersistenceServiceException
	 *
	 */
	void unBlurAlbum(long albumId) throws BlurSettingsPersistenceServiceException;

	/**
	 * Un-blur selected album for selected user.
	 * If album is not blurred for selected user, or album is not blurred for all users -  then {@link AlbumIsNotBlurredPersistenceException}  will be thrown. Otherwise
	 * album will be  unBlurred for this user,
	 * all additional  restrictions and permission will be removed!
	 *
	 * @param albumId id of album
	 * @param userId  id of user
	 * @throws BlurSettingsPersistenceServiceException
	 *
	 */
	void unBlurAlbum(long albumId, String userId) throws BlurSettingsPersistenceServiceException;


	/**
	 * Blur picture from selected album for some user.
	 * If picture is already blurred for this user (means that album can be blurred also) - then   {@link PictureIsBlurredPersistenceException} will be thrown.
	 * Otherwise  picture  will be  blurred.
	 *
	 * @param albumId   id of album  (picture belongs to this  album)
	 * @param pictureId id of picture
	 * @param userId	id of user
	 * @throws BlurSettingsPersistenceServiceException
	 *
	 */
	void blurPicture(long albumId, long pictureId, String userId) throws BlurSettingsPersistenceServiceException;

	/**
	 * Blur picture from selected album for all users.
	 * If this picture is already blurred for all  users, or album is blurred then {@link PictureIsBlurredPersistenceException} will be thrown,
	 * otherwise picture will be blurred
	 * for all users, all previous settings will be removed.
	 *
	 * @param albumId   id of album  (picture belongs to this  album)
	 * @param pictureId id of picture
	 * @throws BlurSettingsPersistenceServiceException
	 *
	 */
	void blurPicture(long albumId, long pictureId) throws BlurSettingsPersistenceServiceException;

	/**
	 * UnBlur picture from selected album for  specified user.
	 * If picture is not blurred for selected user or album is not blurred then {@link PictureIsNotBlurredPersistenceException}, otherwise picture will be unBlurred.
	 *
	 * @param albumId   id of album  (picture belongs to this  album)
	 * @param pictureId id of picture
	 * @param userId	id of user
	 * @throws BlurSettingsPersistenceServiceException
	 *
	 */
	void unBlurPicture(long albumId, long pictureId, String userId) throws BlurSettingsPersistenceServiceException;

	/**
	 * UnBlur picture for all users.
	 * If current picture is not blurred, or album is not blurred then {@link PictureIsNotBlurredPersistenceException}, otherwise picture will be unBlurred.
	 *
	 * @param albumId   id of album  (picture belongs to this  album)
	 * @param pictureId id of picture
	 * @throws BlurSettingsPersistenceServiceException
	 *
	 */
	void unBlurPicture(long albumId, long pictureId) throws BlurSettingsPersistenceServiceException;

	/**
	 * Remove all BlurSettings for selected album.
	 *
	 * @param albumId id of album
	 * @throws BlurSettingsPersistenceServiceException
	 *
	 */
	void removeBlurSettings(long albumId) throws BlurSettingsPersistenceServiceException;

}
