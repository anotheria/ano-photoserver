package net.anotheria.anosite.photoserver.service.blur;

import net.anotheria.anoprise.metafactory.Service;
import org.distributeme.annotation.DistributeMe;

import java.util.List;
import java.util.Map;

/**
 * Blur setting service, allow enable/disable "blurring-feature" for pictures/albums, for selected/all users.
 *
 * @author h3ll
 */
@DistributeMe(initcode="net.anotheria.anosite.photoserver.shared.PhotoServerConfigurator.configure();")
public interface BlurSettingsService extends Service {


	/**
	 * Read picture blur settings, for specified user.
	 *
	 * @param albumId	id of album
	 * @param pictureIds {@link java.util.List<Long>}  picture id's collection
	 * @param userId	 id of user
	 * @return Map<Long, BlurSettingBO>  -- which contains  mapping  pictureId to {@link BlurSettingBO}
	 * @throws BlurSettingsServiceException
	 */
	Map<Long, BlurSettingBO> readBlurSettings(long albumId, List<Long> pictureIds, String userId) throws BlurSettingsServiceException;

	/**
	 * Read picture blur settings, for all/default user.
	 *
	 * @param albumId	id of album
	 * @param pictureIds {@link java.util.List<Long>}  picture id's collection
	 * @return Map<Long, BlurSettingBO>  -- which contains  mapping  pictureId to {@link BlurSettingBO}
	 * @throws BlurSettingsServiceException
	 */
	Map<Long, BlurSettingBO> readBlurSettings(long albumId, List<Long> pictureIds) throws BlurSettingsServiceException;

	/**
	 * Blur selected album for all  users.
	 * If album is  blurred ( blurred for all users) then {@link AlbumIsBlurredException} will be thrown. Otherwise
	 * album will be blurred, all settings for some users and  pictures will be removed!!!
	 *
	 * @param albumId id of album
	 * @throws BlurSettingsServiceException
	 */
	void blurAlbum(long albumId) throws BlurSettingsServiceException;

	/**
	 * Blur selected album for selected user.
	 * If album is already blurred for this  user, or blurred for all users -  then {@link AlbumIsBlurredException} will be thrown. Otherwise
	 * album will be blurred for selected user, and all other  options like (spec permissions to  some Image from this album to current user) will be
	 * removed.
	 *
	 * @param albumId id of album
	 * @param userId  id of user
	 * @throws BlurSettingsServiceException
	 */
	void blurAlbum(long albumId, String userId) throws BlurSettingsServiceException;

	/**
	 * Blur picture from selected album for some user.
	 * If picture is already blurred for this user (means that album can be blurred also) - then   {@link PictureIsBlurredException} will be thrown.
	 * Otherwise  picture  will be  blurred.
	 *
	 * @param albumId   id of album  (picture belongs to this  album)
	 * @param pictureId id of picture
	 * @param userId	id of user
	 * @throws BlurSettingsServiceException
	 */
	void blurPicture(long albumId, long pictureId, String userId) throws BlurSettingsServiceException;

	/**
	 * Blur picture from selected album for all users.
	 * If this picture is already blurred for all  users, or album is blurred then {@link PictureIsBlurredException} will be thrown,
	 * otherwise picture will be blurred
	 * for all users, all previous settings will be removed.
	 *
	 * @param albumId   id of album  (picture belongs to this  album)
	 * @param pictureId id of picture
	 * @throws BlurSettingsServiceException
	 */
	void blurPicture(long albumId, long pictureId) throws BlurSettingsServiceException;


	/**
	 * Un-blur selected album for all users.
	 * If album is not blurred then {@link AlbumIsNotBlurredException}  will be thrown. Otherwise  album will be unBlurred for all user,
	 * all restrictions and permission will be removed!
	 *
	 * @param albumId id of album
	 * @throws BlurSettingsServiceException
	 */
	void unBlurAlbum(long albumId) throws BlurSettingsServiceException;

	/**
	 * Un-blur selected album for selected user.
	 * If album is not blurred for selected user, or album is not blurred for all users -  then {@link AlbumIsNotBlurredException}  will be thrown. Otherwise
	 * album will be  unBlurred for this user,
	 * all additional  restrictions and permission will be removed!
	 *
	 * @param albumId id of album
	 * @param userId  id of user
	 * @throws BlurSettingsServiceException
	 */
	void unBlurAlbum(long albumId, String userId) throws BlurSettingsServiceException;

	/**
	 * UnBlur picture from selected album for  specified user.
	 * If picture is not blurred for selected user or album is not blurred then {@link PictureIsNotBlurredException}, otherwise picture will be unBlurred.
	 *
	 * @param albumId   id of album  (picture belongs to this  album)
	 * @param pictureId id of picture
	 * @param userId	id of user
	 * @throws BlurSettingsServiceException
	 */
	void unBlurPicture(long albumId, long pictureId, String userId) throws BlurSettingsServiceException;

	/**
	 * UnBlur picture for all users.
	 * If current picture is not blurred, or album is not blurred then {@link PictureIsNotBlurredException}, otherwise picture will be unBlurred.
	 *
	 * @param albumId   id of album  (picture belongs to this  album)
	 * @param pictureId id of picture
	 * @throws BlurSettingsServiceException
	 */
	void unBlurPicture(long albumId, long pictureId) throws BlurSettingsServiceException;


	/**
	 * Remove blurSettings for selected album.
	 *
	 * @param albumId id of album
	 * @throws BlurSettingsServiceException
	 */
	void removeBlurSettings(long albumId) throws BlurSettingsServiceException;

}
