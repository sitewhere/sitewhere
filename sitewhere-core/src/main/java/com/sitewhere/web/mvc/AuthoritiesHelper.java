/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.mvc;

import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.SiteWhereRoles;

/**
 * Simplifies permission checking in JSPs.
 * 
 * @author Derek
 */
public class AuthoritiesHelper {

	/** Wrapped user */
	private IUser user;

	public AuthoritiesHelper(IUser user) {
		this.user = user;
	}

	/**
	 * Indicates if user is a user administrator.
	 * 
	 * @return
	 */
	public boolean isAdministerUsers() {
		return getUser().getAuthorities().contains(SiteWhereRoles.AUTH_ADMINISTER_USERS);
	}

	/**
	 * Indicates if user is a tenant administrator.
	 * 
	 * @return
	 */
	public boolean isAdministerTenants() {
		return getUser().getAuthorities().contains(SiteWhereRoles.AUTH_ADMINISTER_TENANTS);
	}

	public IUser getUser() {
		return user;
	}

	public void setUser(IUser user) {
		this.user = user;
	}
}