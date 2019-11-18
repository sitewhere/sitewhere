/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.microservice;

import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.microservice.instance.InstanceSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * Spring bean configuration for microservice.
 */
public class EventManagementMicroserviceConfiguration {

    public IEventManagementMicroservice eventManagementMicroservice() {
	return new EventManagementMicroservice();
    }

    public IInstanceSettings instanceSettings() {
	return new InstanceSettings();
    }
}
