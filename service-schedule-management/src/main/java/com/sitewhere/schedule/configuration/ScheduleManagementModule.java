/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.configuration;

import com.sitewhere.microservice.configuration.MicroserviceModule;

/**
 * Guice module used for configuring components associated with the schedule
 * management microservice.
 */
public class ScheduleManagementModule extends MicroserviceModule<ScheduleManagementConfiguration> {

    public ScheduleManagementModule(ScheduleManagementConfiguration configuration) {
	super(configuration);
    }
}
