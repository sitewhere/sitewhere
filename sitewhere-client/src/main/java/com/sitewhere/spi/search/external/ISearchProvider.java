/*
 * ISearchProvider.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.search.external;

import java.util.List;

import com.sitewhere.spi.ISiteWhereLifecycle;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;

/**
 * Implemented by external search providers that index SiteWhere data.
 * 
 * @author Derek
 */
public interface ISearchProvider extends ISiteWhereLifecycle {

	/**
	 * Get a human-readable name for the search provider.
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * Get a list of device locations near the given lat/long in the given time period.
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