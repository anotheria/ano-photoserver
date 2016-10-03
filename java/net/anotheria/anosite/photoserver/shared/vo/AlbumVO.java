package net.anotheria.anosite.photoserver.shared.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Album meta information.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
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

	/**
	 * <p>Getter for the field <code>id</code>.</p>
	 *
	 * @return a long.
	 */
	public long getId() {
		return id;
	}

	/**
	 * <p>Setter for the field <code>id</code>.</p>
	 *
	 * @param aId a long.
	 */
	public void setId(long aId) {
		this.id = aId;
	}

	/**
	 * <p>Getter for the field <code>userId</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * <p>Setter for the field <code>userId</code>.</p>
	 *
	 * @param aUserId a {@link java.lang.String} object.
	 */
	public void setUserId(String aUserId) {
		this.userId = aUserId;
	}

	/**
	 * <p>isDefault.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isDefault() {
		return isDefault;
	}

	/**
	 * <p>setDefault.</p>
	 *
	 * @param aIsDefault a boolean.
	 */
	public void setDefault(boolean aIsDefault) {
		this.isDefault = aIsDefault;
	}

	/**
	 * <p>Getter for the field <code>name</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return name != null ? name : "";
	}

	/**
	 * <p>Setter for the field <code>name</code>.</p>
	 *
	 * @param aName a {@link java.lang.String} object.
	 */
	public void setName(String aName) {
		this.name = aName;
	}

	/**
	 * <p>Setter for the field <code>description</code>.</p>
	 *
	 * @param aDescription a {@link java.lang.String} object.
	 */
	public void setDescription(String aDescription) {
		this.description = aDescription;
	}

	/**
	 * <p>Getter for the field <code>description</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getDescription() {
		return description != null ? description : "";
	}

	/**
	 * <p>Setter for the field <code>photosOrder</code>.</p>
	 *
	 * @param aPhotosOrder a {@link java.util.List} object.
	 */
	public void setPhotosOrder(List<Long> aPhotosOrder) {
		if (aPhotosOrder == null)
			throw new IllegalArgumentException("Null photos argument.");

		this.photosOrder = aPhotosOrder;
	}

	/**
	 * <p>Getter for the field <code>photosOrder</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<Long> getPhotosOrder() {
		return new ArrayList<Long>(photosOrder);
	}

	/**
	 * <p>getPhotosCount.</p>
	 *
	 * @return a int.
	 */
	public int getPhotosCount() {
		return photosOrder.size();
	}

	/**
	 * <p>addPhotoToPhotoOrder.</p>
	 *
	 * @param photoId a long.
	 */
	public void addPhotoToPhotoOrder(long photoId) {
		photosOrder.add(photoId);
	}

	/**
	 * <p>removePhotofromPhotoOrder.</p>
	 *
	 * @param photoId a long.
	 */
	public void removePhotofromPhotoOrder(long photoId) {
		List<Long> newOrder = new ArrayList<Long>();
		for (Long photo : photosOrder)
			if (photo != photoId)
				newOrder.add(photo);

		this.photosOrder = newOrder;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "AlbumVO [id=" + id + ", userId=" + userId + ", isDefault=" + isDefault + ", name=" + name + ", description=" + description + ", photosOrder="
				+ photosOrder + "]";
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
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
