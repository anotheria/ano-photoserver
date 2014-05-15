package net.anotheria.anosite.photoserver.api.blur;

import net.anotheria.anosite.photoserver.shared.vo.BlurSettingVO;

/**
 * BlurSettingAO.
 *
 * @author h3ll
 */
public class BlurSettingAO extends BlurSettingVO {
	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = 1516728298028572110L;

	/**
	 * Constructor.
	 *
	 * @param pictureBlur {@link BlurSettingVO}
	 */
	public BlurSettingAO(BlurSettingVO pictureBlur) {
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
	public BlurSettingAO(long aAlbumId, long aPictureId, String aUserId, boolean aBlur) {
		super(aAlbumId, aPictureId, aUserId, aBlur);
	}

	@Override
	public String toString() {
		return "BlurSettingVO[" +
				"albumId=" + getAlbumId() +
				", pictureId=" + getPictureId() +
				", userId=" + getUserId() +
				", isBlurred=" + isBlurred() +
				']';
	}
}
