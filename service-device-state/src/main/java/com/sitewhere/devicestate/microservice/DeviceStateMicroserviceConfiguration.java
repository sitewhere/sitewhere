/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.microservice;

import com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice;

/**
 * Spring bean configuration for microservice.
 */
public class DeviceStateMicroserviceConfiguration {

    public IDeviceStateMicroservice deviceStateMicroservice() {
	return new DeviceStateMicroservice();
    }
}