package net.anotheria.anosite.photoserver.api.photo.fs;

import net.anotheria.anoprise.dualcrud.CrudService;
import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.ItemNotFoundException;
import net.anotheria.anoprise.dualcrud.Query;
import net.anotheria.anoprise.dualcrud.SaveableID;
import net.anotheria.anosite.photoserver.api.photo.PhotoFileHolder;
import net.anotheria.anosite.photoserver.api.photo.PhotoStorageUtil;
import net.anotheria.util.StringUtils;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Store photo in FS system.
 *
 * @author ykalapusha
 */
public class PhotoStorageFSService implements CrudService<PhotoFileHolder> {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoStorageFSService.class);
    /**
     * Lock manager for safe operations with files.
     */
    private static final IdBasedLockManager<String> LOCK_MANAGER = new SafeIdBasedLockManager<>();


    @Override
    public PhotoFileHolder create(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        String fileLocation = photoFileHolder.getFileLocation();
        File file = new File(fileLocation);
        // checking folder structure and creating if needed
        if (!file.exists())
            if (!file.mkdirs())
                throw new CrudServiceException("writePhoto([ " + photoFileHolder + "]) fail. Can't create needed folder structure.");

        String fileName = photoFileHolder.getFilePath();
        // checking file name
        if (StringUtils.isEmpty(fileName))
            throw new CrudServiceException("Wrong photo file name[" + fileName + "].");

        file = new File(fileName);
        // removing previously stored file if it exist
        removePhoto(photoFileHolder);

        // synchronizing on fileName for preventing concurrent modifications on same file
        IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(fileName);
        lock.lock();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            // buffered copy from input to output
            IOUtils.copyLarge(photoFileHolder.getPhotoFileInputStream(), out);
            out.flush();
        } catch (IOException ioe) {
            String message = "writePhoto(InputStream, " + photoFileHolder + ") fail.";
            LOGGER.error(message, ioe);
            throw new CrudServiceException(message, ioe);
        } finally {
            // closing output with ignoring exceptions
            IOUtils.closeQuietly(out);
            // closing synchronization
            lock.unlock();
        }
        return photoFileHolder;
    }

    @Override
    public PhotoFileHolder read(SaveableID id) throws CrudServiceException, ItemNotFoundException {
        String fileName = id.getSaveableId().split("______USER_ID______")[0];
        // checking file name
        if (StringUtils.isEmpty(fileName))
            throw new CrudServiceException("Wrong photo file name[" + fileName + "].");

        File file = new File(fileName);
        // checking is photo file exist
        if (!file.exists() || file.isDirectory())
            throw new ItemNotFoundException("getPhoto(" + id + ") fail. Photo not exist or it a directory.");


        try {
            String userId = id.getSaveableId().split("______USER_ID______")[1];
            PhotoFileHolder photoFileHolder = new PhotoFileHolder(PhotoStorageUtil.getId(id.getOwnerId()), PhotoStorageUtil.getOriginalId(id.getOwnerId()), PhotoStorageUtil.getExtension(id.getOwnerId()), userId);
            photoFileHolder.setPhotoFileInputStream(Files.newInputStream(file.toPath()));
            return photoFileHolder;
        } catch (IOException e) {
            throw new ItemNotFoundException("Item [" + id + "] not found.");
        }
    }

    @Override
    public PhotoFileHolder update(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return create(photoFileHolder);
    }

    @Override
    public void delete(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        removePhoto(photoFileHolder);
    }

    @Override
    public PhotoFileHolder save(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return create(photoFileHolder);
    }

    @Override
    public boolean exists(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return new File(photoFileHolder.getFilePath()).exists();
    }

    @Override
    public List<PhotoFileHolder> query(Query q) throws CrudServiceException {
        return null;
    }

    public void removePhoto(final PhotoFileHolder photoFileHolder) throws CrudServiceException {
        String fileName = photoFileHolder.getFilePath();
        // checking file name
        if (StringUtils.isEmpty(fileName))
            throw new RuntimeException("Wrong photo file name[" + fileName + "].");

        File file = new File(fileName);
        // checking is a file not a directory
        if (file.exists() && file.isDirectory()) {
            String message = "removePhoto(" + photoFileHolder + ") fail. File is a folder.";
            LOGGER.error(message);
            throw new CrudServiceException(message);
        }

        // synchronizing on fileName for preventing concurrent modifications on same file
        IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(fileName);
        lock.lock();

        try {
            file.delete();
            removeCachedVersions(photoFileHolder.getFileLocation(), photoFileHolder.getId());
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
}
