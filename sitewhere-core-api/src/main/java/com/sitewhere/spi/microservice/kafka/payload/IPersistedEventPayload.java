/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.kafka.payload;

import java.util.UUID;

import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Get payload passed for inbound events after they have been persisted to the
 * event datastore.
 * 
 * @author Derek
 */
public interface IPersistedEventPayload {

    /**
     * Get device id.
     * 
     * @return
     */
    public UUID getDeviceId();

    /**
     * Get device event details.
     * 
     * @return
     */
    public IDeviceEvent getEvent();
}