package net.anotheria.anosite.photoserver.service.storage.persistence;

import net.anotheria.anosite.photoserver.service.storage.PhotoBO;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;
import net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO;
import net.anotheria.db.service.GenericPersistenceService;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.anotheria.anosite.photoserver.service.storage.persistence.StoragePersistenceServiceConstants.*;

/**
 * Implementation of the StoragePersistenceService.
 *
 * @author dzhmud
 * @version $Id: $Id
 */
public class StoragePersistenceServiceImpl extends GenericPersistenceService implements StoragePersistenceService {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(StoragePersistenceServiceImpl.class);

	/**
	 * String prefix for logging.
	 */
	private static final String LOG_PREFIX = "PHOTO_SERVER STORAGE PERSISTENCE SERVICE: ";

	/**
	 * Default constructor.
	 */
	protected StoragePersistenceServiceImpl() {
		initialize();
	}

	/** {@inheritDoc} */
	@Override
	protected List<String> getDDL() {
		return Arrays.asList(SQL_META_CREATE_TABLE, SQL_META_CREATE_OWNER_INDEX, SQL_META_CREATE_ALBUM_ID_INDEX, SQL_META_CREATE_STATUS_INDEX,
				SQL_META_CREATE_MODIFICATION_TIME_INDEX, SQL_META_ADD_RIGHTS);
	}

	/** {@inheritDoc} */
	@Override
	protected String getPKFieldName() {
		return FIELD_NAME_ID;
	}

