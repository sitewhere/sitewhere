/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.grpc;

import com.sitewhere.grpc.model.BatchModel.GBatchOperationElementSearchResults;
import com.sitewhere.grpc.model.BatchModel.GBatchOperationSearchResults;
import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.BatchModelConverter;
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
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchManagement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementUpdateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.search.ISearchResults;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for batch management GRPC requests.
 * 
 * @author Derek
 */
public class BatchManagementImpl extends BatchManagementGrpc.BatchManagementImplBase {

    /** Batch management persistence */
    private IBatchManagement batchManagement;

    public BatchManagementImpl(IBatchManagement batchManagement) {
	this.batchManagement = batchManagement;
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * createBatchOperation(com.sitewhere.grpc.service.GCreateBatchOperationRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createBatchOperation(GCreateBatchOperationRequest request,
	    StreamObserver<GCreateBatchOperationResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(BatchManagementGrpc.METHOD_CREATE_BATCH_OPERATION);
	    IBatchOperationCreateRequest apiRequest = BatchModelConverter
		    .asApiBatchOperationCreateRequest(request.getRequest());
	    IBatchOperation apiResult = getBatchManagement().createBatchOperation(apiRequest);
	    GCreateBatchOperationResponse.Builder response = GCreateBatchOperationResponse.newBuilder();
	    response.setBatchOperation(BatchModelConverter.asGrpcBatchOperation(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(BatchManagementGrpc.METHOD_CREATE_BATCH_OPERATION, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * createBatchCommandInvocation(com.sitewhere.grpc.service.
     * GCreateBatchCommandInvocationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createBatchCommandInvocation(GCreateBatchCommandInvocationRequest request,
	    StreamObserver<GCreateBatchCommandInvocationResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(BatchManagementGrpc.METHOD_CREATE_BATCH_COMMAND_INVOCATION);
	    IBatchCommandInvocationRequest apiRequest = BatchModelConverter
		    .asApiBatchCommandInvocationRequest(request.getRequest());
	    IBatchOperation apiResult = getBatchManagement().createBatchCommandInvocation(apiRequest);
	    GCreateBatchCommandInvocationResponse.Builder response = GCreateBatchCommandInvocationResponse.newBuilder();
	    response.setBatchOperation(BatchModelConverter.asGrpcBatchOperation(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(BatchManagementGrpc.METHOD_CREATE_BATCH_COMMAND_INVOCATION, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * updateBatchOperation(com.sitewhere.grpc.service.GUpdateBatchOperationRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateBatchOperation(GUpdateBatchOperationRequest request,
	    StreamObserver<GUpdateBatchOperationResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(BatchManagementGrpc.METHOD_UPDATE_BATCH_OPERATION);
	    IBatchOperationUpdateRequest apiRequest = BatchModelConverter
		    .asApiBatchOperationUpdateRequest(request.getRequest());
	    IBatchOperation apiResult = getBatchManagement().updateBatchOperation(request.getToken(), apiRequest);
	    GUpdateBatchOperationResponse.Builder response = GUpdateBatchOperationResponse.newBuilder();
	    response.setBatchOperation(BatchModelConverter.asGrpcBatchOperation(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(BatchManagementGrpc.METHOD_UPDATE_BATCH_OPERATION, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * getBatchOperationByToken(com.sitewhere.grpc.service.
     * GGetBatchOperationByTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getBatchOperationByToken(GGetBatchOperationByTokenRequest request,
	    StreamObserver<GGetBatchOperationByTokenResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(BatchManagementGrpc.METHOD_GET_BATCH_OPERATION_BY_TOKEN);
	    IBatchOperation apiResult = getBatchManagement().getBatchOperation(request.getToken());
	    GGetBatchOperationByTokenResponse.Builder response = GGetBatchOperationByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setBatchOperation(BatchModelConverter.asGrpcBatchOperation(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(BatchManagementGrpc.METHOD_GET_BATCH_OPERATION_BY_TOKEN, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * listBatchOperations(com.sitewhere.grpc.service.GListBatchOperationsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listBatchOperations(GListBatchOperationsRequest request,
	    StreamObserver<GListBatchOperationsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(BatchManagementGrpc.METHOD_LIST_BATCH_OPERATIONS);
	    ISearchResults<IBatchOperation> apiResult = getBatchManagement()
		    .listBatchOperations(BatchModelConverter.asApiBatchOperationSearchCriteria(request.getCriteria()));
	    GListBatchOperationsResponse.Builder response = GListBatchOperationsResponse.newBuilder();
	    GBatchOperationSearchResults.Builder results = GBatchOperationSearchResults.newBuilder();
	    for (IBatchOperation api : apiResult.getResults()) {
		results.addBatchOperations(BatchModelConverter.asGrpcBatchOperation(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(BatchManagementGrpc.METHOD_LIST_BATCH_OPERATIONS, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * deleteBatchOperation(com.sitewhere.grpc.service.GDeleteBatchOperationRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteBatchOperation(GDeleteBatchOperationRequest request,
	    StreamObserver<GDeleteBatchOperationResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(BatchManagementGrpc.METHOD_DELETE_BATCH_OPERATION);
	    IBatchOperation apiResult = getBatchManagement().deleteBatchOperation(request.getToken(),
		    request.getForce());
	    GDeleteBatchOperationResponse.Builder response = GDeleteBatchOperationResponse.newBuilder();
	    response.setBatchOperation(BatchModelConverter.asGrpcBatchOperation(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(BatchManagementGrpc.METHOD_DELETE_BATCH_OPERATION, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * listBatchOperationElements(com.sitewhere.grpc.service.
     * GListBatchOperationElementsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listBatchOperationElements(GListBatchOperationElementsRequest request,
	    StreamObserver<GListBatchOperationElementsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(BatchManagementGrpc.METHOD_LIST_BATCH_OPERATION_ELEMENTS);
	    ISearchResults<IBatchElement> apiResult = getBatchManagement().listBatchElements(request.getToken(),
		    BatchModelConverter.asApiBatchElementSearchCriteria(request.getCriteria()));
	    GListBatchOperationElementsResponse.Builder response = GListBatchOperationElementsResponse.newBuilder();
	    GBatchOperationElementSearchResults.Builder results = GBatchOperationElementSearchResults.newBuilder();
	    for (IBatchElement api : apiResult.getResults()) {
		results.addBatchOperationElements(BatchModelConverter.asGrpcBatchElement(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(BatchManagementGrpc.METHOD_LIST_BATCH_OPERATION_ELEMENTS, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * updateBatchOperationElement(com.sitewhere.grpc.service.
     * GUpdateBatchOperationElementRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateBatchOperationElement(GUpdateBatchOperationElementRequest request,
	    StreamObserver<GUpdateBatchOperationElementResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(BatchManagementGrpc.METHOD_UPDATE_BATCH_OPERATION_ELEMENT);
	    IBatchElementUpdateRequest apiRequest = BatchModelConverter
		    .asApiBatchElementUpdateRequest(request.getRequest());
	    IBatchElement apiResult = getBatchManagement().updateBatchElement(request.getToken(), request.getIndex(),
		    apiRequest);
	    GUpdateBatchOperationElementResponse.Builder response = GUpdateBatchOperationElementResponse.newBuilder();
	    response.setElement(BatchModelConverter.asGrpcBatchElement(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(BatchManagementGrpc.METHOD_UPDATE_BATCH_OPERATION_ELEMENT, e);
	    responseObserver.onError(e);
	}
    }

    public IBatchManagement getBatchManagement() {
	return batchManagement;
    }

    public void setBatchManagement(IBatchManagement batchManagement) {
	this.batchManagement = batchManagement;
    }
}