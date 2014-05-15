package net.anotheria.anosite.photoserver.shared.vo;

import net.anotheria.anosite.photoserver.shared.ApprovalStatus;

import java.io.File;
import java.io.Serializable;

/**
 * User photo information.
 * 
 * @author Alexandr Bolbat
 */
public class PhotoVO implements Serializable, Cloneable {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 611077253349059505L;

	/**
	 * Unique photo id.
	 */
	private long id;

	/**
	 * User id.
	 */
	private String userId;

	/**
	 * Album id.
	 */
	private long albumId;

	/**
	 * Flag that shows where this photo is subject for access control.
	 */
	private boolean restricted;

	/**
	 * Full path to folder where photo file stored.
	 */
	private String fileLocation;

	/**
	 * Extension of the photo file.
	 */
	private String extension;

	/**
	 * Custom photo name from the user. Like: I'm with my friends in USA.
	 */
	private String name;

	/**
	 * Custom photo description from the user. Free text with more detailed information then in name.
	 */
	private String description;

	/**
	 * Modification time.
	 */
	private long modificationTime;

	/**
	 * Preview settings.
	 */
	private PreviewSettingsVO previewSettings;
	
	/**
	 * Photo approval status. WAITING_APPROVAL by default.
	 */
	private ApprovalStatus approvalStatus = ApprovalStatus.WAITING_APPROVAL;

	public void setId(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}

	public long getAlbumId() {
		return albumId;
	}

	public boolean isRestricted() {
		return restricted;
	}

	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public void setFileLocation(String aFileLocation) {
		this.fileLocation = aFileLocation;
	}

	public String getFileLocation() {
		return fileLocation != null ? fileLocation : "";
	}

	public void setName(String aName) {
		this.name = aName;
	}

	public String getName() {
		return name != null ? name : "";
	}

	public void setDescription(String aDescription) {
		this.description = aDescription;
	}

	public String getDescription() {
		return description != null ? description : "";
	}

	public void setModificationTime(long aModificationTime) {
		this.modificationTime = aModificationTime;
	}

	public long getModificationTime() {
		return modificationTime;
	}

	public void setPreviewSettings(PreviewSettingsVO aPreviewSettings) {
		this.previewSettings = aPreviewSettings;
	}

	public PreviewSettingsVO getPreviewSettings() {
		return previewSettings;
	}

	/**
	 * Get full photo file path.
	 * 
	 * @return {@link String} photo file name with full path
	 */
	public String getFilePath() {
		return getFileLocation() + File.separator + String.valueOf(getId()) + getExtension();
	}

	public void setExtension(String aExtension) {
		this.extension = aExtension;
	}

	public String getExtension() {
		return extension != null ? extension : "";
	}

	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}
	
	@Override
	public String toString() {
		return "PhotoVO [id=" + id + ", userId=" + userId + ", albumId=" + albumId + ", fileLocation=" + fileLocation + ", extension=" + extension + ", name="
				+ name + ", description=" + description + ", modificationTime=" + modificationTime + ", previewSettings=" + previewSettings + ", approvalStatus=" + approvalStatus + "]";
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
		PhotoVO other = (PhotoVO) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("PhotoVO should be cloneable!");
		}
	}

}
