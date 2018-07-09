/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.device.request;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.spi.device.DeviceAlarmState;
import com.sitewhere.spi.device.IDeviceAlarm;

/**
 * Information needed to create an {@link IDeviceAlarm}.
 * 
 * @author Derek
 */
public interface IDeviceAlarmCreateRequest extends Serializable {

    /**
     * Get device assignment token.
     * 
     * @return
     */
    public String getDeviceAssignmentToken();

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

    /**
     * Get metadata values.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}
