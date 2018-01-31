/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.event.grpc.EventManagementImpl;
import com.sitewhere.event.kafka.InboundPersistedEventsProducer;
import com.sitewhere.event.kafka.KafkaEventPersistenceTriggers;
import com.sitewhere.event.spi.kafka.IInboundPersistedEventsProducer;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
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

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Event management persistence API */
    private IDeviceEventManagement eventManagement;

    /** Responds to event management GRPC requests */
    private DeviceEventManagementGrpc.DeviceEventManagementImplBase eventManagementImpl;

    /** Kafka producer for pushing persisted events to a topic */
    private IInboundPersistedEventsProducer inboundPersistedEventsProducer;

    public EventManagementTenantEngine(IMultitenantMicroservice<IEventManagementTenantEngine> microservice,
	    ITenant tenant) {
	super(microservice, tenant);
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

	// Initialize inbound persisted events producer.
	init.addInitializeStep(this, getInboundPersistedEventsProducer(), true);

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

	this.eventManagementImpl = new EventManagementImpl(getEventManagement());
	this.inboundPersistedEventsProducer = new InboundPersistedEventsProducer(getMicroservice());
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

	// Start inbound persisted events producer.
	start.addStartStep(this, getInboundPersistedEventsProducer(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine#
     * tenantBootstrap(com.sitewhere.microservice.spi.multitenant. ITenantTemplate,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(ITenantTemplate template, ILifecycleProgressMonitor monitor) throws SiteWhereException {
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

	// Stop inbound persisted events producer.
	stop.addStopStep(this, getInboundPersistedEventsProducer());

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
     * getInboundPersistedEventsProducer()
     */
    @Override
    public IInboundPersistedEventsProducer getInboundPersistedEventsProducer() {
	return inboundPersistedEventsProducer;
    }

    public void setInboundPersistedEventsProducer(IInboundPersistedEventsProducer inboundPersistedEventsProducer) {
	this.inboundPersistedEventsProducer = inboundPersistedEventsProducer;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}