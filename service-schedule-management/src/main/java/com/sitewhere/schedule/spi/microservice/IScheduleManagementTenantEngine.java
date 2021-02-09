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
package com.sitewhere.schedule.spi.microservice;

import com.sitewhere.grpc.service.ScheduleManagementGrpc;
import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.rdb.spi.IRdbEntityManagerProvider;
import com.sitewhere.schedule.configuration.ScheduleManagementTenantConfiguration;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to schedule
 * management.
 */
public interface IScheduleManagementTenantEngine
	extends IMicroserviceTenantEngine<ScheduleManagementTenantConfiguration> {

    /**
     * Get associated schedule management implementation.
     * 
     * @return
     */
    public IScheduleManagement getScheduleManagement();

    /**
     * Get implementation class that wraps schedule management with GRPC
     * conversions.
     * 
     * @return
     */
    public ScheduleManagementGrpc.ScheduleManagementImplBase getScheduleManagementImpl();

    /**
     * Get provider which provides an RDB entity manager for this tenant.
     * 
     * @return
     */
    public IRdbEntityManagerProvider getRdbEntityManagerProvider();
}