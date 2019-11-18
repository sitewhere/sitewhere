/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.sources;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.sources.spi.microservice.IEventSourcesMicroservice;

/**
 * Spring Boot application for event sources microservice.
 */
public class EventSourcesApplication extends MicroserviceApplication<IEventSourcesMicroservice> {

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