/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.client;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.grpc.ManagedChannelBuilder;

/**
 * Management wrapper for a GRPC channel that handles requests for multiple
 * tenants.
 * 
 * @author Derek
 *
 * @param <B>
 * @param <A>
 */
public abstract class MultitenantGrpcChannel<B, A> extends GrpcChannel<B, A> {

    /** Client interceptor for adding tenant token */
    private TenantTokenClientInterceptor tenant = new TenantTokenClientInterceptor();

    public MultitenantGrpcChannel(String hostname, int port) {
	super(hostname, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.channel = ManagedChannelBuilder.forAddress(getHostname(), getPort()).usePlaintext(true).intercept(jwt)
		.intercept(tenant).build();
	this.blockingStub = createBlockingStub();
	this.asyncStub = createAsyncStub();
    }
}