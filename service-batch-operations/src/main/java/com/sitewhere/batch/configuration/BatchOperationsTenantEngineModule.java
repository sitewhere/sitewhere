/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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
