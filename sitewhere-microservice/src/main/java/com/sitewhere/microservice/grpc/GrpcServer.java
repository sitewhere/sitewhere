/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.sitewhere.grpc.client.spi.server.IGrpcServer;
import com.sitewhere.microservice.health.HealthServiceImpl;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;
import io.netty.channel.nio.NioEventLoopGroup;

/**
 * Base class for GRPC servers used by microservices.
 * 
 * @author Derek
 */
public class GrpcServer extends TenantEngineLifecycleComponent implements IGrpcServer {

    /** Max threads used for executing GPRC requests */
    private static final int THREAD_POOL_SIZE = 25;

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

    /** Executor service used to handle GRPC requests */
    private ExecutorService serverExecutor = Executors.newFixedThreadPool(THREAD_POOL_SIZE,
	    new GrpcServerThreadFactory());

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
	builder.executor(getServerExecutor());
	builder.bossEventLoopGroup(new NioEventLoopGroup(1));
	builder.workerEventLoopGroup(new NioEventLoopGroup(100));
	return builder.build();
    }

    /**
     * Build gRPC Health Server.
     * 
     * @return
     */
    protected Server buildHealthServer() {
	getLogger().info("Initialized Health gRPC server on port: " + getHealthPort());

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
	    getLogger().debug("Initialized gRPC API server on port " + getApiPort() + ".");
	    this.healthServer = buildHealthServer();
	    getLogger().debug("Initialized gRPC Health Probe server on port " + getApiPort() + ".");
	} catch (Throwable e) {
	    throw new SiteWhereException("Unable to initialize gRPC server.", e);
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
	    getLogger().debug("Started gRPC API server on port " + getApiPort() + ".");

	    getLogger().debug("Starting gRPC Health Probe server on port " + getHealthPort() + "...");
	    getHealthServer().start();
	    getLogger().debug("Started gRPC Health Probe server on port " + getHealthPort() + ".");
	} catch (IOException e) {
	    throw new SiteWhereException("Unable to start gRPC server.", e);
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
		getLogger().debug("gRPC server terminated successfully.");

		getHealthServer().awaitTermination();
		getLogger().debug("gRPC Health server terminated successfully.");
	    } catch (InterruptedException e) {
		getLogger().error("Interrupted while waiting for gRPC server to terminate.", e);
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

    /** Used for naming gRPC server executor threads */
    private class GrpcServerThreadFactory implements ThreadFactory {

	/** Counts threads */
	private AtomicInteger counter = new AtomicInteger();

	public Thread newThread(Runnable r) {
	    return new Thread(r, "gRPC Server " + counter.incrementAndGet());
	}
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

    public ExecutorService getServerExecutor() {
	return serverExecutor;
    }

    public void setServerExecutor(ExecutorService serverExecutor) {
	this.serverExecutor = serverExecutor;
    }

    public HealthServiceImpl getHealthService() {
	return healthService;
    }

    public void setHealthService(HealthServiceImpl healthService) {
	this.healthService = healthService;
    }
}