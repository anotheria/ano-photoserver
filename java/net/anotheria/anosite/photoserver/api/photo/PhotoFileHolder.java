package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.anoprise.dualcrud.CrudSaveable;

import java.io.File;
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
    private long id;
    /**
     * {@link InputStream} of photo.
     */
    private InputStream photoFileInputStream;
    /**
     * File location is FS.
     */
    private String fileLocation;
    /**
     * File extension.
     */
    private String extension;

    /**
     * Constructor.
     *
     * @param id        photo id
     * @param extension photo extension
     */
    public PhotoFileHolder(long id, String extension) {
        this.id = id;
        this.extension = extension;
    }

    @Override
    public String getOwnerId() {
        return id + extension;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public InputStream getPhotoFileInputStream() {
        return photoFileInputStream;
    }

    public void setPhotoFileInputStream(InputStream photoFileInputStream) {
        this.photoFileInputStream = photoFileInputStream;
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

    @Override
    public String toString() {
        return "PhotoFileHolder{" +
                "id=" + id +
                ", photoFileInputStream=" + photoFileInputStream +
                ", fileLocation='" + fileLocation + '\'' +
                ", extension='" + extension + '\'' +
                '}';
    }
}
