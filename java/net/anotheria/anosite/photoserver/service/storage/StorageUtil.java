package net.anotheria.anosite.photoserver.service.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;
import net.anotheria.util.StringUtils;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for operating with photos on file system. All synchronizations based on file names.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public final class StorageUtil {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(StorageUtil.class);

	/**
	 * Lock manager for safe operations with files.
	 */
	private static final IdBasedLockManager LOCK_MANAGER = new SafeIdBasedLockManager();

	/**
	 * Default constructor.
	 */
	private StorageUtil() {
		throw new IllegalAccessError();
	}

	/**
	 * Write photo to file system persistence. Incoming input stream will be closed inside at and this operation.
	 *
	 * InputStream, photo and extension can't be null or empty.
	 *
	 * @param tempFile
	 *            - {@link java.io.File} where uploaded photo is temporarily stored before storing it in real storage
	 * @param photo
	 *            - photo information
	 * @param overwrite
	 *            - <code>true</code> if we can overwrite or <code>false</code>
	 * @throws net.anotheria.anosite.photoserver.service.storage.StorageUtilException if any.
	 */
	public static void writePhoto(final File tempFile, final PhotoVO photo, final boolean overwrite) throws StorageUtilException {
		if (tempFile == null)
			throw new IllegalArgumentException("File is null.");

		checkArguments(photo);

		File file = new File(photo.getFileLocation());
		// checking folder structure and creating if needed
		if (!file.exists())
			if (!file.mkdirs())
				throw new StorageUtilException("writePhoto(InputStream, " + photo + ", " + overwrite + ") fail. Can't create needed folder structure.");

		String fileName = photo.getFilePath();
		// checking file name
		if (StringUtils.isEmpty(fileName))
			throw new RuntimeException("Wrong photo file name[" + fileName + "].");

		file = new File(fileName);
		// preventing photo overwriting if this operation not permitted
		if (file.exists() && !overwrite)
			throw new StorageUtilException("writePhoto(InputStream, " + photo + ", " + overwrite + ") fail. Photo already exist.");

		// removing previously stored file if it exist
		removePhoto(photo, true);

		// synchronizing on fileName for preventing concurrent modifications on same file
		IdBasedLock lock = LOCK_MANAGER.obtainLock(fileName);
		lock.lock();

		FileOutputStream out = null;
		FileInputStream is = null;
		try {
			out = new FileOutputStream(file);
			is = new FileInputStream(tempFile);
			// buffered copy from input to output
			IOUtils.copyLarge(is, out);
			out.flush();
		} catch (IOException ioe) {
			String message = "writePhoto(InputStream, " + photo + ", " + overwrite + ") fail.";
			LOG.error(message, ioe);
			throw new StorageUtilException(message, ioe);
		} finally {
			// closing output with ignoring exceptions
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(is);
			// closing synchronization
			lock.unlock();
		}
	}

	/**
	 * Read photo from the file system. Don't forget close result {@link java.io.InputStream} after finishing work with it. Photo and extension can't be null or
	 * empty.
	 *
	 * @param photo
	 *            - photo information
	 * @return {@link java.io.InputStream}
	 * @throws net.anotheria.anosite.photoserver.service.storage.StorageUtilException if any.
	 */
	public static InputStream getPhoto(final PhotoVO photo) throws StorageUtilException {
		checkArguments(photo);

		String fileName = photo.getFilePath();
		// checking file name
		if (StringUtils.isEmpty(fileName))
			throw new RuntimeException("Wrong photo file name[" + fileName + "].");

		File file = new File(fileName);
		// checking is photo file exist
		if (!file.exists() || file.isDirectory())
			throw new StorageUtilException("getPhoto(" + photo + ") fail. Photo not exist or it a directory.");

		try {
			return new FileInputStream(fileName);
		} catch (FileNotFoundException fnfe) {
			String message = "getPhoto(" + photo + ") fail. File not found.";
			LOG.error(message, fnfe);
			throw new StorageUtilException(message, fnfe);
		}
	}

	/**
	 * Remove photo file from the file system. Photo and extension can't be null or empty.
	 *
	 * @param photo
	 *            - photo information
	 * @param ignoreIfNotExist
	 *            - ignoring if file not exist or throwing exception
	 * @throws net.anotheria.anosite.photoserver.service.storage.StorageUtilException if any.
	 */
	public static void removePhoto(final PhotoVO photo, final boolean ignoreIfNotExist) throws StorageUtilException {
		checkArguments(photo);

		String fileName = photo.getFilePath();
		// checking file name
		if (StringUtils.isEmpty(fileName))
			throw new RuntimeException("Wrong photo file name[" + fileName + "].");

		File file = new File(fileName);
		// checking is a file not a directory
		if (file.exists() && file.isDirectory()) {
			String message = "removePhoto(" + photo + ", " + ignoreIfNotExist + ") fail. File is a folder.";
			LOG.error(message);
			throw new StorageUtilException(message);
		}

		// synchronizing on fileName for preventing concurrent modifications on same file
		IdBasedLock lock = LOCK_MANAGER.obtainLock(fileName);
		lock.lock();

		try {
			// checking is need throw exception if file not exist
			if (!file.exists() && !ignoreIfNotExist) {
				String message = "removePhoto(" + photo + ", " + ignoreIfNotExist + ") fail. File not found.";
				LOG.error(message);
				throw new StorageUtilException(message);
			}

			file.delete();
			removeCachedVersions(photo.getFileLocation(), String.valueOf(photo.getId()) + photo.getExtension());
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Remove photo cached version's.
	 * 
	 * @param location
	 *            - original photo location
	 * @param fileName
	 *            - original photo file name
	 */
	private static void removeCachedVersions(final String location, final String fileName) {
		File dir = new File(location);
		if (!dir.isDirectory()) {
			LOG.warn("Location[" + location + "] not a directoroy or not exist.");
			return;
		}

		String[] files = dir.list();
		if (files == null || files.length == 0)
			return;

		for (String file : files)
			if (file.startsWith(fileName)) {
				File toDelete = new File(location + File.separator + file);
				if (toDelete.isDirectory())
					continue;

				toDelete.delete();
			}
	}

	/**
	 * Internal method for validating arguments.
	 * 
	 * @param photo
	 *            - photo information
	 * @param extension
	 *            - photo file extension
	 */
	private static void checkArguments(final PhotoVO photo) {
		if (photo == null)
			throw new IllegalArgumentException("PhotoVO is null.");

		if (StringUtils.isEmpty(photo.getFileLocation()))
			throw new IllegalArgumentException("PhotoVO.fileLocation is empty.");
	}

}
