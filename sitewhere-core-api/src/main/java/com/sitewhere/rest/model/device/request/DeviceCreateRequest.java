/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.device.DeviceElementMapping;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;

/**
 * Holds fields needed to create a new device.
 * 
 * @author Derek Adams
 */
@JsonInclude(Include.NON_NULL)
public class DeviceCreateRequest implements IDeviceCreateRequest, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = 5102270168736590229L;

    /** Hardware id for new device */
    private String hardwareId;

    /** Site token */
    private String siteToken;

    /** Device specification token */
    private String specificationToken;

    /** Parent hardware id (if nested) */
    private String parentHardwareId;

    /** Indicates whether parent hardware id should be removed */
    private Boolean removeParentHardwareId;

    /** List of device element mappings */
    private List<DeviceElementMapping> deviceElementMappings;

    /** Comments */
    private String comments;

    /** Device status indicator */
    private String status;

    /** Metadata values */
    private Map<String, String> metadata;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.request.IDeviceCreateRequest#getHardwareId()
     */
    public String getHardwareId() {
	return hardwareId;
    }

    public void setHardwareId(String hardwareId) {
	this.hardwareId = hardwareId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#getSiteToken()
     */
    public String getSiteToken() {
	return siteToken;
    }

    public void setSiteToken(String siteToken) {
	this.siteToken = siteToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#
     * getSpecificationToken()
     */
    public String getSpecificationToken() {
	return specificationToken;
    }

    public void setSpecificationToken(String specificationToken) {
	this.specificationToken = specificationToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.request.IDeviceCreateRequest#getParentHardwareId
     * ()
     */
    public String getParentHardwareId() {
	return parentHardwareId;
    }

    public void setParentHardwareId(String parentHardwareId) {
	this.parentHardwareId = parentHardwareId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#
     * isRemoveParentHardwareId()
     */
    public Boolean isRemoveParentHardwareId() {
	return removeParentHardwareId;
    }

    public void setRemoveParentHardwareId(Boolean removeParentHardwareId) {
	this.removeParentHardwareId = removeParentHardwareId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#
     * getDeviceElementMappings()
     */
    @SuppressWarnings("unchecked")
    public List<IDeviceElementMapping> getDeviceElementMappings() {
	return (List<IDeviceElementMapping>) (List<? extends IDeviceElementMapping>) deviceElementMappings;
    }

    public void setDeviceElementMappings(List<DeviceElementMapping> deviceElementMappings) {
	this.deviceElementMappings = deviceElementMappings;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#getComments()
     */
    public String getComments() {
	return comments;
    }

    public void setComments(String comments) {
	this.comments = comments;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.request.IDeviceCreateRequest#getStatus()
     */
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
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public static class Builder {

	/** Request being built */
	private DeviceCreateRequest request = new DeviceCreateRequest();

	public Builder(IDevice api) {
	    request.setSiteToken(api.getSiteToken());
	    request.setSpecificationToken(api.getSpecificationToken());
	    request.setHardwareId(api.getHardwareId());
	    request.setStatus(api.getStatus());
	    request.setComments(api.getComments());
	    request.setParentHardwareId(api.getParentHardwareId());
	    if (api.getDeviceElementMappings() != null) {
		request.setDeviceElementMappings(new ArrayList<DeviceElementMapping>());
		request.getDeviceElementMappings().addAll(api.getDeviceElementMappings());
	    }
	    if (api.getMetadata() != null) {
		request.setMetadata(new HashMap<String, String>());
		request.getMetadata().putAll(api.getMetadata());
	    }
	}

	public Builder(String siteToken, String specificationToken, String hardwareId) {
	    request.setSiteToken(siteToken);
	    request.setSpecificationToken(specificationToken);
	    request.setHardwareId(hardwareId);
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