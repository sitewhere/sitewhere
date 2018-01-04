/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.destinations.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.destinations.kafka.EnrichedCommandInvocationsConsumer;
import com.sitewhere.destinations.spi.ICommandDestinationsManager;
import com.sitewhere.destinations.spi.kafka.IEnrichedCommandInvocationsConsumer;
import com.sitewhere.destinations.spi.microservice.ICommandDestinationsTenantEngine;
import com.sitewhere.microservice.multitenant.MicroserviceTenantEngine;
import com.sitewhere.server.lifecycle.CompositeLifecycleStep;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.microservice.spring.CommandDestinationsBeans;
import com.sitewhere.spi.server.lifecycle.ICompositeLifecycleStep;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Implementation of {@link IMicroserviceTenantEngine} that implements command
 * destinations functionality.
 * 
 * @author Derek
 */
public class CommandDestinationsTenantEngine extends MicroserviceTenantEngine
	implements ICommandDestinationsTenantEngine {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Command destinations manager */
    private ICommandDestinationsManager commandDestinationsManager;

    /** Kafka consumer for enriched command invocations */
    private IEnrichedCommandInvocationsConsumer enrichedCommandInvocationsConsumer;

    public CommandDestinationsTenantEngine(IMultitenantMicroservice<?> microservice, ITenant tenant) {
	super(microservice, tenant);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantInitialize(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void tenantInitialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	this.enrichedCommandInvocationsConsumer = new EnrichedCommandInvocationsConsumer(this);

	// Load configured registration manager.
	this.commandDestinationsManager = (ICommandDestinationsManager) getModuleContext()
		.getBean(CommandDestinationsBeans.BEAN_COMMAND_DESTINATION_MANAGER);
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

	// Start command invocations consumer.
	start.addStartStep(this, getEnrichedCommandInvocationsConsumer(), true);

	// Execute startup steps.
	start.execute(monitor);
    }

    /*
     * @see com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine#
     * tenantBootstrap(com.sitewhere.spi.microservice.multitenant.ITenantTemplate,
     * com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void tenantBootstrap(ITenantTemplate template, ILifecycleProgressMonitor monitor) throws SiteWhereException {
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

	// Stop command destinations manager.
	stop.addStopStep(this, getCommandDestinationsManager());

	// Execute shutdown steps.
	stop.execute(monitor);
    }

    /*
     * @see
     * com.sitewhere.destinations.spi.microservice.ICommandDestinationsTenantEngine#
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
     * @see
     * com.sitewhere.destinations.spi.microservice.ICommandDestinationsTenantEngine#
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
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}