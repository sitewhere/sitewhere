/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search.asset;

import java.util.UUID;

import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.search.asset.IAssetSearchCriteria;

/**
 * Model object for properties used in asset searches.
 * 
 * @author Derek
 */
public class AssetSearchCriteria extends SearchCriteria implements IAssetSearchCriteria {

    /** Filter by asset type */
    private UUID assetTypeId;

    public AssetSearchCriteria(int pageNumber, int pageSize) {
	super(pageNumber, pageSize);
    }

    /*
     * @see com.sitewhere.spi.search.asset.IAssetSearchCriteria#getAssetTypeId()
     */
    @Override
    public UUID getAssetTypeId() {
	return assetTypeId;
    }

    public void setAssetTypeId(UUID assetTypeId) {
	this.assetTypeId = assetTypeId;
    }
}