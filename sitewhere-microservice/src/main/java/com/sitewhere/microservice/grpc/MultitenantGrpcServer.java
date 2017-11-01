/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.grpc.BindableService;
import io.grpc.ServerBuilder;

/**
 * Base class for GRPC servers that handle requests for multiple tenants.
 * 
 * @author Derek
 */
public class MultitenantGrpcServer extends GrpcServer {

    /** Interceptor for tenant token */
    private TenantTokenServerInterceptor tenant;

    public MultitenantGrpcServer(IMicroservice microservice, BindableService serviceImplementation) {
	super(microservice, serviceImplementation);
	this.tenant = new TenantTokenServerInterceptor(microservice);
    }

    public MultitenantGrpcServer(IMicroservice microservice, BindableService serviceImplementation, int port) {
	super(microservice, serviceImplementation, port);
	this.tenant = new TenantTokenServerInterceptor(microservice);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.
     * sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	try {
	    ServerBuilder<?> builder = ServerBuilder.forPort(port);
	    this.server = builder.addService(getServiceImplementation()).intercept(jwt).intercept(tenant)
		    .intercept(trace).build();
	} catch (Throwable e) {
	    throw new SiteWhereException("Unable to initialize GRPC server.", e);
	}
    }
}