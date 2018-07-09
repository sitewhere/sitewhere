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

import com.sitewhere.spi.common.IMetadataProvider;

/**
 * Indicates an alarm condition that potentially requires human intervention.
 * 
 * @author Derek
 */
public interface IDeviceAlarm extends IMetadataProvider {

    /**
     * Get unique identifier.
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
     * Get alarm message.
     * 
     * @return
     */
    public String getAlarmMessage();

    /**
     * Get event id that triggered alarm.
     * 
     * @return
     */
    public UUID getTriggeringEventId();

    /**
     * Get alarm state.
     * 
     * @return
     */
    public DeviceAlarmState getState();

    /**
     * Get date the alarm was triggered.
     * 
     * @return
     */
    public Date getTriggeredDate();

    /**
     * Get date the alarm was acknowledged.
     * 
     * @return
     */
    public Date getAcknowledgedDate();

    /**
     * Get date the alarm was resolved.
     * 
     * @return
     */
    public Date getResolvedDate();
}