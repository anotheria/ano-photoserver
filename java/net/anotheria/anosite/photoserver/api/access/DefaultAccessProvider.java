package net.anotheria.anosite.photoserver.api.access;

import net.anotheria.anosite.photoserver.presentation.delivery.DeliveryConfig;
import net.anotheria.anosite.photoserver.shared.vo.PhotoVO;

import java.util.Map;

/**
 * Default access provider.
 *
 * @author Alexandr Bolbat
 */
public class DefaultAccessProvider implements AccessProvider {

	@Override
	public ViewAccessResponse isViewAllowed(final long photoId, final Map<AccessParameter, String> parameters) {
		return DeliveryConfig.getInstance().getDefaultViewAccessResponse();
	}

	@Override
	public ViewAccessResponse isViewAllowed(final PhotoVO photo, final Map<AccessParameter, String> parameters) {
		return DeliveryConfig.getInstance().getDefaultViewAccessResponse();
	}
}
