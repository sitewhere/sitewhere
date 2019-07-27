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
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.tenant.ITenantSearchCriteria;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Adds caching support to tenant management API.
 */
public class CachedTenantManagement extends TenantEngineLifecycleComponent implements ITenantManagement {

    /** Wrapped API */
    private ITenantManagement wrapped;

    /** Tenant cache */
    private ICacheProvider<String, ITenant> tenantByTokenCache;

    /** Tenant by id cache */
    private ICacheProvider<UUID, ITenant> tenantByIdCache;

    public CachedTenantManagement(ITenantManagement wrapped, CacheSettings cache) {
	this.wrapped = wrapped;
	this.tenantByTokenCache = new TenantManagementCacheProviders.TenantByTokenCache(cache.getTenantConfiguration());
	this.tenantByIdCache = new TenantManagementCacheProviders.TenantByIdCache(cache.getTenantConfiguration());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	initializeNestedComponent(getWrapped(), monitor, true);
	initializeNestedComponent(getTenantByTokenCache(), monitor, true);
	initializeNestedComponent(getTenantByIdCache(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	startNestedComponent(getWrapped(), monitor, true);
	startNestedComponent(getTenantByTokenCache(), monitor, true);
	startNestedComponent(getTenantByIdCache(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	stopNestedComponent(getWrapped(), monitor);
	stopNestedComponent(getTenantByTokenCache(), monitor);
	stopNestedComponent(getTenantByIdCache(), monitor);
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
	    tenant = getWrapped().getTenant(id);
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
	    tenant = getWrapped().getTenantByToken(token);
	    getTenantByTokenCache().setCacheEntry(null, token, tenant);
	}
	return tenant;
    }

    /*
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#createTenant(com.sitewhere.spi.
     * tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
	return getWrapped().createTenant(request);
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenantManagement#updateTenant(java.util.UUID,
     * com.sitewhere.spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant updateTenant(UUID id, ITenantCreateRequest request) throws SiteWhereException {
	return getWrapped().updateTenant(id, request);
    }

    /*
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#listTenants(com.sitewhere.spi.
     * search.tenant.ITenantSearchCriteria)
     */
    @Override
    public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
	return getWrapped().listTenants(criteria);
    }

    /*
     * @see com.sitewhere.spi.tenant.ITenantManagement#deleteTenant(java.util.UUID)
     */
    @Override
    public ITenant deleteTenant(UUID tenantId) throws SiteWhereException {
	return getWrapped().deleteTenant(tenantId);
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

    protected ICacheProvider<String, ITenant> getTenantByTokenCache() {
	return tenantByTokenCache;
    }

    protected ICacheProvider<UUID, ITenant> getTenantByIdCache() {
	return tenantByIdCache;
    }

    protected ITenantManagement getWrapped() {
	return wrapped;
    }
}