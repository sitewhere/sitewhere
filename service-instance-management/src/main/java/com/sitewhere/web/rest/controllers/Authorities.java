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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.GrantedAuthoritySearchCriteria;
import com.sitewhere.rest.model.user.request.GrantedAuthorityCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.user.IUserManagement;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.web.rest.model.GrantedAuthorityHierarchyBuilder;

import io.swagger.annotations.Api;

/**
 * Controller for user operations.
 */
@Path("/api/authorities")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "authorities")
@Tag(name = "User Granted Authorities", description = "Granted authorities define operations which are authorized for a user.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class Authorities {

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new authority.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Operation(summary = "Create a new authority", description = "Create a new authority")
    public Response createAuthority(@RequestBody GrantedAuthorityCreateRequest input) throws SiteWhereException {
	IGrantedAuthority auth = getUserManagement().createGrantedAuthority(input);
	return Response.ok(GrantedAuthority.copy(auth)).build();
    }

    /**
     * Get an authority by unique name.
     * 
     * @param name
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{name}")
    @Operation(summary = "Get authority by id", description = "Get authority by id")
    public Response getAuthorityByName(
	    @Parameter(description = "Authority name", required = true) @PathParam("name") String name)
	    throws SiteWhereException {
	// checkAuthForAll(SiteWhereAuthority.REST, SiteWhereAuthority.AdminUsers);
	IGrantedAuthority auth = getUserManagement().getGrantedAuthorityByName(name);
	if (auth == null) {
	    return Response.status(Status.NOT_FOUND).build();
	}
	return Response.ok(GrantedAuthority.copy(auth)).build();
    }

    /**
     * List authorities that match given criteria.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Operation(summary = "List authorities that match criteria", description = "List authorities that match criteria")
    public Response listAuthorities() throws SiteWhereException {
	GrantedAuthoritySearchCriteria criteria = new GrantedAuthoritySearchCriteria();
	return Response.ok(getUserManagement().listGrantedAuthorities(criteria)).build();
    }

    /**
     * Get the hierarchy of granted authorities.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/hierarchy")
    @Operation(summary = "Get authorities hierarchy", description = "Get authorities hierarchy")
    public Response getAuthoritiesHierarchy() throws SiteWhereException {
	GrantedAuthoritySearchCriteria criteria = new GrantedAuthoritySearchCriteria(1, 0);
	ISearchResults<IGrantedAuthority> auths = getUserManagement().listGrantedAuthorities(criteria);
	return Response.ok(GrantedAuthorityHierarchyBuilder.build(auths.getResults())).build();
    }

    /**
     * Get user management implementation.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IUserManagement getUserManagement() throws SiteWhereException {
	return getMicroservice().getUserManagement();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}