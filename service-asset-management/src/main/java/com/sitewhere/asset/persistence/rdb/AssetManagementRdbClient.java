/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.persistence.rdb;

import com.sitewhere.asset.persistence.rdb.repository.AssetRepository;
import com.sitewhere.asset.persistence.rdb.repository.AssetTypeRepository;
import com.sitewhere.rdb.spi.IRdbClient;

/**
 * Client used for accessing RDB entities for asset management.
 */
public class AssetManagementRdbClient implements IRdbClient {

    public AssetRepository getAssetRepository() {
	return null;
    }

    public AssetTypeRepository getAssetTypeRepository() {
	return null;
    }
}
