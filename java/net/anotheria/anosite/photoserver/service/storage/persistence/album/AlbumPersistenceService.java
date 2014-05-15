package net.anotheria.anosite.photoserver.service.storage.persistence.album;

import net.anotheria.anoprise.metafactory.Service;
import net.anotheria.anosite.photoserver.service.storage.AlbumBO;

import java.util.List;

/***
 * Album persistence service interface.
 * 
 * @author dzhmud
 */
public interface AlbumPersistenceService extends Service {

	/**
	 * Create Album in persistence.
	 * 
	 * @param album
	 * @return {@link AlbumBO} - albumVO identical to created in persistence.
	 * @throws AlbumPersistenceServiceException
	 */
	AlbumBO createAlbum(AlbumBO album) throws AlbumPersistenceServiceException;

	/**
	 * Get user's Album by id.
	 * 
	 * @param albumId
	 * @return {@link AlbumBO} - AlbumVO that has needed albumId
	 * @throws AlbumPersistenceServiceException
	 *             - if SQLException occurred, {@link AlbumNotFoundPersistenceServiceException} - if album was not found in persistence.
	 */
	AlbumBO getAlbum(long albumId) throws AlbumPersistenceServiceException;

	/**
	 * Get default photo album of the user with provided userId.
	 * 
	 * @param userId
	 *            - userId
	 * @return {@link AlbumBO}
	 * @throws AlbumPersistenceServiceException
	 */
	AlbumBO getDefaultAlbum(String userId) throws AlbumPersistenceServiceException;

	/**
	 * Get photo albums of the user with provided userId. I there are no albums - returns empty List.
	 * 
	 * @param userId
	 *            - userId
	 * @return {@link java.util.List} of {@link AlbumBO}, or empty List if there are no albums for user in persistence.
	 * @throws AlbumPersistenceServiceException
	 */
	List<AlbumBO> getAlbums(String userId) throws AlbumPersistenceServiceException;

	/**
	 * Update existing Album in persistence.
	 * 
	 * @param album
	 * @throws AlbumPersistenceServiceException
	 *             - if SQLException occurred, {@link AlbumNotFoundPersistenceServiceException} - if album was not found in persistence.
	 */
	void updateAlbum(AlbumBO album) throws AlbumPersistenceServiceException;

	/**
	 * Remove Album from persistence.
	 * 
	 * @param albumId
	 *            - ID of album to delete
	 * @throws AlbumPersistenceServiceException
	 *             - if SQLException occurred, {@link AlbumNotFoundPersistenceServiceException} - if album was not found in persistence.
	 */
	void deleteAlbum(long albumId) throws AlbumPersistenceServiceException;

}
