package net.anotheria.anosite.photoserver.service.storage.event;

import net.anotheria.anosite.photoserver.service.storage.PhotoBO;

/**
 * PhotoUpdated Event.
 *
 * @author vkazhdan
 * @version $Id: $Id
 */
public class PhotoUpdatedEvent extends StorageServiceEvent {
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Old photo.
	 */
	private PhotoBO oldPhoto;
	/**
	 * New photo.
	 */
	private PhotoBO newPhoto;

	PhotoUpdatedEvent(PhotoBO newObject, PhotoBO oldObject) {
		this.oldPhoto = oldObject;
		this.newPhoto = newObject;
	}

	/** {@inheritDoc} */
	@Override
	public Operation getOperation() {
		return Operation.UPDATE;
	}

	/**
	 * <p>getUpdatedPhoto.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.service.storage.PhotoBO} object.
	 */
	public PhotoBO getUpdatedPhoto() {
		return newPhoto;
	}

	/**
	 * <p>getOriginalPhoto.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.service.storage.PhotoBO} object.
	 */
	public PhotoBO getOriginalPhoto() {
		return oldPhoto;
	}

	/** {@inheritDoc} */
	@Override
	protected String describePhotos() {
		return oldPhoto.toString() + " -> " + newPhoto.toString();
	}
}
