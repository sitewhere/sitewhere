/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.spi;

import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Manages a list of schedules that are applied to system actions.
 * 
 * @author Derek
 */
public interface IScheduleManager extends ITenantEngineLifecycleComponent {

    /**
     * Called when a new schedule has been added.
     * 
     * @param schedule
     * @throws SiteWhereException
     */
    public void scheduleAdded(ISchedule schedule) throws SiteWhereException;

    /**
     * Called when a schedule is removed.
     * 
     * @param schedule
     * @throws SiteWhereException
     */
    public void scheduleRemoved(ISchedule schedule) throws SiteWhereException;

    /**
     * Adds a job to the scheduler.
     * 
     * @param job
     * @throws SiteWhereException
     */
    public void scheduleJob(IScheduledJob job) throws SiteWhereException;

    /**
     * Unschedules the given job if scheduled.
     * 
     * @param job
     * @throws SiteWhereException
     */
    public void unscheduleJob(IScheduledJob job) throws SiteWhereException;
}