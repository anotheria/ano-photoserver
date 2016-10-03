package net.anotheria.anosite.photoserver.presentation.upload;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.NoLoggedInUserException;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import net.anotheria.anosite.photoserver.api.photo.PhotoAO;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPI;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPI;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploader;
import net.anotheria.anosite.photoserver.api.upload.PhotoWorkbench;
import net.anotheria.anosite.photoserver.presentation.shared.BaseServlet;
import net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO;
import net.anotheria.anosite.photoserver.shared.vo.TempPhotoVO;
import net.anotheria.util.StringUtils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>WorkbenchServlet class.</p>
 *
 * @author otoense
 * @version $Id: $Id
 */
public class WorkbenchServlet extends BaseServlet {

	private static final long serialVersionUID = -8362859860101757828L;

	private static final Logger LOG = LoggerFactory.getLogger(WorkbenchServlet.class);
	/**
	 * {@link PhotoUploadAPI} instance.
	 */
	private PhotoUploadAPI photoUploadAPI;
	/**
	 * {@link LoginAPI} instance.
	 */
	private LoginAPI loginAPI;
	/**
	 * {@link PhotoAPI} instance.
	 */
	private PhotoAPI photoAPI;
	/**
	 * WorkBench id.
	 */
	public static final String PARAM_WORKBENCHID = "id";
	/**
	 * Upload id.
	 */
	public static final String PARAM_UPLOADID = "uploadid";
	/**
	 * Rotation attribute name.
	 */
	public static final String PARAM_ROTATION = "r";

	/**
	 * Work bench id request parameter name. Same as {@link #PARAM_WORKBENCHID} but used for photo create!
	 */
	public static final String PARAM_WORKBENCH_ID = "wbid";
	/**
	 * Transition request parameter name.
	 */
	public static final String PARAM_TRANSITION = "transition";
	/**
	 * {@link HttpServletRequest} parameter: photo
	 */
	public static final String PARAM_UPLOAD_USER_ID = "userId";

	/** {@inheritDoc} */
	@Override
	public void init(ServletConfig config) throws ServletException {
		photoUploadAPI = APIFinder.findAPI(PhotoUploadAPI.class);
		photoAPI = APIFinder.findAPI(PhotoAPI.class);
		loginAPI = APIFinder.findAPI(LoginAPI.class);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String workbenchId = request.getParameter(PARAM_WORKBENCHID);

		if (workbenchId == null || workbenchId.length() == 0) {
			String uploaderId = request.getParameter(PARAM_UPLOADID);
			PhotoUploader uploader = photoUploadAPI.getMyPhotoUploader(uploaderId);
			TempPhotoVO photo = uploader.getUploadedPhoto();
			PhotoWorkbench workbench = photoUploadAPI.createMyPhotoWorkbench(photo);
			JSONObject json = new JSONObject();
			json.put("id", workbench.getId());
			writeResponse(response, json.toJSONString());
			return;
		}

		String rotationParam = request.getParameter(PARAM_ROTATION);
		int rotation = 0;
		if (rotationParam != null && rotationParam.length() != 0) {
			rotation = Integer.parseInt(rotationParam);
		}

		PhotoWorkbench workbench = photoUploadAPI.getMyPhotoWorkbench(workbenchId);
		stream(response, workbench.getWorkbenchImage(rotation));
	}

	/** {@inheritDoc} */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		JSONObject resultJson = new JSONObject();
		final boolean isDebug = LOG.isDebugEnabled();
		try {
			String workbenchId = req.getParameter(PARAM_WORKBENCH_ID);
			String transition = req.getParameter(PARAM_TRANSITION);
			String userId = req.getParameter(PARAM_UPLOAD_USER_ID);

			JSONParser parser = new JSONParser();
			JSONObject tMap = (JSONObject) parser.parse(transition);

			int x = Integer.parseInt(tMap.get("x") + "");
			if (x < 0) {
				x = 0;
				if (isDebug)
					LOG.debug("X coordinate wrong [" + tMap.get("x") + "], relaying on default [" + x + "].");
			}

			int y = Integer.parseInt(tMap.get("y") + "");
			if (y < 0) {
				y = 0;
				if (isDebug)
					LOG.debug("Y coordinate wrong [" + tMap.get("y") + "], relaying on default [" + y + "].");
			}

			int w = Integer.parseInt(tMap.get("w") + "");
			if (w < 1) {
				w = 1;
				if (isDebug)
					LOG.debug("Width wrong [" + tMap.get("w") + "], relaying on default [" + w + "].");
			}

			int h = Integer.parseInt(tMap.get("h") + "");
			if (h < 1) {
				h = 1;
				if (isDebug)
					LOG.debug("Height wrong [" + tMap.get("h") + "], relaying on default [" + h + "].");
			}

			PreviewSettingsVO previewSettings = new PreviewSettingsVO(x, y, w, h);
			int rotation = Integer.parseInt(tMap.get("r") + "");
			if (isDebug)
				LOG.debug("Store uploaded picture." + previewSettings + ", rotation=" + rotation);

			PhotoWorkbench workbench = photoUploadAPI.getMyPhotoWorkbench(workbenchId);
			TempPhotoVO photo = workbench.getPhoto();
			TempPhotoVO rotatedPhoto = photoUploadAPI.rotatePhoto(photo, rotation);

			userId = StringUtils.isEmpty(userId) ? loginAPI.getLogedUserId() : userId;
			if (StringUtils.isEmpty(userId))
				throw new NoLoggedInUserException("No logged in user found");

			PhotoAO createdPhoto = photoAPI.createPhoto(userId, rotatedPhoto.getFile(), previewSettings);
			resultJson.put("encodedPhotoId", createdPhoto.getEncodedId());
			resultJson.put("status", "OK");

		} catch (Exception e) {
			resultJson.put("status", "ERROR");
			resultJson.put("error", e.getMessage());
			LOG.error(e.getMessage(), e);
		}
		writeResponse(resp, resultJson.toJSONString());

	}

}
