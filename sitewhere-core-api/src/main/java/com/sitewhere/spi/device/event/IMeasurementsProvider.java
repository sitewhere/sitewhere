/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import java.util.Map;

/**
 * Class that stores measurement metadata.
 * 
 * @author Derek
 */
public interface IMeasurementsProvider {

    /**
     * Add or replace measurement data.
     * 
     * @param name
     * @param value
     */
    public void addOrReplaceMeasurement(String name, Double value);

    /**
     * Remove a measurement.
     * 
     * @param name
     * @return
     */
    public Double removeMeasurement(String name);

    /**
     * Get measurement by name.
     * 
     * @param name
     * @return
     */
    public Double getMeasurement(String name);

    /**
     * Get map of all measurements.
     * 
     * @return
     */
    public Map<String, Double> getMeasurements();

    /**
     * Clear all measurements.
     */
    public void clearMeasurements();
}