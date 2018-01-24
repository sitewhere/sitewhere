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
import com.sitewhere.rest.model.asset.DefaultAssetReferenceEncoder;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.asset.IAssetReferenceEncoder;
import com.sitewhere.spi.cache.ICacheProvider;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Adds caching support to asset management API channel.
 * 
 * @author Derek
 */
public class CachedAssetManagementApiChannel extends AssetManagementApiChannel {

    /** Reference encoder */
    private IAssetReferenceEncoder assetReferenceEncoder = new DefaultAssetReferenceEncoder();

    /** Asset by reference cache */
    private ICacheProvider<String, IAsset> assetByReferenceCache;

    public CachedAssetManagementApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
	super(demux, microservice, host);
	this.assetByReferenceCache = new AssetManagementCacheProviders.AssetReferenceCache(microservice, false);
    }

    /*
     * @see com.sitewhere.grpc.client.asset.AssetManagementApiChannel#getAsset(com.
     * sitewhere.spi.asset.IAssetReference)
     */
    @Override
    public IAsset getAsset(IAssetReference reference) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	String encoded = getAssetReferenceEncoder().encode(reference);
	IAsset asset = getAssetByReferenceCache().getCacheEntry(tenant, encoded);
	if (asset != null) {
	    CacheUtils.logCacheHit(asset);
	    return asset;
	} else {
	    getLogger().debug("No cached information for asset reference '" + encoded + "'.");
	}
	return super.getAsset(reference);
    }

    public IAssetReferenceEncoder getAssetReferenceEncoder() {
	return assetReferenceEncoder;
    }

    public void setAssetReferenceEncoder(IAssetReferenceEncoder assetReferenceEncoder) {
	this.assetReferenceEncoder = assetReferenceEncoder;
    }

    protected ICacheProvider<String, IAsset> getAssetByReferenceCache() {
	return assetByReferenceCache;
    }

    protected void setAssetByReferenceCache(ICacheProvider<String, IAsset> assetByReferenceCache) {
	this.assetByReferenceCache = assetByReferenceCache;
    }
}