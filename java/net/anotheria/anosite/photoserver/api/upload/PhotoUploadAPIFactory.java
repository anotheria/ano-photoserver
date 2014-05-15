package net.anotheria.anosite.photoserver.api.upload;

import net.anotheria.anoplass.api.APIFactory;

/**
 * {@link PhotoUploadAPI} factory for creating {@link PhotoUploadAPIImpl} instance.
 * 
 * @author otoense
 */
public class PhotoUploadAPIFactory implements APIFactory<PhotoUploadAPI> {

	@Override
	public PhotoUploadAPI createAPI() {
		return new PhotoUploadAPIImpl();
	};

}