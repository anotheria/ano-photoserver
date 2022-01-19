package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.anoplass.api.API;
import net.anotheria.anosite.photoserver.shared.ApprovalStatus;
import net.anotheria.anosite.photoserver.shared.vo.PreviewSettingsVO;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * {@link net.anotheria.anoplass.api.API} for storing and removing photos.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public interface PhotoAPI extends API {

	// album related method's

	/**
	 * Get album by id. Uses default photos filtering.
	 *
	 * @param albumId - album id
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO}
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	AlbumAO getAlbum(long albumId) throws PhotoAPIException;

	/**
	 * Get album by id. Uses passed photos filtering(if null passed - uses PhotosFiltering.DEFAULT).
	 *
	 * @param albumId   - album id
	 * @param filtering - optional bean defining how to filter out photos by their approval status.
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO}
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	AlbumAO getAlbum(long albumId, PhotosFiltering filtering) throws PhotoAPIException;

    /**
     * Get album by id. Uses passed photos filtering(if null passed - uses PhotosFiltering.DEFAULT).
     *
     * @param albumId   - album id
     * @param filtering - optional bean defining how to filter out photos by their approval status.
     * @param authorId - id of author if used not logged in method
     * @return {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO}
     * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
     */
    AlbumAO getAlbum(long albumId, PhotosFiltering filtering, String authorId) throws PhotoAPIException;

	/**
	 * Get all user albums. Uses default photos filtering.
	 *
	 * @param userId - user id
	 * @return {@link java.util.List} of {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO}
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	List<AlbumAO> getAlbums(String userId) throws PhotoAPIException;

	/**
	 * Get all user albums. Uses passed photos filtering(if null passed - uses PhotosFiltering.DEFAULT).
	 *
	 * @param userId	- user id
	 * @param filtering - optional bean defining how to filter out photos by their approval status.
	 * @return {@link java.util.List} of {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO}
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	List<AlbumAO> getAlbums(String userId, PhotosFiltering filtering) throws PhotoAPIException;

    /**
     * Get all user albums. Uses passed photos filtering(if null passed - uses PhotosFiltering.DEFAULT).
     *
     * @param userId	- user id
     * @param filtering - optional bean defining how to filter out photos by their approval status.
     * @param authorId - id of author if used not logged in method
     * @return {@link java.util.List} of {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO}
     * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
     */
    List<AlbumAO> getAlbums(String userId, PhotosFiltering filtering, String authorId) throws PhotoAPIException;

	/**
	 * Get user default album. Uses default photos filtering.
	 *
	 * @param userId - user id
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO}
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	AlbumAO getDefaultAlbum(String userId) throws PhotoAPIException;

	/**
	 * Get user default album. Uses passed photos filtering(if null passed - uses PhotosFiltering.DEFAULT).
	 *
	 * @param userId	- user id
	 * @param filtering - optional bean defining how to filter out photos by their approval status.
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO}
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	AlbumAO getDefaultAlbum(String userId, PhotosFiltering filtering) throws PhotoAPIException;

    /**
     * Get user default album. Uses passed photos filtering(if null passed - uses PhotosFiltering.DEFAULT).
     *
     * @param userId	- user id
     * @param filtering - optional bean defining how to filter out photos by their approval status.
     * @param authorId - id of author if used not logged in method
     * @return {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO}
     * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
     */
    AlbumAO getDefaultAlbum(String userId, PhotosFiltering filtering, String authorId) throws PhotoAPIException;

	/**
	 * Create album.
	 *
	 * @param album - to create
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO} created
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	AlbumAO createAlbum(AlbumAO album) throws PhotoAPIException;

    /**
     * Create album.
     *
     * @param album - to create
     * @param authorId - id of author if used not logged in method
     * @return {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO} created
     * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
     */
    AlbumAO createAlbum(AlbumAO album, String authorId) throws PhotoAPIException;

	/**
	 * Update album.
	 *
	 * @param album - to update
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO} updated
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	AlbumAO updateAlbum(AlbumAO album) throws PhotoAPIException;

    /**
     * Update album.
     *
     * @param album - to update
     * @param authorId - id of author if used not logged in method
     * @return {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO} updated
     * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
     */
    AlbumAO updateAlbum(AlbumAO album, String authorId) throws PhotoAPIException;

	/**
	 * Remove album.
	 *
	 * @param albumId - album id
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO} removed
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	AlbumAO removeAlbum(long albumId) throws PhotoAPIException;

    /**
     * Remove album.
     *
     * @param albumId - album id
     * @param authorId - id of author if used not logged in method
     * @return {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO} removed
     * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
     */
    AlbumAO removeAlbum(long albumId, String authorId) throws PhotoAPIException;

	// "my" album related method's

	/**
	 * Get my (currently logged in user) albums.
	 *
	 * @return {@link java.util.List} of {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO}
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	List<AlbumAO> getMyAlbums() throws PhotoAPIException;

	/**
	 * Get my (currently logged in user) default album.
	 *
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.AlbumAO}
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	AlbumAO getMyDefaultAlbum() throws PhotoAPIException;

	// photo related method's

	/**
	 * Get my (currently logged in user) default photo(1st by photoOrder in the default album).
	 *
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO} or null if default album is empty.
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	PhotoAO getMyDefaultPhoto() throws PhotoAPIException;

	/**
	 * Get users default photo. If such exists.
	 *
	 * @param userId - user id
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO} or null if default album is empty.
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException on errors from StorageService and BlurSettings API,
	 *                           {@link net.anotheria.anosite.photoserver.api.photo.DefaultPhotoNotFoundAPIException} if default photo was not found
	 */
	PhotoAO getDefaultPhoto(String userId) throws PhotoAPIException;

	/**
	 * Return default Photo from selected album. If such was found.
	 *
	 * @param userId  id of album owner
	 * @param albumId id of album
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO}
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException on errors from StorageService and BlurSettings API,
	 *                           {@link net.anotheria.anosite.photoserver.api.photo.DefaultPhotoNotFoundAPIException} if default photo was not found
	 */
	PhotoAO getDefaultPhoto(String userId, long albumId) throws PhotoAPIException;

	/**
	 * Get photo by id.
	 *
	 * @param photoId - photo id
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO}
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	PhotoAO getPhoto(long photoId) throws PhotoAPIException;

	/**
	 * Get photos by album id.
	 * Returned list is ordered according to albums photoOrder.
	 *
	 * @param albumId - album id
	 * @return {@link java.util.List} of {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO}
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	List<PhotoAO> getPhotos(long albumId) throws PhotoAPIException;

	/**
	 * Get photos by album id. Uses passed photos filtering(if null passed - uses PhotosFiltering.DEFAULT).
	 * Returned list is ordered according to albums photoOrder if passed {@code orderByPhotosOrder} is TRUE.
	 *
	 * @param albumId			- album id
	 * @param filtering		  - optional bean defining how to filter out photos by their approval status.
	 * @param orderByPhotosOrder - if true passed, returned list of photos will be ordered.
	 * @return {@link java.util.List} of {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO}
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	List<PhotoAO> getPhotos(long albumId, PhotosFiltering filtering, boolean orderByPhotosOrder) throws PhotoAPIException;

	/**
	 * Create new photo in default album.
	 *
	 * @param userId		  - user id
	 * @param tempFile		- temporary photo file
	 * @param previewSettings - photo preview settings
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO} created
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	PhotoAO createPhoto(String userId, File tempFile, PreviewSettingsVO previewSettings) throws PhotoAPIException;

	/**
	 * Create new photo in default album.
	 *
	 * @param userId		  - user id
	 * @param tempFile		- temporary photo file
	 * @param previewSettings - photo preview settings
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO} created
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 * @param restricted a boolean.
	 */
	PhotoAO createPhoto(String userId, File tempFile, PreviewSettingsVO previewSettings, boolean restricted) throws PhotoAPIException;

	/**
	 * Create new photo.
	 *
	 * @param userId		  - user id
	 * @param albumId		 - album id
	 * @param tempFile		- temporary photo file
	 * @param previewSettings - photo preview settings
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO} created
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	PhotoAO createPhoto(String userId, long albumId, File tempFile, PreviewSettingsVO previewSettings) throws PhotoAPIException;

	/**
	 * Create new photo.
	 *
	 * @param userId		  - user id
	 * @param albumId		 - album id
	 * @param restricted    - if access for this photo will be restricted
	 * @param tempFile		- temporary photo file
	 * @param previewSettings - photo preview settings
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO} created
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	PhotoAO createPhoto(String userId, long albumId, boolean restricted, File tempFile, PreviewSettingsVO previewSettings) throws PhotoAPIException;

	/**
	 * Update photo. ApprovalStatus is not updated by this method.
	 *
	 * @param photo - photo
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO} updated
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	PhotoAO updatePhoto(PhotoAO photo) throws PhotoAPIException;

    /**
     * Update photo. ApprovalStatus is not updated by this method.
     *
     * @param userId		  - user id
     * @param photo - photo
     * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO} updated
     * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
     */
    PhotoAO updatePhoto(String userId, PhotoAO photo) throws PhotoAPIException;

	/**
	 * Remove photo.
	 *
	 * @param photoId - photo id
	 * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO} removed
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	PhotoAO removePhoto(long photoId) throws PhotoAPIException;

    /**
     * Remove photo.
     *
     * @param userId		  - user id
     * @param photoId - photo id
     * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO} removed
     * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
     */
    PhotoAO removePhoto(String userId, long photoId) throws PhotoAPIException;

	/**
	 * Get list of photos in "waitingApproval" status, sorted in special way.
	 *
	 * @param amount - maximum amount of photos to be returned.
	 * @return {@link java.util.List} of {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO}
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	List<PhotoAO> getWaitingApprovalPhotos(int amount) throws PhotoAPIException;

	/**
	 * Get amount of Photos that are in "waiting approval" state.
	 *
	 * @return amount of Photos that are in "waiting approval" state.
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	int getWaitingApprovalPhotosCount() throws PhotoAPIException;

	/**
	 * Update approvalStatus of single photo.
	 *
	 * @param photoId - Id of photo, which status is to be changed.
	 * @param status  - status to set.
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	void setApprovalStatus(long photoId, ApprovalStatus status) throws PhotoAPIException;

	/**
	 * Bulk update of photos approvalStatus.
	 *
	 * @param statuses - mapping between photo ids and approvalStatuses to set.
	 * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
	 */
	void setApprovalStatuses(Map<Long, ApprovalStatus> statuses) throws PhotoAPIException;

    /**
     * Update photo. Album is change to new one.
     *
     * @param photoId - Id of photo, which albums is to be changed
     * @param newAlbumId - Id of album, which should receive new photo
     * @return {@link net.anotheria.anosite.photoserver.api.photo.PhotoAO} updated
     * @throws net.anotheria.anosite.photoserver.api.photo.PhotoAPIException if any.
     */
    PhotoAO movePhoto(long photoId, long newAlbumId) throws PhotoAPIException;

	/**
	 * Get {@link InputStream} photo content.
	 *
	 * @param photo		{@link PhotoAO} photo
	 * @return			{@link InputStream} of photo
	 * @throws PhotoAPIException if any errors occurs
	 */
	InputStream getPhotoContent(PhotoAO photo) throws PhotoAPIException;
}
