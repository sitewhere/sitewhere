/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.streaming;

import com.sitewhere.spi.device.event.streaming.IEventStreamAck;

/**
 * Model object for an event stream ack.
 * 
 * @author Derek
 */
public class EventStreamAck implements IEventStreamAck {

    /** Count of total events processed */
    private long processedEventCount;

    /*
     * @see com.sitewhere.spi.device.event.streaming.IEventStreamAck#
     * getProcessedEventCount()
     */
    @Override
    public long getProcessedEventCount() {
	return processedEventCount;
    }

    public void setProcessedEventCount(long processedEventCount) {
	this.processedEventCount = processedEventCount;
    }
}