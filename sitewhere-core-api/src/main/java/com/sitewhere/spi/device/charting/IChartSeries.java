/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.charting;

import java.io.Serializable;
import java.util.List;

/**
 * Specifies a series of chart entries that may be rendered.
 * 
 * @author Derek
 * 
 * @param <T>
 */
public interface IChartSeries<T> extends Serializable {

    /**
     * Get id a measurement represented by the series.
     * 
     * @return
     */
    public String getMeasurementId();

    /**
     * Get data entries for the series.
     * 
     * @return
     */
    public List<IChartEntry<T>> getEntries();
}