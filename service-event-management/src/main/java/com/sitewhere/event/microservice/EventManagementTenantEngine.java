/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.microservice;

import java.util.Collections;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.event.initializer.GroovyEventModelInitializer;
import com.sitewhere.event.spi.kafka.IInboundEventsConsumer;
import com.sitewhere.event.spi.kafka.IOutboundCommandInvocationsProducer;
import com.sitewhere.event.spi.kafka.IOutboundEventsProducer;
import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.microservice.groovy.GroovyConfiguration;
import com.sitewhere.microservice.grpc.EventManagementImpl;
import com.sitewhere.microservice.kafka.KafkaEventPersistenceTriggers;
import com.sitewhere.microservice.kafka.OutboundEventsProducer;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.server.lifecycle.LifecycleProgressContext;
import com.sitewhere.server.lifecycle.LifecycleProgressMonitor;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.spring.EventManagementBeans;
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

    /** Kafka consumer for decoded, pre-processed inbound events */
    private IInboundEventsConsumer inboundEventsConsumer;

    /** Kafka producer for pushing persisted events to a topic */
    private IOutboundEventsProducer outboundEventsProducer;

    /** Kakfa producer for pushed persistend command invocations to a topic */
    private IOutboundCommandInvocationsProducer outboundCommandInvocationsProducer;

    public EventManagementTenantEngine(ITenant tenant) {
	super(tenant);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Load event management implementation.
	initializeManagementImplementations();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize discoverable lifecycle components.
	init.addStep(initializeDiscoverableBeans(getModuleContext()));

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
	IDeviceEventManagement impl = (IDeviceEventManagement) getModuleContext()
		.getBean(EventManagementBeans.BEAN_EVENT_MANAGEMENT);
	this.eventManagement = new KafkaEventPersistenceTriggers(this, impl);

	this.eventManagementImpl = new EventManagementImpl((IEventManagementMicroservice) getMicroservice(),
		getEventManagement());
	this.outboundEventsProducer = new OutboundEventsProducer();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantStart(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start discoverable lifecycle components.
	start.addStep(startDiscoverableBeans(getModuleContext()));

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
     * tenantBootstrap(com.sitewhere.spi.microservice.multitenant.IDatasetTemplate,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(IDatasetTemplate template, ILifecycleProgressMonitor monitor)
	    throws SiteWhereException {
	List<String> scripts = Collections.emptyList();
	if (template.getInitializers() != null) {
	    scripts = template.getInitializers().getEventManagement();
	    for (String script : scripts) {
		getTenantScriptSynchronizer().add(script);
	    }
	}

	// Execute remote calls as superuser.
	Authentication previous = SecurityContextHolder.getContext().getAuthentication();
	try {
	    SecurityContextHolder.getContext()
		    .setAuthentication(getMicroservice().getSystemUser().getAuthenticationForTenant(getTenant()));
	    GroovyConfiguration groovy = new GroovyConfiguration(getTenantScriptSynchronizer());
	    groovy.start(new LifecycleProgressMonitor(new LifecycleProgressContext(1, "Initialize event model."),
		    getMicroservice()));
	    for (String script : scripts) {
		GroovyEventModelInitializer initializer = new GroovyEventModelInitializer(groovy, script);
		initializer.initialize(getCachedDeviceManagement(), getEventManagement());
	    }
	} finally {
	    SecurityContextHolder.getContext().setAuthentication(previous);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
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

	// Stop discoverable lifecycle components.
	stop.addStep(stopDiscoverableBeans(getModuleContext()));

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

    protected IDeviceManagement getCachedDeviceManagement() {
	return ((IEventManagementMicroservice) getMicroservice()).getCachedDeviceManagement();
    }
}