/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.streaming.request;

import java.io.Serializable;

import com.sitewhere.rest.model.device.event.request.DeviceEventCreateRequest;
import com.sitewhere.rest.model.device.streaming.DeviceStreamData;
import com.sitewhere.spi.device.streaming.request.IDeviceStreamDataCreateRequest;

/**
 * Model object used to create a new {@link DeviceStreamData} via REST APIs.
 * 
 * @author Derek
 */
public class DeviceStreamDataCreateRequest extends DeviceEventCreateRequest
	implements IDeviceStreamDataCreateRequest, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = -8175812734171141445L;

    /** Stream id */
    private String streamId;

    /** Sequence number for ordering chunks */
    private long sequenceNumber;

    /** Chunk data */
    private byte[] data;

    public DeviceStreamDataCreateRequest() {
	setEventType(null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest#
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
     * @see com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest#
     * getSequenceNumber ()
     */
    public long getSequenceNumber() {
	return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
	this.sequenceNumber = sequenceNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest#
     * getData()
     */
    public byte[] getData() {
	return data;
    }

    public void setData(byte[] data) {
	this.data = data;
    }
}