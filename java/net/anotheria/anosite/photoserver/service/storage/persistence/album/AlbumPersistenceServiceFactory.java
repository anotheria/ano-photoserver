package net.anotheria.anosite.photoserver.service.storage.persistence.album;

import net.anotheria.anoprise.metafactory.ServiceFactory;

/**
 * AlbumPersistenceService factory.
 *
 * @author dzhmud
 * @version $Id: $Id
 */
public class AlbumPersistenceServiceFactory implements ServiceFactory<AlbumPersistenceService> {

	/** {@inheritDoc} */
	@Override
	public AlbumPersistenceService create() {
		return new AlbumPersistenceServiceImpl();
	}

}
