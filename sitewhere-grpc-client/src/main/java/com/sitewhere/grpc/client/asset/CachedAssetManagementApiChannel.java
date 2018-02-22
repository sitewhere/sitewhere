/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.asset;

import com.sitewhere.grpc.client.cache.CacheUtils;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.cache.ICacheProvider;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Adds caching support to asset management API channel.
 * 
 * @author Derek
 */
public class CachedAssetManagementApiChannel extends AssetManagementApiChannel {

    /** Asset by reference cache */
    private ICacheProvider<String, IAsset> assetByTokenCache;

    public CachedAssetManagementApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
	super(demux, microservice, host);
	this.assetByTokenCache = new AssetManagementCacheProviders.AssetByTokenCache(microservice, false);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#getAssetByToken(java.lang.String)
     */
    @Override
    public IAsset getAssetByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAsset asset = getAssetByTokenCache().getCacheEntry(tenant, token);
	if (asset != null) {
	    CacheUtils.logCacheHit(asset);
	    return asset;
	} else {
	    getLogger().debug("No cached information for asset token '" + token + "'.");
	}
	return super.getAssetByToken(token);
    }

    public ICacheProvider<String, IAsset> getAssetByTokenCache() {
	return assetByTokenCache;
    }

    public void setAssetByTokenCache(ICacheProvider<String, IAsset> assetByTokenCache) {
	this.assetByTokenCache = assetByTokenCache;
    }
}