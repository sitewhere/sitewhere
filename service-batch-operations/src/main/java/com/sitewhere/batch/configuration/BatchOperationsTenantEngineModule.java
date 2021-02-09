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

import com.sitewhere.batch.configuration.manager.BatchOperationManagerProvider;
import com.sitewhere.batch.persistence.rdb.RdbBatchManagement;
import com.sitewhere.batch.spi.IBatchOperationManager;
import com.sitewhere.batch.spi.microservice.IBatchOperationsTenantEngine;
import com.sitewhere.microservice.api.batch.IBatchManagement;
import com.sitewhere.microservice.multitenant.TenantEngineModule;

/**
 * Guice module used for configuring components associated with a batch
 * operations tenant engine.
 */
public class BatchOperationsTenantEngineModule extends TenantEngineModule<BatchOperationsTenantConfiguration> {

    public BatchOperationsTenantEngineModule(IBatchOperationsTenantEngine tenantEngine,
	    BatchOperationsTenantConfiguration configuration) {
	super(tenantEngine, configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(IBatchOperationsTenantEngine.class).toInstance((IBatchOperationsTenantEngine) getTenantEngine());
	bind(BatchOperationsTenantConfiguration.class).toInstance(getConfiguration());
	bind(IBatchManagement.class).to(RdbBatchManagement.class);
	bind(IBatchOperationManager.class).toProvider(BatchOperationManagerProvider.class);
    }
}
