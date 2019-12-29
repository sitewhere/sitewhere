/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.microservice;

import com.sitewhere.grpc.service.ScheduleManagementGrpc;
import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.schedule.configuration.ScheduleManagementTenantConfiguration;
import com.sitewhere.schedule.configuration.ScheduleManagementTenantEngineModule;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements schedule
 * management functionality.
 */
public class ScheduleManagementTenantEngine extends MicroserviceTenantEngine<ScheduleManagementTenantConfiguration>
	implements IScheduleManagementTenantEngine {

    /** Schedule management persistence API */
    private IScheduleManagement scheduleManagement;

    /** Responds to schedule management GRPC requests */
    private ScheduleManagementGrpc.ScheduleManagementImplBase scheduleManagementImpl;

    public ScheduleManagementTenantEngine(SiteWhereTenantEngine engine) {
	super(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationClass()
     */
    @Override
    public Class<ScheduleManagementTenantConfiguration> getConfigurationClass() {
	return ScheduleManagementTenantConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * createConfigurationModule()
     */
    @Override
    public ITenantEngineModule<ScheduleManagementTenantConfiguration> createConfigurationModule() {
	return new ScheduleManagementTenantEngineModule(getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// // Create management interfaces.
	// this.scheduleManagement = (IScheduleManagement) getModuleContext()
	// .getBean(ScheduleManagementBeans.BEAN_SCHEDULE_MANAGEMENT);
	// this.scheduleManagementImpl = new
	// ScheduleManagementImpl((IScheduleManagementMicroservice) getMicroservice(),
	// getScheduleManagement());

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize schedule management persistence.
	init.addInitializeStep(this, getScheduleManagement(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStart(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start schedule management persistence.
	start.addStartStep(this, getScheduleManagement(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop schedule management persistence.
	stop.addStopStep(this, getScheduleManagement());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.schedule.spi.microservice.IScheduleManagementTenantEngine#
     * getScheduleManagement()
     */
    @Override
    public IScheduleManagement getScheduleManagement() {
	return scheduleManagement;
    }

    protected void setScheduleManagement(IScheduleManagement scheduleManagement) {
	this.scheduleManagement = scheduleManagement;
    }

    /*
     * @see com.sitewhere.schedule.spi.microservice.IScheduleManagementTenantEngine#
     * getScheduleManagementImpl()
     */
    @Override
    public ScheduleManagementGrpc.ScheduleManagementImplBase getScheduleManagementImpl() {
	return scheduleManagementImpl;
    }

    protected void setScheduleManagementImpl(ScheduleManagementGrpc.ScheduleManagementImplBase scheduleManagementImpl) {
	this.scheduleManagementImpl = scheduleManagementImpl;
    }
}