/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.microservice;

import com.sitewhere.batch.spi.microservice.IBatchOperationsMicroservice;

/**
 * Spring bean configuration for microservice.
 */
public class BatchOperationsMicroserviceConfiguration {

    public IBatchOperationsMicroservice batchOperationsMicroservice() {
	return new BatchOperationsMicroservice();
    }
}