/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.request;

import java.util.UUID;

/**
 * Request from a device to send a chunk of device stream data.
 * 
 * @author Derek
 */
public interface ISendDeviceStreamDataRequest {

    /**
     * Get stream id to send from.
     * 
     * @return
     */
    public UUID getStreamId();

    /**
     * Get sequence number of chunk to send.
     * 
     * @return
     */
    public long getSequenceNumber();
}