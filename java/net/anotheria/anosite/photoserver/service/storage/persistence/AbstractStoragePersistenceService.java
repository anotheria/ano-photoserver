package net.anotheria.anosite.photoserver.service.storage.persistence;

import net.anotheria.anoprise.dualcrud.CrudService;
import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.ItemNotFoundException;
import net.anotheria.anoprise.dualcrud.Query;
import net.anotheria.anosite.photoserver.service.storage.PhotoBO;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;
import net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO;
import net.anotheria.db.service.GenericPersistenceService;
import net.anotheria.db.util.DDLConfig;
import net.anotheria.util.StringUtils;
import org.slf4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Base class for storage of photos.
 *
 * @author ykalapusha
 */
public abstract class AbstractStoragePersistenceService extends GenericPersistenceService implements CrudService<PhotoBO> {
    /**
     * Table owner constant.
     */
    private static final String TABLE_OWNER = DDLConfig.getInstance().getDbOwnerName();
    /**
     * SQL WHERE clause.
     */
    private static final String SQL_WHERE = " WHERE ";
    /**
     * Separator.
     */
    private static final String FIELD_SEPARATOR = ", ";
    /**
     * Record id attribute name.
     */
    static final String FIELD_NAME_ID = "id";
    /**
     * User id attribute name.
     */
    static final String FIELD_NAME_USER_ID = "p_userid";
    /**
     * User id attribute name.
     */
    static final String FIELD_NAME_ALBUM_ID = "p_albumid";
    /**
     * Photo name attribute name.
     */
    static final String FIELD_NAME_NAME = "p_name";
    /**
     * Photo description attribute name.
     */
    static final String FIELD_NAME_DESCRIPTION = "p_description";
    /**
     * Photo location attribute name.
     */
    static final String FIELD_NAME_FILE_LOCATION = "p_filelocation";
    /**
     * Photo extension attribute name.
     */
    static final String FIELD_NAME_FILE_EXTENSION = "p_extension";
    /**
     * Photo location attribute name.
     */
    static final String FIELD_NAME_MODIFICATION_TIME = "p_lastmodified";
    /**
     * Photo preview settings attribute name.
     */
    static final String FIELD_NAME_PREVIEW_SETTINGS = "p_previewsettings";
    /**
     * Photo approval status attribute name.
     */
    static final String FIELD_NAME_APPROVAL_STATUS = "p_approvalstatus";
    /**
     * Photo restricted attribute name.
     */
    static final String FIELD_NAME_RESTRICTED = "p_restricted";
    /**
     * Table fields constant.
     */
    private static final String TABLE_FIELDS =
            FIELD_NAME_ID + FIELD_SEPARATOR +
                    FIELD_NAME_USER_ID + FIELD_SEPARATOR +
                    FIELD_NAME_ALBUM_ID + FIELD_SEPARATOR +
                    FIELD_NAME_NAME + FIELD_SEPARATOR +
                    FIELD_NAME_DESCRIPTION + FIELD_SEPARATOR +
                    FIELD_NAME_FILE_LOCATION + FIELD_SEPARATOR +
                    FIELD_NAME_MODIFICATION_TIME + FIELD_SEPARATOR +
                    FIELD_NAME_FILE_EXTENSION + FIELD_SEPARATOR +
                    FIELD_NAME_PREVIEW_SETTINGS + FIELD_SEPARATOR +
                    FIELD_NAME_APPROVAL_STATUS + FIELD_SEPARATOR +
                    FIELD_NAME_RESTRICTED;

