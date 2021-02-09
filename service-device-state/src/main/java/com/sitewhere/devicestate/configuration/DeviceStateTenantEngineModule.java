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
package com.sitewhere.devicestate.configuration;

import com.sitewhere.devicestate.persistence.rdb.RdbDeviceStateManagement;
import com.sitewhere.devicestate.persistence.rdb.RdbDeviceStateMergeStrategy;
import com.sitewhere.devicestate.spi.IDeviceStateMergeStrategy;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.microservice.multitenant.TenantEngineModule;

/**
 * Guice module used for configuring components associated with a device state
 * tenant engine.
 */
public class DeviceStateTenantEngineModule extends TenantEngineModule<DeviceStateTenantConfiguration> {

    public DeviceStateTenantEngineModule(IDeviceStateTenantEngine tenantEngine,
	    DeviceStateTenantConfiguration configuration) {
	super(tenantEngine, configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(IDeviceStateTenantEngine.class).toInstance((IDeviceStateTenantEngine) getTenantEngine());
	bind(DeviceStateTenantConfiguration.class).toInstance(getConfiguration());
	bind(IDeviceStateManagement.class).to(RdbDeviceStateManagement.class);
	bind(IDeviceStateMergeStrategy.class).to(RdbDeviceStateMergeStrategy.class);
    }
}
