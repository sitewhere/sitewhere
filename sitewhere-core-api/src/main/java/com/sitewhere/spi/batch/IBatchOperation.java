/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.batch;

import java.util.Date;
import java.util.Map;

import com.sitewhere.spi.common.IMetadataProviderEntity;

/**
 * Interface for an operation that is applied to multiple devices.
 * 
 * @author Derek
 */
public interface IBatchOperation extends IMetadataProviderEntity {

    /**
     * Get the unique group token.
     * 
     * @return
     */
    public String getToken();

    /**
     * Gets the type of operation to be performed.
     * 
     * @return
     */
    public OperationType getOperationType();

    /**
     * Operation parameters.
     * 
     * @return
     */
    public Map<String, String> getParameters();

    /**
     * Get processing status for the batch operation.
     * 
     * @return
     */
    public BatchOperationStatus getProcessingStatus();

    /**
     * Get the date when processing of the operation started.
     * 
     * @return
     */
    public Date getProcessingStartedDate();

    /**
     * Get the date when processing of the operation ended.
     * 
     * @return
     */
    public Date getProcessingEndedDate();
}