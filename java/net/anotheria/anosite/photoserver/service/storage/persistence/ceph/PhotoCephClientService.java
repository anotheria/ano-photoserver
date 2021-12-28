package net.anotheria.anosite.photoserver.service.storage.persistence.ceph;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anosite.photoserver.service.storage.PhotoBO;
import net.anotheria.anosite.photoserver.service.storage.persistence.AbstractStoragePersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * Implementation of {@link AbstractStoragePersistenceService} for ceph service for store photos.
 *
 * @author ykalapusha
 */
public class PhotoCephClientService extends AbstractStoragePersistenceService {
    /**
     * {@link Logger} instance.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoCephClientService.class);
    /**
     * Persistence table name.
     */
    private static final String TABLE_NAME = "t_photos_ceph";
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
        super(TABLE_NAME, LOGGER);
        PhotoCephClientConfig cephClientConfig = PhotoCephClientConfig.getInstance();
        bucketName = cephClientConfig.getBucket();
        AWSCredentials credentials = new BasicAWSCredentials(cephClientConfig.getAccessKey(), cephClientConfig.getSecretKey());
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);
        amazonS3Connection = new AmazonS3Client(credentials, clientConfig);
        amazonS3Connection.setEndpoint(cephClientConfig.getEndpoint());
    }

    @Override
    protected void createPhotoInStorage(PhotoBO photoBO) throws CrudServiceException {
        amazonS3Connection.putObject(bucketName, photoBO.getOwnerId() + ".dat", photoBO.getPhotoFile());
    }

    @Override
    protected File getPhotoFromStorage(PhotoBO photoBO) throws CrudServiceException {
        ObjectInputStream inputStream = null;
        try {
            final S3Object s3Object = amazonS3Connection.getObject(bucketName, photoBO.getOwnerId() + ".dat");
            S3ObjectInputStream content = s3Object.getObjectContent();
            inputStream = new ObjectInputStream(content);
            return (File) inputStream.readObject();
        }catch (IOException | ClassNotFoundException e) {
            LOGGER.error("Unable to read object", e);
            throw new CrudServiceException("Unable to read object", e);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                LOGGER.error("Unable to close ObjectInputStream stream " + e.getMessage(), e);
            }
        }
    }

    @Override
    protected String getFileLocation(PhotoBO photoBO) {
        return "cephStorage:" + photoBO.getOwnerId() + ".dat";
    }

    @Override
    protected void deletePhotoFromStorage(PhotoBO photoBO) {
        amazonS3Connection.deleteObject(bucketName, photoBO.getOwnerId() + ".dat");
    }
}
