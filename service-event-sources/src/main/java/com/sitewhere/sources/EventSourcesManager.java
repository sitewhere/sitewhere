/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.server.lifecycle.TenantLifecycleComponent;
import com.sitewhere.sources.spi.IEventSourcesManager;
import com.sitewhere.spi.device.communication.IInboundEventSource;

/**
 * Manages lifecycle of the list of event sources configured for a tenant.
 * 
 * @author Derek
 */
public class EventSourcesManager extends TenantLifecycleComponent implements IEventSourcesManager {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** List of event sources */
    private List<IInboundEventSource<?>> eventSources;

    /*
     * @see com.sitewhere.sources.spi.IEventSourcesManager#getEventSources()
     */
    @Override
    public List<IInboundEventSource<?>> getEventSources() {
	return eventSources;
    }

    public void setEventSources(List<IInboundEventSource<?>> eventSources) {
	this.eventSources = eventSources;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}