/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.asset.request;

import com.sitewhere.spi.asset.AssetType;

/**
 * Contains information needed to create an asset category.
 * 
 * @author Derek
 */
public interface IAssetCategoryCreateRequest {

	/**
	 * Get category id.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * Get category name.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Get type of assets held in category.
	 * 
	 * @return
	 */
	public AssetType getAssetType();
}