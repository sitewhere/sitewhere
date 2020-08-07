/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.configuration;

import com.sitewhere.batch.configuration.manager.BatchOperationManagerConfiguration;
import com.sitewhere.microservice.datastore.DatastoreDefinition;
import com.sitewhere.spi.microservice.multitenant.ITenantEngineConfiguration;

import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * Maps batch operation tenant engine YAML configuration to objects.
 */
@RegisterForReflection
public class BatchOperationsTenantConfiguration implements ITenantEngineConfiguration {

    /** Datastore definition */
    private DatastoreDefinition datastore;

    /** Batch operation manager configuration */
    private BatchOperationManagerConfiguration batchOperationManager;

    public DatastoreDefinition getDatastore() {
	return datastore;
    }

    public void setDatastore(DatastoreDefinition datastore) {
	this.datastore = datastore;
    }

    public BatchOperationManagerConfiguration getBatchOperationManager() {
	return batchOperationManager;
    }

    public void setBatchOperationManager(BatchOperationManagerConfiguration batchOperationManager) {
	this.batchOperationManager = batchOperationManager;
    }
}
