/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.spi.microservice;

import com.sitewhere.grpc.service.AssetManagementGrpc;
import com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.asset.IAssetManagement;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to asset
 * management.
 * 
 * @author Derek
 */
public interface IAssetManagementTenantEngine extends IMicroserviceTenantEngine {

    /**
     * Get associated asset management implementation.
     * 
     * @return
     */
    public IAssetManagement getAssetManagement();

    /**
     * Get implementation class that wraps asset management with GRPC
     * conversions.
     * 
     * @return
     */
    public AssetManagementGrpc.AssetManagementImplBase getAssetManagementImpl();
}