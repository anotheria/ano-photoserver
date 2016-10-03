package net.anotheria.anosite.photoserver.service.blur.persistence;

import net.anotheria.db.util.DDLConfig;

/**
 * SQL Constants for  BlurSettingsPersistenceService.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public final class BlurSettingsPersistenceServiceConstants {

	/**
	 * Table owner constant.
	 */
	private static final String TABLE_OWNER = DDLConfig.getInstance().getDbOwnerName();

	/**
	 * Separator.
	 */
	private static final String FIELD_SEPARATOR = ", ";

	/**
	 * Table name for storing photos meta information.
	 */
	static final String TABLE_NAME = "t_blursettings";

	/**
	 * Table photos primary key name.
	 */
	static final String PK_NAME = TABLE_NAME + "_pk";

	/**
	 * User id attribute name.
	 */
	static final String FIELD_NAME_ALBUM_ID = "p_albumid";
	/**
	 * Photo id attribute name.
	 */
	static final String FIELD_NAME_PHOTO_ID = "p_photoid";

	/**
	 * User id attribute name.
	 */
	static final String FIELD_NAME_USER_ID = "p_userid";

	/**
	 * Blur attribute name.
	 */
	static final String FIELD_NAME_BLUR = "blur";
	/**
	 * SQL WHERE clause.
	 */
	private static final String SQL_WHERE = " WHERE ";
	/**
	 * SQL OrderBy clause.
	 */
	private static final String SQL_ORDER_BY = " ORDER BY ";
	/**
	 * SQL AND.
	 */
	private static final String SQL_AND = " AND ";
	/**
	 * SQL OR.
	 */
	private static final String SQL_OR = " OR ";


	/**
	 * Table fields constant.
	 */
	private static final String TABLE_FIELDS =
			FIELD_NAME_ALBUM_ID + FIELD_SEPARATOR +
					FIELD_NAME_PHOTO_ID + FIELD_SEPARATOR +
					FIELD_NAME_USER_ID + FIELD_SEPARATOR +
					FIELD_NAME_BLUR;


	/**
	 * SQL for BlurSetting creation.
	 */
	static final String SQL_CREATE = "INSERT INTO " + TABLE_NAME + " (" + TABLE_FIELDS + ") VALUES ( ?, ?, ?, ?);";


	/**
	 * SQL read settings with Order by clause - DESC.
	 */
	static final String SQL_READ_SETTINGS_WITH_ORDER = "SELECT " + TABLE_FIELDS + " FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_ALBUM_ID + " = ?" +
			SQL_AND + "(" + FIELD_NAME_PHOTO_ID + " = ?" + SQL_OR + FIELD_NAME_PHOTO_ID + " = ?)" + SQL_AND +
			"(" + FIELD_NAME_USER_ID + " = ?" + SQL_OR + FIELD_NAME_USER_ID + "= ?)" + SQL_ORDER_BY + FIELD_NAME_PHOTO_ID + " DESC , " +
			FIELD_NAME_USER_ID + " DESC;";

	/**
	 * SQL read setting.
	 */
	static final String SQL_READ_SETTING = "SELECT " + TABLE_FIELDS + " FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_ALBUM_ID + " = ?" +
			SQL_AND + FIELD_NAME_PHOTO_ID + " = ?" + SQL_AND +
			FIELD_NAME_USER_ID + " = ?;";

	/**
	 * SQL  Album blurred for user. ORDER BY - DESC - USER-ID.
	 */
	static final String SQL_READ_ALBUM_BLURRED_FOR_USER = "SELECT " + TABLE_FIELDS + " FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_ALBUM_ID + " = ?" +
			SQL_AND + FIELD_NAME_PHOTO_ID + " = ?" + SQL_AND + "(" +
			FIELD_NAME_USER_ID + " = ?" + SQL_OR + FIELD_NAME_USER_ID + " =?)" + SQL_ORDER_BY + FIELD_NAME_USER_ID + " DESC;";

	/**
	 * SQL  picture blurred for all. ORDER BY DESC PictureId.
	 */
	static final String SQL_READ_PICTURE_BLURRED_FOR_USER = "SELECT " + TABLE_FIELDS + " FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_ALBUM_ID + " = ?" +
			SQL_AND + "(" + FIELD_NAME_PHOTO_ID + " = ?" + SQL_OR + FIELD_NAME_PHOTO_ID + " = ?)" + SQL_AND +
			FIELD_NAME_USER_ID + " = ?" + SQL_ORDER_BY + FIELD_NAME_PHOTO_ID + " DESC;";


	/**
	 * SQL for update.
	 */
	static final String SQL_UPDATE = "UPDATE " + TABLE_NAME + " SET " +
			FIELD_NAME_ALBUM_ID + " = ?, " +
			FIELD_NAME_PHOTO_ID + " = ?, " +
			FIELD_NAME_USER_ID + " = ?, " +
			FIELD_NAME_BLUR + " = ?" +
			SQL_WHERE + FIELD_NAME_ALBUM_ID + " = ?" + SQL_AND + FIELD_NAME_PHOTO_ID + " = ?" + SQL_AND + FIELD_NAME_USER_ID + " = ?;";
	/**
	 * Deleted by album ID.
	 */
	static final String SQL_DELETE_BY_ALBUM_ID = "DELETE FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_ALBUM_ID + " = ?;";
	/**
	 * Delete by album and UserId.
	 */
	static final String SQL_DELETE_BY_ALBUM_AND_USER_ID = "DELETE FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_ALBUM_ID + " = ?" + SQL_AND +
			FIELD_NAME_USER_ID + " = ?;";
	/**
	 * Delete by album and PhotoId.
	 */
	static final String SQL_DELETE_BY_ALBUM_AND_PHOTO_ID = "DELETE FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_ALBUM_ID + " = ?" + SQL_AND +
			FIELD_NAME_PHOTO_ID + " = ?;";
	/**
	 * Delete by ALBUM and PHOTO and User id's.
	 */
	static final String SQL_DELETE_BY_ALBUM_AND_PHOTO_AND_USER_ID = "DELETE FROM " + TABLE_NAME + SQL_WHERE + FIELD_NAME_ALBUM_ID + " = ?" + SQL_AND +
			FIELD_NAME_PHOTO_ID + " = ?" + SQL_AND + FIELD_NAME_USER_ID + " = ?;";
	/**
	 * SQL for table creation.
	 */
	static final String SQL_META_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
			FIELD_NAME_ALBUM_ID + " bigint NOT NULL, " +
			FIELD_NAME_PHOTO_ID + " bigint NOT NULL, " +
			FIELD_NAME_USER_ID + " character varying NOT NULL, " +
			FIELD_NAME_BLUR + " boolean, " +
			"CONSTRAINT " + PK_NAME + " PRIMARY KEY (" + FIELD_NAME_ALBUM_ID + ", " + FIELD_NAME_PHOTO_ID + ", " + FIELD_NAME_USER_ID + ")" +
			");";


	/**
	 * SQL grant privileges for created table.
	 */
	static final String SQL_META_ADD_RIGHTS = "GRANT ALL ON " + TABLE_NAME + " TO " + TABLE_OWNER + ";";


	/**
	 * Private constructor.
	 */
	private BlurSettingsPersistenceServiceConstants() {
	}
}
