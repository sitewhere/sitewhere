/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.search.microservice;

import com.sitewhere.microservice.instance.InstanceSettings;
import com.sitewhere.search.spi.microservice.IEventSearchMicroservice;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * Spring bean configuration for microservice.
 */
public class EventSearchMicroserviceConfiguration {

    public IEventSearchMicroservice eventSearchMicroservice() {
	return new EventSearchMicroservice();
    }

    public IInstanceSettings instanceSettings() {
	return new InstanceSettings();
    }
}