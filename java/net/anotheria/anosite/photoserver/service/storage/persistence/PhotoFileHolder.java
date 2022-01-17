package net.anotheria.anosite.photoserver.service.storage.persistence;

import net.anotheria.anoprise.dualcrud.CrudSaveable;

import java.io.File;
import java.io.Serializable;

/**
 * @author ykalapusha
 */
public class PhotoFileHolder implements Serializable, CrudSaveable {


    private static final long serialVersionUID = -8632069285815161905L;
    private long id;
    private File photoFile;
    private String userId;
    private String fileLocation;
    private String extension;

    @Override
    public String getOwnerId() {
        return String.valueOf(id);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public File getPhotoFile() {
        return photoFile;
    }

    public void setPhotoFile(File photoFile) {
        this.photoFile = photoFile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
        return getFileLocation() + File.separator + getId() + getExtension();
    }
}
