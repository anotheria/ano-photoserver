package net.anotheria.anosite.photoserver.presentation.upload;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPI;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploader;
import net.anotheria.anosite.photoserver.presentation.shared.BaseServlet;
import net.anotheria.util.StringUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is a generic fileupload servlet. You need to extend it and implement the getFileReceiver-Method in order to do whatever is neccessary with the uploaded
 * file data.
 *
 * On a GET request without parameters the servlet responses with a unique upload-id (json object {id: xxx }) which has to be present on a following POST
 * (multipart/form) request as parameter "id".
 *
 * On a GET request with parameter "id" present passing a unique upload-id it responses with a json object giving the current status of the upload:
 *
 * Example: {"progress":0..100%,"status":0,"data":null,"filename":"102702439_55cec15215.jpg","size":"54 kB"}
 *
 * Description:
 * Attribute
 * Description
 * 
 * progress
 * How many percent of the file is uploaded 0..100%
 * 
 * 
 * status
 * status of the fileupload:
 * <ul>
 * <li>2: upload has not started yes</li>
 * <li>1: upload in progress</li>
 * <li>0: upload finished</li>
 * <li>-1: filesize to large error</li>
 * <li>-2: some undefined technical problems (e.g. status 500)
 * <li>-3: file was rejected by FileReceiver (reason may be in attribute 'data')
 * <li>-4: uploadId is invalid (maybe timeout?)
 * </ul>
 * 
 * 
 * 
 * data
 * filereceiver may pass data to client using this attribute. e.g. url of img-src or error-text
 * 
 * 
 * filename
 * filename of the uploaded file, only present if status=0
 * 
 * 
 * size
 * human readable size of file, only present if status=0
 * 
 *
 * @author otoense
 * @version $Id: $Id
 */
public class UploadServlet extends BaseServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOG = LoggerFactory.getLogger(UploadServlet.class);

	private static final PhotoUploadAPI photoUploadAPI = APIFinder.findAPI(PhotoUploadAPI.class);

	/** Constant <code>PARAM_UPLOADID="id"</code> */
	public static final String PARAM_UPLOADID = "id";

	/**
	 * {@link HttpServletRequest} parameter: photo
	 */
	public static final String PARAM_UPLOAD_USER_ID = "userId";

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	protected void moskitoDoGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uploadId = request.getParameter(PARAM_UPLOADID);
		String userId = request.getParameter(PARAM_UPLOAD_USER_ID);

		if (StringUtils.isEmpty(uploadId)) {
			PhotoUploader uploader;
			try {
				uploader = photoUploadAPI.createPhotoUploader(userId);
			} catch (APIException e) {
				LOG.warn("Can't create PhotoUploader", e);
				return;
			}
			JSONObject json = new JSONObject();
			json.put("id", uploader.getId());
			writeResponse(response, json.toJSONString());
			return;
		}

		PhotoUploader uploader = photoUploadAPI.getMyPhotoUploader(uploadId);
		writeResponse(response, uploader.getStatus().toJSONString());
	}

	/** {@inheritDoc} */
	@Override
	protected void moskitoDoPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uploadId = request.getParameter(PARAM_UPLOADID);

		if (uploadId == null || uploadId.length() == 0) {
			throw new ServletException("Invalid uploadId");
		}

		PhotoUploader uploader = photoUploadAPI.getMyPhotoUploader(uploadId);
		uploader.doUpload(request);

		// Response is empty. However, to prevent situations some browsers use default
		// content type (application/octet-stream) and open save file dialog
		// on this response.
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
	}
}
