package net.anotheria.anosite.photoserver.service.storage.persistence.album;

import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.FIELD_NAME_DEFAULT_ALBUM;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.FIELD_NAME_DESCRIPTION;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.FIELD_NAME_ID;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.FIELD_NAME_NAME;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.FIELD_NAME_PHOTO_IDS;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.FIELD_NAME_USER_ID;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.SQL_CREATE;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.SQL_DELETE;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.SQL_META_ADD_RIGHTS;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.SQL_META_CREATE_DEFAULT_ALBUM_INDEX;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.SQL_META_CREATE_OWNER_INDEX;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.SQL_META_CREATE_TABLE;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.SQL_SELECT_BY_ALBUM_ID;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.SQL_SELECT_BY_USER_ID;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.SQL_SELECT_DEFAULT_ALBUM_BY_USER_ID;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.SQL_UPDATE;
import static net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceConstants.TABLE_NAME;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.anotheria.anosite.photoserver.service.storage.AlbumBO;
import net.anotheria.db.service.GenericPersistenceService;
import net.anotheria.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AlbumPersistenceService implementation.
 * 
 * @author dzhmud
 */
public class AlbumPersistenceServiceImpl extends GenericPersistenceService implements AlbumPersistenceService {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(AlbumPersistenceServiceImpl.class);

	/**
	 * String prefix for logging.
	 */
	private static final String LOG_PREFIX = "PHOTO_SERVER ALBUM PERSISTENCE SERVICE: ";

	/**
	 * Default constructor.
	 */
	protected AlbumPersistenceServiceImpl() {
		initialize();
	}

	@Override
	protected List<String> getDDL() {
		return Arrays.asList(SQL_META_CREATE_TABLE, SQL_META_CREATE_OWNER_INDEX, SQL_META_CREATE_DEFAULT_ALBUM_INDEX, SQL_META_ADD_RIGHTS);
	}

	@Override
	protected String getPKFieldName() {
		return FIELD_NAME_ID;
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	public AlbumBO createAlbum(final AlbumBO album) throws AlbumPersistenceServiceException {
		if (album == null)
			throw new IllegalArgumentException("AlbumVO is null");
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = getConnection();
			// Preparations before create.
			AlbumBO clonedAlbum = (AlbumBO) album.clone();
			clonedAlbum.setId(getNextId());
			// Prepare statement.
			st = conn.prepareStatement(SQL_CREATE);
			st.setLong(1, clonedAlbum.getId());
			st.setString(2, clonedAlbum.getUserId());
			st.setString(3, clonedAlbum.getName());
			st.setString(4, clonedAlbum.getDescription());
			st.setString(5, serialize(clonedAlbum.getPhotosOrder()));
			st.setBoolean(6, clonedAlbum.isDefault());

			st.executeUpdate(); // should return 1;

			return clonedAlbum;
		} catch (SQLException sqlE) {
			String message = "createAlbum(" + album.toString() + ") failed : " + sqlE.getMessage();
			LOG.error(LOG_PREFIX + message, sqlE);
			throw new AlbumPersistenceServiceException(message, sqlE);
		} finally {
			close(st);
			close(conn);
		}
	}

	@Override
	public AlbumBO getAlbum(final long albumId) throws AlbumPersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			// Prepare statement.
			st = conn.prepareStatement(SQL_SELECT_BY_ALBUM_ID);
			st.setLong(1, albumId);
			rs = st.executeQuery();
			if (rs.next())
				return mapResult(rs);

