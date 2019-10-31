/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.user.request;

import com.sitewhere.rest.model.search.user.UserSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;

/**
 * Builder that supports creating user management entities.
 */
public class UserManagementRequestBuilder {

    /** Device management implementation */
    private IUserManagement userManagement;

    public UserManagementRequestBuilder(IUserManagement userManagement) {
	this.userManagement = userManagement;
    }

    /**
     * Create builder for new user request.
     * 
     * @param username
     * @param password
     * @param firstName
     * @param lastName
     * @return
     */
    public UserCreateRequest.Builder newUser(String username, String password, String firstName, String lastName) {
	return new UserCreateRequest.Builder(username, password, firstName, lastName);
    }

    /**
     * Persist user contructed via builder.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IUser persist(UserCreateRequest.Builder builder) throws SiteWhereException {
	return getUserManagement().createUser(builder.build(), true);
    }

    /**
     * Create builder for new granted authority request.
     * 
     * @param authority
     * @return
     */
    public GrantedAuthorityCreateRequest.Builder newGrantedAuthority(String authority) {
	return new GrantedAuthorityCreateRequest.Builder(authority);
    }

    /**
     * Persist granted authority constructed via builder.
     * 
     * @param builder
     * @return
     * @throws SiteWhereException
     */
    public IGrantedAuthority persist(GrantedAuthorityCreateRequest.Builder builder) throws SiteWhereException {
	return getUserManagement().createGrantedAuthority(builder.build());
    }

    /**
     * Get an existing authority by name.
     * 
     * @param authority
     * @return
     * @throws SiteWhereException
     */
    public IGrantedAuthority getAuthority(String authority) throws SiteWhereException {
	return getUserManagement().getGrantedAuthorityByName(authority);
    }

    /**
     * Indicates if the system already contains the given authority.
     * 
     * @param authority
     * @return
     * @throws SiteWhereException
     */
    public boolean hasAuthority(String authority) throws SiteWhereException {
	return getAuthority(authority) != null;
    }

    /**
     * List all users.
     * 
     * @return
     * @throws SiteWhereException
     */
    public ISearchResults<IUser> listUsers() throws SiteWhereException {
	return getUserManagement().listUsers(new UserSearchCriteria());
    }

    /**
     * Indicates if the system has users defined.
     * 
     * @return
     * @throws SiteWhereException
     */
    public boolean hasUsers() throws SiteWhereException {
	return listUsers().getNumResults() > 0;
    }

    public IUserManagement getUserManagement() {
	return userManagement;
    }

    public void setUserManagement(IUserManagement userManagement) {
	this.userManagement = userManagement;
    }
}