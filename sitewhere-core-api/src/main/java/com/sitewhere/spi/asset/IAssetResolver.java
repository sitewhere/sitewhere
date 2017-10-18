/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset;

/**
 * Provides functionality required to resolve an asset reference.
 * 
 * @author Derek
 */
public interface IAssetResolver {

    /**
     * Get underlying asset management implementation.
     * 
     * @return
     */
    public IAssetManagement getAssetManagement();

    /**
     * Get underlying asset module management implementation.
     * 
     * @return
     */
    public IAssetModuleManagement getAssetModuleManagement();
}