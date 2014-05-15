package net.anotheria.anosite.photoserver.service.storage.event;

import net.anotheria.anosite.photoserver.shared.QueuedEventSenderConfig;

/**
 * StorageServiceQueuedEventSenderConfig configureMe config.
 * 
 * @author vkazhdan
 */
public class StorageServiceQueuedEventSenderConfig {
	public static QueuedEventSenderConfig getInstance() {
		return QueuedEventSenderConfig.getQueuedEventSenderConfigByName("storage-service-queued-event-sender");
	}
}
