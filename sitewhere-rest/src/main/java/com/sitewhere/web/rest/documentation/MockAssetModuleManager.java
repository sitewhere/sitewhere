/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.HashMap;
import java.util.Map;

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

	/** Static map of available assets */
	private Map<String, IAsset> assets = new HashMap<String, IAsset>();

	public MockAssetModuleManager() {
		assets.put(ExampleData.ASSET_CATERPILLAR.getId(), ExampleData.ASSET_CATERPILLAR);
		assets.put(ExampleData.ASSET_DEREK.getId(), ExampleData.ASSET_DEREK);
		assets.put(ExampleData.ASSET_MARTIN.getId(), ExampleData.ASSET_MARTIN);
		assets.put(ExampleData.ASSET_CATERPILLAR.getId(), ExampleData.ASSET_CATERPILLAR);
		assets.put(ExampleData.ASSET_MEITRACK.getId(), ExampleData.ASSET_MEITRACK);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.server.asset.AssetModuleManager#getAssetById(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IAsset getAssetById(String assetModuleId, String id) throws SiteWhereException {
		IAsset match = assets.get(id);
		if (match == null) {
			throw new SiteWhereException("Missing sample asset: assetModuleId:" + assetModuleId + " assetId:"
					+ id);
		}
		return match;
	}
}