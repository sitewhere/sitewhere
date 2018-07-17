/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.search.configuration;

import com.sitewhere.spi.microservice.configuration.model.IRoleKey;

public enum EventSearchRoleKeys implements IRoleKey {

    /** Presence management */
    EventSearch("event_search"),

    /** Search providers */
    SearchProviders("search_prvs"),

    /** Search provider */
    SearchProvider("search_prv"),

    /** Solr search provider */
    SolrSearchProvider("solr_search_prv");

    private String id;

    private EventSearchRoleKeys(String id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.microservice.configuration.model.IRoleKey#getId()
     */
    @Override
    public String getId() {
	return id;
    }
}