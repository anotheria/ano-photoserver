package net.anotheria.anosite.photoserver.service.blur.persistence;

import net.anotheria.anoprise.metafactory.ServiceFactory;

/**
 * BlurSettingsPersistenceService factory.
 *
 * @author h3ll
 */
public class BlurSettingsPersistenceServiceFactory implements ServiceFactory<BlurSettingsPersistenceService> {
	@Override
	public BlurSettingsPersistenceService create() {
		return new BlurSettingsPersistenceServiceImpl();
	}
}
