package net.anotheria.anosite.photoserver.presentation.actions;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.anotheria.anoplass.api.APIException;
import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import net.anotheria.anosite.photoserver.api.blur.AlbumIsBlurredAPIException;
import net.anotheria.anosite.photoserver.api.blur.AlbumIsNotBlurredAPIException;
import net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPI;
import net.anotheria.anosite.photoserver.api.blur.BlurSettingsAPIException;
import net.anotheria.anosite.photoserver.api.photo.AlbumAO;
import net.anotheria.anosite.photoserver.api.photo.AlbumNotFoundPhotoAPIException;
import net.anotheria.anosite.photoserver.api.photo.AlbumWithPhotosPhotoAPIException;
import net.anotheria.anosite.photoserver.api.photo.NoAccessPhotoAPIException;
import net.anotheria.anosite.photoserver.api.photo.PhotoAO;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPI;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPIException;
import net.anotheria.anosite.photoserver.api.photo.PhotoNotFoundPhotoAPIException;
import net.anotheria.anosite.photoserver.presentation.shared.BaseServlet;
import net.anotheria.anosite.photoserver.shared.IdCrypter;
import net.anotheria.maf.json.JSONResponse;
import net.anotheria.util.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet for managing photos, albums, etc.
 *
 * @author another
 * @version $Id: $Id
 */
