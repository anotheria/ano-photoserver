package net.anotheria.anosite.photoserver.shared;

import java.io.Serializable;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.LoggerFactory;

/**
 * Configuration class that is keeping most generic PhotoServer configuration parameters.
 * 
 * @author dzhmud
 */
@ConfigureMe(name = "ano-site-photoserver-config")
public final class PhotoServerConfig implements Serializable {

	/**
	 * Generated serial version UID.
	 */
	@DontConfigure
	private static final long serialVersionUID = 193236663710334093L;

	/**
	 * Single instance of configuration.
	 */
	@DontConfigure
	private static PhotoServerConfig instance;

	/**
	 * Get singleton instance of configuration class.
	 * 
	 * @return {@link net.anotheria.anosite.photoserver.shared.PhotoServerConfig} instance.
	 */
	public static synchronized PhotoServerConfig getInstance() {
		if (instance == null)
			instance = new PhotoServerConfig();
		return instance;
	}

	/**
	 * Field that determines if PhotoAPI should deliver only "approved" photos when user looks at other user photos.
	 */
	@Configure
	private boolean photoApprovingEnabled = true;

	private PhotoServerConfig() {
		try {
			ConfigurationManager.INSTANCE.configure(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(PhotoServerConfig.class).error(
					"PhotoServerConfig() Configuration failed. Configuring with defaults[" + this.toString() + "].");
		}
	}

	public void setPhotoApprovingEnabled(boolean photoApprovingEnabled) {
		this.photoApprovingEnabled = photoApprovingEnabled;
	}

	public boolean isPhotoApprovingEnabled() {
		return photoApprovingEnabled;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PhotoServerConfig [photoApprovingEnabled=");
		builder.append(photoApprovingEnabled);
		builder.append("]");
		return builder.toString();
	}

}
