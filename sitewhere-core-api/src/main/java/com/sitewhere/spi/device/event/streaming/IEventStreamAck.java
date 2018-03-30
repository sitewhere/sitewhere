/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.streaming;

/**
 * Acknowledges processing of events via stream.
 * 
 * @author Derek
 */
public interface IEventStreamAck {

    /**
     * Get count of processed events.
     * 
     * @return
     */
    public long getProcessedEventCount();
}