			throw new AlbumNotFoundPersistenceServiceException(albumId);
		} catch (SQLException sqlE) {
			String message = "getAlbum(" + albumId + ") failed : " + sqlE.getMessage();
			LOG.error(LOG_PREFIX + message, sqlE);
			throw new AlbumPersistenceServiceException(message, sqlE);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}

	@Override
	public AlbumBO getDefaultAlbum(String userId) throws AlbumPersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			// Prepare statement.
			st = conn.prepareStatement(SQL_SELECT_DEFAULT_ALBUM_BY_USER_ID);
			st.setString(1, userId);
			rs = st.executeQuery();
			// Map result if any.
			if (rs.next())
				return mapResult(rs);

			throw new DefaultAlbumNotFoundPersistenceServiceException(userId);
		} catch (SQLException sqlE) {
			String message = "getDefaultAlbum(" + userId + ") failed : " + sqlE.getMessage();
			LOG.error(LOG_PREFIX + message, sqlE);
			throw new AlbumPersistenceServiceException(message, sqlE);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}

	@Override
	public List<AlbumBO> getAlbums(final String userId) throws AlbumPersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			// Prepare statement.
			st = conn.prepareStatement(SQL_SELECT_BY_USER_ID);
			st.setString(1, userId);
			rs = st.executeQuery();
			List<AlbumBO> albums = new ArrayList<AlbumBO>();
			while (rs.next())
				albums.add(mapResult(rs));
			return albums;
		} catch (SQLException sqlE) {
			String message = "getAlbums(" + userId + ") failed : " + sqlE.getMessage();
			LOG.error(LOG_PREFIX + message, sqlE);
			throw new AlbumPersistenceServiceException(message, sqlE);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}

	@Override
	public void deleteAlbum(long albumId) throws AlbumPersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = getConnection();
			// Prepare statement.
			st = conn.prepareStatement(SQL_DELETE);
			st.setLong(1, albumId);
			int deleted = st.executeUpdate(); // should return 1;
			if (deleted == 0)
				throw new AlbumNotFoundPersistenceServiceException(albumId);
		} catch (SQLException sqlE) {
			String message = "deleteAlbum(" + albumId + ") failed : " + sqlE.getMessage();
			LOG.error(LOG_PREFIX + message, sqlE);
			throw new AlbumPersistenceServiceException(message, sqlE);
		} finally {
			close(st);
			close(conn);
		}
	}

	@Override
	public void updateAlbum(AlbumBO album) throws AlbumPersistenceServiceException {
		if (album == null)
			throw new IllegalArgumentException("AlbumVO is null");
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = getConnection();
			// Prepare statement.
			st = conn.prepareStatement(SQL_UPDATE);
			st.setString(1, album.getName());
			st.setString(2, album.getDescription());
			st.setString(3, serialize(album.getPhotosOrder()));
			st.setLong(4, album.getId());
			int updated = st.executeUpdate();// should return 1;
			if (updated == 0)
				throw new AlbumNotFoundPersistenceServiceException(album.getId());
		} catch (SQLException sqlE) {
			String message = "updateAlbum(" + album + ") failed : " + sqlE.getMessage();
			LOG.error(LOG_PREFIX + message, sqlE);
			throw new AlbumPersistenceServiceException(message, sqlE);
		} finally {
			close(st);
			close(conn);
		}
	}

	/**
	 * Map resultSet to AlbumBO.
	 * 
	 * @param rs
	 *            {@link java.sql.ResultSet} itself
	 * @return {@link AlbumBO}
	 * @throws java.sql.SQLException
	 *             on errors
	 */
	private AlbumBO mapResult(ResultSet rs) throws SQLException {
		AlbumBO album = new AlbumBO();
		album.setId(rs.getLong(FIELD_NAME_ID));
		album.setUserId(rs.getString(FIELD_NAME_USER_ID));
		album.setName(rs.getString(FIELD_NAME_NAME));
		album.setDescription(rs.getString(FIELD_NAME_DESCRIPTION));
		String photoIDs = rs.getString(FIELD_NAME_PHOTO_IDS);
		album.setPhotosOrder(parsePhotoIDs(photoIDs));
		album.setDefault(rs.getBoolean(FIELD_NAME_DEFAULT_ALBUM));
		return album;
	}

	private List<Long> parsePhotoIDs(String serialized) {
		List<Long> result = new ArrayList<Long>();
		if (!StringUtils.isEmpty(serialized)) {
			String[] ids = StringUtils.tokenize(serialized, true, ',');
			for (String id : ids)
				result.add(Long.valueOf(id));
		}
		return result;
	}

	private String serialize(List<Long> photoIds) {
		if (photoIds == null || photoIds.size() == 0)
			return "";
		StringBuilder sb = new StringBuilder();
		for (Long id : photoIds)
			sb.append(id.toString()).append(',');
		return sb.deleteCharAt(sb.length() - 1).toString();
	}

}
