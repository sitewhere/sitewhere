/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.sources.spi.microservice.IEventSourcesMicroservice;

/**
 * Main application which runs the event sources microservice.
 */
@ApplicationScoped
public class EventSourcesApplication extends MicroserviceApplication<IEventSourcesMicroservice> {

    @Inject
    private IEventSourcesMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IEventSourcesMicroservice getMicroservice() {
	return microservice;
    }
}