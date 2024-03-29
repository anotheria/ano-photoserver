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
import org.apache.commons.io.FileUtils;
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
import java.nio.file.Files;
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
     * Default constructor.
     */
    public PhotoStorageToFoldersFSService() {

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
            saveFileInFS(photoFileHolder);

            if (!StorageConfig.getInstance().isUseSecondStorageRootOnly()) {
                photoFileHolder.setPhotoFileInputStream(new ByteArrayInputStream(bytes));
                String fileLocation = StorageConfig.getStorageFolderPathSecond(photoFileHolder.getUserId());
                photoFileHolder.setFileLocation(fileLocation);
                saveFileInFS(photoFileHolder);
            }
            return photoFileHolder;
        } catch (Exception e) {
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(baos);
            IOUtils.closeQuietly(bais);
        }
    }

    @Override
    public PhotoFileHolder read(SaveableID id) throws CrudServiceException, ItemNotFoundException {
        String userId = id.getSaveableId().split("______USER_ID______")[1];
        String firstLocationPath = StorageConfig.getStoreFolderPathFirst(userId);
        String secondLocationPath = StorageConfig.getStorageFolderPathSecond(userId);

        PhotoFileHolder photoFileHolder = readFromFS(secondLocationPath, id);
        if (photoFileHolder != null)
            return photoFileHolder;

        photoFileHolder = readFromFS(firstLocationPath, id);

        if (photoFileHolder == null)
            throw new ItemNotFoundException("Item [" + id + "] not found.");

        saveOriginalPhotoIfNeeded(photoFileHolder);

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
           photoFileHolder.setFileLocation(secondLocationPath);
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
        if (!StorageConfig.getInstance().isUseSecondStorageRootOnly()) {
            photoFileHolder.setFileLocation(StorageConfig.getStorageFolderPathSecond(photoFileHolder.getUserId()));
            removePhoto(photoFileHolder);
        }
    }

    @Override
    public PhotoFileHolder save(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return create(photoFileHolder);
    }

    @Override
    public boolean exists(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        String path = StorageConfig.getStorageFolderPathSecond(photoFileHolder.getUserId());
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
            lock.unlock();
        }
    }

    public PhotoFileHolder readFromFS(String filePath, SaveableID id) {
        File file = new File(filePath + id.getOwnerId());
        if (!file.exists() || file.isDirectory())
            return null;

        try {
            String userId = id.getSaveableId().split("______USER_ID______")[1];
            PhotoFileHolder photoFileHolder = new PhotoFileHolder(PhotoStorageUtil.getId(id.getOwnerId()), PhotoStorageUtil.getOriginalId(id.getOwnerId()), PhotoStorageUtil.getExtension(id.getOwnerId()), userId);
            photoFileHolder.setPhotoFileInputStream(Files.newInputStream(file.toPath()));
            photoFileHolder.setFileLocation(filePath);
            return photoFileHolder;
        } catch (IOException e) {
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

    private void saveOriginalPhotoIfNeeded(PhotoFileHolder photoFileHolder) {
        try {
            if (photoFileHolder.getId().equals(String.valueOf(photoFileHolder.getOriginalPhotoId())))
                return;

            File dist = new File(StorageConfig.getStorageFolderPathSecond(photoFileHolder.getUserId()) + photoFileHolder.getOriginalPhotoId() + photoFileHolder.getExtension());
            if (dist.exists())
                return;

            File src = new File(StorageConfig.getStoreFolderPathFirst(photoFileHolder.getUserId()) + photoFileHolder.getOriginalPhotoId() + photoFileHolder.getExtension());
            FileUtils.copyFile(src, dist);
            if (StorageConfig.getInstance().isUseSecondStorageRootOnly())
                removeCachedVersions(StorageConfig.getStoreFolderPathFirst(photoFileHolder.getUserId()), String.valueOf(photoFileHolder.getOriginalPhotoId()));

        } catch (Exception any) {
            LOGGER.warn("Unable to process user:{} original photo. {}", photoFileHolder.getUserId(), any.getMessage());
        }
    }
}
