/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.microservice;

import org.springframework.boot.SpringApplication;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;

/**
 * Spring Boot application for instance management microservice.
 * 
 * @author Derek
 */
public class InstanceManagementApplication extends MicroserviceApplication<IInstanceManagementMicroservice> {

    /** Instance management microservice */
    private static IInstanceManagementMicroservice SERVICE = new InstanceManagementMicroservice();

    /** Get microservice instance */
    public static IInstanceManagementMicroservice getInstanceManagementMicroservice() {
	return SERVICE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IInstanceManagementMicroservice getMicroservice() {
	return SERVICE;
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