/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.user.cache;

import java.util.List;

import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.grpc.client.user.UserManagementCacheProviders;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.ICachingMicroservice;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.request.IUserCreateRequest;
import com.sitewhere.user.UserManagementDecorator;

/**
 * Wraps {@link IUserManagement} implementation with cache support.
 * 
 * @author Derek
 */
public class CacheAwareUserManagement extends UserManagementDecorator {

    /** User cache */
    private ICacheProvider<String, IUser> userCache;

    /** Granted authority cache */
    private ICacheProvider<String, List<IGrantedAuthority>> grantedAuthorityCache;

    public CacheAwareUserManagement(IUserManagement delegate, ICachingMicroservice microservice) {
	super(delegate);
	this.userCache = new UserManagementCacheProviders.UserByTokenCache(microservice.getHazelcastManager());
	this.grantedAuthorityCache = new UserManagementCacheProviders.GrantedAuthorityByTokenCache(
		microservice.getHazelcastManager());
    }

    /*
     * @see
     * com.sitewhere.user.UserManagementDecorator#createUser(com.sitewhere.spi.user.
     * request.IUserCreateRequest, java.lang.Boolean)
     */
    @Override
    public IUser createUser(IUserCreateRequest request, Boolean encodePassword) throws SiteWhereException {
	IUser result = super.createUser(request, encodePassword);
	getUserCache().setCacheEntry(null, result.getUsername(), result);
	getLogger().trace("Added created user to cache.");
	return result;
    }

    /*
     * @see com.sitewhere.user.UserManagementDecorator#updateUser(java.lang.String,
     * com.sitewhere.spi.user.request.IUserCreateRequest, boolean)
     */
    @Override
    public IUser updateUser(String username, IUserCreateRequest request, boolean encodePassword)
	    throws SiteWhereException {
	IUser result = super.updateUser(username, request, encodePassword);
	getUserCache().setCacheEntry(null, result.getUsername(), result);
	getGrantedAuthorityCache().removeCacheEntry(null, result.getUsername());
	getLogger().trace("Updated user in cache.");
	return result;
    }

    /*
     * @see com.sitewhere.user.UserManagementDecorator#getUserByUsername(java.lang.
     * String)
     */
    @Override
    public IUser getUserByUsername(String username) throws SiteWhereException {
	IUser result = super.getUserByUsername(username);
	if ((result != null) && (getUserCache().getCacheEntry(null, username) == null)) {
	    getUserCache().setCacheEntry(null, result.getUsername(), result);
	    getGrantedAuthorityCache().removeCacheEntry(null, result.getUsername());
	    getLogger().trace("Added user to cache.");
	}
	return result;
    }

    /*
     * @see com.sitewhere.user.UserManagementDecorator#deleteUser(java.lang.String,
     * boolean)
     */
    @Override
    public IUser deleteUser(String username, boolean force) throws SiteWhereException {
	IUser result = super.deleteUser(username, force);
	getUserCache().removeCacheEntry(null, result.getUsername());
	getGrantedAuthorityCache().removeCacheEntry(null, result.getUsername());
	getLogger().trace("Removed user from cache.");
	return result;
    }

    /*
     * @see
     * com.sitewhere.user.UserManagementDecorator#getGrantedAuthorities(java.lang.
     * String)
     */
    @Override
    public List<IGrantedAuthority> getGrantedAuthorities(String username) throws SiteWhereException {
	List<IGrantedAuthority> result = super.getGrantedAuthorities(username);
	if ((result != null) && (getGrantedAuthorityCache().getCacheEntry(null, username) == null)) {
	    getGrantedAuthorityCache().setCacheEntry(null, username, result);
	    getLogger().trace("Added granted authorities to cache.");
	}
	return result;
    }

    protected ICacheProvider<String, IUser> getUserCache() {
	return userCache;
    }

    protected void setUserCache(ICacheProvider<String, IUser> userCache) {
	this.userCache = userCache;
    }

    protected ICacheProvider<String, List<IGrantedAuthority>> getGrantedAuthorityCache() {
	return grantedAuthorityCache;
    }

    protected void setGrantedAuthorityCache(ICacheProvider<String, List<IGrantedAuthority>> grantedAuthorityCache) {
	this.grantedAuthorityCache = grantedAuthorityCache;
    }
}