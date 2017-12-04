/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.tenant;

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

    public CachedTenantManagementApiChannel(IMicroservice microservice, String host) {
	super(microservice, host);
	this.tenantCache = new TenantManagementCacheProviders.TenantCache(microservice, false);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.tenant.TenantManagementApiChannel#getTenantById(
     * java.lang.String)
     */
    @Override
    public ITenant getTenantById(String id) throws SiteWhereException {
	ITenant tenant = getTenantCache().getCacheEntry(null, id);
	if (tenant != null) {
	    getLogger().trace("Using cached information for tenant '" + id + "'.");
	    return tenant;
	} else {
	    getLogger().trace("No cached information for tenant '" + id + "'.");
	}
	return super.getTenantById(id);
    }

    public ICacheProvider<String, ITenant> getTenantCache() {
	return tenantCache;
    }

    public void setTenantCache(ICacheProvider<String, ITenant> tenantCache) {
	this.tenantCache = tenantCache;
    }
}