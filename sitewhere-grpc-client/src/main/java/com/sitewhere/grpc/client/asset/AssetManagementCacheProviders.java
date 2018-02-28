/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.asset;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.cache.CacheProvider;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.microservice.IMicroservice;

/**
 * Cache providers for asset management entities.
 * 
 * @author Derek
 */
public class AssetManagementCacheProviders {

    /** Cache id for asset type by token cache */
    public static final String ID_ASSET_TYPE_CACHE = "asset_type_by_token";

    /** Cache id for asset type by id cache */
    public static final String ID_ASSET_TYPE_ID_CACHE = "asset_type_by_id";

    /** Cache id for asset by token cache */
    public static final String ID_ASSET_CACHE = "asset_by_token";

    /** Cache id for asset by id cache */
    public static final String ID_ASSET_ID_CACHE = "asset_by_id";

    /**
     * Cache for asset types by token.
     * 
     * @author Derek
     */
    public static class AssetTypeByTokenCache extends CacheProvider<String, IAssetType> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(AssetByTokenCache.class);

	public AssetTypeByTokenCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_ASSET_TYPE_CACHE, createOnStartup);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }

    /**
     * Cache for asset types by id.
     * 
     * @author Derek
     */
    public static class AssetTypeByIdCache extends CacheProvider<UUID, IAssetType> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(AssetByTokenCache.class);

	public AssetTypeByIdCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_ASSET_TYPE_ID_CACHE, createOnStartup);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }

    /**
     * Cache for assets by token.
     * 
     * @author Derek
     */
    public static class AssetByTokenCache extends CacheProvider<String, IAsset> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(AssetByTokenCache.class);

	public AssetByTokenCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_ASSET_CACHE, createOnStartup);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }

    /**
     * Cache for assets by id.
     * 
     * @author Derek
     */
    public static class AssetByIdCache extends CacheProvider<UUID, IAsset> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(AssetByTokenCache.class);

	public AssetByIdCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_ASSET_ID_CACHE, createOnStartup);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }
}