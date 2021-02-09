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
