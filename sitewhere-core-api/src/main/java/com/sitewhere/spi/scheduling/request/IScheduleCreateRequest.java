/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.scheduling.request;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import com.sitewhere.spi.scheduling.TriggerType;

/**
 * Supplies information needed to create or update a schedule.
 * 
 * @author Derek
 */
public interface IScheduleCreateRequest extends Serializable {

    /**
     * Unique token for schedule.
     * 
     * @return
     */
    public String getToken();

    /**
     * Schedule name.
     * 
     * @return
     */
    public String getName();

    /**
     * Get type of trigger for schedule.
     * 
     * @return
     */
    public TriggerType getTriggerType();

    /**
     * Get trigger configuration values.
     * 
     * @return
     */
    public Map<String, String> getTriggerConfiguration();

    /**
     * Get date schedule takes effect.
     * 
     * @return
     */
    public Date getStartDate();

    /**
     * Get date schedule is no longer in effect.
     * 
     * @return
     */
    public Date getEndDate();

    /**
     * Get metadata values.
     * 
     * @return
     */
    public Map<String, String> getMetadata();
}