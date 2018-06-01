/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.tenant;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sitewhere.grpc.client.cache.CacheIdentifier;
import com.sitewhere.grpc.client.cache.CacheProvider;
import com.sitewhere.spi.microservice.hazelcast.IHazelcastProvider;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Cache providers for tenant entities.
 * 
 * @author Derek
 */
public class TenantManagementCacheProviders {

    /** Cache id for tenant cache */
    public static final String ID_TENANT_CACHE = "tent";

    /** Cache id for tenant by id cache */
    public static final String ID_TENANT_BY_ID_CACHE = "tent";

    /**
     * Cache for tenants.
     * 
     * @author Derek
     */
    public static class TenantByTokenCache extends CacheProvider<String, ITenant> {

	/** Static logger instance */
	private static Logger LOGGER = LoggerFactory.getLogger(TenantByTokenCache.class);

	public TenantByTokenCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.TenantByToken, 100);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
	    return LOGGER;
	}
    }

    /**
     * Cache for tenants by id.
     * 
     * @author Derek
     */
    public static class TenantByIdCache extends CacheProvider<UUID, ITenant> {

	/** Static logger instance */
	private static Logger LOGGER = LoggerFactory.getLogger(TenantByIdCache.class);

	public TenantByIdCache(IHazelcastProvider hazelcastProvider) {
	    super(hazelcastProvider, CacheIdentifier.TenantById, 100);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Logger getLogger() {
	    return LOGGER;
	}
    }
}