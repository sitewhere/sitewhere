package com.sitewhere.batch.persistence.hbase;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.batch.persistence.BatchManagementPersistence;
import com.sitewhere.hbase.HBaseContext;
import com.sitewhere.hbase.ISiteWhereHBaseClient;
import com.sitewhere.hbase.encoder.IPayloadMarshaler;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchManagement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.device.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.device.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.device.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.device.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * HBase implementation of SiteWhere device management.
 * 
 * @author Derek
 */
public class HBaseBatchManagement extends TenantLifecycleComponent implements IBatchManagement {

    /** Static logger instance */
    private static final Logger LOGGER = LogManager.getLogger();

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
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#updateBatchOperation(java.lang.
     * String, com.sitewhere.spi.device.request.IBatchOperationUpdateRequest)
     */
    @Override
    public IBatchOperation updateBatchOperation(String token, IBatchOperationUpdateRequest request)
	    throws SiteWhereException {
	return HBaseBatchOperation.updateBatchOperation(context, token, request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#getBatchOperation(java.lang.
     * String)
     */
    @Override
    public IBatchOperation getBatchOperation(String token) throws SiteWhereException {
	return HBaseBatchOperation.getBatchOperationByToken(context, token);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#listBatchOperations(boolean,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IBatchOperation> listBatchOperations(boolean includeDeleted, ISearchCriteria criteria)
	    throws SiteWhereException {
	return HBaseBatchOperation.listBatchOperations(context, includeDeleted, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#deleteBatchOperation(java.lang.
     * String, boolean)
     */
    @Override
    public IBatchOperation deleteBatchOperation(String token, boolean force) throws SiteWhereException {
	return HBaseBatchOperation.deleteBatchOperation(context, token, force);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#listBatchElements(java.lang.
     * String, com.sitewhere.spi.search.device.IBatchElementSearchCriteria)
     */
    @Override
    public SearchResults<IBatchElement> listBatchElements(String batchToken, IBatchElementSearchCriteria criteria)
	    throws SiteWhereException {
	return HBaseBatchElement.listBatchElements(context, batchToken, criteria);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#updateBatchElement(java.lang.
     * String, long,
     * com.sitewhere.spi.device.request.IBatchElementUpdateRequest)
     */
    @Override
    public IBatchElement updateBatchElement(String operationToken, long index, IBatchElementUpdateRequest request)
	    throws SiteWhereException {
	return HBaseBatchElement.updateBatchElement(context, operationToken, index, request);
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
    public Logger getLogger() {
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