/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.microservice;

import com.sitewhere.inbound.kafka.DecodedEventsConsumer;
import com.sitewhere.inbound.kafka.EnrichedCommandInvocationsProducer;
import com.sitewhere.inbound.kafka.EnrichedEventsProducer;
import com.sitewhere.inbound.kafka.PersistedEventsConsumer;
import com.sitewhere.inbound.kafka.UnregisteredEventsProducer;
import com.sitewhere.inbound.spi.kafka.IDecodedEventsConsumer;
import com.sitewhere.inbound.spi.kafka.IEnrichedCommandInvocationsProducer;
import com.sitewhere.inbound.spi.kafka.IEnrichedEventsProducer;
import com.sitewhere.inbound.spi.kafka.IPersistedEventsConsumer;
import com.sitewhere.inbound.spi.kafka.IUnregisteredEventsProducer;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.inbound.spi.processing.IInboundProcessingConfiguration;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.spring.InboundProcessingBeans;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements inbound
 * event processing functionality.
 * 
 * @author Derek
 */
public class InboundProcessingTenantEngine extends MicroserviceTenantEngine implements IInboundProcessingTenantEngine {

    /** Kafka consumer that received inbound decoded events */
    private IDecodedEventsConsumer decodedEventsConsumer;

    /** Kafka producer for events sent to unregistered devices */
    private IUnregisteredEventsProducer unregisteredDeviceEventsProducer;

    /** Kafka consumer for events persisted via event management APIs */
    private IPersistedEventsConsumer persistedEventsConsumer;

    /** Kafka producer for forwarding enriched events */
    private IEnrichedEventsProducer enrichedEventsProducer;

    /** Kafka producer for forwarding enriched command invocations */
    private IEnrichedCommandInvocationsProducer enrichedCommandInvocationsProducer;

    public InboundProcessingTenantEngine(ITenant tenant) {
	super(tenant);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Load core configuration parameters.
	IInboundProcessingConfiguration configuration = (IInboundProcessingConfiguration) getModuleContext()
		.getBean(InboundProcessingBeans.BEAN_INBOUND_PROCESSING_CONFIGURATION);

	this.decodedEventsConsumer = new DecodedEventsConsumer(configuration);
	this.unregisteredDeviceEventsProducer = new UnregisteredEventsProducer();
	this.persistedEventsConsumer = new PersistedEventsConsumer();
	this.enrichedEventsProducer = new EnrichedEventsProducer();
	this.enrichedCommandInvocationsProducer = new EnrichedCommandInvocationsProducer();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize decoded events consumer.
	init.addInitializeStep(this, getDecodedEventsConsumer(), true);

	// Initialize unregistered device events producer.
	init.addInitializeStep(this, getUnregisteredDeviceEventsProducer(), true);

	// Initialize persisted events consumer.
	init.addInitializeStep(this, getPersistedEventsConsumer(), true);

	// Initialize enriched events producer.
	init.addInitializeStep(this, getEnrichedEventsProducer(), true);

	// Initialize enriched command invocations producer.
	init.addInitializeStep(this, getEnrichedCommandInvocationsProducer(), true);

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

	// Start unregistered device events producer.
	start.addStartStep(this, getUnregisteredDeviceEventsProducer(), true);

	// Start decoded events consumer.
	start.addStartStep(this, getDecodedEventsConsumer(), true);

	// Start persisted events consumer.
	start.addStartStep(this, getPersistedEventsConsumer(), true);

	// Start enriched events producer.
	start.addStartStep(this, getEnrichedEventsProducer(), true);

	// Start enriched command invocations producer.
	start.addStartStep(this, getEnrichedCommandInvocationsProducer(), true);

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

	// Stop decoded events consumer.
	stop.addStopStep(this, getDecodedEventsConsumer());

	// Stop unregistered device events producer.
	stop.addStopStep(this, getUnregisteredDeviceEventsProducer());

	// Stop persisted events consumer.
	stop.addStopStep(this, getPersistedEventsConsumer());

	// Stop enriched events producer.
	stop.addStopStep(this, getEnrichedEventsProducer());

	// Stop enriched command invocations producer.
	stop.addStopStep(this, getEnrichedCommandInvocationsProducer());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine#
     * getDecodedEventsConsumer()
     */
    @Override
    public IDecodedEventsConsumer getDecodedEventsConsumer() {
	return decodedEventsConsumer;
    }

    public void setDecodedEventsConsumer(IDecodedEventsConsumer decodedEventsConsumer) {
	this.decodedEventsConsumer = decodedEventsConsumer;
    }

    /*
     * @see com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine#
     * getUnregisteredDeviceEventsProducer()
     */
    @Override
    public IUnregisteredEventsProducer getUnregisteredDeviceEventsProducer() {
	return unregisteredDeviceEventsProducer;
    }

    public void setUnregisteredDeviceEventsProducer(IUnregisteredEventsProducer unregisteredDeviceEventsProducer) {
	this.unregisteredDeviceEventsProducer = unregisteredDeviceEventsProducer;
    }

    /*
     * @see com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine#
     * getPersistedEventsConsumer()
     */
    @Override
    public IPersistedEventsConsumer getPersistedEventsConsumer() {
	return persistedEventsConsumer;
    }

    public void setPersistedEventsConsumer(IPersistedEventsConsumer persistedEventsConsumer) {
	this.persistedEventsConsumer = persistedEventsConsumer;
    }

    /*
     * @see com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine#
     * getEnrichedEventsProducer()
     */
    @Override
    public IEnrichedEventsProducer getEnrichedEventsProducer() {
	return enrichedEventsProducer;
    }

    public void setEnrichedEventsProducer(IEnrichedEventsProducer enrichedEventsProducer) {
	this.enrichedEventsProducer = enrichedEventsProducer;
    }

    /*
     * @see com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine#
     * getEnrichedCommandInvocationsProducer()
     */
    @Override
    public IEnrichedCommandInvocationsProducer getEnrichedCommandInvocationsProducer() {
	return enrichedCommandInvocationsProducer;
    }

    public void setEnrichedCommandInvocationsProducer(
	    IEnrichedCommandInvocationsProducer enrichedCommandInvocationsProducer) {
	this.enrichedCommandInvocationsProducer = enrichedCommandInvocationsProducer;
    }
}