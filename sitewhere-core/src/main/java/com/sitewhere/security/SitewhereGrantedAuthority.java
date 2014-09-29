/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.security;

import org.springframework.security.core.GrantedAuthority;

import com.sitewhere.spi.user.IGrantedAuthority;

/**
 * SiteWhere implementation of granted authority.
 * 
 * @author Derek
 */
public class SitewhereGrantedAuthority implements GrantedAuthority {

	/** Serial verison UID */
	private static final long serialVersionUID = 1L;
	
	/** Prefix Spring Security expects for role authorities */
	public static final String ROLE_VOTER_PREFIX = "ROLE_";

	/** Authority */
	private String authority;

	public SitewhereGrantedAuthority(IGrantedAuthority auth) {
		this.authority = ROLE_VOTER_PREFIX + auth.getAuthority();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.security.GrantedAuthority#getAuthority()
	 */
	public String getAuthority() {
		return authority;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if ((o != null) && (o instanceof GrantedAuthority)) {
			return getAuthority().compareTo(((GrantedAuthority) o).getAuthority());
		}
		return -1;
	}
}