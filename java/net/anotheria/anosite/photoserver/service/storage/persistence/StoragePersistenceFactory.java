package net.anotheria.anosite.photoserver.service.storage.persistence;

import net.anotheria.anoprise.metafactory.ServiceFactory;

/**
 * ServiceFactory for StoragePersistenceService. 
 * @author dzhmud
 */
public class StoragePersistenceFactory implements ServiceFactory<StoragePersistenceService> {

	@Override
	public StoragePersistenceService create() {
		return new StoragePersistenceServiceImpl();
	}

}
