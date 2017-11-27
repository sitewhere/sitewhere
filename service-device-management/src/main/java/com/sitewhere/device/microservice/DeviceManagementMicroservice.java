/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.device.grpc.DeviceManagementGrpcServer;
import com.sitewhere.device.spi.grpc.IDeviceManagementGrpcServer;
import com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice;
import com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Microservice that provides device management functionality.
 * 
 * @author Derek
 */
public class DeviceManagementMicroservice extends MultitenantMicroservice<IDeviceManagementTenantEngine>
	implements IDeviceManagementMicroservice {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Microservice name */
    private static final String NAME = "Device Management";

    /** Identifies module resources such as configuration file */
    private static final String MODULE_IDENTIFIER = "device-management";

    /** Provides server for device management GRPC requests */
    private IDeviceManagementGrpcServer deviceManagementGrpcServer;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.IMicroservice#getIdentifier()
     */
    @Override
    public String getIdentifier() {
	return MODULE_IDENTIFIER;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice#
     * createTenantEngine(com.sitewhere.spi.tenant.ITenant)
     */
    @Override
    public IDeviceManagementTenantEngine createTenantEngine(ITenant tenant) throws SiteWhereException {
	return new DeviceManagementTenantEngine(this, tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.multitenant.MultitenantMicroservice#
     * microserviceInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create device management GRPC server.
	this.deviceManagementGrpcServer = new DeviceManagementGrpcServer(this);

	// Create step that will start components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize device management GRPC server.
	init.addInitializeStep(this, getDeviceManagementGrpcServer(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.multitenant.MultitenantMicroservice#
     * microserviceStart(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Start device management GRPC server.
	start.addStartStep(this, getDeviceManagementGrpcServer(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.multitenant.MultitenantMicroservice#
     * microserviceStop(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void microserviceStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop device management GRPC server.
	stop.addStopStep(this, getDeviceManagementGrpcServer());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    /*
     * @see com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice#
     * getDeviceManagementGrpcServer()
     */
    @Override
    public IDeviceManagementGrpcServer getDeviceManagementGrpcServer() {
	return deviceManagementGrpcServer;
    }

    public void setDeviceManagementGrpcServer(IDeviceManagementGrpcServer deviceManagementGrpcServer) {
	this.deviceManagementGrpcServer = deviceManagementGrpcServer;
    }
}