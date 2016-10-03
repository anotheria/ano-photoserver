package net.anotheria.anosite.photoserver.api.blur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.anotheria.anoplass.api.APIFinder;
import net.anotheria.anoplass.api.APIInitException;
import net.anotheria.anoplass.api.AbstractAPIImpl;
import net.anotheria.anoplass.api.NoLoggedInUserException;
import net.anotheria.anoplass.api.generic.login.LoginAPI;
import net.anotheria.anoprise.metafactory.MetaFactory;
import net.anotheria.anoprise.metafactory.MetaFactoryException;
import net.anotheria.anosite.photoserver.api.photo.AlbumAO;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPI;
import net.anotheria.anosite.photoserver.api.photo.PhotoAPIException;
import net.anotheria.anosite.photoserver.service.blur.AlbumIsBlurredException;
import net.anotheria.anosite.photoserver.service.blur.AlbumIsNotBlurredException;
import net.anotheria.anosite.photoserver.service.blur.BlurSettingBO;
import net.anotheria.anosite.photoserver.service.blur.BlurSettingsService;
import net.anotheria.anosite.photoserver.service.blur.BlurSettingsServiceException;
import net.anotheria.anosite.photoserver.service.blur.PictureIsBlurredException;
import net.anotheria.anosite.photoserver.service.blur.PictureIsNotBlurredException;
import net.anotheria.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BlurSettingsAPI implementation.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class BlurSettingsAPIImpl extends AbstractAPIImpl implements BlurSettingsAPI {

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(BlurSettingsAPIImpl.class);
	/**
	 * {@link BlurSettingsService} backend service instance.
	 */
	private BlurSettingsService blurSettingsService;

	/**
	 * {@link LoginAPI} instance.
	 */
	private LoginAPI loginAPI;

	/**
	 * {@link PhotoAPI} instance.
	 */
	private PhotoAPI photoAPI;

	/** {@inheritDoc} */
	@Override
	public void init() throws APIInitException {
		super.init();
		try {
			blurSettingsService = MetaFactory.get(BlurSettingsService.class);
		} catch (MetaFactoryException e) {
			LOG.error("BlurSettingsAPIImpl init failure.", e);
			throw new APIInitException("BlurSettingsAPIImpl init failure.", e);
		}
		loginAPI = APIFinder.findAPI(LoginAPI.class);
		photoAPI = APIFinder.findAPI(PhotoAPI.class);
	}

	/** {@inheritDoc} */
	@Override
	public Boolean readMyBlurSettings(long albumId, long pictureId) throws BlurSettingsAPIException {

		List<Long> pictureCollection = new ArrayList<Long>();
		pictureCollection.add(pictureId);

		Map<Long, Boolean> map = readMyBlurSettings(albumId, pictureCollection);
		return map.get(pictureId) != null ? map.get(pictureId) : false;
	}

	/** {@inheritDoc} */
	@Override
	public Map<Long, Boolean> readMyBlurSettings(long albumId, List<Long> pictureIds) throws BlurSettingsAPIException {

		if (albumId <= 0)
			throw new IllegalArgumentException("AlbumId is not valid");
		if (pictureIds == null || pictureIds.isEmpty())
			throw new IllegalArgumentException("PictureId is not valid");

		Map<Long, Boolean> result = new HashMap<Long, Boolean>();
		Map<Long, BlurSettingBO> map;
		try {
			// User is logged IN case!
			if (loginAPI.isLogedIn()) {
				String myId = getMyUserId();
				try {
					AlbumAO album = photoAPI.getAlbum(albumId);
					// Show all unBlured for My album!!!\
					if (myId.equals(album.getUserId())) {
						LOG.debug("viewing self settings!");
						for (Long pictureId : pictureIds)
							result.put(pictureId, false);
						return result;
					}
				} catch (PhotoAPIException e) {
					// Skipping! will try to fetch all data from backEnd!
					LOG.warn("error fetching Album{" + albumId + "}", e);
				}
				map = blurSettingsService.readBlurSettings(albumId, pictureIds, myId);
			} else
				// not logged in
				map = blurSettingsService.readBlurSettings(albumId, pictureIds);

			for (Map.Entry<Long, BlurSettingBO> setting : map.entrySet())
				result.put(setting.getKey(), setting.getValue().isBlurred());

			return result;
		} catch (BlurSettingsServiceException e) {
			LOG.error("readMyBlurSettings(" + albumId + ", [" + pictureIds.size() + "])", e);
			throw new BlurSettingsAPIException("Backend failure", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void blurAlbum(long albumId) throws BlurSettingsAPIException {
		if (albumId <= 0)
			throw new IllegalArgumentException("AlbumId is not valid");

		try {

			if (!loginAPI.isLogedIn())
				throw new BlurSettingsAPIException("User is not logged in");

			blurSettingsService.blurAlbum(albumId);
		} catch (AlbumIsBlurredException e) {
			throw new AlbumIsBlurredAPIException(albumId, e);
		} catch (BlurSettingsServiceException e) {
			LOG.error("blurAlbum(" + albumId + ")", e);
			throw new BlurSettingsAPIException("Backend failure", e);
		}

	}

	/** {@inheritDoc} */
	@Override
	public void blurAlbum(long albumId, String userId) throws BlurSettingsAPIException {
		if (albumId <= 0)
			throw new IllegalArgumentException("AlbumId is not valid");
		if (StringUtils.isEmpty(userId))
			throw new IllegalArgumentException("UserId is not valid");

		try {

			if (getMyUserId().equals(userId)) {
				LOG.debug("Illegal try  of album blur for self");
				throw new BlurSettingsAPIException("Album [" + albumId + "] can't be blurred for User[" + userId + "], cause it belongs to User[" + userId
						+ "].");
			}

			blurSettingsService.blurAlbum(albumId, userId);
		} catch (AlbumIsBlurredException e) {
			throw new AlbumIsBlurredAPIException(albumId, userId, e);
		} catch (BlurSettingsServiceException e) {
			LOG.error("blurAlbum(" + albumId + ", " + userId + ")", e);
			throw new BlurSettingsAPIException("Backend failure", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void blurPicture(long albumId, long pictureId, String userId) throws BlurSettingsAPIException {
		if (albumId <= 0)
			throw new IllegalArgumentException("AlbumId is not valid");
		if (pictureId <= 0)
			throw new IllegalArgumentException("PictureId is not valid");
		if (StringUtils.isEmpty(userId))
			throw new IllegalArgumentException("UserId is not valid");

		try {

			if (getMyUserId().equals(userId)) {
				LOG.debug("Illegal try  of picture blur for self");
				throw new BlurSettingsAPIException("Picture [" + pictureId + "] from album[" + albumId + "] can't be blurred for User[" + userId + "], "
						+ "cause it belongs to User[" + userId + "].");
			}

			blurSettingsService.blurPicture(albumId, pictureId, userId);
		} catch (PictureIsBlurredException e) {
			throw new PictureIsBlurredAPIException(albumId, pictureId, userId, e);
		} catch (BlurSettingsServiceException e) {
			LOG.error("blurPicture(" + albumId + ", " + pictureId + ", " + userId + ")", e);
			throw new BlurSettingsAPIException("Backend failure", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void blurPicture(long albumId, long pictureId) throws BlurSettingsAPIException {
		if (albumId <= 0)
			throw new IllegalArgumentException("AlbumId is not valid");
		if (pictureId <= 0)
			throw new IllegalArgumentException("PictureId is not valid");

		try {

			if (!loginAPI.isLogedIn())
				throw new BlurSettingsAPIException("User is not logged in");

			blurSettingsService.blurPicture(albumId, pictureId);
		} catch (PictureIsBlurredException e) {
			throw new PictureIsBlurredAPIException(albumId, pictureId, e);
		} catch (BlurSettingsServiceException e) {
			LOG.error("blurPicture(" + albumId + ", " + pictureId + ")", e);
			throw new BlurSettingsAPIException("Backend failure", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void unBlurAlbum(long albumId) throws BlurSettingsAPIException {
		if (albumId <= 0)
			throw new IllegalArgumentException("AlbumId is not valid");
		try {

			if (!loginAPI.isLogedIn())
				throw new BlurSettingsAPIException("User is not logged in");

			blurSettingsService.unBlurAlbum(albumId);
		} catch (AlbumIsNotBlurredException e) {
			throw new AlbumIsNotBlurredAPIException(albumId, e);
		} catch (BlurSettingsServiceException e) {
			LOG.error("unBlurAlbum(" + albumId + ")", e);
			throw new BlurSettingsAPIException("Backend failure", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void unBlurAlbum(long albumId, String userId) throws BlurSettingsAPIException {
		if (albumId <= 0)
			throw new IllegalArgumentException("AlbumId is not valid");
		if (StringUtils.isEmpty(userId))
			throw new IllegalArgumentException("UserId is not valid");

		try {

			if (getMyUserId().equals(userId)) {
				LOG.debug("Illegal try  of album unBlur for self");
				throw new BlurSettingsAPIException("Album [" + albumId + "] can't be unBlurred for User[" + userId + "], " + "cause it belongs to User["
						+ userId + "].");
			}

			blurSettingsService.unBlurAlbum(albumId, userId);
		} catch (AlbumIsNotBlurredException e) {
			throw new AlbumIsNotBlurredAPIException(albumId, userId, e);
		} catch (BlurSettingsServiceException e) {
			LOG.error("unBlurAlbum(" + albumId + ", " + userId + ")", e);
			throw new BlurSettingsAPIException("Backend failure", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void unBlurPicture(long albumId, long pictureId, String userId) throws BlurSettingsAPIException {
		if (albumId <= 0)
			throw new IllegalArgumentException("AlbumId is not valid");
		if (pictureId <= 0)
			throw new IllegalArgumentException("PictureId is not valid");
		if (StringUtils.isEmpty(userId))
			throw new IllegalArgumentException("UserId is not valid");
		try {
			if (getMyUserId().equals(userId)) {
				LOG.debug("Illegal try  of picture unBlur for self");
				throw new BlurSettingsAPIException("Picture [" + pictureId + "] from album[" + albumId + "] can't be unBlurred for User[" + userId + "], "
						+ "cause it belongs to User[" + userId + "].");
			}
			blurSettingsService.unBlurPicture(albumId, pictureId, userId);

		} catch (PictureIsNotBlurredException e) {
			throw new PictureIsNotBlurredAPIException(albumId, pictureId, userId, e);
		} catch (BlurSettingsServiceException e) {
			LOG.error("unBlurPicture(" + albumId + ", " + pictureId + ", " + userId + ")", e);
			throw new BlurSettingsAPIException("Backend failure", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void unBlurPicture(long albumId, long pictureId) throws BlurSettingsAPIException {
		if (albumId <= 0)
			throw new IllegalArgumentException("AlbumId is not valid");
		if (pictureId <= 0)
			throw new IllegalArgumentException("PictureId is not valid");
		try {
			if (!loginAPI.isLogedIn())
				throw new BlurSettingsAPIException("User is not logged in");
			blurSettingsService.unBlurPicture(albumId, pictureId);

		} catch (PictureIsNotBlurredException e) {
			throw new PictureIsNotBlurredAPIException(albumId, pictureId, e);
		} catch (BlurSettingsServiceException e) {
			LOG.error("unBlurPicture(" + albumId + ", " + pictureId + ")", e);
			throw new BlurSettingsAPIException("Backend failure", e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void removeBlurSettings(long albumId) throws BlurSettingsAPIException {
		if (albumId <= 0)
			throw new IllegalArgumentException("AlbumId is not valid");
		try {
			if (!loginAPI.isLogedIn())
				throw new BlurSettingsAPIException("User is not logged in");
			blurSettingsService.removeBlurSettings(albumId);

		} catch (BlurSettingsServiceException e) {
			LOG.error("removeBlurSettings(" + albumId + ")", e);
			throw new BlurSettingsAPIException("Backend failure", e);
		}
	}

	/**
	 * Get currently logged in user id.
	 * 
	 * @return id of currently logged in user
	 * @throws BlurSettingsAPIException
	 *             when user not logged in
	 */
	private String getMyUserId() throws BlurSettingsAPIException {
		try {
			return getLoggedInUserId();
		} catch (NoLoggedInUserException e) {
			throw new BlurSettingsAPIException("User not logged in", e);
		}
	}
}
