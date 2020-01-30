/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementMicroservice;

/**
 * Main application which runs the schedule management microservice.
 */
@ApplicationScoped
public class ScheduleManagementApplication extends MicroserviceApplication<IScheduleManagementMicroservice> {

    @Inject
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
}