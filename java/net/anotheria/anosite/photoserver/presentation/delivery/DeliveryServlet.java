package net.anotheria.anosite.photoserver.presentation.delivery;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anosite.photoserver.api.access.AccessAPI;
import net.anotheria.anosite.photoserver.api.access.AccessParameter;
import net.anotheria.anosite.photoserver.api.photo.PhotoAO;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPI;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPIConfig;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPIException;
import net.anotheria.anosite.photoserver.api.photo.PhotoNotFoundPhotoAPIException;
import net.anotheria.anosite.photoserver.presentation.shared.BaseServlet;
import net.anotheria.anosite.photoserver.shared.CroppingType;
import net.anotheria.anosite.photoserver.shared.IdCrypter;
import net.anotheria.anosite.photoserver.shared.ModifyPhotoSettings;
import net.anotheria.anosite.photoserver.shared.ResizeType;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet for delivering photos by request.
 *
 * @author another
 * @version $Id: $Id
 */
public class DeliveryServlet extends BaseServlet {

	/**
	 * Full access cookie name.
	 */
	public static final String FULL_ACCESS_COOKIE_NAME = "ps_fa";
	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(DeliveryServlet.class);

	// Parameter names and other constants
	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -3250585990874211552L;
	/**
	 * Internal server error message.
	 */
	private static final String INTERNAL_SERVER_ERROR_MSG = "Internal server error";
	/**
	 * Request parameter name for preview flag.
	 */
	private static final String PARAM_PREVIEW = "preview";
	/**
	 * Request parameter name for blur flag.
	 */
	private static final String PARAM_BLUR = "blur";
	/**
	 * Parameter name for access token.
	 */
	private static final String PARAM_ACCESS_TOKEN = "at";
    /**
     * Parameter name for cropping type.
     */
    private static final String PARAM_CROPPING_TYPE = "ct";
    /**
     * Parameter name for source.
     */
    private static final String PARAM_SOURCE= "s";
	/**
	 * Pattern for single size parameter - number.
	 */
	private static final Pattern sizePattern = Pattern.compile("\\d+|-1");
	/**
	 * Pattern for bounding area sizes - two numbers comma separated.
	 */
	private static final Pattern boundingAreaPattern = Pattern.compile("(\\d+),(\\d+)|-1");
	// APIs
	/**
	 * {@link AccessAPI} instance.
	 */
	private static final AccessAPI ACCESS_API = APIFinder.findAPI(AccessAPI.class);
	/**
	 * {@link PhotoAPI} instance.
	 */
	private static final PhotoAPI photoAPI = APIFinder.findAPI(PhotoAPI.class);
	/**
	 * {@link PhotoAPIConfig} instance.
	 */
	private static final PhotoAPIConfig photoAPIConfig = PhotoAPIConfig.getInstance();

	/**
	 * Sets appropriate HTTP status and other response info if photo can't be found.
	 *
	 * @param response
	 * 		{@link HttpServletResponse}
	 */
	private static void responseSetNotFound(final HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
		response.setHeader("Location", DeliveryConfig.getInstance().getPhotoNotFoundLink());
	}

	/**
	 * Writes debug messages.
	 *
	 * @param messages
	 * 		messages to be written to log
	 */
	private static void debug(String... messages) {
		if (messages == null || messages.length == 0)
			return;

		if (LOGGER.isDebugEnabled())
			for (String message : messages)
				LOGGER.debug(message);
	}

	/** {@inheritDoc} */
	@Override
	protected void moskitoDoGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String[] params = parsePathParameters(req);
		debug("Params[" + Arrays.toString(params) + "].");

		// checking encoded photo id parameter
		if (params == null || params.length < 1 || StringUtils.isEmpty(params[0])) {
			String message = "Wrong parameters[" + Arrays.toString(params) + "].";
			LOGGER.info("doGet(req, resp) fail. " + message);
			responseSetNotFound(resp);
			return;
		}

		final String blurredPathParameter = params[1];
		final String rawPhotoId = params[2];

		long photoId;
		try {
			photoId = IdCrypter.decodeToLong(rawPhotoId); // decode encoded photo id
		} catch (RuntimeException re) {
			String message = "Wrong photo id[" + rawPhotoId + "] parameter.";
			LOGGER.info("doGet(req, resp) fail. " + message);
			responseSetNotFound(resp);
			return;
		}

		final ModifyPhotoSettings modifyPhotoSettings = new ModifyPhotoSettings();

		// getting size for requested photo
		if (params.length > 1) {
			boolean sizeDataValid = buildSizeParametersAndValidate(modifyPhotoSettings, params[3]);
			if(!sizeDataValid){
				String message = "Wrong size[" + params[3] + "] parameter.";
				LOGGER.info("doGet(req, resp) fail. " + message);

				responseSetNotFound(resp);
				return;
			}

			// check is requested size is allowed
			if (!photoAPIConfig.isIgnoreAllowedSizes() && !photoAPIConfig.isAllowedSize(modifyPhotoSettings.getSize()) ) {
				LOGGER.info("doGet(req, resp) fail. " + "Requested size[" + params[3] + "] not allowed.");
				responseSetNotFound(resp);
				return;
			}
		}

		PhotoAO photo = getPhoto(photoId);
		if (photo == null) {
			responseSetNotFound(resp);
			return;
		}

