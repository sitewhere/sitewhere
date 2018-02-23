/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.hazelcast.server;

import java.util.UUID;

import com.sitewhere.grpc.client.tenant.TenantManagementCacheProviders;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.ICacheProvider;
import com.sitewhere.spi.microservice.IMicroservice;
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
    private ICacheProvider<String, ITenant> tenantCache;

    public CacheAwareTenantManagement(ITenantManagement delegate, IMicroservice microservice) {
	super(delegate);
	this.tenantCache = new TenantManagementCacheProviders.TenantCache(microservice, true);
    }

    /*
     * @see
     * com.sitewhere.tenant.TenantManagementDecorator#createTenant(com.sitewhere.spi
     * .tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
	ITenant result = super.createTenant(request);
	getTenantCache().setCacheEntry(null, result.getToken(), result);
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
	getTenantCache().setCacheEntry(null, result.getToken(), result);
	getLogger().trace("Updated tenant in cache.");
	return result;
    }

    /*
     * @see com.sitewhere.tenant.TenantManagementDecorator#getTenant(java.util.UUID)
     */
    @Override
    public ITenant getTenant(UUID id) throws SiteWhereException {
	ITenant result = super.getTenant(id);
	if ((result != null) && (getTenantCache().getCacheEntry(null, result.getToken()) == null)) {
	    getTenantCache().setCacheEntry(null, result.getToken(), result);
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
	if ((result != null) && (getTenantCache().getCacheEntry(null, result.getToken()) == null)) {
	    getTenantCache().setCacheEntry(null, result.getToken(), result);
	    getLogger().trace("Added tenant to cache.");
	}
	return result;
    }

    /*
     * @see
     * com.sitewhere.tenant.TenantManagementDecorator#deleteTenant(java.util.UUID,
     * boolean)
     */
    @Override
    public ITenant deleteTenant(UUID tenantId, boolean force) throws SiteWhereException {
	ITenant result = super.deleteTenant(tenantId, force);
	getTenantCache().removeCacheEntry(null, result.getToken());
	getLogger().trace("Removed tenant from cache.");
	return result;
    }

    public ICacheProvider<String, ITenant> getTenantCache() {
	return tenantCache;
    }

    public void setTenantCache(ICacheProvider<String, ITenant> tenantCache) {
	this.tenantCache = tenantCache;
    }
}