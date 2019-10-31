/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sitewhere.rest.model.batch.request.InvocationByAssignmentCriteriaRequest;
import com.sitewhere.rest.model.batch.request.InvocationByDeviceCriteriaRequest;
import com.sitewhere.rest.model.scheduling.request.ScheduledJobCreateRequest;
import com.sitewhere.spi.scheduling.JobConstants;
import com.sitewhere.spi.scheduling.ScheduledJobState;
import com.sitewhere.spi.scheduling.ScheduledJobType;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;

/**
 * Helper class for building {@link IScheduledJobCreateRequest} instances based
 * on job types.
 */
public class ScheduledJobHelper {

    /**
     * Create job that will invoke a command on an assignment.
     * 
     * @param assignmentToken
     * @param commandToken
     * @param parameters
     * @param scheduleToken
     * @return
     */
    public static IScheduledJobCreateRequest createCommandInvocationJob(String assignmentToken, String commandToken,
	    Map<String, String> parameters, String scheduleToken) {
	ScheduledJobCreateRequest job = new ScheduledJobCreateRequest();
	job.setToken(UUID.randomUUID().toString());
	job.setJobType(ScheduledJobType.CommandInvocation);

	Map<String, String> config = new HashMap<String, String>();
	config.put(JobConstants.CommandInvocation.ASSIGNMENT_TOKEN, assignmentToken);
	config.put(JobConstants.CommandInvocation.COMMAND_TOKEN, commandToken);
	for (String key : parameters.keySet()) {
	    String value = parameters.get(key);
	    config.put(JobConstants.CommandInvocation.PARAMETER_PREFIX + key, value);
	}
	job.setJobConfiguration(config);
	job.setScheduleToken(scheduleToken);

	return job;
    }

    /**
     * Create request for a job that uses device criteria to choose a list of
     * devices on which a command will be invoked.
     * 
     * @param request
     * @param scheduleToken
     * @return
     */
    public static IScheduledJobCreateRequest createBatchCommandInvocationJobForDeviceCriteria(
	    InvocationByDeviceCriteriaRequest request, String scheduleToken) {
	ScheduledJobCreateRequest job = new ScheduledJobCreateRequest();
	job.setToken(UUID.randomUUID().toString());
	job.setJobType(ScheduledJobType.BatchCommandInvocation);
	job.setJobState(ScheduledJobState.Unsubmitted);

	Map<String, String> config = new HashMap<String, String>();

	// Store command information.
	config.put(JobConstants.InvocationByDeviceCriteria.DEVICE_TYPE_TOKEN, request.getDeviceTypeToken());
	config.put(JobConstants.CommandInvocation.COMMAND_TOKEN, request.getCommandToken());
	for (String key : request.getParameterValues().keySet()) {
	    String value = request.getParameterValues().get(key);
	    config.put(JobConstants.CommandInvocation.PARAMETER_PREFIX + key, value);
	}

	// Store criteria information.
	job.setJobConfiguration(config);
	job.setScheduleToken(scheduleToken);

	return job;
    }

    /**
     * Create request for a job that uses assignment criteria to choose a list of
     * devices on which a command will be invoked.
     * 
     * @param request
     * @param scheduleToken
     * @return
     */
    public static IScheduledJobCreateRequest createBatchCommandInvocationJobForAssignmentCriteria(
	    InvocationByAssignmentCriteriaRequest request, String scheduleToken) {
	ScheduledJobCreateRequest job = new ScheduledJobCreateRequest();
	job.setToken(UUID.randomUUID().toString());
	job.setJobType(ScheduledJobType.BatchCommandInvocation);
	job.setJobState(ScheduledJobState.Unsubmitted);

	Map<String, String> config = new HashMap<String, String>();

	// Store command information.
	config.put(JobConstants.InvocationByAssignmentCriteria.DEVICE_TYPE_TOKEN, request.getDeviceTypeToken());
	config.put(JobConstants.CommandInvocation.COMMAND_TOKEN, request.getCommandToken());
	for (String key : request.getParameterValues().keySet()) {
	    String value = request.getParameterValues().get(key);
	    config.put(JobConstants.CommandInvocation.PARAMETER_PREFIX + key, value);
	}

	for (String token : request.getCustomerTokens()) {
	    config.put(JobConstants.InvocationByAssignmentCriteria.CUSTOMER_TOKEN_PREFIX + token, token);
	}
	for (String token : request.getAreaTokens()) {
	    config.put(JobConstants.InvocationByAssignmentCriteria.AREA_TOKEN_PREFIX + token, token);
	}
	for (String token : request.getAssetTokens()) {
	    config.put(JobConstants.InvocationByAssignmentCriteria.ASSET_TOKEN_PREFIX + token, token);
	}

	// Store criteria information.
	job.setJobConfiguration(config);
	job.setScheduleToken(scheduleToken);

	return job;
    }
}