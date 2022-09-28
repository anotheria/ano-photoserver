package net.anotheria.anosite.photoserver.api.photo.google.cloud;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageClass;
import com.google.cloud.storage.StorageException;
import com.google.cloud.storage.StorageOptions;
import net.anotheria.anoprise.dualcrud.CrudService;
import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.ItemNotFoundException;
import net.anotheria.anoprise.dualcrud.Query;
import net.anotheria.anoprise.dualcrud.SaveableID;
import net.anotheria.anosite.photoserver.api.photo.PhotoFileHolder;
import net.anotheria.anosite.photoserver.api.photo.PhotoStorageUtil;
import net.anotheria.anosite.photoserver.service.storage.StorageConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Google cloud storage service for photos.
 *
 * @author ykalapusha
 */
public class PhotoGoogleCloudStorageService implements CrudService<PhotoFileHolder> {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoGoogleCloudStorageService.class);
    /**
     * {@link Storage} instance.
     */
    private final Storage storage;

    public PhotoGoogleCloudStorageService() {
        storage = StorageOptions.newBuilder().setProjectId(PhotoGoogleCloudStorageConfig.getInstance().getProjectId()).build().getService();
        initializeBuckets();
    }

    @Override
    public PhotoFileHolder create(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        BlobId blobId = BlobId.of(getBucket(photoFileHolder), getFilePath(photoFileHolder));
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        try {
            storage.createFrom(blobInfo, photoFileHolder.getPhotoFileInputStream());
        } catch (IOException e) {
            throw new CrudServiceException(e.getMessage(), e);
        }
        return photoFileHolder;
    }

    @Override
    public PhotoFileHolder save(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return create(photoFileHolder);
    }

    @Override
    public PhotoFileHolder update(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return create(photoFileHolder);
    }

    @Override
    public PhotoFileHolder read(SaveableID id) throws CrudServiceException, ItemNotFoundException {
        String userId = id.getSaveableId().split("______USER_ID______")[1];
        PhotoFileHolder photoFileHolder = new PhotoFileHolder(PhotoStorageUtil.getId(id.getOwnerId()), PhotoStorageUtil.getOriginalId(id.getOwnerId()), PhotoStorageUtil.getExtension(id.getOwnerId()), userId);
        try {
            byte[] content = storage.readAllBytes(getBucket(photoFileHolder), getFilePath(photoFileHolder));
            photoFileHolder.setPhotoFileInputStream(new ByteArrayInputStream(content));
            photoFileHolder.setFileLocation(StorageConfig.getFolderPathSecond(photoFileHolder.getUserId()));
            return photoFileHolder;
        } catch (StorageException e) {
            throw new ItemNotFoundException("Element not found");
        } catch (Exception e) {
            throw new CrudServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void delete(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        storage.delete(getBucket(photoFileHolder), getFilePath(photoFileHolder));
    }

    @Override
    public boolean exists(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return Objects.nonNull(storage.get(getBucket(photoFileHolder), getFilePath(photoFileHolder)));
    }

    @Override
    public List<PhotoFileHolder> query(Query q) throws CrudServiceException {
        return null;
    }


    private void initializeBuckets() {
        initializeBucket(PhotoGoogleCloudStorageConfig.getInstance().getOriginalBucketName());
        initializeBucket(PhotoGoogleCloudStorageConfig.getInstance().getScaledBucketName());
    }

    private void initializeBucket(String bucketName) {
        Bucket bucket = storage.get(bucketName, Storage.BucketGetOption.fields(Storage.BucketField.NAME));
        if (bucket == null) {
            //create bucket
            bucket = storage.create(BucketInfo.newBuilder(bucketName)
                            .setStorageClass(StorageClass.STANDARD)
                            .setLocation("EU")
                            .build());
            LOGGER.info("Bucket created: " + bucket.toString());
        }
    }

    private String getBucket(PhotoFileHolder pfh) {
        PhotoGoogleCloudStorageConfig config = PhotoGoogleCloudStorageConfig.getInstance();
        return pfh.getId().equals(String.valueOf(pfh.getOriginalPhotoId())) ? config.getOriginalBucketName() : config.getScaledBucketName();
    }

    private String getFilePath(PhotoFileHolder pfh) {
        return StorageConfig.getFolderPathSecond(pfh.getUserId()) + pfh.getOwnerId();
    }
}
