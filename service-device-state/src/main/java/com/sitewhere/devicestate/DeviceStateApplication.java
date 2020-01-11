/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;

/**
 * Main application which runs the device state microservice.
 */
@ApplicationScoped
public class DeviceStateApplication extends MicroserviceApplication<IDeviceStateMicroservice> {

    @Inject
    private IDeviceStateMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IDeviceStateMicroservice getMicroservice() {
	return microservice;
    }
}