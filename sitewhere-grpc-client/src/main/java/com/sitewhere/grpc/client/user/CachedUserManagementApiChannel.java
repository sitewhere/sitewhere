/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.user;

import java.util.List;

import com.sitewhere.grpc.client.cache.NearCacheManager;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;

/**
 * Adds caching support to user management API channel.
 * 
 * @author Derek
 */
public class CachedUserManagementApiChannel extends UserManagementApiChannel {

    /** Manages local cache */
    private NearCacheManager nearCacheManager;

    /** User cache */
    private ICacheProvider<String, IUser> userCache;

    /** Granted authority cache */
    private ICacheProvider<String, List<IGrantedAuthority>> grantedAuthorityCache;

    public CachedUserManagementApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
	super(demux, microservice, host);
	this.nearCacheManager = new NearCacheManager(microservice, MicroserviceIdentifier.UserManagement);
	this.userCache = new UserManagementCacheProviders.UserByTokenCache(nearCacheManager);
	this.grantedAuthorityCache = new UserManagementCacheProviders.GrantedAuthorityByTokenCache(nearCacheManager);
	getNearCacheManager().setCacheProviders(userCache, grantedAuthorityCache);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.start(monitor);

	// Start near cache manager.
	startNestedComponent(getNearCacheManager(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	super.stop(monitor);

	// Stop near cache manager.
	stopNestedComponent(getNearCacheManager(), monitor);
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

    /*
     * @see
     * com.sitewhere.grpc.client.user.UserManagementApiChannel#getGrantedAuthorities
     * (java.lang.String)
     */
    @Override
    public List<IGrantedAuthority> getGrantedAuthorities(String username) throws SiteWhereException {
	List<IGrantedAuthority> auths = getGrantedAuthorityCache().getCacheEntry(null, username);
	if (auths != null) {
	    getLogger().trace("Using cached authorities for user '" + username + "'.");
	    return auths;
	} else {
	    getLogger().trace("No cached authorities for user '" + username + "'.");
	}
	return super.getGrantedAuthorities(username);
    }

    public NearCacheManager getNearCacheManager() {
	return nearCacheManager;
    }

    public void setNearCacheManager(NearCacheManager nearCacheManager) {
	this.nearCacheManager = nearCacheManager;
    }

    public ICacheProvider<String, IUser> getUserCache() {
	return userCache;
    }

    public void setUserCache(ICacheProvider<String, IUser> userCache) {
	this.userCache = userCache;
    }

    public ICacheProvider<String, List<IGrantedAuthority>> getGrantedAuthorityCache() {
	return grantedAuthorityCache;
    }

    public void setGrantedAuthorityCache(ICacheProvider<String, List<IGrantedAuthority>> grantedAuthorityCache) {
	this.grantedAuthorityCache = grantedAuthorityCache;
    }
}