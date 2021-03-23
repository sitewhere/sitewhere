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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.rest.model.user.RoleSearchCriteria;
import com.sitewhere.rest.model.user.request.RoleCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.user.IUserManagement;
import com.sitewhere.spi.user.IRole;

import io.swagger.annotations.Api;

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
    private IInstanceManagementMicroservice microservice;

    /**
     * Get role by name.
     *
     * @param roleName
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{roleName}")
    @Operation(summary = "Get role by name", description = "Get role by name")
    public Response getRoleByName(
	    @Parameter(description = "Unique roleName", required = true) @PathParam("roleName") String roleName)
	    throws SiteWhereException {
	IRole match = getUserManagement().getRoleByName(roleName);
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
    public Response deleteRoleByRoleName(
	    @Parameter(description = "Unique rolName", required = true) @PathParam("roleName") String roleName)
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

    /**
     * Update an existing role.
     *
     * @param roleName
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{roleName}")
    @Operation(summary = "Update existing user", description = "Update existing user")
    public Response updateUser(
	    @Parameter(description = "Unique roleName", required = true) @PathParam("roleName") String roleName,
	    @RequestBody RoleCreateRequest input) throws SiteWhereException {

	return Response.ok(getUserManagement().updateRole(roleName, input)).build();
    }

    protected IUserManagement getUserManagement() throws SiteWhereException {
	return getMicroservice().getUserManagement();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}