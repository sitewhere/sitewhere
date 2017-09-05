/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.streaming;

import com.sitewhere.spi.common.IMetadataProviderEntity;

/**
 * A data stream associated with a device assignment.
 * 
 * @author Derek
 */
public interface IDeviceStream extends IMetadataProviderEntity {

    /**
     * Get assignment stream belongs to.
     * 
     * @return
     */
    public String getAssignmentToken();

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
}