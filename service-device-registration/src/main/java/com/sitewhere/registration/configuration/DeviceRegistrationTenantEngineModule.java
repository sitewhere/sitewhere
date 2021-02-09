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
package com.sitewhere.registration.configuration;

import com.sitewhere.microservice.multitenant.TenantEngineModule;
import com.sitewhere.registration.DeviceRegistrationManager;
import com.sitewhere.registration.spi.IRegistrationManager;
import com.sitewhere.registration.spi.microservice.IDeviceRegistrationTenantEngine;

/**
 * Guice module used for configuring components associated with a device
 * registration tenant engine.
 */
public class DeviceRegistrationTenantEngineModule extends TenantEngineModule<DeviceRegistrationTenantConfiguration> {

    public DeviceRegistrationTenantEngineModule(IDeviceRegistrationTenantEngine tenantEngine,
	    DeviceRegistrationTenantConfiguration configuration) {
	super(tenantEngine, configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(IDeviceRegistrationTenantEngine.class).toInstance((IDeviceRegistrationTenantEngine) getTenantEngine());
	bind(DeviceRegistrationTenantConfiguration.class).toInstance(getConfiguration());
	bind(IRegistrationManager.class).to(DeviceRegistrationManager.class);
    }
}
