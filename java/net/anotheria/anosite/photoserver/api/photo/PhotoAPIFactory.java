package net.anotheria.anosite.photoserver.api.photo;

import net.anotheria.anoplass.api.APIFactory;

/**
 * {@link net.anotheria.anosite.photoserver.api.photo.PhotoAPI} factory for creating {@link net.anotheria.anosite.photoserver.api.photo.PhotoAPIImpl} instance.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class PhotoAPIFactory implements APIFactory<PhotoAPI> {

	/** {@inheritDoc} */
	@Override
	public PhotoAPI createAPI() {
		return new PhotoAPIImpl();
	};

}
