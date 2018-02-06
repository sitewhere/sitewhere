/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.charting;

import java.io.Serializable;
import java.util.Date;

/**
 * Specifies a single measurement entry for a chart.
 * 
 * @author Derek
 * 
 * @param <T>
 */
public interface IChartEntry<T> extends Comparable<IChartEntry<T>>, Serializable {

    /**
     * Get value for the entry.
     * 
     * @return
     */
    public T getValue();

    /**
     * Get date the value was measured.
     * 
     * @return
     */
    public Date getMeasurementDate();
}