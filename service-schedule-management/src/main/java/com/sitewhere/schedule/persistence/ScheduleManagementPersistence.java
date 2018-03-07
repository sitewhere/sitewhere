/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.persistence;

import java.util.UUID;

import com.sitewhere.persistence.Persistence;
import com.sitewhere.rest.model.common.MetadataProvider;
import com.sitewhere.rest.model.scheduling.Schedule;
import com.sitewhere.rest.model.scheduling.ScheduledJob;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.scheduling.ScheduledJobState;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;

/**
 * Persistence logic for schedule components.
 * 
 * @author Derek
 */
public class ScheduleManagementPersistence extends Persistence {

    /**
     * Handle common logic for creating a schedule.
     * 
     * @param request
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static Schedule scheduleCreateLogic(IScheduleCreateRequest request, String token) throws SiteWhereException {
	Schedule schedule = new Schedule();
	schedule.setId(UUID.randomUUID());

	// Use token if provided, otherwise generate one.
	if (request.getToken() != null) {
	    schedule.setToken(request.getToken());
	} else {
	    schedule.setToken(UUID.randomUUID().toString());
	}

	// Name is required.
	if (request.getName() == null) {
	    throw new SiteWhereSystemException(ErrorCode.IncompleteData, ErrorLevel.ERROR);
	}
	schedule.setName(request.getName());

	// Trigger type is required.
	if (request.getTriggerType() == null) {
	    throw new SiteWhereSystemException(ErrorCode.IncompleteData, ErrorLevel.ERROR);
	}
	schedule.setTriggerType(request.getTriggerType());
	schedule.setTriggerConfiguration(request.getTriggerConfiguration());

	schedule.setStartDate(request.getStartDate());
	schedule.setEndDate(request.getEndDate());

	Persistence.initializeEntityMetadata(schedule);
	MetadataProvider.copy(request.getMetadata(), schedule);

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
	if (request.getToken() != null) {
	    schedule.setToken(request.getToken());
	}
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

	Persistence.setUpdatedEntityMetadata(schedule);
    }

    /**
     * Handle common logic for creating a scheduled job.
     * 
     * @param request
     * @param token
     * @return
     * @throws SiteWhereException
     */
    public static ScheduledJob scheduledJobCreateLogic(IScheduledJobCreateRequest request, String token)
	    throws SiteWhereException {
	ScheduledJob job = new ScheduledJob();
	job.setId(UUID.randomUUID());

	// Use token if provided, otherwise generate one.
	if (request.getToken() != null) {
	    job.setToken(request.getToken());
	} else {
	    job.setToken(UUID.randomUUID().toString());
	}

	// Schedule token is required.
	if (request.getScheduleToken() == null) {
	    throw new SiteWhereSystemException(ErrorCode.IncompleteData, ErrorLevel.ERROR);
	}
	job.setScheduleToken(request.getScheduleToken());

	// Job type is required.
	if (request.getJobType() == null) {
	    throw new SiteWhereSystemException(ErrorCode.IncompleteData, ErrorLevel.ERROR);
	}
	job.setJobType(request.getJobType());
	job.setJobConfiguration(request.getJobConfiguration());

	job.setJobState(ScheduledJobState.Unsubmitted);

	Persistence.initializeEntityMetadata(job);
	MetadataProvider.copy(request.getMetadata(), job);

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
	if (request.getToken() != null) {
	    job.setToken(request.getToken());
	}
	if (request.getScheduleToken() != null) {
	    job.setScheduleToken(request.getScheduleToken());
	}
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
	Persistence.setUpdatedEntityMetadata(job);
    }
}