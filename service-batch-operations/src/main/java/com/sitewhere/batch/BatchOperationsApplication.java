/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch;

import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;
import com.sitewhere.microservice.MicroserviceApplication;

/**
 * Spring Boot application for batch operations microservice.
 * 
 * @author Derek
 */
public class BatchOperationsApplication extends MicroserviceApplication<IBatchOperationsMicroservice> {

    private IBatchOperationsMicroservice microservice;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.microservice.spi.IMicroserviceApplication#getMicroservice()
     */
    @Override
    public IBatchOperationsMicroservice getMicroservice() {
	return microservice;
    }
}