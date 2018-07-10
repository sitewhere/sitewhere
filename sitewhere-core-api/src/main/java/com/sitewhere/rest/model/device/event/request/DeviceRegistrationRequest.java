/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;

/**
 * Default model implementation of {@link IDeviceRegistrationRequest}.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceRegistrationRequest extends MetadataProvider implements IDeviceRegistrationRequest {

    /** Serialization version identifier */
    private static final long serialVersionUID = -6396459122879336428L;

    /** Device token */
    private String deviceToken;

    /** Device type token */
    private String deviceTypeToken;

    /** Customer token */
    private String customerToken;

    /** Area token */
    private String areaToken;

    /*
     * @see com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest#
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
     * @see com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest#
     * getDeviceTypeToken()
     */
    @Override
    public String getDeviceTypeToken() {
	return deviceTypeToken;
    }

    public void setDeviceTypeToken(String deviceTypeToken) {
	this.deviceTypeToken = deviceTypeToken;
    }

    /*
     * @see com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest#
     * getCustomerToken()
     */
    @Override
    public String getCustomerToken() {
	return customerToken;
    }

    public void setCustomerToken(String customerToken) {
	this.customerToken = customerToken;
    }

    /*
     * @see com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest#
     * getAreaToken()
     */
    @Override
    public String getAreaToken() {
	return areaToken;
    }

    public void setAreaToken(String areaToken) {
	this.areaToken = areaToken;
    }
}