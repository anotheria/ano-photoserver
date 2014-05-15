package net.anotheria.anosite.photoserver.service.storage.event;

import net.anotheria.anosite.photoserver.shared.ApprovalStatus;

/**
 * PhotoStatusChangedEvent.
 *
 * @author h3ll
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

	public String getOwnerId() {
		return ownerId;
	}

	public long getPhotoId() {
		return photoId;
	}

	public ApprovalStatus getUpdatedStatus() {
		return updatedStatus;
	}

	public ApprovalStatus getPreviousStatus() {
		return previousStatus;
	}

	@Override
	public Operation getOperation() {
		return Operation.STATUS_CHANGED;
	}

	@Override
	protected String describePhotos() {
		return getOperation() + " -> " + this.toString();
	}

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
