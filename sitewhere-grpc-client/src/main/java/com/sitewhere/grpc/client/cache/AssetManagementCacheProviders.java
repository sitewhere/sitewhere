/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.cache;

import java.util.UUID;

import com.sitewhere.grpc.client.spi.cache.ICacheConfiguration;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;

/**
 * Cache providers for asset management entities.
 */
public class AssetManagementCacheProviders {

    /**
     * Cache for asset types by token.
     * 
     * @author Derek
     */
    public static class AssetTypeByTokenCache extends CacheProvider<String, IAssetType> {

	public AssetTypeByTokenCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.AssetTypeByToken, String.class, IAssetType.class, configuration);
	}
    }

    /**
     * Cache for asset types by id.
     * 
     * @author Derek
     */
    public static class AssetTypeByIdCache extends CacheProvider<UUID, IAssetType> {

	public AssetTypeByIdCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.AssetTypeById, UUID.class, IAssetType.class, configuration);
	}
    }

    /**
     * Cache for assets by token.
     * 
     * @author Derek
     */
    public static class AssetByTokenCache extends CacheProvider<String, IAsset> {

	public AssetByTokenCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.AssetByToken, String.class, IAsset.class, configuration);
	}
    }

    /**
     * Cache for assets by id.
     * 
     * @author Derek
     */
    public static class AssetByIdCache extends CacheProvider<UUID, IAsset> {

	public AssetByIdCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.AssetById, UUID.class, IAsset.class, configuration);
	}
    }
}