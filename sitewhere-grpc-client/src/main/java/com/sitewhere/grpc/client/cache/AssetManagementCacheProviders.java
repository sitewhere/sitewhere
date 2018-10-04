/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.cache;

import java.util.UUID;

import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;

/**
 * Cache providers for asset management entities.
 * 
 * @author Derek
 */
public class AssetManagementCacheProviders {

    /**
     * Cache for asset types by token.
     * 
     * @author Derek
     */
    public static class AssetTypeByTokenCache extends CacheProvider<String, IAssetType> {

	public AssetTypeByTokenCache() {
	    super(CacheIdentifier.AssetTypeByToken, String.class, IAssetType.class, 1000, 60);
	}
    }

    /**
     * Cache for asset types by id.
     * 
     * @author Derek
     */
    public static class AssetTypeByIdCache extends CacheProvider<UUID, IAssetType> {

	public AssetTypeByIdCache() {
	    super(CacheIdentifier.AssetTypeById, UUID.class, IAssetType.class, 1000, 60);
	}
    }

    /**
     * Cache for assets by token.
     * 
     * @author Derek
     */
    public static class AssetByTokenCache extends CacheProvider<String, IAsset> {

	public AssetByTokenCache() {
	    super(CacheIdentifier.AssetByToken, String.class, IAsset.class, 10000, 60);
	}
    }

    /**
     * Cache for assets by id.
     * 
     * @author Derek
     */
    public static class AssetByIdCache extends CacheProvider<UUID, IAsset> {

	public AssetByIdCache() {
	    super(CacheIdentifier.AssetById, UUID.class, IAsset.class, 10000, 60);
	}
    }
}