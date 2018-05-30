/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * Base class for GRPC servers that handle requests for multiple tenants.
 * 
 * @author Derek
 */
public class MultitenantGrpcServer extends GrpcServer {

    /** Interceptor for tenant token */
    private TenantTokenServerInterceptor tenant;

    public MultitenantGrpcServer(BindableService serviceImplementation, int port) {
	super(serviceImplementation, port);
    }

    /**
     * Build server component based on configuration.
     * 
     * @return
     */
    protected Server buildServer() {
	this.tenant = new TenantTokenServerInterceptor(getMicroservice());
	ServerBuilder<?> builder = ServerBuilder.forPort(getPort());
	builder.addService(getServiceImplementation()).intercept(getJwtInterceptor()).intercept(tenant);
	builder.addService(new MultitenantManagementImpl((IMultitenantMicroservice<?, ?>) getMicroservice()));
	if (isUseTracingInterceptor()) {
	    builder.intercept(getTracingInterceptor());
	}
	return builder.build();
    }
}