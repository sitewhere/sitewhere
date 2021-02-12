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
package com.sitewhere.connectors;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.connectors.spi.IDeviceEventFilter;
import com.sitewhere.connectors.spi.IFilteredOutboundConnector;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.kafka.IProcessedEventPayload;
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
    public void processEventBatch(List<IProcessedEventPayload> payloads) throws SiteWhereException {
	List<IProcessedEventPayload> notFiltered = new ArrayList<>();
	for (IProcessedEventPayload payload : payloads) {
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
    protected boolean isFiltered(IProcessedEventPayload payload) throws SiteWhereException {
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