/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.persistence.hbase;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.batch.persistence.BatchManagementPersistence;
import com.sitewhere.hbase.HBaseContext;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.encoder.IPayloadMarshaler;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchManagement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.batch.IBatchOperationSearchCriteria;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * HBase implementation of SiteWhere device management.
 * 
 * @author Derek
 */
public class HBaseBatchManagement extends TenantEngineLifecycleComponent implements IBatchManagement {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(HBaseBatchManagement.class);

    /** Used to communicate with HBase */
    private ISiteWhereHBaseClient client;

    /** Injected payload encoder */
    private IPayloadMarshaler payloadMarshaler;

    /** Supplies context to implementation methods */
    private HBaseContext context;

    public HBaseBatchManagement() {
	super(LifecycleComponentType.DataStore);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.batch.IBatchManagement#createBatchOperation(com.
     * sitewhere.spi.device.request.IBatchOperationCreateRequest)
     */
    @Override
    public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request) throws SiteWhereException {
	return HBaseBatchOperation.createBatchOperation(context, request);
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#updateBatchOperation(java.util.UUID,
     * com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest)
     */
    @Override
    public IBatchOperation updateBatchOperation(UUID batchOperationId, IBatchOperationUpdateRequest request)
	    throws SiteWhereException {
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#getBatchOperation(java.util.UUID)
     */
    @Override
    public IBatchOperation getBatchOperation(UUID batchOperationId) throws SiteWhereException {
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#getBatchOperationByToken(java.lang.
     * String)
     */
    @Override
    public IBatchOperation getBatchOperationByToken(String token) throws SiteWhereException {
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#listBatchOperations(com.sitewhere.
     * spi.search.batch.IBatchOperationSearchCriteria)
     */
    @Override
    public ISearchResults<IBatchOperation> listBatchOperations(IBatchOperationSearchCriteria criteria)
	    throws SiteWhereException {
	return HBaseBatchOperation.listBatchOperations(context, criteria);
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#deleteBatchOperation(java.util.UUID,
     * boolean)
     */
    @Override
    public IBatchOperation deleteBatchOperation(UUID batchOperationId, boolean force) throws SiteWhereException {
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#listBatchElements(java.util.UUID,
     * com.sitewhere.spi.search.device.IBatchElementSearchCriteria)
     */
    @Override
    public SearchResults<IBatchElement> listBatchElements(UUID batchOperationId, IBatchElementSearchCriteria criteria)
	    throws SiteWhereException {
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#updateBatchElement(java.util.UUID,
     * com.sitewhere.spi.batch.request.IBatchElementUpdateRequest)
     */
    @Override
    public IBatchElement updateBatchElement(UUID batchOperationId, IBatchElementUpdateRequest request)
	    throws SiteWhereException {
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#createBatchCommandInvocation(com
     * .sitewhere.spi.device.request.IBatchCommandInvocationRequest)
     */
    @Override
    public IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request)
	    throws SiteWhereException {
	String uuid = ((request.getToken() != null) ? request.getToken() : UUID.randomUUID().toString());
	IBatchOperationCreateRequest generic = BatchManagementPersistence.batchCommandInvocationCreateLogic(request,
		uuid);
	return createBatchOperation(generic);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }

    public ISiteWhereHBaseClient getClient() {
	return client;
    }

    public void setClient(ISiteWhereHBaseClient client) {
	this.client = client;
    }

    public IPayloadMarshaler getPayloadMarshaler() {
	return payloadMarshaler;
    }

    public void setPayloadMarshaler(IPayloadMarshaler payloadMarshaler) {
	this.payloadMarshaler = payloadMarshaler;
    }
}