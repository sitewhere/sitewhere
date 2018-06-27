/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device;

import java.util.Date;
import java.util.UUID;

import com.sitewhere.spi.common.IMetadataProviderEntity;

/**
 * Assigns a device to a customer, area, and/or asset so that events may be
 * associated with those entities.
 * 
 * @author Derek
 */
public interface IDeviceAssignment extends IMetadataProviderEntity {

    /**
     * Get unique id for assigned device.
     * 
     * @return
     */
    public UUID getDeviceId();

    /**
     * Get unique id for assigned device type at time of assignment.
     * 
     * @return
     */
    public UUID getDeviceTypeId();

    /**
     * Get unqiue id for customer assigned to device.
     * 
     * @return
     */
    public UUID getCustomerId();

    /**
     * Get unique id for area assigned to device.
     * 
     * @return
     */
    public UUID getAreaId();

    /**
     * Get asset id.
     * 
     * @return
     */
    public UUID getAssetId();

    /**
     * Get the device assignment status.
     * 
     * @return
     */
    public DeviceAssignmentStatus getStatus();

    /**
     * Get the date/time at which the assignment was made active.
     * 
     * @return
     */
    public Date getActiveDate();

    /**
     * Get the date/time at which the assignment was released.
     * 
     * @return
     */
    public Date getReleasedDate();
}