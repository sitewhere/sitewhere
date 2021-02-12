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
package com.sitewhere.devicestate.spi.microservice;

import com.sitewhere.devicestate.configuration.DeviceStateTenantConfiguration;
import com.sitewhere.devicestate.spi.IDevicePresenceManager;
import com.sitewhere.devicestate.spi.IDeviceStateMergeStrategy;
import com.sitewhere.grpc.service.DeviceStateGrpc;
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.rdb.spi.IRdbEntityManagerProvider;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to device
 * state management.
 */
public interface IDeviceStateTenantEngine extends IMicroserviceTenantEngine<DeviceStateTenantConfiguration> {

    /**
     * Get associated device state management implementation.
     * 
     * @return
     */
    public IDeviceStateManagement getDeviceStateManagement();

    /**
     * Get implementation class that wraps device state with GRPC conversions.
     * 
     * @return
     */
    public DeviceStateGrpc.DeviceStateImplBase getDeviceStateImpl();

    /**
     * Get merge strategy used for assembling device state.
     * 
     * @return
     */
    public IDeviceStateMergeStrategy<?> getDeviceStateMergeStrategy();

    /**
     * Get presence manager implementation.
     * 
     * @return
     */
    public IDevicePresenceManager getDevicePresenceManager();

    /**
     * Get provider which provides an RDB entity manager for this tenant.
     * 
     * @return
     */
    public IRdbEntityManagerProvider getRdbEntityManagerProvider();
}