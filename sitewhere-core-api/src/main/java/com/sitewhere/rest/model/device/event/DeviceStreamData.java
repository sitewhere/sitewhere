/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import java.io.Serializable;

import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceStreamData;

/**
 * Holds a single chunk of data from a binary stream.
 * 
 * @author Derek
 */
public class DeviceStreamData extends DeviceEvent implements IDeviceStreamData, Serializable {

    /** Serial version UID */
    private static final long serialVersionUID = 7241224075838793803L;

    /** Stream id this chunk belongs to */
    private String streamId;

    /** Sequence number for ordering chunks */
    private Long sequenceNumber;

    /** Chunk data */
    private byte[] data;

    public DeviceStreamData() {
	super(DeviceEventType.StreamData);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceStreamData#getStreamId()
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
     * @see com.sitewhere.spi.device.event.IDeviceStreamData#getSequenceNumber()
     */
    public Long getSequenceNumber() {
	return sequenceNumber;
    }

    public void setSequenceNumber(Long sequenceNumber) {
	this.sequenceNumber = sequenceNumber;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.event.IDeviceStreamData#getData()
     */
    public byte[] getData() {
	return data;
    }

    public void setData(byte[] data) {
	this.data = data;
    }
}