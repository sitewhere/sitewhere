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
package com.sitewhere.device.spi.microservice;

import com.sitewhere.device.configuration.DeviceManagementConfiguration;
import com.sitewhere.device.spi.grpc.IDeviceManagementGrpcServer;
import com.sitewhere.grpc.client.spi.client.IAssetManagementApiChannel;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

/**
 * Microservice that provides device management functionality.
 */
public interface IDeviceManagementMicroservice extends
	IMultitenantMicroservice<MicroserviceIdentifier, DeviceManagementConfiguration, IDeviceManagementTenantEngine> {

    /**
     * Get device management GRPC server.
     * 
     * @return
     */
    public IDeviceManagementGrpcServer getDeviceManagementGrpcServer();

    /**
     * Get asset management API channel.
     * 
     * @return
     */
    public IAssetManagementApiChannel<?> getAssetManagementApiChannel();
}