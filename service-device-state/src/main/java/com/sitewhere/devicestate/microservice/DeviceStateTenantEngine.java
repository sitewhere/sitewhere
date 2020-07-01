/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.microservice;

import com.sitewhere.devicestate.configuration.DeviceStateTenantConfiguration;
import com.sitewhere.devicestate.configuration.DeviceStateTenantEngineModule;
import com.sitewhere.devicestate.grpc.DeviceStateImpl;
import com.sitewhere.devicestate.kafka.DeviceStatePipeline;
import com.sitewhere.devicestate.persistence.rdb.entity.RdbDeviceState;
import com.sitewhere.devicestate.persistence.rdb.entity.RdbRecentStateEvent;
import com.sitewhere.devicestate.spi.IDevicePresenceManager;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.grpc.service.DeviceStateGrpc;
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.microservice.datastore.DatastoreDefinition;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.rdb.RdbPersistenceOptions;
import com.sitewhere.rdb.RdbTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements device
 * state management functionality.
 */
public class DeviceStateTenantEngine extends RdbTenantEngine<DeviceStateTenantConfiguration>
	implements IDeviceStateTenantEngine {

    /** Device state management persistence API */
    private IDeviceStateManagement deviceStateManagement;

    /** Responds to device state GRPC requests */
    private DeviceStateGrpc.DeviceStateImplBase deviceStateImpl;

    /** Kafka Streams pipeline for device state event processing */
    private DeviceStatePipeline deviceStatePipeline;

    /** Presence manager implementation */
    private IDevicePresenceManager devicePresenceManager;

    public DeviceStateTenantEngine(SiteWhereTenantEngine engine) {
	super(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationClass()
     */
    @Override
    public Class<DeviceStateTenantConfiguration> getConfigurationClass() {
	return DeviceStateTenantConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * createConfigurationModule()
     */
    @Override
    public ITenantEngineModule<DeviceStateTenantConfiguration> createConfigurationModule() {
	return new DeviceStateTenantEngineModule(getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.rdb.spi.IRdbTenantEngine#getDatastoreDefinition()
     */
    @Override
    public DatastoreDefinition getDatastoreDefinition() {
	return getActiveConfiguration().getDatastore();
    }

    /*
     * @see com.sitewhere.rdb.spi.IRdbTenantEngine#getEntityClasses()
     */
    @Override
    public Class<?>[] getEntityClasses() {
	return new Class<?>[] { RdbDeviceState.class, RdbRecentStateEvent.class };
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * loadEngineComponents()
     */
    @Override
    public void loadEngineComponents() throws SiteWhereException {
	// Create management interfaces.
	IDeviceStateManagement implementation = getInjector().getInstance(IDeviceStateManagement.class);
	this.deviceStateManagement = implementation;
	this.deviceStateImpl = new DeviceStateImpl((IDeviceStateMicroservice) getMicroservice(),
		getDeviceStateManagement());
    }

    /*
     * @see com.sitewhere.rdb.RdbTenantEngine#getPersistenceOptions()
     */
    @Override
    public RdbPersistenceOptions getPersistenceOptions() {
	RdbPersistenceOptions options = new RdbPersistenceOptions();
	// options.setHbmToDdlAuto("update");
	return options;
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * setDatasetBootstrapBindings(com.sitewhere.microservice.scripting.Binding)
     */
    @Override
    public void setDatasetBootstrapBindings(Binding binding) throws SiteWhereException {
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.tenantInitialize(monitor);
	this.deviceStatePipeline = new DeviceStatePipeline(getActiveConfiguration());

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize device state management persistence.
	init.addInitializeStep(this, getDeviceStateManagement(), true);

	// Initialize device state pipeline.
	init.addInitializeStep(this, getDeviceStatePipeline(), true);

	// Initialize device presence manager.
	init.addInitializeStep(this, getDevicePresenceManager(), true);

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
	super.tenantStart(monitor);

	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start device state management persistence.
	start.addStartStep(this, getDeviceStateManagement(), true);

	// Start device state pipeline.
	start.addStartStep(this, getDeviceStatePipeline(), true);

	// Start device presence manager.
	start.addStartStep(this, getDevicePresenceManager(), true);

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
	super.tenantStop(monitor);

	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop device presence manager.
	stop.addStopStep(this, getDevicePresenceManager());

	// Stop device state pipeline.
	stop.addStopStep(this, getDeviceStatePipeline());

	// Stop device state management persistence.
	stop.addStopStep(this, getDeviceStateManagement());

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

    /*
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine#
     * getDeviceStateImpl()
     */
    @Override
    public DeviceStateGrpc.DeviceStateImplBase getDeviceStateImpl() {
	return deviceStateImpl;
    }

    /*
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine#
     * getDevicePresenceManager()
     */
    @Override
    public IDevicePresenceManager getDevicePresenceManager() {
	return devicePresenceManager;
    }

    public DeviceStatePipeline getDeviceStatePipeline() {
	return deviceStatePipeline;
    }
}