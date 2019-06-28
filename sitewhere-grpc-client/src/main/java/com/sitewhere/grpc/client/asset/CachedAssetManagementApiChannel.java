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
import com.sitewhere.grpc.client.cache.CacheConfiguration;
import com.sitewhere.grpc.client.spi.cache.ICacheConfiguration;
import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Adds caching support to asset management API channel.
 * 
 * @author Derek
 */
public class CachedAssetManagementApiChannel extends AssetManagementApiChannel {

    /** Asset type cache */
    private ICacheProvider<String, IAssetType> assetTypeCache;

    /** Asset type by id cache */
    private ICacheProvider<UUID, IAssetType> assetTypeByIdCache;

    /** Asset cache */
    private ICacheProvider<String, IAsset> assetCache;

    /** Asset by id cache */
    private ICacheProvider<UUID, IAsset> assetByIdCache;

    public CachedAssetManagementApiChannel(IInstanceSettings settings, CacheSettings cache) {
	super(settings);
	this.assetTypeCache = new AssetManagementCacheProviders.AssetTypeByTokenCache(
		cache.getAssetTypeConfiguration());
	this.assetTypeByIdCache = new AssetManagementCacheProviders.AssetTypeByIdCache(
		cache.getAssetTypeConfiguration());
	this.assetCache = new AssetManagementCacheProviders.AssetByTokenCache(cache.getAssetConfiguration());
	this.assetByIdCache = new AssetManagementCacheProviders.AssetByIdCache(cache.getAssetConfiguration());
    }

    /*
     * @see
     * com.sitewhere.grpc.client.ApiChannel#initialize(com.sitewhere.spi.server.
     * lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	initializeNestedComponent(getAssetTypeCache(), monitor, true);
	initializeNestedComponent(getAssetTypeByIdCache(), monitor, true);
	initializeNestedComponent(getAssetCache(), monitor, true);
	initializeNestedComponent(getAssetByIdCache(), monitor, true);
	super.initialize(monitor);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.ApiChannel#start(com.sitewhere.spi.server.lifecycle
     * .ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	startNestedComponent(getAssetTypeCache(), monitor, true);
	startNestedComponent(getAssetTypeByIdCache(), monitor, true);
	startNestedComponent(getAssetCache(), monitor, true);
	startNestedComponent(getAssetByIdCache(), monitor, true);
	super.start(monitor);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.ApiChannel#stop(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	stopNestedComponent(getAssetTypeCache(), monitor);
	stopNestedComponent(getAssetTypeByIdCache(), monitor);
	stopNestedComponent(getAssetCache(), monitor);
	stopNestedComponent(getAssetByIdCache(), monitor);
	super.stop(monitor);
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
	if (asset == null) {
	    asset = super.getAsset(assetId);
	    getAssetByIdCache().setCacheEntry(tenant, assetId, asset);
	}
	return asset;
    }

    /*
     * @see
     * com.sitewhere.spi.asset.IAssetManagement#getAssetByToken(java.lang.String)
     */
    @Override
    public IAsset getAssetByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAsset asset = getAssetCache().getCacheEntry(tenant, token);
	if (asset == null) {
	    asset = super.getAssetByToken(token);
	    getAssetCache().setCacheEntry(tenant, token, asset);
	}
	return asset;
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
	if (assetType == null) {
	    assetType = super.getAssetType(assetTypeId);
	    getAssetTypeByIdCache().setCacheEntry(tenant, assetTypeId, assetType);
	}
	return assetType;
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
	if (assetType == null) {
	    assetType = super.getAssetTypeByToken(token);
	    getAssetTypeCache().setCacheEntry(tenant, token, assetType);
	}
	return assetType;
    }

    /**
     * Contains default cache settings for asset management entities.
     */
    public static class CacheSettings {

	/** Cache configuraton for asset types */
	private ICacheConfiguration assetTypeConfiguration = new CacheConfiguration(1000, 60);

	/** Cache configuraton for assets */
	private ICacheConfiguration assetConfiguration = new CacheConfiguration(10000, 60);

	public ICacheConfiguration getAssetTypeConfiguration() {
	    return assetTypeConfiguration;
	}

	public void setAssetTypeConfiguration(ICacheConfiguration assetTypeConfiguration) {
	    this.assetTypeConfiguration = assetTypeConfiguration;
	}

	public ICacheConfiguration getAssetConfiguration() {
	    return assetConfiguration;
	}

	public void setAssetConfiguration(ICacheConfiguration assetConfiguration) {
	    this.assetConfiguration = assetConfiguration;
	}
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