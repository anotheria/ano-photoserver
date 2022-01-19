package net.anotheria.anosite.photoserver.service.storage;

import net.anotheria.anoprise.metafactory.Service;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;
import org.distributeme.annotation.DistributeMe;

import java.util.List;
import java.util.Map;

/**
 * Service for managing user photos.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
@DistributeMe(initcode = "net.anotheria.anosite.photoserver.shared.PhotoServerConfigurator.configure();")
public interface StorageService extends Service {

	// album related method's

	/**
	 * Get album by id.
	 *
	 * @param albumId - album id
	 * @return {@link AlbumBO}
	 * @throws StorageServiceException if any.
	 */
	AlbumBO getAlbum(long albumId) throws StorageServiceException;

	/**
	 * Get all user albums.
	 *
	 * @param userId - user id
	 * @return {@link List} of {@link AlbumBO}
	 * @throws StorageServiceException if any.
	 */
	List<AlbumBO> getAlbums(String userId) throws StorageServiceException;

	/**
	 * Get user default album. New default album will be created if no one can't be found.
	 *
	 * @param userId - user id
	 * @return {@link AlbumBO}
	 * @throws StorageServiceException if any.
	 */
	AlbumBO getDefaultAlbum(String userId) throws StorageServiceException;

	/**
	 * Create album. Can't create default album's.
	 *
	 * @param album - to create
	 * @return {@link AlbumBO} created
	 * @throws StorageServiceException if any.
	 */
	AlbumBO createAlbum(AlbumBO album) throws StorageServiceException;

	/**
	 * Update album.
	 *
	 * @param album - to update
	 * @return {@link AlbumBO} updated
	 * @throws StorageServiceException if any.
	 */
	AlbumBO updateAlbum(AlbumBO album) throws StorageServiceException;

	/**
	 * Remove album. Can't remove album if album have photos.
	 *
	 * @param albumId - album id
	 * @return {@link AlbumBO} removed
	 * @throws StorageServiceException if any.
	 */
	AlbumBO removeAlbum(long albumId) throws StorageServiceException;

	// photo related method's

	/**
	 * Get photo.
	 *
	 * @param photoId - photo id
	 * @return {@link PhotoBO}
	 * @throws StorageServiceException if any.
	 */
	PhotoBO getPhoto(long photoId) throws StorageServiceException;

	/**
	 * Returns default photo from default album, if such exists.
	 * Default photo  - is photo which is First in Album ordering, and it  anyway should be approved (if Approving is enabled)!
	 *
	 * @param userId user id
	 * @return {@link PhotoBO}
	 * @throws StorageServiceException on errors from persistence
	 * {@link DefaultPhotoNotFoundServiceException} in case - when album does not have any photos, or does not have any approved photos (when approving func is enabled).
	 */
	PhotoBO getDefaultPhoto(String userId) throws StorageServiceException;

	/**
	 * Returns default photo from selected album, if such exists.
	 * Default photo  - is photo which is First in Album ordering, and it  anyway should be approved (if Approving is enabled)!
	 *
	 * @param userId  user id
	 * @param albumId album id
	 * @return {@link PhotoBO}
	 * @throws StorageServiceException on errors from persistence, or if album does not belongs to selected user,
	 * {@link DefaultPhotoNotFoundServiceException} in case - when album does not have any photos, or does not have any approved photos (when approving func is enabled).
	 */
	PhotoBO getDefaultPhoto(String userId, long albumId) throws StorageServiceException;

	/**
	 * Get list of user photos.
	 *
	 * @param userId  - user id
	 * @param albumId - album id
	 * @return {@link List} of {@link PhotoBO}
	 * @throws StorageServiceException if any.
	 */
	List<PhotoBO> getPhotos(String userId, long albumId) throws StorageServiceException;

	/**
	 * Get unordered list of user photos by their IDs.
	 *
	 * @param userId   - user id
	 * @param photoIDs - list of photo ids
	 * @return {@link List} of {@link PhotoBO}
	 * @throws StorageServiceException if any.
	 */
	List<PhotoBO> getPhotos(String userId, List<Long> photoIDs) throws StorageServiceException;

	/**
	 * Get all photos that have approvalStatus equal to {WAITING_APPROVAL}.
	 *
	 * @param photosAmount amount of photos
	 * @return {@link List} of {@link PhotoBO}
	 * @throws StorageServiceException if any.
	 */
	List<PhotoBO> getWaitingApprovalPhotos(int photosAmount) throws StorageServiceException;

	/**
	 * Get amount of Photos that are in "waiting approval" state.
	 *
	 * @return amount of Photos that are in "waiting approval" state.
	 * @throws StorageServiceException if any.
	 */
	int getWaitingApprovalPhotosCount() throws StorageServiceException;

	/**
	 * This method: create photo object in persistence, set unique id for the photo, prepare photo file location URI. This method also check is photo album
	 * available and adding new photo to album photos order list to end.
	 * This method explicitly sets approvalStatus to {WAITING_APPROVAL}.
	 * This method don't write real photo file to prepared location we need do this in other (API/etc) layer.
	 *
	 * @param photo - photo information
	 * @return Created {@link PhotoBO} with all information
	 * @throws StorageServiceException if any.
	 */
	PhotoBO createPhoto(PhotoBO photo) throws StorageServiceException;

	/**
	 * This method updating photo information in persistence (Like: name, description, etc).
	 * This method do not update approvalStatus - updatePhotoApprovalStatuses() method should be used for that.
	 *
	 * @param photo - photo information
	 * @return updated PhotoBO
	 * @throws StorageServiceException if any.
	 */
	PhotoBO updatePhoto(PhotoBO photo) throws StorageServiceException;

	/**
	 * Bulk update approvalStatus.
	 *
	 * @param statuses - mapping between photo IDs and their new statuses.
	 * @throws StorageServiceException if any.
	 */
	void updatePhotoApprovalStatuses(Map<Long, ApprovalStatus> statuses) throws StorageServiceException;

	/**
	 * Get approvalStatuses of all the photos in the album.
	 *
	 * @param albumId - specific album ID.
	 * @return Map&lt;photoId, approvalStatus&gt;
	 * @throws StorageServiceException if any.
	 */
	Map<Long, ApprovalStatus> getAlbumPhotosApprovalStatus(long albumId) throws StorageServiceException;

	/**
	 * This method remove photo information from persistence and also remove real photo file from FS storage.
	 *
	 * @param photoId - photo id
	 * @throws StorageServiceException if any.
	 */
	void removePhoto(long photoId) throws StorageServiceException;

    /**
     * This method move photo from old album to new one in persistence.
     *
     * @param photoId  - photo id
     * @param newAlbumId - new album ID
     * @return updated {@link PhotoBO} object
     * @throws StorageServiceException if any.
     */
    PhotoBO movePhoto(long photoId, long newAlbumId) throws StorageServiceException;
}
