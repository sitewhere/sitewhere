/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.user.UserSearchCriteria;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.AccountStatus;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.SiteWhereAuthority;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
import com.sitewhere.web.rest.RestControllerBase;
import com.sitewhere.web.spi.microservice.IWebRestMicroservice;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for user operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/users")
@Api(value = "users")
public class Users extends RestControllerBase {

    /** Injected reference to microservice */
    @Autowired
    IWebRestMicroservice<?> webRestMicroservice;

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Users.class);

    /**
     * Create a new user.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create new user")
    public User createUser(@RequestBody UserCreateRequest input) throws SiteWhereException {
	checkAuthForAll(SiteWhereAuthority.REST, SiteWhereAuthority.AdminUsers);
	if ((input.getUsername() == null) || (input.getPassword() == null) || (input.getFirstName() == null)
		|| (input.getLastName() == null)) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidUserInformation, ErrorLevel.ERROR);
	}
	if (input.getStatus() == null) {
	    input.setStatus(AccountStatus.Active);
	}
	IUser user = getUserManagement().createUser(input, true);
	return User.copy(user);
    }

    /**
     * Update an existing user.
     * 
     * @param username
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{username:.+}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update existing user.")
    public User updateUser(@ApiParam(value = "Unique username", required = true) @PathVariable String username,
	    @RequestBody UserCreateRequest input) throws SiteWhereException {
	checkForAdminOrEditSelf(username);
	IUser user = getUserManagement().updateUser(username, input, true);
	return User.copy(user);
    }

    /**
     * Get a user by unique username.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{username:.+}", method = RequestMethod.GET)
    @ApiOperation(value = "Get user by username")
    public User getUserByUsername(@ApiParam(value = "Unique username", required = true) @PathVariable String username)
	    throws SiteWhereException {
	checkForAdminOrEditSelf(username);
	IUser user = getUserManagement().getUserByUsername(StringEscapeUtils.unescapeHtml(username));
	if (user == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidUsername, ErrorLevel.ERROR,
		    HttpServletResponse.SC_NOT_FOUND);
	}
	return User.copy(user);
    }

    /**
     * Delete information for a given user based on username.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{username:.+}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete user by username")
    public User deleteUserByUsername(
	    @ApiParam(value = "Unique username", required = true) @PathVariable String username)
	    throws SiteWhereException {
	checkAuthForAll(SiteWhereAuthority.REST, SiteWhereAuthority.AdminUsers);
	IUser user = getUserManagement().deleteUser(username);
	return User.copy(user);
    }

    /**
     * Get a list of detailed authority information for a given user.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{username:.+}/authorities", method = RequestMethod.GET)
    @ApiOperation(value = "Get authorities for user")
    public SearchResults<GrantedAuthority> getAuthoritiesForUsername(
	    @ApiParam(value = "Unique username", required = true) @PathVariable String username)
	    throws SiteWhereException {
	checkForAdminOrEditSelf(username);
	List<IGrantedAuthority> matches = getUserManagement().getGrantedAuthorities(username);
	List<GrantedAuthority> converted = new ArrayList<GrantedAuthority>();
	for (IGrantedAuthority auth : matches) {
	    converted.add(GrantedAuthority.copy(auth));
	}
	return new SearchResults<GrantedAuthority>(converted);
    }

    /**
     * List users matching criteria.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List users matching criteria")
    public ISearchResults<IUser> listUsers() throws SiteWhereException {
	UserSearchCriteria criteria = new UserSearchCriteria();
	return getUserManagement().listUsers(criteria);
    }

    /**
     * Check for privileges to use REST services + either admin all users or admin
     * self on the currently logged in user.
     * 
     * @param username
     * @throws SiteWhereException
     */
    public static void checkForAdminOrEditSelf(String username) throws SiteWhereException {
	checkAuthFor(SiteWhereAuthority.REST, true);
	if (!checkAuthFor(SiteWhereAuthority.AdminUsers, false)) {
	    IUser loggedIn = UserContextManager.getCurrentlyLoggedInUser();
	    if ((loggedIn == null) || (!loggedIn.getUsername().equals(username))) {
		throw operationNotPermitted();
	    } else {
		checkAuthFor(SiteWhereAuthority.AdminSelf, true);
	    }
	}
    }

    /**
     * Get {@link IUserManagement} implementation.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IUserManagement getUserManagement() throws SiteWhereException {
	return webRestMicroservice.getUserManagementApiChannel();
    }
}