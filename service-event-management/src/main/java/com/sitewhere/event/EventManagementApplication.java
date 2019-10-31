/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event;

import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;

/**
 * Spring Boot application for device event management microservice.
 * 
 * @author Derek
 */
public class EventManagementApplication extends MicroserviceApplication<IEventManagementMicroservice> {

    private IEventManagementMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IEventManagementMicroservice getMicroservice() {
	return microservice;
    }
}