public class ActionsServlet extends BaseServlet {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = 6412967542399590460L;

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ActionsServlet.class);

	/**
	 * {@link PhotoAPI} instance.
	 */
	private static final PhotoAPI photoAPI = APIFinder.findAPI(PhotoAPI.class);

	/**
	 * {@link LoginAPI} instance.
	 */
	private static final LoginAPI loginAPI = APIFinder.findAPI(LoginAPI.class);

	/**
	 * {@link BlurSettingsAPI} instance.
	 */
	private static final BlurSettingsAPI blurSettingsAPI = APIFinder.findAPI(BlurSettingsAPI.class);

	/**
	 * Request parameter with photo/album name.
	 */
	private static final String PARAM_NAME = "name";

	/**
	 * Request parameter with photo/album description.
	 */
	private static final String PARAM_DESCRIPTION = "description";

	/**
	 * Request parameter with album photos order.
	 */
	private static final String PARAM_ORDER = "order";

	/**
	 * Request parameter with user id.
	 */
	private static final String PARAM_USER_ID = "userId";

	/**
	 * Internal server error message.
	 */
	private static final String INTERNAL_SERVER_ERROR_MSG = "Internal server error";

	/** {@inheritDoc} */
	@Override
	protected void moskitoDoPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}

	/** {@inheritDoc} */
	@Override
	protected void moskitoDoGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		// checking and preventing anonymous access to servlet
		if (StringUtils.isEmpty(req.getParameter(PARAM_USER_ID)) && !loginAPI.isLogedIn()) {
			JSONResponse response = new JSONResponse();
			response.addError("Not Logged In");
			response.addCommand("redirect", "/Login.html");
			writeJSONResponse(resp, response);
			return;
		}

		String[] params = parsePathParameters(req);
		if (LOG.isDebugEnabled())
			LOG.debug(Arrays.asList(params).toString());

		// wrong url used
		if (params == null || params.length < 3) {
			JSONResponse response = new JSONResponse();
			response.addError("Wrong URL");
			writeJSONResponse(resp, response);
			return;
		}

		// processing action
		try {
			if ("photo".equalsIgnoreCase(params[0])) {
				handlePhotoAction(params[1], IdCrypter.decodeToLong(params[2]), req.getParameter(PARAM_USER_ID), req, resp);
				return;
			}
			if ("album".equalsIgnoreCase(params[0])) {
				handleAlbumAction(params[1], params[2], req.getParameter(PARAM_USER_ID), req, resp);
				return;
			}
		} catch (Exception e) {
			// internal error happened
			LOG.warn("doGet(req, resp) fail.", e);
			JSONResponse response = new JSONResponse();
			response.addError(INTERNAL_SERVER_ERROR_MSG);
			writeJSONResponse(resp, response);
		}

		// if nothing to done we sending forbidden response status
		resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
	}

	/**
	 * Method for handling action's on photos.
	 * 
	 * @param action
	 *            - action
	 * @param id
	 *            - id
	 * @param req
	 *            - request
	 * @param resp
	 *            - response
	 */
	private void handlePhotoAction(final String action, final long id, final String userId, final HttpServletRequest req, final HttpServletResponse resp) {
		JSONResponse response = new JSONResponse();
		switch (PhotoAction.getAction(action)) {
		case LIST:
			// http://server/photos/a/photo/list/[id] - id is a photo id
			listPhoto(id, response);
			break;
		case LISTALL:
			// http://server/photos/a/photo/listall/[id] - id is a album id
			listPhotos(id, response);
			break;
		case UPDATE:
			// http://server/photos/a/photo/update/[id]/?name=XYZ&description=XYZ&userId=1...
			updatePhoto(id, userId, response, req);
			break;
		case REMOVE:
			// http://server/photos/a/photo/remove/[id]/?userId=1...
			removePhoto(id, userId, response);
			break;
		default:
			LOG.warn("handlePhotoAction(" + action + ", " + id + ", req, resp) Unsupported EntityAction called.");
			response.addError("Unsupported EntityAction called");
		}

		writeJSONResponse(resp, response);
	}

	/**
	 * List album photos data.
	 * 
	 * @param albumId
	 *            - album id
	 * @param response
	 *            - response with photo data
	 */
	private void listPhotos(final long albumId, final JSONResponse response) {
		try {
			List<PhotoAO> userPhotos = photoAPI.getPhotos(albumId);

			JSONObject photos = new JSONObject();
			for (PhotoAO photo : userPhotos)
				photos.put(IdCrypter.encode(photo.getId()), photoToJSON(photo));

			JSONObject result = new JSONObject();
			result.put("photos", photos);

			response.setData(result);
		} catch (NoAccessPhotoAPIException e) {
			response.addError("No access.");
		} catch (AlbumNotFoundPhotoAPIException e) {
			response.addError("Album with id[" + IdCrypter.encode(albumId) + "] not found.");
		} catch (PhotoAPIException e) {
			LOG.warn("listPhotos(" + albumId + ", " + response + ") fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		} catch (JSONException e) {
			LOG.warn("listPhotos(" + albumId + ", " + response + ") fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		}
	}

	/**
	 * List photo data.
	 * 
	 * @param photoId
	 *            - photo id
	 * @param response
	 *            - response with photo data
	 */
	private void listPhoto(final long photoId, final JSONResponse response) {
		try {
			PhotoAO photo = photoAPI.getPhoto(photoId);
			response.setData(photoToJSON(photo));
		} catch (NoAccessPhotoAPIException e) {
			response.addError("No access.");
		} catch (PhotoNotFoundPhotoAPIException e) {
			response.addError("Photo with id[" + IdCrypter.encode(photoId) + "] not found.");
		} catch (PhotoAPIException e) {
			LOG.warn("listPhoto(" + photoId + ", " + response + ") fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		} catch (JSONException e) {
			LOG.warn("listPhoto(" + photoId + ", " + response + ") fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		}
	}

	/**
	 * Map {@link PhotoAO} to {@link JSONObject}.
	 * 
	 * @param photo
	 *            - {@link PhotoAO}
	 * @return {@link JSONObject}
	 * @throws JSONException
	 */
	private JSONObject photoToJSON(PhotoAO photo) throws JSONException {
		JSONObject photoObj = new JSONObject();
		photoObj.put("id", photo.getEncodedId());
		photoObj.put("name", photo.getName());
		photoObj.put("description", photo.getDescription());
		photoObj.put("isBlurred", photo.isBlurred());

		return photoObj;
	}

	/**
	 * Update photo data.
	 * 
	 * @param photoId
	 *            - photo id
	 * @param userId
	 *            - user id
	 * @param response
	 *            - response with photo data
	 * @param req
	 *            - request
	 */
	private void updatePhoto(final long photoId, final String userId, final JSONResponse response, final HttpServletRequest req) {
		try {
			String name = req.getParameter(PARAM_NAME);
			String description = req.getParameter(PARAM_DESCRIPTION);
			if (StringUtils.isEmpty(name) && StringUtils.isEmpty(description))
				return;
			PhotoAO photo = photoAPI.getPhoto(photoId);
			if (!StringUtils.isEmpty(name))
				photo.setName(name);
			if (!StringUtils.isEmpty(description))
				photo.setDescription(description);

			if (StringUtils.isEmpty(userId))
				photoAPI.updatePhoto(photo);
			else
				photoAPI.updatePhoto(userId, photo);
		} catch (NoAccessPhotoAPIException e) {
			response.addError("No access.");
		} catch (PhotoNotFoundPhotoAPIException e) {
			response.addError("Photo with id[" + IdCrypter.encode(photoId) + "] not found.");
		} catch (PhotoAPIException e) {
			LOG.warn("updatePhoto(" + photoId + ", " + response + ", req) fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		}
	}

	/**
	 * Remove photo.
	 * 
	 * @param photoId
	 *            - photo id
	 * @param userId
	 *            - user id
	 * @return {@link JSONResponse}
	 */
	private void removePhoto(final long photoId, final String userId, final JSONResponse response) {
		try {
			if (StringUtils.isEmpty(userId))
				photoAPI.removePhoto(photoId);
			else
				photoAPI.removePhoto(userId, photoId);
		} catch (NoAccessPhotoAPIException e) {
			response.addError("No access.");
		} catch (PhotoNotFoundPhotoAPIException e) {
			response.addError("Photo with id[" + IdCrypter.encode(photoId) + "] not found.");
		} catch (PhotoAPIException e) {
			LOG.warn("removePhoto(" + photoId + ", " + response + ") fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		}
	}

	/**
	 * Method for handling action's on album's.
	 * 
	 * @param action
	 *            - action
	 * @param id
	 *            - id
	 * @param userId
	 *            - user id
	 * @param req
	 *            - request
	 * @param resp
	 *            - response
	 */
	private void handleAlbumAction(final String action, final String id, final String userId, final HttpServletRequest req, final HttpServletResponse resp) {
		JSONResponse response = new JSONResponse();
		switch (AlbumAction.getAction(action)) {
		case LIST:
			// http://server/photos/a/album/list/[id] - id is album id
			listAlbum(IdCrypter.decodeToLong(id), response);
			break;
		case LISTALL:
			// http://server/photos/a/album/listall/[id] - id is encoded user id
			listAlbums(IdCrypter.decodeToString(id), response);
			break;
		case UPDATE:
			// http://server/photos/a/album/update/[id]/?name=XYZ&description=XYZ&...
			updateAlbum(IdCrypter.decodeToLong(id), response, req);
			break;
		case SETORDER:
			// http://server/photos/a/album/setorder/[id]/?order=[id0,id...,idN]
			updateAlbumPhotoOrder(IdCrypter.decodeToLong(id), response, req);
			break;
		case BLUR:
			// http://server/photos/a/album/blur/[id]
			blurAlbumToAll(IdCrypter.decodeToLong(id), response, req);
			break;
		case UNBLUR:
			// http://server/photos/a/album/unblur/[id]
			unblurAlbumToAll(IdCrypter.decodeToLong(id), response, req);
			break;
		case REMOVE:
			// http://server/photos/a/album/remove/[id]
			removeAlbum(IdCrypter.decodeToLong(id), response);
			break;
		default:
			LOG.warn("handleAlbumAction(" + action + ", " + id + ", req, resp) Unsupported EntityAction called.");
			response.addError("Unsupported EntityAction called");
		}

		writeJSONResponse(resp, response);
	}

	/**
	 * Blur album to all user's.
	 * 
	 * @param id
	 *            - decoded album id
	 * @param response
	 *            - {@link JSONResponse}
	 * @param req
	 *            - request
	 */
	private void blurAlbumToAll(long id, JSONResponse response, HttpServletRequest req) {
		try {
			AlbumAO album = photoAPI.getAlbum(id);
			if (!loginAPI.getLogedUserId().equalsIgnoreCase(String.valueOf(album.getUserId()))) {
				response.addError("No access to blur not own album[" + IdCrypter.encode(id) + "].");
				return;
			}

			blurSettingsAPI.blurAlbum(album.getId());
		} catch (AlbumNotFoundPhotoAPIException e) {
			response.addError("Album with id[" + IdCrypter.encode(id) + "] not found.");
		} catch (AlbumIsBlurredAPIException e) {
			response.addError("Album with id[" + IdCrypter.encode(id) + "] already blurred.");
		} catch (BlurSettingsAPIException e) {
			LOG.warn("blurAlbumToAll(" + id + ", " + response + ", req) fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		} catch (PhotoAPIException e) {
			LOG.warn("blurAlbumToAll(" + id + ", " + response + ", req) fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		} catch (APIException e) {
			LOG.warn("blurAlbumToAll(" + id + ", " + response + ", req) fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		}
	}

	/**
	 * Unblur album to all user's.
	 * 
	 * @param id
	 *            - decoded album id
	 * @param response
	 *            - {@link JSONResponse}
	 * @param req
	 *            - request
	 */
	private void unblurAlbumToAll(long id, JSONResponse response, HttpServletRequest req) {
		try {
			AlbumAO album = photoAPI.getAlbum(id);
			if (!loginAPI.getLogedUserId().equalsIgnoreCase(String.valueOf(album.getUserId()))) {
				response.addError("No access to unblur not own album[" + IdCrypter.encode(id) + "].");
				return;
			}

			blurSettingsAPI.unBlurAlbum(album.getId());
		} catch (AlbumNotFoundPhotoAPIException e) {
			response.addError("Album with id[" + IdCrypter.encode(id) + "] not found.");
		} catch (AlbumIsNotBlurredAPIException e) {
			response.addError("Album with id[" + IdCrypter.encode(id) + "] not blurred.");
		} catch (BlurSettingsAPIException e) {
			LOG.warn("unblurAlbumToAll(" + id + ", " + response + ", req) fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		} catch (PhotoAPIException e) {
			LOG.warn("unblurAlbumToAll(" + id + ", " + response + ", req) fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		} catch (APIException e) {
			LOG.warn("blurAlbumToAll(" + id + ", " + response + ", req) fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		}
	}

	/**
	 * Update album data.
	 * 
	 * @param albumId
	 *            - album id
	 * @param response
	 *            - response with album data
	 * @param req
	 *            - request
	 */
	private void updateAlbum(final long albumId, final JSONResponse response, final HttpServletRequest req) {
		try {
			AlbumAO album = photoAPI.getAlbum(albumId);
			album.setName(req.getParameter(PARAM_NAME));
			album.setDescription(req.getParameter(PARAM_DESCRIPTION));

			photoAPI.updateAlbum(album);
		} catch (NoAccessPhotoAPIException e) {
			response.addError("No access.");
		} catch (AlbumNotFoundPhotoAPIException e) {
			response.addError("Album with id[" + IdCrypter.encode(albumId) + "] not found.");
		} catch (PhotoAPIException e) {
			LOG.warn("updateAlbum(" + albumId + ", " + response + ", req) fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		}
	}

	/**
	 * Update album photos order.
	 * 
	 * @param albumId
	 *            - album id
	 * @param response
	 *            - response with album data
	 * @param req
	 *            - request
	 */
	private void updateAlbumPhotoOrder(final long albumId, final JSONResponse response, final HttpServletRequest req) {
		String idsParam = req.getParameter(PARAM_ORDER);
		if (StringUtils.isEmpty(idsParam)) {
			response.addError("Wrong photos order");
			return;
		}

		List<Long> photosIds = new ArrayList<Long>();
		String[] ids = StringUtils.tokenize(idsParam, true, ',');
		try {
			for (String id : ids)
				photosIds.add(IdCrypter.decodeToLong(id));
		} catch (RuntimeException e) {
			response.addError("Wrong photos ids in order");
		}

		try {
			AlbumAO album = photoAPI.getAlbum(albumId);
			album.setPhotosOrder(photosIds);
			photoAPI.updateAlbum(album);
		} catch (NoAccessPhotoAPIException e) {
			response.addError("No access.");
		} catch (AlbumNotFoundPhotoAPIException e) {
			response.addError("Album with id[" + IdCrypter.encode(albumId) + "] not found.");
		} catch (PhotoAPIException e) {
			LOG.warn("updateAlbumPhotoOrder(" + albumId + ", " + response + ", req) fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		}
	}

	/**
	 * List all user album's.
	 * 
	 * @param userId
	 *            - decoded user id
	 * @param response
	 *            - response with album's data
	 */
	private void listAlbums(String userId, JSONResponse response) {
		try {
			List<AlbumAO> userAlbums = photoAPI.getAlbums(userId);

			JSONObject albums = new JSONObject();
			for (AlbumAO album : userAlbums)
				albums.put(IdCrypter.encode(album.getId()), albumToJSON(album));

			JSONObject result = new JSONObject();
			result.put("albums", albums);

			response.setData(result);
		} catch (NoAccessPhotoAPIException e) {
			response.addError("No access.");
		} catch (PhotoAPIException e) {
			LOG.warn("listAlbums(" + userId + ", " + response + ") fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		} catch (JSONException e) {
			LOG.warn("listAlbums(" + userId + ", " + response + ") fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		}

	}

	/**
	 * List photo album data.
	 * 
	 * @param albumId
	 *            - album id
	 * @param response
	 *            - response with album data
	 */
	private void listAlbum(final long albumId, final JSONResponse response) {
		try {
			AlbumAO album = photoAPI.getAlbum(albumId);
			JSONObject albumJSON = new JSONObject();
			albumJSON.put("album", albumToJSON(album));
			response.setData(albumJSON);
		} catch (NoAccessPhotoAPIException e) {
			response.addError("No access.");
		} catch (AlbumNotFoundPhotoAPIException e) {
			response.addError("Album with id[" + IdCrypter.encode(albumId) + "] not found.");
		} catch (PhotoAPIException e) {
			LOG.warn("listAlbum(" + albumId + ", " + response + ") fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		} catch (JSONException e) {
			LOG.warn("listAlbum(" + albumId + ", " + response + ") fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		}
	}

	/**
	 * Map {@link AlbumAO} to {@link JSONObject}.
	 * 
	 * @param album
	 *            - {@link AlbumAO}
	 * @return {@link JSONObject}
	 * @throws JSONException
	 */
	private JSONObject albumToJSON(AlbumAO album) throws JSONException {
		JSONObject albumJSON = new JSONObject();
		albumJSON.put("id", album.getEncodedId());
		albumJSON.put("userId", IdCrypter.encode(album.getUserId()));
		albumJSON.put("name", album.getName());
		albumJSON.put("description", album.getDescription());
		albumJSON.put("photosOrder", new JSONArray(album.getPhotosOrder()));

		return albumJSON;
	}

	/**
	 * Remove album.
	 * 
	 * @param albumId
	 *            - album id
	 * @return {@link JSONResponse}
	 */
	private void removeAlbum(final long albumId, final JSONResponse response) {
		try {
			photoAPI.removeAlbum(albumId);
		} catch (NoAccessPhotoAPIException e) {
			response.addError("No access.");
		} catch (AlbumNotFoundPhotoAPIException e) {
			response.addError("Album with id[" + IdCrypter.encode(albumId) + "] not found.");
		} catch (AlbumWithPhotosPhotoAPIException e) {
			response.addError("Album with id[" + IdCrypter.encode(albumId) + "] contain photos.");
		} catch (PhotoAPIException e) {
			LOG.warn("removeAlbum(" + albumId + ", " + response + ") fail.", e);
			response.addError(INTERNAL_SERVER_ERROR_MSG);
		}
	}

	/**
	 * Writes {@link JSONResponse} to response and flushes the stream.
	 * 
	 * @param response
	 *            - {@link HttpServletRequest}
	 * @param jsonResponse
	 *            - {@link JSONResponse}
	 */
	private void writeJSONResponse(final HttpServletResponse response, final JSONResponse jsonResponse) {
		try {
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Cacheable", "false");
			response.setDateHeader("Last-Modified", System.currentTimeMillis());
			response.setDateHeader("Expires", 0);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("text/x-json");

			PrintWriter writer = response.getWriter();
			response.setStatus(HttpServletResponse.SC_OK);

			writer.write(jsonResponse.toString());
			writer.flush();
		} catch (IOException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}
}
