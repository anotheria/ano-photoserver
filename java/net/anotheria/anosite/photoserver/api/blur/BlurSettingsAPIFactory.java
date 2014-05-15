package net.anotheria.anosite.photoserver.api.blur;

import net.anotheria.anoplass.api.APIFactory;

/**
 * BlurSettingsAPI factory.
 *
 * @author h3ll
 */
public class BlurSettingsAPIFactory implements APIFactory<BlurSettingsAPI> {
	@Override
	public BlurSettingsAPI createAPI() {

		return new BlurSettingsAPIImpl();
	}
}
