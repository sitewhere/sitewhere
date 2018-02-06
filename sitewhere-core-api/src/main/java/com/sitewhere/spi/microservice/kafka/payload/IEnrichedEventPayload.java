/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.kafka.payload;

import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;

/**
 * Event payload that has been enriched with extra device/assignment data that
 * may be useful in processing.
 * 
 * @author Derek
 */
public interface IEnrichedEventPayload {

    /**
     * Get extra context information for event.
     * 
     * @return
     */
    public IDeviceEventContext getEventContext();

    /**
     * Get event data.
     * 
     * @return
     */
    public IDeviceEvent getEvent();
}