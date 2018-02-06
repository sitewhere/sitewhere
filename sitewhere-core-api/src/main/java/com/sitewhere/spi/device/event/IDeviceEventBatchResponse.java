/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import java.util.List;

/**
 * Response generated when a batch of events has been processed.
 * 
 * @author Derek
 */
public interface IDeviceEventBatchResponse {

    /**
     * List of measurements that were created.
     * 
     * @return
     */
    public List<IDeviceMeasurements> getCreatedMeasurements();

    /**
     * List of locations that were created.
     * 
     * @return
     */
    public List<IDeviceLocation> getCreatedLocations();

    /**
     * List of alerts that were created.
     * 
     * @return
     */
    public List<IDeviceAlert> getCreatedAlerts();
}