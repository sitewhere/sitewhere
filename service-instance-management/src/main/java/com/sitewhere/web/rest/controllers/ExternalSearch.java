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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Controller for search operations.
 */
@RestController
@RequestMapping("/api/search")
public class ExternalSearch {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(ExternalSearch.class);

    @Autowired
    private IInstanceManagementMicroservice microservice;

    /**
     * Get list of all search providers.
     * 
     * @return
     * @throws SiteWhereException
     */
    @GetMapping
    public ResponseEntity<?> listSearchProviders() throws SiteWhereException {
	// List<ISearchProvider> providers =
	// getSearchProviderManager().getSearchProviders();
	// List<SearchProvider> retval = new ArrayList<SearchProvider>();
	// for (ISearchProvider provider : providers) {
	// retval.add(SearchProvider.copy(provider));
	// }
	// return retval;
	return ResponseEntity.ok().build();
    }

    /**
     * Perform search and marshal resulting events into {@link IDeviceEvent}
     * response.
     * 
     * @param providerId
     * @return
     * @throws SiteWhereException
     */
    @GetMapping("/{providerId}/events")
    public ResponseEntity<?> searchDeviceEvents(@PathVariable String providerId) throws SiteWhereException {
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
	return ResponseEntity.ok().build();
    }

    /**
     * Perform serach and return raw JSON response.
     * 
     * @param providerId
     * @param query
     * @return
     * @throws SiteWhereException
     */
    @PostMapping("/{providerId}/raw")
    public ResponseEntity<?> rawSearch(@PathVariable String providerId, @RequestBody String query)
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
	// return ((IDeviceEventSearchProvider)
	// provider).executeQueryWithRawResponse(query);
	return ResponseEntity.ok().build();
    }

    protected IInstanceManagementMicroservice getMicroservice() {
	return microservice;
    }
}