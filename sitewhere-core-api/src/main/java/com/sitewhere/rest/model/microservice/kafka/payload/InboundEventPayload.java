/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.kafka.payload;

import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.microservice.kafka.payload.IInboundEventPayload;

/**
 * Model object for event payload received from event sources from a tenant.
 * 
 * @author Derek
 */
public class InboundEventPayload implements IInboundEventPayload {

    /** Event source id */
    private String sourceId;

    /** Device token */
    private String deviceToken;

    /** Id of event originator */
    private String originator;

    /** Event create request */
    private IDeviceEventCreateRequest eventCreateRequest;

    /*
     * @see com.sitewhere.spi.microservice.kafka.payload.IInboundEventPayload#
     * getSourceId()
     */
    @Override
    public String getSourceId() {
	return sourceId;
    }

    public void setSourceId(String sourceId) {
	this.sourceId = sourceId;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.payload.IInboundEventPayload#
     * getDeviceToken()
     */
    @Override
    public String getDeviceToken() {
	return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
	this.deviceToken = deviceToken;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.payload.IInboundEventPayload#
     * getOriginator()
     */
    @Override
    public String getOriginator() {
	return originator;
    }

    public void setOriginator(String originator) {
	this.originator = originator;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.payload.IInboundEventPayload#
     * getEventCreateRequest()
     */
    @Override
    public IDeviceEventCreateRequest getEventCreateRequest() {
	return eventCreateRequest;
    }

    public void setEventCreateRequest(IDeviceEventCreateRequest eventCreateRequest) {
	this.eventCreateRequest = eventCreateRequest;
    }
}