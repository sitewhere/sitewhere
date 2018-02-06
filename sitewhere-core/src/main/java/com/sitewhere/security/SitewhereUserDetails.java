/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.sitewhere.spi.user.AccountStatus;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;

/**
 * SiteWhere implementation of user details.
 * 
 * @author Derek
 */
public class SitewhereUserDetails implements UserDetails {

    /** Serial version UID */
    private static final long serialVersionUID = 1L;

    /** User */
    private IUser user;

    /** Granted authorities */
    private Collection<GrantedAuthority> grantedAuthorities;

    public SitewhereUserDetails(IUser user, List<IGrantedAuthority> authorities) {
	this.user = user;
	this.grantedAuthorities = convertAuthorities(authorities);
    }

    /**
     * Get and convert the granted authorities.
     * 
     * @param authorities
     * @return
     */
    protected Collection<GrantedAuthority> convertAuthorities(List<IGrantedAuthority> authorities) {
	List<GrantedAuthority> results = new ArrayList<GrantedAuthority>();
	for (IGrantedAuthority auth : authorities) {
	    results.add(new SitewhereGrantedAuthority(auth));
	}
	return results;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetails#getUsername()
     */
    public String getUsername() {
	return user.getUsername();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetails#getPassword()
     */
    public String getPassword() {
	return user.getHashedPassword();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.core.userdetails.UserDetails#getAuthorities(
     * )
     */
    public Collection<GrantedAuthority> getAuthorities() {
	return grantedAuthorities;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.userdetails.UserDetails#isAccountNonExpired(
     * )
     */
    public boolean isAccountNonExpired() {
	return !(user.getStatus() == AccountStatus.Expired);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.userdetails.UserDetails#isAccountNonLocked()
     */
    public boolean isAccountNonLocked() {
	return !(user.getStatus() == AccountStatus.Locked);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetails#
     * isCredentialsNonExpired()
     */
    public boolean isCredentialsNonExpired() {
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetails#isEnabled()
     */
    public boolean isEnabled() {
	return (user.getStatus() == AccountStatus.Active);
    }

    /**
     * Get the underlying user object.
     * 
     * @return
     */
    public IUser getUser() {
	return user;
    }
}