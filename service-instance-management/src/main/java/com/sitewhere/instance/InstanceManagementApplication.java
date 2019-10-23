/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import com.sitewhere.instance.microservice.InstanceManagementMicroservice;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.web.WebRestConfiguration;

/**
 * Spring Boot application for web/REST microservice.
 */
@ComponentScan(basePackageClasses = { InstanceManagementMicroservice.class, WebRestConfiguration.class })
public class InstanceManagementApplication extends MicroserviceApplication<IInstanceManagementMicroservice<?>> {

    @Autowired
    private IInstanceManagementMicroservice<?> microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }

    /**
     * Entry point for Spring Boot.
     * 
     * @param args
     */
    public static void main(String[] args) {
	SpringApplication.run(InstanceManagementApplication.class, args);
    }
}