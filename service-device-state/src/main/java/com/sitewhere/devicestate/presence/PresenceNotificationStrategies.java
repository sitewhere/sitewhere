/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.presence;

import com.sitewhere.devicestate.spi.IPresenceNotificationStrategy;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.state.IDeviceState;

/**
 * Various implementations of {@link IPresenceNotificationStrategy}.
 */
public class PresenceNotificationStrategies {

    /**
     * Only stores/sends a single presence missing notification and keeps state. If
     * presence is regained, the state will be reset.
     */
    public static class SendOnceNotificationStrategy implements IPresenceNotificationStrategy {

	/*
	 * @see com.sitewhere.devicestate.spi.IPresenceNotificationStrategy#
	 * shouldGenerateEvent(com.sitewhere.spi.device.state.IDeviceState,
	 * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
	 */
	@Override
	public boolean shouldGenerateEvent(IDeviceState deviceState, IDeviceStateChangeCreateRequest request)
		throws SiteWhereException {
	    if (deviceState.getPresenceMissingDate() != null) {
		return false;
	    }
	    return true;
	}
    }
}