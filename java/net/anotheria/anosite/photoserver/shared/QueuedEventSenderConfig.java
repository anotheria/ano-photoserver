package net.anotheria.anosite.photoserver.shared;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>QueuedEventSenderConfig class.</p>
 *
 * @author another
 * @version $Id: $Id
 */
public class QueuedEventSenderConfig {
	/**
	 * Default event channel queue size.
	 */
	protected static final int DEFAULT_EVENT_CHANNEL_QUEUE_SIZE = 5000;
	/**
	 * Default event channel queue sleep time.
	 */
	protected static final int DEFAULT_EVENT_CHANNEL_QUEUE_SLEEP = 25;

	private static final ConcurrentMap<String, QueuedEventSenderConfig> instances = new ConcurrentHashMap<String, QueuedEventSenderConfig>();

	private static Logger log = LoggerFactory.getLogger(QueuedEventSenderConfig.class);

	/**
	 * Max queue size.
	 */
	@Configure
	private int eventsQueueSize;

	/**
	 * Sleep time.
	 */
	@Configure
	private int eventsQueueSleepTime;

	/**
	 * Config name for debug and sysout purposes.
	 */
	private String configName;

	private QueuedEventSenderConfig(String aConfigName) {
		eventsQueueSize = DEFAULT_EVENT_CHANNEL_QUEUE_SIZE;
		eventsQueueSleepTime = DEFAULT_EVENT_CHANNEL_QUEUE_SLEEP;
		configName = aConfigName;
	}

	/**
	 * <p>Getter for the field <code>eventsQueueSize</code>.</p>
	 *
	 * @return a int.
	 */
	public int getEventsQueueSize() {
		return eventsQueueSize;
	}

	/**
	 * <p>Setter for the field <code>eventsQueueSize</code>.</p>
	 *
	 * @param eventsQueueSize a int.
	 */
	public void setEventsQueueSize(int eventsQueueSize) {
		this.eventsQueueSize = eventsQueueSize;
	}

	/**
	 * <p>Getter for the field <code>eventsQueueSleepTime</code>.</p>
	 *
	 * @return a int.
	 */
	public int getEventsQueueSleepTime() {
		return eventsQueueSleepTime;
	}

	/**
	 * <p>Setter for the field <code>eventsQueueSleepTime</code>.</p>
	 *
	 * @param eventsQueueSleepTime a int.
	 */
	public void setEventsQueueSleepTime(int eventsQueueSleepTime) {
		this.eventsQueueSleepTime = eventsQueueSleepTime;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return configName + "{" + "eventsQueueSize=" + eventsQueueSize + ", eventsQueueSleepTime=" + eventsQueueSleepTime + '}';
	}

	/**
	 * <p>getQueuedEventSenderConfigByName.</p>
	 *
	 * @param configName a {@link java.lang.String} object.
	 * @return a {@link net.anotheria.anosite.photoserver.shared.QueuedEventSenderConfig} object.
	 */
	public static QueuedEventSenderConfig getQueuedEventSenderConfigByName(String configName) {
		QueuedEventSenderConfig newConfig = new QueuedEventSenderConfig(configName);
		QueuedEventSenderConfig storedConfig = instances.putIfAbsent(configName, newConfig);
		if (storedConfig == null) {
			storedConfig = newConfig;
			try {
				ConfigurationManager.INSTANCE.configureAs(storedConfig, configName);
			} catch (IllegalArgumentException e) {
				log.warn("No config for QueuedEventSenderConfig with name " + configName + " found.");
			}
		}

		return storedConfig;

	}

}
