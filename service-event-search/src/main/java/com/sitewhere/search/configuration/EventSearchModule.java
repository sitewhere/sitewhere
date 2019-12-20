/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.search.configuration;

import com.sitewhere.microservice.configuration.MicroserviceModule;

/**
 * Guice module used for configuring components associated with the event search
 * microservice.
 */
public class EventSearchModule extends MicroserviceModule<EventSearchConfiguration> {

    public EventSearchModule(EventSearchConfiguration configuration) {
	super(configuration);
    }
}
