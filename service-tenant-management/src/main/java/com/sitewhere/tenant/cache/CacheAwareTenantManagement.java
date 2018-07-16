/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.cache;

import java.util.UUID;

import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.grpc.client.tenant.TenantManagementCacheProviders;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.ICachingMicroservice;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;
import com.sitewhere.tenant.TenantManagementDecorator;

/**
 * Wraps {@link ITenantManagement} implementation with cache support.
 * 
 * @author Derek
 */
public class CacheAwareTenantManagement extends TenantManagementDecorator {

    /** Tenant cache */
    private ICacheProvider<String, ITenant> tenantByTokenCache;

    public CacheAwareTenantManagement(ITenantManagement delegate, ICachingMicroservice microservice) {
	super(delegate);
	this.tenantByTokenCache = new TenantManagementCacheProviders.TenantByTokenCache(
		microservice.getHazelcastManager());
    }

    /*
     * @see
     * com.sitewhere.tenant.TenantManagementDecorator#createTenant(com.sitewhere.spi
     * .tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
	ITenant result = super.createTenant(request);
	getTenantByTokenCache().setCacheEntry(null, result.getToken(), result);
	getLogger().trace("Added created tenant to cache.");
	return result;
    }

    /*
     * @see
     * com.sitewhere.tenant.TenantManagementDecorator#updateTenant(java.util.UUID,
     * com.sitewhere.spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant updateTenant(UUID id, ITenantCreateRequest request) throws SiteWhereException {
	ITenant result = super.updateTenant(id, request);
	getTenantByTokenCache().setCacheEntry(null, result.getToken(), result);
	getLogger().trace("Updated tenant in cache.");
	return result;
    }

    /*
     * @see com.sitewhere.tenant.TenantManagementDecorator#getTenant(java.util.UUID)
     */
    @Override
    public ITenant getTenant(UUID id) throws SiteWhereException {
	ITenant result = super.getTenant(id);
	if ((result != null) && (getTenantByTokenCache().getCacheEntry(null, result.getToken()) == null)) {
	    getTenantByTokenCache().setCacheEntry(null, result.getToken(), result);
	    getLogger().trace("Added tenant to cache.");
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.tenant.TenantManagementDecorator#getTenantByToken(java.lang.
     * String)
     */
    @Override
    public ITenant getTenantByToken(String token) throws SiteWhereException {
	ITenant result = super.getTenantByToken(token);
	if ((result != null) && (getTenantByTokenCache().getCacheEntry(null, result.getToken()) == null)) {
	    getTenantByTokenCache().setCacheEntry(null, result.getToken(), result);
	    getLogger().trace("Added tenant to cache.");
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.tenant.TenantManagementDecorator#deleteTenant(java.util.UUID)
     */
    @Override
    public ITenant deleteTenant(UUID tenantId) throws SiteWhereException {
	ITenant result = super.deleteTenant(tenantId);
	getTenantByTokenCache().removeCacheEntry(null, result.getToken());
	getLogger().trace("Removed tenant from cache.");
	return result;
    }

    public ICacheProvider<String, ITenant> getTenantByTokenCache() {
	return tenantByTokenCache;
    }

    public void setTenantByTokenCache(ICacheProvider<String, ITenant> tenantByTokenCache) {
	this.tenantByTokenCache = tenantByTokenCache;
    }
}