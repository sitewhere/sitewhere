/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.BatchModelConverter;
import com.sitewhere.grpc.model.spi.client.IBatchManagementApiChannel;
import com.sitewhere.grpc.service.BatchManagementGrpc;
import com.sitewhere.grpc.service.GCreateBatchCommandInvocationRequest;
import com.sitewhere.grpc.service.GCreateBatchCommandInvocationResponse;
import com.sitewhere.grpc.service.GCreateBatchOperationRequest;
import com.sitewhere.grpc.service.GCreateBatchOperationResponse;
import com.sitewhere.grpc.service.GDeleteBatchOperationRequest;
import com.sitewhere.grpc.service.GDeleteBatchOperationResponse;
import com.sitewhere.grpc.service.GGetBatchOperationByTokenRequest;
import com.sitewhere.grpc.service.GGetBatchOperationByTokenResponse;
import com.sitewhere.grpc.service.GListBatchOperationElementsRequest;
import com.sitewhere.grpc.service.GListBatchOperationElementsResponse;
import com.sitewhere.grpc.service.GListBatchOperationsRequest;
import com.sitewhere.grpc.service.GListBatchOperationsResponse;
import com.sitewhere.grpc.service.GUpdateBatchOperationElementRequest;
import com.sitewhere.grpc.service.GUpdateBatchOperationElementResponse;
import com.sitewhere.grpc.service.GUpdateBatchOperationRequest;
import com.sitewhere.grpc.service.GUpdateBatchOperationResponse;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.batch.IBatchOperationSearchCriteria;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;

