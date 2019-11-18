/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.connectors;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.connectors.spi.IDeviceEventFilter;
import com.sitewhere.connectors.spi.IFilteredOutboundConnector;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.kafka.IEnrichedEventPayload;
import com.sitewhere.spi.microservice.lifecycle.ILifecycleProgressMonitor;

/**
 * Extends {@link OutboundConnector} with filtering functionality.
 */
public abstract class FilteredOutboundConnector extends OutboundConnector implements IFilteredOutboundConnector {

    /** List of filters in order they should be applied */
    private List<IDeviceEventFilter> filters = new ArrayList<IDeviceEventFilter>();

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);
	for (IDeviceEventFilter filter : getFilters()) {
	    initializeNestedComponent(filter, monitor, true);
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);
	for (IDeviceEventFilter filter : getFilters()) {
	    startNestedComponent(filter, monitor, true);
	}
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	for (IDeviceEventFilter filter : getFilters()) {
	    stopNestedComponent(filter, monitor);
	}
	super.stop(monitor);
    }

    /*
     * @see
     * com.sitewhere.connectors.spi.IOutboundConnector#processEventBatch(java.util.
     * List)
     */
    @Override
    public void processEventBatch(List<IEnrichedEventPayload> payloads) throws SiteWhereException {
	List<IEnrichedEventPayload> notFiltered = new ArrayList<>();
	for (IEnrichedEventPayload payload : payloads) {
	    if (!isFiltered(payload)) {
		notFiltered.add(payload);
	    }
	}
	processFilteredEventBatch(notFiltered);
    }

    /**
     * Indicates if an event is filtered.
     * 
     * @param payload
     * @return
     * @throws SiteWhereException
     */
    protected boolean isFiltered(IEnrichedEventPayload payload) throws SiteWhereException {
	for (IDeviceEventFilter filter : getFilters()) {
	    if (filter.isFiltered(payload.getEventContext(), payload.getEvent())) {
		getLogger().info(String.format("Event payload filtered for %s based on %s.",
			payload.getEvent().getDeviceId().toString(), filter.getClass().getSimpleName()));
		return true;
	    }
	}
	return false;
    }

    /*
     * @see com.sitewhere.connectors.spi.IFilteredOutboundConnector#getFilters()
     */
    @Override
    public List<IDeviceEventFilter> getFilters() {
	return filters;
    }

    public void setFilters(List<IDeviceEventFilter> filters) {
	this.filters = filters;
    }
}