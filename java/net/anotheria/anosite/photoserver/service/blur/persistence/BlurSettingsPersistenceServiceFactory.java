package net.anotheria.anosite.photoserver.service.blur.persistence;

import net.anotheria.anoprise.metafactory.ServiceFactory;

/**
 * BlurSettingsPersistenceService factory.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class BlurSettingsPersistenceServiceFactory implements ServiceFactory<BlurSettingsPersistenceService> {
	/** {@inheritDoc} */
	@Override
	public BlurSettingsPersistenceService create() {
		return new BlurSettingsPersistenceServiceImpl();
	}
}
