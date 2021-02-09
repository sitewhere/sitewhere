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
package com.sitewhere.labels.spi.microservice;

import com.sitewhere.labels.configuration.LabelGenerationConfiguration;
import com.sitewhere.labels.spi.grpc.ILabelGenerationGrpcServer;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

/**
 * Microservice that provides label generation functionality.
 */
public interface ILabelGenerationMicroservice extends
	IMultitenantMicroservice<MicroserviceIdentifier, LabelGenerationConfiguration, ILabelGenerationTenantEngine> {

    /**
     * Get label generation GRPC server.
     * 
     * @return
     */
    public ILabelGenerationGrpcServer getLabelGenerationGrpcServer();

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
}