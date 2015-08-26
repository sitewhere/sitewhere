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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.core.user.SitewhereRoles;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for event operations.
 * 
 * @author Derek Adams
 */
@Controller
@RequestMapping(value = "/events")
@Api(value = "events", description = "Operations related to SiteWhere device events.")
public class EventsController extends SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(EventsController.class);

	/**
	 * Used by AJAX calls to find an event by unique id.
	 * 
	 * @param eventId
	 * @return
	 */
	@RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get an event by unique id.")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public IDeviceEvent getEventById(
			@ApiParam(value = "Event id", required = true) @PathVariable String eventId,
			HttpServletRequest servletRequest) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "getEventById", LOGGER);
		try {
			return SiteWhere.getServer().getDeviceManagement(getTenant(servletRequest)).getDeviceEventById(
					eventId);
		} finally {
			Tracer.stop(LOGGER);
		}
	}
}