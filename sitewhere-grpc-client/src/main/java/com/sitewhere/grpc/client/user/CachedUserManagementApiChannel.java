/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.user;

import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.cache.ICacheProvider;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.user.IUser;

/**
 * Adds caching support to user management API channel.
 * 
 * @author Derek
 */
public class CachedUserManagementApiChannel extends UserManagementApiChannel {

    /** User cache */
    private ICacheProvider<String, IUser> userCache;

    public CachedUserManagementApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
	super(demux, microservice, host);
	this.userCache = new UserManagementCacheProviders.UserCache(microservice, false);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.user.UserManagementApiChannel#getUserByUsername(
     * java.lang.String)
     */
    @Override
    public IUser getUserByUsername(String username) throws SiteWhereException {
	IUser user = getUserCache().getCacheEntry(null, username);
	if (user != null) {
	    getLogger().trace("Using cached information for user '" + username + "'.");
	    return user;
	} else {
	    getLogger().trace("No cached information for user '" + username + "'.");
	}
	return super.getUserByUsername(username);
    }

    public ICacheProvider<String, IUser> getUserCache() {
	return userCache;
    }

    public void setUserCache(ICacheProvider<String, IUser> userCache) {
	this.userCache = userCache;
    }
}