/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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