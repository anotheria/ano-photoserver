package net.anotheria.anosite.photoserver.presentation.delivery;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anosite.photoserver.api.access.AccessAPI;
import net.anotheria.anosite.photoserver.api.access.AccessParameter;
import net.anotheria.anosite.photoserver.api.photo.PhotoAO;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPI;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPIConfig;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPIException;
import net.anotheria.anosite.photoserver.api.photo.PhotoNotFoundPhotoAPIException;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPIConfig;
import net.anotheria.anosite.photoserver.presentation.shared.BaseServlet;
import net.anotheria.anosite.photoserver.presentation.shared.PhotoDimension;
import net.anotheria.anosite.photoserver.presentation.shared.PhotoUtil;
import net.anotheria.anosite.photoserver.shared.IdCrypter;
import net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO;
import net.anotheria.util.StringUtils;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;
import net.anotheria.webutils.util.CookieUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet for delivering photos by request.
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
	 * {@link PhotoUploadAPIConfig} instance.
	 */
	private static final PhotoUploadAPIConfig photoUploadAPIConfig = PhotoUploadAPIConfig.getInstance();
	/**
	 * {@link IdBasedLockManager} instance.
	 */
	private static final IdBasedLockManager<String> LOCK_MANAGER = new SafeIdBasedLockManager<String>();

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

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String[] params = parsePathParameters(req);
		debug("Params[" + Arrays.toString(params) + "].");

		// checking encoded photo id parameter
		if (params == null || params.length < 1 || StringUtils.isEmpty(params[0])) {
			String message = "Wrong parameters[" + Arrays.toString(params) + "].";
			LOGGER.info("doGet(req, resp) fail. " + message);
			responseSetNotFound(resp);
			return;
		}

		final String rawPhotoId = params[0];

		long photoId;
		try {
			photoId = IdCrypter.decodeToLong(rawPhotoId); // decode encoded photo id
		} catch (RuntimeException re) {
			String message = "Wrong photo id[" + rawPhotoId + "] parameter.";
			LOGGER.info("doGet(req, resp) fail. " + message);
			responseSetNotFound(resp);
			return;
		}

		int size = -1;
		// getting size for requested photo
		if (params.length > 1) {
			try {
				size = Integer.parseInt(params[1]);
			} catch (NumberFormatException nfe) {
				String message = "Wrong size[" + params[1] + "] parameter.";
				LOGGER.info("doGet(req, resp) fail. " + message);

				responseSetNotFound(resp);
				return;
			}
			// check is requested size is allowed
			if (!photoAPIConfig.isIgnoreAllowedSizes() && !photoAPIConfig.isAllowedSize(size)) {
				LOGGER.info("doGet(req, resp) fail. " + "Requested size[" + params[1] + "] not allowed.");
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
		boolean resized = size != -1;
		boolean blurred = req.getParameter(PARAM_BLUR) != null || photo.isBlurred();

        Map<AccessParameter, String> optionalParameters = new HashMap<AccessParameter, String>();
        optionalParameters.put(AccessParameter.SOURCE, req.getParameter(PARAM_SOURCE));
        optionalParameters.put(AccessParameter.BLUR, req.getParameter(PARAM_BLUR));
        optionalParameters.put(AccessParameter.PREVIEW, req.getParameter(PARAM_PREVIEW));
        optionalParameters.put(AccessParameter.ACCESS_TOKEN, req.getParameter(PARAM_ACCESS_TOKEN));
        optionalParameters.put(AccessParameter.ACCESS_COOKIE_NAME, req.getParameter(FULL_ACCESS_COOKIE_NAME));

        // get full access cookie value
        String fullAccessCookieValue = DeliveryConfig.getInstance().getRestrictionBypassCookie();
        // check whether current user has full access granted by cookie
        if (fullAccessCookieValue == null || ! fullAccessCookieValue.equals(CookieUtil.getCookieValue(req, FULL_ACCESS_COOKIE_NAME)))
            switch (ACCESS_API.isViewAllowed(photo, optionalParameters)){
                case VIEW_ALLOWED:
                    break;
                case BLURRED_VIEW_ALLOWED:
                    blurred = true;
                    break;
                case VIEW_DENIED: // same as default
                default:
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
            }

		try {
			// check is delivering for original photos enabled
			if (!cropped && !DeliveryConfig.getInstance().isOriginalPhotosAccessible()) {
				debug("Delivering of original photo is denied. Original photo id: " + photo.getId() + ", path: " + photo.getFilePath());
				responseSetNotFound(resp);
				return;
			}

			// delivering original photo
			if (!cropped && !resized && !blurred) {
				debug("Returning original photo: " + photo.getFilePath());

				File photoFile = new File(photo.getFilePath());
				writeJpegHeaders(resp);
				streamImageFile(resp, photoFile);
				return;
			}

			// preparing cached photo postfix
			String cachedFile = photo.getFilePath();
			cachedFile += cropped ? "_c_t" + croppingType : "";
			cachedFile += resized ? "_s" + size : "";
			cachedFile += blurred ? "_b" : "";

			// checking cached photo and steaming it if exist
			if (streamPhoto(cachedFile, resp))
				return;


			// locking all incoming photo modification requests for same picture
			IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(cachedFile);
			lock.lock();
			try {
				// checking again cached photo and steaming it if exist
				if (streamPhoto(cachedFile, resp))
					return;

				// modifying photo and storing to new photo file
				modifyPhoto(photo.getFilePath(), cachedFile, cropped, photo.getPreviewSettings(), blurred, resized, size, croppingType);
			} finally {
				lock.unlock();
			}

			// streaming cached file
			streamPhoto(cachedFile, resp);
		} catch (Exception e) {
			String message = "doGet(req, resp) fail. " + INTERNAL_SERVER_ERROR_MSG;
			LOGGER.warn(message, e);
			responseSetNotFound(resp);
		}
	}

	/**
	 * Stream photo file if file exist.
	 *
	 * @param photoPath
	 * 		- full photo file path
	 * @param resp
	 * 		- response
	 * @return <code>true</code> if photo steamed or <code>false</code>
	 * @throws java.io.IOException
	 */
	private boolean streamPhoto(final String photoPath, final HttpServletResponse resp) throws IOException {
		File cachedPhoto = new File(photoPath);
		if (cachedPhoto.exists() && cachedPhoto.length() > 0) {
			LOGGER.debug("Returning cached photo: " + photoPath);
			writeJpegHeaders(resp);
			streamImageFile(resp, cachedPhoto);
			return true;
		}

		return false;
	}

	/**
	 * Modify photo with some settings and store it to some file.
	 *
	 * @param photoPath
	 * 		- full photo file path
	 * @param resultPhotoPath
	 * 		- full result photo file path
	 * @param croped
	 * 		- is need crop
	 * @param pvSettings
	 * 		- crop settings
	 * @param blurred
	 * 		- is blur
	 * @param resized
	 * 		- is resize
	 * @param size
	 * 		- size
     * 	@param croppingType
     * 		- value of {@link CroppingType} enum
	 * @throws java.io.IOException
	 */
	private void modifyPhoto(final String photoPath, final String resultPhotoPath, final boolean croped, final PreviewSettingsVO pvSettings,
							 final boolean blurred, final boolean resized, final int size, final int croppingType) throws IOException {
		debug("Changing original photo: " + photoPath);

		// read photo file
		PhotoUtil putil = new PhotoUtil();
		putil.read(new File(photoPath));

		// if preview param is present we have to crop image first
		if (croped) {
			PhotoDimension move = new PhotoDimension(pvSettings.getX(), pvSettings.getY());
			PhotoDimension crop = new PhotoDimension(pvSettings.getWidth(), pvSettings.getHeight());
			PhotoDimension originalDimension = new PhotoDimension(putil.getWidth(), putil.getHeight());
			PhotoDimension workbenchDimension;
			if (putil.getHeight() > putil.getWidth()) {
				workbenchDimension = new PhotoDimension(photoUploadAPIConfig.getWorkbenchWidth() * putil.getWidth() / putil.getHeight(),
						photoUploadAPIConfig.getWorkbenchWidth());
			} else {
				workbenchDimension = new PhotoDimension(photoUploadAPIConfig.getWorkbenchWidth(), photoUploadAPIConfig.getWorkbenchWidth() * putil.getHeight()
						/ putil.getWidth());
			}
			PhotoDimension xy = move.getRelationTo(workbenchDimension, originalDimension);
			PhotoDimension wh = crop.getRelationTo(workbenchDimension, originalDimension);

			debug("originalDimension:" + originalDimension);
			debug("workbenchDimension:" + workbenchDimension);
			debug("move:" + move);
			debug("crop:" + crop);
			debug("Crop:" + xy.w + "," + xy.h + "," + wh.w + "," + wh.h);

			putil.crop(xy.w, xy.h, wh.w, wh.h);
		}

		// scale photo if needed
		if (resized){
            int height = putil.getHeight();
            int width = putil.getWidth();

            switch (CroppingType.valueOf(croppingType)){
                case HEIGHT:
                    putil.scale((int)((double)size/height*width), size);
                    break;
                case NATURAL_HEIGHT:
                    height = height<size?height:size;
                    width = (int)((double)height/putil.getHeight()*width);

                    putil.scale(width, height);
                    break;
                case WIDTH:
                    putil.scale(size,(int)((double)size/width*height));
                    break;
                case NATURAL_WIDTH:
                    width = width<size?width:size;
                    height = (int)((double)width/putil.getWidth()*height);

                    putil.scale(width, height);
                    break;
                case BOTH:
                    putil.scale(size);
                    break;
                case NATURAL_BOTH:
                    if(width>size&&height>size)
                        putil.scale(size);
            }
        }

		// if blur param is present or photo should be blurred for user we have to blur image
		if (blurred)
			putil.blur();


		// caching changed photo
		debug("Storing changed photo: " + resultPhotoPath);
		File cachedPhoto = new File(resultPhotoPath);
		putil.write(photoAPIConfig.getJpegQuality(), cachedPhoto);
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
}
