package net.anotheria.anosite.photoserver.service.storage.event;

import net.anotheria.anosite.photoserver.service.storage.PhotoBO;

/**
 * PhotoCreated Event.
 *
 * @author vkazhdan
 */
public class PhotoCreatedEvent extends StorageServiceEvent {
	/**
	 * SerialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Created photo.
	 */
	private PhotoBO photo;
	
	PhotoCreatedEvent(PhotoBO photoObject) {
		if (photoObject==null)
			throw new IllegalArgumentException("Null is not allowed");
		this.photo = photoObject;
	}
	
	@Override public Operation getOperation(){
		return Operation.CREATE;
	}
	
	public PhotoBO getCreatedPhoto(){
		return photo;
	}

	@Override
	protected String describePhotos() {
		return photo.toString();
	}
}	
