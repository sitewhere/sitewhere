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
package com.sitewhere.asset.spi.microservice;

import com.sitewhere.asset.configuration.AssetManagementTenantConfiguration;
import com.sitewhere.grpc.service.AssetManagementGrpc;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.rdb.spi.IRdbEntityManagerProvider;
import com.sitewhere.spi.microservice.multitenant.IMicroserviceTenantEngine;

/**
 * Tenant engine which implements asset management functionality.
 */
public interface IAssetManagementTenantEngine extends IMicroserviceTenantEngine<AssetManagementTenantConfiguration> {

    /**
     * Get associated asset management implementation.
     * 
     * @return
     */
    public IAssetManagement getAssetManagement();

    /**
     * Get implementation class that wraps asset management with GRPC conversions.
     * 
     * @return
     */
    public AssetManagementGrpc.AssetManagementImplBase getAssetManagementImpl();

    /**
     * Get provider which provides an RDB entity manager for this tenant.
     * 
     * @return
     */
    public IRdbEntityManagerProvider getRdbEntityManagerProvider();
}