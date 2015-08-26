/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.hbase.asset;

import com.sitewhere.hbase.IHBaseContext;
import com.sitewhere.hbase.uid.UniqueIdCounterMap;
import com.sitewhere.hbase.uid.UniqueIdType;
import com.sitewhere.spi.SiteWhereException;

/**
 * Singleton that keeps up with asset management entities.
 * 
 * @author Derek
 */
public class AssetIdManager {

	/** Singleton instance */
	private static AssetIdManager SINGLETON;

	/** Manager for category ids */
	private UniqueIdCounterMap categoryKeys;

	private AssetIdManager() {
	}

	public static AssetIdManager getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new AssetIdManager();
		}
		return SINGLETON;
	}

	/**
	 * Load existing keys from table.
	 * 
	 * @param context
	 * @throws SiteWhereException
	 */
	public void load(IHBaseContext context) throws SiteWhereException {
		categoryKeys =
				new UniqueIdCounterMap(context, UniqueIdType.AssetKey.getIndicator(),
						UniqueIdType.AssetValue.getIndicator());
		categoryKeys.refresh();
	}

	/**
	 * Get asset key manager.
	 * 
	 * @return
	 */
	public UniqueIdCounterMap getAssetKeys() {
		return categoryKeys;
	}
}