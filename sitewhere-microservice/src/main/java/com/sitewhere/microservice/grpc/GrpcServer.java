/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.spi.server.IGrpcServer;
import com.sitewhere.grpc.model.tracing.ServerTracingInterceptor;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * Base class for GRPC servers used by microservices.
 * 
 * @author Derek
 */
public class GrpcServer extends TenantEngineLifecycleComponent implements IGrpcServer {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(GrpcServer.class);

    /** Port for GRPC server */
    protected int port;

    /** Wrapped GRPC server */
    protected Server server;

    /** Parent microservice */
    protected IMicroservice microservice;

    /** Service implementation */
    protected BindableService serviceImplementation;

    /** Indicates whether to use tracing interceptor */
    protected boolean useTracingInterceptor = false;

    /** Interceptor for JWT authentication */
    protected JwtServerInterceptor jwt;

    /** Interceptor for open tracing APIs */
    protected ServerTracingInterceptor trace;

    public GrpcServer(IMicroservice microservice, BindableService serviceImplementation) {
	this(microservice, serviceImplementation, microservice.getInstanceSettings().getGrpcPort());
    }

    public GrpcServer(IMicroservice microservice, BindableService serviceImplementation, int port) {
	this.microservice = microservice;
	this.serviceImplementation = serviceImplementation;
	this.port = port;

	this.jwt = new JwtServerInterceptor(microservice, serviceImplementation.getClass());
	this.trace = new ServerTracingInterceptor(microservice.getTracer());
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
	    builder.addService(getServiceImplementation()).intercept(jwt);
	    builder.intercept(trace);
	    this.server = builder.build();
	    getLogger().info("Initialized GRPC server on port " + port + ".");
	} catch (Throwable e) {
	    throw new SiteWhereException("Unable to initialize tenant management GRPC server.", e);
	}
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
	try {
	    getServer().start();
	    getLogger().info("Started GRPC server on port " + port + ".");
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to start tenant management GRPC server.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getServer().shutdown();
	try {
	    getServer().awaitTermination();
	    getLogger().info("GRPC server terminated successfully.");
	} catch (InterruptedException e) {
	    getLogger().error("Interrupted while waiting for GRPC server to terminate.", e);
	}
    }

    /*
     * @see com.sitewhere.grpc.client.spi.server.IGrpcServer#getMicroservice()
     */
    @Override
    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.server.IGrpcServer#getServiceImplementation()
     */
    @Override
    public BindableService getServiceImplementation() {
	return serviceImplementation;
    }

    public void setServiceImplementation(BindableService serviceImplementation) {
	this.serviceImplementation = serviceImplementation;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.server.IGrpcServer#isUseTracingInterceptor()
     */
    @Override
    public boolean isUseTracingInterceptor() {
	return useTracingInterceptor;
    }

    public void setUseTracingInterceptor(boolean useTracingInterceptor) {
	this.useTracingInterceptor = useTracingInterceptor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }

    public Server getServer() {
	return server;
    }

    public void setServer(Server server) {
	this.server = server;
    }
}