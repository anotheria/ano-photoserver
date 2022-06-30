package net.anotheria.anosite.photoserver.api.photo.fs;

import net.anotheria.anoprise.dualcrud.CrudService;
import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.ItemNotFoundException;
import net.anotheria.anoprise.dualcrud.Query;
import net.anotheria.anoprise.dualcrud.SaveableID;
import net.anotheria.anosite.photoserver.api.photo.PhotoFileHolder;
import net.anotheria.anosite.photoserver.api.photo.PhotoStorageUtil;
import net.anotheria.anosite.photoserver.service.storage.StorageConfig;
import net.anotheria.util.StringUtils;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Store photo in FS system on two folders.
 *
 * @author ykalapusha
 */
public class PhotoStorageToFoldersFSService implements CrudService<PhotoFileHolder> {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoStorageToFoldersFSService.class);
    /**
     * Lock manager for safe operations with files.
     */
    private static final IdBasedLockManager<String> LOCK_MANAGER = new SafeIdBasedLockManager<>();
    /**
     * Storage root first base folder.
     */
    private final String firstFolder;
    /**
     * Storage root second base folder.
     */
    private final String secondFolder;


    public PhotoStorageToFoldersFSService() {
        firstFolder = getCorrectPath(StorageConfig.getInstance().getStorageRoot());
        secondFolder = getCorrectPath(StorageConfig.getInstance().getStorageRootSecond());
    }

    @Override
    public PhotoFileHolder create(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream bais = null;
        try {
            baos = new ByteArrayOutputStream();
            IOUtils.copyLarge(photoFileHolder.getPhotoFileInputStream(), baos);
            byte[] bytes = baos.toByteArray();
            bais = new ByteArrayInputStream(bytes);
            photoFileHolder.setPhotoFileInputStream(bais);
            photoFileHolder.setFileLocation(photoFileHolder.getFileLocation().replace(firstFolder, secondFolder));
            saveFileInFS(photoFileHolder);

            if (!StorageConfig.getInstance().isUseSecondStorageRootOnly()) {
                photoFileHolder.setPhotoFileInputStream(new ByteArrayInputStream(bytes));
                photoFileHolder.setFileLocation(photoFileHolder.getFileLocation().replace(secondFolder, firstFolder));
                saveFileInFS(photoFileHolder);
            }
            return photoFileHolder;
        } catch (Exception e) {
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(baos);
            IOUtils.closeQuietly(bais);
            IOUtils.closeQuietly(photoFileHolder.getPhotoFileInputStream());
        }
    }

    @Override
    public PhotoFileHolder read(SaveableID id) throws CrudServiceException, ItemNotFoundException {
        String firstLocationPath = id.getSaveableId();
        String secondLocationPath = firstLocationPath.replace(firstFolder, secondFolder);

        PhotoFileHolder photoFileHolder = readFromFS(secondLocationPath, id);
        if (photoFileHolder != null)
            return photoFileHolder;

        photoFileHolder = readFromFS(firstLocationPath, id);

        if (photoFileHolder == null) {
            throw new ItemNotFoundException("Item [" + id + "] not found.");
        }
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream bais = null;
        try {
           baos = new ByteArrayOutputStream();
           IOUtils.copyLarge(photoFileHolder.getPhotoFileInputStream(), baos);
           byte[] bytes = baos.toByteArray();
           bais = new ByteArrayInputStream(bytes);
           if (StorageConfig.getInstance().isUseSecondStorageRootOnly())
               removePhoto(photoFileHolder);                //we will remove photo from first persistence

           photoFileHolder.setPhotoFileInputStream(bais);
           photoFileHolder.setFileLocation(photoFileHolder.getFileLocation().replace(firstFolder, secondFolder));
           saveFileInFS(photoFileHolder);
           photoFileHolder.setPhotoFileInputStream(new ByteArrayInputStream(bytes));
           return photoFileHolder;
        } catch (Exception e) {
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(baos);
            IOUtils.closeQuietly(bais);
        }
    }

    @Override
    public PhotoFileHolder update(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return create(photoFileHolder);
    }

    @Override
    public void delete(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        removePhoto(photoFileHolder);
        photoFileHolder.setFileLocation(photoFileHolder.getFileLocation().replace(firstFolder, secondFolder));
        removePhoto(photoFileHolder);
    }

    @Override
    public PhotoFileHolder save(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return create(photoFileHolder);
    }

    @Override
    public boolean exists(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        String path = photoFileHolder.getFilePath().replace(firstFolder, secondFolder);
        return new File(path).exists();
    }

    @Override
    public List<PhotoFileHolder> query(Query q) throws CrudServiceException {
        return null;
    }

    private void saveFileInFS(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        File file = new File(photoFileHolder.getFileLocation());
        if (!file.exists())
            if (!file.mkdirs())
                throw new CrudServiceException("writePhoto([ " + photoFileHolder + "]) fail. Can't create needed folder structure.");

        String fileName = photoFileHolder.getFilePath();
        if (StringUtils.isEmpty(fileName))
            throw new CrudServiceException("Wrong photo file name[" + fileName + "].");

        file = new File(fileName);
        removePhoto(photoFileHolder);
        IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(fileName);
        lock.lock();

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            IOUtils.copyLarge(photoFileHolder.getPhotoFileInputStream(), out);
            out.flush();
        } catch (IOException ioe) {
            String message = "writePhoto(InputStream, " + photoFileHolder + ") fail.";
            LOGGER.error(message, ioe);
            throw new CrudServiceException(message, ioe);
        } finally {
            IOUtils.closeQuietly(out);
            IOUtils.closeQuietly(photoFileHolder.getPhotoFileInputStream());
            lock.unlock();
        }
    }

    public PhotoFileHolder readFromFS(String fileName, SaveableID id) {
        File file = new File(fileName);
        if (!file.exists() || file.isDirectory())
            return null;

        try {
            PhotoFileHolder photoFileHolder = new PhotoFileHolder(PhotoStorageUtil.getId(id.getOwnerId()), PhotoStorageUtil.getOriginalId(id.getOwnerId()), PhotoStorageUtil.getExtension(id.getOwnerId()));
            photoFileHolder.setPhotoFileInputStream(new FileInputStream(file));
            photoFileHolder.setFileLocation(file.getPath().substring(0, file.getPath().lastIndexOf(File.separator) + 1));
            return photoFileHolder;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    private void removePhoto(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        String fileName = photoFileHolder.getFilePath();
        if (StringUtils.isEmpty(fileName))
            throw new RuntimeException("Wrong photo file name[" + fileName + "].");

        File file = new File(fileName);
        if (file.exists() && file.isDirectory()) {
            String message = "removePhoto(" + photoFileHolder + ") fail. File is a folder.";
            LOGGER.error(message);
            throw new CrudServiceException(message);
        }

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

    private String getCorrectPath(String path) {
        String lastChar = path.substring(path.length() - 1);
        if (!lastChar.equals(File.separator))
            path += File.separator;

        return path;
    }
}
