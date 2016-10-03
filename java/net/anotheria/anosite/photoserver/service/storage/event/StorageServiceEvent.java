package net.anotheria.anosite.photoserver.service.storage.event;

import net.anotheria.anosite.photoserver.service.storage.PhotoBO;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;

import java.io.Serializable;

/**
 * StorageService Event.
 *
 * @author vkazhdan
 * @version $Id: $Id
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


    /**
     * <p>photoCreated.</p>
     *
     * @param newPhoto a {@link net.anotheria.anosite.photoserver.service.storage.PhotoBO} object.
     * @return a {@link net.anotheria.anosite.photoserver.service.storage.event.PhotoCreatedEvent} object.
     */
    public static final PhotoCreatedEvent photoCreated(PhotoBO newPhoto) {
        return new PhotoCreatedEvent(newPhoto);
    }

    /**
     * <p>photoDeleted.</p>
     *
     * @param deletedPhotoId a long.
     * @param owner a {@link java.lang.String} object.
     * @return a {@link net.anotheria.anosite.photoserver.service.storage.event.PhotoDeletedEvent} object.
     */
    public static final PhotoDeletedEvent photoDeleted(long deletedPhotoId, String owner) {
        return new PhotoDeletedEvent(deletedPhotoId, owner);
    }

    /**
     * <p>photoUpdated.</p>
     *
     * @param updatedPhoto a {@link net.anotheria.anosite.photoserver.service.storage.PhotoBO} object.
     * @param originalPhoto a {@link net.anotheria.anosite.photoserver.service.storage.PhotoBO} object.
     * @return a {@link net.anotheria.anosite.photoserver.service.storage.event.PhotoUpdatedEvent} object.
     */
    public static final PhotoUpdatedEvent photoUpdated(PhotoBO updatedPhoto, PhotoBO originalPhoto) {
        return new PhotoUpdatedEvent(updatedPhoto, originalPhoto);
    }

    /**
     * Creates {@link net.anotheria.anosite.photoserver.service.storage.event.PhotoStatusChangedEvent}.
     *
     * @param photoOwner     owner id
     * @param photoId        id of photo
     * @param updatedStatus  current status
     * @param previousStatus previous status
     * @return {@link net.anotheria.anosite.photoserver.service.storage.event.PhotoStatusChangedEvent}
     */
    public static final PhotoStatusChangedEvent statusChanged(String photoOwner, long photoId, ApprovalStatus updatedStatus, ApprovalStatus previousStatus) {
        return new PhotoStatusChangedEvent(photoOwner, photoId, updatedStatus, previousStatus);
    }

    /**
     * <p>getOperation.</p>
     *
     * @return a {@link net.anotheria.anosite.photoserver.service.storage.event.StorageServiceEvent.Operation} object.
     */
    public abstract Operation getOperation();

    /**
     * <p>describePhotos.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected abstract String describePhotos();

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return getOperation() + " " + describePhotos();
    }

}
