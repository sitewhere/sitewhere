/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.server.scheduling.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sitewhere.SiteWhere;
import com.sitewhere.device.batch.BatchUtils;
import com.sitewhere.rest.model.device.request.BatchCommandForCriteriaRequest;
import com.sitewhere.rest.model.device.request.BatchCommandInvocationRequest;
import com.sitewhere.server.SiteWhereServer;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.JobConstants;
import com.sitewhere.spi.user.ITenant;

/**
 * Creates a batch command invocation as the result of a Quartz schedule.
 * 
 * @author Derek
 */
public class BatchCommandInvocationJob implements Job {

	/** Static logger instance */
	private static final Logger LOGGER = Logger.getLogger(BatchCommandInvocationJob.class);

	/** Specification token */
	private String specificationToken;

	/** Filter for excluding assigned devices */
	private boolean excludeAssigned;

	/** Group token if filtering by group */
	private String groupToken;

	/** Role if filtering by groups with role */
	private String groupRole;

	/** Command token */
	private String commandToken;

	/** Command parameters */
	private Map<String, String> parameters = new HashMap<String, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		parse(context);
		if (getSpecificationToken() == null) {
			throw new JobExecutionException("Specification token not provided.");
		}
		if (getCommandToken() == null) {
			throw new JobExecutionException("Command token not provided.");
		}
		try {
			ITenant tenant =
					SiteWhere.getServer().getUserManagement().getTenantById(
							context.getScheduler().getSchedulerName());

			// Resolve hardware ids for devices matching criteria.
			BatchCommandForCriteriaRequest request = new BatchCommandForCriteriaRequest();
			request.setCommandToken(getCommandToken());
			request.setParameterValues(getParameters());
			request.setSpecificationToken(getSpecificationToken());
			request.setExcludeAssigned(isExcludeAssigned());
			request.setGroupToken(getGroupToken());
			request.setGroupsWithRole(getGroupRole());
			List<String> hardwareIds = BatchUtils.getHardwareIds(request, tenant);

			// Create batch command invocation.
			BatchCommandInvocationRequest invoke = new BatchCommandInvocationRequest();
			invoke.setCommandToken(request.getCommandToken());
			invoke.setParameterValues(request.getParameterValues());
			invoke.setHardwareIds(hardwareIds);

			// Use the system account for logging "created by" on created elements.
			SecurityContextHolder.getContext().setAuthentication(SiteWhereServer.getSystemAuthentication());

			SiteWhere.getServer().getDeviceManagement(tenant).createBatchCommandInvocation(invoke);

			SecurityContextHolder.getContext().setAuthentication(null);
			LOGGER.info("Executed batch command invocation job.");
		} catch (SiteWhereException e) {
			throw new JobExecutionException("Unable to create batch command invocation.", e);
		} catch (SchedulerException e) {
			throw new JobExecutionException("Unable to get scheduler name.", e);
		}
	}

	/**
	 * Parse configuration data.
	 * 
	 * @param context
	 * @throws JobExecutionException
	 */
	protected void parse(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getJobDetail().getJobDataMap();
		for (String key : data.keySet()) {
			String value = data.getString(key);
			if (JobConstants.BatchCommandInvocation.SPECIFICATION_TOKEN.equals(key)) {
				this.specificationToken = value;
			} else if (JobConstants.BatchCommandInvocation.EXCLUDE_ASSIGNED.equals(key)) {
				this.excludeAssigned = Boolean.getBoolean(value);
			} else if (JobConstants.BatchCommandInvocation.GROUP_TOKEN.equals(key)) {
				this.groupToken = value;
			} else if (JobConstants.BatchCommandInvocation.GROUP_ROLE.equals(key)) {
				this.groupRole = value;
			} else if (JobConstants.CommandInvocation.COMMAND_TOKEN.equals(key)) {
				this.commandToken = value;
			} else if (key.startsWith(JobConstants.CommandInvocation.PARAMETER_PREFIX)) {
				String paramKey = key.substring(JobConstants.CommandInvocation.PARAMETER_PREFIX.length());
				getParameters().put(paramKey, value);
			}
		}
	}

	public String getSpecificationToken() {
		return specificationToken;
	}

	public void setSpecificationToken(String specificationToken) {
		this.specificationToken = specificationToken;
	}

	public boolean isExcludeAssigned() {
		return excludeAssigned;
	}

	public void setExcludeAssigned(boolean excludeAssigned) {
		this.excludeAssigned = excludeAssigned;
	}

	public String getGroupToken() {
		return groupToken;
	}

	public void setGroupToken(String groupToken) {
		this.groupToken = groupToken;
	}

	public String getGroupRole() {
		return groupRole;
	}

	public void setGroupRole(String groupRole) {
		this.groupRole = groupRole;
	}

	public String getCommandToken() {
		return commandToken;
	}

	public void setCommandToken(String commandToken) {
		this.commandToken = commandToken;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
}