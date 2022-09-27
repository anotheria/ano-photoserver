package net.anotheria.anosite.photoserver.service.storage;

import java.io.File;
import java.io.Serializable;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.LoggerFactory;

/**
 *
 * Configuration bean of the PhotoServer storage.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
@ConfigureMe(name = "ano-site-photoserver-storage-config")
public final class StorageConfig implements Serializable {

	/**
	 * Basic serialVersionUID variable.
	 */
	@DontConfigure
	private static final long serialVersionUID = -428530914695519035L;

	/**
	 * Text prefix for validation exception message.
	 */
	@DontConfigure
	public static final String VALIDATION_ERROR_PREFIX = "Validation error: ";

	/**
	 * Default maximum owner id length.
	 */
	@DontConfigure
	public static final int DEFAULT_MAX_OWNER_ID_LENGTH = 10;

	/**
	 * Default fragment length.
	 */
	@DontConfigure
	public static final int DEFAULT_FRAGMENT_LENGTH = 2;

	/**
	 * Configuration instance.
	 */
	@DontConfigure
	private static StorageConfig INSTANCE;

	/**
	 * File separator.
	 */
	@DontConfigure
	private static final String S = File.separator;

	/**
	 * Storage root folder.
	 */
	@Configure
	private String storageRoot = S + "work" + S + "data" + S + "photoserver";

	/**
	 *  Second storage root folder,
	 *  if the value is not empty, the file will also be written to the second persistence (if ceph is disabled).
	 */
	@Configure
	private String storageRootSecond = null;

	/**
	 * Boolean attribute to check if we need to use both storage root directories or only first one.
	 */
	@Configure
	private boolean useSecondStorageRootOnly = false;

	/**
	 * Temporary storage root folder.
	 */
	@Configure
	private String tmpStorageRoot = S + "work" + S + "data" + S + "photoserver" + S + "tmp";

	/**
	 * Maximum owner id length.
	 */
	@Configure
	private int maxOwnerIdLength = DEFAULT_MAX_OWNER_ID_LENGTH;

	/**
	 * Fragment length.
	 */
	@Configure
	private int fragmentLegth = DEFAULT_FRAGMENT_LENGTH;

	/**
	 * Replace special characters for path creation from any {@link String} user identifier.
	 */
	@Configure
	private boolean replaceSpecCharacters = true;

	/**
	 * Get instance method.
	 *
	 * @return {@link net.anotheria.anosite.photoserver.service.storage.StorageConfig}
	 */
	public static synchronized StorageConfig getInstance() {
		if (INSTANCE == null)
			INSTANCE = new StorageConfig();

		return INSTANCE;
	}

	/**
	 * Default constructor.
	 */
	private StorageConfig() {
		try {
			ConfigurationManager.INSTANCE.configure(this);
		} catch (Exception e) {
			LoggerFactory.getLogger(StorageConfig.class).error("StorageConfig() Configuration failed. Configuring with defaults.", e);
		}
	}

	/**
	 * <p>Setter for the field <code>storageRoot</code>.</p>
	 *
	 * @param aStorageRoot a {@link java.lang.String} object.
	 */
	public void setStorageRoot(String aStorageRoot) {
		this.storageRoot = aStorageRoot;
	}

	/**
	 * <p>Getter for the field <code>storageRoot</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getStorageRoot() {
		return storageRoot;
	}

	public String getStorageRootSecond() {
		return storageRootSecond;
	}

	public void setStorageRootSecond(String storageRootSecond) {
		this.storageRootSecond = storageRootSecond;
	}

	public boolean isUseSecondStorageRootOnly() {
		return useSecondStorageRootOnly;
	}

	public void setUseSecondStorageRootOnly(boolean useSecondStorageRootOnly) {
		this.useSecondStorageRootOnly = useSecondStorageRootOnly;
	}

	/**
	 * <p>Setter for the field <code>tmpStorageRoot</code>.</p>
	 *
	 * @param tmpStorageRoot a {@link java.lang.String} object.
	 */
	public void setTmpStorageRoot(String tmpStorageRoot) {
		this.tmpStorageRoot = tmpStorageRoot;
	}

	/**
	 * <p>Getter for the field <code>tmpStorageRoot</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getTmpStorageRoot() {
		return tmpStorageRoot;
	}

	/**
	 * <p>Setter for the field <code>maxOwnerIdLength</code>.</p>
	 *
	 * @param maxOwnerIdLength a int.
	 */
	public void setMaxOwnerIdLength(int maxOwnerIdLength) {
		this.maxOwnerIdLength = maxOwnerIdLength;
	}

	/**
	 * <p>Getter for the field <code>maxOwnerIdLength</code>.</p>
	 *
	 * @return a int.
	 */
	public int getMaxOwnerIdLength() {
		return maxOwnerIdLength;
	}

	/**
	 * <p>Setter for the field <code>fragmentLegth</code>.</p>
	 *
	 * @param fragmentLegth a int.
	 */
	public void setFragmentLegth(int fragmentLegth) {
		this.fragmentLegth = fragmentLegth;
	}

	/**
	 * <p>Getter for the field <code>fragmentLegth</code>.</p>
	 *
	 * @return a int.
	 */
	public int getFragmentLegth() {
		return fragmentLegth;
	}

	/**
	 * <p>isReplaceSpecCharacters.</p>
	 *
	 * @return a boolean.
	 */
	public boolean isReplaceSpecCharacters() {
		return replaceSpecCharacters;
	}

	/**
	 * <p>Setter for the field <code>replaceSpecCharacters</code>.</p>
	 *
	 * @param aReplaceSpecCharacters a boolean.
	 */
	public void setReplaceSpecCharacters(boolean aReplaceSpecCharacters) {
		this.replaceSpecCharacters = aReplaceSpecCharacters;
	}

	/**
	 * Return store folder path.
	 *
	 * @param ownerId
	 *            - owner id
	 * @return folder path
	 */
	public static String getStoreFolderPath(String ownerId) {
		return getInstance().useSecondStorageRootOnly ? getStorageFolderPathSecond(ownerId) : getStorageFolderPath(ownerId, getInstance().storageRoot);
	}

	public static String getStoreFolderPathFirst(String ownerId) {
		return getStorageFolderPath(ownerId, getInstance().storageRoot);
	}

	public static String getStorageFolderPathSecond(String ownerId) {
		String id = validateOwnerId(ownerId);
		String token = id.split("-")[0];

		String path = getInstance().storageRootSecond;
		String lastChar = path.substring(path.length() - 1);
		if (!lastChar.equals(File.separator))
			path += File.separator;

		String[] fragments = fragmentOwnerId(token, 0, getInstance().fragmentLegth);
		StringBuilder ret = new StringBuilder();
		for (String f : fragments)
			ret.append(f).append(File.separatorChar);

		return path + ret + ownerId + File.separator;
	}

	public static String getFolderPathSecond(String ownerId) {
		String id = validateOwnerId(ownerId);
		String token = id.split("-")[0];

		String[] fragments = fragmentOwnerId(token, 0, getInstance().fragmentLegth);
		StringBuilder ret = new StringBuilder();
		for (String f : fragments)
			ret.append(f).append(File.separatorChar);

		return ret + ownerId + File.separator;
	}

	/**
	 * Return temporary store folder path.
	 *
	 * @param ownerId
	 *            - owner id
	 * @return folder path
	 */
	public static String getTmpStoreFolderPath(String ownerId) {
		return getStorageFolderPath(ownerId, getInstance().tmpStorageRoot);
	}

	/**
	 * Internal method.
	 * 
	 * @param ownerId
	 *            - owner id
	 * @param storageRootFolder
	 *            - storage root folder
	 * @return folder path
	 */
	private static String getStorageFolderPath(String ownerId, String storageRootFolder) {
		String id = validateOwnerId(ownerId);

		String path = storageRootFolder;
		String lastChar = path.substring(path.length() - 1, path.length());
		if (!lastChar.equals(File.separator))
			path += File.separator;

		String[] fragments = fragmentOwnerId(id, getInstance().maxOwnerIdLength, getInstance().fragmentLegth);
		StringBuilder ret = new StringBuilder();
		for (String f : fragments)
			ret.append(f).append(File.separatorChar);

		return path + replaceSpecCharacters(ret.toString());
	}

	/**
	 * Internal method for fragmenting owner id by parameters.
	 * 
	 * @param ownerId
	 *            - owner id
	 * @param maxOwnerIdLength
	 *            - max owner id length
	 * @param fragmentLength
	 *            - fragment length
	 * @return fragments
	 */
	private static String[] fragmentOwnerId(String ownerId, int maxOwnerIdLength, int fragmentLength) {
		if (ownerId == null || ownerId.length() == 0)
			throw new IllegalArgumentException("OwnerId is null or empty");

		while (ownerId.length() < maxOwnerIdLength)
			ownerId = "0" + ownerId;

		while (ownerId.length() % fragmentLength != 0)
			ownerId = "0" + ownerId;

		int fragmentationDepth = ownerId.length() / fragmentLength;
		String[] ret = new String[fragmentationDepth];
		for (int i = 0; i < fragmentationDepth; i++) {
			String fragment = ownerId.substring(i * fragmentLength, i * fragmentLength + fragmentLength);
			ret[i] = fragment;
		}

		return ret;
	}

	/**
	 * Validation method.
	 * 
	 * @param ownerId
	 *            - owner id
	 * @return validated owner id
	 */
	private static String validateOwnerId(String ownerId) {
		if (ownerId == null)
			throw new IllegalArgumentException(VALIDATION_ERROR_PREFIX + "Null ownerId argument.");

		if (ownerId.length() < 1)
			throw new IllegalArgumentException(VALIDATION_ERROR_PREFIX + "Minimum length for ownerId: 1.");

		return ownerId;
	}

	/**
	 * Utility method for replacing some problem characters used in user identifiers and not possible to use as folder or file names on file systems.
	 * 
	 * @param value
	 *            original value
	 * @return value with replacements if replacement enabled
	 */
	private static String replaceSpecCharacters(final String value) {
		if (!getInstance().isReplaceSpecCharacters())
			return value;

		String result = value;

		// replacing "-" to "_", for Linux file systems fix
		result = result.replace("-", "_");

		return result;

	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "StorageConfig [storageRoot=" + storageRoot + ", tmpStorageRoot=" + tmpStorageRoot + ", maxOwnerIdLength=" + maxOwnerIdLength
				+ ", fragmetLegth=" + fragmentLegth + "]";
	}

}
