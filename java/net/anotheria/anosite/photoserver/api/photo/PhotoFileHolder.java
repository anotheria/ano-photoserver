package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.anoprise.dualcrud.CrudSaveable;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * {@link CrudSaveable} object for user photo.
 *
 * @author ykalapusha
 */
public class PhotoFileHolder implements Serializable, CrudSaveable {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = -8632069285815161905L;
    /**
     * Photo id.
     */
    private String id;
    /**
     * Original photo id.
     */
    private long originalPhotoId;
    /**
     * File location is FS.
     */
    private String fileLocation;
    /**
     * File extension.
     */
    private String extension;
    /**
     * Photo owner.
     */
    private String userId;

    /**
     * Data.
     */
    private ByteArrayOutputStream baos;

    /**
     * Constructor.
     *
     * @param id        photo id
     * @param extension photo extension
     */
    public PhotoFileHolder(String id, long originalPhotoId, String extension, String userId) {
        this.id = id;
        this.originalPhotoId = originalPhotoId;
        this.extension = extension;
        this.userId = userId;
    }

    @Override
    public String getOwnerId() {
        return id + extension;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getOriginalPhotoId() {
        return originalPhotoId;
    }

    public void setOriginalPhotoId(long originalPhotoId) {
        this.originalPhotoId = originalPhotoId;
    }

    public InputStream getPhotoFileInputStream() {
        return new ByteArrayInputStream(baos.toByteArray());
    }

    public void setPhotoFileInputStream(InputStream photoFileInputStream) throws IOException {
        baos = new ByteArrayOutputStream();
        IOUtils.copyLarge(photoFileInputStream, baos);
        IOUtils.closeQuietly(photoFileInputStream);
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getFilePath() {
        return getFileLocation() + getId() + getExtension();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void closeInputStream() {
        IOUtils.closeQuietly(baos);
    }

    @Override
    public String toString() {
        return "PhotoFileHolder{" +
                "id='" + id + '\'' +
                ", originalPhotoId=" + originalPhotoId +
                ", fileLocation='" + fileLocation + '\'' +
                ", extension='" + extension + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
