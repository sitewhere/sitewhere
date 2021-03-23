/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.web.rest.controllers;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.user.UserSearchCriteria;
import com.sitewhere.rest.model.user.Role;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.user.IUserManagement;
import com.sitewhere.spi.user.IRole;
import com.sitewhere.spi.user.IUser;

import io.swagger.annotations.Api;

/**
 * Controller for user operations.
 */
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "users")
@Tag(name = "Users", description = "Users provide context for authenticating access to an instance.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class Users {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Users.class);

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new user.
     *
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Operation(summary = "Create new user", description = "Create new user")
    public Response createUser(@RequestBody UserCreateRequest input) throws SiteWhereException {
	return Response.ok(getUserManagement().createUser(input)).build();
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
    @Operation(summary = "Update existing user", description = "Update existing user")
    public Response updateUser(
	    @Parameter(description = "Unique username", required = true) @PathParam("username") String username,
	    @RequestBody UserCreateRequest input) throws SiteWhereException {
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
    @Operation(summary = "Get user by username", description = "Get user by username")
    public Response getUserByUsername(
	    @Parameter(description = "Unique username", required = true) @PathParam("username") String username)
	    throws SiteWhereException {
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
    @Operation(summary = "Delete user by username", description = "Delete user by username")
    public Response deleteUserByUsername(
	    @Parameter(description = "Unique username", required = true) @PathParam("username") String username)
	    throws SiteWhereException {
	return Response.ok(getUserManagement().deleteUser(username)).build();
    }

    /**
     * List users matching criteria.
     *
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Operation(summary = "List users matching criteria", description = "List users matching criteria")
    public Response listUsers() throws SiteWhereException {
	UserSearchCriteria criteria = new UserSearchCriteria();
	return Response.ok(getUserManagement().listUsers(criteria)).build();
    }

    /**
     * Get a list of detailed role information for a given user.
     *
     * @param username
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{username}/roles")
    @Operation(summary = "Get roles for user", description = "Get roles for user")
    public Response getRolesForUsername(
	    @Parameter(description = "Unique username", required = true) @PathParam("username") String username)
	    throws SiteWhereException {
	List<IRole> matches = getUserManagement().getRoles(username);
	List<Role> converted = new ArrayList<>();
	for (IRole role : matches) {
	    converted.add(Role.copy(role));
	}
	return Response.ok(new SearchResults<Role>(converted)).build();
    }

    /**
     * add roles to users
     *
     * @param roles
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{username}/roles")
    @Operation(summary = "Add roles to users", description = "Add roles to users")
    public Response addRoles(
	    @Parameter(description = "Unique username", required = true) @PathParam("username") String username,
	    @RequestBody String[] roles) throws SiteWhereException {
	if ((roles == null) || (roles.length == 0)) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidUserInformation, ErrorLevel.ERROR);
	}
	return Response.ok(getUserManagement().addRoles(username, Arrays.asList(roles))).build();
    }

    /**
     * remove roles to users
     *
     * @param roles
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{username}/roles")
    @Operation(summary = "Delete roles to users", description = "Delete roles to users")
    public Response removeRoles(
	    @Parameter(description = "Unique username", required = true) @PathParam("username") String username,
	    @RequestBody String[] roles) throws SiteWhereException {
	if ((roles == null) || (roles.length == 0)) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidUserInformation, ErrorLevel.ERROR);
	}

	return Response.ok(getUserManagement().removeRoles(username, Arrays.asList(roles))).build();
    }

    protected IUserManagement getUserManagement() throws SiteWhereException {
	return getMicroservice().getUserManagement();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}