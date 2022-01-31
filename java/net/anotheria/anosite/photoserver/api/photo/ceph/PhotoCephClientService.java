package net.anotheria.anosite.photoserver.api.photo.ceph;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import net.anotheria.anoprise.dualcrud.CrudService;
import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.ItemNotFoundException;
import net.anotheria.anoprise.dualcrud.Query;
import net.anotheria.anoprise.dualcrud.SaveableID;
import net.anotheria.anosite.photoserver.api.photo.PhotoFileHolder;
import net.anotheria.anosite.photoserver.api.photo.PhotoStorageUtil;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

        //first of all we try to write into the ceph storage, so we will read input stream and can not use it for second dual crud service, that`s why we create new one.
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream bais = null;
        try {
            baos = new ByteArrayOutputStream();
            IOUtils.copyLarge(photoFileHolder.getPhotoFileInputStream(), baos);
            byte[] bytes = baos.toByteArray();
            bais = new ByteArrayInputStream(bytes);

            photoFileHolder.setPhotoFileInputStream(bais);
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, photoFileHolder.getOwnerId(), photoFileHolder.getPhotoFileInputStream(), new ObjectMetadata());
            amazonS3Connection.putObject(putObjectRequest);
            photoFileHolder.setPhotoFileInputStream(new ByteArrayInputStream(bytes));
            return photoFileHolder;
        } catch (Exception e) {
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(photoFileHolder.getPhotoFileInputStream());
            IOUtils.closeQuietly(baos);
            IOUtils.closeQuietly(bais);
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
            PhotoFileHolder photoFileHolder = new PhotoFileHolder(PhotoStorageUtil.getId(id.getOwnerId()), PhotoStorageUtil.getOriginalId(id.getOwnerId()), PhotoStorageUtil.getExtension(id.getOwnerId()));
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
        try {
            amazonS3Connection.deleteObject(bucketName, photoFileHolder.getOwnerId());

            //delete cached versions
            ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
            listObjectsRequest.setBucketName(bucketName);
            listObjectsRequest.setPrefix(photoFileHolder.getOriginalPhotoId() + "_");

            ObjectListing objectListing = amazonS3Connection.listObjects(listObjectsRequest);
            for (S3ObjectSummary summary :objectListing.getObjectSummaries()) {
                amazonS3Connection.deleteObject(bucketName, summary.getKey());
            }
        } catch (Exception e) {
            LOGGER.error("Unable to delete photo from ceph: " + e.getMessage());
        }
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
