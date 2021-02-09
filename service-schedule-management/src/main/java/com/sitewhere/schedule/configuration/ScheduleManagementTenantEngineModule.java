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
package com.sitewhere.schedule.configuration;

import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.microservice.multitenant.TenantEngineModule;
import com.sitewhere.schedule.persistence.rdb.RdbScheduleManagement;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementTenantEngine;

/**
 * Guice module used for configuring components associated with a schedule
 * management tenant engine.
 */
public class ScheduleManagementTenantEngineModule extends TenantEngineModule<ScheduleManagementTenantConfiguration> {

    public ScheduleManagementTenantEngineModule(IScheduleManagementTenantEngine tenantEngine,
	    ScheduleManagementTenantConfiguration configuration) {
	super(tenantEngine, configuration);
    }

    /*
     * @see com.google.inject.AbstractModule#configure()
     */
    @Override
    protected void configure() {
	bind(IScheduleManagementTenantEngine.class).toInstance((IScheduleManagementTenantEngine) getTenantEngine());
	bind(ScheduleManagementTenantConfiguration.class).toInstance(getConfiguration());
	bind(IScheduleManagement.class).to(RdbScheduleManagement.class);
    }
}
