/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import java.io.Serializable;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;

/**
 * Model object used to create a new {@link DeviceLocation} via REST APIs.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceLocationCreateRequest extends DeviceEventCreateRequest
	implements IDeviceLocationCreateRequest, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -7160866457228082338L;

    /** Latitude value */
    private Double latitude;

    /** Longitude value */
    private Double longitude;

    /** Elevation value */
    private Double elevation;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.request.IDeviceLocationCreateRequest#getLatitude
     * ()
     */
    public Double getLatitude() {
	return latitude;
    }

    public void setLatitude(Double latitude) {
	this.latitude = latitude;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceLocationCreateRequest#
     * getLongitude()
     */
    public Double getLongitude() {
	return longitude;
    }

    public void setLongitude(Double longitude) {
	this.longitude = longitude;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceLocationCreateRequest#
     * getElevation()
     */
    public Double getElevation() {
	return elevation;
    }

    public void setElevation(Double elevation) {
	this.elevation = elevation;
    }

    public static class Builder extends DeviceEventCreateRequest.Builder<DeviceLocationCreateRequest> {

	private DeviceLocationCreateRequest request = new DeviceLocationCreateRequest();

	public Builder(double latitude, double longitude) {
	    request.setLatitude(latitude);
	    request.setLongitude(longitude);
	    request.setElevation(0.0);
	}

	public Builder withElevation(double value) {
	    request.setElevation(value);
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
	public DeviceLocationCreateRequest getRequest() {
	    return request;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.rest.model.device.event.request.
	 * DeviceEventCreateRequest.Builder# build()
	 */
	@Override
	public DeviceLocationCreateRequest build() {
	    return request;
	}
    }
}