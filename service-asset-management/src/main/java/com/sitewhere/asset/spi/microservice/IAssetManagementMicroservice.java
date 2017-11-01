/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.spi.microservice;

import com.sitewhere.asset.spi.modules.IAssetModuleManager;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

/**
 * Microservice that provides asset management functionality.
 * 
 * @author Derek
 */
public interface IAssetManagementMicroservice extends IMultitenantMicroservice<IAssetManagementTenantEngine> {

    /**
     * Get asset management implementation.
     * 
     * @return
     */
    public IAssetManagement getAssetManagement();

    /**
     * Get asset module manager implementation.
     * 
     * @return
     */
    public IAssetModuleManager getAssetModuleManager();
}