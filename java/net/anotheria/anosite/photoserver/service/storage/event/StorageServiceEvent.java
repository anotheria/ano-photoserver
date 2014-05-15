package net.anotheria.anosite.photoserver.service.storage.event;

import net.anotheria.anosite.photoserver.service.storage.PhotoBO;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;

import java.io.Serializable;

/**
 * StorageService Event.
 *
 * @author vkazhdan
 */
public abstract class StorageServiceEvent implements Serializable {
    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Event operations.
     */
    public static enum Operation {
        CREATE, UPDATE, DELETE, STATUS_CHANGED
    }


    public static final PhotoCreatedEvent photoCreated(PhotoBO newPhoto) {
        return new PhotoCreatedEvent(newPhoto);
    }

    public static final PhotoDeletedEvent photoDeleted(long deletedPhotoId, String owner) {
        return new PhotoDeletedEvent(deletedPhotoId, owner);
    }

    public static final PhotoUpdatedEvent photoUpdated(PhotoBO updatedPhoto, PhotoBO originalPhoto) {
        return new PhotoUpdatedEvent(updatedPhoto, originalPhoto);
    }

    /**
     * Creates {@link PhotoStatusChangedEvent}.
     *
     * @param photoOwner     owner id
     * @param photoId        id of photo
     * @param updatedStatus  current status
     * @param previousStatus previous status
     * @return {@link PhotoStatusChangedEvent}
     */
    public static final PhotoStatusChangedEvent statusChanged(String photoOwner, long photoId, ApprovalStatus updatedStatus, ApprovalStatus previousStatus) {
        return new PhotoStatusChangedEvent(photoOwner, photoId, updatedStatus, previousStatus);
    }

    public abstract Operation getOperation();

    protected abstract String describePhotos();

    @Override
    public String toString() {
        return getOperation() + " " + describePhotos();
    }

}
