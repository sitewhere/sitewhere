/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.tenant;

import java.util.UUID;

import com.sitewhere.grpc.client.cache.CacheIdentifier;
import com.sitewhere.grpc.client.cache.CacheProvider;
import com.sitewhere.grpc.client.spi.cache.ICacheConfiguration;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Cache providers for tenant entities.
 * 
 * @author Derek
 */
public class TenantManagementCacheProviders {

    /**
     * Cache for tenants.
     * 
     * @author Derek
     */
    public static class TenantByTokenCache extends CacheProvider<String, ITenant> {

	public TenantByTokenCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.TenantByToken, String.class, ITenant.class, configuration);
	}
    }

    /**
     * Cache for tenants by id.
     * 
     * @author Derek
     */
    public static class TenantByIdCache extends CacheProvider<UUID, ITenant> {

	public TenantByIdCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.TenantById, UUID.class, ITenant.class, configuration);
	}
    }
}