/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.microservice.kafka.payload;

import java.util.List;

import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.microservice.kafka.payload.IUnprocessedBatchOperation;

/**
 * Model object for a batch operation that has not been processed into batch
 * elements.
 */
public class UnprocessedBatchOperation implements IUnprocessedBatchOperation {

    /** Batch operation */
    private IBatchOperation batchOperation;

    /** List of device tokens for operation */
    private List<String> deviceTokens;

    /*
     * @see com.sitewhere.spi.microservice.kafka.payload.IUnprocessedBatchOperation#
     * getBatchOperation()
     */
    @Override
    public IBatchOperation getBatchOperation() {
	return batchOperation;
    }

    public void setBatchOperation(IBatchOperation batchOperation) {
	this.batchOperation = batchOperation;
    }

    /*
     * @see com.sitewhere.spi.microservice.kafka.payload.IUnprocessedBatchOperation#
     * getDeviceTokens()
     */
    @Override
    public List<String> getDeviceTokens() {
	return deviceTokens;
    }

    public void setDeviceTokens(List<String> deviceTokens) {
	this.deviceTokens = deviceTokens;
    }
}
