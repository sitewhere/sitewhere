/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset;

import java.io.Serializable;
import java.util.UUID;

import com.sitewhere.spi.common.IMetadataProviderEntity;

/**
 * Item that represents a tangible object (person, place, thing) in the world.
 * 
 * @author Derek
 */
public interface IAsset extends IMetadataProviderEntity, Serializable {

    /**
     * Get unique id of asset type.
     * 
     * @return
     */
    public UUID getAssetTypeId();

    /**
     * Get asset name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get URL for asset image.
     * 
     * @return
     */
    public String getImageUrl();
}