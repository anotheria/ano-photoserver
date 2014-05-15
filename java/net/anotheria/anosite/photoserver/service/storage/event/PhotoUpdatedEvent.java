package net.anotheria.anosite.photoserver.service.storage.event;

import net.anotheria.anosite.photoserver.service.storage.PhotoBO;

/**
 * PhotoUpdated Event.
 * 
 * @author vkazhdan
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

	@Override
	public Operation getOperation() {
		return Operation.UPDATE;
	}

	public PhotoBO getUpdatedPhoto() {
		return newPhoto;
	}

	public PhotoBO getOriginalPhoto() {
		return oldPhoto;
	}

	@Override
	protected String describePhotos() {
		return oldPhoto.toString() + " -> " + newPhoto.toString();
	}
}
