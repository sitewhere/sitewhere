/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.presence;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.presence.IPresenceNotificationStrategy;

/**
 * Various implementations of {@link IPresenceNotificationStrategy}.
 * 
 * @author Derek
 */
public class PresenceNotificationStrategies {

	/**
	 * Only stores/sends a single presence missing notification and keeps state. If
	 * presence is regained, the state will be reset.
	 * 
	 * @author Derek
	 *
	 */
	public static class SendOnceNotificationStrategy implements IPresenceNotificationStrategy {

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.sitewhere.spi.device.presence.IPresenceNotificationStrategy#
		 * shouldGenerateEvent(com.sitewhere.spi.device.IDeviceAssignment,
		 * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
		 */
		@Override
		public boolean shouldGenerateEvent(IDeviceAssignment assignment,
				IDeviceStateChangeCreateRequest request) throws SiteWhereException {
			if (assignment.getState().getPresenceMissingDate() != null) {
				return false;
			}
			return true;
		}
	}
}