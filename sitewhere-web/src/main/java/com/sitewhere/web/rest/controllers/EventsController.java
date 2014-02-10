/*
 * EventsController.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
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
@Api(value = "", description = "Operations related to SiteWhere device events.")
public class EventsController extends SiteWhereController {

	/**
	 * Used by AJAX calls to find an event by unique id.
	 * 
	 * @param eventId
	 * @return
	 */
	@RequestMapping(value = "/{eventId}", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get an event by unique id.")
	public IDeviceEvent getEventById(
			@ApiParam(value = "Event id", required = true) @PathVariable String eventId)
			throws SiteWhereException {
		return SiteWhereServer.getInstance().getDeviceManagement().getDeviceEventById(eventId);
	}
}