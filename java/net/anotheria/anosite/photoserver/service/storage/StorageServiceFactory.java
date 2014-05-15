package net.anotheria.anosite.photoserver.service.storage;

import net.anotheria.anoprise.metafactory.ServiceFactory;

/**
 * Factory for instantiating {@link StorageService} main implementation.
 * 
 * @author Alexandr Bolbat
 */
public class StorageServiceFactory implements ServiceFactory<StorageService> {

	@Override
	public StorageService create() {
		return new StorageServiceImpl();
	}
}
