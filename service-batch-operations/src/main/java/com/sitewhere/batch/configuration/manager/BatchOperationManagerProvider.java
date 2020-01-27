/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.batch.configuration.manager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.sitewhere.batch.BatchOperationManager;
import com.sitewhere.batch.configuration.BatchOperationsTenantConfiguration;

/**
 * Provides a batch operation manager based on tenant configuration.
 */
public class BatchOperationManagerProvider implements Provider<BatchOperationManager> {

    /** Injected configuration */
    private BatchOperationsTenantConfiguration configuration;

    @Inject
    public BatchOperationManagerProvider(BatchOperationsTenantConfiguration configuration) {
	this.configuration = configuration;
    }

    /*
     * @see com.google.inject.Provider#get()
     */
    @Override
    public BatchOperationManager get() {
	BatchOperationManager manager = new BatchOperationManager();
	manager.setThrottleDelayMs(getConfiguration().getBatchOperationManager().getThrottleDelayMs());
	return manager;
    }

    protected BatchOperationsTenantConfiguration getConfiguration() {
	return configuration;
    }
}
