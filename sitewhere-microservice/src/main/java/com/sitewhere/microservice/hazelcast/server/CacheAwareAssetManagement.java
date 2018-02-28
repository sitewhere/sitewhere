/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.hazelcast.server;

import java.util.UUID;

import com.sitewhere.asset.AssetManagementDecorator;
import com.sitewhere.grpc.client.asset.AssetManagementCacheProviders;
import com.sitewhere.grpc.client.cache.CacheUtils;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.asset.request.IAssetCreateRequest;
import com.sitewhere.spi.asset.request.IAssetTypeCreateRequest;
import com.sitewhere.spi.cache.ICacheProvider;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Wraps {@link IAssetManagement} implementation with cache support.
 * 
 * @author Derek
 */
public class CacheAwareAssetManagement extends AssetManagementDecorator {

    /** Asset type cache */
    private ICacheProvider<String, IAssetType> assetTypeCache;

    /** Asset type by id cache */
    private ICacheProvider<UUID, IAssetType> assetTypeByIdCache;

    /** Asset cache */
    private ICacheProvider<String, IAsset> assetCache;

    /** Asset by id cache */
    private ICacheProvider<UUID, IAsset> assetByIdCache;

    public CacheAwareAssetManagement(IAssetManagement delegate, IMicroservice microservice) {
	super(delegate);
	this.assetTypeCache = new AssetManagementCacheProviders.AssetTypeByTokenCache(microservice, true);
	this.assetTypeByIdCache = new AssetManagementCacheProviders.AssetTypeByIdCache(microservice, true);
	this.assetCache = new AssetManagementCacheProviders.AssetByTokenCache(microservice, true);
	this.assetByIdCache = new AssetManagementCacheProviders.AssetByIdCache(microservice, true);
    }

    /*
     * @see
     * com.sitewhere.asset.AssetManagementDecorator#createAsset(com.sitewhere.spi.
     * asset.request.IAssetCreateRequest)
     */
    @Override
    public IAsset createAsset(IAssetCreateRequest request) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAsset result = super.createAsset(request);
	getAssetCache().setCacheEntry(tenant, result.getToken(), result);
	getAssetByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see com.sitewhere.asset.AssetManagementDecorator#updateAsset(java.util.UUID,
     * com.sitewhere.spi.asset.request.IAssetCreateRequest)
     */
    @Override
    public IAsset updateAsset(UUID assetId, IAssetCreateRequest request) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAsset result = super.updateAsset(assetId, request);
	getAssetCache().setCacheEntry(tenant, result.getToken(), result);
	getAssetByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see com.sitewhere.asset.AssetManagementDecorator#getAsset(java.util.UUID)
     */
    @Override
    public IAsset getAsset(UUID assetId) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAsset result = super.getAsset(assetId);
	if ((result != null) && (getAssetByIdCache().getCacheEntry(tenant, assetId) == null)) {
	    getAssetCache().setCacheEntry(tenant, result.getToken(), result);
	    getAssetByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see com.sitewhere.asset.AssetManagementDecorator#getAssetByToken(java.lang.
     * String)
     */
    @Override
    public IAsset getAssetByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAsset result = super.getAssetByToken(token);
	if ((result != null) && (getAssetCache().getCacheEntry(tenant, token) == null)) {
	    getAssetCache().setCacheEntry(tenant, result.getToken(), result);
	    getAssetByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see com.sitewhere.asset.AssetManagementDecorator#deleteAsset(java.util.UUID,
     * boolean)
     */
    @Override
    public IAsset deleteAsset(UUID assetId, boolean force) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAsset result = super.deleteAsset(assetId, force);
	getAssetCache().removeCacheEntry(tenant, result.getToken());
	getAssetByIdCache().removeCacheEntry(tenant, result.getId());
	CacheUtils.logCacheRemoved(result.getToken());
	return result;
    }

    /*
     * @see
     * com.sitewhere.asset.AssetManagementDecorator#createAssetType(com.sitewhere.
     * spi.asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType createAssetType(IAssetTypeCreateRequest request) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAssetType result = super.createAssetType(request);
	getAssetTypeCache().setCacheEntry(tenant, result.getToken(), result);
	getAssetTypeByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see
     * com.sitewhere.asset.AssetManagementDecorator#updateAssetType(java.util.UUID,
     * com.sitewhere.spi.asset.request.IAssetTypeCreateRequest)
     */
    @Override
    public IAssetType updateAssetType(UUID assetTypeId, IAssetTypeCreateRequest request) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAssetType result = super.updateAssetType(assetTypeId, request);
	getAssetTypeCache().setCacheEntry(tenant, result.getToken(), result);
	getAssetTypeByIdCache().setCacheEntry(tenant, result.getId(), result);
	CacheUtils.logCacheUpdated(result);
	return result;
    }

    /*
     * @see
     * com.sitewhere.asset.AssetManagementDecorator#getAssetType(java.util.UUID)
     */
    @Override
    public IAssetType getAssetType(UUID assetTypeId) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAssetType result = super.getAssetType(assetTypeId);
	if ((result != null) && (getAssetTypeByIdCache().getCacheEntry(tenant, assetTypeId) == null)) {
	    getAssetTypeCache().setCacheEntry(tenant, result.getToken(), result);
	    getAssetTypeByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.asset.AssetManagementDecorator#getAssetTypeByToken(java.lang.
     * String)
     */
    @Override
    public IAssetType getAssetTypeByToken(String token) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAssetType result = super.getAssetTypeByToken(token);
	if ((result != null) && (getAssetTypeCache().getCacheEntry(tenant, token) == null)) {
	    getAssetTypeCache().setCacheEntry(tenant, result.getToken(), result);
	    getAssetTypeByIdCache().setCacheEntry(tenant, result.getId(), result);
	    CacheUtils.logCacheUpdated(result);
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.asset.AssetManagementDecorator#deleteAssetType(java.util.UUID,
     * boolean)
     */
    @Override
    public IAssetType deleteAssetType(UUID assetTypeId, boolean force) throws SiteWhereException {
	ITenant tenant = UserContextManager.getCurrentTenant(true);
	IAssetType result = super.deleteAssetType(assetTypeId, force);
	getAssetTypeCache().removeCacheEntry(tenant, result.getToken());
	getAssetTypeByIdCache().removeCacheEntry(tenant, result.getId());
	CacheUtils.logCacheRemoved(result.getToken());
	return result;
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