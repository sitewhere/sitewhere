/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.kafka.payload;

import java.util.UUID;

import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.microservice.kafka.payload.IPersistedEventPayload;

/**
 * Model object for a payload that has been persisted to the event datastore.
 * 
 * @author Derek
 */
public class PersistedEventPayload implements IPersistedEventPayload {

    /** Device id */
    private UUID deviceId;

    /** Device event */
    private IDeviceEvent event;

    /*
     * @see com.sitewhere.spi.microservice.kafka.payload.IPersistedEventPayload#
     * getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.payload.IPersistedEventPayload#
     * getEvent()
     */
    @Override
    public IDeviceEvent getEvent() {
	return event;
    }

    public void setEvent(IDeviceEvent event) {
	this.event = event;
    }
}