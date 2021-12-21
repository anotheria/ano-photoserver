package net.anotheria.anosite.photoserver.service.storage.photolocation.ceph;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import net.anotheria.anoprise.dualcrud.CrudService;
import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.ItemNotFoundException;
import net.anotheria.anoprise.dualcrud.Query;
import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;
import net.anotheria.util.log.LogMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * Ceph configuration for store photos.
 *
 * @author ykalapusha
 */
public class PhotoCephClientService implements CrudService<PhotoVO> {
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
        PhotoCephClientConfig cephConfig = PhotoCephClientConfig.getInstance();
        bucketName = cephConfig.getBucket();

        AWSCredentials credentials = new BasicAWSCredentials(cephConfig.getAccessKey(), cephConfig.getSecretKey());

        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setProtocol(Protocol.HTTP);

        amazonS3Connection = new AmazonS3Client(credentials, clientConfig);
        amazonS3Connection.setEndpoint(cephConfig.getEndpoint());
    }


    @Override
    public PhotoVO create(PhotoVO photo) throws CrudServiceException {
        return save(photo);
    }

    @Override
    public PhotoVO read(String id) throws CrudServiceException, ItemNotFoundException {
        try {
            if (!isExists(id))
                throw new ItemNotFoundException("Conversation id with " + id + " not found in ceph " + bucketName + " storage");

            final S3Object s3Object = amazonS3Connection.getObject(bucketName, id + ".dat");
            S3ObjectInputStream content = s3Object.getObjectContent();
            ObjectInputStream inputStream = new ObjectInputStream(content);
            return  (PhotoVO) inputStream.readObject();
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
            throw new CrudServiceException("Unable to read object from ceph filesystem with id: " + id, e);
        }
    }

    @Override
    public PhotoVO update(PhotoVO photo) throws CrudServiceException {
        return save(photo);
    }

    @Override
    public void delete(PhotoVO photo) throws CrudServiceException {
        amazonS3Connection.deleteObject(bucketName, photo.getOwnerId() + ".dat");
    }

    @Override
    public PhotoVO save(PhotoVO photo) throws CrudServiceException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(photo);
            oos.flush();
            oos.close();
            InputStream input = new ByteArrayInputStream(baos.toByteArray());
            amazonS3Connection.putObject(bucketName, photo.getOwnerId() + ".dat", input, new ObjectMetadata());
        } catch (IOException e) {
            LOGGER.error(LogMessageUtil.failMsg(e));
        }
        return photo;
    }

    @Override
    public boolean exists(PhotoVO photo) throws CrudServiceException {
        return isExists(photo.getOwnerId());
    }

    @Override
    public List<PhotoVO> query(Query q) throws CrudServiceException {
        return null;
    }

    private boolean isExists(String id) {
        return amazonS3Connection.doesObjectExist(bucketName, id + ".dat");
    }
}
