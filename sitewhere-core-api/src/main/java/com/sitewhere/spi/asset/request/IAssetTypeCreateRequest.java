/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset.request;

import com.sitewhere.spi.asset.AssetCategory;
import com.sitewhere.spi.common.IAccessible;
import com.sitewhere.spi.common.request.IBrandedEntityCreateRequest;

/**
 * Information needed to create a new asset type.
 * 
 * @author Derek
 */
public interface IAssetTypeCreateRequest extends IAccessible, IBrandedEntityCreateRequest {

    /**
     * Get category of asset type.
     * 
     * @return
     */
    public AssetCategory getAssetCategory();
}