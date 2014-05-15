package net.anotheria.anosite.photoserver.service.storage.persistence.album;

import net.anotheria.anoprise.metafactory.ServiceFactory;

/**
 * AlbumPersistenceService factory. 
 * @author dzhmud
 */
public class AlbumPersistenceServiceFactory implements ServiceFactory<AlbumPersistenceService> {

	@Override
	public AlbumPersistenceService create() {
		return new AlbumPersistenceServiceImpl();
	}

}
