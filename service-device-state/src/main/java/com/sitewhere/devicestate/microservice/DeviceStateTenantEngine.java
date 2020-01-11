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
import com.sitewhere.devicestate.persistence.rdb.entity.RdbDeviceState;
import com.sitewhere.devicestate.spi.IDevicePresenceManager;
import com.sitewhere.devicestate.spi.kafka.IDeviceStateEnrichedEventsConsumer;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.grpc.service.DeviceStateGrpc;
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.rdb.RdbPersistenceOptions;
import com.sitewhere.rdb.RdbProviderInformation;
import com.sitewhere.rdb.RdbTenantEngine;
import com.sitewhere.rdb.providers.postgresql.Postgres95Provider;
import com.sitewhere.rdb.providers.postgresql.PostgresConnectionInfo;
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

    /** Kafka consumer for processing enriched events for device state */
    private IDeviceStateEnrichedEventsConsumer deviceStateEnrichedEventsConsumer;

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
     * @see com.sitewhere.rdb.spi.IRdbTenantEngine#getProviderInformation()
     */
    @Override
    public RdbProviderInformation<?> getProviderInformation() {
	PostgresConnectionInfo connInfo = new PostgresConnectionInfo();
	connInfo.setHostname("sitewhere-postgresql");
	connInfo.setPort(5000);
	connInfo.setUsername("syncope");
	connInfo.setPassword("syncope");
	return new Postgres95Provider(connInfo);
    }

    /*
     * @see com.sitewhere.rdb.spi.IRdbTenantEngine#getEntityClasses()
     */
    @Override
    public Class<?>[] getEntityClasses() {
	return new Class<?>[] { RdbDeviceState.class };
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
	// // Create enriched events consumer for building device state.
	// this.deviceStateEnrichedEventsConsumer = new
	// DeviceStateEnrichedEventsConsumer();
	//
	// // Create presence manager.
	// this.devicePresenceManager = (IDevicePresenceManager) getModuleContext()
	// .getBean(DeviceStateManagementBeans.BEAN_PRESENCE_MANAGER);

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize device state management persistence.
	init.addInitializeStep(this, getDeviceStateManagement(), true);

	// Initialize device state enriched events consumer.
	init.addInitializeStep(this, getDeviceStateEnrichedEventsConsumer(), true);

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
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start device state management persistence.
	start.addStartStep(this, getDeviceStateManagement(), true);

	// Start device state enriched events consumer.
	start.addStartStep(this, getDeviceStateEnrichedEventsConsumer(), true);

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
	// Create step that will stop components.
	ICompositeLifecycleStep stop = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop device presence manager.
	stop.addStopStep(this, getDevicePresenceManager());

	// Stop device state enriched events consumer.
	stop.addStopStep(this, getDeviceStateEnrichedEventsConsumer());

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

    protected void setDeviceStateManagement(IDeviceStateManagement deviceStateManagement) {
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

    protected void setDeviceStateImpl(DeviceStateGrpc.DeviceStateImplBase deviceStateImpl) {
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

    protected void setDeviceStateEnrichedEventsConsumer(
	    IDeviceStateEnrichedEventsConsumer deviceStateEnrichedEventsConsumer) {
	this.deviceStateEnrichedEventsConsumer = deviceStateEnrichedEventsConsumer;
    }

    /*
     * @see com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine#
     * getDevicePresenceManager()
     */
    @Override
    public IDevicePresenceManager getDevicePresenceManager() {
	return devicePresenceManager;
    }

    protected void setDevicePresenceManager(IDevicePresenceManager devicePresenceManager) {
	this.devicePresenceManager = devicePresenceManager;
    }
}