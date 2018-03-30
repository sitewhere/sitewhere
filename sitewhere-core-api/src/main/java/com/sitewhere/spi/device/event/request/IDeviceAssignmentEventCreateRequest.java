/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.request;

import java.util.UUID;

/**
 * Holds information required to create an event for a device assignment.
 * 
 * @author Derek
 */
public interface IDeviceAssignmentEventCreateRequest {

    /**
     * Get id of device assignment event will be posted to.
     * 
     * @return
     */
    public UUID getDeviceAssignmentId();

    /**
     * Get request information for event to be posted.
     * 
     * @return
     */
    public IDeviceEventCreateRequest getRequest();
}