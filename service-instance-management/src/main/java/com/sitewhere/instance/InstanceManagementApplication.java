/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance;

import javax.inject.Inject;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;

/**
 * Spring Boot application for web/REST microservice.
 */
public class InstanceManagementApplication extends MicroserviceApplication<IInstanceManagementMicroservice<?>> {

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}