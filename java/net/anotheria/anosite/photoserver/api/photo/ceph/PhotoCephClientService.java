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
import net.anotheria.moskito.core.dynamic.OnDemandStatsProducer;
import net.anotheria.moskito.core.registry.ProducerRegistryFactory;
import net.anotheria.util.concurrency.IdBasedLock;
import net.anotheria.util.concurrency.IdBasedLockManager;
import net.anotheria.util.concurrency.SafeIdBasedLockManager;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
     * {@link OnDemandStatsProducer} instance.
     */
    private final OnDemandStatsProducer<PhotoCephClientStats> statsProducer;

    /**
     * Constructor.
     */
    public PhotoCephClientService() {

        statsProducer = new OnDemandStatsProducer<>("CephPhotos", "business", "api", new PhotoCephClientStatsFactory());
        ProducerRegistryFactory.getProducerRegistryInstance().registerProducer(statsProducer);

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
        statsProducer.getDefaultStats().incPhotosToAdd();
        IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(photoFileHolder.getOwnerId());
        lock.lock();

        //first of all we try to write into the ceph storage, so we will read input stream and can not use it for second dual crud service, that`s why we create new one.
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            IOUtils.copyLarge(photoFileHolder.getPhotoFileInputStream(), baos);
            byte[] bytes = baos.toByteArray();

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(bytes.length);

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, photoFileHolder.getOwnerId(), photoFileHolder.getPhotoFileInputStream(), objectMetadata);
            amazonS3Connection.putObject(putObjectRequest);
            statsProducer.getDefaultStats().incAddedPhotos();
            return photoFileHolder;
        } catch (Exception e) {
            statsProducer.getDefaultStats().incCrudErrors();
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(baos);
            lock.unlock();
        }
    }

    @Override
    public PhotoFileHolder read(SaveableID id) throws CrudServiceException, ItemNotFoundException {
        statsProducer.getDefaultStats().incPhotosToRead();
        IdBasedLock<String> lock = LOCK_MANAGER.obtainLock(id.getOwnerId());
        lock.lock();
        try {
            final S3Object s3Object = amazonS3Connection.getObject(bucketName, id.getOwnerId());
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            String userId = id.getSaveableId().split("______USER_ID______")[1];
            PhotoFileHolder photoFileHolder = new PhotoFileHolder(PhotoStorageUtil.getId(id.getOwnerId()), PhotoStorageUtil.getOriginalId(id.getOwnerId()), PhotoStorageUtil.getExtension(id.getOwnerId()), userId);
            photoFileHolder.setPhotoFileInputStream(inputStream);
            statsProducer.getDefaultStats().incReadPhotos();
            return photoFileHolder;
        } catch (AmazonS3Exception e) {
            if (e.getErrorCode().equals("NoSuchKey")) {
                statsProducer.getDefaultStats().incNotFoundPhotos();
                throw new ItemNotFoundException(e.getMessage());
            }

            LOGGER.error("Unable to read object", e);
            statsProducer.getDefaultStats().incCrudErrors();
            throw new CrudServiceException("Unable to read object", e);
        } catch (IOException e) {
            throw new CrudServiceException(e.getMessage(), e);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public PhotoFileHolder update(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return create(photoFileHolder);
    }

    @Override
    public void delete(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        statsProducer.getDefaultStats().incPhotosToRemove();
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
            statsProducer.getDefaultStats().incRemovedPhotos();
        } catch (Exception e) {
            statsProducer.getDefaultStats().incCrudErrors();
            LOGGER.error("Unable to delete photo from ceph: " + e.getMessage());
        }
    }

    @Override
    public PhotoFileHolder save(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return create(photoFileHolder);
    }

    @Override
    public boolean exists(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        statsProducer.getDefaultStats().incIsPhotoExistsCheck();
        return amazonS3Connection.doesObjectExist(bucketName, photoFileHolder.getOwnerId());
    }

    @Override
    public List<PhotoFileHolder> query(Query q) throws CrudServiceException {
        return null;
    }
}
