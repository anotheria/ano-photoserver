package net.anotheria.anosite.photoserver.service.blur;

import net.anotheria.anoprise.metafactory.ServiceFactory;

/**
 * BlurSettingsService factory.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class BlurSettingsServiceFactory implements ServiceFactory<BlurSettingsService> {

	/** {@inheritDoc} */
	@Override
	public BlurSettingsService create() {
		return new BlurSettingsServiceImpl();
	}
}
