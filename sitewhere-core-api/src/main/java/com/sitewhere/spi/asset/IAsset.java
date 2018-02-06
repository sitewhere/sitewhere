/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset;

import java.io.Serializable;
import java.util.Map;

/**
 * Unique item to which a device may be associated.
 * 
 * @author dadams
 */
public interface IAsset extends Comparable<IAsset>, Serializable {

    /**
     * Unique asset id.
     * 
     * @return
     */
    public String getId();

    /**
     * Get asset name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get the asset type indicator.
     * 
     * @return
     */
    public AssetType getType();

    /**
     * Get id of parent asset category.
     * 
     * @return
     */
    public String getAssetCategoryId();

    /**
     * Get URL for asset image.
     * 
     * @return
     */
    public String getImageUrl();

    /**
     * Get properties associated with asset.
     * 
     * @return
     */
    public Map<String, String> getProperties();
}