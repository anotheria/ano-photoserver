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

	public boolean isOriginalPhotosAccessible() {
		return originalPhotosAccessible;
	}

	public void setOriginalPhotosAccessible(boolean isOriginalPhotosAccessible) {
		this.originalPhotosAccessible = isOriginalPhotosAccessible;
	}

	public String getPhotoNotFoundLink() {
		return photoNotFoundLink;
	}

	public void setPhotoNotFoundLink(String photoNotFoundLink) {
		this.photoNotFoundLink = photoNotFoundLink;
	}

	public String getNoPhotoAccessLink() {
		return noPhotoAccessLink;
	}

	public void setNoPhotoAccessLink(String noPhotoAccessLink) {
		this.noPhotoAccessLink = noPhotoAccessLink;
	}

	public String getRestrictionBypassCookie() {
		return restrictionBypassCookie;
	}

	public void setRestrictionBypassCookie(String restrictionBypassCookie) {
		this.restrictionBypassCookie = restrictionBypassCookie;
	}

	public ViewAccessResponse getDefaultViewAccessResponse() {
		return defaultViewAccessResponse;
	}

	public void setDefaultViewAccessResponse(ViewAccessResponse defaultViewAccessResponse) {
		this.defaultViewAccessResponse = defaultViewAccessResponse;
	}

    public CroppingType getCroppingType(){
        return croppingType;
    }

    public void setCroppingType(CroppingType croppingType){
        this.croppingType = croppingType;
    }

	public Float getBlurMinRadius() {
		return blurMinRadius;
	}

	public void setBlurMinRadius(Float blurMinRadius) {
		this.blurMinRadius = blurMinRadius;
	}

	public Integer getBlurIteration() {
		return blurIteration;
	}

	public void setBlurIteration(Integer blurIteration) {
		this.blurIteration = blurIteration;
	}

	public BluringRadiusChoose getRadius() {
		return radius;
	}

	public void setRadius(BluringRadiusChoose radius) {
		this.radius = radius;
	}

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
