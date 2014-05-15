package net.anotheria.anosite.photoserver.service.storage.persistence.album;

import net.anotheria.db.util.DDLConfig;

/**
 * Class containing constants for creating SQL queries used by AlbumPersistenceService.
 *
 * @author dzhmud
 */
final class AlbumPersistenceServiceConstants {

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
	 * Table name for storing albums meta information.
	 */
	static final String TABLE_NAME = "t_albums";

	/**
	 * Table albums primary key name.
	 */
	static final String PK_NAME = TABLE_NAME + "_pk";

	/**
	 * Record id attribute name.
	 */
	static final String FIELD_NAME_ID = "id";

	/**
	 * User id attribute name.
	 */
	static final String FIELD_NAME_USER_ID = "a_userid";

	/**
	 * Album name attribute name.
	 */
	static final String FIELD_NAME_NAME = "a_name";

	/**
	 * Album description attribute name.
	 */
	static final String FIELD_NAME_DESCRIPTION = "a_description";

	/**
	 * Photo ids attribute name.
	 */
	static final String FIELD_NAME_PHOTO_IDS = "a_photosorder";

	/**
	 * Album 'isDefault' attribute name.
	 */
	static final String FIELD_NAME_DEFAULT_ALBUM = "a_isdefault";

	/**
	 * Table fields constant.
	 */
	static final String TABLE_FIELDS =
			FIELD_NAME_ID + FIELD_SEPARATOR +
					FIELD_NAME_USER_ID + FIELD_SEPARATOR +
					FIELD_NAME_NAME + FIELD_SEPARATOR +
					FIELD_NAME_DESCRIPTION + FIELD_SEPARATOR +
					FIELD_NAME_PHOTO_IDS + FIELD_SEPARATOR +
					FIELD_NAME_DEFAULT_ALBUM;

	/**
	 * SQL for Album creation.
	 */
	static final String SQL_CREATE = "INSERT INTO " + TABLE_NAME + " (" + TABLE_FIELDS + ") VALUES ( ?, ?, ?, ?, ?, ?);";

	/**
	 * SQL for Album update.
	 */
	static final String SQL_UPDATE = "UPDATE " + TABLE_NAME + " SET " +
			FIELD_NAME_NAME + " = ?, " +
			FIELD_NAME_DESCRIPTION + " = ?, " +
			FIELD_NAME_PHOTO_IDS + " = ?" +
			SQL_WHERE + FIELD_NAME_ID + " = ?;";

	/**
	 * SQL for Album deleting.
	 */
	static final String SQL_DELETE = "DELETE FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_ID + " = ?;";

	/**
	 * SQL for selecting Albums by userID.
	 */
	static final String SQL_SELECT_BY_USER_ID = "SELECT " + TABLE_FIELDS + " FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_USER_ID + " = ?;";

	/**
	 * SQL for selecting default Album by userID.
	 */
	static final String SQL_SELECT_DEFAULT_ALBUM_BY_USER_ID = "SELECT " + TABLE_FIELDS + " FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_USER_ID + " = ? AND " + FIELD_NAME_DEFAULT_ALBUM + " = true;";

	/**
	 * SQL for selecting Album by album ID.
	 */
	static final String SQL_SELECT_BY_ALBUM_ID = "SELECT " + TABLE_FIELDS + " FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_ID + " = ?;";

	/**
	 * SQL for table creation.
	 */
	static final String SQL_META_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
			FIELD_NAME_ID + " bigint NOT NULL, " +
			FIELD_NAME_USER_ID + " character varying NOT NULL, " +
			FIELD_NAME_NAME + " character varying, " +
			FIELD_NAME_DESCRIPTION + " character varying, " +
			FIELD_NAME_PHOTO_IDS + " character varying NOT NULL, " +
			FIELD_NAME_DEFAULT_ALBUM + " boolean, " +
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
	private static final String DEFAULT_ALBUM_INDEX = FIELD_NAME_DEFAULT_ALBUM + "_idx";

	/**
	 * SQL create user id index.
	 */
	protected static final String SQL_META_CREATE_OWNER_INDEX = "CREATE INDEX " + OWNER_INDEX + " ON " + TABLE_NAME +
			" (" + FIELD_NAME_USER_ID + ");";
	/**
	 * SQL create user id index.
	 */
	protected static final String SQL_META_CREATE_DEFAULT_ALBUM_INDEX = "CREATE INDEX " + DEFAULT_ALBUM_INDEX + " ON " + TABLE_NAME +
			" (" + FIELD_NAME_DEFAULT_ALBUM + ");";
}
