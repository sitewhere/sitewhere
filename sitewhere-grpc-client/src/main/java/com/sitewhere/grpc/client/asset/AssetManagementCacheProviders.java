/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.asset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.cache.CacheProvider;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.microservice.IMicroservice;

/**
 * Cache providers for asset management entities.
 * 
 * @author Derek
 */
public class AssetManagementCacheProviders {

    /** Cache id for asset by token cache */
    public static final String ID_ASSET_BY_TOKEN_CACHE = "atok";

    /**
     * Cache for assets by reference.
     * 
     * @author Derek
     */
    public static class AssetByTokenCache extends CacheProvider<String, IAsset> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(AssetByTokenCache.class);

	public AssetByTokenCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_ASSET_BY_TOKEN_CACHE, createOnStartup);
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