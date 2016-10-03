package net.anotheria.anosite.photoserver.api.access;

import net.anotheria.anosite.photoserver.presentation.delivery.DeliveryConfig;
import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;

import java.util.Map;

/**
 * Default access provider.
 *
 * @author Alexandr Bolbat
 * @version $Id: $Id
 */
public class DefaultAccessProvider implements AccessProvider {

	/** {@inheritDoc} */
	@Override
	public ViewAccessResponse isViewAllowed(final long photoId, final Map<AccessParameter, String> parameters) {
		return DeliveryConfig.getInstance().getDefaultViewAccessResponse();
	}

	/** {@inheritDoc} */
	@Override
	public ViewAccessResponse isViewAllowed(final PhotoVO photo, final Map<AccessParameter, String> parameters) {
		return DeliveryConfig.getInstance().getDefaultViewAccessResponse();
	}
}
