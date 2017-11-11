/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementMicroservice;

/**
 * Spring Boot application for schedule management microservice.
 * 
 * @author Derek
 */
@ComponentScan
public class ScheduleManagementApplication extends MicroserviceApplication<IScheduleManagementMicroservice> {

    @Autowired
    private IScheduleManagementMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IScheduleManagementMicroservice getMicroservice() {
	return microservice;
    }

    /**
     * Entry point for Spring Boot.
     * 
     * @param args
     */
    public static void main(String[] args) {
	SpringApplication.run(ScheduleManagementApplication.class, args);
    }
}