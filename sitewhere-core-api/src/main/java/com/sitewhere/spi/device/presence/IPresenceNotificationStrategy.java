/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.presence;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;

/**
 * Indicates how often events should be generated for non-present devices.
 * 
 * @author Derek
 */
public interface IPresenceNotificationStrategy {

    /**
     * Based on the given data, chooses whether to store an event which will in
     * turn fire notifications to the outbound processing chain.
     * 
     * @param assignment
     *            affected device assignment
     * @param request
     *            state change request
     * @return true if event should be generated, false if not
     * @throws SiteWhereException
     *             if error in implementation
     */
    public boolean shouldGenerateEvent(IDeviceAssignment assignment, IDeviceStateChangeCreateRequest request)
	    throws SiteWhereException;
}