/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import com.sitewhere.grpc.client.spi.IApiChannel;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;

/**
 * Base class for channels that uses SiteWhere APIs to communicate with GRPC
 * services.
 * 
 * @author Derek
 *
 * @param <T>
 */
public abstract class ApiChannel<T extends GrpcChannel<?, ?>> extends TenantEngineLifecycleComponent
	implements IApiChannel<T> {

    /** Instance settings */
    private IInstanceSettings settings;

    /** Function identifier */
    private IFunctionIdentifier functionIdentifier;

    /** gRPC service identifier */
    private IGrpcServiceIdentifier grpcServiceIdentifier;

    /** Binding port */
    private int port;

    /** Underlying GRPC channel */
    private T grpcChannel;

    public ApiChannel(IInstanceSettings settings, IFunctionIdentifier functionIdentifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	this.settings = settings;
	this.functionIdentifier = functionIdentifier;
	this.grpcServiceIdentifier = grpcServiceIdentifier;
	this.port = port;
    }

    /*
     * @see com.sitewhere.grpc.model.spi.IApiChannel#getGrpcChannel()
     */
    @Override
    public T getGrpcChannel() {
	return grpcChannel;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.grpcChannel = createGrpcChannel(getSettings(), getFunctionIdentifier(), getGrpcServiceIdentifier(),
		getPort());
	getLogger().info(String.format("Initializing gRPC channel for %s to '%s:%d'", getGrpcServiceIdentifier(),
		getGrpcChannel().getHostname(), getGrpcChannel().getPort()));
	initializeNestedComponent(getGrpcChannel(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLogger().info(String.format("Starting gRPC channel to '%s:%d'", getGrpcChannel().getHostname(),
		getGrpcChannel().getPort()));
	startNestedComponent(getGrpcChannel(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLogger().info(String.format("Stopping gRPC channel to '%s:%d'", getGrpcChannel().getHostname(),
		getGrpcChannel().getPort()));
	stopNestedComponent(getGrpcChannel(), monitor);
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiChannel#getFunctionIdentifier()
     */
    @Override
    public IFunctionIdentifier getFunctionIdentifier() {
	return functionIdentifier;
    }

    public void setFunctionIdentifier(IFunctionIdentifier functionIdentifier) {
	this.functionIdentifier = functionIdentifier;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiChannel#getGrpcServiceIdentifier()
     */
    @Override
    public IGrpcServiceIdentifier getGrpcServiceIdentifier() {
	return grpcServiceIdentifier;
    }

    public void setGrpcServiceIdentifier(IGrpcServiceIdentifier grpcServiceIdentifier) {
	this.grpcServiceIdentifier = grpcServiceIdentifier;
    }

    /*
     * @see com.sitewhere.grpc.client.spi.IApiChannel#getPort()
     */
    @Override
    public int getPort() {
	return port;
    }

    protected void setPort(int port) {
	this.port = port;
    }

    protected IInstanceSettings getSettings() {
	return settings;
    }

    protected void setSettings(IInstanceSettings settings) {
	this.settings = settings;
    }
}