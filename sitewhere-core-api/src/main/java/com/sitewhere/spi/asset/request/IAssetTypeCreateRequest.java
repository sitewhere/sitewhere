/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset.request;

import java.io.Serializable;
import java.util.Map;

import com.sitewhere.spi.asset.AssetCategory;

/**
 * Information needed to create a new asset type.
 * 
 * @author Derek
 */
public interface IAssetTypeCreateRequest extends Serializable {

    /**
     * Get token used to reference asset type.
     * 
     * @return
     */
    public String getToken();

    /**
     * Get asset type name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get asset type description.
     * 
     * @return
     */
    public String getDescription();

    /**
     * Get URL for asset type image.
     * 
     * @return
     */
    public String getImageUrl();

    /**
     * Get category of asset type.
     * 
     * @return
     */
    public AssetCategory getAssetCategory();

    /**
     * Get metadata values.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}