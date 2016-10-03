package net.anotheria.anosite.photoserver.presentation.delivery;

import net.anotheria.anosite.photoserver.api.access.ViewAccessResponse;
import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Configuration class that is keeping most delivery PhotoServer configuration parameters.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
@ConfigureMe(name = "ano-site-photoserver-delivery-config")
public final class DeliveryConfig implements Serializable {

	/**
	 * Generated serial version UID.
	 */
	@DontConfigure
	private static final long serialVersionUID = -554996883302930354L;

	/**
	 * Single instance of configuration.
	 */
	@DontConfigure
	private static DeliveryConfig instance;

	/**
	 * Get singleton instance of configuration class.
	 *
	 * @return {@link net.anotheria.anosite.photoserver.presentation.delivery.DeliveryConfig} instance.
	 */
	public static synchronized DeliveryConfig getInstance() {
		if (instance == null)
			instance = new DeliveryConfig();
		return instance;
	}

	/**
	 * Field that determines is DeliveryServlet can delivery original photos.
	 */
	@Configure
	private boolean originalPhotosAccessible = false;

	/**
	 * Field that set link to photo when problems with photo access.
	 */
	@Configure
	private String noPhotoAccessLink = "/img/no_photo_access.jpg";

	/**
	 * Field that set link to photo when problems link loading or no photo with such parameters.
	 */
	@Configure
	private String photoNotFoundLink = "/img/no_photo.jpg";

	/**
	 * Restriction bypass cookie.
	 */
	@Configure
	private String restrictionBypassCookie;

	/**
	 * Default {@link ViewAccessResponse}.
	 */
	@Configure
	private ViewAccessResponse defaultViewAccessResponse = ViewAccessResponse.VIEW_ALLOWED;

    /**
     * Default {@link CroppingType}.
     */
    @Configure
    private CroppingType croppingType = CroppingType.BOTH;

	/**
	 * Minimum radius.
	 */
	@Configure
	private Float blurMinRadius = 9f;
	/**
	 * Number of the blur iteration.
	 */
	@Configure
	private Integer blurIteration = 2;

	@Configure
	private BluringRadiusChoose radius = BluringRadiusChoose.MIN;

