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
