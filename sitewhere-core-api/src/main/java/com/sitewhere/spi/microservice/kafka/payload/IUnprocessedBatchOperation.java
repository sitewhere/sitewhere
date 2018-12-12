/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.microservice.kafka.payload;

import java.util.List;

import com.sitewhere.spi.batch.IBatchOperation;

/**
 * Contains information about a batch operation which has not been processed
 * into batch elements.
 */
public interface IUnprocessedBatchOperation {

    /**
     * Batch operation information.
     * 
     * @return
     */
    public IBatchOperation getBatchOperation();

    /**
     * Get list of tokens for devices to which operation will be applied.
     * 
     * @return
     */
    public List<String> getDeviceTokens();
}
