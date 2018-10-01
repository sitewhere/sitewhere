/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sitewhere.grpc.client.spi.multitenant.IMultitenantGrpcChannel;
import com.sitewhere.grpc.service.GCheckTenantEngineAvailableRequest;
import com.sitewhere.grpc.service.GCheckTenantEngineAvailableResponse;
import com.sitewhere.grpc.service.MultitenantManagementGrpc;
import com.sitewhere.grpc.service.MultitenantManagementGrpc.MultitenantManagementBlockingStub;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tracing.ITracerProvider;

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
public abstract class MultitenantGrpcChannel<B, A> extends GrpcChannel<B, A> implements IMultitenantGrpcChannel<B, A> {

    /** Max threads used for executing GPRC requests */
    private static final int THREAD_POOL_SIZE = 25;

    /** Client interceptor for adding tenant token */
    private TenantTokenClientInterceptor tenantTokenInterceptor = new TenantTokenClientInterceptor();

    /** Executor service used to handle GRPC requests */
    private ExecutorService serverExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    public MultitenantGrpcChannel(ITracerProvider tracerProvider, String hostname, int port) {
	super(tracerProvider, hostname, port);
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
	ManagedChannelBuilder<?> builder = ManagedChannelBuilder.forAddress(getHostname(), getPort());
	builder.executor(getServerExecutor());
	builder.usePlaintext().intercept(getTenantTokenInterceptor()).intercept(getJwtInterceptor());
	if (isUseTracingInterceptor()) {
	    builder.intercept(getTracingInterceptor());
	}
	this.channel = builder.build();
	this.blockingStub = createBlockingStub();
	this.asyncStub = createAsyncStub();
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantManagement#
     * checkTenantEngineAvailable()
     */
    @Override
    public boolean checkTenantEngineAvailable() {
	MultitenantManagementBlockingStub stub = MultitenantManagementGrpc.newBlockingStub(getChannel());

	GCheckTenantEngineAvailableRequest request = GCheckTenantEngineAvailableRequest.newBuilder().build();
	GCheckTenantEngineAvailableResponse response = stub.checkTenantEngineAvailable(request);

	if (response == null) {
	    return false;
	}

	return response.getAvailable();
    }

    protected TenantTokenClientInterceptor getTenantTokenInterceptor() {
	return tenantTokenInterceptor;
    }

    protected void setTenantTokenInterceptor(TenantTokenClientInterceptor tenantTokenInterceptor) {
	this.tenantTokenInterceptor = tenantTokenInterceptor;
    }

    public ExecutorService getServerExecutor() {
	return serverExecutor;
    }

    public void setServerExecutor(ExecutorService serverExecutor) {
	this.serverExecutor = serverExecutor;
    }
}