	/**
	 * Private constructor.
	 */
	private DeliveryConfig() {
		try {
			ConfigurationManager.INSTANCE.configure(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(DeliveryConfig.class).warn("DeliveryConfig() Configuration failed. Configuring with defaults[" + this.toString() + "].");
		}
	}

	/**
	 * <p>isOriginalPhotosAccessible.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isOriginalPhotosAccessible() {
		return originalPhotosAccessible;
	}

	/**
	 * <p>Setter for the field <code>originalPhotosAccessible</code>.</p>
	 *
	 * @param isOriginalPhotosAccessible a boolean.
	 */
	public void setOriginalPhotosAccessible(boolean isOriginalPhotosAccessible) {
		this.originalPhotosAccessible = isOriginalPhotosAccessible;
	}

	/**
	 * <p>Getter for the field <code>photoNotFoundLink</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getPhotoNotFoundLink() {
		return photoNotFoundLink;
	}

	/**
	 * <p>Setter for the field <code>photoNotFoundLink</code>.</p>
	 *
	 * @param photoNotFoundLink a {@link java.lang.String} object.
	 */
	public void setPhotoNotFoundLink(String photoNotFoundLink) {
		this.photoNotFoundLink = photoNotFoundLink;
	}

	/**
	 * <p>Getter for the field <code>noPhotoAccessLink</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getNoPhotoAccessLink() {
		return noPhotoAccessLink;
	}

	/**
	 * <p>Setter for the field <code>noPhotoAccessLink</code>.</p>
	 *
	 * @param noPhotoAccessLink a {@link java.lang.String} object.
	 */
	public void setNoPhotoAccessLink(String noPhotoAccessLink) {
		this.noPhotoAccessLink = noPhotoAccessLink;
	}

	/**
	 * <p>Getter for the field <code>restrictionBypassCookie</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getRestrictionBypassCookie() {
		return restrictionBypassCookie;
	}

	/**
	 * <p>Setter for the field <code>restrictionBypassCookie</code>.</p>
	 *
	 * @param restrictionBypassCookie a {@link java.lang.String} object.
	 */
	public void setRestrictionBypassCookie(String restrictionBypassCookie) {
		this.restrictionBypassCookie = restrictionBypassCookie;
	}

	/**
	 * <p>Getter for the field <code>defaultViewAccessResponse</code>.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.api.access.ViewAccessResponse} object.
	 */
	public ViewAccessResponse getDefaultViewAccessResponse() {
		return defaultViewAccessResponse;
	}

	/**
	 * <p>Setter for the field <code>defaultViewAccessResponse</code>.</p>
	 *
	 * @param defaultViewAccessResponse a {@link net.anotheria.anosite.photoserver.api.access.ViewAccessResponse} object.
	 */
	public void setDefaultViewAccessResponse(ViewAccessResponse defaultViewAccessResponse) {
		this.defaultViewAccessResponse = defaultViewAccessResponse;
	}

    /**
     * <p>Getter for the field <code>croppingType</code>.</p>
     *
     * @return a {@link net.anotheria.anosite.photoserver.presentation.delivery.CroppingType} object.
     */
    public CroppingType getCroppingType(){
        return croppingType;
    }

    /**
     * <p>Setter for the field <code>croppingType</code>.</p>
     *
     * @param croppingType a {@link net.anotheria.anosite.photoserver.presentation.delivery.CroppingType} object.
     */
    public void setCroppingType(CroppingType croppingType){
        this.croppingType = croppingType;
    }

	/**
	 * <p>Getter for the field <code>blurMinRadius</code>.</p>
	 *
	 * @return a {@link java.lang.Float} object.
	 */
	public Float getBlurMinRadius() {
		return blurMinRadius;
	}

	/**
	 * <p>Setter for the field <code>blurMinRadius</code>.</p>
	 *
	 * @param blurMinRadius a {@link java.lang.Float} object.
	 */
	public void setBlurMinRadius(Float blurMinRadius) {
		this.blurMinRadius = blurMinRadius;
	}

	/**
	 * <p>Getter for the field <code>blurIteration</code>.</p>
	 *
	 * @return a {@link java.lang.Integer} object.
	 */
	public Integer getBlurIteration() {
		return blurIteration;
	}

	/**
	 * <p>Setter for the field <code>blurIteration</code>.</p>
	 *
	 * @param blurIteration a {@link java.lang.Integer} object.
	 */
	public void setBlurIteration(Integer blurIteration) {
		this.blurIteration = blurIteration;
	}

	/**
	 * <p>Getter for the field <code>radius</code>.</p>
	 *
	 * @return a {@link net.anotheria.anosite.photoserver.presentation.delivery.BluringRadiusChoose} object.
	 */
	public BluringRadiusChoose getRadius() {
		return radius;
	}

	/**
	 * <p>Setter for the field <code>radius</code>.</p>
	 *
	 * @param radius a {@link net.anotheria.anosite.photoserver.presentation.delivery.BluringRadiusChoose} object.
	 */
	public void setRadius(BluringRadiusChoose radius) {
		this.radius = radius;
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("DeliveryConfig{");
		sb.append("originalPhotosAccessible=").append(originalPhotosAccessible);
		sb.append(", noPhotoAccessLink='").append(noPhotoAccessLink);
		sb.append(", photoNotFoundLink='").append(photoNotFoundLink);
		sb.append(", restrictionBypassCookie='").append(restrictionBypassCookie);
		sb.append(", defaultViewAccessResponse=").append(defaultViewAccessResponse);
		sb.append(", croppingType=").append(croppingType);
		sb.append(", blurMinRadius=").append(blurMinRadius);
		sb.append(", blurIteration=").append(blurIteration);
		sb.append(", radius=").append(radius);
		sb.append('}');
		return sb.toString();
	}
}
