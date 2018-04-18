/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.asset;

import java.util.UUID;

import com.sitewhere.grpc.client.cache.AssetManagementCacheProviders;
import com.sitewhere.grpc.client.cache.CacheUtils;
import com.sitewhere.grpc.client.cache.NearCacheManager;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Adds caching support to asset management API channel.
 * 
 * @author Derek
 */
public class CachedAssetManagementApiChannel extends AssetManagementApiChannel {

    /** Manages local cache */
    private NearCacheManager nearCacheManager;

    /** Asset type cache */
    private ICacheProvider<String, IAssetType> assetTypeCache;

    /** Asset type by id cache */
    private ICacheProvider<UUID, IAssetType> assetTypeByIdCache;

    /** Asset cache */
    private ICacheProvider<String, IAsset> assetCache;

    /** Asset by id cache */
    private ICacheProvider<UUID, IAsset> assetByIdCache;

    public CachedAssetManagementApiChannel(IApiDemux<?> demux, String host, int port) {
	super(demux, host, port);
	this.nearCacheManager = new NearCacheManager(MicroserviceIdentifier.AssetManagement);
	this.assetTypeCache = new AssetManagementCacheProviders.AssetTypeByTokenCache(nearCacheManager);
	this.assetTypeByIdCache = new AssetManagementCacheProviders.AssetTypeByIdCache(nearCacheManager);
	this.assetCache = new AssetManagementCacheProviders.AssetByTokenCache(nearCacheManager);
	this.assetByIdCache = new AssetManagementCacheProviders.AssetByIdCache(nearCacheManager);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.initialize(monitor);

	// Initialize near cache manager.
	initializeNestedComponent(getNearCacheManager(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Start near cache manager.
	startNestedComponent(getNearCacheManager(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);

	// Stop near cache manager.
	stopNestedComponent(getNearCacheManager(), monitor);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.asset.AssetManagementApiChannel#getAsset(java.util.
     * UUID)
     */
    @Override
    public IAsset getAsset(UUID assetId) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAsset asset = getAssetByIdCache().getCacheEntry(tenant, assetId);
	if (asset != null) {
	    CacheUtils.logCacheHit(asset);
	    return asset;
	} else {
	    getLogger().debug("No cached information for asset id '" + assetId + "'.");
	}
	return super.getAsset(assetId);
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#getAssetByToken(java.lang.String)
     */
    @Override
    public IAsset getAssetByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAsset asset = getAssetCache().getCacheEntry(tenant, token);
	if (asset != null) {
	    CacheUtils.logCacheHit(asset);
	    return asset;
	} else {
	    getLogger().debug("No cached information for asset token '" + token + "'.");
	}
	return super.getAssetByToken(token);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.asset.AssetManagementApiChannel#getAssetType(java.
     * util.UUID)
     */
    @Override
    public IAssetType getAssetType(UUID assetTypeId) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAssetType assetType = getAssetTypeByIdCache().getCacheEntry(tenant, assetTypeId);
	if (assetType != null) {
	    CacheUtils.logCacheHit(assetType);
	    return assetType;
	} else {
	    getLogger().debug("No cached information for asset type id '" + assetTypeId + "'.");
	}
	return super.getAssetType(assetTypeId);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.asset.AssetManagementApiChannel#getAssetTypeByToken
     * (java.lang.String)
     */
    @Override
    public IAssetType getAssetTypeByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAssetType assetType = getAssetTypeCache().getCacheEntry(tenant, token);
	if (assetType != null) {
	    CacheUtils.logCacheHit(assetType);
	    return assetType;
	} else {
	    getLogger().debug("No cached information for asset type token '" + token + "'.");
	}
	return super.getAssetTypeByToken(token);
    }

    public NearCacheManager getNearCacheManager() {
	return nearCacheManager;
    }

    public void setNearCacheManager(NearCacheManager nearCacheManager) {
	this.nearCacheManager = nearCacheManager;
    }

    public ICacheProvider<String, IAssetType> getAssetTypeCache() {
	return assetTypeCache;
    }

    public void setAssetTypeCache(ICacheProvider<String, IAssetType> assetTypeCache) {
	this.assetTypeCache = assetTypeCache;
    }

    public ICacheProvider<UUID, IAssetType> getAssetTypeByIdCache() {
	return assetTypeByIdCache;
    }

    public void setAssetTypeByIdCache(ICacheProvider<UUID, IAssetType> assetTypeByIdCache) {
	this.assetTypeByIdCache = assetTypeByIdCache;
    }

    public ICacheProvider<String, IAsset> getAssetCache() {
	return assetCache;
    }

    public void setAssetCache(ICacheProvider<String, IAsset> assetCache) {
	this.assetCache = assetCache;
    }

    public ICacheProvider<UUID, IAsset> getAssetByIdCache() {
	return assetByIdCache;
    }

    public void setAssetByIdCache(ICacheProvider<UUID, IAsset> assetByIdCache) {
	this.assetByIdCache = assetByIdCache;
    }
}