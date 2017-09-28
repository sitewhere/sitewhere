/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.security;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;

import com.sitewhere.microservice.security.SitewhereUserDetails;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;

/**
 * SiteWhere implementation of Spring security UserDetailsManager.
 * 
 * @author Derek
 */
public class SitewhereUserDetailsService implements UserDetailsManager {

    /** User management implementation */
    private IUserManagement userManagement;

    public SitewhereUserDetailsService(IUserManagement userManagement) {
	this.userManagement = userManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetailsService#
     * loadUserByUsername( java.lang. String)
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	try {
	    IUser user = getUserManagement().getUserByUsername(username);
	    List<IGrantedAuthority> auths = getUserManagement().getGrantedAuthorities(username);
	    return new SitewhereUserDetails(user, auths);
	} catch (SiteWhereException e) {
	    throw new UsernameNotFoundException("Unable to load user by username.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.userdetails.UserDetailsManager#createUser(
     * org. springframework .security.userdetails .UserDetails)
     */
    public void createUser(UserDetails info) {
	UserCreateRequest user = new UserCreateRequest();
	user.setUsername(info.getUsername());
	user.setPassword(info.getPassword());
	try {
	    getUserManagement().createUser(user, true);
	} catch (SiteWhereException e) {
	    throw new RuntimeException(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.userdetails.UserDetailsManager#deleteUser(
     * java.lang .String)
     */
    public void deleteUser(String username) {
	try {
	    getUserManagement().deleteUser(username, true);
	} catch (SiteWhereException e) {
	    throw new RuntimeException("Unable to delete user.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.userdetails.UserDetailsManager#userExists(
     * java.lang .String)
     */
    public boolean userExists(String username) {
	return (loadUserByUsername(username) != null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.springframework.security.userdetails.UserDetailsManager#updateUser(
     * org. springframework .security.userdetails .UserDetails)
     */
    public void updateUser(UserDetails info) {
	throw new RuntimeException("User updates not supported.");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.springframework.security.userdetails.UserDetailsManager#
     * changePassword(java .lang.String, java.lang.String)
     */
    public void changePassword(String oldPassword, String newPassword) {
	throw new RuntimeException("User updates not supported.");
    }

    public IUserManagement getUserManagement() {
	return userManagement;
    }

    public void setUserManagement(IUserManagement userManagement) {
	this.userManagement = userManagement;
    }
}