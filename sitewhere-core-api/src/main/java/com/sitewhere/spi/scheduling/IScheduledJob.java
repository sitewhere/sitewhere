/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.scheduling;

import java.util.Map;

import com.sitewhere.spi.common.IPersistentEntity;

/**
 * Unit of work that should be executed on a schedule.
 * 
 * @author Derek
 */
public interface IScheduledJob extends IPersistentEntity {

    /**
     * Get unique schedule token.
     * 
     * @return
     */
    public String getScheduleToken();

    /**
     * Get job type.
     * 
     * @return
     */
    public ScheduledJobType getJobType();

    /**
     * Get job configuration values.
     * 
     * @return
     */
    public Map<String, String> getJobConfiguration();

    /**
     * Get job scheduling state.
     * 
     * @return
     */
    public ScheduledJobState getJobState();
}