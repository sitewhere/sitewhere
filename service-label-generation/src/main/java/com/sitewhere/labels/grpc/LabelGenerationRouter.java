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

import com.sitewhere.grpc.service.GGetAreaTypeLabelRequest;
import com.sitewhere.grpc.service.GGetAreaTypeLabelResponse;
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

    public ILabelGenerationMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(ILabelGenerationMicroservice microservice) {
	this.microservice = microservice;
    }
}