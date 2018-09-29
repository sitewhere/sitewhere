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

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.asset.marshaling.AssetMarshalHelper;
import com.sitewhere.rest.model.asset.request.AssetCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.asset.AssetSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.label.ILabelGeneration;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for asset operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/assets")
@Api(value = "assets")
public class Assets extends RestControllerBase {

    /**
     * Create a new asset.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    @ApiOperation(value = "Create a new asset")
    public IAsset createAsset(@RequestBody AssetCreateRequest request) throws SiteWhereException {
	return getAssetManagement().createAsset(request);
    }

    /**
     * Get information for an asset based on token.
     * 
     * @param assetToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping(value = "/{assetToken:.+}")
    @ApiOperation(value = "Get asset by token")
    public IAsset getAssetByToken(@ApiParam(value = "Asset token", required = true) @PathVariable String assetToken)
	    throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	AssetMarshalHelper helper = new AssetMarshalHelper(getAssetManagement());
	helper.setIncludeAssetType(true);
	return helper.convert(existing);
    }

    /**
     * Update an existing asset.
     * 
     * @param assetToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping(value = "/{assetToken:.+}")
    @ResponseBody
    @ApiOperation(value = "Update an existing hardware asset in category")
    public IAsset updateAsset(@ApiParam(value = "Asset token", required = true) @PathVariable String assetToken,
	    @RequestBody AssetCreateRequest request) throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	return getAssetManagement().updateAsset(existing.getId(), request);
    }

    /**
     * Get label for asset based on a specific generator.
     * 
     * @param assetToken
     * @param generatorId
     * @param servletRequest
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @GetMapping(value = "/{assetToken}/label/{generatorId}")
    @ApiOperation(value = "Get label for area")
    public ResponseEntity<byte[]> getAssetLabel(
	    @ApiParam(value = "Asset token", required = true) @PathVariable String assetToken,
	    @ApiParam(value = "Generator id", required = true) @PathVariable String generatorId,
	    HttpServletRequest servletRequest, HttpServletResponse response) throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	ILabel label = getLabelGeneration().getAssetLabel(generatorId, existing.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	final HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.IMAGE_PNG);
	return new ResponseEntity<byte[]>(label.getContent(), headers, HttpStatus.OK);
    }

    /**
     * List assets matching criteria.
     * 
     * @param includeAssetType
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    @ApiOperation(value = "List assets matching criteria")
    public ISearchResults<IAsset> listAssets(
	    @ApiParam(value = "Limit by asset type", required = false) @RequestParam(required = false) String assetTypeToken,
	    @ApiParam(value = "Include asset type", required = false) @RequestParam(defaultValue = "false") boolean includeAssetType,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize)
	    throws SiteWhereException {
	// Build criteria.
	AssetSearchCriteria criteria = new AssetSearchCriteria(page, pageSize);

	// If limiting by asset type, look up asset type.
	if (assetTypeToken != null) {
	    IAssetType assetType = getAssetManagement().getAssetTypeByToken(assetTypeToken);
	    if (assetType == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidAssetTypeToken, ErrorLevel.ERROR);
	    }
	    criteria.setAssetTypeId(assetType.getId());
	}

	// Perform search.
	ISearchResults<IAsset> matches = getAssetManagement().listAssets(criteria);
	AssetMarshalHelper helper = new AssetMarshalHelper(getAssetManagement());
	helper.setIncludeAssetType(includeAssetType);

	List<IAsset> results = new ArrayList<IAsset>();
	for (IAsset asset : matches.getResults()) {
	    results.add(helper.convert(asset));
	}
	return new SearchResults<IAsset>(results, matches.getNumResults());
    }

    /**
     * Delete information for an asset based on token.
     * 
     * @param assetToken
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping(value = "/{assetToken:.+}")
    @ApiOperation(value = "Delete asset by token")
    public IAsset deleteAsset(@ApiParam(value = "Asset token", required = true) @PathVariable String assetToken)
	    throws SiteWhereException {
	IAsset existing = assureAsset(assetToken);
	return getAssetManagement().deleteAsset(existing.getId());
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

    private IAssetManagement getAssetManagement() throws SiteWhereException {
	return getMicroservice().getAssetManagementApiDemux().getApiChannel();
    }

    private ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiDemux().getApiChannel();
    }
}