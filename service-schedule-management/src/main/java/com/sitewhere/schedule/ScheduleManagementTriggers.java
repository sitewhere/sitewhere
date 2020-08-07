/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule;

import java.util.UUID;

import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.schedule.spi.IScheduleManager;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;

/**
 * Trigger actions based on schedule management API calls.
 */
public class ScheduleManagementTriggers extends ScheduleManagementDecorator {

    /** Schedule manager */
    private IScheduleManager scheduleManager;

    public ScheduleManagementTriggers(IScheduleManagement delegate, IScheduleManager scheduleManager) {
	super(delegate);
	this.scheduleManager = scheduleManager;
    }

    /*
     * @see com.sitewhere.schedule.ScheduleManagementDecorator#createSchedule(com.
     * sitewhere.spi.scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public ISchedule createSchedule(IScheduleCreateRequest request) throws SiteWhereException {
	ISchedule schedule = super.createSchedule(request);
	getScheduleManager().scheduleAdded(schedule);
	return schedule;
    }

    /*
     * @see
     * com.sitewhere.schedule.ScheduleManagementDecorator#deleteSchedule(java.util.
     * UUID)
     */
    @Override
    public ISchedule deleteSchedule(UUID scheduleId) throws SiteWhereException {
	ISchedule schedule = super.deleteSchedule(scheduleId);
	getScheduleManager().scheduleRemoved(schedule);
	return schedule;
    }

    /*
     * @see
     * com.sitewhere.schedule.ScheduleManagementDecorator#createScheduledJob(com.
     * sitewhere.spi.scheduling.request.IScheduledJobCreateRequest)
     */
    @Override
    public IScheduledJob createScheduledJob(IScheduledJobCreateRequest request) throws SiteWhereException {
	IScheduledJob job = super.createScheduledJob(request);
	getScheduleManager().scheduleJob(job);
	return job;
    }

    /*
     * @see
     * com.sitewhere.schedule.ScheduleManagementDecorator#deleteScheduledJob(java.
     * util.UUID)
     */
    @Override
    public IScheduledJob deleteScheduledJob(UUID scheduledJobId) throws SiteWhereException {
	IScheduledJob job = super.deleteScheduledJob(scheduledJobId);
	getScheduleManager().unscheduleJob(job);
	return job;
    }

    public IScheduleManager getScheduleManager() {
	return scheduleManager;
    }

    public void setScheduleManager(IScheduleManager scheduleManager) {
	this.scheduleManager = scheduleManager;
    }
}