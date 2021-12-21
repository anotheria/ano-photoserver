package net.anotheria.anosite.photoserver.shared.vo;

import net.anotheria.anoprise.dualcrud.CrudSaveable;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;

import java.io.File;
import java.io.Serializable;

/**
 * User photo information.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class PhotoVO implements Serializable, Cloneable, CrudSaveable {

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

	/**
	 * User photo.
	 */
	private File tempFile;

	/**
	 * <p>Setter for the field <code>id</code>.</p>
	 *
	 * @param id a long.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * <p>Getter for the field <code>id</code>.</p>
	 *
	 * @return a long.
	 */
	public long getId() {
		return id;
	}

	/**
	 * <p>Setter for the field <code>userId</code>.</p>
	 *
	 * @param userId a {@link java.lang.String} object.
	 */
	public void setUserId(String userId) {
		this.userId = userId;
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
	 * <p>Setter for the field <code>albumId</code>.</p>
	 *
	 * @param albumId a long.
	 */
	public void setAlbumId(long albumId) {
		this.albumId = albumId;
	}

	/**
	 * <p>Getter for the field <code>albumId</code>.</p>
	 *
	 * @return a long.
	 */
	public long getAlbumId() {
		return albumId;
	}

	/**
	 * <p>isRestricted.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isRestricted() {
		return restricted;
	}

	/**
	 * <p>Setter for the field <code>restricted</code>.</p>
	 *
	 * @param restricted a boolean.
	 */
	public void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	/**
	 * <p>Setter for the field <code>fileLocation</code>.</p>
	 *
	 * @param aFileLocation a {@link java.lang.String} object.
	 */
	public void setFileLocation(String aFileLocation) {
		this.fileLocation = aFileLocation;
	}

	/**
	 * <p>Getter for the field <code>fileLocation</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getFileLocation() {
		return fileLocation != null ? fileLocation : "";
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
	 * <p>Getter for the field <code>name</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getName() {
		return name != null ? name : "";
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
	 * <p>Setter for the field <code>modificationTime</code>.</p>
	 *
	 * @param aModificationTime a long.
	 */
	public void setModificationTime(long aModificationTime) {
		this.modificationTime = aModificationTime;
	}

	/**
	 * <p>Getter for the field <code>modificationTime</code>.</p>
	 *
	 * @return a long.
	 */
	public long getModificationTime() {
		return modificationTime;
	}

	/**
	 * <p>Setter for the field <code>previewSettings</code>.</p>
	 *
	 * @param aPreviewSettings a {@link net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO} object.
	 */
	public void setPreviewSettings(PreviewSettingsVO aPreviewSettings) {
		this.previewSettings = aPreviewSettings;
	}

	/**
	 * <p>Getter for the field <code>previewSettings</code>.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO} object.
	 */
	public PreviewSettingsVO getPreviewSettings() {
		return previewSettings;
	}

	/**
	 * Get full photo file path.
	 *
	 * @return {@link java.lang.String} photo file name with full path
	 */
	public String getFilePath() {
		return getFileLocation() + File.separator + String.valueOf(getId()) + getExtension();
	}

	/**
	 * <p>Setter for the field <code>extension</code>.</p>
	 *
	 * @param aExtension a {@link java.lang.String} object.
	 */
	public void setExtension(String aExtension) {
		this.extension = aExtension;
	}

	/**
	 * <p>Getter for the field <code>extension</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getExtension() {
		return extension != null ? extension : "";
	}

	/**
	 * <p>Setter for the field <code>approvalStatus</code>.</p>
	 *
	 * @param approvalStatus a {@link net.anotheria.anosite.photoserver.shared.ApprovalStatus} object.
	 */
	public void setApprovalStatus(ApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	/**
	 * <p>Getter for the field <code>approvalStatus</code>.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.shared.ApprovalStatus} object.
	 */
	public ApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	public File getTempFile() {
		return tempFile;
	}

	public void setTempFile(File tempFile) {
		this.tempFile = tempFile;
	}

	@Override
	public String toString() {
		return "PhotoVO{" +
				"id=" + id +
				", userId='" + userId + '\'' +
				", albumId=" + albumId +
				", restricted=" + restricted +
				", fileLocation='" + fileLocation + '\'' +
				", extension='" + extension + '\'' +
				", name='" + name + '\'' +
				", description='" + description + '\'' +
				", modificationTime=" + modificationTime +
				", previewSettings=" + previewSettings +
				", approvalStatus=" + approvalStatus +
				", tempFile=" + tempFile +
				'}';
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
		PhotoVO other = (PhotoVO) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("PhotoVO should be cloneable!");
		}
	}

	@Override
	public String getOwnerId() {
		return userId + "_" + id;
	}
}
