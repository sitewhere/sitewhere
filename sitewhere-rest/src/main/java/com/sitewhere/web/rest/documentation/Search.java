/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.documentation;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.rest.model.search.external.SearchProvider;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;

/**
 * Example of REST requests for interacting with external search providers.
 * 
 * @author Derek
 */
public class Search {

    public static class ListSearchProvidersResponse {

	public Object generate() throws SiteWhereException {
	    List<SearchProvider> list = new ArrayList<SearchProvider>();
	    list.add(ExampleData.SEARCH_SOLR);
	    return new SearchResults<SearchProvider>(list, 1);
	}
    }

    public static class ListExternalEventsResponse {

	public Object generate() throws SiteWhereException {
	    List<IDeviceEvent> events = new ArrayList<IDeviceEvent>();
	    events.add(ExampleData.EVENT_LOCATION1);
	    events.add(ExampleData.EVENT_MEASUREMENT1);
	    events.add(ExampleData.EVENT_ALERT1);
	    return events;
	}
    }
}