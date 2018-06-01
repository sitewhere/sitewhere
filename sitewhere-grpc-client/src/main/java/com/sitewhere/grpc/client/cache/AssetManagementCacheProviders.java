/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.cache;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.microservice.hazelcast.IHazelcastProvider;

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

	/** Static logger instance */
	private static Logger LOGGER = LoggerFactory.getLogger(AssetByTokenCache.class);

	public AssetTypeByTokenCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.AssetTypeByToken, 1000);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
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
	private static Logger LOGGER = LoggerFactory.getLogger(AssetByTokenCache.class);

	public AssetTypeByIdCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.AssetTypeById, 1000);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
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
	private static Logger LOGGER = LoggerFactory.getLogger(AssetByTokenCache.class);

	public AssetByTokenCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.AssetByToken, 5000);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
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
	private static Logger LOGGER = LoggerFactory.getLogger(AssetByTokenCache.class);

	public AssetByIdCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.AssetById, 5000);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
	    return LOGGER;
	}
    }
}