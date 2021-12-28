package net.anotheria.anosite.photoserver.service.storage.persistence.ceph;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Photo ceph configuration.
 *
 * @author ykalapusha
 */
@ConfigureMe(name = "ano-site-photoserver-ceph-client")
public class PhotoCephClientConfig implements Serializable {
    /**
     * Serial version UID.
     */
    @DontConfigure
    private static final long serialVersionUID = -7663346108930551984L;
    /**
     * {@link Logger} instance.
     */
    @DontConfigure
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoCephClientConfig.class);
    /**
     * Synchronization lock.
     */
    @DontConfigure
    private static final Object LOCK = new Object();
    /**
     * {@link PhotoCephClientConfig} configured instance.
     */
    @DontConfigure
    private static volatile PhotoCephClientConfig instance;
    /**
     * S3 access key.
     */
    @Configure
    private String accessKey;
    /**
     * S3 secret key.
     */
    @Configure
    private String secretKey;
    /**
     * S3 endpoint to ceph cluster.
     */
    @Configure
    private String endpoint;
    /**
     * Bucket data name.
     */
    @Configure
    private String bucket;

    /**
     * Private constructor.
     */
    private PhotoCephClientConfig(){
        try {
            ConfigurationManager.INSTANCE.configure(this);
        }catch (final IllegalArgumentException e){
            LOGGER.warn("PhotoCephClientConfig() configuration fail [" + e.getMessage() + "]");
            throw new RuntimeException("Unable to configure PhotoCephClientConfig", e);
        }

        if(LOGGER.isDebugEnabled()){
            LOGGER.warn("PhotoCephClientConfig() configured with [" + this + "]");
        }
    }

    /**
     * Get configured {@link PhotoCephClientConfig} instance.
     *
     * @return configured {@link PhotoCephClientConfig} instance
     */
    public static PhotoCephClientConfig getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new PhotoCephClientConfig();
                }
            }
        }
        return instance;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    @Override
    public String toString() {
        return "PhotoCephClientConfig{" +
                "accessKey='" + accessKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
                ", endpoint='" + endpoint + '\'' +
                ", bucket='" + bucket + '\'' +
                '}';
    }
}
