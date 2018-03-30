/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.tenant;

import java.util.UUID;

import com.sitewhere.grpc.client.cache.NearCacheManager;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Adds caching support to tenant management API channel.
 * 
 * @author Derek
 */
public class CachedTenantManagementApiChannel extends TenantManagementApiChannel {

    /** Manages local cache */
    private NearCacheManager nearCacheManager;

    /** Tenant cache */
    private ICacheProvider<String, ITenant> tenantByTokenCache;

    /** Tenant by id cache */
    private ICacheProvider<UUID, ITenant> tenantByIdCache;

    public CachedTenantManagementApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
	super(demux, microservice, host);
	this.nearCacheManager = new NearCacheManager(microservice, MicroserviceIdentifier.TenantManagement);
	this.tenantByTokenCache = new TenantManagementCacheProviders.TenantByTokenCache(nearCacheManager);
	this.tenantByIdCache = new TenantManagementCacheProviders.TenantByIdCache(nearCacheManager);
	getNearCacheManager().setCacheProviders(tenantByTokenCache, tenantByIdCache);
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
     * com.sitewhere.grpc.client.tenant.TenantManagementApiChannel#getTenant(java.
     * util.UUID)
     */
    @Override
    public ITenant getTenant(UUID id) throws SiteWhereException {
	ITenant tenant = getTenantByIdCache().getCacheEntry(null, id);
	if (tenant != null) {
	    getLogger().trace("Using cached information for tenant '" + id + "'.");
	    return tenant;
	} else {
	    getLogger().trace("No cached information for tenant '" + id + "'.");
	}
	return super.getTenant(id);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.tenant.TenantManagementApiChannel#getTenantByToken(
     * java.lang.String)
     */
    @Override
    public ITenant getTenantByToken(String token) throws SiteWhereException {
	ITenant tenant = getTenantByTokenCache().getCacheEntry(null, token);
	if (tenant != null) {
	    getLogger().trace("Using cached information for tenant '" + token + "'.");
	    return tenant;
	} else {
	    getLogger().trace("No cached information for tenant '" + token + "'.");
	}
	return super.getTenantByToken(token);
    }

    public NearCacheManager getNearCacheManager() {
	return nearCacheManager;
    }

    public void setNearCacheManager(NearCacheManager nearCacheManager) {
	this.nearCacheManager = nearCacheManager;
    }

    public ICacheProvider<String, ITenant> getTenantByTokenCache() {
	return tenantByTokenCache;
    }

    public void setTenantByTokenCache(ICacheProvider<String, ITenant> tenantByTokenCache) {
	this.tenantByTokenCache = tenantByTokenCache;
    }

    public ICacheProvider<UUID, ITenant> getTenantByIdCache() {
	return tenantByIdCache;
    }

    public void setTenantByIdCache(ICacheProvider<UUID, ITenant> tenantByIdCache) {
	this.tenantByIdCache = tenantByIdCache;
    }
}