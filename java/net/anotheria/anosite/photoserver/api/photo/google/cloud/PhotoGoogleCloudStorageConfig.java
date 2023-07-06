package net.anotheria.anosite.photoserver.api.photo.google.cloud;

import org.configureme.ConfigurationManager;
import org.configureme.annotations.Configure;
import org.configureme.annotations.ConfigureMe;
import org.configureme.annotations.DontConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * Configuration for Google Storage.
 *
 * @author ykalapusha
 */
@ConfigureMe(name = "ano-site-photoserver-google-cloud-storage-config")
public class PhotoGoogleCloudStorageConfig implements Serializable {
    /**
     * Serial version UID.
     */
    @DontConfigure
    private static final long serialVersionUID = -4223128277128558385L;
    /**
     * {@link Logger} instance
     */
    @DontConfigure
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotoGoogleCloudStorageConfig.class);
    /**
     * Synchronization lock
     */
    @DontConfigure
    private static final Object LOCK = new Object();
    /**
     * Configured instance.
     */
    @DontConfigure
    private static volatile PhotoGoogleCloudStorageConfig instance;
    /**
     * Bucket name for original photos.
     */
    @Configure
    private String originalBucketName;
    /**
     * Bucket name for scaled photos.
     */
    @Configure
    private String scaledBucketName;
    /**
     * Google project id.
     */
    @Configure
    private String projectId;
    /**
     * Credential path for google service account file.
     */
    @Configure
    private String credentialsPath;

    /**
     * Private constructor.
     */
    private PhotoGoogleCloudStorageConfig() {
        try {
            ConfigurationManager.INSTANCE.configure(this);
        }catch (final IllegalArgumentException e){
            LOGGER.warn("PhotoGoogleCloudStorageConfig() configuration fail [" + e.getMessage() + "]. Relaying on defaults [" + this + "]" );
        }

        if(LOGGER.isDebugEnabled()){
            LOGGER.warn("PhotoGoogleCloudStorageConfig() configured with [" + this + "]");
        }
    }

    /**
     * Get {@link PhotoGoogleCloudStorageConfig} configured instance.
     *
     * @return PhotoGoogleCloudStorageConfig instance
     */
    public static PhotoGoogleCloudStorageConfig getInstance() {
        if(instance == null){
            synchronized (LOCK){
                if(instance == null){
                    instance = new PhotoGoogleCloudStorageConfig();
                }
            }
        }
        return instance;
    }

    public String getOriginalBucketName() {
        return originalBucketName;
    }

    public void setOriginalBucketName(String originalBucketName) {
        this.originalBucketName = originalBucketName;
    }

    public String getScaledBucketName() {
        return scaledBucketName;
    }

    public void setScaledBucketName(String scaledBucketName) {
        this.scaledBucketName = scaledBucketName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getCredentialsPath() {
        return credentialsPath;
    }

    public void setCredentialsPath(String credentialsPath) {
        this.credentialsPath = credentialsPath;
    }

    @Override
    public String toString() {
        return "PhotoGoogleCloudStorageConfig{" +
                "originalBucketName='" + originalBucketName + '\'' +
                ", scaledBucketName='" + scaledBucketName + '\'' +
                ", projectId='" + projectId + '\'' +
                ", credentialsPath='" + credentialsPath + '\'' +
                '}';
    }
}
