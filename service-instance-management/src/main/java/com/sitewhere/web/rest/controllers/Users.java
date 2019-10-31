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

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.user.UserSearchCriteria;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.user.AccountStatus;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Controller for user operations.
 * 
 * @author Derek Adams
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "users")
public class Users {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Users.class);

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Create a new user.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @POST
    @ApiOperation(value = "Create new user")
    public Response createUser(@RequestBody UserCreateRequest input) throws SiteWhereException {
	// checkAuthForAll(SiteWhereAuthority.REST, SiteWhereAuthority.AdminUsers);
	if ((input.getUsername() == null) || (input.getPassword() == null) || (input.getFirstName() == null)
		|| (input.getLastName() == null)) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidUserInformation, ErrorLevel.ERROR);
	}
	if (input.getStatus() == null) {
	    input.setStatus(AccountStatus.Active);
	}
	return Response.ok(getUserManagement().createUser(input, true)).build();
    }

    /**
     * Update an existing user.
     * 
     * @param username
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{username}")
    @ApiOperation(value = "Update existing user.")
    public Response updateUser(
	    @ApiParam(value = "Unique username", required = true) @PathParam("username") String username,
	    @RequestBody UserCreateRequest input) throws SiteWhereException {
	checkForAdminOrEditSelf(username);
	return Response.ok(getUserManagement().updateUser(username, input, true)).build();
    }

    /**
     * Get a user by unique username.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{username}")
    @ApiOperation(value = "Get user by username")
    public Response getUserByUsername(
	    @ApiParam(value = "Unique username", required = true) @PathParam("username") String username)
	    throws SiteWhereException {
	checkForAdminOrEditSelf(username);
	IUser user = getUserManagement().getUserByUsername(username);
	if (user == null) {
	    return Response.status(Status.NOT_FOUND).build();
	}
	return Response.ok(user).build();
    }

    /**
     * Delete information for a given user based on username.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{username}")
    @ApiOperation(value = "Delete user by username")
    public Response deleteUserByUsername(
	    @ApiParam(value = "Unique username", required = true) @PathParam("username") String username)
	    throws SiteWhereException {
	// checkAuthForAll(SiteWhereAuthority.REST, SiteWhereAuthority.AdminUsers);
	return Response.ok(getUserManagement().deleteUser(username)).build();
    }

    /**
     * Get a list of detailed authority information for a given user.
     * 
     * @param username
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{username}/authorities")
    @ApiOperation(value = "Get authorities for user")
    public Response getAuthoritiesForUsername(
	    @ApiParam(value = "Unique username", required = true) @PathParam("username") String username)
	    throws SiteWhereException {
	checkForAdminOrEditSelf(username);
	List<IGrantedAuthority> matches = getUserManagement().getGrantedAuthorities(username);
	List<GrantedAuthority> converted = new ArrayList<GrantedAuthority>();
	for (IGrantedAuthority auth : matches) {
	    converted.add(GrantedAuthority.copy(auth));
	}
	return Response.ok(new SearchResults<GrantedAuthority>(converted)).build();
    }

    /**
     * List users matching criteria.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @ApiOperation(value = "List users matching criteria")
    public Response listUsers() throws SiteWhereException {
	UserSearchCriteria criteria = new UserSearchCriteria();
	return Response.ok(getUserManagement().listUsers(criteria)).build();
    }

    /**
     * Check for privileges to use REST services + either admin all users or admin
     * self on the currently logged in user.
     * 
     * @param username
     * @throws SiteWhereException
     */
    public static void checkForAdminOrEditSelf(String username) throws SiteWhereException {
	// checkAuthFor(SiteWhereAuthority.REST, true);
	// if (!checkAuthFor(SiteWhereAuthority.AdminUsers, false)) {
	// IUser loggedIn = UserContextManager.getCurrentlyLoggedInUser();
	// if ((loggedIn == null) || (!loggedIn.getUsername().equals(username))) {
	// throw operationNotPermitted();
	// } else {
	// checkAuthFor(SiteWhereAuthority.AdminSelf, true);
	// }
	// }
    }

    protected IUserManagement getUserManagement() throws SiteWhereException {
	return getMicroservice().getUserManagement();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}