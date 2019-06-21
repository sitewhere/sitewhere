/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.microservice;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.grpc.service.ScheduleManagementGrpc;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.microservice.grpc.ScheduleManagementImpl;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.schedule.initializer.GroovyScheduleModelInitializer;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementMicroservice;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.spring.ScheduleManagementBeans;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements schedule
 * management functionality.
 * 
 * @author Derek
 */
public class ScheduleManagementTenantEngine extends MicroserviceTenantEngine
	implements IScheduleManagementTenantEngine {

    /** Schedule management persistence API */
    private IScheduleManagement scheduleManagement;

    /** Responds to schedule management GRPC requests */
    private ScheduleManagementGrpc.ScheduleManagementImplBase scheduleManagementImpl;

    public ScheduleManagementTenantEngine(ITenant tenant) {
	super(tenant);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create management interfaces.
	this.scheduleManagement = (IScheduleManagement) getModuleContext()
		.getBean(ScheduleManagementBeans.BEAN_SCHEDULE_MANAGEMENT);
	this.scheduleManagementImpl = new ScheduleManagementImpl((IScheduleManagementMicroservice) getMicroservice(),
		getScheduleManagement());

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize discoverable lifecycle components.
	init.addStep(initializeDiscoverableBeans(getModuleContext()));

	// Initialize schedule management persistence.
	init.addInitializeStep(this, getScheduleManagement(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStart(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start discoverable lifecycle components.
	start.addStep(startDiscoverableBeans(getModuleContext()));

	// Start schedule management persistence.
	start.addStartStep(this, getScheduleManagement(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantBootstrap(com.sitewhere.spi.microservice.multitenant.IDatasetTemplate,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(IDatasetTemplate template, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	List<String> scripts = Collections.emptyList();
	if (template.getInitializers() != null) {
	    scripts = template.getInitializers().getScheduleManagement();
	    for (String script : scripts) {
		getTenantScriptSynchronizer().add(script);
	    }
	}

	// Execute calls as superuser.
	Authentication previous = SecurityContextHolder.getContext().getAuthentication();
	try {
	    SecurityContextHolder.getContext()
		    .setAuthentication(getMicroservice().getSystemUser().getAuthenticationForTenant(getTenant()));
	    GroovyConfiguration groovy = new GroovyConfiguration(getTenantScriptSynchronizer());
	    groovy.start(new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Initialize schedule model."),
		    getMicroservice()));
	    for (String script : scripts) {
		GroovyScheduleModelInitializer initializer = new GroovyScheduleModelInitializer(groovy, script);
		initializer.initialize(getScheduleManagement());
	    }
	} finally {
	    SecurityContextHolder.getContext().setAuthentication(previous);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop schedule management persistence.
	stop.addStopStep(this, getScheduleManagement());

	// Stop discoverable lifecycle components.
	stop.addStep(stopDiscoverableBeans(getModuleContext()));

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