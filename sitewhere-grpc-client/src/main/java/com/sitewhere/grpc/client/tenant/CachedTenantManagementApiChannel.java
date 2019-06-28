/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.tenant;

import java.util.UUID;

import com.sitewhere.grpc.client.cache.CacheConfiguration;
import com.sitewhere.grpc.client.spi.cache.ICacheConfiguration;
import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Adds caching support to tenant management API channel.
 * 
 * @author Derek
 */
public class CachedTenantManagementApiChannel extends TenantManagementApiChannel {

    /** Tenant cache */
    private ICacheProvider<String, ITenant> tenantByTokenCache;

    /** Tenant by id cache */
    private ICacheProvider<UUID, ITenant> tenantByIdCache;

    public CachedTenantManagementApiChannel(IInstanceSettings settings, CacheSettings cache) {
	super(settings);
	this.tenantByTokenCache = new TenantManagementCacheProviders.TenantByTokenCache(cache.getTenantConfiguration());
	this.tenantByIdCache = new TenantManagementCacheProviders.TenantByIdCache(cache.getTenantConfiguration());
    }

    /*
     * @see
     * com.sitewhere.grpc.client.ApiChannel#initialize(com.sitewhere.spi.server.
     * lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	initializeNestedComponent(getTenantByTokenCache(), monitor, true);
	initializeNestedComponent(getTenantByIdCache(), monitor, true);
	super.initialize(monitor);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.ApiChannel#start(com.sitewhere.spi.server.lifecycle
     * .ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	startNestedComponent(getTenantByTokenCache(), monitor, true);
	startNestedComponent(getTenantByIdCache(), monitor, true);
	super.start(monitor);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.ApiChannel#stop(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	stopNestedComponent(getTenantByTokenCache(), monitor);
	stopNestedComponent(getTenantByIdCache(), monitor);
	super.stop(monitor);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.tenant.TenantManagementApiChannel#getTenant(java.
     * util.UUID)
     */
    @Override
    public ITenant getTenant(UUID id) throws SiteWhereException {
	ITenant tenant = getTenantByIdCache().getCacheEntry(null, id);
	if (tenant == null) {
	    tenant = super.getTenant(id);
	    getTenantByIdCache().setCacheEntry(null, id, tenant);
	}
	return tenant;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.tenant.TenantManagementApiChannel#getTenantByToken(
     * java.lang.String)
     */
    @Override
    public ITenant getTenantByToken(String token) throws SiteWhereException {
	ITenant tenant = getTenantByTokenCache().getCacheEntry(null, token);
	if (tenant == null) {
	    tenant = super.getTenantByToken(token);
	    getTenantByTokenCache().setCacheEntry(null, token, tenant);
	}
	return tenant;
    }

    /**
     * Contains default cache settings for tenant management entities.
     */
    public static class CacheSettings {

	/** Cache configuraton for tenants */
	private ICacheConfiguration tenantConfiguration = new CacheConfiguration(1000, 60);

	public ICacheConfiguration getTenantConfiguration() {
	    return tenantConfiguration;
	}

	public void setTenantConfiguration(ICacheConfiguration tenantConfiguration) {
	    this.tenantConfiguration = tenantConfiguration;
	}
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