package net.anotheria.anosite.photoserver.shared.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Album meta information.
 * 
 * @author Alexandr Bolbat
 */
public class AlbumVO implements Serializable, Cloneable {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -5297438096611917019L;

	/**
	 * Album id. Unique.
	 */
	private long id;

	/**
	 * User id.
	 */
	private String userId;

	/**
	 * Id default album.
	 */
	private boolean isDefault;

	/**
	 * Album name.
	 */
	private String name;

	/**
	 * Album description.
	 */
	private String description;

	/**
	 * Album photos order. This list can contain not all album pictures. Don't use this list as base for loading album photos. Only for implementing sorting for
	 * presentation.
	 */
	private List<Long> photosOrder = new ArrayList<Long>();

	public long getId() {
		return id;
	}

	public void setId(long aId) {
		this.id = aId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String aUserId) {
		this.userId = aUserId;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setDefault(boolean aIsDefault) {
		this.isDefault = aIsDefault;
	}

	public String getName() {
		return name != null ? name : "";
	}

	public void setName(String aName) {
		this.name = aName;
	}

	public void setDescription(String aDescription) {
		this.description = aDescription;
	}

	public String getDescription() {
		return description != null ? description : "";
	}

	public void setPhotosOrder(List<Long> aPhotosOrder) {
		if (aPhotosOrder == null)
			throw new IllegalArgumentException("Null photos argument.");

		this.photosOrder = aPhotosOrder;
	}

	public List<Long> getPhotosOrder() {
		return new ArrayList<Long>(photosOrder);
	}

	public int getPhotosCount() {
		return photosOrder.size();
	}

	public void addPhotoToPhotoOrder(long photoId) {
		photosOrder.add(photoId);
	}

	public void removePhotofromPhotoOrder(long photoId) {
		List<Long> newOrder = new ArrayList<Long>();
		for (Long photo : photosOrder)
			if (photo != photoId)
				newOrder.add(photo);

		this.photosOrder = newOrder;
	}

	@Override
	public String toString() {
		return "AlbumVO [id=" + id + ", userId=" + userId + ", isDefault=" + isDefault + ", name=" + name + ", description=" + description + ", photosOrder="
				+ photosOrder + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlbumVO other = (AlbumVO) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public Object clone() {
		try {
			AlbumVO cloned = (AlbumVO) super.clone();
			cloned.setPhotosOrder(getPhotosOrder());
			return cloned;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("AlbumVO should be cloneable!");
		}
	}

}
