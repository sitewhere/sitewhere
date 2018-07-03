/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.state;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * Contains most recent state information for a device.
 * 
 * @author Derek
 */
public interface IDeviceState extends Serializable {

    /**
     * Get id.
     * 
     * @return
     */
    public UUID getId();

    /**
     * Get device id.
     * 
     * @return
     */
    public UUID getDeviceId();

    /**
     * Get device type id.
     * 
     * @return
     */
    public UUID getDeviceTypeId();

    /**
     * Get device assignment id.
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
     * Get date of last device interaction.
     * 
     * @return
     */
    public Date getLastInteractionDate();

    /**
     * Get date device was marked as missing.
     * 
     * @return
     */
    public Date getPresenceMissingDate();

    /**
     * Get event id for last location reported.
     * 
     * @return
     */
    public UUID getLastLocationEventId();

    /**
     * Get last measurement event ids indexed by measurement id.
     * 
     * @return
     */
    public Map<String, UUID> getLastMeasurementEventIds();

    /**
     * Get last alert event ids indexed by alert type.
     * 
     * @return
     */
    public Map<String, UUID> getLastAlertEventIds();
}
