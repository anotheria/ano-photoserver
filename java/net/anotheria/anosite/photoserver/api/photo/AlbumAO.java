package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.anosite.photoserver.shared.IdCrypter;
import net.anotheria.anosite.photoserver.shared.vo.AlbumVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
	}

	public List<PhotoAO> getPhotosOrdered() {
		Map<Long, PhotoAO> photosMap = new LinkedHashMap<>();
		List<PhotoAO> result = new ArrayList<>();
		for (PhotoAO photo : photos)
			photosMap.put(photo.getId(), photo);

		for (Long id : getPhotosOrder()) {
			PhotoAO photo = photosMap.remove(id);
			if (photo != null) {
				result.add(photo);
			}
		}
		result.addAll(photosMap.values());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "AlbumAO [getEncodedId()=" + getEncodedId() + ", toString()=" + super.toString() + "]";
	}

}
