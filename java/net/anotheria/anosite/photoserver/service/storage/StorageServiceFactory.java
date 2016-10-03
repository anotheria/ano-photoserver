package net.anotheria.anosite.photoserver.service.storage;

import net.anotheria.anoprise.metafactory.ServiceFactory;

/**
 * Factory for instantiating {@link net.anotheria.anosite.photoserver.service.storage.StorageService} main implementation.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class StorageServiceFactory implements ServiceFactory<StorageService> {

	/** {@inheritDoc} */
	@Override
	public StorageService create() {
		return new StorageServiceImpl();
	}
}
