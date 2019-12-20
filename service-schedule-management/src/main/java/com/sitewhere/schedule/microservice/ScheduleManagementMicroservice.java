/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.microservice;

import javax.enterprise.context.ApplicationScoped;

import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MultitenantMicroservice;
import com.sitewhere.schedule.configuration.ScheduleManagementConfiguration;
import com.sitewhere.schedule.configuration.ScheduleManagementModule;
import com.sitewhere.schedule.grpc.ScheduleManagementGrpcServer;
import com.sitewhere.schedule.spi.grpc.IScheduleManagementGrpcServer;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementMicroservice;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.configuration.IMicroserviceModule;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Microservice that provides schedule management functionality.
 */
@ApplicationScoped
public class ScheduleManagementMicroservice extends
	MultitenantMicroservice<MicroserviceIdentifier, ScheduleManagementConfiguration, IScheduleManagementTenantEngine>
	implements IScheduleManagementMicroservice {

    /** Provides server for schedule management GRPC requests */
    private IScheduleManagementGrpcServer scheduleManagementGrpcServer;

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getName()
     */
    @Override
    public String getName() {
	return "Schedule Management";
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#getIdentifier()
     */
    @Override
    public MicroserviceIdentifier getIdentifier() {
	return MicroserviceIdentifier.ScheduleManagement;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.IConfigurableMicroservice#
     * getConfigurationClass()
     */
    @Override
    public Class<ScheduleManagementConfiguration> getConfigurationClass() {
	return ScheduleManagementConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroservice#createConfigurationModule()
     */
    @Override
    public IMicroserviceModule<ScheduleManagementConfiguration> createConfigurationModule() {
	return new ScheduleManagementModule(getMicroserviceConfiguration());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice#
     * createTenantEngine(io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine)
     */
    @Override
    public IScheduleManagementTenantEngine createTenantEngine(SiteWhereTenantEngine engine) throws SiteWhereException {
	return new ScheduleManagementTenantEngine(engine);
    }

    /*
     * @see
     * com.sitewhere.microservice.multitenant.MultitenantMicroservice#initialize(com
     * .sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Create schedule management GRPC server.
	this.scheduleManagementGrpcServer = new ScheduleManagementGrpcServer(this);

	// Create step that will start components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getName());

	// Initialize schedule management GRPC server.
	init.addInitializeStep(this, getScheduleManagementGrpcServer(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.microservice.multitenant.MultitenantMicroservice#start(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getName());

	// Start schedule management GRPC server.
	start.addStartStep(this, getScheduleManagementGrpcServer(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MultitenantMicroservice#stop(com.
     * sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getName());

	// Stop schedule management GRPC server.
	stop.addStopStep(this, getScheduleManagementGrpcServer());

	// Execute shutdown steps.
	stop.execute(monitor);

	super.stop(monitor);
    }

    /*
     * @see com.sitewhere.schedule.spi.microservice.IScheduleManagementMicroservice#
     * getScheduleManagementGrpcServer()
     */
    @Override
    public IScheduleManagementGrpcServer getScheduleManagementGrpcServer() {
	return scheduleManagementGrpcServer;
    }

    protected void setScheduleManagementGrpcServer(IScheduleManagementGrpcServer scheduleManagementGrpcServer) {
	this.scheduleManagementGrpcServer = scheduleManagementGrpcServer;
    }
}