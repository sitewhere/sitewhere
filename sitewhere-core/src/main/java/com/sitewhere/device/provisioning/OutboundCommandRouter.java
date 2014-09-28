/*
 * OutboundCommandRouter.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.provisioning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.provisioning.ICommandDestination;
import com.sitewhere.spi.device.provisioning.IOutboundCommandRouter;

/**
 * Abstract base class for {@link IOutboundCommandRouter} implementations.
 * 
 * @author Derek
 */
public abstract class OutboundCommandRouter implements IOutboundCommandRouter {

	/** List of destinations serviced by the router */
	private Map<String, ICommandDestination<?, ?>> destinations =
			new HashMap<String, ICommandDestination<?, ?>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#start()
	 */
	@Override
	public void start() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereLifecycle#stop()
	 */
	@Override
	public void stop() throws SiteWhereException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.device.provisioning.IOutboundCommandRouter#initialize(java.util
	 * .List)
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