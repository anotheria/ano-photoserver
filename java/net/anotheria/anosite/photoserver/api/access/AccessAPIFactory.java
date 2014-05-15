package net.anotheria.anosite.photoserver.api.access;

import net.anotheria.anoplass.api.APIFactory;

/**
 * {@link AccessAPI} factory for creating {@link AccessAPIImpl} instance.
 * 
 * @author Alexandr Bolbat
 */
public class AccessAPIFactory implements APIFactory<AccessAPI> {

	@Override
	public AccessAPI createAPI() {
		return new AccessAPIImpl();
	}

}