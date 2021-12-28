package net.anotheria.anosite.photoserver.service.storage.persistence.fs;

import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anosite.photoserver.service.storage.PhotoBO;
import net.anotheria.anosite.photoserver.service.storage.persistence.AbstractStoragePersistenceService;
import net.anotheria.util.StringUtils;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;
import net.anotheria.util.log.LogMessageUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Implementation of {@link AbstractStoragePersistenceService} for FS system.
 *
 * @author ykalapusha
 */
public class PhotoStoragePersistenceService extends AbstractStoragePersistenceService {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoStoragePersistenceService.class);
    /**
     * Lock manager for safe operations with files.
     */
    private static final IdBasedLockManager<String> LOCK_MANAGER = new SafeIdBasedLockManager<>();
    /**
     * Table name for storing photos meta information.
     */
    static final String TABLE_NAME = "t_photos";

    /**
     * Constructor.
     */
    public PhotoStoragePersistenceService() {
        super(TABLE_NAME, LOGGER);
    }

    @Override
    protected void createPhotoInStorage(PhotoBO photoBO) throws CrudServiceException {
        checkArguments(photoBO);
        File file = new File(photoBO.getFileLocation());
        // checking folder structure and creating if needed
        if (!file.exists())
            if (!file.mkdirs())
                throw new CrudServiceException("writePhoto(InputStream, " + photoBO + ") fail. Can't create needed folder structure.");

        String fileName = photoBO.getFilePath();
        // checking file name
        if (StringUtils.isEmpty(fileName))
            throw new CrudServiceException("Wrong photo file name[" + fileName + "].");

        file = new File(fileName);
        // removing previously stored file if it exist
        removePhoto(photoBO);

        // synchronizing on fileName for preventing concurrent modifications on same file
        IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(fileName);
        lock.lock();

        FileOutputStream out = null;
        FileInputStream is = null;
        try {
            out = new FileOutputStream(file);
            is = new FileInputStream(photoBO.getPhotoFile());
            // buffered copy from input to output
            IOUtils.copyLarge(is, out);
            out.flush();
        } catch (IOException ioe) {
            String message = "writePhoto(InputStream, " + photoBO + ") fail.";
            LOGGER.error(message, ioe);
            throw new CrudServiceException(message, ioe);
        } finally {
            // closing output with ignoring exceptions
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(is);
            // closing synchronization
            lock.unlock();
        }
    }

    @Override
    protected File getPhotoFromStorage(PhotoBO photoBO) throws CrudServiceException {
        checkArguments(photoBO);

        String fileName = photoBO.getFilePath();
        // checking file name
        if (StringUtils.isEmpty(fileName))
            throw new CrudServiceException("Wrong photo file name[" + fileName + "].");

        File file = new File(fileName);
        // checking is photo file exist
        if (!file.exists() || file.isDirectory())
            throw new CrudServiceException("getPhoto(" + photoBO + ") fail. Photo not exist or it a directory.");

        return file;
    }

    @Override
    protected String getFileLocation(PhotoBO photoBO) {
        return photoBO.getFileLocation();
    }

    @Override
    protected void deletePhotoFromStorage(PhotoBO photoBO) {
        try {
            removePhoto(photoBO);
        } catch (CrudServiceException e) {
            LOGGER.error(LogMessageUtil.failMsg(e));
        }
    }

    public void removePhoto(final PhotoBO photo) throws CrudServiceException {
        checkArguments(photo);

        String fileName = photo.getFilePath();
        // checking file name
        if (StringUtils.isEmpty(fileName))
            throw new RuntimeException("Wrong photo file name[" + fileName + "].");

        File file = new File(fileName);
        // checking is a file not a directory
        if (file.exists() && file.isDirectory()) {
            String message = "removePhoto(" + photo + ") fail. File is a folder.";
            LOGGER.error(message);
            throw new CrudServiceException(message);
        }

        // synchronizing on fileName for preventing concurrent modifications on same file
        IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(fileName);
        lock.lock();

        try {
            file.delete();
            removeCachedVersions(photo.getFileLocation(), photo.getId() + photo.getExtension());
        } finally {
            lock.unlock();
        }
    }

    private void removeCachedVersions(final String location, final String fileName) {
        File dir = new File(location);
        if (!dir.isDirectory()) {
            LOGGER.warn("Location[" + location + "] not a directory or not exist.");
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

    private void checkArguments(final PhotoBO photo) {
        if (photo == null)
            throw new IllegalArgumentException("PhotoVO is null.");

        if (photo.getPhotoFile() == null)
            throw new IllegalArgumentException();

        if (StringUtils.isEmpty(photo.getFileLocation()))
            throw new IllegalArgumentException("PhotoVO.fileLocation is empty.");
    }
}
