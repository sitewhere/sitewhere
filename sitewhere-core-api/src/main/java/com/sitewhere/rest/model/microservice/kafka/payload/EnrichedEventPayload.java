/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.kafka.payload;

import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.microservice.kafka.payload.IEnrichedEventPayload;

/**
 * Event payload that has been enriched with extra device/assignment data that
 * may be useful in processing.
 * 
 * @author Derek
 */
public class EnrichedEventPayload implements IEnrichedEventPayload {

    /** Extra context */
    private IDeviceEventContext eventContext;

    /** Event */
    private IDeviceEvent event;

    /*
     * @see com.sitewhere.spi.microservice.kafka.payload.IEnrichedEventPayload#
     * getEventContext()
     */
    @Override
    public IDeviceEventContext getEventContext() {
	return eventContext;
    }

    public void setEventContext(IDeviceEventContext eventContext) {
	this.eventContext = eventContext;
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.kafka.payload.IEnrichedEventPayload#getEvent()
     */
    @Override
    public IDeviceEvent getEvent() {
	return event;
    }

    public void setEvent(IDeviceEvent event) {
	this.event = event;
    }
}