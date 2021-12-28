package net.anotheria.anosite.photoserver.service.storage.persistence.album;

/**
 * Exception indicating that album was not found in persistence when trying to operate with it by specifying explicit ID.
 *
 * @author dzhmud
 * @version $Id: $Id
 */
public class DefaultAlbumNotFoundPersistenceServiceException extends AlbumPersistenceServiceException {

	/**
	 * Basic serialVersionUID variable.
	 */
	private static final long serialVersionUID = -4640820911969357352L;

	/**
	 * Default constructor.
	 *
	 * @param userId a {@link java.lang.String} object.
	 */
	public DefaultAlbumNotFoundPersistenceServiceException(String userId) {
		super("Album for User[" + userId + "] was not found.");
	}

}
