/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import java.util.UUID;

import com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest;

/**
 * Implementation of {@link ISendDeviceStreamDataRequest}.
 * 
 * @author Derek
 */
public class SendDeviceStreamDataRequest implements ISendDeviceStreamDataRequest {

    /** Stream id */
    private UUID streamId;

    /** Sequence number */
    private long sequenceNumber;

    /*
     * @see com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest#
     * getStreamId()
     */
    @Override
    public UUID getStreamId() {
	return streamId;
    }

    public void setStreamId(UUID streamId) {
	this.streamId = streamId;
    }

    /*
     * @see com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest#
     * getSequenceNumber()
     */
    @Override
    public long getSequenceNumber() {
	return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
	this.sequenceNumber = sequenceNumber;
    }
}