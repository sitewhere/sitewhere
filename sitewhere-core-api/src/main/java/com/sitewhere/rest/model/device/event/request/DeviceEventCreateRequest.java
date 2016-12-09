/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.sitewhere.rest.model.datatype.JsonDateSerializer;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;

/**
 * Holds common fields for creating {@link DeviceEvent} subclasses.
 * 
 * @author Derek
 */
public class DeviceEventCreateRequest implements IDeviceEventCreateRequest, Serializable {

    /** Serialization version identifier */
    private static final long serialVersionUID = -8906177904822194407L;

    /** Date event occurred */
    private Date eventDate;

    /** Indicates whether device assignment state should be updated */
    private boolean updateState = false;

    /** Metadata values */
    private Map<String, String> metadata;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.request.IDeviceEventCreateRequest#getEventDate()
     */
    @JsonSerialize(using = JsonDateSerializer.class)
    public Date getEventDate() {
	return eventDate;
    }

    public void setEventDate(Date eventDate) {
	this.eventDate = eventDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest#
     * isUpdateState()
     */
    public boolean isUpdateState() {
	return updateState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest#
     * setUpdateState (boolean)
     */
    public void setUpdateState(boolean updateState) {
	this.updateState = updateState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest#
     * getMetadata()
     */
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    /**
     * Uses builder pattern to create device event requests.
     * 
     * @author Derek
     */
    public abstract static class Builder<T extends DeviceEventCreateRequest> {

	// Request type implemented in subclasses.
	public abstract T getRequest();

	// Build the create request.
	public abstract T build();

	public Builder<T> on(Date date) {
	    getRequest().setEventDate(date);
	    return this;
	}

	public Builder<T> trackState() {
	    getRequest().setUpdateState(true);
	    return this;
	}
    }
}