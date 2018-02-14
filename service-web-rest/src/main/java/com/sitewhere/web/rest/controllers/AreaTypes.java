/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.rest.model.area.request.AreaTypeCreateRequest;
import com.sitewhere.rest.model.search.SearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
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
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
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
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create new area type")
    @Secured({ SiteWhereRoles.REST })
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
    @RequestMapping(value = "/{areaTypeToken}", method = RequestMethod.GET)
    @ApiOperation(value = "Get area type by token")
    @Secured({ SiteWhereRoles.REST })
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
    @RequestMapping(value = "/{areaTypeToken}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update existing area")
    @Secured({ SiteWhereRoles.REST })
    public IAreaType updateAreaType(
	    @ApiParam(value = "Token that identifies area type", required = true) @PathVariable String areaTypeToken,
	    @RequestBody AreaTypeCreateRequest request) throws SiteWhereException {
	IAreaType existing = assertAreaType(areaTypeToken);
	return getDeviceManagement().updateAreaType(existing.getId(), request);
    }

    /**
     * List all area types.
     * 
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List area types matching criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IAreaType> listAreaTypes(
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	SearchCriteria criteria = new SearchCriteria(page, pageSize);
	ISearchResults<IAreaType> matches = getDeviceManagement().listAreaTypes(criteria);
	return matches;
    }

    /**
     * Delete information for an area type based on token.
     * 
     * @param areaTypeToken
     * @param force
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{areaTypeToken}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete area by token")
    @Secured({ SiteWhereRoles.REST })
    public IAreaType deleteAreaType(
	    @ApiParam(value = "Token that identifies area type", required = true) @PathVariable String areaTypeToken,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IAreaType existing = assertAreaType(areaTypeToken);
	return getDeviceManagement().deleteAreaType(existing.getId(), force);
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
}