/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.charting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sitewhere.rest.model.device.charting.ChartEntry;
import com.sitewhere.rest.model.device.charting.ChartSeries;
import com.sitewhere.spi.device.charting.IChartSeries;
import com.sitewhere.spi.device.event.IDeviceMeasurement;

/**
 * Builds chart series from measurements.
 */
public class ChartBuilder {

    /** Map of measurement names to series */
    private Map<String, ChartSeries<Double>> seriesByMeasurementName;

    /**
     * Process measurements into a list of charts series.
     * 
     * @param matches
     * @return
     */
    public List<IChartSeries<Double>> process(List<IDeviceMeasurement> matches, String[] measurementIds) {
	seriesByMeasurementName = new HashMap<String, ChartSeries<Double>>();
	List<String> mxids = null;
	if ((measurementIds != null) && (measurementIds.length > 0)) {
	    mxids = Arrays.asList(measurementIds);
	}

	// Add all measurements.
	for (IDeviceMeasurement mx : matches) {
	    addSeriesEntry(mx.getName(), mx.getValue(), mx.getEventDate());
	}
	// Sort entries by date.
	List<IChartSeries<Double>> results = new ArrayList<IChartSeries<Double>>();
	for (IChartSeries<Double> series : seriesByMeasurementName.values()) {
	    if ((mxids == null) || (mxids.contains(series.getMeasurementId()))) {
		Collections.sort(series.getEntries());
		results.add(series);
	    }
	}
	return results;
    }

    /**
     * Add a new measurement entry. Create a new series if one does not already
     * exist.
     * 
     * @param key
     * @param value
     * @param date
     */
    protected void addSeriesEntry(String key, Double value, Date date) {
	ChartSeries<Double> series = seriesByMeasurementName.get(key);
	if (series == null) {
	    ChartSeries<Double> newSeries = new ChartSeries<Double>();
	    newSeries.setMeasurementId(key);
	    seriesByMeasurementName.put(key, newSeries);
	    series = newSeries;
	}
	ChartEntry<Double> seriesEntry = new ChartEntry<Double>();
	seriesEntry.setValue(value);
	seriesEntry.setMeasurementDate(date);
	series.getEntries().add(seriesEntry);
    }
}