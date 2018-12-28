/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.user;

import java.util.List;

import com.sitewhere.grpc.client.cache.CacheConfiguration;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.cache.ICacheConfiguration;
import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;

/**
 * Adds caching support to user management API channel.
 * 
 * @author Derek
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CachedUserManagementApiChannel extends UserManagementApiChannel {

    /** User cache */
    private ICacheProvider<String, IUser> userCache;

    /** Granted authority cache */
    private ICacheProvider<String, List> grantedAuthorityCache;

    public CachedUserManagementApiChannel(IApiDemux<?> demux, String host, int port, CacheSettings settings) {
	super(demux, host, port);
	this.userCache = new UserManagementCacheProviders.UserByTokenCache(settings.getUserConfiguration());
	this.grantedAuthorityCache = new UserManagementCacheProviders.GrantedAuthorityByTokenCache(
		settings.getUserConfiguration());
    }

    /*
     * @see
     * com.sitewhere.grpc.client.ApiChannel#initialize(com.sitewhere.spi.server.
     * lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	initializeNestedComponent(getUserCache(), monitor, true);
	initializeNestedComponent(getGrantedAuthorityCache(), monitor, true);
	super.initialize(monitor);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.ApiChannel#start(com.sitewhere.spi.server.lifecycle
     * .ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	startNestedComponent(getUserCache(), monitor, true);
	startNestedComponent(getGrantedAuthorityCache(), monitor, true);
	super.start(monitor);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.ApiChannel#stop(com.sitewhere.spi.server.lifecycle.
     * ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	stopNestedComponent(getUserCache(), monitor);
	stopNestedComponent(getGrantedAuthorityCache(), monitor);
	super.stop(monitor);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.user.UserManagementApiChannel#getUserByUsername(
     * java.lang.String)
     */
    @Override
    public IUser getUserByUsername(String username) throws SiteWhereException {
	IUser user = getUserCache().getCacheEntry(null, username);
	if (user == null) {
	    user = super.getUserByUsername(username);
	    getUserCache().setCacheEntry(null, username, user);
	}
	return user;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.user.UserManagementApiChannel#getGrantedAuthorities
     * (java.lang.String)
     */
    @Override
    public List<IGrantedAuthority> getGrantedAuthorities(String username) throws SiteWhereException {
	List<IGrantedAuthority> auths = (List<IGrantedAuthority>) getGrantedAuthorityCache().getCacheEntry(null,
		username);
	if (auths == null) {
	    auths = super.getGrantedAuthorities(username);
	    getGrantedAuthorityCache().setCacheEntry(null, username, auths);
	}
	return auths;
    }

    /**
     * Contains default cache settings for user management entities.
     */
    public static class CacheSettings {

	/** Cache configuraton for users */
	private ICacheConfiguration userConfiguration = new CacheConfiguration(1000, 60);

	public ICacheConfiguration getUserConfiguration() {
	    return userConfiguration;
	}

	public void setUserConfiguration(ICacheConfiguration userConfiguration) {
	    this.userConfiguration = userConfiguration;
	}
    }

    public ICacheProvider<String, IUser> getUserCache() {
	return userCache;
    }

    public void setUserCache(ICacheProvider<String, IUser> userCache) {
	this.userCache = userCache;
    }

    public ICacheProvider<String, List> getGrantedAuthorityCache() {
	return grantedAuthorityCache;
    }

    public void setGrantedAuthorityCache(ICacheProvider<String, List> grantedAuthorityCache) {
	this.grantedAuthorityCache = grantedAuthorityCache;
    }
}