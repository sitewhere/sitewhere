/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceLocation;

/**
 * Search provider that provides information about SiteWhere device events.
 */
public interface IDeviceEventSearchProvider extends ISearchProvider {

    /**
     * Executes an arbitrary event query against the search provider.
     * 
     * @param query
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceEvent> executeQuery(String query) throws SiteWhereException;

    /**
     * Execute a query, returning a raw response from the provider.
     * 
     * @param query
     * @return
     * @throws SiteWhereException
     */
    public JsonNode executeQueryWithRawResponse(String query) throws SiteWhereException;

    /**
     * Get a list of device locations near the given lat/long in the given time
     * period.
     * 
     * @param latitude
     * @param longitude
     * @param distance
     * @param criteria
     * @return
     * @throws SiteWhereException
     */
    public List<IDeviceLocation> getLocationsNear(double latitude, double longitude, double distance,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException;
}