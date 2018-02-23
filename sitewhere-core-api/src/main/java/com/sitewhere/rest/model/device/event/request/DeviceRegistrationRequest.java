/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest;
import com.sitewhere.spi.device.event.state.RegistrationState;
import com.sitewhere.spi.device.event.state.StateChangeCategory;
import com.sitewhere.spi.device.event.state.StateChangeType;

/**
 * Default model implementation of {@link IDeviceRegistrationRequest}.
 * 
 * @author Derek
 */
@JsonIgnoreProperties
@JsonInclude(Include.NON_NULL)
public class DeviceRegistrationRequest extends DeviceStateChangeCreateRequest
	implements IDeviceRegistrationRequest, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -6396459122879336428L;

    /** Data map identifier for hardware id */
    public static final String DATA_DEVICE_TOKEN = "deviceToken";

    /** Data map identifier for device type token */
    public static final String DATA_DEVICE_TYPE_TOKEN = "deviceTypeToken";

    /** Data map identifier for area token */
    public static final String DATA_AREA_TOKEN = "areaToken";

    public DeviceRegistrationRequest() {
	super(StateChangeCategory.Registration, StateChangeType.Registration_Requested,
		RegistrationState.Unregistered.name(), RegistrationState.Registered.name());
    }

    /*
     * @see com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest#
     * getDeviceToken()
     */
    @Override
    public String getDeviceToken() {
	return getData().get(DATA_DEVICE_TOKEN);
    }

    public void setDeviceToken(String deviceToken) {
	getData().put(DATA_DEVICE_TOKEN, deviceToken);
    }

    /*
     * @see com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest#
     * getDeviceTypeToken()
     */
    @Override
    public String getDeviceTypeToken() {
	return getData().get(DATA_DEVICE_TYPE_TOKEN);
    }

    public void setDeviceTypeToken(String specificationToken) {
	getData().put(DATA_DEVICE_TYPE_TOKEN, specificationToken);
    }

    /*
     * @see com.sitewhere.spi.device.event.request.IDeviceRegistrationRequest#
     * getAreaToken()
     */
    @Override
    public String getAreaToken() {
	return getData().get(DATA_AREA_TOKEN);
    }

    public void setAreaToken(String token) {
	getData().put(DATA_AREA_TOKEN, token);
    }
}