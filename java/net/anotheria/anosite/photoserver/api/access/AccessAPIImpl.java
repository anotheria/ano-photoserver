package net.anotheria.anosite.photoserver.api.access;

import net.anotheria.anoplass.api.APIInitException;
import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * {@link AccessAPI} main implementation.
 *
 * @author Alexandr Bolbat
 */
public class AccessAPIImpl implements AccessAPI {

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessAPIImpl.class);
	/**
	 * {@link AccessProvider} instance.
	 */
	private AccessProvider accessProvider;

	@Override
	public void init() throws APIInitException {
		accessProvider = new DefaultAccessProvider(); // initially set-up default provider
	}

	@Override
	public void deInit() {

	}

	@Override
	public boolean isAllowedForMe(PhotoAction action, long photoId) {
		return false;

	}

	@Override
	public boolean isAllowedForMe(AlbumAction action, long albumId) {
		return false;
	}

	@Override
	public ViewAccessResponse isViewAllowed(final long photoId, final Map<AccessParameter, String> parameters) {
		return accessProvider.isViewAllowed(photoId, parameters);
	}

	@Override
	public ViewAccessResponse isViewAllowed(final PhotoVO photo, final Map<AccessParameter, String> parameters) {
		if (photo == null)
			throw new IllegalArgumentException("photo is null");

		return accessProvider.isViewAllowed(photo, parameters);
	}

	@Override
	public void registerAccessProvider(final AccessProvider accessProvider) {
		if (accessProvider == null)
			throw new IllegalArgumentException("accessProvider is null");

		this.accessProvider = accessProvider;
	}
}
