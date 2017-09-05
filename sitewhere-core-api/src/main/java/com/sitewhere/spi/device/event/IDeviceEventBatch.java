/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import java.util.List;

import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Used to create multiple events for a given device.
 * 
 * @author Derek
 */
public interface IDeviceEventBatch {

    /**
     * Unique device hardware id.
     * 
     * @return
     */
    public String getHardwareId();

    /**
     * Get a list of device measurements create requests.
     * 
     * @return
     */
    public List<IDeviceMeasurementsCreateRequest> getMeasurements();

    /**
     * Get a list of device location create requests.
     * 
     * @return
     */
    public List<IDeviceLocationCreateRequest> getLocations();

    /**
     * Get a list of device alert create requests.
     * 
     * @return
     */
    public List<IDeviceAlertCreateRequest> getAlerts();
}