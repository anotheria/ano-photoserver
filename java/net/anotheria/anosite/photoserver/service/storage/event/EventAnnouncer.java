package net.anotheria.anosite.photoserver.service.storage.event;

import net.anotheria.anoprise.eventservice.Event;
import net.anotheria.anoprise.eventservice.EventChannel;
import net.anotheria.anoprise.eventservice.EventServiceFactory;
import net.anotheria.anoprise.eventservice.EventServicePushSupplier;
import net.anotheria.anoprise.eventservice.util.QueueFullException;
import net.anotheria.anoprise.eventservice.util.QueuedEventSender;
import net.anotheria.anosite.photoserver.service.storage.PhotoBO;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;
import net.anotheria.anosite.photoserver.shared.QueuedEventSenderConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EventAnnouncer implements EventServicePushSupplier {

	public static final String STORAGESERVICE_EVENTCHANNEL_NAME = "storageservice";

	private QueuedEventSender eventSender;
	private QueuedEventSenderConfig eventSenderConfig;

	private static Logger log = LoggerFactory.getLogger(EventAnnouncer.class);

	public EventAnnouncer() {
		eventSenderConfig = StorageServiceQueuedEventSenderConfig.getInstance();
		EventChannel eventChannel = EventServiceFactory.createEventService().obtainEventChannel(STORAGESERVICE_EVENTCHANNEL_NAME, this);
		boolean unittesting = Boolean.parseBoolean(System.getProperty("JUNITTEST", "false"));
		eventSender = new QueuedEventSender(STORAGESERVICE_EVENTCHANNEL_NAME + "-sender", eventChannel, eventSenderConfig.getEventsQueueSize(), eventSenderConfig.getEventsQueueSleepTime(),
				log);
		if (unittesting) {
			eventSender.setSynchedMode(true);
		} else {
			eventSender.start();
		}
	}

	public void photoCreated(PhotoBO newPhoto) {
		deliverEvent(StorageServiceEvent.photoCreated(newPhoto));
	}

	public void photoUpdated(PhotoBO newPhoto, PhotoBO oldPhoto) {
		deliverEvent(StorageServiceEvent.photoUpdated(newPhoto, oldPhoto));
	}

	public void photoDeleted(long deletedPhotoId, String owner) {
		deliverEvent(StorageServiceEvent.photoDeleted(deletedPhotoId,owner));
	}

	/**
	 * Push status changed event.
	 *
	 * @param ownerId  user id
	 * @param photoId  photo id
	 * @param updated  current status
	 * @param previous prev status
	 */
	public void photoStatusChanged(String ownerId, long photoId, ApprovalStatus updated, ApprovalStatus previous) {
		deliverEvent(StorageServiceEvent.statusChanged(ownerId, photoId, updated, previous));
	}

	private void deliverEvent(StorageServiceEvent eventData) {
		Event event = new Event(eventData);
		event.setOriginator("StorageService");
		try {
			eventSender.push(event);
		} catch (QueueFullException e) {
			log.error("Couldn't publish event due to queue overflow " + event);
		}
	}
}
