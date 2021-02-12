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
package com.sitewhere.schedule.persistence;

import com.sitewhere.microservice.persistence.Persistence;
import com.sitewhere.rest.model.scheduling.Schedule;
import com.sitewhere.rest.model.scheduling.ScheduledJob;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.ScheduledJobState;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;

/**
 * Persistence logic for schedule components.
 */
public class ScheduleManagementPersistence extends Persistence {

    /**
     * Handle common logic for creating a schedule.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static Schedule scheduleCreateLogic(IScheduleCreateRequest request) throws SiteWhereException {
	Schedule schedule = new Schedule();
	Persistence.entityCreateLogic(request, schedule);

	// Name is required.
	require("Name", request.getName());
	schedule.setName(request.getName());

	// Trigger type is required.
	requireNotNull("Trigger Type", request.getTriggerType());
	schedule.setTriggerType(request.getTriggerType());
	schedule.setTriggerConfiguration(request.getTriggerConfiguration());

	schedule.setStartDate(request.getStartDate());
	schedule.setEndDate(request.getEndDate());

	return schedule;
    }

    /**
     * Handle common logic for updating a schedule.
     * 
     * @param schedule
     * @param request
     * @throws SiteWhereException
     */
    public static void scheduleUpdateLogic(Schedule schedule, IScheduleCreateRequest request)
	    throws SiteWhereException {
	Persistence.entityUpdateLogic(request, schedule);

	if (request.getName() != null) {
	    schedule.setName(request.getName());
	}
	if (request.getTriggerType() != null) {
	    schedule.setTriggerType(request.getTriggerType());
	}
	if (request.getTriggerConfiguration() != null) {
	    schedule.getTriggerConfiguration().clear();
	    schedule.getTriggerConfiguration().putAll(request.getTriggerConfiguration());
	}

	schedule.setStartDate(request.getStartDate());
	schedule.setEndDate(request.getEndDate());
    }

    /**
     * Handle common logic for creating a scheduled job.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static ScheduledJob scheduledJobCreateLogic(ISchedule schedule, IScheduledJobCreateRequest request)
	    throws SiteWhereException {
	ScheduledJob job = new ScheduledJob();
	Persistence.entityCreateLogic(request, job);
	job.setScheduleId(schedule.getId());

	// Job type is required.
	requireNotNull("Job Type", request.getJobType());
	job.setJobType(request.getJobType());
	job.setJobConfiguration(request.getJobConfiguration());
	job.setJobState(ScheduledJobState.Unsubmitted);

	return job;
    }

    /**
     * Handle common logic for updating a scheduled job.
     * 
     * @param job
     * @param request
     * @throws SiteWhereException
     */
    public static void scheduledJobUpdateLogic(ScheduledJob job, IScheduledJobCreateRequest request)
	    throws SiteWhereException {
	Persistence.entityUpdateLogic(request, job);

	if (request.getJobType() != null) {
	    job.setJobType(request.getJobType());
	}
	if (request.getJobConfiguration() != null) {
	    job.getJobConfiguration().clear();
	    job.getJobConfiguration().putAll(request.getJobConfiguration());
	}
	if (request.getJobState() != null) {
	    job.setJobState(request.getJobState());
	}
    }
}