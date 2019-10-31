/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.batch;

import java.util.UUID;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.batch.IBatchOperationSearchCriteria;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Interface for batch management operations.
 */
public interface IBatchManagement extends ITenantEngineLifecycleComponent {

    /**
     * Creates an {@link IBatchOperation} to perform an operation on multiple
     * devices.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request) throws SiteWhereException;

    /**
     * Update an existing {@link IBatchOperation}.
     * 
     * @param batchOperationId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IBatchOperation updateBatchOperation(UUID batchOperationId, IBatchOperationUpdateRequest request)
	    throws SiteWhereException;

    /**
     * Get an {@link IBatchOperation} by unique token.
     * 
     * @param batchOperationId
     * @return
     * @throws SiteWhereException
     */
    public IBatchOperation getBatchOperation(UUID batchOperationId) throws SiteWhereException;

    /**
     * Get a batch operation by token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public IBatchOperation getBatchOperationByToken(String token) throws SiteWhereException;

    /**
     * List batch operations based on the given criteria.
     * 
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IBatchOperation> listBatchOperations(IBatchOperationSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Deletes a batch operation and its elements.
     * 
     * @param batchOperationId
     * @return
     * @throws SiteWhereException
     */
    public IBatchOperation deleteBatchOperation(UUID batchOperationId) throws SiteWhereException;

    /**
     * Create a batch element associated with a batch operation.
     * 
     * @param batchOperationId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IBatchElement createBatchElement(UUID batchOperationId, IBatchElementCreateRequest request)
	    throws SiteWhereException;

    /**
     * Lists elements for an {@link IBatchOperation} that meet the given criteria.
     * 
     * @param batchOperationId
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IBatchElement> listBatchElements(UUID batchOperationId, IBatchElementSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Updates an existing batch operation element.
     * 
     * @param elementId
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IBatchElement updateBatchElement(UUID elementId, IBatchElementCreateRequest request)
	    throws SiteWhereException;

    /**
     * Creates an {@link ISearchResults} that will invoke a command on multiple
     * devices.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request)
	    throws SiteWhereException;
}