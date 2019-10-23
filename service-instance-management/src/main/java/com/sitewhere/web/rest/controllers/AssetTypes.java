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

import com.sitewhere.asset.marshaling.AssetTypeMarshalHelper;
import com.sitewhere.rest.model.asset.request.AssetTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.asset.AssetTypeSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
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
 * Controller for asset type operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/assettypes")
@Api(value = "assettypes")
public class AssetTypes extends RestControllerBase {

    /**
     * Create a new asset type.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    @ApiOperation(value = "Create a new asset type")
    public IAssetType createAssetType(@RequestBody AssetTypeCreateRequest request) throws SiteWhereException {
	return getAssetManagement().createAssetType(request);
    }

    /**
     * Get information for an asset type based on token.
     * 
     * @param assetTypeToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping(value = "/{assetTypeToken:.+}")
    @ApiOperation(value = "Get asset by token")
    public IAssetType getAssetTypeByToken(
	    @ApiParam(value = "Asset type token", required = true) @PathVariable String assetTypeToken)
	    throws SiteWhereException {
	IAssetType existing = assureAssetType(assetTypeToken);
	AssetTypeMarshalHelper helper = new AssetTypeMarshalHelper(getAssetManagement());
	return helper.convert(existing);
    }

    /**
     * Update an existing asset type.
     * 
     * @param assetTypeToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping(value = "/{assetTypeToken:.+}")
    @ResponseBody
    @ApiOperation(value = "Update an existing hardware asset in category")
    public IAssetType updateAssetType(
	    @ApiParam(value = "Asset type token", required = true) @PathVariable String assetTypeToken,
	    @RequestBody AssetTypeCreateRequest request) throws SiteWhereException {
	IAssetType existing = assureAssetType(assetTypeToken);
	return getAssetManagement().updateAssetType(existing.getId(), request);
    }

    /**
     * Get label for asset type based on a specific generator.
     * 
     * @param assetTypeToken
     * @param generatorId
     * @param servletRequest
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @GetMapping(value = "/{assetTypeToken}/label/{generatorId}")
    @ApiOperation(value = "Get label for area")
    public ResponseEntity<byte[]> getAssignmentLabel(
	    @ApiParam(value = "Asset type token", required = true) @PathVariable String assetTypeToken,
	    @ApiParam(value = "Generator id", required = true) @PathVariable String generatorId,
	    HttpServletRequest servletRequest, HttpServletResponse response) throws SiteWhereException {
	IAssetType existing = assureAssetType(assetTypeToken);
	ILabel label = getLabelGeneration().getAssetTypeLabel(generatorId, existing.getId());
	if (label == null) {
	    return ResponseEntity.notFound().build();
	}
	final HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.IMAGE_PNG);
	return new ResponseEntity<byte[]>(label.getContent(), headers, HttpStatus.OK);
    }

    /**
     * List asset types matching criteria.
     * 
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    @ApiOperation(value = "List asset types matching criteria")
    public ISearchResults<IAssetType> listAssetTypes(
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize)
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
	return new SearchResults<IAssetType>(results, matches.getNumResults());
    }

    /**
     * Delete information for an asset type based on token.
     * 
     * @param assetTypeToken
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping(value = "/{assetTypeToken:.+}")
    @ApiOperation(value = "Delete asset type by token")
    public IAssetType deleteAsset(
	    @ApiParam(value = "Asset type token", required = true) @PathVariable String assetTypeToken)
	    throws SiteWhereException {
	IAssetType existing = assureAssetType(assetTypeToken);
	return getAssetManagement().deleteAssetType(existing.getId());
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

    private IAssetManagement getAssetManagement() throws SiteWhereException {
	return getMicroservice().getAssetManagementApiChannel();
    }

    private ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiChannel();
    }
}