/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.user;

import java.util.List;

import com.sitewhere.grpc.client.cache.CacheIdentifier;
import com.sitewhere.grpc.client.cache.CacheProvider;
import com.sitewhere.grpc.client.spi.cache.ICacheConfiguration;
import com.sitewhere.spi.user.IUser;

/**
 * Cache providers for user management entities.
 * 
 * @author Derek
 */
public class UserManagementCacheProviders {

    /**
     * Cache for users.
     * 
     * @author Derek
     */
    public static class UserByTokenCache extends CacheProvider<String, IUser> {

	public UserByTokenCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.UserByToken, String.class, IUser.class, configuration);
	}
    }

    /**
     * Cache for user granted authorities.
     * 
     * @author Derek
     */
    @SuppressWarnings("rawtypes")
    public static class GrantedAuthorityByTokenCache extends CacheProvider<String, List> {

	public GrantedAuthorityByTokenCache(ICacheConfiguration configuration) {
	    super(CacheIdentifier.GrantedAuthorityByToken, String.class, List.class, configuration);
	}
    }
}