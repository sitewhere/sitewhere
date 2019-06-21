/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.microservice;

import com.sitewhere.commands.DefaultCommandProcessingStrategy;
import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.commands.spi.ICommandProcessingStrategy;
import com.sitewhere.commands.spi.IOutboundCommandRouter;
import com.sitewhere.commands.spi.kafka.IEnrichedCommandInvocationsConsumer;
import com.sitewhere.commands.spi.kafka.IUndeliveredCommandInvocationsProducer;
import com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine;
import com.sitewhere.microservice.kafka.EnrichedCommandInvocationsConsumer;
import com.sitewhere.microservice.kafka.UndeliveredCommandInvocationsProducer;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.spring.CommandDestinationsBeans;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements command
 * delivery functionality.
 * 
 * @author Derek
 */
public class CommandDeliveryTenantEngine extends MicroserviceTenantEngine implements ICommandDeliveryTenantEngine {

    /** Configured command processing strategy */
    private ICommandProcessingStrategy commandProcessingStrategy = new DefaultCommandProcessingStrategy();

    /** Configured outbound command router */
    private IOutboundCommandRouter outboundCommandRouter;

    /** Command destinations manager */
    private ICommandDestinationsManager commandDestinationsManager;

    /** Kafka consumer for enriched command invocations */
    private IEnrichedCommandInvocationsConsumer enrichedCommandInvocationsConsumer;

    /** Kafka producer for undelivered command invocations */
    private IUndeliveredCommandInvocationsProducer undeliveredCommandInvocationsProducer;

    public CommandDeliveryTenantEngine(ITenant tenant) {
	super(tenant);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	// Listener for enriched command invocations.
	this.enrichedCommandInvocationsConsumer = new EnrichedCommandInvocationsConsumer();

	// Listener for enriched command invocations.
	this.undeliveredCommandInvocationsProducer = new UndeliveredCommandInvocationsProducer();

	// Load configured command destinations manager.
	this.commandDestinationsManager = (ICommandDestinationsManager) getModuleContext()
		.getBean(CommandDestinationsBeans.BEAN_COMMAND_DESTINATIONS_MANAGER);

	// Load configured command router.
	this.outboundCommandRouter = (IOutboundCommandRouter) getModuleContext()
		.getBean(CommandDestinationsBeans.BEAN_COMMAND_ROUTER);

	// Create step that will initialize components.
	ICompositeLifecycleStep init = new CompositeLifecycleStep("Initialize " + getComponentName());

	// Initialize command destinations manager.
	init.addInitializeStep(this, getCommandDestinationsManager(), true);

	// Initialize outbound command router.
	init.addInitializeStep(this, getOutboundCommandRouter(), true);

	// Initialize command processing strategy.
	init.addInitializeStep(this, getCommandProcessingStrategy(), true);

	// Initialize undelivered command invocations producer.
	init.addInitializeStep(this, getUndeliveredCommandInvocationsProducer(), true);

	// Initialize enriched command invocations consumer.
	init.addInitializeStep(this, getEnrichedCommandInvocationsConsumer(), true);

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

	// Start command destinations manager.
	start.addStartStep(this, getCommandDestinationsManager(), true);

	// Start outbound command router.
	start.addStartStep(this, getOutboundCommandRouter(), true);

	// Start command processing strategy.
	start.addStartStep(this, getCommandProcessingStrategy(), true);

	// Start undelivered command invocations producer.
	start.addStartStep(this, getUndeliveredCommandInvocationsProducer(), true);

	// Start command invocations consumer.
	start.addStartStep(this, getEnrichedCommandInvocationsConsumer(), true);

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

	// Stop command invocations consumer.
	stop.addStopStep(this, getEnrichedCommandInvocationsConsumer());

	// Stop undelivered command invocations producer.
	stop.addStopStep(this, getUndeliveredCommandInvocationsProducer());

	// Stop outbound command router.
	stop.addStopStep(this, getOutboundCommandRouter());

	// Stop command processing strategy.
	stop.addStopStep(this, getCommandProcessingStrategy());

	// Stop command destinations manager.
	stop.addStopStep(this, getCommandDestinationsManager());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine#
     * getCommandProcessingStrategy()
     */
    @Override
    public ICommandProcessingStrategy getCommandProcessingStrategy() {
	return commandProcessingStrategy;
    }

    public void setCommandProcessingStrategy(ICommandProcessingStrategy commandProcessingStrategy) {
	this.commandProcessingStrategy = commandProcessingStrategy;
    }

    /*
     * @see com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine#
     * getOutboundCommandRouter()
     */
    @Override
    public IOutboundCommandRouter getOutboundCommandRouter() {
	return outboundCommandRouter;
    }

    public void setOutboundCommandRouter(IOutboundCommandRouter outboundCommandRouter) {
	this.outboundCommandRouter = outboundCommandRouter;
    }

    /*
     * @see com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine#
     * getCommandDestinationsManager()
     */
    @Override
    public ICommandDestinationsManager getCommandDestinationsManager() {
	return commandDestinationsManager;
    }

    public void setCommandDestinationsManager(ICommandDestinationsManager commandDestinationsManager) {
	this.commandDestinationsManager = commandDestinationsManager;
    }

    /*
     * @see com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine#
     * getEnrichedCommandInvocationsConsumer()
     */
    @Override
    public IEnrichedCommandInvocationsConsumer getEnrichedCommandInvocationsConsumer() {
	return enrichedCommandInvocationsConsumer;
    }

    public void setEnrichedCommandInvocationsConsumer(
	    IEnrichedCommandInvocationsConsumer enrichedCommandInvocationsConsumer) {
	this.enrichedCommandInvocationsConsumer = enrichedCommandInvocationsConsumer;
    }

    /*
     * @see com.sitewhere.commands.spi.microservice.ICommandDeliveryTenantEngine#
     * getUndeliveredCommandInvocationsProducer()
     */
    @Override
    public IUndeliveredCommandInvocationsProducer getUndeliveredCommandInvocationsProducer() {
	return undeliveredCommandInvocationsProducer;
    }

    public void setUndeliveredCommandInvocationsProducer(
	    IUndeliveredCommandInvocationsProducer undeliveredCommandInvocationsProducer) {
	this.undeliveredCommandInvocationsProducer = undeliveredCommandInvocationsProducer;
    }
}