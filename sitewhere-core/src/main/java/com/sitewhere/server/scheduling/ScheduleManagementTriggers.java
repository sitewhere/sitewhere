/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.scheduling;

import com.sitewhere.SiteWhere;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduleManager;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;

/**
 * Trigger actions based on schedule management API calls.
 * 
 * @author Derek
 */
public class ScheduleManagementTriggers extends ScheduleManagementDecorator {

    /** Schedule manager */
    private IScheduleManager scheduleManager;

    public ScheduleManagementTriggers(IScheduleManagement delegate, IScheduleManager scheduleManager) {
	super(delegate);
	this.scheduleManager = scheduleManager;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.scheduling.ScheduleManagementDecorator#
     * createSchedule(com.
     * sitewhere.spi.scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public ISchedule createSchedule(IScheduleCreateRequest request) throws SiteWhereException {
	ISchedule schedule = super.createSchedule(request);
	getScheduleManager().scheduleAdded(schedule);
	return schedule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.scheduling.ScheduleManagementDecorator#
     * deleteSchedule(java .lang.String, boolean)
     */
    @Override
    public ISchedule deleteSchedule(String token, boolean force) throws SiteWhereException {
	ISchedule schedule = super.deleteSchedule(token, force);
	getScheduleManager().scheduleRemoved(schedule);
	return schedule;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.scheduling.ScheduleManagementDecorator#
     * createScheduledJob(
     * com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest)
     */
    @Override
    public IScheduledJob createScheduledJob(IScheduledJobCreateRequest request) throws SiteWhereException {
	IScheduledJob job = super.createScheduledJob(request);
	getScheduleManager().scheduleJob(job);
	return job;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.server.scheduling.ScheduleManagementDecorator#
     * deleteScheduledJob( java.lang.String, boolean)
     */
    @Override
    public IScheduledJob deleteScheduledJob(String token, boolean force) throws SiteWhereException {
	IScheduledJob job = super.deleteScheduledJob(token, force);
	SiteWhere.getServer().getScheduleManager(getTenant()).unscheduleJob(job);
	return job;
    }

    public IScheduleManager getScheduleManager() {
	return scheduleManager;
    }

    public void setScheduleManager(IScheduleManager scheduleManager) {
	this.scheduleManager = scheduleManager;
    }
}