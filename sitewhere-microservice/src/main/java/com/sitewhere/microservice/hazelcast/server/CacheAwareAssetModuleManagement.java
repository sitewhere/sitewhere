/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.hazelcast.server;

import com.sitewhere.asset.AssetModuleManagementDecorator;
import com.sitewhere.grpc.client.asset.AssetManagementCacheProviders;
import com.sitewhere.rest.model.asset.DefaultAssetReferenceEncoder;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetModuleManagement;
import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.asset.IAssetReferenceEncoder;
import com.sitewhere.spi.cache.ICacheProvider;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Wraps {@link IAssetModuleManagement} implementation with cache support.
 * 
 * @author Derek
 */
public class CacheAwareAssetModuleManagement extends AssetModuleManagementDecorator {

    /** Reference encoder */
    private IAssetReferenceEncoder assetReferenceEncoder = new DefaultAssetReferenceEncoder();

    /** Asset by reference cache */
    private ICacheProvider<String, IAsset> assetByReferenceCache;

    public CacheAwareAssetModuleManagement(IAssetModuleManagement delegate, IMicroservice microservice) {
	super(delegate);
	this.assetByReferenceCache = new AssetManagementCacheProviders.AssetReferenceCache(microservice, true);
    }

    /*
     * @see
     * com.sitewhere.asset.AssetModuleManagementDecorator#getAsset(com.sitewhere.spi
     * .asset.IAssetReference)
     */
    @Override
    public IAsset getAsset(IAssetReference reference) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAsset result = super.getAsset(reference);
	String encoded = getAssetReferenceEncoder().encode(reference);
	if ((result != null) && (getAssetByReferenceCache().getCacheEntry(tenant, encoded) == null)) {
	    getAssetByReferenceCache().setCacheEntry(tenant, encoded, result);
	    getLogger().debug("Added asset to asset reference cache.");
	}
	return result;
    }

    public IAssetReferenceEncoder getAssetReferenceEncoder() {
	return assetReferenceEncoder;
    }

    public void setAssetReferenceEncoder(IAssetReferenceEncoder assetReferenceEncoder) {
	this.assetReferenceEncoder = assetReferenceEncoder;
    }

    public ICacheProvider<String, IAsset> getAssetByReferenceCache() {
	return assetByReferenceCache;
    }

    public void setAssetByReferenceCache(ICacheProvider<String, IAsset> assetByReferenceCache) {
	this.assetByReferenceCache = assetByReferenceCache;
    }
}