/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset;

/**
 * Provides information about an asset module.
 * 
 * @author Derek
 */
public interface IAssetModuleDescriptor {

    /**
     * Get the unique module identifier.
     * 
     * @return
     */
    public String getId();

    /**
     * Get the module name.
     * 
     * @return
     */
    public String getName();

    /**
     * Indicates the type of assets provided.
     * 
     * @return
     */
    public AssetType getAssetType();
}
