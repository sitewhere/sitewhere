/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.user;

import java.util.List;

import com.sitewhere.server.lifecycle.LifecycleComponentDecorator;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.ITenant;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.ITenantCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * Uses decorator pattern to allow behaviors to be injected around user management API
 * calls.
 * 
 * @author Derek
 */
public class UserManagementDecorator extends LifecycleComponentDecorator implements IUserManagement {

	/** Delegate */
	private IUserManagement delegate;

	public UserManagementDecorator(IUserManagement delegate) {
		super(delegate);
		this.delegate = delegate;
	}

	@Override
	public IUser createUser(IUserCreateRequest request) throws SiteWhereException {
		return delegate.createUser(request);
	}

	@Override
	public IUser authenticate(String username, String password) throws SiteWhereException {
		return delegate.authenticate(username, password);
	}

	@Override
	public IUser updateUser(String username, IUserCreateRequest request) throws SiteWhereException {
		return delegate.updateUser(username, request);
	}

	@Override
	public IUser getUserByUsername(String username) throws SiteWhereException {
		return delegate.getUserByUsername(username);
	}

	@Override
	public List<IGrantedAuthority> getGrantedAuthorities(String username) throws SiteWhereException {
		return delegate.getGrantedAuthorities(username);
	}

	@Override
	public List<IGrantedAuthority> addGrantedAuthorities(String username, List<String> authorities)
			throws SiteWhereException {
		return delegate.addGrantedAuthorities(username, authorities);
	}

	@Override
	public List<IGrantedAuthority> removeGrantedAuthorities(String username, List<String> authorities)
			throws SiteWhereException {
		return delegate.removeGrantedAuthorities(username, authorities);
	}

	@Override
	public List<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
		return delegate.listUsers(criteria);
	}

	@Override
	public IUser deleteUser(String username, boolean force) throws SiteWhereException {
		return delegate.deleteUser(username, force);
	}

	@Override
	public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request)
			throws SiteWhereException {
		return delegate.createGrantedAuthority(request);
	}

	@Override
	public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
		return delegate.getGrantedAuthorityByName(name);
	}

	@Override
	public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
			throws SiteWhereException {
		return delegate.updateGrantedAuthority(name, request);
	}

	@Override
	public List<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
			throws SiteWhereException {
		return delegate.listGrantedAuthorities(criteria);
	}

	@Override
	public void deleteGrantedAuthority(String authority) throws SiteWhereException {
		delegate.deleteGrantedAuthority(authority);
	}

	@Override
	public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
		return delegate.createTenant(request);
	}

	@Override
	public ITenant updateTenant(String id, ITenantCreateRequest request) throws SiteWhereException {
		return delegate.updateTenant(id, request);
	}

	@Override
	public ITenant getTenantById(String id) throws SiteWhereException {
		return delegate.getTenantById(id);
	}

	@Override
	public ITenant getTenantByAuthenticationToken(String token) throws SiteWhereException {
		return delegate.getTenantByAuthenticationToken(token);
	}

	@Override
	public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
		return delegate.listTenants(criteria);
	}

	@Override
	public ITenant deleteTenant(String tenantId, boolean force) throws SiteWhereException {
		return delegate.deleteTenant(tenantId, force);
	}
}