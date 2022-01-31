package net.anotheria.anosite.photoserver.api.photo.ceph;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import net.anotheria.anoprise.dualcrud.CrudService;
import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.ItemNotFoundException;
import net.anotheria.anoprise.dualcrud.Query;
import net.anotheria.anoprise.dualcrud.SaveableID;
import net.anotheria.anosite.photoserver.api.photo.PhotoFileHolder;
import net.anotheria.anosite.photoserver.api.photo.StorageUtil;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Implementation for ceph service for store photos.
 *
 * @author ykalapusha
 */
public class PhotoCephClientService implements CrudService<PhotoFileHolder> {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoCephClientService.class);
    /**
     * Lock manager for safe operations with files.
     */
    private static final IdBasedLockManager<String> LOCK_MANAGER = new SafeIdBasedLockManager<>();
    /**
     * {@link AmazonS3} client.
     */
    private final AmazonS3 amazonS3Connection;
    /**
     * Bucket name.
     */
    private final String bucketName;

    /**
     * Constructor.
     */
    public PhotoCephClientService() {
        PhotoCephClientConfig cephClientConfig = PhotoCephClientConfig.getInstance();
        bucketName = cephClientConfig.getBucket();
        AWSCredentials credentials = new BasicAWSCredentials(cephClientConfig.getAccessKey(), cephClientConfig.getSecretKey());
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);
        amazonS3Connection = new AmazonS3Client(credentials, clientConfig);
        amazonS3Connection.setEndpoint(cephClientConfig.getEndpoint());
    }

    @Override
    public PhotoFileHolder create(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(photoFileHolder.getOwnerId());
        lock.lock();
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, photoFileHolder.getOwnerId(), photoFileHolder.getPhotoFileInputStream(), new ObjectMetadata());
            amazonS3Connection.putObject(putObjectRequest);
            return photoFileHolder;
        } catch (Exception e) {
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(photoFileHolder.getPhotoFileInputStream());
            lock.unlock();
        }
    }

    @Override
    public PhotoFileHolder read(SaveableID id) throws CrudServiceException, ItemNotFoundException {
        IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(id.getOwnerId());
        lock.lock();
        try {
            final S3Object s3Object = amazonS3Connection.getObject(bucketName, id.getOwnerId());
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            PhotoFileHolder photoFileHolder = new PhotoFileHolder(StorageUtil.getId(id.getOwnerId()), StorageUtil.getExtension(id.getOwnerId()));
            photoFileHolder.setPhotoFileInputStream(inputStream);
            return photoFileHolder;
        } catch (AmazonS3Exception e) {
            if (e.getErrorCode().equals("NoSuchKey"))
                throw new ItemNotFoundException(e.getMessage());

            LOGGER.error("Unable to read object", e);
            throw new CrudServiceException("Unable to read object", e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public PhotoFileHolder update(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return create(photoFileHolder);
    }

    @Override
    public void delete(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        amazonS3Connection.deleteObject(bucketName, photoFileHolder.getOwnerId());
    }

    @Override
    public PhotoFileHolder save(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return create(photoFileHolder);
    }

    @Override
    public boolean exists(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return amazonS3Connection.doesObjectExist(bucketName, photoFileHolder.getOwnerId());
    }

    @Override
    public List<PhotoFileHolder> query(Query q) throws CrudServiceException {
        return null;
    }
}