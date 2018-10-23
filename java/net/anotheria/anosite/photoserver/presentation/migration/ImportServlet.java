package net.anotheria.anosite.photoserver.presentation.migration;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anosite.photoserver.api.photo.AlbumAO;
import net.anotheria.anosite.photoserver.api.photo.PhotoAO;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPI;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPI;
import net.anotheria.anosite.photoserver.api.upload.PhotoUploader;
import net.anotheria.anosite.photoserver.presentation.shared.BaseServlet;
import net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO;
import net.anotheria.anosite.photoserver.shared.vo.TempPhotoVO;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This is a generic photo import servlet.
 * {@link javax.servlet.http.HttpServletRequest} should contain such parameters.
 * <ul>
 * <li>
 *   {@link java.lang.String} pLink - link on photo location for loading by link (example: http://my.server.com/).
 * </li>
 * <li>
 *   {@link java.lang.String} userId - id of photo owner.
 * </li>
 * <li>
 *   {@link java.lang.Long} albumId - id of photo album, create new album if there aer no photo with such album.
 * </li>
 * <li>
 *   {@link java.lang.String} albumName - name of photo album, create new album if there aer no photo with such album.
 * </li>
 * <li>
 *   {@link org.json.JSONObject} previewSettings - photo preview settings, setting of photo cropping in JSON format (example {"x": 0, "y": 0, "width": 100, "height": 100}).
 * </li>
 * <li>
 *   {@link java.lang.String} callback - callback method name.
 * </li>
 * </ul>
 * Can get error in creating process that return in JSON answer in global error parameter.<br>
 * In success case Servlet returns JSON answer with {@link java.lang.String}encodedPhotoId and {@link java.lang.Long}photoId.
 * <br>
 * Method GET use JSONP answer for cross domain call
 * <br>
 * Method POST use JSON answer
 *
 * @author rkapushchak
 * @version $Id: $Id
 */
public class ImportServlet extends BaseServlet {
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -3769188769374731369L;
	/**
	 * Photo preview settings.
	 */
	public static final String PARAM_PREVIEW_SETTINGS = "previewSettings";
	/**
	 * Photo preview settings.
	 */
	public static final String PARAM_PHOTO_UPLOAD_LINK = "pLink";
	/**
	 * {@link HttpServletRequest} parameter: user id
	 */
	public static final String PARAM_UPLOAD_USER_ID = "userId";
	/**
	 * {@link HttpServletRequest} parameter: user id
	 */
	public static final String PARAM_ALBUM_ID = "albumId";
	/**
	 * {@link HttpServletRequest} parameter: album name
	 */
	public static final String PARAM_ALBUM_NAME = "albumName";
	/**
	 * {@link HttpServletRequest} parameter: callback method name.
	 */
	public static final String PARAM_CALLBACK_METHOD_NAME = "callback";

	/**
	 * {@link PhotoUploadAPI} instance.
	 */
	private PhotoUploadAPI photoUploadAPI;
	/**
	 * {@link PhotoAPI} instance.
	 */
	private PhotoAPI photoAPI;

	/** {@inheritDoc} */
	@Override
	public void init(ServletConfig config) throws ServletException {
		photoUploadAPI = APIFinder.findAPI(PhotoUploadAPI.class);
		photoAPI = APIFinder.findAPI(PhotoAPI.class);
	}

	/** {@inheritDoc} */
	@Override
	protected void moskitoDoGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject result = new JSONObject();
			//get parameters
			final String userId = request.getParameter(PARAM_UPLOAD_USER_ID);
			final String albumId = request.getParameter(PARAM_ALBUM_ID);
			final String albumName = request.getParameter(PARAM_ALBUM_NAME);
			final String previewSettingString = request.getParameter(PARAM_PREVIEW_SETTINGS);
			final String uploadLink = request.getParameter(PARAM_PHOTO_UPLOAD_LINK);
			final String callBackMethodName = request.getParameter(PARAM_CALLBACK_METHOD_NAME);
			//validate parameters
			if (userId == null || userId.length() == 0){
				result.put("status", "ERROR");
				result.put("error", "Invalid user iId");
				writeResponseJSONPResponse(response, callBackMethodName, result.toString());
				return;
			}
			if (albumId == null || albumId.length() == 0){
				result.put("status", "ERROR");
				result.put("error", "Invalid album iId");
				writeResponseJSONPResponse(response, callBackMethodName, result.toString());
				return;
			}
			if (previewSettingString == null || previewSettingString.length() == 0){
				result.put("status", "ERROR");
				result.put("error", "Invalid photo settings");
				writeResponseJSONPResponse(response, callBackMethodName, result.toString());
				return;
			}
			if (uploadLink == null || uploadLink.length() == 0){
				result.put("status", "ERROR");
				result.put("error", "Invalid photo upload link");
				writeResponseJSONPResponse(response, callBackMethodName, result.toString());
				return;
			}
			if (callBackMethodName == null || callBackMethodName.length() == 0){
				result.put("status", "ERROR");
				result.put("error", "Empty callback method name.");
				writeResponseJSONPResponse(response, callBackMethodName, result.toString());
				return;
			}

			try {
				//create Photo preview setting
				JSONObject previewSettings = new JSONObject(previewSettingString);
				int x = previewSettings.getInt("x");
				int y = previewSettings.getInt("y");
				int height = previewSettings.getInt("height");
				int width = previewSettings.getInt("width");

				PreviewSettingsVO previewSettingsVO = new PreviewSettingsVO(x, y, width, height);
				request.setAttribute(PARAM_PHOTO_UPLOAD_LINK, uploadLink);

				//upload photo by URL
				PhotoUploader uploader = photoUploadAPI.createPhotoUploader(userId);
				uploader.doUpload(request);

				//check upload status
				if(uploader.getStatus().getStatus() < 0){
					result.put("status", "ERROR");
					result.put("uploadStatus", uploader.getStatus().getStatus());
					result.put("error", uploader.getStatus().toJSONString());
					writeResponseJSONPResponse(response, callBackMethodName, result.toString());
					return;
				}

				//get uploaded photo
				TempPhotoVO photo = uploader.getUploadedPhoto();

				//get album gor new photo
				AlbumAO albumAO = null;
				for (AlbumAO album : photoAPI.getAlbums(userId)) {
					if (album.getId() == Long.parseLong(albumId)) {
						albumAO = album;
						break;
					}

					if (album.getName() != null && album.getName().equals(albumName)) {
						albumAO = album;
						break;
					}
				}
				//create album if it doesn't exist
				if(albumAO == null){
					final AlbumAO newAlbum = new AlbumAO();
					newAlbum.setUserId(userId);
					newAlbum.setName(albumName);
					newAlbum.setId(Long.parseLong(albumId));

					albumAO = photoAPI.createAlbum(newAlbum, userId);
				}

				//save photo
				PhotoAO createdPhoto = photoAPI.createPhoto(userId, albumAO.getId(), photo.getFile(), previewSettingsVO);
				//return photo id's
				result.put("encodedPhotoId", createdPhoto.getEncodedId());
				result.put("photoId", createdPhoto.getId());
				result.put("status", "OK");
			} catch (JSONException e) {
				result.put("status", "ERROR");
				result.put("error", "Invalid photo preview settings.");
			} catch (APIException e) {
				result.put("status", "ERROR");
				result.put("error", e.getMessage());
			}

			writeResponseJSONPResponse(response, callBackMethodName, result.toString());
		} catch (JSONException e1) {
			throw new ServletException(e1.getMessage());
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void moskitoDoPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			JSONObject result = new JSONObject();
			//get parameters
			final String userId = request.getParameter(PARAM_UPLOAD_USER_ID);
			final String albumId = request.getParameter(PARAM_ALBUM_ID);
			final String albumName = request.getParameter(PARAM_ALBUM_NAME);
			final String previewSettingString = request.getParameter(PARAM_PREVIEW_SETTINGS);
			final String uploadLink = request.getParameter(PARAM_PHOTO_UPLOAD_LINK);
			//validate parameters
			if (userId == null || userId.length() == 0)
				throw new ServletException("Invalid user iId");
			if (albumId == null || albumId.length() == 0)
				throw new ServletException("Invalid user iId");
			if (previewSettingString == null || previewSettingString.length() == 0)
				throw new ServletException("Invalid photo settings");
			if (uploadLink == null || uploadLink.length() == 0)
				throw new ServletException("Invalid photo upload link");

			try {
				//create Photo preview setting
				JSONObject previewSettings = new JSONObject(previewSettingString);
				int x = previewSettings.getInt("x");
				int y = previewSettings.getInt("y");
				int height = previewSettings.getInt("height");
				int width = previewSettings.getInt("width");

				PreviewSettingsVO previewSettingsVO = new PreviewSettingsVO(x, y, width, height);
				request.setAttribute(PARAM_PHOTO_UPLOAD_LINK, uploadLink);

				//upload photo by URL
				PhotoUploader uploader = photoUploadAPI.createPhotoUploader(userId);
				uploader.doUpload(request);

				//check upload status
				if(uploader.getStatus().getStatus() < 0){
					result.put("status", "ERROR");
					result.put("uploadStatus", uploader.getStatus().getStatus());
					result.put("error", uploader.getStatus().toJSONString());
					return;
				}

				//get uploaded photo
				TempPhotoVO photo = uploader.getUploadedPhoto();

				//get album gor new photo
				AlbumAO albumAO = null;
				for (AlbumAO album : photoAPI.getAlbums(userId)) {
					if (album.getId() == Long.parseLong(albumId)) {
						albumAO = album;
						break;
					}

					if (album.getName() != null && album.getName().equals(albumName)) {
						albumAO = album;
						break;
					}
				}
				//create album if it doesn't exist
				if(albumAO == null){
					final AlbumAO newAlbum = new AlbumAO();
					newAlbum.setUserId(userId);
					newAlbum.setName(albumName);
					newAlbum.setId(Long.parseLong(albumId));

					albumAO = photoAPI.createAlbum(newAlbum, userId);
				}

				//save photo
				PhotoAO createdPhoto = photoAPI.createPhoto(userId, albumAO.getId(), photo.getFile(), previewSettingsVO);
				//return photo id's
				result.put("encodedPhotoId", createdPhoto.getEncodedId());
				result.put("photoId", createdPhoto.getId());
				result.put("status", "OK");
			} catch (JSONException e) {
				result.put("status", "ERROR");
				result.put("error", "Invalid photo preview settings.");
			} catch (APIException e) {
				result.put("status", "ERROR");
				result.put("error", e.getMessage());
			}

			writeResponse(response, result.toString());
		} catch (JSONException e1) {
			throw new ServletException(e1.getMessage());
		}
	}
}
