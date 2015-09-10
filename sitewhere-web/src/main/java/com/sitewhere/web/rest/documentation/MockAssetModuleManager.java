/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import com.sitewhere.server.asset.AssetModuleManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetModuleManager;

/**
 * Mock version of {@link IAssetModuleManager} that always returns the same asset.
 * 
 * @author Derek
 */
public class MockAssetModuleManager extends AssetModuleManager {

	/** Always return the same asset */
	private IAsset asset = ExampleData.ASSET_DEREK;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.server.asset.AssetModuleManager#getAssetById(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IAsset getAssetById(String assetModuleId, String id) throws SiteWhereException {
		return asset;
	}
}