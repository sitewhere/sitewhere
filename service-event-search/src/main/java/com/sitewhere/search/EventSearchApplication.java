/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.search;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.search.spi.microservice.IEventSearchMicroservice;

/**
 * Spring Boot application for event search microservice.
 */
public class EventSearchApplication extends MicroserviceApplication<IEventSearchMicroservice> {

    private IEventSearchMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IEventSearchMicroservice getMicroservice() {
	return microservice;
    }
}