/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.request.PersistentEntityCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;

/**
 * Holds fields needed to create a new device stream.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class DeviceStreamCreateRequest extends PersistentEntityCreateRequest implements IDeviceStreamCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = -8887616206756385150L;

    /** Stream id */
    private String streamId;

    /** Content type */
    private String contentType;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest#
     * getStreamId()
     */
    public String getStreamId() {
	return streamId;
    }

    public void setStreamId(String streamId) {
	this.streamId = streamId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest#
     * getContentType()
     */
    public String getContentType() {
	return contentType;
    }

    public void setContentType(String contentType) {
	this.contentType = contentType;
    }
}