/**
 * Supports SiteWhere batch management APIs on top of a
 * {@link BatchManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class BatchManagementApiChannel extends ApiChannel<BatchManagementGrpcChannel>
	implements IBatchManagementApiChannel {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Batch management GRPC channel */
    private BatchManagementGrpcChannel grpcChannel;

    public BatchManagementApiChannel(BatchManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#createBatchOperation(com.sitewhere.
     * spi.batch.request.IBatchOperationCreateRequest)
     */
    @Override
    public IBatchOperation createBatchOperation(IBatchOperationCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(BatchManagementGrpc.METHOD_CREATE_BATCH_OPERATION);
	    GCreateBatchOperationRequest.Builder grequest = GCreateBatchOperationRequest.newBuilder();
	    grequest.setRequest(BatchModelConverter.asGrpcBatchOperationCreateRequest(request));
	    GCreateBatchOperationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createBatchOperation(grequest.build());
	    IBatchOperation response = (gresponse.hasBatchOperation())
		    ? BatchModelConverter.asApiBatchOperation(gresponse.getBatchOperation())
		    : null;
	    GrpcUtils.logClientMethodResponse(BatchManagementGrpc.METHOD_CREATE_BATCH_OPERATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(BatchManagementGrpc.METHOD_CREATE_BATCH_OPERATION, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#createBatchCommandInvocation(com.
     * sitewhere.spi.batch.request.IBatchCommandInvocationRequest)
     */
    @Override
    public IBatchOperation createBatchCommandInvocation(IBatchCommandInvocationRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(BatchManagementGrpc.METHOD_CREATE_BATCH_COMMAND_INVOCATION);
	    GCreateBatchCommandInvocationRequest.Builder grequest = GCreateBatchCommandInvocationRequest.newBuilder();
	    grequest.setRequest(BatchModelConverter.asGrpcBatchCommandInvocationRequest(request));
	    GCreateBatchCommandInvocationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createBatchCommandInvocation(grequest.build());
	    IBatchOperation response = (gresponse.hasBatchOperation())
		    ? BatchModelConverter.asApiBatchOperation(gresponse.getBatchOperation())
		    : null;
	    GrpcUtils.logClientMethodResponse(BatchManagementGrpc.METHOD_CREATE_BATCH_COMMAND_INVOCATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(BatchManagementGrpc.METHOD_CREATE_BATCH_COMMAND_INVOCATION, t);
	}
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchManagement#updateBatchOperation(java.lang.
     * String, com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest)
     */
    @Override
    public IBatchOperation updateBatchOperation(String token, IBatchOperationUpdateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(BatchManagementGrpc.METHOD_UPDATE_BATCH_OPERATION);
	    GUpdateBatchOperationRequest.Builder grequest = GUpdateBatchOperationRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setRequest(BatchModelConverter.asGrpcBatchOperationUpdateRequest(request));
	    GUpdateBatchOperationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateBatchOperation(grequest.build());
	    IBatchOperation response = (gresponse.hasBatchOperation())
		    ? BatchModelConverter.asApiBatchOperation(gresponse.getBatchOperation())
		    : null;
	    GrpcUtils.logClientMethodResponse(BatchManagementGrpc.METHOD_UPDATE_BATCH_OPERATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(BatchManagementGrpc.METHOD_UPDATE_BATCH_OPERATION, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#getBatchOperation(java.lang.String)
     */
    @Override
    public IBatchOperation getBatchOperation(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(BatchManagementGrpc.METHOD_GET_BATCH_OPERATION_BY_TOKEN);
	    GGetBatchOperationByTokenRequest.Builder grequest = GGetBatchOperationByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetBatchOperationByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getBatchOperationByToken(grequest.build());
	    IBatchOperation response = (gresponse.hasBatchOperation())
		    ? BatchModelConverter.asApiBatchOperation(gresponse.getBatchOperation())
		    : null;
	    GrpcUtils.logClientMethodResponse(BatchManagementGrpc.METHOD_GET_BATCH_OPERATION_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(BatchManagementGrpc.METHOD_GET_BATCH_OPERATION_BY_TOKEN, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#listBatchOperations(com.sitewhere.
     * spi.search.batch.IBatchOperationSearchCriteria)
     */
    @Override
    public ISearchResults<IBatchOperation> listBatchOperations(IBatchOperationSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(BatchManagementGrpc.METHOD_LIST_BATCH_OPERATIONS);
	    GListBatchOperationsRequest.Builder grequest = GListBatchOperationsRequest.newBuilder();
	    grequest.setCriteria(BatchModelConverter.asGrpcBatchOperationSearchCriteria(criteria));
	    GListBatchOperationsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listBatchOperations(grequest.build());
	    ISearchResults<IBatchOperation> results = BatchModelConverter
		    .asApiBatchOperationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(BatchManagementGrpc.METHOD_LIST_BATCH_OPERATIONS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(BatchManagementGrpc.METHOD_LIST_BATCH_OPERATIONS, t);
	}
    }

    /*
     * @see com.sitewhere.spi.batch.IBatchManagement#deleteBatchOperation(java.lang.
     * String, boolean)
     */
    @Override
    public IBatchOperation deleteBatchOperation(String token, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(BatchManagementGrpc.METHOD_DELETE_BATCH_OPERATION);
	    GDeleteBatchOperationRequest.Builder grequest = GDeleteBatchOperationRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setForce(force);
	    GDeleteBatchOperationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteBatchOperation(grequest.build());
	    IBatchOperation response = (gresponse.hasBatchOperation())
		    ? BatchModelConverter.asApiBatchOperation(gresponse.getBatchOperation())
		    : null;
	    GrpcUtils.logClientMethodResponse(BatchManagementGrpc.METHOD_DELETE_BATCH_OPERATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(BatchManagementGrpc.METHOD_DELETE_BATCH_OPERATION, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#listBatchElements(java.lang.String,
     * com.sitewhere.spi.search.device.IBatchElementSearchCriteria)
     */
    @Override
    public ISearchResults<IBatchElement> listBatchElements(String batchToken, IBatchElementSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(BatchManagementGrpc.METHOD_LIST_BATCH_OPERATION_ELEMENTS);
	    GListBatchOperationElementsRequest.Builder grequest = GListBatchOperationElementsRequest.newBuilder();
	    grequest.setToken(batchToken);
	    grequest.setCriteria(BatchModelConverter.asGrpcBatchElementSearchCriteria(criteria));
	    GListBatchOperationElementsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listBatchOperationElements(grequest.build());
	    ISearchResults<IBatchElement> results = BatchModelConverter
		    .asApiBatchElementSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(BatchManagementGrpc.METHOD_LIST_BATCH_OPERATION_ELEMENTS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(BatchManagementGrpc.METHOD_LIST_BATCH_OPERATION_ELEMENTS, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.batch.IBatchManagement#updateBatchElement(java.lang.String,
     * long, com.sitewhere.spi.batch.request.IBatchElementUpdateRequest)
     */
    @Override
    public IBatchElement updateBatchElement(String operationToken, long index, IBatchElementUpdateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(BatchManagementGrpc.METHOD_UPDATE_BATCH_OPERATION_ELEMENT);
	    GUpdateBatchOperationElementRequest.Builder grequest = GUpdateBatchOperationElementRequest.newBuilder();
	    grequest.setToken(operationToken);
	    grequest.setIndex(index);
	    grequest.setRequest(BatchModelConverter.asGrpcBatchElementUpdateRequest(request));
	    GUpdateBatchOperationElementResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateBatchOperationElement(grequest.build());
	    IBatchElement response = (gresponse.hasElement())
		    ? BatchModelConverter.asApiBatchElement(gresponse.getElement())
		    : null;
	    GrpcUtils.logClientMethodResponse(BatchManagementGrpc.METHOD_UPDATE_BATCH_OPERATION_ELEMENT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(BatchManagementGrpc.METHOD_UPDATE_BATCH_OPERATION_ELEMENT, t);
	}
    }

    /*
     * @see com.sitewhere.grpc.model.client.ApiChannel#getGrpcChannel()
     */
    @Override
    public BatchManagementGrpcChannel getGrpcChannel() {
	return grpcChannel;
    }

    public void setGrpcChannel(BatchManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
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
}