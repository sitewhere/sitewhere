/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcRouter;
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
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 */
public class BatchManagementRouter extends BatchManagementGrpc.BatchManagementImplBase
	implements IGrpcRouter<BatchManagementGrpc.BatchManagementImplBase> {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(BatchManagementRouter.class);

    /** Parent microservice */
    private IBatchOperationsMicroservice microservice;

    public BatchManagementRouter(IBatchOperationsMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.spi.grpc.IGrpcRouter#getTenantImplementation()
     */
    @Override
    public BatchManagementGrpc.BatchManagementImplBase getTenantImplementation(StreamObserver<?> observer) {
	String token = GrpcKeys.TENANT_CONTEXT_KEY.get();
	if (token == null) {
	    throw new RuntimeException("Tenant token not found in request.");
	}
	try {
	    IBatchOperationsTenantEngine engine = getMicroservice().assureTenantEngineAvailable(token);
	    return engine.getBatchManagementImpl();
	} catch (TenantEngineNotAvailableException e) {
	    observer.onError(GrpcUtils.convertServerException(e));
	    return null;
	}
    }

    /*
     * @see com.sitewhere.grpc.service.BatchManagementGrpc.BatchManagementImplBase#
     * createBatchOperation(com.sitewhere.grpc.service.GCreateBatchOperationRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createBatchOperation(GCreateBatchOperationRequest request,
	    StreamObserver<GCreateBatchOperationResponse> responseObserver) {
	BatchManagementGrpc.BatchManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createBatchOperation(request, responseObserver);
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
	BatchManagementGrpc.BatchManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createBatchCommandInvocation(request, responseObserver);
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
	BatchManagementGrpc.BatchManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateBatchOperation(request, responseObserver);
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
	BatchManagementGrpc.BatchManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getBatchOperation(request, responseObserver);
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
	BatchManagementGrpc.BatchManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getBatchOperationByToken(request, responseObserver);
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
	BatchManagementGrpc.BatchManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listBatchOperations(request, responseObserver);
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
	BatchManagementGrpc.BatchManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteBatchOperation(request, responseObserver);
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
	BatchManagementGrpc.BatchManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createBatchElement(request, responseObserver);
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
	BatchManagementGrpc.BatchManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listBatchElements(request, responseObserver);
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
	BatchManagementGrpc.BatchManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateBatchElement(request, responseObserver);
	}
    }

    public IBatchOperationsMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IBatchOperationsMicroservice microservice) {
	this.microservice = microservice;
    }
}