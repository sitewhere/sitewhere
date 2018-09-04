/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset.request;

import com.sitewhere.spi.common.request.IBrandedEntityCreateRequest;

/**
 * Information needed to create a new asset.
 * 
 * @author Derek
 */
public interface IAssetCreateRequest extends IBrandedEntityCreateRequest {

    /**
     * Get reference token for asset type.
     * 
     * @return
     */
    public String getAssetTypeToken();

    /**
     * Get asset name.
     * 
     * @return
     */
    public String getName();
}