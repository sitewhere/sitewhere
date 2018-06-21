/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.state.IDeviceState;

/**
 * Indicates how often events should be generated for non-present devices.
 * 
 * @author Derek
 */
public interface IPresenceNotificationStrategy {

    /**
     * Based on the given data, chooses whether to trigger a state change event
     * based on the given device state information.
     * 
     * @param deviceState
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public boolean shouldGenerateEvent(IDeviceState deviceState, IDeviceStateChangeCreateRequest request)
	    throws SiteWhereException;
}