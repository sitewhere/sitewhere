/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset.request;

import com.sitewhere.spi.asset.IHardwareAsset;

/**
 * Get information needed to create an {@link IHardwareAsset}.
 * 
 * @author Derek
 */
public interface IHardwareAssetCreateRequest extends IAssetCreateRequest {

    /**
     * Get the stock keeping unit (SKU).
     * 
     * @return
     */
    public String getSku();

    /**
     * Get the asset description.
     * 
     * @return
     */
    public String getDescription();
}