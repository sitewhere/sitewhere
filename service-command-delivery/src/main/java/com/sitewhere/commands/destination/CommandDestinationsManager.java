/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.commands.destination;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.commands.spi.ICommandDestination;
import com.sitewhere.commands.spi.ICommandDestinationsManager;
import com.sitewhere.microservice.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Default {@link ICommandDestinationsManager} implementation.
 */
public class CommandDestinationsManager extends TenantEngineLifecycleComponent implements ICommandDestinationsManager {

    /** Map of command destinations indexed by destination id */
    private Map<String, ICommandDestination<?, ?>> commandDestinations = new HashMap<>();

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (ICommandDestination<?, ?> destination : getCommandDestinations().values()) {
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
	for (ICommandDestination<?, ?> destination : getCommandDestinations().values()) {
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
	for (ICommandDestination<?, ?> destination : getCommandDestinations().values()) {
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
    public Map<String, ICommandDestination<?, ?>> getCommandDestinations() {
	return commandDestinations;
    }

    public void setCommandDestinations(List<ICommandDestination<?, ?>> commandDestinations) {
	getCommandDestinations().clear();
	for (ICommandDestination<?, ?> destination : commandDestinations) {
	    getCommandDestinations().put(destination.getDestinationId(), destination);
	}
    }
}