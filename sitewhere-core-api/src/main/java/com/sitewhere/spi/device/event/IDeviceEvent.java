/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.event;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import com.sitewhere.spi.common.IMetadataProvider;

/**
 * Event that originates from a device.
 * 
 * @author Derek
 */
public interface IDeviceEvent extends IMetadataProvider, Serializable {

    /**
     * Get unique event identifier.
     * 
     * @return
     */
    public UUID getId();

    /**
     * Get alternate id that can be used for correlating events with external
     * systems and for deduplication.
     * 
     * @return
     */
    public String getAlternateId();

    /**
     * Get event type indicator.
     * 
     * @return
     */
    public DeviceEventType getEventType();

    /**
     * Get device id.
     * 
     * @return
     */
    public UUID getDeviceId();

    /**
     * Get assignment id.
     * 
     * @return
     */
    public UUID getDeviceAssignmentId();

    /**
     * Get customer id if assigned.
     * 
     * @return
     */
    public UUID getCustomerId();

    /**
     * Get area id if assigned.
     * 
     * @return
     */
    public UUID getAreaId();

    /**
     * Get asset id if assigned.
     * 
     * @return
     */
    public UUID getAssetId();

    /**
     * Get the date the event occurred.
     * 
     * @return
     */
    public Date getEventDate();

    /**
     * Get the date this event was received.
     * 
     * @return
     */
    public Date getReceivedDate();
}