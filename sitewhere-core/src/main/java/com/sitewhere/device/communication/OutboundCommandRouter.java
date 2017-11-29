/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.communication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.communication.ICommandDestination;
import com.sitewhere.spi.device.communication.IOutboundCommandRouter;
import com.sitewhere.spi.server.lifecycle.LifecycleComponentType;

/**
 * Abstract base class for {@link IOutboundCommandRouter} implementations.
 * 
 * @author Derek
 */
public abstract class OutboundCommandRouter extends TenantEngineLifecycleComponent implements IOutboundCommandRouter {

    /** List of destinations serviced by the router */
    private Map<String, ICommandDestination<?, ?>> destinations = new HashMap<String, ICommandDestination<?, ?>>();

    public OutboundCommandRouter() {
	super(LifecycleComponentType.CommandRouter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.communication.IOutboundCommandRouter#initialize(
     * java.util .List)
     */
    @Override
    public void initialize(List<ICommandDestination<?, ?>> destinationList) throws SiteWhereException {
	this.destinations.clear();

	// Create map of destinations by id.
	for (ICommandDestination<?, ?> destination : destinationList) {
	    destinations.put(destination.getDestinationId(), destination);
	}
    }

    public Map<String, ICommandDestination<?, ?>> getDestinations() {
	return destinations;
    }
}