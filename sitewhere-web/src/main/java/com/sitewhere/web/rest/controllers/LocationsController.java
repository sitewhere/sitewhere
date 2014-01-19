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

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.core.device.InterpolatedHistoryBuilder;
import com.sitewhere.rest.model.device.support.InterpolatedAssignmentHistory;
import com.sitewhere.rest.model.search.DateRangeSearchCriteria;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.search.ISearchResults;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for location operations.
 * 
 * @author Derek Adams
 */
// Location queries are being redesigned. These REST services are no longer available.
// @Controller
// @RequestMapping(value = "/locations")
// @Api(value = "", description = "Operations related to SiteWhere assignment locations.")
public class LocationsController extends SiteWhereController {

	/**
	 * Gets an interpolated history of locations derived from real locations within the
	 * search time period.
	 * 
	 * @param input
	 * @return
	 * @throws SiteWhereException
	 */
	@RequestMapping(value = "/history/interpolated", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get the interpolated assignment location history based on criteria")
	public List<InterpolatedAssignmentHistory> getInterpolatedHistory(
			@ApiParam(value = "Assignment Tokens") @RequestParam List<String> tokens,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize)
			throws SiteWhereException {
		DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
		ISearchResults<IDeviceLocation> matches =
				SiteWhereServer.getInstance().getDeviceManagement().listDeviceLocations(tokens, criteria);
		InterpolatedHistoryBuilder builder = new InterpolatedHistoryBuilder();
		return builder.build(matches.getResults());
	}

	@RequestMapping(value = "/history", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get the location history for assignments based on criteria")
	public ISearchResults<IDeviceLocation> getDeviceAssignmentsLocationHistory(
			@ApiParam(value = "Assignment Tokens") @RequestParam List<String> tokens,
			@ApiParam(value = "Start date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date startDate,
			@ApiParam(value = "End date", required = false) @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date endDate,
			@ApiParam(value = "Page number (First page is 1)", required = false) @RequestParam(defaultValue = "1") int page,
			@ApiParam(value = "Page size", required = false) @RequestParam(defaultValue = "100") int pageSize)
			throws SiteWhereException {
		DateRangeSearchCriteria criteria = new DateRangeSearchCriteria(page, pageSize, startDate, endDate);
		return SiteWhereServer.getInstance().getDeviceManagement().listDeviceLocations(tokens, criteria);
	}
}