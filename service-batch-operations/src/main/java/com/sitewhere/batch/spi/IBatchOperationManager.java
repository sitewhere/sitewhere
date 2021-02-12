/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.batch.spi;

import java.util.List;

import com.sitewhere.batch.spi.kafka.IUnprocessedBatchElementsConsumer;
import com.sitewhere.batch.spi.kafka.IUnprocessedBatchOperationsConsumer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.kafka.IUnprocessedBatchElement;
import com.sitewhere.spi.batch.kafka.IUnprocessedBatchOperation;
import com.sitewhere.spi.microservice.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Interface for interacting with a batch operation manager.
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
     * Process an unprocessed batch element.
     * 
     * @param element
     * @throws SiteWhereException
     */
    public void processBatchElement(IUnprocessedBatchElement element) throws SiteWhereException;

    /**
     * Get consumer for unprocessed batch operations.
     * 
     * @return
     */
    public IUnprocessedBatchOperationsConsumer getUnprocessedBatchOperationsConsumer();

    /**
     * Get consumer for unprocessed batch elements.
     * 
     * @return
     */
    public IUnprocessedBatchElementsConsumer getUnprocessedBatchElementsConsumer();
}