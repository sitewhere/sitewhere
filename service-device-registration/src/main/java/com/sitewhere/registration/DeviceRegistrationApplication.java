/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.registration;

import com.sitewhere.microservice.MicroserviceApplication;
import com.sitewhere.registration.spi.microservice.IDeviceRegistrationMicroservice;

/**
 * Spring Boot application for device registration microservice.
 * 
 * @author Derek
 */
public class DeviceRegistrationApplication extends MicroserviceApplication<IDeviceRegistrationMicroservice> {

    private IDeviceRegistrationMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IDeviceRegistrationMicroservice getMicroservice() {
	return microservice;
    }
}