/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.kafka.payload;

import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.microservice.kafka.payload.IDeviceRegistrationPayload;

/**
 * Model object for device registration payload.
 * 
 * @author Derek
 */
public class DeviceRegistrationPayload implements IDeviceRegistrationPayload {

    /** Serial version UID */
    private static final long serialVersionUID = -6254913551092919151L;

    /** Event source id */
    private String sourceId;

    /** Device token */
    private String deviceToken;

    /** Id of event originator */
    private String originator;

    /** Event create request */
    private IDeviceRegistrationRequest deviceRegistrationRequest;

    /*
     * @see com.sitewhere.spi.microservice.kafka.payload.IDeviceRegistrationPayload#
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
     * @see com.sitewhere.spi.microservice.kafka.payload.IDeviceRegistrationPayload#
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
     * @see com.sitewhere.spi.microservice.kafka.payload.IDeviceRegistrationPayload#
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
     * @see com.sitewhere.spi.microservice.kafka.payload.IDeviceRegistrationPayload#
     * getDeviceRegistrationRequest()
     */
    @Override
    public IDeviceRegistrationRequest getDeviceRegistrationRequest() {
	return deviceRegistrationRequest;
    }

    public void setDeviceRegistrationRequest(IDeviceRegistrationRequest deviceRegistrationRequest) {
	this.deviceRegistrationRequest = deviceRegistrationRequest;
    }
}
