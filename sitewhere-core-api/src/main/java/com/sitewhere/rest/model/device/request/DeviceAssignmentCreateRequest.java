/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.request.PersistentEntityCreateRequest;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;

/**
 * Holds fields needed to create a device assignment.
 * 
 * @author Derek Adams
 */
@JsonInclude(Include.NON_NULL)
public class DeviceAssignmentCreateRequest extends PersistentEntityCreateRequest
	implements IDeviceAssignmentCreateRequest {

    /** Serialization version identifier */
    private static final long serialVersionUID = -6880578458870122016L;

    /** Device token */
    private String deviceToken;

    /** Customer token */
    private String customerToken;

    /** Area token */
    private String areaToken;

    /** Asset token */
    private String assetToken;

    /** Status */
    private DeviceAssignmentStatus status;

    /*
     * @see com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#
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
     * @see com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#
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
     * @see
     * com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#getAreaToken(
     * )
     */
    @Override
    public String getAreaToken() {
	return areaToken;
    }

    public void setAreaToken(String areaToken) {
	this.areaToken = areaToken;
    }

    /*
     * @see
     * com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#getAssetToken
     * ()
     */
    @Override
    public String getAssetToken() {
	return assetToken;
    }

    public void setAssetToken(String assetToken) {
	this.assetToken = assetToken;
    }

    /*
     * @see
     * com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest#getStatus()
     */
    @Override
    public DeviceAssignmentStatus getStatus() {
	return status;
    }

    public void setStatus(DeviceAssignmentStatus status) {
	this.status = status;
    }

    public static class Builder {

	/** Request being built */
	private DeviceAssignmentCreateRequest request = new DeviceAssignmentCreateRequest();

	public Builder(String deviceToken, String customerToken, String areaToken, String assetToken) {
	    request.setDeviceToken(deviceToken);
	    request.setCustomerToken(customerToken);
	    request.setAreaToken(areaToken);
	    request.setAssetToken(assetToken);
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	public DeviceAssignmentCreateRequest build() {
	    return request;
	}
    }
}