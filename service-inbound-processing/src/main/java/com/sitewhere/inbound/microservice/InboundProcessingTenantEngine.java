/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.inbound.kafka.DecodedEventsConsumer;
import com.sitewhere.inbound.kafka.UnregisteredDeviceEventsProducer;
import com.sitewhere.inbound.processing.RegistrationVerificationProcessor;
import com.sitewhere.inbound.spi.kafka.IDecodedEventsConsumer;
import com.sitewhere.inbound.spi.kafka.IUnregisteredDeviceEventsProducer;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingMicroservice;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.inbound.spi.processing.IRegistrationVerificationProcessor;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
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

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Kafka consumer that received inbound decoded events */
    private IDecodedEventsConsumer decodedEventsConsumer;

    /** Processor that verifies an event belongs to a registered device */
    private IRegistrationVerificationProcessor registrationVerificationProcessor;

    /** Kafka producer for events sent to unregistered devices */
    private IUnregisteredDeviceEventsProducer unregisteredDeviceEventsProducer;

    public InboundProcessingTenantEngine(IMultitenantMicroservice<?> microservice, ITenant tenant) {
	super(microservice, tenant);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	IInboundProcessingMicroservice ipMicroservice = (IInboundProcessingMicroservice) getMicroservice();

	this.decodedEventsConsumer = new DecodedEventsConsumer(ipMicroservice, this);
	this.registrationVerificationProcessor = new RegistrationVerificationProcessor(this);
	this.unregisteredDeviceEventsProducer = new UnregisteredDeviceEventsProducer(ipMicroservice);

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize decoded events consumer.
	init.addInitializeStep(this, getDecodedEventsConsumer(), true);

	// Initialize registration verification processor.
	init.addInitializeStep(this, getRegistrationVerificationProcessor(), true);

	// Initialize unregistered device events producer.
	init.addInitializeStep(this, getUnregisteredDeviceEventsProducer(), true);

	// Execute initialization steps.
	init.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStart(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStart(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will start components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Start " + getComponentName());

	// Start unregistered device events producer.
	start.addStartStep(this, getUnregisteredDeviceEventsProducer(), true);

	// Start registration verification processor.
	start.addStartStep(this, getRegistrationVerificationProcessor(), true);

	// Start decoded events consumer.
	start.addStartStep(this, getDecodedEventsConsumer(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantBootstrap(com.sitewhere.spi.microservice.multitenant.
     * ITenantTemplate,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(ITenantTemplate template, ILifecycleProgressMonitor monitor) throws SiteWhereException {
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantStop(com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantStop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Create step that will stop components.
	ICompositeLifecycleStep start = new CompositeLifecycleStep("Stop " + getComponentName());

	// Stop decoded events consumer.
	start.addStopStep(this, getDecodedEventsConsumer());

	// Stop registration verification processor.
	start.addStopStep(this, getRegistrationVerificationProcessor());

	// Stop unregistered device events producer.
	start.addStopStep(this, getUnregisteredDeviceEventsProducer());

	// Execute shutdown steps.
	start.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine#
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
     * @see
     * com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine#
     * getRegistrationVerificationProcessor()
     */
    @Override
    public IRegistrationVerificationProcessor getRegistrationVerificationProcessor() {
	return registrationVerificationProcessor;
    }

    public void setRegistrationVerificationProcessor(
	    IRegistrationVerificationProcessor registrationVerificationProcessor) {
	this.registrationVerificationProcessor = registrationVerificationProcessor;
    }

    /*
     * @see
     * com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine#
     * getUnregisteredDeviceEventsProducer()
     */
    @Override
    public IUnregisteredDeviceEventsProducer getUnregisteredDeviceEventsProducer() {
	return unregisteredDeviceEventsProducer;
    }

    public void setUnregisteredDeviceEventsProducer(
	    IUnregisteredDeviceEventsProducer unregisteredDeviceEventsProducer) {
	this.unregisteredDeviceEventsProducer = unregisteredDeviceEventsProducer;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}