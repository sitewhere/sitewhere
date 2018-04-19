/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.rest.model.area.request.ZoneCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
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

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(Zones.class);

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
	    @ApiParam(value = "Unique token that identifies zone", required = true) @PathVariable String zoneToken,
	    @ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
	    HttpServletRequest servletRequest) throws SiteWhereException {
	IZone existing = assertZone(zoneToken);
	return getDeviceManagement().deleteZone(existing.getId(), force);
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