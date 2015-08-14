/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.asset;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.AssetType;
import com.sitewhere.spi.asset.IAssetCategory;

/**
 * REST model implementation of {@link IAssetCategory}.
 * 
 * @author Derek
 */
public class AssetCategory implements IAssetCategory {

	/** Category id */
	private String id;

	/** Category name */
	private String name;

	/** Category asset type */
	private AssetType assetType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetCategory#getId()
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetCategory#getName()
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.asset.IAssetCategory#getAssetType()
	 */
	public AssetType getAssetType() {
		return assetType;
	}

	public void setAssetType(AssetType assetType) {
		this.assetType = assetType;
	}

	/**
	 * Create a copy of an SPI object. Used by web services for marshaling.
	 * 
	 * @param input
	 * @return
	 */
	public static AssetCategory copy(IAssetCategory input) throws SiteWhereException {
		AssetCategory category = new AssetCategory();
		category.setId(input.getId());
		category.setName(input.getName());
		category.setAssetType(input.getAssetType());
		return category;
	}
}