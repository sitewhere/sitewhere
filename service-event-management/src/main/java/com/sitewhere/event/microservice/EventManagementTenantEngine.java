/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.microservice;

import com.sitewhere.event.grpc.EventManagementImpl;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.microservice.spi.multitenant.IMultitenantMicroservice;
import com.sitewhere.microservice.spi.multitenant.ITenantTemplate;
import com.sitewhere.microservice.spi.spring.EventManagementBeans;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.InitializeComponentLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements event
 * management functionality.
 * 
 * @author Derek
 */
public class EventManagementTenantEngine extends MicroserviceTenantEngine implements IEventManagementTenantEngine {

    /** Event management persistence API */
    private IDeviceEventManagement eventManagement;

    /** Responds to event management GRPC requests */
    private DeviceEventManagementGrpc.DeviceEventManagementImplBase eventManagementImpl;

    public EventManagementTenantEngine(IMultitenantMicroservice<IEventManagementTenantEngine> microservice,
	    ITenant tenant) {
	super(microservice, tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.eventManagement = (IDeviceEventManagement) getModuleContext()
		.getBean(EventManagementBeans.BEAN_EVENT_MANAGEMENT);
	this.eventManagementImpl = new EventManagementImpl(getEventManagement());

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize discoverable lifecycle components.
	init.addStep(getMicroservice().initializeDiscoverableBeans(getModuleContext(), monitor));

	// Initialize event management persistence.
	init.addStep(new InitializeComponentLifecycleStep(this, getEventManagement(), true));

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantStart(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantBootstrap(com.sitewhere.microservice.spi.multitenant.
     * ITenantTemplate,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(ITenantTemplate template, ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.event.spi.microservice.IEventManagementTenantEngine#
     * getEventManagement()
     */
    @Override
    public IDeviceEventManagement getEventManagement() {
	return eventManagement;
    }

    public void setEventManagement(IDeviceEventManagement eventManagement) {
	this.eventManagement = eventManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.event.spi.microservice.IEventManagementTenantEngine#
     * getEventManagementImpl()
     */
    @Override
    public DeviceEventManagementGrpc.DeviceEventManagementImplBase getEventManagementImpl() {
	return eventManagementImpl;
    }

    public void setEventManagementImpl(DeviceEventManagementGrpc.DeviceEventManagementImplBase eventManagementImpl) {
	this.eventManagementImpl = eventManagementImpl;
    }
}