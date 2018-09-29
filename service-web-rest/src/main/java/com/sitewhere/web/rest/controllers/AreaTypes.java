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
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.device.marshaling.AreaTypeMarshalHelper;
import com.sitewhere.rest.model.area.request.AreaTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.device.IDeviceManagement;
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
 * Controller for area type operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/areatypes")
@Api(value = "areatypes")
public class AreaTypes extends RestControllerBase {

    /**
     * Create an area type.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @PostMapping
    @ApiOperation(value = "Create new area type")
    public IAreaType createAreaType(@RequestBody AreaTypeCreateRequest input) throws SiteWhereException {
	return getDeviceManagement().createAreaType(input);
    }

    /**
     * Get information for a given area type based on token.
     * 
     * @param areaTypeToken
     * @return
     * @throws SiteWhereException
     */
    @GetMapping(value = "/{areaTypeToken:.+}")
    @ApiOperation(value = "Get area type by token")
    public IAreaType getAreaTypeByToken(
	    @ApiParam(value = "Token that identifies area type", required = true) @PathVariable String areaTypeToken)
	    throws SiteWhereException {
	return assertAreaType(areaTypeToken);
    }

    /**
     * Update information for an area type.
     * 
     * @param areaTypeToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @PutMapping(value = "/{areaTypeToken:.+}")
    @ApiOperation(value = "Update existing area type")
    public IAreaType updateAreaType(
	    @ApiParam(value = "Token that identifies area type", required = true) @PathVariable String areaTypeToken,
	    @RequestBody AreaTypeCreateRequest request) throws SiteWhereException {
	IAreaType existing = assertAreaType(areaTypeToken);
	return getDeviceManagement().updateAreaType(existing.getId(), request);
    }

    /**
     * Get label for area type based on a specific generator.
     * 
     * @param areaTypeToken
     * @param generatorId
     * @param servletRequest
     * @param response
     * @return
     * @throws SiteWhereException
     */
    @GetMapping(value = "/{areaTypeToken}/label/{generatorId}")
    @ApiOperation(value = "Get label for area type")
    public ResponseEntity<byte[]> getAreaTypeLabel(
	    @ApiParam(value = "Token that identifies area type", required = true) @PathVariable String areaTypeToken,
	    @ApiParam(value = "Generator id", required = true) @PathVariable String generatorId,
	    HttpServletRequest servletRequest, HttpServletResponse response) throws SiteWhereException {
	IAreaType existing = assertAreaType(areaTypeToken);
	ILabel label = getLabelGeneration().getAreaTypeLabel(generatorId, existing.getId());

	final HttpHeaders headers = new HttpHeaders();
	headers.setContentType(MediaType.IMAGE_PNG);
	return new ResponseEntity<byte[]>(label.getContent(), headers, HttpStatus.OK);
    }

    /**
     * List all area types.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    @ApiOperation(value = "List area types matching criteria")
    public ISearchResults<IAreaType> listAreaTypes(
	    @ApiParam(value = "Include contained area types", required = false) @RequestParam(defaultValue = "false") boolean includeContainedAreaTypes,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<IAreaType> matches = getDeviceManagement().listAreaTypes(criteria);

	AreaTypeMarshalHelper helper = new AreaTypeMarshalHelper(getDeviceManagement());
	helper.setIncludeContainedAreaTypes(includeContainedAreaTypes);

	List<IAreaType> results = new ArrayList<IAreaType>();
	for (IAreaType area : matches.getResults()) {
	    results.add(helper.convert(area));
	}
	return new SearchResults<IAreaType>(results, matches.getNumResults());
    }

    /**
     * Delete information for an area type based on token.
     * 
     * @param areaTypeToken
     * @return
     * @throws SiteWhereException
     */
    @DeleteMapping(value = "/{areaTypeToken:.+}")
    @ApiOperation(value = "Delete area type by token")
    public IAreaType deleteAreaType(
	    @ApiParam(value = "Token that identifies area type", required = true) @PathVariable String areaTypeToken)
	    throws SiteWhereException {
	IAreaType existing = assertAreaType(areaTypeToken);
	return getDeviceManagement().deleteAreaType(existing.getId());
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

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiDemux().getApiChannel();
    }

    private ILabelGeneration getLabelGeneration() {
	return getMicroservice().getLabelGenerationApiDemux().getApiChannel();
    }
}