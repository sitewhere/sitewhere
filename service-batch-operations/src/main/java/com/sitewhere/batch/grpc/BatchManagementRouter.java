/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.grpc;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcRouter;
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
import com.sitewhere.microservice.grpc.TenantTokenServerInterceptor;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 * 
 * @author Derek
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
	String tenantId = TenantTokenServerInterceptor.TENANT_ID_KEY.get();
	if (tenantId == null) {
	    throw new RuntimeException("Tenant id not found in schedule management request.");
	}
	try {
	    IBatchOperationsTenantEngine engine = getMicroservice()
		    .assureTenantEngineAvailable(UUID.fromString(tenantId));
	    UserContextManager.setCurrentTenant(engine.getTenant());
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
     * listBatchOperationElements(com.sitewhere.grpc.service.
     * GListBatchOperationElementsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listBatchOperationElements(GListBatchOperationElementsRequest request,
	    StreamObserver<GListBatchOperationElementsResponse> responseObserver) {
	BatchManagementGrpc.BatchManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listBatchOperationElements(request, responseObserver);
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
	BatchManagementGrpc.BatchManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateBatchOperationElement(request, responseObserver);
	}
    }

    public IBatchOperationsMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IBatchOperationsMicroservice microservice) {
	this.microservice = microservice;
    }
}