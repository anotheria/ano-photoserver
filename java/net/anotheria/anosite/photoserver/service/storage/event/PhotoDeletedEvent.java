package net.anotheria.anosite.photoserver.service.storage.event;

/**
 * PhotoDeleted Event.
 *
 * @author vkazhdan
 */
public class PhotoDeletedEvent extends StorageServiceEvent {
    /**
     * SerialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Deleted photo id.
     */
    private long photoId;
    /**
     * Photo owner id.
     */
    private String ownerId;

    /**
     * Constructor.
     *
     * @param photoId id of the photo
     * @param owner   owner id
     */
    PhotoDeletedEvent(long photoId, String owner) {
        this.photoId = photoId;
        this.ownerId = owner;
    }

    @Override
    public Operation getOperation() {
        return Operation.DELETE;
    }

    public long getDeletedPhotoId() {
        return photoId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    @Override
    protected String describePhotos() {
        return String.valueOf(photoId);
    }
}
