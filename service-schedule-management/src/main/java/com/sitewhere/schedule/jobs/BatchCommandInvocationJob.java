/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import com.sitewhere.server.schedule.BatchCommandInvocationJobParser;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Creates a batch command invocation as the result of a Quartz schedule.
 * 
 * @author Derek
 */
public class BatchCommandInvocationJob implements Job {

    /** Static logger instance */
    private static final Logger LOGGER = LogManager.getLogger();

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

	BatchCommandForCriteriaRequest criteria = BatchCommandInvocationJobParser.parse(data);
	if (criteria.getSpecificationToken() == null) {
	    throw new JobExecutionException("Specification token not provided.");
	}
	if (criteria.getCommandToken() == null) {
	    throw new JobExecutionException("Command token not provided.");
	}
	try {
	    ITenant tenant = SiteWhere.getServer().getTenantManagement()
		    .getTenantById(context.getScheduler().getSchedulerName());

	    // Resolve hardware ids for devices matching criteria.
	    List<String> hardwareIds = BatchUtils.getHardwareIds(criteria, tenant);

	    // Create batch command invocation.
	    BatchCommandInvocationRequest invoke = new BatchCommandInvocationRequest();
	    invoke.setCommandToken(criteria.getCommandToken());
	    invoke.setParameterValues(criteria.getParameterValues());
	    invoke.setHardwareIds(hardwareIds);

	    SiteWhere.getServer().getDeviceManagement(tenant).createBatchCommandInvocation(invoke);

	    SecurityContextHolder.getContext().setAuthentication(null);
	    LOGGER.info("Executed batch command invocation job.");
	} catch (SiteWhereException e) {
	    throw new JobExecutionException("Unable to create batch command invocation.", e);
	} catch (SchedulerException e) {
	    throw new JobExecutionException("Unable to get scheduler name.", e);
	}
    }
}