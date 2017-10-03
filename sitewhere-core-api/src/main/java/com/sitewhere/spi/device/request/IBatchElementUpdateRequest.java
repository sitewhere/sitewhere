/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.util.Date;
import java.util.Map;

import com.sitewhere.spi.batch.ElementProcessingStatus;
import com.sitewhere.spi.batch.IBatchElement;

/**
 * Defines fields that can be updated on an {@link IBatchElement}.
 * 
 * @author Derek
 */
public interface IBatchElementUpdateRequest {

    /**
     * Get processing status indicator.
     * 
     * @return
     */
    public ElementProcessingStatus getProcessingStatus();

    /**
     * Get date element was processed.
     * 
     * @return
     */
    public Date getProcessedDate();

    /**
     * Get metadata values.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}