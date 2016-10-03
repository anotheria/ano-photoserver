package net.anotheria.anosite.photoserver.service.storage.persistence;

import net.anotheria.anoprise.metafactory.ServiceFactory;

/**
 * ServiceFactory for StoragePersistenceService.
 *
 * @author dzhmud
 * @version $Id: $Id
 */
public class StoragePersistenceFactory implements ServiceFactory<StoragePersistenceService> {

	/** {@inheritDoc} */
	@Override
	public StoragePersistenceService create() {
		return new StoragePersistenceServiceImpl();
	}

}
