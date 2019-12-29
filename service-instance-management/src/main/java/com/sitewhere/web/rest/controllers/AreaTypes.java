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

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.microservice.api.device.AreaTypeMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.area.request.AreaTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Controller for area type operations.
 */
@Path("/areatypes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "areatypes")
public class AreaTypes {

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Create an area type.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @POST
    @ApiOperation(value = "Create new area type")
    public Response createAreaType(@RequestBody AreaTypeCreateRequest input) throws SiteWhereException {
	return Response.ok(getDeviceManagement().createAreaType(input)).build();
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
    @ApiOperation(value = "Get area type by token")
    public Response getAreaTypeByToken(
	    @ApiParam(value = "Token that identifies area type", required = true) @PathParam("areaTypeToken") String areaTypeToken)
	    throws SiteWhereException {
	return Response.ok(assertAreaType(areaTypeToken)).build();
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
    @ApiOperation(value = "Update existing area type")
    public Response updateAreaType(
	    @ApiParam(value = "Token that identifies area type", required = true) @PathParam("areaTypeToken") String areaTypeToken,
	    @RequestBody AreaTypeCreateRequest request) throws SiteWhereException {
	IAreaType existing = assertAreaType(areaTypeToken);
	return Response.ok(getDeviceManagement().updateAreaType(existing.getId(), request)).build();
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
    @ApiOperation(value = "Get label for area type")
    public Response getAreaTypeLabel(
	    @ApiParam(value = "Token that identifies area type", required = true) @PathParam("areaTypeToken") String areaTypeToken,
	    @ApiParam(value = "Generator id", required = true) @PathParam("areaToken") String generatorId)
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
    @ApiOperation(value = "List area types matching criteria")
    public Response listAreaTypes(
	    @ApiParam(value = "Include contained area types", required = false) @QueryParam("includeContainedAreaTypes") @DefaultValue("false") boolean includeContainedAreaTypes,
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
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
    @ApiOperation(value = "Delete area type by token")
    public Response deleteAreaType(
	    @ApiParam(value = "Token that identifies area type", required = true) @PathParam("areaTypeToken") String areaTypeToken)
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
	return getMicroservice().getDeviceManagementApiChannel();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}