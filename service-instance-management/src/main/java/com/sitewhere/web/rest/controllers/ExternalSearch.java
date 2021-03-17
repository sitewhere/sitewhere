/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.web.rest.controllers;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;

import io.swagger.annotations.Api;

/**
 * Controller for search operations.
 */
@Path("/api/search")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Api(value = "search")
@Tag(name = "External Search", description = "Allows for searching device event data stored externally.")
@SecurityRequirements({ @SecurityRequirement(name = "jwtAuth", scopes = {}),
	@SecurityRequirement(name = "tenantIdHeader", scopes = {}),
	@SecurityRequirement(name = "tenantAuthHeader", scopes = {}) })
public class ExternalSearch {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(ExternalSearch.class);

    @Inject
    private IInstanceManagementMicroservice microservice;

    /**
     * Get list of all search providers.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Operation(summary = "List available search providers", description = "List available search providers")
    public Response listSearchProviders() throws SiteWhereException {
	// List<ISearchProvider> providers =
	// getSearchProviderManager().getSearchProviders();
	// List<SearchProvider> retval = new ArrayList<SearchProvider>();
	// for (ISearchProvider provider : providers) {
	// retval.add(SearchProvider.copy(provider));
	// }
	// return retval;
	return Response.ok().build();
    }

    /**
     * Perform search and marshal resulting events into {@link IDeviceEvent}
     * response.
     * 
     * @param providerId
     * @return
     * @throws SiteWhereException
     */
    @GET
    @Path("/{providerId}/events")
    @Operation(summary = "Search for events in provider", description = "Search for events in provider")
    public Response searchDeviceEvents(
	    @Parameter(description = "Search provider id", required = true) @PathParam("providerId") String providerId)
	    throws SiteWhereException {
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
	return Response.ok().build();
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
    @POST
    @Path("/{providerId}/raw")
    @Operation(summary = "Execute search and return raw results", description = "Execute search and return raw results")
    public Response rawSearch(
	    @Parameter(description = "Search provider id", required = true) @PathParam("providerId") String providerId,
	    @RequestBody String query) throws SiteWhereException {
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
	return Response.ok().build();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}