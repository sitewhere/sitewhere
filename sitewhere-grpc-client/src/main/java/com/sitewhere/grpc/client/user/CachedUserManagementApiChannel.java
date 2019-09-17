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
import com.sitewhere.grpc.client.spi.cache.ICacheConfiguration;
import com.sitewhere.grpc.client.spi.cache.ICacheProvider;
import com.sitewhere.grpc.client.spi.client.IUserManagementApiChannel;
import com.sitewhere.server.lifecycle.TenantEngineLifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.server.lifecycle.ILifecycleProgressMonitor;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * Adds caching support to user management API channel.
 * 
 * @author Derek
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CachedUserManagementApiChannel extends TenantEngineLifecycleComponent implements IUserManagement {

    /** Wrapped API channel */
    private IUserManagementApiChannel<?> wrapped;

    /** User cache */
    private ICacheProvider<String, IUser> userCache;

    /** Granted authority cache */
    private ICacheProvider<String, List> grantedAuthorityCache;

    public CachedUserManagementApiChannel(IUserManagementApiChannel<?> wrapped, CacheSettings cache) {
	this.wrapped = wrapped;
	this.userCache = new UserManagementCacheProviders.UserByTokenCache(cache.getUserConfiguration());
	this.grantedAuthorityCache = new UserManagementCacheProviders.GrantedAuthorityByTokenCache(
		cache.getUserConfiguration());
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#initialize(com.sitewhere.
     * spi.server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void initialize(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	initializeNestedComponent(getWrapped(), monitor, true);
	initializeNestedComponent(getUserCache(), monitor, true);
	initializeNestedComponent(getGrantedAuthorityCache(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#start(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void start(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	startNestedComponent(getWrapped(), monitor, true);
	startNestedComponent(getUserCache(), monitor, true);
	startNestedComponent(getGrantedAuthorityCache(), monitor, true);
    }

    /*
     * @see
     * com.sitewhere.server.lifecycle.LifecycleComponent#stop(com.sitewhere.spi.
     * server.lifecycle.ILifecycleProgressMonitor)
     */
    @Override
    public void stop(ILifecycleProgressMonitor monitor) throws SiteWhereException {
	stopNestedComponent(getWrapped(), monitor);
	stopNestedComponent(getUserCache(), monitor);
	stopNestedComponent(getGrantedAuthorityCache(), monitor);
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
	    user = getWrapped().getUserByUsername(username);
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
	    auths = getWrapped().getGrantedAuthorities(username);
	    getGrantedAuthorityCache().setCacheEntry(null, username, auths);
	}
	return auths;
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#createUser(com.sitewhere.spi.user.
     * request.IUserCreateRequest, java.lang.Boolean)
     */
    @Override
    public IUser createUser(IUserCreateRequest request, Boolean encodePassword) throws SiteWhereException {
	return getWrapped().createUser(request, encodePassword);
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#importUser(com.sitewhere.spi.user.
     * IUser, boolean)
     */
    @Override
    public IUser importUser(IUser user, boolean overwrite) throws SiteWhereException {
	return getWrapped().importUser(user, overwrite);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#authenticate(java.lang.String,
     * java.lang.String, boolean)
     */
    @Override
    public IUser authenticate(String username, String password, boolean updateLastLogin) throws SiteWhereException {
	return getWrapped().authenticate(username, password, updateLastLogin);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#updateUser(java.lang.String,
     * com.sitewhere.spi.user.request.IUserCreateRequest, boolean)
     */
    @Override
    public IUser updateUser(String username, IUserCreateRequest request, boolean encodePassword)
	    throws SiteWhereException {
	return getWrapped().updateUser(username, request, encodePassword);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#addGrantedAuthorities(java.lang.
     * String, java.util.List)
     */
    @Override
    public List<IGrantedAuthority> addGrantedAuthorities(String username, List<String> authorities)
	    throws SiteWhereException {
	return getWrapped().addGrantedAuthorities(username, authorities);
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#removeGrantedAuthorities(java.lang.
     * String, java.util.List)
     */
    @Override
    public List<IGrantedAuthority> removeGrantedAuthorities(String username, List<String> authorities)
	    throws SiteWhereException {
	return getWrapped().removeGrantedAuthorities(username, authorities);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#listUsers(com.sitewhere.spi.user.
     * IUserSearchCriteria)
     */
    @Override
    public ISearchResults<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
	return getWrapped().listUsers(criteria);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#deleteUser(java.lang.String)
     */
    @Override
    public IUser deleteUser(String username) throws SiteWhereException {
	return getWrapped().deleteUser(username);
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#createGrantedAuthority(com.sitewhere.
     * spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request) throws SiteWhereException {
	return getWrapped().createGrantedAuthority(request);
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#getGrantedAuthorityByName(java.lang.
     * String)
     */
    @Override
    public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
	return getWrapped().getGrantedAuthorityByName(name);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#updateGrantedAuthority(java.lang.
     * String, com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException {
	return getWrapped().updateGrantedAuthority(name, request);
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#listGrantedAuthorities(com.sitewhere.
     * spi.user.IGrantedAuthoritySearchCriteria)
     */
    @Override
    public ISearchResults<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
	    throws SiteWhereException {
	return getWrapped().listGrantedAuthorities(criteria);
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#deleteGrantedAuthority(java.lang.
     * String)
     */
    @Override
    public void deleteGrantedAuthority(String authority) throws SiteWhereException {
	getWrapped().deleteGrantedAuthority(authority);
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

    public IUserManagementApiChannel<?> getWrapped() {
	return wrapped;
    }

    public void setWrapped(IUserManagementApiChannel<?> wrapped) {
	this.wrapped = wrapped;
    }
}