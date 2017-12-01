/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.client;

import com.sitewhere.grpc.model.spi.IGrpcChannel;
import com.sitewhere.grpc.model.tracing.ClientTracingInterceptor;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tracing.ITracerProvider;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentracing.Tracer;

/**
 * Management wrapper for a GRPC channel.
 * 
 * @author Derek
 *
 * @param <B>
 * @param <A>
 */
public abstract class GrpcChannel<B, A> extends TenantEngineLifecycleComponent implements IGrpcChannel<B, A> {

    /** Tracer provider */
    protected ITracerProvider tracerProvider;

    /** Remote host */
    protected String hostname;

    /** Remote port */
    protected int port;

    /** GRPC managed channe */
    protected ManagedChannel channel;

    /** Blocking stub */
    protected B blockingStub;

    /** Asynchronous stub */
    protected A asyncStub;

    /** Client interceptor for adding JWT from Spring Security context */
    protected JwtClientInterceptor jwt;

    /** Client interceptor for GRPC tracing */
    protected ClientTracingInterceptor trace;

    public GrpcChannel(ITracerProvider tracerProvider, String hostname, int port) {
	this.tracerProvider = tracerProvider;
	this.hostname = hostname;
	this.port = port;

	this.jwt = new JwtClientInterceptor();
	this.trace = new ClientTracingInterceptor(tracerProvider.getTracer());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi
     * .server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.channel = ManagedChannelBuilder.forAddress(getHostname(), getPort()).usePlaintext(true).intercept(jwt)
		.intercept(trace).build();
	this.blockingStub = createBlockingStub();
	this.asyncStub = createAsyncStub();
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	if (getChannel() != null) {
	    getChannel().shutdown();
	}
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#getChannel()
     */
    @Override
    public ManagedChannel getChannel() {
	return channel;
    }

    public void setChannel(ManagedChannel channel) {
	this.channel = channel;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#getBlockingStub()
     */
    @Override
    public B getBlockingStub() {
	return blockingStub;
    }

    public void setBlockingStub(B blockingStub) {
	this.blockingStub = blockingStub;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#getAsyncStub()
     */
    @Override
    public A getAsyncStub() {
	return asyncStub;
    }

    public void setAsyncStub(A asyncStub) {
	this.asyncStub = asyncStub;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#createBlockingStub()
     */
    @Override
    public abstract B createBlockingStub();

    /*
     * @see com.sitewhere.grpc.model.spi.IGrpcChannel#createAsyncStub()
     */
    @Override
    public abstract A createAsyncStub();

    /*
     * @see com.sitewhere.spi.tracing.ITracerProvider#getTracer()
     */
    @Override
    public Tracer getTracer() {
	return getTracerProvider().getTracer();
    }

    public ITracerProvider getTracerProvider() {
	return tracerProvider;
    }

    public void setTracerProvider(ITracerProvider tracerProvider) {
	this.tracerProvider = tracerProvider;
    }

    public String getHostname() {
	return hostname;
    }

    public void setHostname(String hostname) {
	this.hostname = hostname;
    }

    public int getPort() {
	return port;
    }

    public void setPort(int port) {
	this.port = port;
    }
}