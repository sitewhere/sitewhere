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

import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.common.IMetadataProviderEntity;

/**
 * Assigns a device to a physical entity being monitored. A device may be used
 * for multiple assets over a period of time.
 * 
 * @author Derek
 */
public interface IDeviceAssignment extends IMetadataProviderEntity {

    /**
     * Get unique device assignment id.
     * 
     * @return
     */
    public UUID getId();

    /**
     * Get token that acts as an alias for assignment id.
     * 
     * @return
     */
    public String getToken();

    /**
     * Get unique id for assigned device.
     * 
     * @return
     */
    public UUID getDeviceId();

    /**
     * Get unique id for area assigned to device.
     * 
     * @return
     */
    public UUID getAreaId();

    /**
     * Get the referenced asset type.
     * 
     * @return
     */
    public DeviceAssignmentType getAssignmentType();

    /**
     * Get asset reference.
     * 
     * @return
     */
    public IAssetReference getAssetReference();

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