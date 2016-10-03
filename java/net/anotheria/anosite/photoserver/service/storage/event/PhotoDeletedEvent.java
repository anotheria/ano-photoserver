package net.anotheria.anosite.photoserver.service.storage.event;

/**
 * PhotoDeleted Event.
 *
 * @author vkazhdan
 * @version $Id: $Id
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

    /** {@inheritDoc} */
    @Override
    public Operation getOperation() {
        return Operation.DELETE;
    }

    /**
     * <p>getDeletedPhotoId.</p>
     *
     * @return a long.
     */
    public long getDeletedPhotoId() {
        return photoId;
    }

    /**
     * <p>Getter for the field <code>ownerId</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getOwnerId() {
        return ownerId;
    }

    /** {@inheritDoc} */
    @Override
    protected String describePhotos() {
        return String.valueOf(photoId);
    }
}
