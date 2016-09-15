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

import com.sitewhere.rest.model.device.event.DeviceMeasurements;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceMeasurements;

/**
 * Search results that contain device measurements.
 * 
 * @author dadams
 */
public class DeviceMeasurementsSearchResults extends SearchResults<DeviceMeasurements> {

    public DeviceMeasurementsSearchResults() {
	super(new ArrayList<DeviceMeasurements>());
    }

    public DeviceMeasurementsSearchResults(List<DeviceMeasurements> results) {
	super(results);
    }

    /**
     * Copy the API version of results so they can be marshaled.
     * 
     * @param source
     * @return
     */
    public static DeviceMeasurementsSearchResults copy(SearchResults<IDeviceMeasurements> source)
	    throws SiteWhereException {
	DeviceMeasurementsSearchResults result = new DeviceMeasurementsSearchResults();
	List<DeviceMeasurements> converted = new ArrayList<DeviceMeasurements>();
	for (IDeviceMeasurements measurement : source.getResults()) {
	    converted.add(DeviceMeasurements.copy(measurement));
	}
	result.setNumResults(source.getNumResults());
	result.setResults(converted);
	return result;
    }
}