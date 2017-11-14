/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import java.util.Map;

import com.sitewhere.spi.device.DeviceAssignmentStatus;

/**
 * Provides contextual information about a device event. This is a combination
 * of device and assignment data that might be used in outbound processors or
 * for rule calculations.
 * 
 * @author Derek
 */
public interface IDeviceEventContext {

    /**
     * Get the unique hardware id of the device.
     * 
     * @return
     */
    public String getHardwareId();

    /**
     * Get token for device specification.
     * 
     * @return
     */
    public String getSpecificationToken();

    /**
     * If contained by a parent device, returns the parent hardware id.
     * 
     * @return
     */
    public String getParentHardwareId();

    /**
     * Get most recent device status.
     * 
     * @return
     */
    public String getDeviceStatus();

    /**
     * Get a map of device metadata.
     * 
     * @return
     */
    public Map<String, String> getDeviceMetadata();

    /**
     * Get the device assignment status.
     * 
     * @return
     */
    public DeviceAssignmentStatus getAssignmentStatus();

    /**
     * Get a map of device assignment metadata.
     * 
     * @return
     */
    public Map<String, String> getAssignmentMetadata();
}