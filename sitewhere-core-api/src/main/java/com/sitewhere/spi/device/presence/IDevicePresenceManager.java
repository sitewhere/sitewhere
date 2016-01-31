/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.presence;

import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Manages monitoring of device assignment state to determine when device presence
 * changes.
 * 
 * @author Derek
 */
public interface IDevicePresenceManager extends ITenantLifecycleComponent {

	/**
	 * Get notification strategy that controls how often presence events are sent.
	 * 
	 * @return
	 */
	public IPresenceNotificationStrategy getPresenceNotificationStrategy();
}