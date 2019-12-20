/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.microservice;

import com.sitewhere.inbound.configuration.InboundProcessingTenantConfiguration;
import com.sitewhere.inbound.configuration.InboundProcessingTenantEngineModule;
import com.sitewhere.inbound.spi.kafka.IDecodedEventsConsumer;
import com.sitewhere.inbound.spi.kafka.IInboundEventsProducer;
import com.sitewhere.inbound.spi.kafka.IUnregisteredEventsProducer;
import com.sitewhere.inbound.spi.microservice.IInboundProcessingTenantEngine;
import com.sitewhere.microservice.lifecycle.CompositeLifecycleStep;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineModule;

import io.sitewhere.k8s.crd.tenant.engine.SiteWhereTenantEngine;
import io.sitewhere.k8s.crd.tenant.engine.dataset.TenantEngineDatasetTemplate;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements inbound
 * event processing functionality.
 */
public class InboundProcessingTenantEngine extends MicroserviceTenantEngine<InboundProcessingTenantConfiguration>
	implements IInboundProcessingTenantEngine {

    /** Kafka consumer that received inbound decoded events */
    private IDecodedEventsConsumer decodedEventsConsumer;

    /** Kafka producer for events sent to unregistered devices */
    private IUnregisteredEventsProducer unregisteredDeviceEventsProducer;

    /** Kafka producer for forwarding processed events */
    private IInboundEventsProducer inboundEventsProducer;

    public InboundProcessingTenantEngine(SiteWhereTenantEngine engine) {
	super(engine);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * getConfigurationClass()
     */
    @Override
    public Class<InboundProcessingTenantConfiguration> getConfigurationClass() {
	return InboundProcessingTenantConfiguration.class;
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * createConfigurationModule()
     */
    @Override
    public ITenantEngineModule<InboundProcessingTenantConfiguration> createConfigurationModule() {
	return new InboundProcessingTenantEngineModule(getActiveConfiguration());
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.microservice.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Load core configuration parameters.
	// IInboundProcessingConfiguration configuration =
	// (IInboundProcessingConfiguration) getModuleContext()
	// .getBean(InboundProcessingBeans.BEAN_INBOUND_PROCESSING_CONFIGURATION);

	// this.decodedEventsConsumer = new DecodedEventsConsumer(configuration);
	// this.unregisteredDeviceEventsProducer = new UnregisteredEventsProducer();
	// this.inboundEventsProducer = new InboundEventsProducer();

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize decoded events consumer.
	init.addInitializeStep(this, getDecodedEventsConsumer(), true);

	// Initialize unregistered device events producer.
	init.addInitializeStep(this, getUnregisteredDeviceEventsProducer(), true);

	// Initialize inbound events producer.
	init.addInitializeStep(this, getInboundEventsProducer(), true);

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

	// Start unregistered device events producer.
	start.addStartStep(this, getUnregisteredDeviceEventsProducer(), true);

	// Start decoded events consumer.
	start.addStartStep(this, getDecodedEventsConsumer(), true);

	// Start inbound events producer.
	start.addStartStep(this, getInboundEventsProducer(), true);

	// Execute startup steps.
	start.execute(monitor);
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

	// Stop decoded events consumer.
	stop.addStopStep(this, getDecodedEventsConsumer());

	// Stop unregistered device events producer.
	stop.addStopStep(this, getUnregisteredDeviceEventsProducer());

	// Stop inbound events producer.
	stop.addStopStep(this, getInboundEventsProducer());

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
     * getInboundEventsProducer()
     */
    @Override
    public IInboundEventsProducer getInboundEventsProducer() {
	return inboundEventsProducer;
    }

    public void setInboundEventsProducer(IInboundEventsProducer inboundEventsProducer) {
	this.inboundEventsProducer = inboundEventsProducer;
    }
}