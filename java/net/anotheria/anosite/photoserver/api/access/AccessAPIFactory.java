package net.anotheria.anosite.photoserver.api.access;

import net.anotheria.anoplass.api.APIFactory;

/**
 * {@link net.anotheria.anosite.photoserver.api.access.AccessAPI} factory for creating {@link net.anotheria.anosite.photoserver.api.access.AccessAPIImpl} instance.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class AccessAPIFactory implements APIFactory<AccessAPI> {

	/** {@inheritDoc} */
	@Override
	public AccessAPI createAPI() {
		return new AccessAPIImpl();
	}

}
