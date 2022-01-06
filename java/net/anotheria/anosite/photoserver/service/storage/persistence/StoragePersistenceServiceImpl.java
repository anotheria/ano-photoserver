package net.anotheria.anosite.photoserver.service.storage.persistence;

import net.anotheria.anoprise.dualcrud.CrudServiceException;
import net.anotheria.anoprise.dualcrud.DualCrudConfig;
import net.anotheria.anoprise.dualcrud.DualCrudService;
import net.anotheria.anoprise.dualcrud.DualCrudServiceFactory;
import net.anotheria.anoprise.dualcrud.ItemNotFoundException;
import net.anotheria.anoprise.dualcrud.Query;
import net.anotheria.anoprise.dualcrud.SaveableID;
import net.anotheria.anosite.photoserver.service.storage.PhotoBO;
import net.anotheria.anosite.photoserver.service.storage.persistence.ceph.PhotoCephClientService;
import net.anotheria.anosite.photoserver.service.storage.persistence.fs.PhotoStoragePersistenceService;
import net.anotheria.anosite.photoserver.shared.PhotoServerConfig;

import java.util.List;

/**
 * Implementation of the StoragePersistenceService.
 *
 * @author dzhmud
 * @version $Id: $Id
 */
public class StoragePersistenceServiceImpl implements StoragePersistenceService {
    /**
     * {@link DualCrudService} for photos.
     */
    private final DualCrudService<PhotoBO> dualCrudService;

    /**
     * Constructor.
     */
    public StoragePersistenceServiceImpl() {
        PhotoStoragePersistenceService photoStoragePersistenceService = new PhotoStoragePersistenceService();
        if (PhotoServerConfig.getInstance().isPhotoCephEnabled()) {
            DualCrudConfig config = DualCrudConfig.migrateOnTheFly();
            dualCrudService = DualCrudServiceFactory.createDualCrudService(photoStoragePersistenceService, new PhotoCephClientService(), config);
        } else {
            dualCrudService = DualCrudServiceFactory.createDualCrudService(photoStoragePersistenceService, null, DualCrudConfig.useLeftOnly());
        }
    }

    @Override
    public PhotoBO createPhoto(PhotoBO photoBO) throws StoragePersistenceServiceException {
        try {
            return dualCrudService.create(photoBO);
        } catch (CrudServiceException e) {
            throw new StoragePersistenceServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void updatePhoto(PhotoBO photoBO) throws StoragePersistenceServiceException {
        try {
            dualCrudService.update(photoBO);
        } catch (ItemNotFoundException e) {
            throw new PhotoNotFoundPersistenceServiceException(photoBO.getId());
        } catch (CrudServiceException e) {
            throw new StoragePersistenceServiceException(e.getMessage(), e);
        }
    }

    @Override
    public PhotoBO getPhoto(final long photoId) throws StoragePersistenceServiceException {
        try {
            SaveableID saveableID = new SaveableID();
            saveableID.setSaveableId(String.valueOf(photoId));
            return dualCrudService.read(saveableID);
        } catch (ItemNotFoundException e) {
            throw new PhotoNotFoundPersistenceServiceException(photoId);
        }  catch (CrudServiceException e) {
            throw new StoragePersistenceServiceException(e.getMessage(), e);
        }
    }

    @Override
    public void deletePhoto(long photoId) throws StoragePersistenceServiceException {
        try {
            dualCrudService.delete(getPhoto(photoId));
        } catch (CrudServiceException e) {
            throw new StoragePersistenceServiceException(e.getMessage(), e);
        }
    }

    @Override
    public List<PhotoBO> getPhotosByQuery(Query query) throws StoragePersistenceServiceException {
        try {
            return dualCrudService.query(query).getResult(false);
        } catch (CrudServiceException e) {
            throw new StoragePersistenceServiceException(e.getMessage(), e);
        }
    }
}
