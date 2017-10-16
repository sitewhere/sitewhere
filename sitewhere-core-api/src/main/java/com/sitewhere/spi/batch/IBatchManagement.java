/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.batch;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.device.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.device.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.device.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ITenantLifecycleComponent;

/**
 * Interface for batch management operations.
 * 
 * @author Derek
 */
public interface IBatchManagement extends ITenantLifecycleComponent {

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
     * @param token
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IBatchOperation updateBatchOperation(String token, IBatchOperationUpdateRequest request)
	    throws SiteWhereException;

    /**
     * Get an {@link IBatchOperation} by unique token.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public IBatchOperation getBatchOperation(String token) throws SiteWhereException;

    /**
     * List batch operations based on the given criteria.
     * 
     * @param includeDeleted
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IBatchOperation> listBatchOperations(boolean includeDeleted, ISearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Deletes a batch operation and its elements.
     * 
     * @param token
     * @param force
     * @return
     * @throws SiteWhereException
     */
    public IBatchOperation deleteBatchOperation(String token, boolean force) throws SiteWhereException;

    /**
     * Lists elements for an {@link IBatchOperation} that meet the given
     * criteria.
     * 
     * @param batchToken
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IBatchElement> listBatchElements(String batchToken, IBatchElementSearchCriteria criteria)
	    throws SiteWhereException;

    /**
     * Updates an existing batch operation element.
     * 
     * @param operationToken
     * @param index
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public IBatchElement updateBatchElement(String operationToken, long index, IBatchElementUpdateRequest request)
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