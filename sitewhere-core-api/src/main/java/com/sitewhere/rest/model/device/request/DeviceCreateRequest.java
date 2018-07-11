/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;

/**
 * Fields needed to create/update a device.
 * 
 * @author Derek Adams
 */
@JsonInclude(Include.NON_NULL)
public class DeviceCreateRequest implements IDeviceCreateRequest {

    /** Serialization version identifier */
    private static final long serialVersionUID = 5102270168736590229L;

    /** Token for new device */
    private String token;

    /** Device type token */
    private String deviceTypeToken;

    /** Parent device token (if nested) */
    private String parentDeviceToken;

    /** Indicates whether parent hardware id should be removed */
    private Boolean removeParentHardwareId;

    /** List of device element mappings */
    private List<? extends IDeviceElementMapping> deviceElementMappings;

    /** Comments */
    private String comments;

    /** Device status indicator */
    private String status;

    /** Metadata values */
    private Map<String, String> metadata;

    /*
     * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#getToken()
     */
    @Override
    public String getToken() {
	return token;
    }

    public void setToken(String token) {
	this.token = token;
    }

    /*
     * @see
     * com.sitewhere.spi.device.request.IDeviceCreateRequest#getDeviceTypeToken()
     */
    @Override
    public String getDeviceTypeToken() {
	return deviceTypeToken;
    }

    public void setDeviceTypeToken(String deviceTypeToken) {
	this.deviceTypeToken = deviceTypeToken;
    }

    /*
     * @see
     * com.sitewhere.spi.device.request.IDeviceCreateRequest#getParentDeviceToken()
     */
    @Override
    public String getParentDeviceToken() {
	return parentDeviceToken;
    }

    public void setParentDeviceToken(String parentDeviceToken) {
	this.parentDeviceToken = parentDeviceToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#
     * isRemoveParentHardwareId()
     */
    @Override
    public Boolean isRemoveParentHardwareId() {
	return removeParentHardwareId;
    }

    public void setRemoveParentHardwareId(Boolean removeParentHardwareId) {
	this.removeParentHardwareId = removeParentHardwareId;
    }

    /*
     * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#
     * getDeviceElementMappings()
     */
    @Override
    public List<? extends IDeviceElementMapping> getDeviceElementMappings() {
	return deviceElementMappings;
    }

    public void setDeviceElementMappings(List<? extends IDeviceElementMapping> deviceElementMappings) {
	this.deviceElementMappings = deviceElementMappings;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#getComments()
     */
    @Override
    public String getComments() {
	return comments;
    }

    public void setComments(String comments) {
	this.comments = comments;
    }

    /*
     * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#getStatus()
     */
    @Override
    public String getStatus() {
	return status;
    }

    public void setStatus(String status) {
	this.status = status;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public static class Builder {

	/** Request being built */
	private DeviceCreateRequest request = new DeviceCreateRequest();

	public Builder(String deviceTypeToken, String token) {
	    request.setDeviceTypeToken(deviceTypeToken);
	    request.setToken(token);
	    request.setStatus(null);
	    request.setComments("");
	}

	public Builder withComment(String comments) {
	    request.setComments(comments);
	    return this;
	}

	public Builder metadata(String name, String value) {
	    if (request.getMetadata() == null) {
		request.setMetadata(new HashMap<String, String>());
	    }
	    request.getMetadata().put(name, value);
	    return this;
	}

	public DeviceCreateRequest build() {
	    return request;
	}
    }
}