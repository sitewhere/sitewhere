/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.grpc;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.service.GGetAreaLabelRequest;
import com.sitewhere.grpc.service.GGetAreaLabelResponse;
import com.sitewhere.grpc.service.GGetAreaTypeLabelRequest;
import com.sitewhere.grpc.service.GGetAreaTypeLabelResponse;
import com.sitewhere.grpc.service.GGetAssetLabelRequest;
import com.sitewhere.grpc.service.GGetAssetLabelResponse;
import com.sitewhere.grpc.service.GGetAssetTypeLabelRequest;
import com.sitewhere.grpc.service.GGetAssetTypeLabelResponse;
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
import com.sitewhere.microservice.grpc.TenantTokenServerInterceptor;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.grpc.IGrpcRouter;
import com.sitewhere.spi.microservice.RuntimeServiceNotAvailableException;

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
    public LabelGenerationImplBase getTenantImplementation() {
	String tenantId = TenantTokenServerInterceptor.TENANT_ID_KEY.get();
	if (tenantId == null) {
	    throw new RuntimeException("Tenant id not found in label generation request.");
	}
	try {
	    ILabelGenerationTenantEngine engine = getMicroservice()
		    .getTenantEngineByTenantId(UUID.fromString(tenantId));
	    if (engine != null) {
		UserContextManager.setCurrentTenant(engine.getTenant());
		return engine.getLabelGenerationImpl();
	    }
	    throw new RuntimeServiceNotAvailableException("Tenant engine not found.");
	} catch (SiteWhereException e) {
	    throw new RuntimeServiceNotAvailableException("Error locating tenant engine.", e);
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
	getTenantImplementation().getAreaTypeLabel(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAreaLabel(com.sitewhere.grpc.service.GGetAreaLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreaLabel(GGetAreaLabelRequest request, StreamObserver<GGetAreaLabelResponse> responseObserver) {
	getTenantImplementation().getAreaLabel(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceTypeLabel(com.sitewhere.grpc.service.GGetDeviceTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceTypeLabel(GGetDeviceTypeLabelRequest request,
	    StreamObserver<GGetDeviceTypeLabelResponse> responseObserver) {
	getTenantImplementation().getDeviceTypeLabel(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceLabel(com.sitewhere.grpc.service.GGetDeviceLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceLabel(GGetDeviceLabelRequest request,
	    StreamObserver<GGetDeviceLabelResponse> responseObserver) {
	getTenantImplementation().getDeviceLabel(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceGroupLabel(com.sitewhere.grpc.service.GGetDeviceGroupLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceGroupLabel(GGetDeviceGroupLabelRequest request,
	    StreamObserver<GGetDeviceGroupLabelResponse> responseObserver) {
	getTenantImplementation().getDeviceGroupLabel(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceAssignmentLabel(com.sitewhere.grpc.service.
     * GGetDeviceAssignmentLabelRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceAssignmentLabel(GGetDeviceAssignmentLabelRequest request,
	    StreamObserver<GGetDeviceAssignmentLabelResponse> responseObserver) {
	getTenantImplementation().getDeviceAssignmentLabel(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAssetTypeLabel(com.sitewhere.grpc.service.GGetAssetTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetTypeLabel(GGetAssetTypeLabelRequest request,
	    StreamObserver<GGetAssetTypeLabelResponse> responseObserver) {
	getTenantImplementation().getAssetTypeLabel(request, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAssetLabel(com.sitewhere.grpc.service.GGetAssetLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetLabel(GGetAssetLabelRequest request, StreamObserver<GGetAssetLabelResponse> responseObserver) {
	getTenantImplementation().getAssetLabel(request, responseObserver);
    }

    public ILabelGenerationMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(ILabelGenerationMicroservice microservice) {
	this.microservice = microservice;
    }
}