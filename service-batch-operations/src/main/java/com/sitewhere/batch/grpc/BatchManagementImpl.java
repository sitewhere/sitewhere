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
package com.sitewhere.batch.grpc;

import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;
import com.sitewhere.grpc.batch.BatchModelConverter;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.model.BatchModel.GBatchElementSearchResults;
import com.sitewhere.grpc.model.BatchModel.GBatchOperationSearchResults;
import com.sitewhere.grpc.service.BatchManagementGrpc;
import com.sitewhere.grpc.service.GCreateBatchCommandInvocationRequest;
import com.sitewhere.grpc.service.GCreateBatchCommandInvocationResponse;
import com.sitewhere.grpc.service.GCreateBatchElementRequest;
import com.sitewhere.grpc.service.GCreateBatchElementResponse;
import com.sitewhere.grpc.service.GCreateBatchOperationRequest;
import com.sitewhere.grpc.service.GCreateBatchOperationResponse;
import com.sitewhere.grpc.service.GDeleteBatchOperationRequest;
import com.sitewhere.grpc.service.GDeleteBatchOperationResponse;
import com.sitewhere.grpc.service.GGetBatchOperationByTokenRequest;
import com.sitewhere.grpc.service.GGetBatchOperationByTokenResponse;
import com.sitewhere.grpc.service.GGetBatchOperationRequest;
import com.sitewhere.grpc.service.GGetBatchOperationResponse;
import com.sitewhere.grpc.service.GListBatchElementsRequest;
import com.sitewhere.grpc.service.GListBatchElementsResponse;
import com.sitewhere.grpc.service.GListBatchOperationsRequest;
import com.sitewhere.grpc.service.GListBatchOperationsResponse;
import com.sitewhere.grpc.service.GUpdateBatchElementRequest;
import com.sitewhere.grpc.service.GUpdateBatchElementResponse;
import com.sitewhere.grpc.service.GUpdateBatchOperationRequest;
import com.sitewhere.grpc.service.GUpdateBatchOperationResponse;
import com.sitewhere.microservice.api.batch.IBatchManagement;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.batch.IBatchElement;
import com.sitewhere.spi.batch.IBatchOperation;
import com.sitewhere.spi.batch.request.IBatchCommandInvocationRequest;
import com.sitewhere.spi.batch.request.IBatchElementCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationCreateRequest;
import com.sitewhere.spi.batch.request.IBatchOperationUpdateRequest;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IBatchElementSearchCriteria;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for batch management GRPC requests.
 */
public class BatchManagementImpl extends BatchManagementGrpc.BatchManagementImplBase implements IGrpcApiImplementation {

    /** Parent microservice */
    private IBatchOperationsMicroservice microservice;

    /** Batch management persistence */
    private IBatchManagement batchManagement;

