/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.configuration;

import com.sitewhere.microservice.configuration.MicroserviceModule;

/**
 * Guice module used for configuring components associated with the batch
 * operations microservice.
 */
public class BatchOperationsModule extends MicroserviceModule<BatchOperationsConfiguration> {

    public BatchOperationsModule(BatchOperationsConfiguration configuration) {
	super(configuration);
    }
}
