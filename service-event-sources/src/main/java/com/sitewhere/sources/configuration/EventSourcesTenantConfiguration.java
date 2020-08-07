/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources.configuration;

import java.util.List;

import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;

/**
 * Maps event sources tenant engine YAML configuration to objects.
 */
public class EventSourcesTenantConfiguration implements ITenantEngineConfiguration {

    /** Event source configurations */
    private List<EventSourceGenericConfiguration> eventSources;

    public List<EventSourceGenericConfiguration> getEventSources() {
	return eventSources;
    }

    public void setEventSources(List<EventSourceGenericConfiguration> eventSources) {
	this.eventSources = eventSources;
    }
}
