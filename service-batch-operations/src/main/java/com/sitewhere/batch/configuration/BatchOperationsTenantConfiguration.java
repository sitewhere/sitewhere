/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
