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
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#createUser(com.sitewhere.spi.user.request.
	 * IUserCreateRequest)
	 */
	@Override
	public IUser createUser(IUserCreateRequest request) throws SiteWhereException {
		return delegate.createUser(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#importUser(com.sitewhere.spi.user.IUser,
	 * boolean)
	 */
	@Override
	public IUser importUser(IUser user, boolean overwrite) throws SiteWhereException {
		return delegate.importUser(user, overwrite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#authenticate(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public IUser authenticate(String username, String password) throws SiteWhereException {
		return delegate.authenticate(username, password);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#updateUser(java.lang.String,
	 * com.sitewhere.spi.user.request.IUserCreateRequest)
	 */
	@Override
	public IUser updateUser(String username, IUserCreateRequest request) throws SiteWhereException {
		return delegate.updateUser(username, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#getUserByUsername(java.lang.String)
	 */
	@Override
	public IUser getUserByUsername(String username) throws SiteWhereException {
		return delegate.getUserByUsername(username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#getGrantedAuthorities(java.lang.String)
	 */
	@Override
	public List<IGrantedAuthority> getGrantedAuthorities(String username) throws SiteWhereException {
		return delegate.getGrantedAuthorities(username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#addGrantedAuthorities(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public List<IGrantedAuthority> addGrantedAuthorities(String username, List<String> authorities)
			throws SiteWhereException {
		return delegate.addGrantedAuthorities(username, authorities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#removeGrantedAuthorities(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public List<IGrantedAuthority> removeGrantedAuthorities(String username, List<String> authorities)
			throws SiteWhereException {
		return delegate.removeGrantedAuthorities(username, authorities);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#listUsers(com.sitewhere.spi.user.
	 * IUserSearchCriteria)
	 */
	@Override
	public List<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
		return delegate.listUsers(criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.user.IUserManagement#deleteUser(java.lang.String, boolean)
	 */
	@Override
	public IUser deleteUser(String username, boolean force) throws SiteWhereException {
		return delegate.deleteUser(username, force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#createGrantedAuthority(com.sitewhere.spi.
	 * user.request.IGrantedAuthorityCreateRequest)
	 */
	@Override
	public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request)
			throws SiteWhereException {
		return delegate.createGrantedAuthority(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#getGrantedAuthorityByName(java.lang.String)
	 */
	@Override
	public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
		return delegate.getGrantedAuthorityByName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#updateGrantedAuthority(java.lang.String,
	 * com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
	 */
	@Override
	public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
			throws SiteWhereException {
		return delegate.updateGrantedAuthority(name, request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#listGrantedAuthorities(com.sitewhere.spi.
	 * user.IGrantedAuthoritySearchCriteria)
	 */
	@Override
	public List<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
			throws SiteWhereException {
		return delegate.listGrantedAuthorities(criteria);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sitewhere.spi.user.IUserManagement#deleteGrantedAuthority(java.lang.String)
	 */
	@Override
	public void deleteGrantedAuthority(String authority) throws SiteWhereException {
		delegate.deleteGrantedAuthority(authority);
	}
}