    /**
     * Index name for album owner.
     */
    private static final String OWNER_INDEX = FIELD_NAME_USER_ID + "_idx";
    /**
     * Index name for default album property.
     */
    private static final String ALBUM_ID_INDEX = FIELD_NAME_ALBUM_ID + "_idx";
    /**
     * Approval status index.
     */
    private static final String APPROVAL_STATUS_INDEX = FIELD_NAME_APPROVAL_STATUS + "_idx";
    /**
     * Modification time index.
     */
    private static final String MODIFICATION_TIME_INDEX = FIELD_NAME_MODIFICATION_TIME + "_idx";
    /**
     * Table name.
     */
    final String tableName;
    /**
     * Table photos primary key name.
     */
    final String pkName;
    /**
     * SQL for Photo creation.
     */
    final String sqlCreate;
    /**
     * SQL for Photo update.
     */
    final String sqlUpdate;
    /**
     * SQL for updating Photo approvalStatus field.
     */
    final String sqlUpdateStatus;
    /**
     * SQL for updating Photo approvalStatus field.
     */
    final String sqlUpdateAlbumId;
    /**
     * SQL for Photo deleting.
     */
    final String sqlDeleteById;
    /**
     * SQL for selecting Photos by userID and albumId.
     */
    final String sqlSelectByUserIdAndAlbumId;
    /**
     * Read by UserId.
     */
    final String sqlSelectByUserId;
    /**
     * SQL for selecting Photos by userID and photoIDs.
     */
    final String sqlSelectByUserIdAndPhotoIds;
    /**
     * SQL for selecting Photos by photo ID.
     */
    final String sqlSelectByPhotoId;
    /**
     * SQL for selecting Photos with specific ApprovalStatus.
     * Amount  should be passed as params!
     */
    final String sqlSelectByApprovalStatus;
    /**
     * SQL for selecting ApprovalStatus of photos in specific Album.
     */
    final String sqlSelectByAlbumId;
    /**
     * SQL for selecting Photos count with specific ApprovalStatus.
     */
    final String sqlCountByApprovalStatus;
    /**
     * SQL for table creation.
     */
    final String sqlMetaCreateTable;
    /**
     * SQL grant privileges for created table.
     */
    final String sqlMetaAddRights;
    /**
     * SQL create user id index.
     */
    final String sqlMetaCreateOwnerIndex;
    /**
     * SQL create album id index.
     */
    final String sqlMetaCreateAlbumIdIndex;
    /**
     * Approval status  index creation META.
     */
    final String sqlMetaCreateStatusIndex;
    /**
     * Approval status  index creation META.
     */
    final String sqlMetaCreateModificationTimeIndex;
    /**
     * {@link Logger} instance.
     */
    final Logger log;
    /**
     * Base constructor.
     */
    public AbstractStoragePersistenceService(String tableName, Logger logger) {
        this.log = logger;
        this.tableName = tableName;
        pkName = tableName + "_pk";
        sqlCreate = "INSERT INTO " + tableName + " (" + TABLE_FIELDS + ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        sqlUpdate = "UPDATE " + tableName + " SET " +
                FIELD_NAME_NAME + " = ?, " +
                FIELD_NAME_DESCRIPTION + " = ?, " +
                FIELD_NAME_MODIFICATION_TIME + " = ?, " +
                FIELD_NAME_PREVIEW_SETTINGS + " = ?" +
                SQL_WHERE + FIELD_NAME_ID + " = ?;";

        sqlUpdateStatus = "UPDATE " + tableName + " SET " +
                FIELD_NAME_APPROVAL_STATUS + " = ?" +
                SQL_WHERE + FIELD_NAME_ID + " = ?;";

        sqlUpdateAlbumId = "UPDATE " + tableName + " SET " +
                FIELD_NAME_MODIFICATION_TIME + " = ?, " +
                FIELD_NAME_ALBUM_ID + " = ?" +
                SQL_WHERE + FIELD_NAME_ID + " = ?;";

        sqlDeleteById = "DELETE FROM " + tableName + SQL_WHERE + FIELD_NAME_ID + " = ?;";
        sqlSelectByUserIdAndAlbumId = "SELECT " + TABLE_FIELDS + " FROM " + tableName + SQL_WHERE + FIELD_NAME_USER_ID + " = ? AND " + FIELD_NAME_ALBUM_ID + " = ?;";
        sqlSelectByUserId = "SELECT " + TABLE_FIELDS + " FROM " + tableName + SQL_WHERE + FIELD_NAME_USER_ID + " = ?;";
        sqlSelectByUserIdAndPhotoIds = "SELECT " + TABLE_FIELDS + " FROM " + tableName + SQL_WHERE + FIELD_NAME_USER_ID + " = ? AND " + FIELD_NAME_ID + " IN(?);";
        sqlSelectByPhotoId = "SELECT " + TABLE_FIELDS + " FROM " + tableName + SQL_WHERE + FIELD_NAME_ID + " = ?;";
        sqlSelectByApprovalStatus = "SELECT " + TABLE_FIELDS + " FROM " + tableName + SQL_WHERE + FIELD_NAME_APPROVAL_STATUS + " = ?" + " " +
                " ORDER BY " + FIELD_NAME_MODIFICATION_TIME + " ASC";

        sqlSelectByAlbumId = "SELECT " + TABLE_FIELDS + ", FROM " + tableName + SQL_WHERE + FIELD_NAME_ALBUM_ID + " = ?;";
        sqlCountByApprovalStatus = "SELECT COUNT(" + FIELD_NAME_ID + ") FROM " + tableName + SQL_WHERE + FIELD_NAME_APPROVAL_STATUS + " = ?;";

        sqlMetaCreateTable  = "CREATE TABLE " + tableName + " (" +
                FIELD_NAME_ID + " bigint NOT NULL, " +
                FIELD_NAME_USER_ID + " character varying NOT NULL, " +
                FIELD_NAME_ALBUM_ID + " bigint NOT NULL, " +
                FIELD_NAME_NAME + " character varying, " +
                FIELD_NAME_DESCRIPTION + " character varying, " +
                FIELD_NAME_FILE_LOCATION + " character varying NOT NULL, " +
                FIELD_NAME_MODIFICATION_TIME + " bigint NOT NULL, " +
                FIELD_NAME_FILE_EXTENSION + " character varying, " +
                FIELD_NAME_PREVIEW_SETTINGS + " character varying, " +
                FIELD_NAME_APPROVAL_STATUS + " integer NOT NULL, " +
                FIELD_NAME_RESTRICTED + " boolean," +
                "CONSTRAINT " + pkName + " PRIMARY KEY (" + FIELD_NAME_ID + ")" +
                ");";

        sqlMetaAddRights = "GRANT ALL ON " + tableName + " TO " + TABLE_OWNER + ";";
        sqlMetaCreateOwnerIndex = "CREATE INDEX " + OWNER_INDEX + " ON " + tableName + " (" + FIELD_NAME_USER_ID + ");";
        sqlMetaCreateAlbumIdIndex = "CREATE INDEX " + ALBUM_ID_INDEX + " ON " + tableName + " (" + FIELD_NAME_ALBUM_ID + ");";
        sqlMetaCreateStatusIndex = "CREATE INDEX " + APPROVAL_STATUS_INDEX + " ON " + tableName + " (" + FIELD_NAME_APPROVAL_STATUS + ");";
        sqlMetaCreateModificationTimeIndex = "CREATE INDEX " + MODIFICATION_TIME_INDEX + " ON " + tableName + " (" + FIELD_NAME_MODIFICATION_TIME + ");";
        initialize();
    }

