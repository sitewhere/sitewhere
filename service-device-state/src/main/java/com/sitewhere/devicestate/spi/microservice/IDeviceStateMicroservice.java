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

import com.sitewhere.devicestate.configuration.DeviceStateConfiguration;
import com.sitewhere.devicestate.spi.grpc.IDeviceStateGrpcServer;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

/**
 * Microservice that provides device state management functionality.
 */
public interface IDeviceStateMicroservice
	extends IMultitenantMicroservice<MicroserviceIdentifier, DeviceStateConfiguration, IDeviceStateTenantEngine> {

    /**
     * Get device state GRPC server.
     * 
     * @return
     */
    public IDeviceStateGrpcServer getDeviceStateGrpcServer();

    /**
     * Get device management API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceManagement getDeviceManagement();

    /**
     * Get asset management API access via GRPC channel.
     * 
     * @return
     */
    public IAssetManagement getAssetManagement();

    /**
     * Get device event management API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceEventManagementApiChannel<?> getDeviceEventManagementApiChannel();
}