        int croppingType = DeliveryConfig.getInstance().getCroppingType().getValue();
        if (! StringUtils.isEmpty(req.getParameter(PARAM_CROPPING_TYPE))){
            try {
                croppingType = Integer.parseInt(req.getParameter(PARAM_CROPPING_TYPE));

                if(CroppingType.valueOf(croppingType)==null){
                    responseSetNotFound(resp);
                    return;
                }
            } catch (NumberFormatException e){
                responseSetNotFound(resp);
                return;
            }
        }

		boolean cropped = req.getParameter(PARAM_PREVIEW) != null;
		boolean resized = modifyPhotoSettings.isResized();
		boolean blurred = req.getParameter(PARAM_BLUR) != null || (photo.isBlurred() && blurredPathParameter.equals("b"));

        Map<AccessParameter, String> optionalParameters = new HashMap<>();
        optionalParameters.put(AccessParameter.SOURCE, req.getParameter(PARAM_SOURCE));
        optionalParameters.put(AccessParameter.BLUR, req.getParameter(PARAM_BLUR));
        optionalParameters.put(AccessParameter.PREVIEW, req.getParameter(PARAM_PREVIEW));
        optionalParameters.put(AccessParameter.ACCESS_TOKEN, req.getParameter(PARAM_ACCESS_TOKEN));
        optionalParameters.put(AccessParameter.ACCESS_COOKIE_NAME, req.getParameter(FULL_ACCESS_COOKIE_NAME));

        // get full access cookie value
        String fullAccessCookieValue = DeliveryConfig.getInstance().getRestrictionBypassCookie();
        // check whether current user has full access granted by cookie
        if (fullAccessCookieValue == null || ! fullAccessCookieValue.equals(getCookieValue(req, FULL_ACCESS_COOKIE_NAME)))
            switch (ACCESS_API.isViewAllowed(photo, optionalParameters)){
                case VIEW_ALLOWED:
                    break;
                case BLURRED_VIEW_ALLOWED:
                    blurred &= true;
                    break;
                case VIEW_DENIED: // same as default
                default:
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
            }

		try {
			// check is delivering for original photos enabled
			if (!cropped && !DeliveryConfig.getInstance().isOriginalPhotosAccessible()) {
				debug("Delivering of original photo is denied. Original photo id: " + photo.getId());
				responseSetNotFound(resp);
				return;
			}


			// delivering original photo
			if (!cropped && !resized && !blurred) {
				InputStream photoInputStream = photoAPI.getPhotoContent(photo);
				debug("Returning original photo: " + photo.getFilePath());
				writeImageHeaders(resp);
				stream(resp, photoInputStream);
				return;
			}

			writeImageHeaders(resp);
			stream(resp, photoAPI.getCachedPhotoContent(photo, modifyPhotoSettings, cropped, croppingType, blurred));
		} catch (Exception e) {
			String message = "doGet(req, resp) fail. " + INTERNAL_SERVER_ERROR_MSG;
			LOGGER.warn(message, e);
			responseSetNotFound(resp);
		}
	}

	/**
	 * Validate incoming size data and populate photo settings holder with it.
	 *
	 * @param modifyPhotoSettings {@link ModifyPhotoSettings}
	 * @param params              size data - single number or two comma separated numbers
	 * @return {@code true} - if incoming size data is valid, {@code false} - otherwise
	 */
	private boolean buildSizeParametersAndValidate(final ModifyPhotoSettings modifyPhotoSettings, final String params) {
		if (StringUtils.isEmpty(params))
			return false;

		Matcher m = sizePattern.matcher(params);
		if (m.matches()) {
			modifyPhotoSettings.setSize(Integer.parseInt(params));
			modifyPhotoSettings.setResizeType(ResizeType.SIZE);
			return true;
		}

		m = boundingAreaPattern.matcher(params);
		if (m.matches()) {
			modifyPhotoSettings.setBoundaryWidth(Integer.parseInt(m.group(1)));
			modifyPhotoSettings.setBoundaryHeight(Integer.parseInt(m.group(2)));
			modifyPhotoSettings.setResizeType(ResizeType.BOUNDING_AREA);
			return true;
		}

		return false;
	}

	/**
	 * Returns {@link PhotoAO} by given photo id, or {@code null} if photo can't be found.
	 *
	 * @param photoId
	 * 		id of the photo
	 * @return {@link PhotoAO}, or {@code null}
	 */
	private PhotoAO getPhoto(final long photoId) {
		try {
			return photoAPI.getPhoto(photoId);
		} catch (PhotoNotFoundPhotoAPIException e) {
			LOGGER.warn("doGet(req, resp) fail. Photo[" + photoId + "] not found.");
		} catch (PhotoAPIException e) {
			LOGGER.warn("doGet(req, resp) fail. " + INTERNAL_SERVER_ERROR_MSG);
		}

		return null;
	}

	/**
	 * <p>getCookieByName.</p>
	 *
	 * @param req a {@link jakarta.servlet.http.HttpServletRequest} object.
	 * @param name a {@link java.lang.String} object.
	 * @return a {@link jakarta.servlet.http.Cookie} object.
	 */
	public static Cookie getCookieByName(HttpServletRequest req, String name) {
		Cookie result = null;
		Cookie[] cookies = req.getCookies();
		if (cookies != null){
			for (Cookie cookie : cookies) {
				if (name.equals(cookie.getName())) {
					return cookie;
				}
			}
		}
		return result;
	}

	/**
	 * <p>getCookieValue.</p>
	 *
	 * @param req a {@link jakarta.servlet.http.HttpServletRequest} object.
	 * @param name a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	public static String getCookieValue(HttpServletRequest req, String name) {
		Cookie cookie = getCookieByName(req, name);

		if(cookie != null)
			return cookie.getValue();
		return null;
	}

}
