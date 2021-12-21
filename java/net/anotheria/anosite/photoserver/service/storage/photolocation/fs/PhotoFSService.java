package net.anotheria.anosite.photoserver.service.storage.photolocation.fs;

import net.anotheria.anoprise.dualcrud.CrudService;
import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.ItemNotFoundException;
import net.anotheria.anoprise.dualcrud.Query;
import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;
import net.anotheria.util.StringUtils;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * File system configuration for store photos.
 *
 * @author ykalapusha
 */
public class PhotoFSService implements CrudService<PhotoVO> {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoFSService.class);
    /**
     * Lock manager for safe operations with files.
     */
    private static final IdBasedLockManager<String> LOCK_MANAGER = new SafeIdBasedLockManager<>();

    @Override
    public PhotoVO create(PhotoVO photoVO) throws CrudServiceException {
        return writePhoto(photoVO, false);
    }

    @Override
    public PhotoVO save(PhotoVO photoVO) throws CrudServiceException {
        return writePhoto(photoVO, true);
    }

    @Override
    public PhotoVO update(PhotoVO photoVO) throws CrudServiceException {
        return writePhoto(photoVO, true);
    }

    @Override
    public PhotoVO read(String ownerId) throws CrudServiceException, ItemNotFoundException {
        return null;
    }

    @Override
    public void delete(PhotoVO photoVO) throws CrudServiceException {
        checkArguments(photoVO);

        String fileName = photoVO.getFilePath();
        // checking file name
        if (StringUtils.isEmpty(fileName))
            throw new RuntimeException("Wrong photo file name[" + fileName + "].");

        File file = new File(fileName);
        // checking is a file not a directory
        if (file.exists() && file.isDirectory()) {
            String message = "removePhoto(" + photoVO + ",) fail. File is a folder.";
            LOGGER.error(message);
            throw new CrudServiceException(message);
        }

        // synchronizing on fileName for preventing concurrent modifications on same file
        IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(fileName);
        lock.lock();

        try {
            if (file.exists()) {
                file.delete();
                removeCachedVersions(photoVO.getFileLocation(), (photoVO.getId() + photoVO.getExtension()));
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean exists(PhotoVO photoVO) throws CrudServiceException {
        return false;
    }

    @Override
    public List<PhotoVO> query(Query q) throws CrudServiceException {
        return null;
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
            LOGGER.warn("Location[" + location + "] not a directoroy or not exist.");
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
     */
    private void checkArguments(final PhotoVO photo) {
        if (photo == null)
            throw new IllegalArgumentException("PhotoVO is null.");

        if (StringUtils.isEmpty(photo.getFileLocation()))
            throw new IllegalArgumentException("PhotoVO.fileLocation is empty.");
    }

    public PhotoVO writePhoto(final PhotoVO photoVO, final boolean overwrite) throws CrudServiceException {
        if (photoVO.getTempFile() == null)
            throw new IllegalArgumentException("File is null.");

        checkArguments(photoVO);

        File file = new File(photoVO.getFileLocation());
        // checking folder structure and creating if needed
        if (!file.exists())
            if (!file.mkdirs())
                throw new CrudServiceException("writePhoto(InputStream, " + photoVO + ", " + overwrite + ") fail. Can't create needed folder structure.");

        String fileName = photoVO.getFilePath();
        // checking file name
        if (StringUtils.isEmpty(fileName))
            throw new CrudServiceException("Wrong photo file name[" + fileName + "].");

        file = new File(fileName);
        // preventing photo overwriting if this operation not permitted
        if (file.exists() && !overwrite)
            throw new CrudServiceException("writePhoto(InputStream, " + photoVO + ", " + overwrite + ") fail. Photo already exist.");

        // removing previously stored file if it exist
        delete(photoVO);

        // synchronizing on fileName for preventing concurrent modifications on same file
        IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(fileName);
        lock.lock();

        FileOutputStream out = null;
        FileInputStream is = null;
        try {
            out = new FileOutputStream(file);
            is = new FileInputStream(photoVO.getTempFile());
            // buffered copy from input to output
            IOUtils.copyLarge(is, out);
            out.flush();
        } catch (IOException ioe) {
            String message = "writePhoto(InputStream, " + photoVO + ", " + overwrite + ") fail.";
            LOGGER.error(message, ioe);
            throw new CrudServiceException(message, ioe);
        } finally {
            // closing output with ignoring exceptions
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(is);
            // closing synchronization
            lock.unlock();
        }
        return photoVO;
    }
}