    public BatchManagementImpl(IBatchOperationsMicroservice microservice, IBatchManagement batchManagement) {
	this.microservice = microservice;
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
	    GrpcUtils.handleServerMethodEntry(this, BatchManagementGrpc.getCreateBatchOperationMethod());
	    IBatchOperationCreateRequest apiRequest = BatchModelConverter
		    .asApiBatchOperationCreateRequest(request.getRequest());
	    IBatchOperation apiResult = getBatchManagement().createBatchOperation(apiRequest);
	    GCreateBatchOperationResponse.Builder response = GCreateBatchOperationResponse.newBuilder();
	    response.setBatchOperation(BatchModelConverter.asGrpcBatchOperation(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(BatchManagementGrpc.getCreateBatchOperationMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(BatchManagementGrpc.getCreateBatchOperationMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, BatchManagementGrpc.getCreateBatchCommandInvocationMethod());
	    IBatchCommandInvocationRequest apiRequest = BatchModelConverter
		    .asApiBatchCommandInvocationRequest(request.getRequest());
	    IBatchOperation apiResult = getBatchManagement().createBatchCommandInvocation(apiRequest);
	    GCreateBatchCommandInvocationResponse.Builder response = GCreateBatchCommandInvocationResponse.newBuilder();
	    response.setBatchOperation(BatchModelConverter.asGrpcBatchOperation(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(BatchManagementGrpc.getCreateBatchCommandInvocationMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(BatchManagementGrpc.getCreateBatchCommandInvocationMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, BatchManagementGrpc.getUpdateBatchOperationMethod());
	    IBatchOperationUpdateRequest apiRequest = BatchModelConverter
		    .asApiBatchOperationUpdateRequest(request.getRequest());
	    IBatchOperation apiResult = getBatchManagement()
		    .updateBatchOperation(CommonModelConverter.asApiUuid(request.getBatchOperationId()), apiRequest);
	    GUpdateBatchOperationResponse.Builder response = GUpdateBatchOperationResponse.newBuilder();
	    response.setBatchOperation(BatchModelConverter.asGrpcBatchOperation(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(BatchManagementGrpc.getUpdateBatchOperationMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(BatchManagementGrpc.getUpdateBatchOperationMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * getBatchOperation(com.sitewhere.grpc.service.GGetBatchOperationRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getBatchOperation(GGetBatchOperationRequest request,
	    StreamObserver<GGetBatchOperationResponse> responseObserver) {
	throw new RuntimeException("Implement getBatchOperation()");
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
	    GrpcUtils.handleServerMethodEntry(this, BatchManagementGrpc.getGetBatchOperationByTokenMethod());
	    IBatchOperation apiResult = getBatchManagement().getBatchOperationByToken(request.getToken());
	    GGetBatchOperationByTokenResponse.Builder response = GGetBatchOperationByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setBatchOperation(BatchModelConverter.asGrpcBatchOperation(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(BatchManagementGrpc.getGetBatchOperationByTokenMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(BatchManagementGrpc.getGetBatchOperationByTokenMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, BatchManagementGrpc.getListBatchOperationsMethod());
	    ISearchResults<? extends IBatchOperation> apiResult = getBatchManagement()
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
	    GrpcUtils.handleServerMethodException(BatchManagementGrpc.getListBatchOperationsMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(BatchManagementGrpc.getListBatchOperationsMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, BatchManagementGrpc.getDeleteBatchOperationMethod());
	    IBatchOperation apiResult = getBatchManagement()
		    .deleteBatchOperation(CommonModelConverter.asApiUuid(request.getBatchOperationId()));
	    GDeleteBatchOperationResponse.Builder response = GDeleteBatchOperationResponse.newBuilder();
	    response.setBatchOperation(BatchModelConverter.asGrpcBatchOperation(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(BatchManagementGrpc.getDeleteBatchOperationMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(BatchManagementGrpc.getDeleteBatchOperationMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * createBatchElement(com.sitewhere.grpc.service.GCreateBatchElementRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createBatchElement(GCreateBatchElementRequest request,
	    StreamObserver<GCreateBatchElementResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, BatchManagementGrpc.getCreateBatchElementMethod());
	    IBatchElementCreateRequest apiRequest = BatchModelConverter
		    .asApiBatchElementUpdateRequest(request.getRequest());
	    IBatchElement apiResult = getBatchManagement()
		    .createBatchElement(CommonModelConverter.asApiUuid(request.getBatchOperationId()), apiRequest);
	    GCreateBatchElementResponse.Builder response = GCreateBatchElementResponse.newBuilder();
	    response.setElement(BatchModelConverter.asGrpcBatchElement(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(BatchManagementGrpc.getCreateBatchElementMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(BatchManagementGrpc.getCreateBatchElementMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * listBatchElements(com.sitewhere.grpc.service.GListBatchElementsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listBatchElements(GListBatchElementsRequest request,
	    StreamObserver<GListBatchElementsResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, BatchManagementGrpc.getListBatchElementsMethod());
	    IBatchElementSearchCriteria criteria = BatchModelConverter
		    .asApiBatchElementSearchCriteria(request.getCriteria());
	    getMicroservice().getLogger()
		    .info("Batch element search criteria:\n" + MarshalUtils.marshalJsonAsPrettyString(criteria));
	    ISearchResults<? extends IBatchElement> apiResult = getBatchManagement()
		    .listBatchElements(CommonModelConverter.asApiUuid(request.getBatchOperationId()), criteria);
	    GListBatchElementsResponse.Builder response = GListBatchElementsResponse.newBuilder();
	    GBatchElementSearchResults.Builder results = GBatchElementSearchResults.newBuilder();
	    for (IBatchElement api : apiResult.getResults()) {
		results.addBatchElements(BatchModelConverter.asGrpcBatchElement(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(BatchManagementGrpc.getListBatchElementsMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(BatchManagementGrpc.getListBatchElementsMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * updateBatchElement(com.sitewhere.grpc.service.GUpdateBatchElementRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateBatchElement(GUpdateBatchElementRequest request,
	    StreamObserver<GUpdateBatchElementResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, BatchManagementGrpc.getUpdateBatchElementMethod());
	    IBatchElementCreateRequest apiRequest = BatchModelConverter
		    .asApiBatchElementUpdateRequest(request.getRequest());
	    IBatchElement apiResult = getBatchManagement()
		    .updateBatchElement(CommonModelConverter.asApiUuid(request.getElementId()), apiRequest);
	    GUpdateBatchElementResponse.Builder response = GUpdateBatchElementResponse.newBuilder();
	    response.setElement(BatchModelConverter.asGrpcBatchElement(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(BatchManagementGrpc.getUpdateBatchElementMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(BatchManagementGrpc.getUpdateBatchElementMethod());
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation#getMicroservice()
     */
    @Override
    public IMicroservice<?, ?> getMicroservice() {
	return microservice;
    }

    protected IBatchManagement getBatchManagement() {
	return batchManagement;
    }
}