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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.rest.model.area.request.ZoneCreateRequest;
import com.sitewhere.rest.model.search.device.ZoneSearchCriteria;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.annotation.SiteWhereCrossOrigin;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for site operations.
 * 
 * @author Derek Adams
 */
@RestController
@SiteWhereCrossOrigin
@RequestMapping(value = "/zones")
@Api(value = "zones")
public class Zones extends RestControllerBase {

    /**
     * Create a new zone.
     * 
     * @param areaToken
     * @param request
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.POST)
    @ApiOperation(value = "Create new zone")
    @Secured({ SiteWhereRoles.REST })
    public IZone createZone(@RequestBody ZoneCreateRequest request) throws SiteWhereException {
	return getDeviceManagement().createZone(request);
    }

    /**
     * Get zone based on unique token.
     * 
     * @param zoneToken
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{zoneToken}", method = RequestMethod.GET)
    @ApiOperation(value = "Get zone by token")
    @Secured({ SiteWhereRoles.REST })
    public IZone getZone(
	    @ApiParam(value = "Unique token that identifies zone", required = true) @PathVariable String zoneToken,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	return assertZone(zoneToken);
    }

    /**
     * Update information for a zone.
     * 
     * @param input
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{zoneToken}", method = RequestMethod.PUT)
    @ApiOperation(value = "Update an existing zone")
    @Secured({ SiteWhereRoles.REST })
    public IZone updateZone(
	    @ApiParam(value = "Unique token that identifies zone", required = true) @PathVariable String zoneToken,
	    @RequestBody ZoneCreateRequest request, HttpServletRequest servletRequest) throws SiteWhereException {
	IZone existing = assertZone(zoneToken);
	return getDeviceManagement().updateZone(existing.getId(), request);
    }

    /**
     * List zones that match criteria.
     * 
     * @param areaToken
     * @param page
     * @param pageSize
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List zones that match criteria")
    @Secured({ SiteWhereRoles.REST })
    public ISearchResults<IZone> listZones(
	    @ApiParam(value = "Token that identifies an area", required = false) @RequestParam(required = false) String areaToken,
	    @ApiParam(value = "Page number", required = false) @RequestParam(required = false, defaultValue = "1") int page,
	    @ApiParam(value = "Page size", required = false) @RequestParam(required = false, defaultValue = "100") int pageSize)
	    throws SiteWhereException {
	ZoneSearchCriteria criteria = new ZoneSearchCriteria(page, pageSize);

	// If area token specified, look up area.
	if (areaToken != null) {
	    IArea area = getDeviceManagement().getAreaByToken(areaToken);
	    if (area == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidAreaToken, ErrorLevel.ERROR);
	    }
	    criteria.setAreaId(area.getId());
	}

	return getDeviceManagement().listZones(criteria);
    }

    /**
     * Delete an existing zone.
     * 
     * @param zoneToken
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{zoneToken}", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete zone by unique token")
    @Secured({ SiteWhereRoles.REST })
    public IZone deleteZone(
	    @ApiParam(value = "Unique token that identifies zone", required = true) @PathVariable String zoneToken)
	    throws SiteWhereException {
	IZone existing = assertZone(zoneToken);
	return getDeviceManagement().deleteZone(existing.getId());
    }

    /**
     * Get zone associated with token or throw an exception if invalid.
     * 
     * @param token
     * @return
     * @throws SiteWhereException
     */
    protected IZone assertZone(String token) throws SiteWhereException {
	IZone zone = getDeviceManagement().getZoneByToken(token);
	if (zone == null) {
	    throw new SiteWhereSystemException(ErrorCode.InvalidZoneToken, ErrorLevel.ERROR);
	}
	return zone;
    }

    private IDeviceManagement getDeviceManagement() {
	return getMicroservice().getDeviceManagementApiDemux().getApiChannel();
    }
}