package net.anotheria.anosite.photoserver.api.access;

import net.anotheria.anoplass.api.APIInitException;
import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;
import net.anotheria.moskito.aop.annotation.Accumulate;
import net.anotheria.moskito.aop.annotation.Accumulates;
import net.anotheria.moskito.aop.annotation.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * {@link net.anotheria.anosite.photoserver.api.access.AccessAPI} main implementation.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
@Monitor(producerId = "PS_AccessAPIImpl", category = "api", subsystem = "photoserver")
@Accumulates({
		@Accumulate(valueName = "Avg", intervalName = "5m"),
		@Accumulate(valueName = "Avg", intervalName = "1h"),
		@Accumulate(valueName = "Req", intervalName = "5m"),
		@Accumulate(valueName = "Req", intervalName = "1h"),
		@Accumulate(valueName = "Err", intervalName = "5m"),
		@Accumulate(valueName = "Err", intervalName = "1h"),
		@Accumulate(valueName = "Time", intervalName = "5m"),
		@Accumulate(valueName = "Time", intervalName = "1h")
})
public class AccessAPIImpl implements AccessAPI {

	/**
	 * {@link Logger} instance.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(AccessAPIImpl.class);
	/**
	 * {@link AccessProvider} instance.
	 */
	private AccessProvider accessProvider;

	/** {@inheritDoc} */
	@Override
	public void init() throws APIInitException {
		accessProvider = new DefaultAccessProvider(); // initially set-up default provider
	}

	/** {@inheritDoc} */
	@Override
	public void deInit() {

	}

	/** {@inheritDoc} */
	@Override
	public boolean isAllowedForMe(PhotoAction action, long photoId) {
		return false;

	}

	/** {@inheritDoc} */
	@Override
	public boolean isAllowedForMe(AlbumAction action, long albumId) {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public ViewAccessResponse isViewAllowed(final long photoId, final Map<AccessParameter, String> parameters) {
		return accessProvider.isViewAllowed(photoId, parameters);
	}

	/** {@inheritDoc} */
	@Override
	public ViewAccessResponse isViewAllowed(final PhotoVO photo, final Map<AccessParameter, String> parameters) {
		if (photo == null)
			throw new IllegalArgumentException("photo is null");

		return accessProvider.isViewAllowed(photo, parameters);
	}

	/** {@inheritDoc} */
	@Override
	public void registerAccessProvider(final AccessProvider accessProvider) {
		if (accessProvider == null)
			throw new IllegalArgumentException("accessProvider is null");

		this.accessProvider = accessProvider;
	}
}