    @Override
    protected List<String> getDDL() {
        return Arrays.asList(sqlMetaCreateTable, sqlMetaCreateOwnerIndex, sqlMetaCreateAlbumIdIndex, sqlMetaCreateStatusIndex,
                sqlMetaCreateModificationTimeIndex, sqlMetaAddRights);
    }

    @Override
    protected String getTableName() {
        return tableName;
    }

    @Override
    protected String getPKFieldName() {
        return FIELD_NAME_ID;
    }


    @Override
    public PhotoBO create(PhotoBO photoBO) throws CrudServiceException {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = getConnection();
            PhotoBO photo = photoBO.clone();
            photo.setId(getNextId());
            st = conn.prepareStatement(sqlCreate);
            st.setLong(1, photo.getId());
            st.setString(2, photo.getUserId());
            st.setLong(3, photo.getAlbumId());
            st.setString(4, photo.getName());
            st.setString(5, photo.getDescription());
            st.setString(6, getFileLocation(photoBO));
            st.setLong(7, photo.getModificationTime());
            st.setString(8, photo.getExtension());
            st.setString(9, serialize(photo.getPreviewSettings()));
            st.setInt(10, photo.getApprovalStatus().getCode());
            st.setBoolean(11, photo.isRestricted());
            st.executeUpdate(); // should return 1;

            createPhotoInStorage(photo);
            return photo;
        } catch (SQLException e) {
            log.error("SQL Exception: " + e.getMessage(), e);
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            close(st);
            close(conn);
        }
    }

    @Override
    public PhotoBO update(PhotoBO photoBO) throws CrudServiceException {
        return updatePhoto(photoBO);
    }

    @Override
    public PhotoBO save(PhotoBO photoBO) throws CrudServiceException {
        return updatePhoto(photoBO);
    }

    private PhotoBO updatePhoto(PhotoBO photo) throws CrudServiceException {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = getConnection();
            // Prepare statement.
            st = conn.prepareStatement(sqlUpdate);
            st.setString(1, photo.getName());
            st.setString(2, photo.getDescription());
            st.setLong(3, photo.getModificationTime());
            st.setString(4, serialize(photo.getPreviewSettings()));
            st.setLong(5, photo.getId());
            int updated = st.executeUpdate(); // should return 1;
            if (updated == 0)
                throw new ItemNotFoundException("Photo with id: " + photo.getId() + " not found");

            createPhotoInStorage(photo);
            return photo;
        } catch (SQLException e) {
            log.error("SQL Exception: " + e.getMessage(), e);
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            close(st);
            close(conn);
        }
    }

    @Override
    public PhotoBO read(String id) throws CrudServiceException, ItemNotFoundException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        PhotoBO photoBO;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sqlSelectByPhotoId);
            // where
            st.setLong(1, Long.parseLong(id));
            rs = st.executeQuery();
            if (rs.next())
                photoBO = mapResult(rs);
            else
                throw new ItemNotFoundException("Photo with " + id + "Not found");

            return photoBO;
        } catch (SQLException e) {
            log.error("SQL Exception: " + e.getMessage());
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            close(rs);
            close(st);
            close(conn);
        }
    }

    @Override
    public void delete(PhotoBO photoBO) throws CrudServiceException {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sqlDeleteById);
            // where
            st.setLong(1, photoBO.getId());
            int deletedRows = st.executeUpdate();
            if (deletedRows == 0)
                throw new ItemNotFoundException("Photo with id: " + photoBO.getId() + " not found");

            deletePhotoFromStorage(photoBO);
        } catch (SQLException e) {
            log.error("SQL Exception: " + e.getMessage());
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            close(st);
            close(conn);
        }
    }

    @Override
    public boolean exists(PhotoBO photoBO) throws CrudServiceException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        boolean existsInTable;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sqlSelectByPhotoId);
            st.setLong(1, photoBO.getId());
            rs = st.executeQuery();
            existsInTable = rs.next();
        } catch (SQLException e) {
            log.error("SQL Exception: " + e.getMessage());
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            close(rs);
            close(st);
            close(conn);
        }

        boolean existsInStorage = getPhotoFromStorage(photoBO).exists();
        if ((existsInStorage && !existsInTable) || (!existsInStorage && existsInTable))
            throw new CrudServiceException("There are missing data in some storage, existsInStorage=" + existsInStorage + ", existsInTable=" + existsInTable);
        else
            return existsInTable && existsInStorage;
    }

    @Override
    public List<PhotoBO> query(Query q) throws CrudServiceException {
        PhotoQueryName photoQueryName = PhotoQueryName.valueOf(q.getName());
        Map<String, String> valuesMap = StringUtils.buildParameterMap(q.getValue(), '&', '=');
        switch (photoQueryName) {
            case ALL_ALBUM_PHOTOS_BY_USER_ID:
                return getUserPhotosByUserIdAndAlbumId(valuesMap);
            case ALL_PHOTOS_FOR_USER_BY_PHOTOS_IDS:
                return getUserPhotosByUserIdAndPhotosIds(valuesMap);
            case PHOTOS_BY_STATUS_AND_IF_EXISTS_AMOUNT:
                return getPhotosByStatusAndAmountIfExists(valuesMap);
            case UPDATE_PHOTO_APPROVAL_STATUSES:
                return updatePhotoApprovalStatuses(valuesMap);
            case ALL_PHOTOS_BY_ALBUM_ID:
                return getPhotosByAlbumId(valuesMap);
            case MOVE_PHOTO_TO_NEW_ALBUM:
                return movePhotoToNewAlbum(valuesMap);
            default:
                throw new IllegalArgumentException("Query: [name=" + q.getName() + ", value=" + q.getValue() + "] is wrong");
        }
    }

    private List<PhotoBO> getUserPhotosByUserIdAndAlbumId(Map<String, String> valuesMap) throws CrudServiceException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sqlSelectByUserIdAndAlbumId);
            // where
            st.setString(1, valuesMap.get("userId"));
            st.setLong(2, Long.parseLong(valuesMap.get("albumId")));
            rs = st.executeQuery();

            List<PhotoBO> photos = new ArrayList<>();
            while (rs.next())
                photos.add(mapResult(rs));

            return photos;
        } catch (SQLException e) {
            log.error("SQL Exception: " + e.getMessage());
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            close(rs);
            close(st);
            close(conn);
        }
    }

    private List<PhotoBO> getUserPhotosByUserIdAndPhotosIds(Map<String, String> valuesMap) throws CrudServiceException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String query = sqlSelectByUserIdAndPhotoIds;
            query = query.substring(0, query.indexOf('?')) + "'" + valuesMap.get("userId") + "'" + query.substring(query.indexOf('?') + 1);
            query = query.substring(0, query.indexOf('?')) + valuesMap.get("photosIds") + query.substring(query.indexOf('?') + 1);
            st = conn.prepareStatement(query);
            rs = st.executeQuery();

            List<PhotoBO> photos = new ArrayList<>();
            while (rs.next())
                photos.add(mapResult(rs));
            return photos;
        } catch (SQLException e) {
            log.error("SQL Exception: " + e.getMessage());
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            close(rs);
            close(st);
            close(conn);
        }
    }

    private List<PhotoBO> getPhotosByStatusAndAmountIfExists(Map<String, String> valuesMap) throws CrudServiceException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        boolean withAmount = valuesMap.get("amount") != null;
        try {
            conn = getConnection();
            String statementStr = withAmount ? sqlSelectByApprovalStatus + " LIMIT ?;" : sqlSelectByApprovalStatus + ";";
            st = conn.prepareStatement(statementStr);
            // where clause
            st.setInt(1, Integer.parseInt(valuesMap.get("statusCode")));

            if (withAmount)
                st.setInt(2, Integer.parseInt(valuesMap.get("amount")));

            rs = st.executeQuery();

            List<PhotoBO> photos = new ArrayList<>();
            while (rs.next())
                photos.add(mapResult(rs));
            return photos;

        } catch (SQLException e) {
            log.error("SQL Exception: " + e.getMessage());
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            close(rs);
            close(st);
            close(conn);
        }
    }

    private List<PhotoBO> updatePhotoApprovalStatuses(Map<String, String> valuesMap) throws CrudServiceException {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sqlUpdateStatus);

            final boolean batchUpdate = valuesMap.size() > 1 && conn.getMetaData().supportsBatchUpdates();

            for (Map.Entry<String, String> status : valuesMap.entrySet()) {
                if (status.getValue() == null) {
                    log.warn("updatePhotoApprovalStatuses() received NULL as value in the map argument. Skipping entry.");
                    continue;
                }
                st.setInt(1, Integer.parseInt(status.getValue()));
                st.setLong(2, Long.parseLong(status.getKey()));

                if (batchUpdate)
                    st.addBatch();
                else
                    st.executeUpdate();
            }
            if (batchUpdate) {
                st.executeBatch();
            }
            return Collections.emptyList();
        } catch (SQLException e) {
            log.error("SQL Exception: " + e.getMessage());
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            close(st);
            close(conn);
        }
    }

    private List<PhotoBO> getPhotosByAlbumId(Map<String, String> valuesMap) throws CrudServiceException {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sqlSelectByAlbumId);
            st.setLong(1, Long.parseLong(valuesMap.get("albumId")));
            rs = st.executeQuery();

            List<PhotoBO> photos = new ArrayList<>();
            while (rs.next())
                photos.add(mapResult(rs));
            return photos;

        } catch (SQLException e) {
            log.error("SQL Exception: " + e.getMessage());
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            close(rs);
            close(st);
            close(conn);
        }
    }

    private List<PhotoBO> movePhotoToNewAlbum(Map<String, String> valuesMap) throws CrudServiceException {
        Connection conn = null;
        PreparedStatement st = null;
        try {
            conn = getConnection();
            st = conn.prepareStatement(sqlUpdateAlbumId);
            // where
            st.setLong(1, Long.parseLong(valuesMap.get("time")));
            st.setLong(2, Long.parseLong(valuesMap.get("newAlbum")));
            st.setLong(3, Long.parseLong(valuesMap.get("photoId")));
            int updated = st.executeUpdate(); // should return 1;
            if (updated == 0)
                throw new ItemNotFoundException("Photo with id=" + valuesMap.get("photoId") + " not found");

            return Collections.emptyList();
        } catch (SQLException e) {
            log.error("SQL Exception: " + e.getMessage(), e);
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            close(st);
            close(conn);
        }
    }

    protected abstract void createPhotoInStorage(PhotoBO photoBO) throws CrudServiceException;

    protected abstract File getPhotoFromStorage(PhotoBO photoBO) throws CrudServiceException;

    protected abstract String getFileLocation(PhotoBO photoBO);

    protected abstract void deletePhotoFromStorage(PhotoBO photoBO);

    String serialize(PreviewSettingsVO settings) {
        String result = "";
        if (settings != null) {
            result = String.valueOf(settings.getX()) + ',' + settings.getY() + ',' + settings.getWidth() + ',' +
                    settings.getHeight();
        }
        return result;
    }

    PhotoBO mapResult(ResultSet rs) throws SQLException, CrudServiceException {
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
        photo.setPhotoFile(getPhotoFromStorage(photo));
        return photo;
    }

    private PreviewSettingsVO mapPreviewSettings(String serialized) throws CrudServiceException {
        PreviewSettingsVO result = null;
        if (!StringUtils.isEmpty(serialized)) {
            String[] settings = StringUtils.tokenize(serialized, true, ',');
            if (settings.length == 4)
                result = new PreviewSettingsVO(Integer.parseInt(settings[0]), Integer.parseInt(settings[1]), Integer.parseInt(settings[2]),
                        Integer.parseInt(settings[3]));
            else
                throw new CrudServiceException("Can't deserialize PreviewSettingsVO : '" + serialized + "'");
        }
        return result;
    }
}
