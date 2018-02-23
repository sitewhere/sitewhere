/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.tenant;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.cache.CacheProvider;
import com.sitewhere.spi.microservice.IMicroservice;
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
    public static class TenantCache extends CacheProvider<String, ITenant> {

	/** Static logger instance */
	private static Log LOGGER = LogFactory.getLog(TenantCache.class);

	public TenantCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_TENANT_CACHE, createOnStartup);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
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
	private static Log LOGGER = LogFactory.getLog(TenantByIdCache.class);

	public TenantByIdCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_TENANT_BY_ID_CACHE, createOnStartup);
	}

	/*
	 * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
	 */
	@Override
	public Log getLogger() {
	    return LOGGER;
	}
    }
}