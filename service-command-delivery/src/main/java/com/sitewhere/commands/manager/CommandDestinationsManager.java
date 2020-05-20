/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.sitewhere.commands.configuration.CommandDeliveryTenantConfiguration;
import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Default {@link ICommandDestinationsManager} implementation.
 */
public class CommandDestinationsManager extends TenantEngineLifecycleComponent implements ICommandDestinationsManager {

    /** Command delivery configuration */
    private CommandDeliveryTenantConfiguration configuration;

    /** Map of command destinations indexed by destination id */
    private List<ICommandDestination<?, ?>> commandDestinations = new ArrayList<>();

    @Inject
    public CommandDestinationsManager(CommandDeliveryTenantConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	getLogger().info(String.format("About to initialize command destinations manager with configuration:\n%s\n\n",
		MarshalUtils.marshalJsonAsPrettyString(getConfiguration().getCommandDestinations())));
	this.commandDestinations = CommandDestinationsParser.parse(this, getConfiguration());

	for (ICommandDestination<?, ?> destination : getCommandDestinations()) {
	    try {
		initializeNestedComponent(destination, monitor, true);
	    } catch (SiteWhereException e) {
		getLogger().error("Error initializing command destination.", e);
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (ICommandDestination<?, ?> destination : getCommandDestinations()) {
	    try {
		startNestedComponent(destination, monitor, true);
	    } catch (SiteWhereException e) {
		getLogger().error("Error starting command destination.", e);
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (ICommandDestination<?, ?> destination : getCommandDestinations()) {
	    try {
		stopNestedComponent(destination, monitor);
	    } catch (SiteWhereException e) {
		getLogger().error("Error stopping command destination.", e);
	    }
	}
    }

    /*
     * @see
     * com.sitewhere.commands.spi.ICommandDestinationsManager#getCommandDestinations
     * ()
     */
    @Override
    public List<ICommandDestination<?, ?>> getCommandDestinations() {
	return commandDestinations;
    }

    /*
     * @see com.sitewhere.commands.spi.ICommandDestinationsManager#
     * getCommandDestinationsMap()
     */
    @Override
    public Map<String, ICommandDestination<?, ?>> getCommandDestinationsMap() {
	Map<String, ICommandDestination<?, ?>> destById = new HashMap<>();
	for (ICommandDestination<?, ?> destination : getCommandDestinations()) {
	    destById.put(destination.getDestinationId(), destination);
	}
	return destById;
    }

    protected CommandDeliveryTenantConfiguration getConfiguration() {
	return configuration;
    }
}