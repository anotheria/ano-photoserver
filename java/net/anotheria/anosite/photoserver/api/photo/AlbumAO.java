package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.anosite.photoserver.shared.IdCrypter;
import net.anotheria.anosite.photoserver.shared.vo.AlbumVO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User photo album information.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class AlbumAO extends AlbumVO {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -8392874328183792765L;

	private List<PhotoAO> photos = new ArrayList<>();
	/**
	 * Public constructor. Creates new AlbumAO.
	 */
	public AlbumAO() {
		super();
	}

	/**
	 * Public constructor. Creates new AlbumAO and fills it with information from AlbumVO.
	 *
	 * @param albumVO a {@link net.anotheria.anosite.photoserver.shared.vo.AlbumVO} object.
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
	public AlbumAO(AlbumVO albumVO, List<PhotoAO> photos) {
		super();
		setId(albumVO.getId());
		setUserId(albumVO.getUserId());
		setDefault(albumVO.isDefault());
		setName(albumVO.getName());
		setDescription(albumVO.getDescription());
		setPhotosOrder(albumVO.getPhotosOrder());
		this.photos = photos;
	}

	/**
	 * Method encodes ID for use in the frontend.
	 *
	 * @return {@link java.lang.String} encoded id
	 */
	public String getEncodedId() {
		return IdCrypter.encode(getId());
	}

	public List<PhotoAO> getPhotos() {
		return photos;
	}

	public void setPhotos(List<PhotoAO> photos) {
		this.photos = photos;
		setPhotosOrder(photos.stream().map(PhotoAO::getId).collect(Collectors.toList()));
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "AlbumAO [getEncodedId()=" + getEncodedId() + ", toString()=" + super.toString() + "]";
	}

}
