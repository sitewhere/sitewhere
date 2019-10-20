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

import com.sitewhere.grpc.client.GrpcContextKeys;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcRouter;
import com.sitewhere.grpc.service.GGetAreaLabelRequest;
import com.sitewhere.grpc.service.GGetAreaLabelResponse;
import com.sitewhere.grpc.service.GGetAreaTypeLabelRequest;
import com.sitewhere.grpc.service.GGetAreaTypeLabelResponse;
import com.sitewhere.grpc.service.GGetAssetLabelRequest;
import com.sitewhere.grpc.service.GGetAssetLabelResponse;
import com.sitewhere.grpc.service.GGetAssetTypeLabelRequest;
import com.sitewhere.grpc.service.GGetAssetTypeLabelResponse;
import com.sitewhere.grpc.service.GGetCustomerLabelRequest;
import com.sitewhere.grpc.service.GGetCustomerLabelResponse;
import com.sitewhere.grpc.service.GGetCustomerTypeLabelRequest;
import com.sitewhere.grpc.service.GGetCustomerTypeLabelResponse;
import com.sitewhere.grpc.service.GGetDeviceAssignmentLabelRequest;
import com.sitewhere.grpc.service.GGetDeviceAssignmentLabelResponse;
import com.sitewhere.grpc.service.GGetDeviceGroupLabelRequest;
import com.sitewhere.grpc.service.GGetDeviceGroupLabelResponse;
import com.sitewhere.grpc.service.GGetDeviceLabelRequest;
import com.sitewhere.grpc.service.GGetDeviceLabelResponse;
import com.sitewhere.grpc.service.GGetDeviceTypeLabelRequest;
import com.sitewhere.grpc.service.GGetDeviceTypeLabelResponse;
import com.sitewhere.grpc.service.LabelGenerationGrpc;
import com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase;
import com.sitewhere.labels.spi.microservice.ILabelGenerationMicroservice;
import com.sitewhere.labels.spi.microservice.ILabelGenerationTenantEngine;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 * 
 * @author Derek
 */
public class LabelGenerationRouter extends LabelGenerationGrpc.LabelGenerationImplBase
	implements IGrpcRouter<LabelGenerationGrpc.LabelGenerationImplBase> {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(LabelGenerationRouter.class);

    /** Parent microservice */
    private ILabelGenerationMicroservice microservice;

    public LabelGenerationRouter(ILabelGenerationMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.spi.grpc.IGrpcRouter#getTenantImplementation()
     */
    @Override
    public LabelGenerationImplBase getTenantImplementation(StreamObserver<?> observer) {
	String token = GrpcContextKeys.TENANT_TOKEN_KEY.get();
	if (token == null) {
	    throw new RuntimeException("Tenant token not found in request.");
	}
	try {
	    ILabelGenerationTenantEngine engine = getMicroservice().assureTenantEngineAvailable(token);
	    return engine.getLabelGenerationImpl();
	} catch (TenantEngineNotAvailableException e) {
	    observer.onError(GrpcUtils.convertServerException(e));
	    return null;
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getCustomerTypeLabel(com.sitewhere.grpc.service.GGetCustomerTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCustomerTypeLabel(GGetCustomerTypeLabelRequest request,
	    StreamObserver<GGetCustomerTypeLabelResponse> responseObserver) {
	LabelGenerationGrpc.LabelGenerationImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getCustomerTypeLabel(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getCustomerLabel(com.sitewhere.grpc.service.GGetCustomerLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCustomerLabel(GGetCustomerLabelRequest request,
	    StreamObserver<GGetCustomerLabelResponse> responseObserver) {
	LabelGenerationGrpc.LabelGenerationImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getCustomerLabel(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAreaTypeLabel(com.sitewhere.grpc.service.GGetAreaTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreaTypeLabel(GGetAreaTypeLabelRequest request,
	    StreamObserver<GGetAreaTypeLabelResponse> responseObserver) {
	LabelGenerationGrpc.LabelGenerationImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getAreaTypeLabel(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAreaLabel(com.sitewhere.grpc.service.GGetAreaLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreaLabel(GGetAreaLabelRequest request, StreamObserver<GGetAreaLabelResponse> responseObserver) {
	LabelGenerationGrpc.LabelGenerationImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getAreaLabel(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceTypeLabel(com.sitewhere.grpc.service.GGetDeviceTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceTypeLabel(GGetDeviceTypeLabelRequest request,
	    StreamObserver<GGetDeviceTypeLabelResponse> responseObserver) {
	LabelGenerationGrpc.LabelGenerationImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceTypeLabel(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceLabel(com.sitewhere.grpc.service.GGetDeviceLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceLabel(GGetDeviceLabelRequest request,
	    StreamObserver<GGetDeviceLabelResponse> responseObserver) {
	LabelGenerationGrpc.LabelGenerationImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceLabel(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceGroupLabel(com.sitewhere.grpc.service.GGetDeviceGroupLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceGroupLabel(GGetDeviceGroupLabelRequest request,
	    StreamObserver<GGetDeviceGroupLabelResponse> responseObserver) {
	LabelGenerationGrpc.LabelGenerationImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceGroupLabel(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceAssignmentLabel(com.sitewhere.grpc.service.
     * GGetDeviceAssignmentLabelRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceAssignmentLabel(GGetDeviceAssignmentLabelRequest request,
	    StreamObserver<GGetDeviceAssignmentLabelResponse> responseObserver) {
	LabelGenerationGrpc.LabelGenerationImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceAssignmentLabel(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAssetTypeLabel(com.sitewhere.grpc.service.GGetAssetTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetTypeLabel(GGetAssetTypeLabelRequest request,
	    StreamObserver<GGetAssetTypeLabelResponse> responseObserver) {
	LabelGenerationGrpc.LabelGenerationImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getAssetTypeLabel(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAssetLabel(com.sitewhere.grpc.service.GGetAssetLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetLabel(GGetAssetLabelRequest request, StreamObserver<GGetAssetLabelResponse> responseObserver) {
	LabelGenerationGrpc.LabelGenerationImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getAssetLabel(request, responseObserver);
	}
    }

    public ILabelGenerationMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(ILabelGenerationMicroservice microservice) {
	this.microservice = microservice;
    }
}