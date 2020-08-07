/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
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