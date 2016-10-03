package net.anotheria.anosite.photoserver.service.storage.persistence.album;

import net.anotheria.anoprise.metafactory.Service;
import net.anotheria.anosite.photoserver.service.storage.AlbumBO;

import java.util.List;

/**
 *
 * Album persistence service interface.
 *
 * @author dzhmud
 * @version $Id: $Id
 */
public interface AlbumPersistenceService extends Service {

	/**
	 * Create Album in persistence.
	 *
	 * @param album a {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO} object.
	 * @return {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO} - albumVO identical to created in persistence.
	 * @throws net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceException if any.
	 */
	AlbumBO createAlbum(AlbumBO album) throws AlbumPersistenceServiceException;

	/**
	 * Get user's Album by id.
	 *
	 * @param albumId a long.
	 * @return {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO} - AlbumVO that has needed albumId
	 * @throws net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceException
	 *             - if SQLException occurred, {@link net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumNotFoundPersistenceServiceException} - if album was not found in persistence.
	 */
	AlbumBO getAlbum(long albumId) throws AlbumPersistenceServiceException;

	/**
	 * Get default photo album of the user with provided userId.
	 *
	 * @param userId
	 *            - userId
	 * @return {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO}
	 * @throws net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceException if any.
	 */
	AlbumBO getDefaultAlbum(String userId) throws AlbumPersistenceServiceException;

	/**
	 * Get photo albums of the user with provided userId. I there are no albums - returns empty List.
	 *
	 * @param userId
	 *            - userId
	 * @return {@link java.util.List} of {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO}, or empty List if there are no albums for user in persistence.
	 * @throws net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceException if any.
	 */
	List<AlbumBO> getAlbums(String userId) throws AlbumPersistenceServiceException;

	/**
	 * Update existing Album in persistence.
	 *
	 * @param album a {@link net.anotheria.anosite.photoserver.service.storage.AlbumBO} object.
	 * @throws net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceException
	 *             - if SQLException occurred, {@link net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumNotFoundPersistenceServiceException} - if album was not found in persistence.
	 */
	void updateAlbum(AlbumBO album) throws AlbumPersistenceServiceException;

	/**
	 * Remove Album from persistence.
	 *
	 * @param albumId
	 *            - ID of album to delete
	 * @throws net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumPersistenceServiceException
	 *             - if SQLException occurred, {@link net.anotheria.anosite.photoserver.service.storage.persistence.album.AlbumNotFoundPersistenceServiceException} - if album was not found in persistence.
	 */
	void deleteAlbum(long albumId) throws AlbumPersistenceServiceException;

}
