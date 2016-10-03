package net.anotheria.anosite.photoserver.api.blur;

import net.anotheria.anoplass.api.APIFactory;

/**
 * BlurSettingsAPI factory.
 *
 * @author h3ll
 * @version $Id: $Id
 */
public class BlurSettingsAPIFactory implements APIFactory<BlurSettingsAPI> {
	/** {@inheritDoc} */
	@Override
	public BlurSettingsAPI createAPI() {

		return new BlurSettingsAPIImpl();
	}
}
