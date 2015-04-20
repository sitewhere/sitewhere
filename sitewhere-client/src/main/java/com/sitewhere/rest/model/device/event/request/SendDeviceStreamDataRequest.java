/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request;

import com.sitewhere.spi.device.event.request.ISendDeviceStreamDataRequest;

/**
 * Implementation of {@link ISendDeviceStreamDataRequest}.
 * 
 * @author Derek
 */
public class SendDeviceStreamDataRequest implements ISendDeviceStreamDataRequest {

	/** Stream id */
	private String streamId;

	/** Sequence number */
	private long sequenceNumber;

	public String getStreamId() {
		return streamId;
	}

	public void setStreamId(String streamId) {
		this.streamId = streamId;
	}

	public long getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(long sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
}