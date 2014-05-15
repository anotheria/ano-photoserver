package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.anoplass.api.APIFactory;

/**
 * {@link PhotoAPI} factory for creating {@link PhotoAPIImpl} instance.
 * 
 * @author Alexandr Bolbat
 */
public class PhotoAPIFactory implements APIFactory<PhotoAPI> {

	@Override
	public PhotoAPI createAPI() {
		return new PhotoAPIImpl();
	};

}
