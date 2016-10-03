package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.anosite.photoserver.shared.IdCrypter;
import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;
import net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO;

/**
 * User photo information.
 * 
 * @author Alexandr Bolbat
 */
public class PhotoAO extends PhotoVO {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -5037535463982395045L;

	/**
	 * Is photo blurred for user who requested this photo.
	 */
	private boolean isBlurred = false;
	
	/**
	 * Public constructor. Creates new PhotoAO.
	 */
	public PhotoAO() {
		super();
	}

	/**
	 * Public constructor. Creates new PhotoAO and fills it with information from PhotoVO. 
	 */
	public PhotoAO(PhotoVO photo) {
		super();
		setId(photo.getId());
		setUserId(photo.getUserId());
		setAlbumId(photo.getAlbumId());
		setFileLocation(photo.getFileLocation());
		setExtension(photo.getExtension());
		setName(photo.getName());
		setDescription(photo.getDescription());
		setModificationTime(photo.getModificationTime());
		setPreviewSettings(new PreviewSettingsVO(photo.getPreviewSettings()));
		setApprovalStatus(photo.getApprovalStatus());
		setRestricted(photo.isRestricted());
	}

	/**
	 * Method encodes ID for use in the frontend.
	 * 
	 * @return {@link String} encoded id
	 */
	public String getEncodedId() {
		return IdCrypter.encode(getId());
	}

	public boolean isBlurred() {
		return isBlurred;
	}

	public void setBlurred(boolean aIsBlurred) {
		this.isBlurred = aIsBlurred;
	}

	@Override
	public String toString() {
		return "PhotoAO [getEncodedId()=" + getEncodedId() + ", isBlurred()=" + isBlurred() + ", toString()=" + super.toString() + "]";
	}

}
