/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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