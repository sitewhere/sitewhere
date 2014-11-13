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
import com.sitewhere.rest.model.search.external.SearchProvider;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.search.external.IDeviceEventSearchProvider;
import com.sitewhere.spi.search.external.ISearchProvider;
import com.sitewhere.spi.server.debug.TracerCategory;
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
@Api(value = "search", description = "Operations related to external search providers.")
public class SearchController extends SiteWhereController {

	/** Static logger instance */
	private static Logger LOGGER = Logger.getLogger(SearchController.class);

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Get list of available search providers")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public List<ISearchProvider> listSearchProviders() throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "listSearchProviders", LOGGER);
		try {
			List<ISearchProvider> providers =
					SiteWhere.getServer().getSearchProviderManager().getSearchProviders();
			List<ISearchProvider> retval = new ArrayList<ISearchProvider>();
			for (ISearchProvider provider : providers) {
				retval.add(SearchProvider.copy(provider));
			}
			return retval;
		} finally {
			Tracer.stop(LOGGER);
		}
	}

	@RequestMapping(value = "/{providerId}/events", method = RequestMethod.GET)
	@ResponseBody
	@ApiOperation(value = "Search provider for events that match the given criteria")
	@Secured({ SitewhereRoles.ROLE_AUTHENTICATED_USER })
	public List<IDeviceEvent> searchDeviceEvents(
			@ApiParam(value = "Search provider id", required = true) @PathVariable String providerId,
			HttpServletRequest request) throws SiteWhereException {
		Tracer.start(TracerCategory.RestApiCall, "searchDeviceEvents", LOGGER);
		try {
			ISearchProvider provider =
					SiteWhere.getServer().getSearchProviderManager().getSearchProvider(providerId);
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
}