package net.anotheria.anosite.photoserver.service.blur;

import net.anotheria.anosite.photoserver.shared.vo.BlurSettingVO;

/**
 * BlurSetting business object.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class BlurSettingBO extends BlurSettingVO implements Cloneable {
	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = 4175470140105195245L;
	/**
	 * Default constant for ll pictures in album.
	 */
	public static final long ALL_ALBUM_PICTURES_DEFAULT_CONSTANT = BlurSettingVO.ALL_ALBUM_PICTURES_DEFAULT_CONSTANT;
	/**
	 * Default constant for All user's id.
	 */
	public static final String ALL_USERS_DEFAULT_CONSTANT = BlurSettingVO.ALL_USERS_DEFAULT_CONSTANT;

	/**
	 * Constructor.
	 *
	 * @param pictureBlur {@link net.anotheria.anosite.photoserver.shared.vo.BlurSettingVO}
	 */
	public BlurSettingBO(BlurSettingVO pictureBlur) {
		setAlbumId(pictureBlur.getAlbumId());
		setPictureId(pictureBlur.getPictureId());
		setUserId(pictureBlur.getUserId());
		setBlurred(pictureBlur.isBlurred());
	}

	/**
	 * Constructor.
	 *
	 * @param aAlbumId   id of album
	 * @param aPictureId id of picture
	 * @param aUserId	id of user
	 * @param aBlur	  blur
	 */
	public BlurSettingBO(long aAlbumId, long aPictureId, String aUserId, boolean aBlur) {
		super(aAlbumId, aPictureId, aUserId, aBlur);
	}


	/**
	 * Constructor.
	 */
	public BlurSettingBO() {
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "BlurSettingBO[" +
				"albumId=" + getAlbumId() +
				", pictureId=" + getPictureId() +
				", userId=" + getUserId() +
				", isBlurred=" + isBlurred() +
				']';
	}

	/** {@inheritDoc} */
	@Override
	protected BlurSettingBO clone() {
		try {
			return BlurSettingBO.class.cast(super.clone());
		} catch (CloneNotSupportedException e) {
			throw new AssertionError("Not cloneable???");
		}
	}
}
