/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.user;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.client.cache.CacheProvider;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.user.IUser;

/**
 * Cache providers for user management entities.
 * 
 * @author Derek
 */
public class UserManagementCacheProviders {

    /** Cache id for user cache */
    public static final String ID_USER_CACHE = "us";

    /**
     * Cache for users.
     * 
     * @author Derek
     */
    public static class UserCache extends CacheProvider<String, IUser> {

	/** Static logger instance */
	private static Logger LOGGER = LogManager.getLogger();

	public UserCache(IMicroservice microservice, boolean createOnStartup) {
	    super(microservice, ID_USER_CACHE, createOnStartup);
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