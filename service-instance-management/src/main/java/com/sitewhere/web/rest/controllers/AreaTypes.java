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
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
import com.sitewhere.microservice.api.device.AreaTypeMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.area.request.AreaTypeCreateRequest;
import com.sitewhere.rest.model.device.marshaling.MarshaledAreaType;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.ApiOperation;

/**
 * Controller for area type operations.
 */
@Path("/api/areatypes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Area Types", description = "Area types define common characteristics for related areas.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class AreaTypes {

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Create an area type.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Operation(summary = "Create area type", description = "Create new area type")
    public Response createAreaType(@RequestBody AreaTypeCreateRequest input) throws SiteWhereException {
	AreaTypeMarshalHelper helper = new AreaTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedAreaTypes(true);
	MarshaledAreaType marshaled = helper.convert(getDeviceManagement().createAreaType(input));
	return Response.ok(marshaled).build();
    }

    /**
     * Get information for a given area type based on token.
     * 
     * @param areaTypeToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{areaTypeToken}")
    @Operation(summary = "Get area type by token", description = "Get area type by its unqiue token")
    public Response getAreaTypeByToken(
	    @Parameter(description = "Token that identifies area type", required = true) @PathParam("areaTypeToken") String areaTypeToken)
	    throws SiteWhereException {
	AreaTypeMarshalHelper helper = new AreaTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedAreaTypes(true);
	MarshaledAreaType marshaled = helper.convert(assertAreaType(areaTypeToken));
	return Response.ok(marshaled).build();
    }

    /**
     * Update information for an area type.
     * 
     * @param areaTypeToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{areaTypeToken}")
    @Operation(summary = "Update area type", description = "Update an existing area type")
    public Response updateAreaType(
	    @Parameter(description = "Token that identifies area type", required = true) @PathParam("areaTypeToken") String areaTypeToken,
	    @RequestBody AreaTypeCreateRequest request) throws SiteWhereException {
	IAreaType existing = assertAreaType(areaTypeToken);
	AreaTypeMarshalHelper helper = new AreaTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedAreaTypes(true);
	MarshaledAreaType marshaled = helper.convert(getDeviceManagement().updateAreaType(existing.getId(), request));
	return Response.ok(marshaled).build();
    }

    /**
     * Get label for area type based on a specific generator.
     * 
     * @param areaTypeToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{areaTypeToken}/label/{generatorId}")
    @Produces("image/png")
    @Operation(summary = "Get area type label", description = "Get label for an area type")
    public Response getAreaTypeLabel(
	    @Parameter(description = "Token that identifies area type", required = true) @PathParam("areaTypeToken") String areaTypeToken,
	    @Parameter(description = "Generator id", required = true) @PathParam("generatorId") String generatorId)
	    throws SiteWhereException {
	IAreaType existing = assertAreaType(areaTypeToken);
	ILabel label = getLabelGeneration().getAreaTypeLabel(generatorId, existing.getId());
	if (label == null) {
	    return Response.status(Status.NOT_FOUND).build();
	}
	return Response.ok(label.getContent()).build();
    }

    /**
     * List area types matching criteria.
     * 
     * @param includeContainedAreaTypes
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Operation(summary = "List area types", description = "List area types matching criteria")
    public Response listAreaTypes(
	    @Parameter(description = "Include contained area types", required = false) @QueryParam("includeContainedAreaTypes") @DefaultValue("false") boolean includeContainedAreaTypes,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<? extends IAreaType> matches = getDeviceManagement().listAreaTypes(criteria);

	AreaTypeMarshalHelper helper = new AreaTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedAreaTypes(includeContainedAreaTypes);

	List<IAreaType> results = new ArrayList<IAreaType>();
	for (IAreaType area : matches.getResults()) {
	    results.add(helper.convert(area));
	}
	return Response.ok(new SearchResults<IAreaType>(results, matches.getNumResults())).build();
    }

    /**
     * Delete information for an area type based on token.
     * 
     * @param areaTypeToken
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{areaTypeToken}")
    @Operation(summary = "Delete area type by token", description = "Delete area type by token")
    @ApiOperation(value = "")
    public Response deleteAreaType(
	    @Parameter(description = "Token that identifies area type", required = true) @PathParam("areaTypeToken") String areaTypeToken)
	    throws SiteWhereException {
	IAreaType existing = assertAreaType(areaTypeToken);
	return Response.ok(getDeviceManagement().deleteAreaType(existing.getId())).build();
    }

    /**
     * Get area type associated with token or throw an exception if invalid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IAreaType assertAreaType(String token) throws SiteWhereException {
	IAreaType type = getDeviceManagement().getAreaTypeByToken(token);
	if (type == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAreaTypeToken, ErrorLevel.ERROR);
	}
	return type;
    }

    protected IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagement();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}