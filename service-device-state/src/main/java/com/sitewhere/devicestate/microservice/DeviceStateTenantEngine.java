/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.microservice;

import com.sitewhere.devicestate.grpc.DeviceStateImpl;
import com.sitewhere.devicestate.kafka.DeviceStateEnrichedEventsConsumer;
import com.sitewhere.devicestate.spi.kafka.IDeviceStateEnrichedEventsConsumer;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.grpc.service.DeviceStateGrpc;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.state.IDeviceStateManagement;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.spring.DeviceStateManagementBeans;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements device
 * state management functionality.
 * 
 * @author Derek
 */
public class DeviceStateTenantEngine extends MicroserviceTenantEngine implements IDeviceStateTenantEngine {

    /** Device state management persistence API */
    private IDeviceStateManagement deviceStateManagement;

    /** Responds to device state GRPC requests */
    private DeviceStateGrpc.DeviceStateImplBase deviceStateImpl;

    /** Kafka consumer for processing enriched events for device state */
    private IDeviceStateEnrichedEventsConsumer deviceStateEnrichedEventsConsumer;

    public DeviceStateTenantEngine(ITenant tenant) {
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
	IDeviceStateManagement implementation = (IDeviceStateManagement) getModuleContext()
		.getBean(DeviceStateManagementBeans.BEAN_DEVICE_STATE_MANAGEMENT);
	this.deviceStateManagement = implementation;
	this.deviceStateImpl = new DeviceStateImpl((IDeviceStateMicroservice) getMicroservice(),
		getDeviceStateManagement());
	this.deviceStateEnrichedEventsConsumer = new DeviceStateEnrichedEventsConsumer();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize discoverable lifecycle components.
	init.addStep(initializeDiscoverableBeans(getModuleContext()));

	// Initialize device state management persistence.
	init.addInitializeStep(this, getDeviceStateManagement(), true);

	// Initialize device state enriched events consumer.
	init.addInitializeStep(this, getDeviceStateEnrichedEventsConsumer(), true);

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

	// Start device state management persistence.
	start.addStartStep(this, getDeviceStateManagement(), true);

	// Start device state enriched events consumer.
	start.addStartStep(this, getDeviceStateEnrichedEventsConsumer(), true);

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
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop device state enriched events consumer.
	stop.addStopStep(this, getDeviceStateEnrichedEventsConsumer());

	// Stop device state management persistence.
	stop.addStopStep(this, getDeviceStateManagement());

	// Stop discoverable lifecycle components.
	stop.addStep(stopDiscoverableBeans(getModuleContext()));

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine#
     * getDeviceStateManagement()
     */
    @Override
    public IDeviceStateManagement getDeviceStateManagement() {
	return deviceStateManagement;
    }

    public void setDeviceStateManagement(IDeviceStateManagement deviceStateManagement) {
	this.deviceStateManagement = deviceStateManagement;
    }

    /*
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine#
     * getDeviceStateImpl()
     */
    @Override
    public DeviceStateGrpc.DeviceStateImplBase getDeviceStateImpl() {
	return deviceStateImpl;
    }

    public void setDeviceStateImpl(DeviceStateGrpc.DeviceStateImplBase deviceStateImpl) {
	this.deviceStateImpl = deviceStateImpl;
    }

    /*
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine#
     * getDeviceStateEnrichedEventsConsumer()
     */
    @Override
    public IDeviceStateEnrichedEventsConsumer getDeviceStateEnrichedEventsConsumer() {
	return deviceStateEnrichedEventsConsumer;
    }

    public void setDeviceStateEnrichedEventsConsumer(
	    IDeviceStateEnrichedEventsConsumer deviceStateEnrichedEventsConsumer) {
	this.deviceStateEnrichedEventsConsumer = deviceStateEnrichedEventsConsumer;
    }
}