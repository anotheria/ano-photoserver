package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.anosite.photoserver.shared.IdCrypter;
import net.anotheria.anosite.photoserver.shared.vo.AlbumVO;

/**
 * User photo album information.
 * 
 * @author Alexandr Bolbat
 */
public class AlbumAO extends AlbumVO {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -8392874328183792765L;

	/**
	 * Public constructor. Creates new AlbumAO.
	 */
	public AlbumAO() {
		super();
	}

	/**
	 * Public constructor. Creates new AlbumAO and fills it with information from AlbumVO. 
	 */
	public AlbumAO(AlbumVO albumVO) {
		super();
		setId(albumVO.getId());
		setUserId(albumVO.getUserId());
		setDefault(albumVO.isDefault());
		setName(albumVO.getName());
		setDescription(albumVO.getDescription());
		setPhotosOrder(albumVO.getPhotosOrder());
	}

	/**
	 * Method encodes ID for use in the frontend.
	 * 
	 * @return {@link String} encoded id
	 */
	public String getEncodedId() {
		return IdCrypter.encode(getId());
	}

	@Override
	public String toString() {
		return "AlbumAO [getEncodedId()=" + getEncodedId() + ", toString()=" + super.toString() + "]";
	}

}
