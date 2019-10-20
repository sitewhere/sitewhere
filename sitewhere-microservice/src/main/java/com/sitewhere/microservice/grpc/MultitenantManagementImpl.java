/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import com.sitewhere.grpc.client.GrpcContextKeys;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.service.GCheckTenantEngineAvailableRequest;
import com.sitewhere.grpc.service.GCheckTenantEngineAvailableResponse;
import com.sitewhere.grpc.service.MultitenantManagementGrpc;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;

import io.grpc.stub.StreamObserver;

/**
 *
 * @author Jorge Villaverde
 */
public class MultitenantManagementImpl extends MultitenantManagementGrpc.MultitenantManagementImplBase {

    /** Multitenant microservice */
    private IMultitenantMicroservice<?, ?> microservice;

    public MultitenantManagementImpl(IMultitenantMicroservice<?, ?> microservice) {
	super();
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.grpc.service.MultitenantManagementGrpc.
     * MultitenantManagementImplBase#checkMultitenantServicesAvailable(com.sitewhere
     * .grpc.service.GCheckMultitenantServicesAvailableRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void checkTenantEngineAvailable(GCheckTenantEngineAvailableRequest request,
	    StreamObserver<GCheckTenantEngineAvailableResponse> responseObserver) {
	GCheckTenantEngineAvailableResponse.Builder response = GCheckTenantEngineAvailableResponse.newBuilder();
	try {
	    String token = GrpcContextKeys.TENANT_TOKEN_KEY.get();
	    if (token == null) {
		throw new RuntimeException("Tenant token not found in request.");
	    }
	    getMicroservice().assureTenantEngineAvailable(token);
	    response.setAvailable(true);
	} catch (TenantEngineNotAvailableException e) {
	    response.setAvailable(false);
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(MultitenantManagementGrpc.getCheckTenantEngineAvailableMethod(), e,
		    responseObserver);
	    response.setAvailable(false);
	}

	responseObserver.onNext(response.build());
	responseObserver.onCompleted();
    }

    public IMultitenantMicroservice<?, ?> getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMultitenantMicroservice<IFunctionIdentifier, IMicroserviceTenantEngine> microservice) {
	this.microservice = microservice;
    }
}