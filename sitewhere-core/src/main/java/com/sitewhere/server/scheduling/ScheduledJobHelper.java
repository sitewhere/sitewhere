/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.scheduling;

import java.util.HashMap;
import java.util.Map;

import com.sitewhere.rest.model.scheduling.request.ScheduledJobCreateRequest;
import com.sitewhere.spi.scheduling.JobConstants;
import com.sitewhere.spi.scheduling.ScheduledJobType;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;

/**
 * Helper class for building {@link IScheduledJobCreateRequest} instances based on job
 * types.
 * 
 * @author Derek
 */
public class ScheduledJobHelper {

	/**
	 * Create job that will invoke a command on an assignment.
	 * 
	 * @param token
	 * @param assignmentToken
	 * @param commandToken
	 * @param parameters
	 * @param scheduleToken
	 * @return
	 */
	public static IScheduledJobCreateRequest createCommandInvocationJob(String token, String assignmentToken,
			String commandToken, Map<String, String> parameters, String scheduleToken) {
		ScheduledJobCreateRequest job = new ScheduledJobCreateRequest();

		job.setToken(token);
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
}