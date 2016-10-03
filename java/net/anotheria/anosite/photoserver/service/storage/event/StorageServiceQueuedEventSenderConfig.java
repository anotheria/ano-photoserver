package net.anotheria.anosite.photoserver.service.storage.event;

import net.anotheria.anosite.photoserver.shared.QueuedEventSenderConfig;

/**
 * StorageServiceQueuedEventSenderConfig configureMe config.
 *
 * @author vkazhdan
 * @version $Id: $Id
 */
public class StorageServiceQueuedEventSenderConfig {
	/**
	 * <p>getInstance.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.shared.QueuedEventSenderConfig} object.
	 */
	public static QueuedEventSenderConfig getInstance() {
		return QueuedEventSenderConfig.getQueuedEventSenderConfigByName("storage-service-queued-event-sender");
	}
}
