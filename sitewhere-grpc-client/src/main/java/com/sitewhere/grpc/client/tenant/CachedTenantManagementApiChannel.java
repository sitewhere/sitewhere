/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.tenant;

import java.util.UUID;

import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.ICacheProvider;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Adds caching support to tenant management API channel.
 * 
 * @author Derek
 */
public class CachedTenantManagementApiChannel extends TenantManagementApiChannel {

    /** Tenant cache */
    private ICacheProvider<String, ITenant> tenantCache;

    /** Tenant by id cache */
    private ICacheProvider<UUID, ITenant> tenantByIdCache;

    public CachedTenantManagementApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
	super(demux, microservice, host);
	this.tenantCache = new TenantManagementCacheProviders.TenantCache(microservice, false);
	this.tenantByIdCache = new TenantManagementCacheProviders.TenantByIdCache(microservice, false);
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
	ITenant tenant = getTenantCache().getCacheEntry(null, token);
	if (tenant != null) {
	    getLogger().trace("Using cached information for tenant '" + token + "'.");
	    return tenant;
	} else {
	    getLogger().trace("No cached information for tenant '" + token + "'.");
	}
	return super.getTenantByToken(token);
    }

    public ICacheProvider<String, ITenant> getTenantCache() {
	return tenantCache;
    }

    public void setTenantCache(ICacheProvider<String, ITenant> tenantCache) {
	this.tenantCache = tenantCache;
    }

    public ICacheProvider<UUID, ITenant> getTenantByIdCache() {
	return tenantByIdCache;
    }

    public void setTenantByIdCache(ICacheProvider<UUID, ITenant> tenantByIdCache) {
	this.tenantByIdCache = tenantByIdCache;
    }
}