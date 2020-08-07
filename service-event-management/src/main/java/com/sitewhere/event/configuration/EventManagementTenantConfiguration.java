/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.configuration;

import com.sitewhere.microservice.datastore.DatastoreDefinition;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;

/**
 * Maps event management tenant engine YAML configuration to objects.
 */
public class EventManagementTenantConfiguration implements ITenantEngineConfiguration {

    /** Default number of threads used for concurrent processing of events */
    private static final int DEFAULT_PROCESSING_THREAD_COUNT = 10;

    /** Datastore definition */
    private DatastoreDefinition datastore;

    /** Number of threads used for concurrent processing of events */
    private int processingThreadCount = DEFAULT_PROCESSING_THREAD_COUNT;

    public DatastoreDefinition getDatastore() {
	return datastore;
    }

    public void setDatastore(DatastoreDefinition datastore) {
	this.datastore = datastore;
    }

    public int getProcessingThreadCount() {
	return processingThreadCount;
    }

    public void setProcessingThreadCount(int processingThreadCount) {
	this.processingThreadCount = processingThreadCount;
    }
}
