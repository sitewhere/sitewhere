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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.SiteWhere;
import com.sitewhere.Tracer;
import com.sitewhere.rest.model.search.external.SearchProvider;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.external.IDeviceEventSearchProvider;
import com.sitewhere.spi.search.external.ISearchProvider;
import com.sitewhere.spi.server.debug.TracerCategory;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestController;
import com.sitewhere.web.rest.annotations.Documented;
import com.sitewhere.web.rest.annotations.DocumentedController;
import com.sitewhere.web.rest.annotations.Example;
import com.sitewhere.web.rest.annotations.Example.Stage;
import com.sitewhere.web.rest.documentation.Search;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Controller for search operations.
 * 
 * @author Derek
 */
@Controller
@CrossOrigin
@RequestMapping(value = "/search")
@Api(value = "search", description = "Operations related to external search providers.")
@DocumentedController(name = "External Search")
public class SearchController extends RestController {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "List available search providers")
    @Secured({ SiteWhereRoles.REST })
    @Documented(examples = {
	    @Example(stage = Stage.Response, json = Search.ListSearchProvidersResponse.class, description = "listSearchProvidersResponse.md") })
    public List<SearchProvider> listSearchProviders(HttpServletRequest servletRequest) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "listSearchProviders", LOGGER);
	try {
	    List<ISearchProvider> providers = SiteWhere.getServer().getSearchProviderManager(getTenant(servletRequest))
		    .getSearchProviders();
	    List<SearchProvider> retval = new ArrayList<SearchProvider>();
	    for (ISearchProvider provider : providers) {
		retval.add(SearchProvider.copy(provider));
	    }
	    return retval;
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Perform search and marshal resulting events into {@link IDeviceEvent}
     * response.
     * 
     * @param providerId
     * @param request
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{providerId}/events", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "Search for events in provider")
    @Secured({ SiteWhereRoles.REST })
    public List<IDeviceEvent> searchDeviceEvents(
	    @ApiParam(value = "Search provider id", required = true) @PathVariable String providerId,
	    HttpServletRequest request, HttpServletRequest servletRequest) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "searchDeviceEvents", LOGGER);
	try {
	    ISearchProvider provider = SiteWhere.getServer().getSearchProviderManager(getTenant(servletRequest))
		    .getSearchProvider(providerId);
	    if (provider == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidSearchProviderId, ErrorLevel.ERROR,
			HttpServletResponse.SC_NOT_FOUND);
	    }
	    if (!(provider instanceof IDeviceEventSearchProvider)) {
		throw new SiteWhereException("Search provider does not provide event search capability.");
	    }
	    String query = request.getQueryString();
	    return ((IDeviceEventSearchProvider) provider).executeQuery(query);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }

    /**
     * Perform serach and return raw JSON response.
     * 
     * @param providerId
     * @param request
     * @param servletRequest
     * @return
     * @throws SiteWhereException
     */
    @RequestMapping(value = "/{providerId}/raw", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(value = "Execute search and return raw results")
    @Secured({ SiteWhereRoles.REST })
    public JsonNode rawSearch(@ApiParam(value = "Search provider id", required = true) @PathVariable String providerId,
	    @RequestBody String query, HttpServletRequest servletRequest) throws SiteWhereException {
	Tracer.start(TracerCategory.RestApiCall, "searchDeviceEvents", LOGGER);
	try {
	    ISearchProvider provider = SiteWhere.getServer().getSearchProviderManager(getTenant(servletRequest))
		    .getSearchProvider(providerId);
	    if (provider == null) {
		throw new SiteWhereSystemException(ErrorCode.InvalidSearchProviderId, ErrorLevel.ERROR,
			HttpServletResponse.SC_NOT_FOUND);
	    }
	    if (!(provider instanceof IDeviceEventSearchProvider)) {
		throw new SiteWhereException("Search provider does not provide event search capability.");
	    }
	    return ((IDeviceEventSearchProvider) provider).executeQueryWithRawResponse(query);
	} finally {
	    Tracer.stop(LOGGER);
	}
    }
}