/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.web.microservice.WebRestMicroservice;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

/**
 * Spring Boot application for web/REST microservice.
 * 
 * @author Derek
 */
@ComponentScan(basePackageClasses = { WebRestMicroservice.class })
public class WebRestApplication extends MicroserviceApplication<IWebRestMicroservice<?>> {

    @Autowired
    private IWebRestMicroservice<?> microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IWebRestMicroservice<?> getMicroservice() {
	return microservice;
    }

    /**
     * Entry point for Spring Boot.
     * 
     * @param args
     */
    public static void main(String[] args) {
	SpringApplication.run(WebRestApplication.class, args);
    }
}