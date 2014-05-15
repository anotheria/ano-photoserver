package net.anotheria.anosite.photoserver.shared.vo;

import java.io.Serializable;

/**
 * Blur Setting value object.
 * Object also maintains with Default data.
 * When  pictureId is equal to ALL_ALBUM_PICTURES_DEFAULT_CONSTANT constant, we can  simply say that
 * this  record  belongs  to all album pictures.
 * Same situation with ALL_USERS_DEFAULT_CONSTANT.
 *
 * @author h3ll
 */
public class BlurSettingVO implements Serializable {
	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = -5251151993225003601L;

	/**
	 * Default constant for ll pictures in album.
	 */
	public static long ALL_ALBUM_PICTURES_DEFAULT_CONSTANT = -10l;
	/**
	 * Default constant for All user's id.
	 */
	public static String ALL_USERS_DEFAULT_CONSTANT = "-10";

	/**
	 * BlurSettingVO 'albumId'.
	 * Id of album to which picture belongs.
	 */
	private long albumId;
	/**
	 * BlurSettingVO 'pictureId'.
	 * Id of picture.
	 */
	private long pictureId;
	/**
	 * BlurSettingVO 'userId'.
	 * Id of  user - who trying to view  picture.
	 */
	private String userId;
	/**
	 * BlurSettingVO 'isBlurred'.
	 * True if blurred, false otherwise.
	 */
	private boolean isBlurred;


	/**
	 * Constructor.
	 *
	 * @param aAlbumId   id of album
	 * @param aPictureId id of picture
	 * @param aUserId    id of user
	 * @param aBlur      blur
	 */
	public BlurSettingVO(long aAlbumId, long aPictureId, String aUserId, boolean aBlur) {
		this.albumId = aAlbumId;
		this.pictureId = aPictureId;
		this.userId = aUserId;
		this.isBlurred = aBlur;
	}

	/**
	 * Constructor.
	 */
	public BlurSettingVO() {
	}


	public long getAlbumId() {
		return albumId;
	}

	public void setAlbumId(long aAlbumId) {
		this.albumId = aAlbumId;
	}

	public long getPictureId() {
		return pictureId;
	}

	public void setPictureId(long aPictureId) {
		this.pictureId = aPictureId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String aUserId) {
		this.userId = aUserId;
	}

	public boolean isBlurred() {
		return isBlurred;
	}

	public void setBlurred(boolean aIsBlurred) {
		this.isBlurred = aIsBlurred;
	}


	@Override
	public String toString() {
		return "BlurSettingVO[" +
				"albumId=" + albumId +
				", pictureId=" + pictureId +
				", userId=" + userId +
				", isBlurred=" + isBlurred +
				']';
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BlurSettingVO that = (BlurSettingVO) o;

		if (albumId != that.albumId) return false;
		if (pictureId != that.pictureId) return false;
		if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = (int) (albumId ^ (albumId >>> 32));
		result = 31 * result + (int) (pictureId ^ (pictureId >>> 32));
		result = 31 * result + (userId != null ? userId.hashCode() : 0);
		return result;
	}
}
