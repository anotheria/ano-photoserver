package net.anotheria.anosite.photoserver.shared;

import java.io.Serializable;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Configuration of photo server tier services.
 * 
 * @author Alexandr Bolbat
 */
@ConfigureMe(name = "ano-site-photoserver-services-config")
public final class PhotoServerServicesConfig implements Serializable {

	/**
	 * Basic serialVersionUID variable.
	 */
	@DontConfigure
	private static final long serialVersionUID = 3035931284964993691L;

	/**
	 * Logger.
	 */
	@DontConfigure
	private static final Logger LOGGER = LoggerFactory.getLogger(PhotoServerServicesConfig.class);

	/**
	 * Configuration instance.
	 */
	@DontConfigure
	private static PhotoServerServicesConfig INSTANCE;

	/**
	 * Is services configured for remote use. If <code>false</code> all services will be configured for local use.
	 */
	@Configure
	private boolean remoteServices = false;

	/**
	 * Services configuration.
	 */
	@Configure
	private boolean storageServiceRemote = true;
	@Configure
	private String storageServiceRemoteFactory = "net.anotheria.anosite.photoserver.service.storage.generated.RemoteStorageServiceFactory";
	@Configure
	private boolean blurSettingsServiceRemote = true;
	@Configure
	private String blurSettingsServiceRemoteFactory = "net.anotheria.anosite.photoserver.service.blur.generated.RemoteBlurSettingsServiceFactory";

	/**
	 * Get instance method.
	 * 
	 * @return {@link net.anotheria.anosite.photoserver.shared.PhotoServerServicesConfig}
	 */
	public static synchronized PhotoServerServicesConfig getInstance() {
		if (INSTANCE == null)
			INSTANCE = new PhotoServerServicesConfig();

		return INSTANCE;
	}

	/**
	 * Default constructor.
	 */
	private PhotoServerServicesConfig() {
		try {
			ConfigurationManager.INSTANCE.configure(this);
			LOGGER.info("PhotoServerServicesConfig() Configured. Configuration[" + this.toString() + "].");
		} catch (Exception e) {
			LOGGER.warn("PhotoServerServicesConfig() Configuration failed. Configuring with defaults[" + this.toString() + "].");
		}
	}

	public boolean isRemoteServices() {
		return remoteServices;
	}

	public void setRemoteServices(boolean remoteServices) {
		this.remoteServices = remoteServices;
	}

	public boolean isStorageServiceRemote() {
		return storageServiceRemote;
	}

	public void setStorageServiceRemote(boolean storageServiceRemote) {
		this.storageServiceRemote = storageServiceRemote;
	}

	public String getStorageServiceRemoteFactory() {
		return storageServiceRemoteFactory;
	}

	public void setStorageServiceRemoteFactory(String storageServiceRemoteFactory) {
		this.storageServiceRemoteFactory = storageServiceRemoteFactory;
	}

	public boolean isBlurSettingsServiceRemote() {
		return blurSettingsServiceRemote;
	}

	public void setBlurSettingsServiceRemote(boolean blurSettingsServiceRemote) {
		this.blurSettingsServiceRemote = blurSettingsServiceRemote;
	}

	public String getBlurSettingsServiceRemoteFactory() {
		return blurSettingsServiceRemoteFactory;
	}

	public void setBlurSettingsServiceRemoteFactory(String blurSettingsServiceRemoteFactory) {
		this.blurSettingsServiceRemoteFactory = blurSettingsServiceRemoteFactory;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PhotoServerServicesConfig [");
		builder.append("\n remoteServices=" + remoteServices);
		builder.append(",\n storageServiceRemote=" + storageServiceRemote);
		builder.append(",\n storageServiceRemoteFactory=" + storageServiceRemoteFactory);
		builder.append(",\n blurSettingsServiceRemote=" + blurSettingsServiceRemote);
		builder.append(",\n blurSettingsServiceRemoteFactory=" + blurSettingsServiceRemoteFactory);
		builder.append("]");
		return builder.toString();
	}

}
