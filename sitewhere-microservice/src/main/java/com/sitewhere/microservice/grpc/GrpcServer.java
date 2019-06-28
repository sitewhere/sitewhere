/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import java.io.IOException;

import com.sitewhere.grpc.client.spi.server.IGrpcServer;
import com.sitewhere.microservice.health.HealthServiceImpl;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

/**
 * Base class for GRPC servers used by microservices.
 * 
 * @author Derek
 */
public class GrpcServer extends TenantEngineLifecycleComponent implements IGrpcServer {

    /** Port for gRPC server */
    private int apiPort;

    /** Port for gRPC Health Protocol. */
    private int healthPort;

    /** Wrapped GRPC server */
    private Server server;

    /** Wrapped gRPC Health server */
    private Server healthServer;

    /** Service implementation */
    private BindableService serviceImplementation;

    /** Interceptor for JWT authentication */
    private JwtServerInterceptor jwtInterceptor;

    /** Health Service Implementation */
    private HealthServiceImpl healthService = new HealthServiceImpl();

    public GrpcServer(BindableService serviceImplementation, int apiPort, int healthPort) {
	this.serviceImplementation = serviceImplementation;
	this.apiPort = apiPort;
	this.healthPort = healthPort;
    }

    /**
     * Build server component based on configuration.
     * 
     * @return
     */
    protected Server buildServer() {
	NettyServerBuilder builder = NettyServerBuilder.forPort(getApiPort());
	builder.addService(getServiceImplementation()).intercept(getJwtInterceptor());
	return builder.build();
    }

    /**
     * Build gRPC Health Server.
     * 
     * @return
     */
    protected Server buildHealthServer() {
	NettyServerBuilder builder = NettyServerBuilder.forPort(getHealthPort());
	builder.addService(getHealthService());
	return builder.build();
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
	    this.jwtInterceptor = new JwtServerInterceptor(getMicroservice(), getServiceImplementation().getClass());
	    this.server = buildServer();
	    getLogger().info("Initialized gRPC API server on port " + getApiPort() + ".");
	    this.healthServer = buildHealthServer();
	    getLogger().info("Initialized gRPC Health Probe server on port " + getHealthPort() + ".");
	} catch (Throwable t) {
	    getLogger().error("Unhandled exception initializing gRPC server.", t);
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
	    getLogger().debug("Starting gRPC API server on port " + getApiPort() + "...");
	    getServer().start();
	    getLogger().info("Started gRPC API server on port " + getApiPort() + ".");

	    getLogger().debug("Starting gRPC Health Probe server on port " + getHealthPort() + "...");
	    getHealthServer().start();
	    getLogger().info("Started gRPC Health Probe server on port " + getHealthPort() + ".");
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to start gRPC server.", e);
	} catch (Throwable t) {
	    getLogger().error("Unhandled exception starting gRPC server.", t);
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
	if (getServer() != null) {
	    try {
		getServer().shutdown();
		getServer().awaitTermination();
		getLogger().info("gRPC server terminated successfully.");

		getHealthServer().shutdown();
		getHealthServer().awaitTermination();
		getLogger().info("gRPC Health server terminated successfully.");
	    } catch (InterruptedException e) {
		getLogger().error("Interrupted while waiting for gRPC server to terminate.", e);
	    } catch (Throwable t) {
		getLogger().error("Unhandled exception stopping gRPC server.", t);
	    }
	}
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

    public Server getServer() {
	return server;
    }

    public void setServer(Server server) {
	this.server = server;
    }

    public Server getHealthServer() {
	return healthServer;
    }

    public void setHealthServer(Server healthServer) {
	this.healthServer = healthServer;
    }

    public int getApiPort() {
	return apiPort;
    }

    public void setApiPort(int apiPort) {
	this.apiPort = apiPort;
    }

    public int getHealthPort() {
	return healthPort;
    }

    public void setHealthPort(int healthPort) {
	this.healthPort = healthPort;
    }

    public JwtServerInterceptor getJwtInterceptor() {
	return jwtInterceptor;
    }

    public void setJwtInterceptor(JwtServerInterceptor jwtInterceptor) {
	this.jwtInterceptor = jwtInterceptor;
    }

    public HealthServiceImpl getHealthService() {
	return healthService;
    }

    public void setHealthService(HealthServiceImpl healthService) {
	this.healthService = healthService;
    }
}