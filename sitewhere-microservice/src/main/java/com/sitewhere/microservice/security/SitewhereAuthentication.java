/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.sitewhere.grpc.model.spi.security.ITenantAwareAuthentication;

/**
 * Implementation of Spring security interface.
 * 
 * @author Derek
 */
public class SitewhereAuthentication implements ITenantAwareAuthentication {

    /** Serial version UID */
    private static final long serialVersionUID = 1L;

    /** Spring UserDetails */
    private SitewhereUserDetails userDetails;

    /** Unhashed password */
    private String password;

    /** Authenticated flag */
    private boolean authenticated;

    /** Tenant token */
    private String tenantToken;

    public SitewhereAuthentication(SitewhereUserDetails details, String password) {
	this.userDetails = details;
	this.password = password;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.core.Authentication#getAuthorities()
     */
    @Override
    public Collection<GrantedAuthority> getAuthorities() {
	return userDetails.getAuthorities();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.Authentication#getCredentials()
     */
    @Override
    public Object getCredentials() {
	return password;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.Authentication#getDetails()
     */
    public Object getDetails() {
	return userDetails;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.Authentication#getPrincipal()
     */
    @Override
    public Object getPrincipal() {
	return userDetails.getUser();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.Authentication#isAuthenticated()
     */
    @Override
    public boolean isAuthenticated() {
	return authenticated;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.Authentication#setAuthenticated(boolean)
     */
    @Override
    public void setAuthenticated(boolean value) throws IllegalArgumentException {
	this.authenticated = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.security.Principal#getName()
     */
    @Override
    public String getName() {
	return userDetails.getUsername();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.security.ITenantAwareAuthentication#
     * getTenantToken()
     */
    @Override
    public String getTenantToken() {
	return tenantToken;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.security.ITenantAwareAuthentication#
     * setTenantToken(java.lang.String)
     */
    @Override
    public void setTenantToken(String tenantToken) {
	this.tenantToken = tenantToken;
    }
}