	/** {@inheritDoc} */
	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	/** {@inheritDoc} */
	@Override
	public PhotoBO createPhoto(final PhotoBO photoBO) throws StoragePersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = getConnection();
			// Preparations before create.
			PhotoBO photo = (PhotoBO) photoBO.clone();
			photo.setId(getNextId());
			// Prepare statement.
			st = conn.prepareStatement(SQL_CREATE);
			st.setLong(1, photo.getId());
			st.setString(2, photo.getUserId());
			st.setLong(3, photo.getAlbumId());
			st.setString(4, photo.getName());
			st.setString(5, photo.getDescription());
			st.setString(6, photo.getFileLocation());
			st.setLong(7, photo.getModificationTime());
			st.setString(8, photo.getExtension());
			st.setString(9, serialize(photo.getPreviewSettings()));
			st.setInt(10, photo.getApprovalStatus().getCode());
			st.setBoolean(11, photo.isRestricted());
			st.executeUpdate(); // should return 1;
			return photo;
		} catch (SQLException sqlE) {
			LOG.error(LOG_PREFIX + "SQL Exception: " + sqlE.getMessage(), sqlE);
			throw new StoragePersistenceServiceException(sqlE.getMessage(), sqlE);
		} finally {
			close(st);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void deletePhoto(final long photoId) throws StoragePersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(SQL_DELETE_BY_ID);
			// where
			st.setLong(1, photoId);
			int deletedRows = st.executeUpdate();
			if (deletedRows == 0)
				throw new PhotoNotFoundPersistenceServiceException(photoId);
		} catch (SQLException sqlE) {
			LOG.error(LOG_PREFIX + "SQL Exception: " + sqlE.getMessage());
			throw new StoragePersistenceServiceException(sqlE.getMessage(), sqlE);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public PhotoBO getPhoto(final long photoId) throws StoragePersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(SQL_SELECT_BY_PHOTO_ID);
			// where
			st.setLong(1, photoId);
			rs = st.executeQuery();
			if (rs.next())
				return mapResult(rs);

			throw new PhotoNotFoundPersistenceServiceException(photoId);
		} catch (SQLException sqlE) {
			LOG.error(LOG_PREFIX + "SQL Exception: " + sqlE.getMessage());
			throw new StoragePersistenceServiceException(sqlE.getMessage(), sqlE);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void updatePhoto(final PhotoBO photo) throws StoragePersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = getConnection();
			// Prepare statement.
			st = conn.prepareStatement(SQL_UPDATE);
			st.setString(1, photo.getName());
			st.setString(2, photo.getDescription());
			st.setLong(3, photo.getModificationTime());
			st.setString(4, serialize(photo.getPreviewSettings()));
			st.setLong(5, photo.getId());
			int updated = st.executeUpdate(); // should return 1;
			if (updated == 0)
				throw new PhotoNotFoundPersistenceServiceException(photo.getId());
		} catch (SQLException sqlE) {
			LOG.error(LOG_PREFIX + "SQL Exception: " + sqlE.getMessage(), sqlE);
			throw new StoragePersistenceServiceException(sqlE.getMessage(), sqlE);
		} finally {
			close(st);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<PhotoBO> getUserPhotos(final String userId, final long albumId) throws StoragePersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(SQL_SELECT_BY_USER_ID_AND_ALBUM_ID);
			// where
			st.setString(1, userId);
			st.setLong(2, albumId);
			rs = st.executeQuery();

			List<PhotoBO> photos = new ArrayList<PhotoBO>();
			while (rs.next())
				photos.add(mapResult(rs));

			return photos;
		} catch (SQLException sqlE) {
			LOG.error(LOG_PREFIX + "SQL Exception: " + sqlE.getMessage());
			throw new StoragePersistenceServiceException(sqlE.getMessage(), sqlE);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<PhotoBO> getAllUserPhotos(String userId) throws StoragePersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(SQL_SELECT_BY_USER_ID);
			// where
			st.setString(1, userId);
			rs = st.executeQuery();

			List<PhotoBO> photos = new ArrayList<PhotoBO>();
			while (rs.next())
				photos.add(mapResult(rs));

			return photos;
		} catch (SQLException sqlE) {
			LOG.error(LOG_PREFIX + "SQL Exception: " + sqlE.getMessage());
			throw new StoragePersistenceServiceException(sqlE.getMessage(), sqlE);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<PhotoBO> getUserPhotos(String userId, List<Long> photoIDs) throws StoragePersistenceServiceException {
		if (photoIDs == null)
			throw new IllegalArgumentException("NULL photoIDs");
		if (photoIDs.isEmpty())
			return Collections.emptyList();
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			String query = SQL_SELECT_BY_USER_ID_AND_PHOTO_IDS;
			query = query.substring(0, query.indexOf('?')) + "'" + userId + "'" + query.substring(query.indexOf('?') + 1);
			query = query.substring(0, query.indexOf('?')) + serialize(photoIDs) + query.substring(query.indexOf('?') + 1);
			st = conn.prepareStatement(query);
			rs = st.executeQuery();

			List<PhotoBO> photos = new ArrayList<PhotoBO>();
			while (rs.next())
				photos.add(mapResult(rs));
			return photos;

		} catch (SQLException sqlE) {
			LOG.error(LOG_PREFIX + "SQL Exception: " + sqlE.getMessage());
			throw new StoragePersistenceServiceException(sqlE.getMessage(), sqlE);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public List<PhotoBO> getPhotosWithStatus(int amount, ApprovalStatus status) throws StoragePersistenceServiceException {
		if (status == null)
			throw new IllegalArgumentException("NULL status received as parameter.");

		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(SQL_SELECT_BY_APPROVAL_STATUS);
			// where clause
			st.setInt(1, status.getCode());
			// limit
			st.setInt(2, amount);

			rs = st.executeQuery();

			List<PhotoBO> photos = new ArrayList<PhotoBO>();
			while (rs.next())
				photos.add(mapResult(rs));
			return photos;

		} catch (SQLException sqlE) {
			LOG.error(LOG_PREFIX + "SQL Exception: " + sqlE.getMessage());
			throw new StoragePersistenceServiceException(sqlE.getMessage(), sqlE);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public int getPhotosWithStatusCount(ApprovalStatus status) throws StoragePersistenceServiceException {
		if (status == null)
			throw new IllegalArgumentException("NULL status received as parameter.");
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(SQL_COUNT_BY_APPROVAL_STATUS);
			st.setInt(1, status.getCode());
			rs = st.executeQuery();
			if (rs.next())
				return rs.getInt(1);

			throw new StoragePersistenceServiceException("getPhotosWithStatusCount() failed.");
		} catch (SQLException sqlE) {
			LOG.error(LOG_PREFIX + "SQL Exception: " + sqlE.getMessage());
			throw new StoragePersistenceServiceException(sqlE.getMessage(), sqlE);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void updatePhotoApprovalStatuses(Map<Long, ApprovalStatus> statuses) throws StoragePersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(SQL_UPDATE_STATUS);

			final boolean batchUpdate = statuses.size() > 1 && conn.getMetaData().supportsBatchUpdates();

			for (Map.Entry<Long, ApprovalStatus> status : statuses.entrySet()) {
				if (status.getValue() == null) {
					LOG.warn("updatePhotoApprovalStatuses() received NULL as value in the map argument. Skipping entry.");
					continue;
				}
				st.setInt(1, status.getValue().getCode());
				st.setLong(2, status.getKey());

				if (batchUpdate)
					st.addBatch();
				else
					st.executeUpdate();
			}
			if (batchUpdate) {
				st.executeBatch();
			}
		} catch (SQLException sqlE) {
			LOG.error(LOG_PREFIX + "SQL Exception: " + sqlE.getMessage());
			throw new StoragePersistenceServiceException(sqlE.getMessage(), sqlE);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}

	}

	/** {@inheritDoc} */
	@Override
	public Map<Long, ApprovalStatus> getAlbumPhotosApprovalStatus(long albumId) throws StoragePersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(SQL_SELECT_APPROVAL_STATUS_BY_ALBUM_ID);
			st.setLong(1, albumId);
			rs = st.executeQuery();

			Map<Long, ApprovalStatus> result = new HashMap<Long, ApprovalStatus>();
			while (rs.next()) {
				result.put(rs.getLong(FIELD_NAME_ID), ApprovalStatus.getStatusByCode(rs.getInt(FIELD_NAME_APPROVAL_STATUS)));
			}
			return result;

		} catch (SQLException sqlE) {
			LOG.error(LOG_PREFIX + "SQL Exception: " + sqlE.getMessage());
			throw new StoragePersistenceServiceException(sqlE.getMessage(), sqlE);
		} finally {
			close(rs);
			close(st);
			close(conn);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void movePhoto(long photoId, long newAlbumId, long modificationTime) throws StoragePersistenceServiceException {
		Connection conn = null;
		PreparedStatement st = null;
		try {
			conn = getConnection();
			st = conn.prepareStatement(SQL_UPDATE_ALBUM_ID);
			// where
			st.setLong(1, modificationTime);
			st.setLong(2, newAlbumId);
			st.setLong(3, photoId);
			int updated = st.executeUpdate(); // should return 1;
			if (updated == 0)
				throw new PhotoNotFoundPersistenceServiceException(photoId);
		} catch (SQLException sqlE) {
			LOG.error(LOG_PREFIX + "SQL Exception: " + sqlE.getMessage(), sqlE);
			throw new StoragePersistenceServiceException(sqlE.getMessage(), sqlE);
		} finally {
			close(st);
			close(conn);
		}
	}

	/**
	 * Map resultSet to PhotoBO.
	 * 
	 * @param rs
	 *            {@link java.sql.ResultSet} itself
	 * @return {@link PhotoBO}
	 * @throws java.sql.SQLException
	 *             on errors
	 * @throws StoragePersistenceServiceException
	 *             - if deserializing PreviewSettingsVO from previewSettings field is broken.
	 */
	private PhotoBO mapResult(ResultSet rs) throws SQLException, StoragePersistenceServiceException {
		PhotoBO photo = new PhotoBO();
		photo.setId(rs.getLong(FIELD_NAME_ID));
		photo.setUserId(rs.getString(FIELD_NAME_USER_ID));
		photo.setAlbumId(rs.getLong(FIELD_NAME_ALBUM_ID));
		photo.setName(rs.getString(FIELD_NAME_NAME));
		photo.setDescription(rs.getString(FIELD_NAME_DESCRIPTION));
		photo.setFileLocation(rs.getString(FIELD_NAME_FILE_LOCATION));
		photo.setModificationTime(rs.getLong(FIELD_NAME_MODIFICATION_TIME));
		photo.setExtension(rs.getString(FIELD_NAME_FILE_EXTENSION));
		photo.setPreviewSettings(mapPreviewSettings(rs.getString(FIELD_NAME_PREVIEW_SETTINGS)));
		photo.setApprovalStatus(ApprovalStatus.getStatusByCode(rs.getInt(FIELD_NAME_APPROVAL_STATUS)));
		photo.setRestricted(rs.getBoolean(FIELD_NAME_RESTRICTED));
		return photo;
	}

	private PreviewSettingsVO mapPreviewSettings(String serialized) throws StoragePersistenceServiceException {
		PreviewSettingsVO result = null;
		if (!StringUtils.isEmpty(serialized)) {
			String[] settings = StringUtils.tokenize(serialized, true, ',');
			if (settings.length == 4)
				result = new PreviewSettingsVO(Integer.parseInt(settings[0]), Integer.parseInt(settings[1]), Integer.parseInt(settings[2]),
						Integer.parseInt(settings[3]));
			else
				throw new StoragePersistenceServiceException("Can't deserialize PreviewSettingsVO : '" + serialized + "'");
		}
		return result;
	}

	private String serialize(PreviewSettingsVO settings) {
		String result = "";
		if (settings != null) {
			result = new StringBuilder().append(settings.getX()).append(',').append(settings.getY()).append(',').append(settings.getWidth()).append(',')
					.append(settings.getHeight()).toString();
		}
		return result;
	}

	private String serialize(List<Long> photoIDs) {
		StringBuilder result = new StringBuilder();
		for (Long photoID : photoIDs) {
			result.append(photoID).append(',');
		}
		return result.deleteCharAt(result.length() - 1).toString();
	}

}
