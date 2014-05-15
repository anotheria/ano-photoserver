package net.anotheria.anosite.photoserver.service.storage;

import net.anotheria.anosite.photoserver.shared.vo.AlbumVO;

/**
 * User photo album information.
 *
 * @author Alexandr Bolbat
 */
public class AlbumBO extends AlbumVO {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -5480275735050747635L;

	/**
	 * Constructor.
	 */
	public AlbumBO() {
		super();
	}

	/**
	 * Constructor.
	 *
	 * @param album {@link AlbumVO}
	 */
	public AlbumBO(AlbumVO album) {
		super();
		setId(album.getId());
		setUserId(album.getUserId());
		setDefault(album.isDefault());
		setName(album.getName());
		setDescription(album.getDescription());
		setPhotosOrder(album.getPhotosOrder());
	}

	@Override
	public String toString() {
		return "AlbumBO [getId()=" + getId() + ", getUserId()=" + getUserId() + ", getName()=" + getName() + ", getDescription()=" + getDescription()
				+ ", getPhotosOrder()=" + getPhotosOrder() + "]";
	}

	@Override
	public AlbumBO clone() {
		AlbumBO cloned = (AlbumBO) super.clone();
		cloned.setPhotosOrder(getPhotosOrder());
		return cloned;

	}

}
