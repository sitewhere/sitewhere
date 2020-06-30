/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.user.IUserManagement;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.user.UserSearchCriteria;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.Role;
import com.sitewhere.rest.model.user.RoleSearchCriteria;
import com.sitewhere.rest.model.user.request.RoleCreateRequest;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.user.AccountStatus;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IRole;
import com.sitewhere.spi.user.IUser;
import io.swagger.annotations.Api;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for user operations.
 */
@Path("/api/roles")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "roles")
@Tag(name = "Roles", description = "Roles")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class Roles {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Roles.class);

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Get role by name.
     *
     * @param roleName
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{roleName}/roles")
    @Operation(summary = "Get role by name", description = "Get role by name")
    public Response getRoleByName(
		    @Parameter(description = "Unique roleName", required = true) @PathParam("roleName") String roleName)
		    throws SiteWhereException {
	IRole match = getUserManagement().getRoleByName(roleName);
	List<Role> converted = new ArrayList<Role>();
	return Response.ok(match).build();
    }

    @GET
    @Operation(summary = "List roles matching criteria", description = "List Roles matching criteria")
    public Response listRoles() throws SiteWhereException {
	RoleSearchCriteria criteria = new RoleSearchCriteria();
	return Response.ok(getUserManagement().listRoles(criteria)).build();
    }

    /**
     * Delete information for a given user based on username.
     *
     * @param roleName
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{roleName}")
    @Operation(summary = "Delete role by roleName", description = "Delete role by roleName")
    public Response deleteUserByUsername(
		    @Parameter(description = "Unique username", required = true) @PathParam("roleName") String roleName)
		    throws SiteWhereException {
        getUserManagement().deleteRole(roleName);
	return Response.ok().build();
    }

    /**
     * Create a new role.
     *
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Operation(summary = "Create new role", description = "Create new role")
    public Response createRole(@RequestBody RoleCreateRequest input) throws SiteWhereException {
	if ((input.getRole() == null)) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidUserInformation, ErrorLevel.ERROR);
	}
	return Response.ok(getUserManagement().createRole(input)).build();
    }

    protected IUserManagement getUserManagement() throws SiteWhereException {
	return getMicroservice().getUserManagement();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}