/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.batch;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.sitewhere.spi.common.IMetadataProvider;

/**
 * Holds information about a single element within an {@link IBatchOperation}.
 * 
 * @author Derek
 */
public interface IBatchElement extends IMetadataProvider, Serializable {

    /**
     * Get Unique id.
     * 
     * @return
     */
    public UUID getId();

    /**
     * Get id for parent batch operation.
     * 
     * @return
     */
    public UUID getBatchOperationId();

    /**
     * Get id for associated device.
     * 
     * @return
     */
    public UUID getDeviceId();

    /**
     * Get processing status indicator.
     * 
     * @return
     */
    public ElementProcessingStatus getProcessingStatus();

    /**
     * Get the date on which the element was processed.
     * 
     * @return
     */
    public Date getProcessedDate();
}