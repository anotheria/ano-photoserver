package net.anotheria.anosite.photoserver.service.storage.persistence.ceph;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import net.anotheria.anoprise.dualcrud.CrudService;
import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.ItemNotFoundException;
import net.anotheria.anoprise.dualcrud.Query;
import net.anotheria.anoprise.dualcrud.SaveableID;
import net.anotheria.anosite.photoserver.service.storage.persistence.PhotoFileHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        InputStream inputStream = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(photoFileHolder.getPhotoFile());
            oos.flush();
            oos.close();
            inputStream = new ByteArrayInputStream(baos.toByteArray());
            amazonS3Connection.putObject(bucketName, photoFileHolder.getOwnerId() + ".dat", inputStream, new ObjectMetadata());
            return photoFileHolder;
        } catch (IOException e) {
            LOGGER.error("Input/Output Exception: " + e.getMessage(), e);
            throw new CrudServiceException(e.getMessage(), e);
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException e) {
                LOGGER.error("Unable to close input stream " + e.getMessage(), e);
            }
        }
    }

    @Override
    public PhotoFileHolder read(SaveableID id) throws CrudServiceException, ItemNotFoundException {
        ObjectInputStream inputStream = null;
        try {
            final S3Object s3Object = amazonS3Connection.getObject(bucketName, id.getOwnerId() + ".dat");
            S3ObjectInputStream content = s3Object.getObjectContent();
            inputStream = new ObjectInputStream(content);
            File file = (File) inputStream.readObject();
            PhotoFileHolder photoFileHolder = new PhotoFileHolder();
            photoFileHolder.setId(Long.parseLong(id.getOwnerId()));
            photoFileHolder.setPhotoFile(file);
            return photoFileHolder;
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("Unable to read object", e);
            throw new CrudServiceException("Unable to read object", e);
        } catch (AmazonS3Exception e) {
           if (e.getErrorCode().equals("NoSuchKey"))
               throw new ItemNotFoundException(e.getMessage());

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
    public PhotoFileHolder update(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        return create(photoFileHolder);
    }

    @Override
    public void delete(PhotoFileHolder photoFileHolder) throws CrudServiceException {
        amazonS3Connection.deleteObject(bucketName, photoFileHolder.getOwnerId() + ".dat");
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
