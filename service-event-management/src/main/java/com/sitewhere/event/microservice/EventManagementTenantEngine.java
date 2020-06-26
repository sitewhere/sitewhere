/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.microservice;

import com.sitewhere.event.configuration.EventManagementTenantConfiguration;
import com.sitewhere.event.configuration.EventManagementTenantEngineModule;
import com.sitewhere.event.grpc.EventManagementImpl;
import com.sitewhere.event.kafka.KafkaEventPersistenceTriggers;
import com.sitewhere.event.kafka.OutboundCommandInvocationsProducer;
import com.sitewhere.event.kafka.OutboundEventsProducer;
import com.sitewhere.event.kafka.PreprocessedEventsPipeline;
import com.sitewhere.event.spi.kafka.IOutboundCommandInvocationsProducer;
import com.sitewhere.event.spi.kafka.IOutboundEventsProducer;
import com.sitewhere.event.spi.kafka.IPreprocessedEventsPipeline;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.microservice.api.device.DeviceManagementRequestBuilder;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.DeviceEventRequestBuilder;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.microservice.scripting.Binding;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;
import com.sitewhere.spi.microservice.scripting.IScriptVariables;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements event
 * management functionality.
 */
public class EventManagementTenantEngine extends MicroserviceTenantEngine<EventManagementTenantConfiguration>
	implements IEventManagementTenantEngine {

    /** Event management persistence API */
    private IDeviceEventManagement eventManagement;

    /** Responds to event management GRPC requests */
    private DeviceEventManagementGrpc.DeviceEventManagementImplBase eventManagementImpl;

    /** Kafka Streams pipeline for decoded, pre-processed inbound events */
    private IPreprocessedEventsPipeline preprocessedEventsPipeline;

    /** Kafka producer for pushing persisted events to a topic */
    private IOutboundEventsProducer outboundEventsProducer;

    /** Kakfa producer for pushed persistend command invocations to a topic */
    private IOutboundCommandInvocationsProducer outboundCommandInvocationsProducer;

    public EventManagementTenantEngine(SiteWhereTenantEngine engine) {
	super(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationClass()
     */
    @Override
    public Class<EventManagementTenantConfiguration> getConfigurationClass() {
	return EventManagementTenantConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * createConfigurationModule()
     */
    @Override
    public ITenantEngineModule<EventManagementTenantConfiguration> createConfigurationModule() {
	return new EventManagementTenantEngineModule(this, getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * loadEngineComponents()
     */
    @Override
    public void loadEngineComponents() throws SiteWhereException {
	// Create API implementation and gRPC server.
	IDeviceEventManagement implementation = getInjector().getInstance(IDeviceEventManagement.class);
	this.eventManagement = new KafkaEventPersistenceTriggers(this, implementation);
	this.eventManagementImpl = new EventManagementImpl((IEventManagementMicroservice) getMicroservice(),
		getEventManagement());

	// Create Kafka components.
	this.preprocessedEventsPipeline = new PreprocessedEventsPipeline();
	this.outboundEventsProducer = new OutboundEventsProducer();
	this.outboundCommandInvocationsProducer = new OutboundCommandInvocationsProducer();
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * setDatasetBootstrapBindings(com.sitewhere.microservice.scripting.Binding)
     */
    @Override
    public void setDatasetBootstrapBindings(Binding binding) throws SiteWhereException {
	binding.setVariable(IScriptVariables.VAR_DEVICE_MANAGEMENT_BUILDER,
		new DeviceManagementRequestBuilder(getDeviceManagement()));
	binding.setVariable(IScriptVariables.VAR_EVENT_MANAGEMENT_BUILDER,
		new DeviceEventRequestBuilder(getDeviceManagement(), getEventManagement()));
    }

    /*
     * @see com.sitewhere.microservice.multitenant.MicroserviceTenantEngine#
     * getTenantBootstrapPrerequisites()
     */
    @Override
    public IFunctionIdentifier[] getTenantBootstrapPrerequisites() {
	return new IFunctionIdentifier[] { MicroserviceIdentifier.DeviceManagement };
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize event management persistence.
	init.addInitializeStep(this, getEventManagement(), true);

	// Initialize outbound events producer.
	init.addInitializeStep(this, getOutboundEventsProducer(), true);

	// Initialize outbound command invocations producer.
	init.addInitializeStep(this, getOutboundCommandInvocationsProducer(), true);

	// Initialize preprocessed events pipeline.
	init.addInitializeStep(this, getPreprocessedEventsPipeline(), true);

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

	// Start event management persistence.
	start.addStartStep(this, getEventManagement(), true);

	// Start outbound events producer.
	start.addStartStep(this, getOutboundEventsProducer(), true);

	// Start outbound command invocations producer.
	start.addStartStep(this, getOutboundCommandInvocationsProducer(), true);

	// Start preprocessed events pipeline.
	start.addStartStep(this, getPreprocessedEventsPipeline(), true);

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

	// Stop event management persistence.
	stop.addStopStep(this, getEventManagement());

	// Stop preprocessed events pipeline.
	stop.addStopStep(this, getPreprocessedEventsPipeline());

	// Stop outbound command invocations producer.
	stop.addStopStep(this, getOutboundCommandInvocationsProducer());

	// Stop outbound events producer.
	stop.addStopStep(this, getOutboundEventsProducer());

	// Execute shutdown steps.
	stop.execute(monitor);
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

    /*
     * @see com.sitewhere.event.spi.microservice.IEventManagementTenantEngine#
     * getPreprocessedEventsPipeline()
     */
    @Override
    public IPreprocessedEventsPipeline getPreprocessedEventsPipeline() {
	return preprocessedEventsPipeline;
    }

    /*
     * @see com.sitewhere.event.spi.microservice.IEventManagementTenantEngine#
     * getOutboundEventsProducer()
     */
    @Override
    public IOutboundEventsProducer getOutboundEventsProducer() {
	return outboundEventsProducer;
    }

    /*
     * @see com.sitewhere.event.spi.microservice.IEventManagementTenantEngine#
     * getOutboundCommandInvocationsProducer()
     */
    @Override
    public IOutboundCommandInvocationsProducer getOutboundCommandInvocationsProducer() {
	return outboundCommandInvocationsProducer;
    }

    protected IDeviceManagement getDeviceManagement() {
	return ((IEventManagementMicroservice) getMicroservice()).getDeviceManagement();
    }
}