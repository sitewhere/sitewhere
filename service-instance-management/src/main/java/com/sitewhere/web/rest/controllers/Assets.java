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
import com.sitewhere.microservice.api.asset.AssetMarshalHelper;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.label.ILabelGeneration;
import com.sitewhere.rest.model.asset.request.AssetCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.asset.AssetSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.search.ISearchResults;

import io.swagger.annotations.Api;

/**
 * Controller for asset operations.
 */
@Path("/api/assets")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "assets")
@Tag(name = "Assets", description = "Assets are used to associate physical objects with device assignments.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class Assets {

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Create a new asset.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @POST
    @Operation(summary = "Create a new asset", description = "Create a new asset")
    public Response createAsset(@RequestBody AssetCreateRequest request) throws SiteWhereException {
	return Response.ok(getAssetManagement().createAsset(request)).build();
    }

    /**
     * Get information for an asset based on token.
     * 
     * @param assetToken
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{assetToken}")
    @Operation(summary = "Get asset by token", description = "Get asset by token")
    public Response getAssetByToken(
	    @Parameter(description = "Asset token", required = true) @PathParam("assetToken") String assetToken)
	    throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	AssetMarshalHelper helper = new AssetMarshalHelper(getAssetManagement());
	helper.setIncludeAssetType(true);
	return Response.ok(helper.convert(existing)).build();
    }

    /**
     * Update an existing asset.
     * 
     * @param assetToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PUT
    @Path("/{assetToken}")
    @Operation(summary = "Update an existing asset", description = "Update details for an existing asset")
    public Response updateAsset(
	    @Parameter(description = "Asset token", required = true) @PathParam("assetToken") String assetToken,
	    @RequestBody AssetCreateRequest request) throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	return Response.ok(getAssetManagement().updateAsset(existing.getId(), request)).build();
    }

    /**
     * Get label for asset based on a specific generator.
     * 
     * @param assetToken
     * @param generatorId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{assetToken}/label/{generatorId}")
    @Produces("image/png")
    @Operation(summary = "Get label for asset", description = "Get label for asset")
    public Response getAssetLabel(
	    @Parameter(description = "Asset token", required = true) @PathParam("assetToken") String assetToken,
	    @Parameter(description = "Generator id", required = true) @PathParam("generatorId") String generatorId)
	    throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	ILabel label = getLabelGeneration().getAssetLabel(generatorId, existing.getId());
	if (label == null) {
	    return Response.status(Status.NOT_FOUND).build();
	}
	return Response.ok(label.getContent()).build();
    }

    /**
     * List assets matching criteria.
     * 
     * @param assetTypeToken
     * @param includeAssetType
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Operation(summary = "List assets matching criteria", description = "List assets matching criteria")
    public Response listAssets(
	    @Parameter(description = "Limit by asset type", required = false) @QueryParam("assetTypeToken") String assetTypeToken,
	    @Parameter(description = "Include asset type", required = false) @QueryParam("includeAssetType") @DefaultValue("false") boolean includeAssetType,
	    @Parameter(description = "Page number", required = false) @QueryParam("page") @DefaultValue("1") int page,
	    @Parameter(description = "Page size", required = false) @QueryParam("pageSize") @DefaultValue("100") int pageSize)
	    throws SiteWhereException {
	// Build criteria.
	AssetSearchCriteria criteria = new AssetSearchCriteria(page, pageSize);
	criteria.setAssetTypeToken(assetTypeToken);

	// Perform search.
	ISearchResults<? extends IAsset> matches = getAssetManagement().listAssets(criteria);
	AssetMarshalHelper helper = new AssetMarshalHelper(getAssetManagement());
	helper.setIncludeAssetType(includeAssetType);

	List<IAsset> results = new ArrayList<IAsset>();
	for (IAsset asset : matches.getResults()) {
	    results.add(helper.convert(asset));
	}
	return Response.ok(new SearchResults<IAsset>(results, matches.getNumResults())).build();
    }

    /**
     * Delete information for an asset based on token.
     * 
     * @param assetToken
     * @return
     * @throws SiteWhereException
     */
    @DELETE
    @Path("/{assetToken}")
    @Operation(summary = "Delete asset by token", description = "Delete asset by token")
    public Response deleteAsset(
	    @Parameter(description = "Asset token", required = true) @PathParam("assetToken") String assetToken)
	    throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	return Response.ok(getAssetManagement().deleteAsset(existing.getId())).build();
    }

    /**
     * Find an asset by token or throw an exception if not found.
     * 
     * @param assetId
     * @return
     * @throws SiteWhereException
     */
    private IAsset assureAsset(String assetToken) throws SiteWhereException {
	IAsset asset = getAssetManagement().getAssetByToken(assetToken);
	if (asset == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidAssetToken, ErrorLevel.ERROR);
	}
	return asset;
    }

    protected IAssetManagement getAssetManagement() throws SiteWhereException {
	return getMicroservice().getAssetManagement();
    }

    protected ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}