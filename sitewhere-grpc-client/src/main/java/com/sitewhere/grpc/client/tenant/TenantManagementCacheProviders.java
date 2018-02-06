/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.tenant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    /**
     * Cache for tenants.
     * 
     * @author Derek
     */
    public static class TenantCache extends CacheProvider<String, ITenant> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	public TenantCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_TENANT_CACHE, createOnStartup);
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