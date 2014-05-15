package net.anotheria.anosite.photoserver.service.storage.persistence;

import net.anotheria.anoprise.metafactory.Service;
import net.anotheria.anosite.photoserver.service.storage.PhotoBO;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;

import java.util.List;
import java.util.Map;

/**
 * Persistence service for StorageService.
 *
 * @author dzhmud
 */
public interface StoragePersistenceService extends Service {

	/**
	 * Create(store) photo meta-information in persistence.
	 *
	 * @param photo
	 * @return created {@link PhotoBO}
	 * @throws StoragePersistenceServiceException
	 *
	 */
	PhotoBO createPhoto(PhotoBO photo) throws StoragePersistenceServiceException;

	/**
	 * Updates photo meta-information. Do not update approvalStatus - updatePhotoApprovalStatuses() method should be used instead.
	 *
	 * @param photo
	 * @throws StoragePersistenceServiceException
	 *          - if SQLException occurred, {@link PhotoNotFoundPersistenceServiceException} - if photo was not found in persistence.
	 */
	void updatePhoto(PhotoBO photo) throws StoragePersistenceServiceException;

	/**
	 * Returns photo by id (if present). Throws {@link PhotoNotFoundPersistenceServiceException} if not present.
	 *
	 * @param photoId
	 * @return {@link PhotoBO} if found
	 * @throws StoragePersistenceServiceException
	 *          - if SQLException occurred, {@link PhotoNotFoundPersistenceServiceException} - if photo was not found in persistence.
	 */
	PhotoBO getPhoto(long photoId) throws StoragePersistenceServiceException;

	/**
	 * Get list of user photos.
	 *
	 * @param userId  - user id
	 * @param albumId - album id
	 * @return {@link java.util.List} of {@link PhotoBO}
	 * @throws StoragePersistenceServiceException
	 *
	 */
	List<PhotoBO> getUserPhotos(String userId, long albumId) throws StoragePersistenceServiceException;

	/**
	 * Get list of user photos.
	 *
	 * @param userId - user id
	 * @return {@link java.util.List} of {@link PhotoBO}
	 * @throws StoragePersistenceServiceException
	 *
	 */
	List<PhotoBO> getAllUserPhotos(String userId) throws StoragePersistenceServiceException;

	/**
	 * /**
	 * Get unordered list of user photos by their IDs.
	 *
	 * @param userId   - user id
	 * @param photoIDs - list of photo ids
	 * @return {@link java.util.List} of {@link PhotoBO}
	 * @throws StoragePersistenceServiceException
	 *
	 */
	List<PhotoBO> getUserPhotos(String userId, List<Long> photoIDs) throws StoragePersistenceServiceException;

	/**
	 * Remove photo meta-information from persistence.
	 *
	 * @param photoId
	 * @throws StoragePersistenceServiceException
	 *          - if SQLException occurred, {@link PhotoNotFoundPersistenceServiceException} - if photo was not found in persistence.
	 */
	void deletePhoto(long photoId) throws StoragePersistenceServiceException;

	/**
	 * Bulk update approvalStatus for photos with given IDs.
	 *
	 * @param statuses - mapping of photoIDs and statuses to set.
	 * @throws StoragePersistenceServiceException
	 *
	 */
	void updatePhotoApprovalStatuses(Map<Long, ApprovalStatus> statuses) throws StoragePersistenceServiceException;

	/**
	 * Get all photos with specific approvalStatus.
	 *
	 * @param amount amount of photos to get
	 * @param status - status to select photos with.
	 * @return {@link java.util.List} with all Photos that have provided status.
	 * @throws StoragePersistenceServiceException
	 *
	 */
	List<PhotoBO> getPhotosWithStatus(int amount, ApprovalStatus status) throws StoragePersistenceServiceException;

	/**
	 * Get count of Photos that have specific approvalStatus.
	 *
	 * @param status - status to count photos with.
	 * @return quantity of photos with provided status.
	 * @throws StoragePersistenceServiceException
	 *
	 */
	int getPhotosWithStatusCount(ApprovalStatus status) throws StoragePersistenceServiceException;

	/**
	 * Get approvalStatus of each of the albums photos.
	 *
	 * @param albumId - specific album ID.
	 * @return Map&lt;photoId, approvalStatus&gt;
	 * @throws StoragePersistenceServiceException
	 *
	 */
	Map<Long, ApprovalStatus> getAlbumPhotosApprovalStatus(long albumId) throws StoragePersistenceServiceException;

    /**
     * Change photo album meta-information in persistence.
     *
     * @param photoId  - photo id
     * @param newAlbumId - new album id
     * @param modificationTime - timestamp of modification
     * @throws StoragePersistenceServiceException
     */
    void movePhoto(long photoId, long newAlbumId, long modificationTime) throws StoragePersistenceServiceException;
}
