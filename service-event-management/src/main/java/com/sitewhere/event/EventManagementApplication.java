/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;

/**
 * Spring Boot application for device event management microservice.
 * 
 * @author Derek
 */
@ComponentScan
public class EventManagementApplication extends MicroserviceApplication<IEventManagementMicroservice> {

    @Autowired
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

    /**
     * Entry point for Spring Boot.
     * 
     * @param args
     */
    public static void main(String[] args) {
	SpringApplication.run(EventManagementApplication.class, args);
    }
}