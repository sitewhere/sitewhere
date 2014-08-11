/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.rest.model.device.Zone;
import com.sitewhere.rest.model.device.request.ZoneCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for site operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/zones")
public class ZonesController extends SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(ZonesController.class);

	@RequestMapping(value = "/{zoneToken}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get zone by unique token")
	public Zone getZone(
			@ApiParam(value = "Unique token that identifies zone", required = true) @PathVariable String zoneToken)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getZone", LOGGER);
		try {
			IZone found = SiteWhere.getServer().getDeviceManagement().getZone(zoneToken);
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
	public Zone updateZone(
			@ApiParam(value = "Unique token that identifies zone", required = true) @PathVariable String zoneToken,
			@RequestBody ZoneCreateRequest request) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "updateZone", LOGGER);
		try {
			IZone zone = SiteWhere.getServer().getDeviceManagement().updateZone(zoneToken, request);
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
	public Zone deleteZone(
			@ApiParam(value = "Unique token that identifies zone", required = true) @PathVariable String zoneToken,
			@ApiParam(value = "Delete permanently", required = false) @RequestParam(defaultValue = "false") boolean force)
			throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "deleteZone", LOGGER);
		try {
			IZone deleted = SiteWhere.getServer().getDeviceManagement().deleteZone(zoneToken, force);
			return Zone.copy(deleted);
		} finally {
			Tracer.stop(LOGGER);
		}
	}
}