/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event.request;

import java.util.Map;

import com.sitewhere.spi.device.streaming.IDeviceStream;

/**
 * Information needed to create a new {@link IDeviceStream}.
 * 
 * @author Derek
 */
public interface IDeviceStreamCreateRequest {

    /**
     * Get unique identifier for stream within assignment.
     * 
     * @return
     */
    public String getStreamId();

    /**
     * Get content type of stream data.
     * 
     * @return
     */
    public String getContentType();

    /**
     * Get metadata associated with stream.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}