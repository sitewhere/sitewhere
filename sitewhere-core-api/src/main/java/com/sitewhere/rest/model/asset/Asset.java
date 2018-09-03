/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.BrandedEntity;
import com.sitewhere.spi.asset.IAsset;

/**
 * Model object for an asset.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class Asset extends BrandedEntity implements IAsset {

    /** Serial version UID */
    private static final long serialVersionUID = -853673101089583873L;

    /** Asset type id */
    private UUID assetTypeId;

    /** Asset name */
    private String name;

    /*
     * @see com.sitewhere.spi.asset.IAsset#getAssetTypeId()
     */
    @Override
    public UUID getAssetTypeId() {
	return assetTypeId;
    }

    public void setAssetTypeId(UUID assetTypeId) {
	this.assetTypeId = assetTypeId;
    }

    /*
     * @see com.sitewhere.spi.asset.IAsset#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }
}