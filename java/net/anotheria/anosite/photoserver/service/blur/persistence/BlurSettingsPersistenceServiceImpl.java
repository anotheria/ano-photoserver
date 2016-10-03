package net.anotheria.anosite.photoserver.service.blur.persistence;

import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.FIELD_NAME_ALBUM_ID;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.FIELD_NAME_BLUR;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.FIELD_NAME_PHOTO_ID;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.FIELD_NAME_USER_ID;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.SQL_CREATE;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.SQL_DELETE_BY_ALBUM_AND_PHOTO_AND_USER_ID;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.SQL_DELETE_BY_ALBUM_AND_USER_ID;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.SQL_DELETE_BY_ALBUM_ID;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.SQL_META_ADD_RIGHTS;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.SQL_META_CREATE_TABLE;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.SQL_READ_ALBUM_BLURRED_FOR_USER;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.SQL_READ_PICTURE_BLURRED_FOR_USER;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.SQL_READ_SETTING;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.SQL_READ_SETTINGS_WITH_ORDER;
import static net.anotheria.anosite.photoserver.service.blur.persistence.BlurSettingsPersistenceServiceConstants.TABLE_NAME;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.anotheria.anosite.photoserver.service.blur.BlurSettingBO;
import net.anotheria.db.service.GenericPersistenceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BlurSettingsPersistenceService implementation.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class BlurSettingsPersistenceServiceImpl extends GenericPersistenceService implements BlurSettingsPersistenceService {

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BlurSettingsPersistenceServiceImpl.class);
	/**
	 * DDL queries list.
	 */
	private final List<String> ddlQueries = new ArrayList<String>();
	/**
	 * Default - All album pictures ID.
	 */
	protected static final long FOR_ALL_PICTURES_IN_ALBUM = BlurSettingBO.ALL_ALBUM_PICTURES_DEFAULT_CONSTANT;
	/**
	 * Default all users ID.
	 */
	protected static final String FOR_ALL_USERS = BlurSettingBO.ALL_USERS_DEFAULT_CONSTANT;

	/**
	 * Constructor.
	 */
	protected BlurSettingsPersistenceServiceImpl() {
		ddlQueries.add(SQL_META_CREATE_TABLE);
		ddlQueries.add(SQL_META_ADD_RIGHTS);

		initialize();

	}

	/** {@inheritDoc} */
	@Override
	public BlurSettingBO readBlurSetting(long albumId, long pictureId, String userId) throws BlurSettingsPersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(SQL_READ_SETTINGS_WITH_ORDER);
			st.setLong(1, albumId);
			st.setLong(2, pictureId);
			st.setLong(3, FOR_ALL_PICTURES_IN_ALBUM);// default picture (for all)
			st.setString(4, userId);
			st.setString(5, FOR_ALL_USERS); ;//default user (for all)

			rs = st.executeQuery();
			BlurSettingBO result = null;

			if (rs.next()) {
				result = new BlurSettingBO();
				result.setAlbumId(rs.getLong(FIELD_NAME_ALBUM_ID));
				result.setPictureId(rs.getLong(FIELD_NAME_PHOTO_ID));
				result.setUserId(rs.getString(FIELD_NAME_USER_ID));
				result.setBlurred(rs.getBoolean(FIELD_NAME_BLUR));
				//just  for debug
				LOGGER.debug(result.toString());
			}
			//debug section.
			while (rs.next()) {
				BlurSettingBO debug = new BlurSettingBO();
				debug.setAlbumId(rs.getLong(FIELD_NAME_ALBUM_ID));
				debug.setPictureId(rs.getLong(FIELD_NAME_PHOTO_ID));
				debug.setUserId(rs.getString(FIELD_NAME_USER_ID));
				debug.setBlurred(rs.getBoolean(FIELD_NAME_BLUR));
				//just  for debug
				LOGGER.debug(result.toString());
			}
			return result;

		} catch (SQLException e) {
			LOGGER.error("readBlurSetting(" + albumId + "," + pictureId + "," + userId + ")", e);
			throw new BlurSettingsPersistenceServiceException("Persistence failed.", e);

		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void blurAlbum(long albumId) throws BlurSettingsPersistenceServiceException {
		//check
		BlurSettingBO settings = readAlbumSetting(albumId);
		if (settings != null && settings.isBlurred())
			throw new AlbumIsBlurredPersistenceException(albumId);


		Connection conn = null;
		PreparedStatement stDel = null;
		PreparedStatement stCr = null;
		try {
			conn = getConnection();
			stDel = conn.prepareStatement(SQL_DELETE_BY_ALBUM_ID);
			stDel.setLong(1, albumId);
			stDel.executeUpdate();

			//creating new record!
			stCr = conn.prepareStatement(SQL_CREATE);
			stCr.setLong(1, albumId);
			stCr.setLong(2, FOR_ALL_PICTURES_IN_ALBUM);
			stCr.setString(3, FOR_ALL_USERS);
			stCr.setBoolean(4, true);

			stCr.executeUpdate();

		} catch (SQLException e) {
			LOGGER.error("isAlbumBlurred(" + albumId + ")", e);
			throw new BlurSettingsPersistenceServiceException("Persistence failed.", e);
		} finally {
			close(stDel);
			close(stCr);
			close(conn);
		}
	}


	/** {@inheritDoc} */
	@Override
	public void blurAlbum(long albumId, String userId) throws BlurSettingsPersistenceServiceException {
		//check
		BlurSettingBO settings = readAlbumSetting(albumId, userId);
		if (settings != null && settings.isBlurred())
			throw new AlbumIsBlurredPersistenceException(albumId, userId);

		Connection conn = null;
		PreparedStatement stDel = null;
		PreparedStatement stCr = null;
		try {
			conn = getConnection();
			stDel = conn.prepareStatement(SQL_DELETE_BY_ALBUM_AND_USER_ID);
			stDel.setLong(1, albumId);
			stDel.setString(2, userId);
			stDel.executeUpdate();

			//creating new record!
			stCr = conn.prepareStatement(SQL_CREATE);
			stCr.setLong(1, albumId);
			stCr.setLong(2, FOR_ALL_PICTURES_IN_ALBUM);
			stCr.setString(3, userId);
			stCr.setBoolean(4, true);

			stCr.executeUpdate();

		} catch (SQLException e) {
			LOGGER.error("blurAlbum(" + albumId + ", " + userId + ")", e);
			throw new BlurSettingsPersistenceServiceException("Persistence failed.", e);
		} finally {
			close(stDel);
			close(stCr);
			close(conn);
		}
	}


	/** {@inheritDoc} */
	@Override
	public void unBlurAlbum(long albumId) throws BlurSettingsPersistenceServiceException {
		//check
		BlurSettingBO setting = readAlbumSetting(albumId);
		if (setting == null || !setting.isBlurred())
			throw new AlbumIsNotBlurredPersistenceException(albumId);

		Connection conn = null;
		PreparedStatement stDel = null;
		PreparedStatement stCR = null;
		try {
			conn = getConnection();
			stDel = conn.prepareStatement(SQL_DELETE_BY_ALBUM_ID);
			stDel.setLong(1, albumId);
			stDel.executeUpdate();

			stCR = conn.prepareStatement(SQL_CREATE);
			stCR.setLong(1, albumId);
			stCR.setLong(2, FOR_ALL_PICTURES_IN_ALBUM);
			stCR.setString(3, FOR_ALL_USERS);
			stCR.setBoolean(4, false);

			stCR.executeUpdate();


		} catch (SQLException e) {
			LOGGER.error("unBlurAlbum(" + albumId + ")", e);
			throw new BlurSettingsPersistenceServiceException("Persistence failed.", e);
		} finally {
			close(stDel);
			close(stCR);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void unBlurAlbum(long albumId, String userId) throws BlurSettingsPersistenceServiceException {

		//checking that is blurred!
		BlurSettingBO setting = readAlbumSetting(albumId, userId);
		if (setting == null || !setting.isBlurred())
			throw new AlbumIsNotBlurredPersistenceException(albumId);


		Connection conn = null;
		PreparedStatement stDel = null;
		PreparedStatement stCR = null;
		try {
			conn = getConnection();
			stDel = conn.prepareStatement(SQL_DELETE_BY_ALBUM_AND_USER_ID);
			stDel.setLong(1, albumId);
			stDel.setString(2, userId);
			stDel.executeUpdate();


			stCR = conn.prepareStatement(SQL_CREATE);
			stCR.setLong(1, albumId);
			stCR.setLong(2, FOR_ALL_PICTURES_IN_ALBUM);
			stCR.setString(3, userId);
			stCR.setBoolean(4, false);

			stCR.executeUpdate();

		} catch (SQLException e) {
			LOGGER.error("unBlurAlbum(" + albumId + ", " + userId + ")", e);
			throw new BlurSettingsPersistenceServiceException("Persistence failed.", e);
		} finally {
			close(stDel);
			close(stCR);
			close(conn);
		}
	}


	/** {@inheritDoc} */
	@Override
	public void blurPicture(long albumId, long pictureId, String userId) throws BlurSettingsPersistenceServiceException {

		//check
		BlurSettingBO setting = readBlurSetting(albumId, pictureId, userId);
		if (setting != null && setting.isBlurred())
			throw new PictureIsBlurredPersistenceException(albumId, pictureId, userId);

		Connection conn = null;
		PreparedStatement stDel = null;
		PreparedStatement stCR = null;
		try {
			conn = getConnection();
			stDel = conn.prepareStatement(SQL_DELETE_BY_ALBUM_AND_PHOTO_AND_USER_ID);
			stDel.setLong(1, albumId);
			stDel.setLong(2, pictureId);
			stDel.setString(3, userId);

			stDel.executeUpdate();

			stCR = conn.prepareStatement(SQL_CREATE);
			stCR.setLong(1, albumId);
			stCR.setLong(2, pictureId);
			stCR.setString(3, userId);
			stCR.setBoolean(4, true);
			stCR.executeUpdate();


		} catch (SQLException e) {
			LOGGER.error("blurPicture(" + albumId + ", " + pictureId + "," + userId + ")", e);
			throw new BlurSettingsPersistenceServiceException("Persistence failed.", e);
		} finally {
			close(stDel);
			close(stCR);
			close(conn);
		}

	}

	/** {@inheritDoc} */
	@Override
	public void blurPicture(long albumId, long pictureId) throws BlurSettingsPersistenceServiceException {
		if (isPictureBlurredForAllUsers(albumId, pictureId))
			throw new PictureIsBlurredPersistenceException(albumId, pictureId);
		Connection conn = null;
		PreparedStatement stDel = null;
		PreparedStatement stCR = null;
		try {
			conn = getConnection();
			stDel = conn.prepareStatement(SQL_DELETE_BY_ALBUM_AND_PHOTO_AND_USER_ID);
			stDel.setLong(1, albumId);
			stDel.setLong(2, pictureId);
			stDel.setString(3, FOR_ALL_USERS);

			stDel.executeUpdate();

			stCR = conn.prepareStatement(SQL_CREATE);
			stCR.setLong(1, albumId);
			stCR.setLong(2, pictureId);
			stCR.setString(3, FOR_ALL_USERS);
			stCR.setBoolean(4, true);
			stCR.executeUpdate();


		} catch (SQLException e) {
			LOGGER.error("blurPicture(" + albumId + ", " + pictureId + ")", e);
			throw new BlurSettingsPersistenceServiceException("Persistence failed.", e);
		} finally {
			close(stDel);
			close(stCR);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void unBlurPicture(long albumId, long pictureId, String userId) throws BlurSettingsPersistenceServiceException {

		//check
		BlurSettingBO setting = readBlurSetting(albumId, pictureId, userId);
		if (setting == null || !setting.isBlurred())
			throw new PictureIsNotBlurredPersistenceException(albumId, pictureId, userId);


		Connection conn = null;
		PreparedStatement stDel = null;
		PreparedStatement stCR = null;
		try {
			conn = getConnection();
			stDel = conn.prepareStatement(SQL_DELETE_BY_ALBUM_AND_PHOTO_AND_USER_ID);
			stDel.setLong(1, albumId);
			stDel.setLong(2, pictureId);
			stDel.setString(3, userId);

			stDel.executeUpdate();

			stCR = conn.prepareStatement(SQL_CREATE);
			stCR.setLong(1, albumId);
			stCR.setLong(2, pictureId);
			stCR.setString(3, userId);
			stCR.setBoolean(4, false);
			stCR.executeUpdate();

		} catch (SQLException e) {
			LOGGER.error("blurPicture(" + albumId + ", " + pictureId + "," + userId + ")", e);
			throw new BlurSettingsPersistenceServiceException("Persistence failed.", e);
		} finally {
			close(stDel);
			close(stCR);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void unBlurPicture(long albumId, long pictureId) throws BlurSettingsPersistenceServiceException {
		if (!isPictureBlurredForAllUsers(albumId, pictureId))
			throw new PictureIsNotBlurredPersistenceException(albumId, pictureId);

		Connection conn = null;
		PreparedStatement stDel = null;
		PreparedStatement stCR = null;
		try {
			conn = getConnection();
			stDel = conn.prepareStatement(SQL_DELETE_BY_ALBUM_AND_PHOTO_AND_USER_ID);
			stDel.setLong(1, albumId);
			stDel.setLong(2, pictureId);
			stDel.setString(3, FOR_ALL_USERS);

			stDel.executeUpdate();

			stCR = conn.prepareStatement(SQL_CREATE);
			stCR.setLong(1, albumId);
			stCR.setLong(2, pictureId);
			stCR.setString(3, FOR_ALL_USERS);
			stCR.setBoolean(4, false);
			stCR.executeUpdate();


		} catch (SQLException e) {
			LOGGER.error("blurPicture(" + albumId + ", " + pictureId + ")", e);
			throw new BlurSettingsPersistenceServiceException("Persistence failed.", e);
		} finally {
			close(stDel);
			close(stCR);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void removeBlurSettings(long albumId) throws BlurSettingsPersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(SQL_DELETE_BY_ALBUM_ID);
			st.setLong(1, albumId);

			st.executeUpdate();

		} catch (SQLException e) {
			LOGGER.error("removeBlurSettings(" + albumId + ")", e);
			throw new BlurSettingsPersistenceServiceException("Persistence failed.", e);
		} finally {
			close(st);
			close(conn);
		}
	}

	/**
	 * Return true - if picture blurred for all users!  Means  that   record like (albumId,0,0) - album blurred  or
	 * record (albumId,pictureId,0)  - exists.
	 *
	 * @param albumId   id of album
	 * @param pictureId id of picture
	 * @return boolean value
	 * @throws BlurSettingsPersistenceServiceException
	 *          on sql erros
	 */
	private boolean isPictureBlurredForAllUsers(long albumId, long pictureId) throws BlurSettingsPersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(SQL_READ_PICTURE_BLURRED_FOR_USER);
			st.setLong(1, albumId);
			st.setLong(2, FOR_ALL_PICTURES_IN_ALBUM);
			st.setLong(3, pictureId);
			st.setString(4, FOR_ALL_USERS);

			rs = st.executeQuery();
			return rs.next() && rs.getBoolean(FIELD_NAME_BLUR);

		} catch (SQLException e) {
			LOGGER.error("isPictureBlurredForAllUsers(" + albumId + "," + pictureId + ")", e);
			throw new BlurSettingsPersistenceServiceException("Persistence failed.", e);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}


	/** {@inheritDoc} */
	@Override
	protected List<String> getDDL() {
		return ddlQueries;
	}

	/** {@inheritDoc} */
	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	/** {@inheritDoc} */
	@Override
	protected String getPKFieldName() {
		//Not required!  here we have Primary on Multiple fields.
		return null;
	}


	/**
	 * Return BlurSetting with selected properties.
	 *
	 * @param args arguments array
	 * @return {@link BlurSettingBO}
	 * @throws BlurSettingsPersistenceServiceException
	 *          on SQL errors
	 */
	private BlurSettingBO readAlbumSetting(Object... args) throws BlurSettingsPersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			switch (args.length) {
				case 1:
					st = conn.prepareStatement(SQL_READ_SETTING);
					//albumID
					st.setLong(1, Long.valueOf(String.valueOf(args[0])));
					//all pictures in album
					st.setLong(2, FOR_ALL_PICTURES_IN_ALBUM);
					//all users in album
					st.setString(3, FOR_ALL_USERS);
					break;

				case 2:
					st = conn.prepareStatement(SQL_READ_ALBUM_BLURRED_FOR_USER);
					//album id
					st.setLong(1, Long.valueOf(String.valueOf(args[0])));
					//picture id
					st.setLong(2, FOR_ALL_PICTURES_IN_ALBUM);
					// user Id
					st.setString(3, String.valueOf(args[1]));
					//// all users Ids
					st.setString(4, FOR_ALL_USERS);
					break;

				case 3:
					st = conn.prepareStatement(SQL_READ_SETTING);
					//album ID
					st.setLong(1, Long.valueOf(String.valueOf(args[0])));
					//photoId
					st.setLong(2, Long.valueOf(String.valueOf(args[1])));
					//userId
					st.setString(3, String.valueOf(args[2]));
					break;
			}
			rs = st.executeQuery();
			if (rs.next()) {
				BlurSettingBO result = new BlurSettingBO();
				result.setAlbumId(rs.getLong(FIELD_NAME_ALBUM_ID));
				result.setPictureId(rs.getLong(FIELD_NAME_PHOTO_ID));
				result.setUserId(rs.getString(FIELD_NAME_USER_ID));
				result.setBlurred(rs.getBoolean(FIELD_NAME_BLUR));
				return result;
			}

			return null;

		} catch (SQLException e) {
			LOGGER.error("readAlbumSetting(" + args.length + ")", e);
			throw new BlurSettingsPersistenceServiceException("Persistence failed.", e);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}

}
