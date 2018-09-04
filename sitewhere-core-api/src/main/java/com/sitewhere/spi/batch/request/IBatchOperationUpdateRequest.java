/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.batch.request;

import java.util.Date;

import com.sitewhere.spi.batch.BatchOperationStatus;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.common.request.IPersistentEntityCreateRequest;

/**
 * Defines fields that can be updated on an {@link IBatchOperation}.
 * 
 * @author Derek
 */
public interface IBatchOperationUpdateRequest extends IPersistentEntityCreateRequest {

    /**
     * Get updated processing status for the batch operation.
     * 
     * @return
     */
    public BatchOperationStatus getProcessingStatus();

    /**
     * Get updated processing start date.
     * 
     * @return
     */
    public Date getProcessingStartedDate();

    /**
     * Get updated processing end date.
     * 
     * @return
     */
    public Date getProcessingEndedDate();
}