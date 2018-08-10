package net.anotheria.anosite.photoserver.api.blur;

import net.anotheria.anoplass.api.API;

import java.util.List;
import java.util.Map;

/**
 * BlurSettingsAPI interface.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public interface BlurSettingsAPI extends API {

	/**
	 * Read picture blur settings for currently Logged in user.
	 * If we trying to read users own picture-settings  we will always  receive  UN-Blurred  results!
	 *
	 * @param albumId   id of album
	 * @param pictureId id of picture
	 * @return true if blurred, false otherwise
	 * @throws net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException if any.
	 */
	Boolean readMyBlurSettings(long albumId, long pictureId) throws BlurSettingsAPIException;

	/**
	 * Read picture blur settings for currently Logged in user.
	 * If we trying to read users own picture-settings  we will always  receive  UN-Blurred  results!
	 *
	 * @param albumId   id of album
	 * @param pictureId picture id's collection
	 * @return {@link java.util.Map}  pictureId - {@link java.lang.Boolean}  mapping map with.
	 * @throws net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException if any.
	 */
	Map<Long, Boolean> readMyBlurSettings(long albumId, List<Long> pictureId) throws BlurSettingsAPIException;

	/**
	 * Blur selected album for all  users.
	 * If album is  blurred ( blurred for all users) then {@link net.anotheria.anosite.photoserver.api.blur.AlbumIsBlurredAPIException} will be thrown. Otherwise
	 * album will be blurred, all settings for some users and  pictures will be removed!!!
	 *
	 * @param albumId id of album
	 * @throws net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException if any.
	 */
	void blurAlbum(long albumId) throws BlurSettingsAPIException;

	/**
	 * Blur selected album for selected user.
	 * If album is already blurred for this  user, or blurred for all users -  then {@link net.anotheria.anosite.photoserver.api.blur.AlbumIsBlurredAPIException} will be thrown. Otherwise
	 * album will be blurred for selected user, and all other  options like (spec permissions to  some Image from this album to current user) will be
	 * removed.
	 *
	 * @param albumId id of album
	 * @param userId  id of user
	 * @throws net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException if user will try to blur his album, for himself!
	 */
	void blurAlbum(long albumId, String userId) throws BlurSettingsAPIException;

	/**
	 * Blur picture from selected album for some user.
	 * If picture is already blurred for this user (means that album can be blurred also) - then   {@link net.anotheria.anosite.photoserver.api.blur.PictureIsBlurredAPIException} will be thrown.
	 * Otherwise  picture  will be  blurred.
	 *
	 * @param albumId   id of album  (picture belongs to this  album)
	 * @param pictureId id of picture
	 * @param userId	id of user
	 * @throws net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException if user will try to blur his picture for himself
	 */
	void blurPicture(long albumId, long pictureId, String userId) throws BlurSettingsAPIException;

	/**
	 * Blur picture from selected album for all users.
	 * If this picture is already blurred for all  users, or album is blurred then {@link net.anotheria.anosite.photoserver.api.blur.PictureIsBlurredAPIException} will be thrown,
	 * otherwise picture will be blurred
	 * for all users, all previous settings will be removed.
	 *
	 * @param albumId   id of album  (picture belongs to this  album)
	 * @param pictureId id of picture
	 * @throws net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException if any.
	 */
	void blurPicture(long albumId, long pictureId) throws BlurSettingsAPIException;

	/**
	 * Blur picture from selected album for all users without login in user.
	 * If this picture is already blurred for all  users, or album is blurred then {@link net.anotheria.anosite.photoserver.api.blur.PictureIsBlurredAPIException} will be thrown,
	 * otherwise picture will be blurred
	 * for all users, all previous settings will be removed.
	 *
	 * @param albumId   id of album  (picture belongs to this  album)
	 * @param pictureId id of picture
	 * @throws net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException if any.
	 */
	void blurUserPicture(long albumId, long pictureId) throws BlurSettingsAPIException;


	/**
	 * Un-blur selected album for all users.
	 * If album is not blurred then {@link net.anotheria.anosite.photoserver.api.blur.AlbumIsNotBlurredAPIException}  will be thrown. Otherwise  album will be unBlurred for all user,
	 * all restrictions and permission will be removed!
	 *
	 * @param albumId id of album
	 * @throws net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException if any.
	 */
	void unBlurAlbum(long albumId) throws BlurSettingsAPIException;

	/**
	 * Un-blur selected album for selected user.
	 * If album is not blurred for selected user, or album is not blurred for all users -  then {@link net.anotheria.anosite.photoserver.api.blur.AlbumIsNotBlurredAPIException}  will be thrown. Otherwise
	 * album will be  unBlurred for this user,
	 * all additional  restrictions and permission will be removed!
	 *
	 * @param albumId id of album
	 * @param userId  id of user
	 * @throws net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException if user will try to unBlur his own album for himself
	 */
	void unBlurAlbum(long albumId, String userId) throws BlurSettingsAPIException;

	/**
	 * UnBlur picture from selected album for  specified user.
	 * If picture is not blurred for selected user or album is not blurred then {@link net.anotheria.anosite.photoserver.api.blur.PictureIsNotBlurredAPIException}, otherwise picture will be unBlurred.
	 *
	 * @param albumId   id of album  (picture belongs to this  album)
	 * @param pictureId id of picture
	 * @param userId	id of user
	 * @throws net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException if user will try to unBlure his own picture for himself
	 */
	void unBlurPicture(long albumId, long pictureId, String userId) throws BlurSettingsAPIException;

	/**
	 * UnBlur picture for all users.
	 * If current picture is not blurred, or album is not blurred then {@link net.anotheria.anosite.photoserver.api.blur.PictureIsNotBlurredAPIException}, otherwise picture will be unBlurred.
	 *
	 * @param albumId   id of album  (picture belongs to this  album)
	 * @param pictureId id of picture
	 * @throws net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException if any.
	 */
	void unBlurPicture(long albumId, long pictureId) throws BlurSettingsAPIException;


	/**
	 * Removes blur settings for selected album.
	 *
	 * @param albumId id of album
	 * @throws net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException on backend errors
	 */
	void removeBlurSettings(long albumId) throws BlurSettingsAPIException;
}
