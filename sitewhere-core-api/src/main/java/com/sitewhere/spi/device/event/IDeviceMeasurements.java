/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

/**
 * Measurements associated with a device assignment at a point in time.
 * 
 * @author Derek
 */
public interface IDeviceMeasurements extends IDeviceEvent, IMeasurementsProvider {

    /**
     * Get a simple string that lists the measurements.
     * 
     * @return
     */
    public String getMeasurementsSummary();
}