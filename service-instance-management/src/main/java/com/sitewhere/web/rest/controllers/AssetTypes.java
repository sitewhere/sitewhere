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
import com.sitewhere.microservice.api.asset.AssetTypeMarshalHelper;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.asset.request.AssetTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.asset.AssetTypeSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Controller for asset type operations.
 */
@Path("/assettypes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "assettypes")
public class AssetTypes {

    @Inject
    private IInstanceManagementMicroservice<?> microservice;

    /**
     * Create a new asset type.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @ApiOperation(value = "Create a new asset type")
    public Response createAssetType(@RequestBody AssetTypeCreateRequest request) throws SiteWhereException {
	return Response.ok(getAssetManagement().createAssetType(request)).build();
    }

    /**
     * Get information for an asset type based on token.
     * 
     * @param assetTypeToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{assetTypeToken}")
    @ApiOperation(value = "Get asset by token")
    public Response getAssetTypeByToken(
	    @ApiParam(value = "Asset type token", required = true) @PathParam("assetTypeToken") String assetTypeToken)
	    throws SiteWhereException {
	IAssetType existing = assureAssetType(assetTypeToken);
	AssetTypeMarshalHelper helper = new AssetTypeMarshalHelper(getAssetManagement());
	return Response.ok(helper.convert(existing)).build();
    }

    /**
     * Update an existing asset type.
     * 
     * @param assetTypeToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{assetTypeToken}")
    @ApiOperation(value = "Update an existing asset type")
    public Response updateAssetType(
	    @ApiParam(value = "Asset type token", required = true) @PathParam("assetTypeToken") String assetTypeToken,
	    @RequestBody AssetTypeCreateRequest request) throws SiteWhereException {
	IAssetType existing = assureAssetType(assetTypeToken);
	return Response.ok(getAssetManagement().updateAssetType(existing.getId(), request)).build();
    }

    /**
     * Get label for asset type based on a specific generator.
     * 
     * @param assetTypeToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{areaTypeToken}/label/{generatorId}")
    @Produces("image/png")
    @ApiOperation(value = "Get label for asset type")
    public Response getAssignmentLabel(
	    @ApiParam(value = "Asset type token", required = true) @PathParam("assetTypeToken") String assetTypeToken,
	    @ApiParam(value = "Generator id", required = true) @PathParam("v") String generatorId)
	    throws SiteWhereException {
	IAssetType existing = assureAssetType(assetTypeToken);
	ILabel label = getLabelGeneration().getAssetTypeLabel(generatorId, existing.getId());
	if (label == null) {
	    return Response.status(Status.NOT_FOUND).build();
	}
	return Response.ok(label.getContent()).build();
    }

    /**
     * List asset types matching criteria.
     * 
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @ApiOperation(value = "List asset types matching criteria")
    public Response listAssetTypes(
	    @ApiParam(value = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @ApiParam(value = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	// Build criteria.
	AssetTypeSearchCriteria criteria = new AssetTypeSearchCriteria(page, pageSize);

	// Perform search.
	ISearchResults<IAssetType> matches = getAssetManagement().listAssetTypes(criteria);
	AssetTypeMarshalHelper helper = new AssetTypeMarshalHelper(getAssetManagement());

	List<IAssetType> results = new ArrayList<IAssetType>();
	for (IAssetType assetType : matches.getResults()) {
	    results.add(helper.convert(assetType));
	}
	return Response.ok(new SearchResults<IAssetType>(results, matches.getNumResults())).build();
    }

    /**
     * Delete information for an asset type based on token.
     * 
     * @param assetTypeToken
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{assetTypeToken}")
    @ApiOperation(value = "Delete asset type by token")
    public Response deleteAsset(
	    @ApiParam(value = "Asset type token", required = true) @PathParam("assetTypeToken") String assetTypeToken)
	    throws SiteWhereException {
	IAssetType existing = assureAssetType(assetTypeToken);
	return Response.ok(getAssetManagement().deleteAssetType(existing.getId())).build();
    }

    /**
     * Find an asset type by token or throw an exception if not found.
     * 
     * @param assetTypeToken
     * @return
     * @throws SiteWhereException
     */
    private IAssetType assureAssetType(String assetTypeToken) throws SiteWhereException {
	IAssetType assetType = getAssetManagement().getAssetTypeByToken(assetTypeToken);
	if (assetType == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAssetTypeToken, ErrorLevel.ERROR);
	}
	return assetType;
    }

    protected IAssetManagement getAssetManagement() throws SiteWhereException {
	return getMicroservice().getAssetManagementApiChannel();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice<?> getMicroservice() {
	return microservice;
    }
}