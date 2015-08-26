/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.core.user.SitewhereRoles;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.request.ZoneCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for site operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/zones")
@Api(value = "zones", description = "Operations related to SiteWhere zones")
public class ZonesController extends SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ZonesController.class);

	@RequestMapping(value = "/{zoneToken}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get zone by unique token")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public Zone getZone(
			@ApiParam(value = "Unique token that identifies zone", required = true) @PathVariable String zoneToken,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getZone", LOGGER);
		try {
			IZone found =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).getZone(zoneToken);
			return Zone.copy(found);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Update information for a zone.
	 * 
	 * @param input
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{zoneToken}", method = RequestMethod.PUT)
	@ResponseBody
	@ApiOperation(value = "Update an existing zone")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_SITES })
	public Zone updateZone(
			@ApiParam(value = "Unique token that identifies zone", required = true) @PathVariable String zoneToken,
			@RequestBody ZoneCreateRequest request, HttpServletRequest servletRequest)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "updateZone", LOGGER);
		try {
			IZone zone =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).updateZone(
							zoneToken, request);
			return Zone.copy(zone);
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	/**
	 * Delete an existing zone.
	 * 
	 * @param zoneToken
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/{zoneToken}", method = RequestMethod.DELETE)
	@ResponseBody
	@ApiOperation(value = "Delete zone based on unique token")
	@Secured({ SitewhereRoles.ROLE_ADMINISTER_SITES })
	public Zone deleteZone(
			@ApiParam(value = "Unique token that identifies zone", required = true) @PathVariable String zoneToken,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "deleteZone", LOGGER);
		try {
			IZone deleted =
					SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).deleteZone(
							zoneToken, force);
			return Zone.copy(deleted);
		} finally {
			Tracer.stop(LOGGER);
		}
	}
}