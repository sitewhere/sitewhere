/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi;

import java.util.List;

import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;

/**
 * Holds SiteWhere information associated with a reqeust.
 */
public interface ISiteWhereContext {

    /**
     * Get current assignment for device associated with the request.
     * 
     * @return
     */
    public IDeviceAssignment getDeviceAssignment();

    /**
     * Get a list of device measurements that have not been persisted.
     * 
     * @return
     */
    public List<IDeviceMeasurementCreateRequest> getUnsavedDeviceMeasurements();

    /**
     * Get a list of device locations that have not been persisted.
     * 
     * @return
     */
    public List<IDeviceLocationCreateRequest> getUnsavedDeviceLocations();

    /**
     * Get a list of device alerts that have not been persisted.
     * 
     * @return
     */
    public List<IDeviceAlertCreateRequest> getUnsavedDeviceAlerts();

    /**
     * Get the {@link IDeviceMeasurement} events.
     * 
     * @return
     */
    public List<IDeviceMeasurement> getDeviceMeasurements();

    /**
     * Get the {@link IDeviceLocation} events.
     * 
     * @return
     */
    public List<IDeviceLocation> getDeviceLocations();

    /**
     * Get the {@link IDeviceAlert} events.
     * 
     * @return
     */
    public List<IDeviceAlert> getDeviceAlerts();

    /**
     * Get the {@link IDeviceCommandInvocation} events.
     * 
     * @return
     */
    public List<IDeviceCommandInvocation> getDeviceCommandInvocations();

    /**
     * Get the {@link IDeviceCommandResponse} events.
     * 
     * @return
     */
    public List<IDeviceCommandResponse> getDeviceCommandResponses();

    /**
     * Get information for replying to originator.
     * 
     * @return
     */
    public String getReplyTo();
}