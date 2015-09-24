package net.anotheria.anosite.photoserver.service.storage.persistence;

import net.anotheria.db.util.DDLConfig;

/**
 * Class containing constants for creating SQL queries used by StoragePersistenceService.
 *
 * @author dzhmud
 */
final class StoragePersistenceServiceConstants {

	/**
	 * Do not instantiate this class.
	 */
	private StoragePersistenceServiceConstants() {
	}

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
	 * Table name for storing photos meta information.
	 */
	static final String TABLE_NAME = "t_photos";

	/**
	 * Table photos primary key name.
	 */
	static final String PK_NAME = TABLE_NAME + "_pk";

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
	 * Photo type attribute name.
	 */
	static final String FIELD_NAME_TYPE = "p_type";

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
					FIELD_NAME_RESTRICTED + FIELD_SEPARATOR +
					FIELD_NAME_TYPE;

	/**
	 * SQL for Photo creation.
	 */
	static final String SQL_CREATE = "INSERT INTO " + TABLE_NAME + " (" + TABLE_FIELDS + ") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

	/**
	 * SQL for Photo update.
	 */
	static final String SQL_UPDATE = "UPDATE " + TABLE_NAME + " SET " +
			FIELD_NAME_NAME + " = ?, " +
			FIELD_NAME_DESCRIPTION + " = ?, " +
			FIELD_NAME_MODIFICATION_TIME + " = ?, " +
			FIELD_NAME_PREVIEW_SETTINGS + " = ?, " +
			FIELD_NAME_TYPE + " = ?" +
			SQL_WHERE + FIELD_NAME_ID + " = ?;";

	/**
	 * SQL for updating Photo approvalStatus field.
	 */
	static final String SQL_UPDATE_STATUS = "UPDATE " + TABLE_NAME + " SET " +
			FIELD_NAME_APPROVAL_STATUS + " = ?" +
			SQL_WHERE + FIELD_NAME_ID + " = ?;";

    /**
     * SQL for updating Photo approvalStatus field.
     */
    static final String SQL_UPDATE_ALBUM_ID = "UPDATE " + TABLE_NAME + " SET " +
            FIELD_NAME_MODIFICATION_TIME + " = ?, " +
            FIELD_NAME_ALBUM_ID + " = ?" +
            SQL_WHERE + FIELD_NAME_ID + " = ?;";

	/**
	 * SQL for Photo deleting.
	 */
	static final String SQL_DELETE_BY_ID = "DELETE FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_ID + " = ?;";

	/**
	 * SQL for selecting Photos by userID and albumId.
	 */
	static final String SQL_SELECT_BY_USER_ID_AND_ALBUM_ID = "SELECT " + TABLE_FIELDS + " FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_USER_ID + " = ? AND " + FIELD_NAME_ALBUM_ID + " = ?;";
	/**
	 * Read by UserId.
	 */
	static final String SQL_SELECT_BY_USER_ID = "SELECT " + TABLE_FIELDS + " FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_USER_ID + " = ?;";

	/**
	 * SQL for selecting Photos by userID and photoIDs.
	 */
	static final String SQL_SELECT_BY_USER_ID_AND_PHOTO_IDS = "SELECT " + TABLE_FIELDS + " FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_USER_ID + " = ? AND " + FIELD_NAME_ID + " IN(?);";

	/**
	 * SQL for selecting Photos by photo ID.
	 */
	static final String SQL_SELECT_BY_PHOTO_ID = "SELECT " + TABLE_FIELDS + " FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_ID + " = ?;";

	/**
	 * SQL for selecting Photos with specific ApprovalStatus.
	 * Amount and LIMIT  should be passed as params!
	 */
	static final String SQL_SELECT_BY_APPROVAL_STATUS = "SELECT " + TABLE_FIELDS + " FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_APPROVAL_STATUS + " = ?" + " " +
			" ORDER BY " + FIELD_NAME_MODIFICATION_TIME + " ASC LIMIT ?;";

	/**
	 * SQL for selecting ApprovalStatus of photos in specific Album.
	 */
	static final String SQL_SELECT_APPROVAL_STATUS_BY_ALBUM_ID = "SELECT " + FIELD_NAME_ID + "," + FIELD_NAME_APPROVAL_STATUS + " FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_ALBUM_ID + " = ?;";

	/**
	 * SQL for selecting Photos with specific ApprovalStatus.
	 */
	static final String SQL_COUNT_BY_APPROVAL_STATUS = "SELECT COUNT(" + FIELD_NAME_ID + ") FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_APPROVAL_STATUS + " = ?;";

	/**
	 * SQL for table creation.
	 */
	static final String SQL_META_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
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
			FIELD_NAME_TYPE + " character varying, " +
			"CONSTRAINT " + PK_NAME + " PRIMARY KEY (" + FIELD_NAME_ID + ")" +
			");";

	/**
	 * SQL grant privileges for created table.
	 */
	static final String SQL_META_ADD_RIGHTS = "GRANT ALL ON " + TABLE_NAME + " TO " + TABLE_OWNER + ";";
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
	 * SQL create user id index.
	 */
	static final String SQL_META_CREATE_OWNER_INDEX = "CREATE INDEX " + OWNER_INDEX + " ON " + TABLE_NAME +
			" (" + FIELD_NAME_USER_ID + ");";
	/**
	 * SQL create album id index.
	 */
	static final String SQL_META_CREATE_ALBUM_ID_INDEX = "CREATE INDEX " + ALBUM_ID_INDEX + " ON " + TABLE_NAME +
			" (" + FIELD_NAME_ALBUM_ID + ");";
	/**
	 * Approval status  index creation META.
	 */
	static final String SQL_META_CREATE_STATUS_INDEX = "CREATE INDEX " + APPROVAL_STATUS_INDEX + " ON " + TABLE_NAME +
			" (" + FIELD_NAME_APPROVAL_STATUS + ");";
	/**
	 * Approval status  index creation META.
	 */
	static final String SQL_META_CREATE_MODIFICATION_TIME_INDEX = "CREATE INDEX " + MODIFICATION_TIME_INDEX + " ON " + TABLE_NAME +
			" (" + FIELD_NAME_MODIFICATION_TIME + ");";




}
