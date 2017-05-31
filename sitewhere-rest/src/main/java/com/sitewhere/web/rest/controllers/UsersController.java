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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.user.UserSearchCriteria;
import com.sitewhere.rest.model.tenant.Tenant;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.security.LoginManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.server.tenant.ISiteWhereTenantEngine;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.user.AccountStatus;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.SiteWhereAuthority;
import com.sitewhere.web.rest.RestController;
import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.sitewhere.web.rest.annotations.Example;
import com.sitewhere.web.rest.annotations.Example.Stage;
import com.sitewhere.web.rest.documentation.Tenants;
import com.sitewhere.web.rest.documentation.Users;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for user operations.
 * 
 * @author Derek Adams
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/users")
@Api(value = "users", description = "Operations related to SiteWhere users.")
@DocumentedController(name = "Users")
public class UsersController extends RestController {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /**
     * Create a new user.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Create new user")
    @Documented(examples = {
	    @Example(stage = Stage.Request, json = Users.CreateUserRequest.class, description = "createUserRequest.md"),
	    @Example(stage = Stage.Response, json = Users.CreateUserResponse.class, description = "createUserResponse.md") })
    public User createUser(@RequestBody UserCreateRequest input, HttpServletRequest servletRequest,
	    HttpServletResponse servletResponse) throws SiteWhereException {
	checkAuthForAll(servletRequest, servletResponse, SiteWhereAuthority.REST, SiteWhereAuthority.AdminUsers);
	Tracer.start(TracerCategory.RestApiCall, "createUser", LOGGER);
	try {
	    if ((input.getUsername() == null) || (input.getPassword() == null) || (input.getFirstName() == null)
		    || (input.getLastName() == null)) {
		throw new SiteWhereSystemException(ErrorCode.InvalidUserInformation, ErrorLevel.ERROR);
	    }
	    if (input.getStatus() == null) {
		input.setStatus(AccountStatus.Active);
	    }
	    IUser user = getUserManagement().createUser(input, true);
	    return User.copy(user);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Update an existing user.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{username:.+}", method = RequestMethod.PUT)
    @ResponseBody
    @ApiOperation(value = "Update existing user.")
    @Documented(examples = {
	    @Example(stage = Stage.Request, json = Users.UpdateUserRequest.class, description = "updateUserRequest.md"),
	    @Example(stage = Stage.Response, json = Users.UpdateUserResponse.class, description = "updateUserResponse.md") })
    public User updateUser(@ApiParam(value = "Unique username", required = true) @PathVariable String username,
	    @RequestBody UserCreateRequest input, HttpServletRequest servletRequest,
	    HttpServletResponse servletResponse) throws SiteWhereException {
	checkForAdminOrEditSelf(servletRequest, servletResponse, username);
	Tracer.start(TracerCategory.RestApiCall, "updateUser", LOGGER);
	try {
	    IUser user = getUserManagement().updateUser(username, input, true);
	    return User.copy(user);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Get a user by unique username.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{username:.+}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get user by username")
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Users.CreateUserResponse.class, description = "getUserByUsernameResponse.md") })
    public User getUserByUsername(@ApiParam(value = "Unique username", required = true) @PathVariable String username,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	checkForAdminOrEditSelf(servletRequest, servletResponse, username);
	Tracer.start(TracerCategory.RestApiCall, "getUserByUsername", LOGGER);
	try {
	    IUser user = getUserManagement().getUserByUsername(StringEscapeUtils.unescapeHtml(username));
	    if (user == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidUsername, ErrorLevel.ERROR,
			HttpServletResponse.SC_NOT_FOUND);
	    }
	    return User.copy(user);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Delete information for a given user based on username.
     * 
     * @param siteToken
     * @param force
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{username:.+}", method = RequestMethod.DELETE)
    @ResponseBody
    @ApiOperation(value = "Delete user by username")
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Users.CreateUserResponse.class, description = "deleteUserByUsernameResponse.md") })
    public User deleteUserByUsername(
	    @ApiParam(value = "Unique username", required = true) @PathVariable String username,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	checkAuthForAll(servletRequest, servletResponse, SiteWhereAuthority.REST, SiteWhereAuthority.AdminUsers);
	Tracer.start(TracerCategory.RestApiCall, "deleteUserByUsername", LOGGER);
	try {
	    IUser user = getUserManagement().deleteUser(username, force);
	    return User.copy(user);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Get a list of detailed authority information for a given user.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{username:.+}/authorities", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Get authorities for user")
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Users.ListAuthoritiesForUserResponse.class, description = "getAuthoritiesForUsernameResponse.md") })
    public SearchResults<GrantedAuthority> getAuthoritiesForUsername(
	    @ApiParam(value = "Unique username", required = true) @PathVariable String username,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	checkForAdminOrEditSelf(servletRequest, servletResponse, username);
	Tracer.start(TracerCategory.RestApiCall, "getAuthoritiesForUsername", LOGGER);
	try {
	    List<IGrantedAuthority> matches = getUserManagement().getGrantedAuthorities(username);
	    List<GrantedAuthority> converted = new ArrayList<GrantedAuthority>();
	    for (IGrantedAuthority auth : matches) {
		converted.add(GrantedAuthority.copy(auth));
	    }
	    return new SearchResults<GrantedAuthority>(converted);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * List devices that match given criteria.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List users matching criteria")
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Users.ListUsersResponse.class, description = "listUsersResponse.md") })
    public SearchResults<User> listUsers(
	    @ApiParam(value = "Include deleted", required = false) @RequestParam(defaultValue = "false") boolean includeDeleted,
	    @ApiParam(value = "Max records to return", required = false) @RequestParam(defaultValue = "100") int count,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	checkAuthForAll(servletRequest, servletResponse, SiteWhereAuthority.REST, SiteWhereAuthority.AdminUsers);
	Tracer.start(TracerCategory.RestApiCall, "listUsers", LOGGER);
	try {
	    List<User> usersConv = new ArrayList<User>();
	    UserSearchCriteria criteria = new UserSearchCriteria();
	    criteria.setIncludeDeleted(includeDeleted);
	    List<IUser> users = getUserManagement().listUsers(criteria);
	    for (IUser user : users) {
		usersConv.add(User.copy(user));
	    }
	    SearchResults<User> results = new SearchResults<User>(usersConv);
	    return results;
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Get tenants associated with a user.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{username:.+}/tenants", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List authorized tenants for user")
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Tenants.ListTenantsResponse.class, description = "getTenantsForUsernameResponse.md") })
    public List<ITenant> getTenantsForUsername(
	    @ApiParam(value = "Unique username", required = true) @PathVariable String username,
	    @ApiParam(value = "Include runtime info", required = false) @RequestParam(required = false, defaultValue = "false") boolean includeRuntimeInfo,
	    HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws SiteWhereException {
	checkForAdminOrEditSelf(servletRequest, servletResponse, username);
	Tracer.start(TracerCategory.RestApiCall, "getTenantsForUsername", LOGGER);

	// TODO: This should be in the system controller since it's not using
	// the user management implementation, but rather uses the runtime
	// tenant engine state to build the list.

	try {
	    List<ITenant> results = SiteWhere.getServer().getAuthorizedTenants(username, false);
	    if (includeRuntimeInfo) {
		for (ITenant tenant : results) {
		    ISiteWhereTenantEngine engine = SiteWhere.getServer().getTenantEngine(tenant.getId());
		    if (engine != null) {
			((Tenant) tenant).setEngineState(engine.getEngineState());
		    }
		}
	    }
	    return results;
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Check for privileges to use REST services + either admin all users or
     * admin self on the currently logged in user.
     * 
     * @param servletRequest
     * @param servletResponse
     * @param username
     * @throws SiteWhereException
     */
    public static void checkForAdminOrEditSelf(HttpServletRequest servletRequest, HttpServletResponse servletResponse,
	    String username) throws SiteWhereException {
	checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.REST, true);
	if (!checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.AdminUsers, false)) {
	    IUser loggedIn = LoginManager.getCurrentlyLoggedInUser();
	    if ((loggedIn == null) || (!loggedIn.getUsername().equals(username))) {
		throw new SiteWhereSystemException(ErrorCode.OperationNotPermitted, ErrorLevel.ERROR,
			HttpServletResponse.SC_FORBIDDEN);
	    } else {
		checkAuthFor(servletRequest, servletResponse, SiteWhereAuthority.AdminSelf, true);
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
	return SiteWhere.getServer().getUserManagement();
    }
}