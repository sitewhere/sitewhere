/*
 * SearchController.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sitewhere.rest.model.search.external.SearchProvider;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.external.IDeviceEventSearchProvider;
import com.sitewhere.spi.search.external.ISearchProvider;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for search operations.
 * 
 * @author Derek
 */
@Controller
@RequestMapping(value = "/search")
@Api(value = "", description = "Operations related to external search providers.")
public class SearchController extends SiteWhereController {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get list of available search providers")
	public List<ISearchProvider> listSearchProviders() throws SiteWhereException {
		List<ISearchProvider> providers =
				SiteWhereServer.getInstance().getSearchProviderManager().getSearchProviders();
		List<ISearchProvider> converted = new ArrayList<ISearchProvider>();
		for (ISearchProvider provider : providers) {
			converted.add(SearchProvider.copy(provider));
		}
		return converted;
	}

	@RequestMapping(value = "/{providerId}/events", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Search provider for events that match the given criteria")
	public List<IDeviceEvent> searchDeviceEvents(
			@ApiParam(value = "Search provider id", required = true) @PathVariable String providerId,
			HttpServletRequest request) throws SiteWhereException {
		ISearchProvider provider =
				SiteWhereServer.getInstance().getSearchProviderManager().getSearchProvider(providerId);
		if (provider == null) {
			throw new SiteWhereSystemException(ErrorCode.InvalidSearchProviderId, ErrorLevel.ERROR,
					HttpServletResponse.SC_NOT_FOUND);
		}
		if (!(provider instanceof IDeviceEventSearchProvider)) {
			throw new SiteWhereException("Search provider does not provide event search capability.");
		}
		String query = request.getQueryString();
		return ((IDeviceEventSearchProvider) provider).executeQuery(query);
	}
}