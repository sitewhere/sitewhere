/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;

/**
 * Model object used to create a new {@link DeviceMeasurement} via REST APIs.
 * 
 * @author Derek
 */
@JsonIgnoreProperties
@JsonInclude(Include.NON_NULL)
public class DeviceMeasurementCreateRequest extends DeviceEventCreateRequest
	implements IDeviceMeasurementCreateRequest {

    /** Serialization version identifier */
    private static final long serialVersionUID = 9193083760712267587L;

    /** Measurement name */
    private String name;

    /** Measurement value */
    private double value;

    public DeviceMeasurementCreateRequest() {
	setEventType(DeviceEventType.Measurement);
    }

    /*
     * @see com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest#
     * getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest#
     * getValue()
     */
    @Override
    public double getValue() {
	return value;
    }

    public void setValue(double value) {
	this.value = value;
    }

    public static class Builder extends DeviceEventCreateRequest.Builder<DeviceMeasurementCreateRequest> {

	private DeviceMeasurementCreateRequest request = new DeviceMeasurementCreateRequest();

	public Builder() {
	}

	public Builder measurement(String name, double value) {
	    request.setName(name);
	    request.setValue(value);
	    return this;
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.request.
	 * DeviceEventCreateRequest.Builder# getRequest()
	 */
	@Override
	public DeviceMeasurementCreateRequest getRequest() {
	    return request;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.request.
	 * DeviceEventCreateRequest.Builder# build()
	 */
	@Override
	public DeviceMeasurementCreateRequest build() {
	    return request;
	}
    }
}