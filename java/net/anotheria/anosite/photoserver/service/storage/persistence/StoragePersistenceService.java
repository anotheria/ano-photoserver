package net.anotheria.anosite.photoserver.service.storage.persistence;

import net.anotheria.anoprise.dualcrud.Query;
import net.anotheria.anoprise.metafactory.Service;
import net.anotheria.anosite.photoserver.service.storage.PhotoBO;

import java.util.List;

/**
 * Persistence service for StorageService.
 *
 * @author dzhmud
 * @version $Id: $Id
 */
public interface StoragePersistenceService extends Service {
    /**
     * Create photo for user.
     *
     * @param photoBO  {@link PhotoBO}
     * @return         created {@link PhotoBO}
     * @throws StoragePersistenceServiceException if any errors occurs
     */
    PhotoBO createPhoto(PhotoBO photoBO) throws StoragePersistenceServiceException;

    /**
     * Update photo for user.
     *
     * @param photoBO {@link PhotoBO}
     * @throws StoragePersistenceServiceException if any errors occurs
     */
    void updatePhoto(PhotoBO photoBO) throws StoragePersistenceServiceException;

    /**
     * Get user photo.
     *
     * @param photoId {@code long} id of photo
     * @return        {@link PhotoBO}
     * @throws StoragePersistenceServiceException if any errors occurs
     */
    PhotoBO getPhoto(final long photoId) throws StoragePersistenceServiceException;

    /**
     * Detele user photo.
     *
     * @param photoId {@code long} id of photo
     * @throws StoragePersistenceServiceException if any errors occurs
     */
    void deletePhoto(final long photoId) throws StoragePersistenceServiceException;

    /**
     * Get {@link List} of {@link PhotoBO} by configured {@link Query}
     *
     * @param query configured {@link Query}
     * @return  {@link List} of {@link PhotoBO}
     * @throws StoragePersistenceServiceException if any errors occurs
     */
    List<PhotoBO> getPhotosByQuery(Query query) throws StoragePersistenceServiceException;
}
