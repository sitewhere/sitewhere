/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.kafka.payload.IEnrichedEventPayload;

/**
 * Default {@link ICommandDestinationsManager} implementation.
 * 
 * @author Derek
 */
public class CommandDestinationsManager extends TenantEngineLifecycleComponent implements ICommandDestinationsManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }

    /*
     * @see com.sitewhere.destinations.spi.ICommandDestinationsManager#
     * processCommandInvocation(com.sitewhere.spi.microservice.kafka.payload.
     * IEnrichedEventPayload)
     */
    @Override
    public void processCommandInvocation(IEnrichedEventPayload payload) throws SiteWhereException {
	getLogger().info("Command destinations manager received a command invocation.");
    }
}