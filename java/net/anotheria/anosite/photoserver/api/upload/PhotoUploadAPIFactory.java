package net.anotheria.anosite.photoserver.api.upload;

import net.anotheria.anoplass.api.APIFactory;

/**
 * {@link net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPI} factory for creating {@link net.anotheria.anosite.photoserver.api.upload.PhotoUploadAPIImpl} instance.
 *
 * @author otoense
 * @version $Id: $Id
 */
public class PhotoUploadAPIFactory implements APIFactory<PhotoUploadAPI> {

	/** {@inheritDoc} */
	@Override
	public PhotoUploadAPI createAPI() {
		return new PhotoUploadAPIImpl();
	};

}
