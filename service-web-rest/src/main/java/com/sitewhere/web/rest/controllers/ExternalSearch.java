/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.rest.model.search.external.SearchProvider;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.user.SiteWhereRoles;
import com.sitewhere.web.rest.RestControllerBase;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * Controller for search operations.
 * 
 * @author Derek
 */
@RestController
@CrossOrigin(exposedHeaders = { "X-SiteWhere-Error", "X-SiteWhere-Error-Code" })
@RequestMapping(value = "/search")
@Api(value = "search")
public class ExternalSearch extends RestControllerBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    @RequestMapping(method = RequestMethod.GET)
    @ApiOperation(value = "List available search providers")
    @Secured({ SiteWhereRoles.REST })
    public List<SearchProvider> listSearchProviders(HttpServletRequest servletRequest) throws SiteWhereException {
	// List<ISearchProvider> providers =
	// getSearchProviderManager().getSearchProviders();
	// List<SearchProvider> retval = new ArrayList<SearchProvider>();
	// for (ISearchProvider provider : providers) {
	// retval.add(SearchProvider.copy(provider));
	// }
	// return retval;
	return null;
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
    @ApiOperation(value = "Search for events in provider")
    @Secured({ SiteWhereRoles.REST })
    public List<IDeviceEvent> searchDeviceEvents(
	    @ApiParam(value = "Search provider id", required = true) @PathVariable String providerId,
	    HttpServletRequest request, HttpServletRequest servletRequest) throws SiteWhereException {
	// ISearchProvider provider =
	// getSearchProviderManager().getSearchProvider(providerId);
	// if (provider == null) {
	// throw new SiteWhereSystemException(ErrorCode.InvalidSearchProviderId,
	// ErrorLevel.ERROR,
	// HttpServletResponse.SC_NOT_FOUND);
	// }
	// if (!(provider instanceof IDeviceEventSearchProvider)) {
	// throw new SiteWhereException("Search provider does not provide event search
	// capability.");
	// }
	// String query = request.getQueryString();
	// return ((IDeviceEventSearchProvider) provider).executeQuery(query);
	return null;
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
    @ApiOperation(value = "Execute search and return raw results")
    @Secured({ SiteWhereRoles.REST })
    public JsonNode rawSearch(@ApiParam(value = "Search provider id", required = true) @PathVariable String providerId,
	    @RequestBody String query, HttpServletRequest servletRequest) throws SiteWhereException {
	// ISearchProvider provider =
	// getSearchProviderManager().getSearchProvider(providerId);
	// if (provider == null) {
	// throw new SiteWhereSystemException(ErrorCode.InvalidSearchProviderId,
	// ErrorLevel.ERROR,
	// HttpServletResponse.SC_NOT_FOUND);
	// }
	// if (!(provider instanceof IDeviceEventSearchProvider)) {
	// throw new SiteWhereException("Search provider does not provide event search
	// capability.");
	// }
	// return ((IDeviceEventSearchProvider)
	// provider).executeQueryWithRawResponse(query);
	return null;
    }
}