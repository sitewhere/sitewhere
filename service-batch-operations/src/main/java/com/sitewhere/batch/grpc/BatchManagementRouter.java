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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
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
import com.sitewhere.microservice.grpc.GrpcTenantEngineProvider;
import com.sitewhere.spi.microservice.grpc.ITenantEngineCallback;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 */
public class BatchManagementRouter extends BatchManagementGrpc.BatchManagementImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(BatchManagementRouter.class);

    /** Parent microservice */
    private IBatchOperationsMicroservice microservice;

    /** Tenant engine provider */
    private GrpcTenantEngineProvider<IBatchOperationsTenantEngine> grpcTenantEngineProvider;

    public BatchManagementRouter(IBatchOperationsMicroservice microservice) {
	this.microservice = microservice;
	this.grpcTenantEngineProvider = new GrpcTenantEngineProvider<>(microservice);
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * createBatchOperation(com.sitewhere.grpc.service.GCreateBatchOperationRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createBatchOperation(GCreateBatchOperationRequest request,
	    StreamObserver<GCreateBatchOperationResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IBatchOperationsTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IBatchOperationsTenantEngine tenantEngine) {
		tenantEngine.getBatchManagementImpl().createBatchOperation(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * createBatchCommandInvocation(com.sitewhere.grpc.service.
     * GCreateBatchCommandInvocationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createBatchCommandInvocation(GCreateBatchCommandInvocationRequest request,
	    StreamObserver<GCreateBatchCommandInvocationResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IBatchOperationsTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IBatchOperationsTenantEngine tenantEngine) {
		tenantEngine.getBatchManagementImpl().createBatchCommandInvocation(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * updateBatchOperation(com.sitewhere.grpc.service.GUpdateBatchOperationRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateBatchOperation(GUpdateBatchOperationRequest request,
	    StreamObserver<GUpdateBatchOperationResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IBatchOperationsTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IBatchOperationsTenantEngine tenantEngine) {
		tenantEngine.getBatchManagementImpl().updateBatchOperation(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * getBatchOperation(com.sitewhere.grpc.service.GGetBatchOperationRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getBatchOperation(GGetBatchOperationRequest request,
	    StreamObserver<GGetBatchOperationResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IBatchOperationsTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IBatchOperationsTenantEngine tenantEngine) {
		tenantEngine.getBatchManagementImpl().getBatchOperation(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * getBatchOperationByToken(com.sitewhere.grpc.service.
     * GGetBatchOperationByTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getBatchOperationByToken(GGetBatchOperationByTokenRequest request,
	    StreamObserver<GGetBatchOperationByTokenResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IBatchOperationsTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IBatchOperationsTenantEngine tenantEngine) {
		tenantEngine.getBatchManagementImpl().getBatchOperationByToken(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * listBatchOperations(com.sitewhere.grpc.service.GListBatchOperationsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listBatchOperations(GListBatchOperationsRequest request,
	    StreamObserver<GListBatchOperationsResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IBatchOperationsTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IBatchOperationsTenantEngine tenantEngine) {
		tenantEngine.getBatchManagementImpl().listBatchOperations(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * deleteBatchOperation(com.sitewhere.grpc.service.GDeleteBatchOperationRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteBatchOperation(GDeleteBatchOperationRequest request,
	    StreamObserver<GDeleteBatchOperationResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IBatchOperationsTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IBatchOperationsTenantEngine tenantEngine) {
		tenantEngine.getBatchManagementImpl().deleteBatchOperation(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * createBatchElement(com.sitewhere.grpc.service.GCreateBatchElementRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createBatchElement(GCreateBatchElementRequest request,
	    StreamObserver<GCreateBatchElementResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IBatchOperationsTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IBatchOperationsTenantEngine tenantEngine) {
		tenantEngine.getBatchManagementImpl().createBatchElement(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * listBatchElements(com.sitewhere.grpc.service.GListBatchElementsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listBatchElements(GListBatchElementsRequest request,
	    StreamObserver<GListBatchElementsResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IBatchOperationsTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IBatchOperationsTenantEngine tenantEngine) {
		tenantEngine.getBatchManagementImpl().listBatchElements(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * updateBatchElement(com.sitewhere.grpc.service.GUpdateBatchElementRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateBatchElement(GUpdateBatchElementRequest request,
	    StreamObserver<GUpdateBatchElementResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IBatchOperationsTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IBatchOperationsTenantEngine tenantEngine) {
		tenantEngine.getBatchManagementImpl().updateBatchElement(request, responseObserver);
	    }
	}, responseObserver);
    }

    public IBatchOperationsMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IBatchOperationsMicroservice microservice) {
	this.microservice = microservice;
    }

    protected GrpcTenantEngineProvider<IBatchOperationsTenantEngine> getGrpcTenantEngineProvider() {
	return grpcTenantEngineProvider;
    }
}