package net.anotheria.anosite.photoserver.service.storage.event;

import net.anotheria.anosite.photoserver.shared.ApprovalStatus;

/**
 * PhotoStatusChangedEvent.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class PhotoStatusChangedEvent extends StorageServiceEvent {
	/**
	 * Basic serial version UID.
	 */
	private static final long serialVersionUID = -5676543772961676820L;
	/**
	 * UserId - owner.
	 */
	private String ownerId;
	/**
	 * Selected photo id.
	 */
	private long photoId;
	/**
	 * Current status.
	 */
	private ApprovalStatus updatedStatus;
	/**
	 * Previous status.
	 */
	private ApprovalStatus previousStatus;


	/**
	 * Constructor.
	 *
	 * @param aOwnerId id of photo owner
	 * @param aPhotoId id of photo
	 * @param current  current updated status
	 * @param previous previous status
	 */
	PhotoStatusChangedEvent(String aOwnerId, long aPhotoId, ApprovalStatus current, ApprovalStatus previous) {
		this.ownerId = aOwnerId;
		this.photoId = aPhotoId;
		this.updatedStatus = current;
		this.previousStatus = previous;
	}

	/**
	 * <p>Getter for the field <code>ownerId</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getOwnerId() {
		return ownerId;
	}

	/**
	 * <p>Getter for the field <code>photoId</code>.</p>
	 *
	 * @return a long.
	 */
	public long getPhotoId() {
		return photoId;
	}

	/**
	 * <p>Getter for the field <code>updatedStatus</code>.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.shared.ApprovalStatus} object.
	 */
	public ApprovalStatus getUpdatedStatus() {
		return updatedStatus;
	}

	/**
	 * <p>Getter for the field <code>previousStatus</code>.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.shared.ApprovalStatus} object.
	 */
	public ApprovalStatus getPreviousStatus() {
		return previousStatus;
	}

	/** {@inheritDoc} */
	@Override
	public Operation getOperation() {
		return Operation.STATUS_CHANGED;
	}

	/** {@inheritDoc} */
	@Override
	protected String describePhotos() {
		return getOperation() + " -> " + this.toString();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "PhotoStatusChangedEvent{" +
				"ownerId=" + ownerId +
				", photoId=" + photoId +
				", updatedStatus=" + updatedStatus +
				", previousStatus=" + previousStatus +
				'}';
	}
}
