package net.anotheria.anosite.photoserver.service.blur;

import net.anotheria.anoprise.metafactory.ServiceFactory;

/**
 * BlurSettingsService factory.
 *
 * @author h3ll
 */
public class BlurSettingsServiceFactory implements ServiceFactory<BlurSettingsService> {

	@Override
	public BlurSettingsService create() {
		return new BlurSettingsServiceImpl();
	}
}
