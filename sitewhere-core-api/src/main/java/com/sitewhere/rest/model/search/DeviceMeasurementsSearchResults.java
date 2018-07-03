/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.search;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Search results that contain device measurements.
 * 
 * @author dadams
 */
public class DeviceMeasurementsSearchResults extends SearchResults<IDeviceMeasurement> {

    public DeviceMeasurementsSearchResults() {
	super(new ArrayList<IDeviceMeasurement>());
    }

    public DeviceMeasurementsSearchResults(List<IDeviceMeasurement> results) {
	super(results);
    }

    /**
     * Copy the API version of results so they can be marshaled.
     * 
     * @param source
     * @return
     */
    public static DeviceMeasurementsSearchResults copy(SearchResults<IDeviceMeasurement> source)
	    throws SiteWhereException {
	DeviceMeasurementsSearchResults result = new DeviceMeasurementsSearchResults();
	result.setNumResults(source.getNumResults());
	result.setResults(source.getResults());
	return result;
    }
}