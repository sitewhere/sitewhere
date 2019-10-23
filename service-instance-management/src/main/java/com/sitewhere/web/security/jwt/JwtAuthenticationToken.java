/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.security.jwt;

import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

/**
 * Extends {@link AbstractAuthenticationToken} with JWT details.
 * 
 * @author Derek
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    /** Serial version UID */
    private static final long serialVersionUID = 246330914120864789L;

    /** Username */
    private String username;

    /** Token */
    private String token;

    public JwtAuthenticationToken(String username, List<GrantedAuthority> authorities, String token) {
	super(authorities);
	this.username = username;
	this.token = token;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.authentication.AbstractAuthenticationToken#
     * isAuthenticated()
     */
    @Override
    public boolean isAuthenticated() {
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.Authentication#getCredentials()
     */
    @Override
    public Object getCredentials() {
	return token;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.Authentication#getPrincipal()
     */
    @Override
    public Object getPrincipal() {
	return username;
    }
}