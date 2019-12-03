/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.microservice;

import com.sitewhere.schedule.spi.microservice.IScheduleManagementMicroservice;

/**
 * Spring bean configuration for microservice.
 */
public class ScheduleManagementMicroserviceConfiguration {

    public IScheduleManagementMicroservice ruleProcessingMicroservice() {
	return new ScheduleManagementMicroservice();
    }
}