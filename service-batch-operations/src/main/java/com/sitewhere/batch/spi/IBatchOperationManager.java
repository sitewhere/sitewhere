/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.spi;

import java.util.List;

import com.sitewhere.batch.spi.kafka.IUnprocessedBatchOperationsConsumer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.kafka.IUnprocessedBatchOperation;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Interface for interacting with a batch operation manager.
 * 
 * @author Derek
 */
public interface IBatchOperationManager extends ITenantEngineLifecycleComponent {

    /**
     * Add an unprocessed batch operation to the processing pipeline.
     * 
     * @param operation
     * @param deviceTokens
     * @throws SiteWhereException
     */
    public void addUnprocessedBatchOperation(IBatchOperation operation, List<String> deviceTokens)
	    throws SiteWhereException;

    /**
     * Initialize an unprocessed batch operation.
     * 
     * @param operation
     * @throws SiteWhereException
     */
    public void initializeBatchOperation(IUnprocessedBatchOperation operation) throws SiteWhereException;

    /**
     * Get consumer for unprocessed batch operations.
     * 
     * @return
     */
    public IUnprocessedBatchOperationsConsumer getUnprocessedBatchOperationsConsumer();
}