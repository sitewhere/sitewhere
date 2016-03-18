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
import com.sitewhere.spi.tenant.ITenant;

/**
 * Creates a batch command invocation as the result of a Quartz schedule.
 * 
 * @author Derek
 */
public class BatchCommandInvocationJob implements Job {

	/** Static logger instance */
	private static final Logger LOGGER = Logger.getLogger(BatchCommandInvocationJob.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Map<String, String> data = new HashMap<String, String>();
		JobDataMap jobData = context.getJobDetail().getJobDataMap();
		for (String key : jobData.keySet()) {
			String value = jobData.getString(key);
			data.put(key, value);
		}

		BatchCommandForCriteriaRequest criteria = BatchCommandInvocationJob.parse(data);
		if (criteria.getSpecificationToken() == null) {
			throw new JobExecutionException("Specification token not provided.");
		}
		if (criteria.getCommandToken() == null) {
			throw new JobExecutionException("Command token not provided.");
		}
		try {
			ITenant tenant =
					SiteWhere.getServer().getTenantManagement().getTenantById(
							context.getScheduler().getSchedulerName());

			// Resolve hardware ids for devices matching criteria.
			List<String> hardwareIds = BatchUtils.getHardwareIds(criteria, tenant);

			// Create batch command invocation.
			BatchCommandInvocationRequest invoke = new BatchCommandInvocationRequest();
			invoke.setCommandToken(criteria.getCommandToken());
			invoke.setParameterValues(criteria.getParameterValues());
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
	 * @param data
	 * @throws JobExecutionException
	 */
	public static BatchCommandForCriteriaRequest parse(Map<String, String> data)
			throws JobExecutionException {

		String specificationToken = null;
		String siteToken = null;
		String groupToken = null;
		String groupRole = null;
		String commandToken = null;
		Map<String, String> parameters = new HashMap<String, String>();

		for (String key : data.keySet()) {
			String value = data.get(key);
			if (JobConstants.BatchCommandInvocation.SPECIFICATION_TOKEN.equals(key)) {
				specificationToken = value;
			} else if (JobConstants.BatchCommandInvocation.SITE_TOKEN.equals(key)) {
				siteToken = value;
			} else if (JobConstants.BatchCommandInvocation.GROUP_TOKEN.equals(key)) {
				groupToken = value;
			} else if (JobConstants.BatchCommandInvocation.GROUP_ROLE.equals(key)) {
				groupRole = value;
			} else if (JobConstants.CommandInvocation.COMMAND_TOKEN.equals(key)) {
				commandToken = value;
			} else if (key.startsWith(JobConstants.CommandInvocation.PARAMETER_PREFIX)) {
				String paramKey = key.substring(JobConstants.CommandInvocation.PARAMETER_PREFIX.length());
				parameters.put(paramKey, value);
			}
		}

		BatchCommandForCriteriaRequest request = new BatchCommandForCriteriaRequest();
		request.setCommandToken(commandToken);
		request.setParameterValues(parameters);
		request.setSpecificationToken(specificationToken);
		request.setSiteToken(siteToken);
		request.setGroupToken(groupToken);
		request.setGroupsWithRole(groupRole);
		return request;
	}
}