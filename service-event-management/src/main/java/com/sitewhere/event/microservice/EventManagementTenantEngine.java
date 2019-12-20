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
import com.sitewhere.event.kafka.OutboundCommandInvocationsProducer;
import com.sitewhere.event.kafka.OutboundEventsProducer;
import com.sitewhere.event.spi.kafka.IInboundEventsConsumer;
import com.sitewhere.event.spi.kafka.IOutboundCommandInvocationsProducer;
import com.sitewhere.event.spi.kafka.IOutboundEventsProducer;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.dataset.TenantEngineDatasetTemplate;

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

    /** Kafka consumer for decoded, pre-processed inbound events */
    private IInboundEventsConsumer inboundEventsConsumer;

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
	return new EventManagementTenantEngineModule(getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Load event management implementation.
	initializeManagementImplementations();

	// Initialize Kafka components.
	initializeKafkaComponents();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize event management persistence.
	init.addInitializeStep(this, getEventManagement(), true);

	// Initialize outbound events producer.
	init.addInitializeStep(this, getOutboundEventsProducer(), true);

	// Initialize outbound command invocations producer.
	init.addInitializeStep(this, getOutboundCommandInvocationsProducer(), true);

	// Initialize inbound events consumer.
	init.addInitializeStep(this, getInboundEventsConsumer(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /**
     * Initialize event management implementations based on configured model Spring
     * context.
     * 
     * @throws SiteWhereException
     */
    protected void initializeManagementImplementations() throws SiteWhereException {
	// IDeviceEventManagement impl = (IDeviceEventManagement) getModuleContext()
	// .getBean(EventManagementBeans.BEAN_EVENT_MANAGEMENT);
	// this.eventManagement = new KafkaEventPersistenceTriggers(this, impl);

	this.eventManagementImpl = new EventManagementImpl((IEventManagementMicroservice) getMicroservice(),
		getEventManagement());
    }

    /**
     * Initialize Kafka components.
     * 
     * @throws SiteWhereException
     */
    protected void initializeKafkaComponents() throws SiteWhereException {
	this.outboundEventsProducer = new OutboundEventsProducer();
	this.outboundCommandInvocationsProducer = new OutboundCommandInvocationsProducer();
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

	// Start inbound events consumer.
	start.addStartStep(this, getInboundEventsConsumer(), true);

	// Execute startup steps.
	start.execute(monitor);
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
     * tenantBootstrap(io.sitewhere.k8s.crd.tenant.engine.dataset.
     * TenantEngineDatasetTemplate,
     * com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(TenantEngineDatasetTemplate template, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	// String scriptName = String.format("%s.groovy",
	// template.getMetadata().getName());
	// Path path = getScriptSynchronizer().add(getScriptContext(),
	// ScriptType.Initializer, scriptName,
	// template.getSpec().getConfiguration().getBytes());
	//
	// Execute remote calls as superuser.
	// Authentication previous =
	// SecurityContextHolder.getContext().getAuthentication();
	// try {
	// SecurityContextHolder.getContext()
	// .setAuthentication(getMicroservice().getSystemUser().getAuthenticationForTenant(getTenant()));
	//
	// getLogger().info(String.format("Applying bootstrap script '%s'.", path));
	// GroovyEventModelInitializer initializer = new
	// GroovyEventModelInitializer(getGroovyConfiguration(), path);
	// initializer.initialize(getCachedDeviceManagement(), getEventManagement());
	// } catch (Throwable e) {
	// getLogger().error("Unhandled exception in bootstrap script.", e);
	// throw new SiteWhereException(e);
	// } finally {
	// SecurityContextHolder.getContext().setAuthentication(previous);
	// }
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

	// Stop inbound events consumer.
	stop.addStopStep(this, getInboundEventsConsumer());

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

    /*
     * @see com.sitewhere.event.spi.microservice.IEventManagementTenantEngine#
     * getInboundEventsConsumer()
     */
    @Override
    public IInboundEventsConsumer getInboundEventsConsumer() {
	return inboundEventsConsumer;
    }

    public void setInboundEventsConsumer(IInboundEventsConsumer inboundEventsConsumer) {
	this.inboundEventsConsumer = inboundEventsConsumer;
    }

    /*
     * @see com.sitewhere.event.spi.microservice.IEventManagementTenantEngine#
     * getOutboundEventsProducer()
     */
    @Override
    public IOutboundEventsProducer getOutboundEventsProducer() {
	return outboundEventsProducer;
    }

    public void setOutboundEventsProducer(IOutboundEventsProducer outboundEventsProducer) {
	this.outboundEventsProducer = outboundEventsProducer;
    }

    /*
     * @see com.sitewhere.event.spi.microservice.IEventManagementTenantEngine#
     * getOutboundCommandInvocationsProducer()
     */
    @Override
    public IOutboundCommandInvocationsProducer getOutboundCommandInvocationsProducer() {
	return outboundCommandInvocationsProducer;
    }

    public void setOutboundCommandInvocationsProducer(
	    IOutboundCommandInvocationsProducer outboundCommandInvocationsProducer) {
	this.outboundCommandInvocationsProducer = outboundCommandInvocationsProducer;
    }

    protected IDeviceManagement getDeviceManagement() {
	return ((IEventManagementMicroservice) getMicroservice()).getDeviceManagementApiChannel();
    }
}