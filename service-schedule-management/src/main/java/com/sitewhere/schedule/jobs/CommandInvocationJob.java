/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.jobs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.sitewhere.SiteWhere;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.scheduling.JobConstants;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Creates an {@link IDeviceCommandInvocation} as the result of a Quarz
 * schedule.
 * 
 * @author Derek
 */
public class CommandInvocationJob implements Job {

    /** Static logger instance */
    private static final Logger LOGGER = LogManager.getLogger();

    /** Assignment token */
    private String assignmentToken;

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
	if (getAssignmentToken() == null) {
	    throw new JobExecutionException("Assignment token not provided.");
	}
	if (getCommandToken() == null) {
	    throw new JobExecutionException("Command token not provided.");
	}
	try {
	    ITenant tenant = SiteWhere.getServer().getTenantManagement()
		    .getTenantById(context.getScheduler().getSchedulerName());
	    IDeviceEventManagement events = SiteWhere.getServer().getDeviceEventManagement(tenant);
	    DeviceCommandInvocationCreateRequest create = new DeviceCommandInvocationCreateRequest();
	    create.setCommandToken(getCommandToken());
	    create.setParameterValues(getParameters());
	    create.setInitiator(CommandInitiator.Scheduler);
	    create.setInitiatorId(context.getJobDetail().getKey().getName());
	    create.setTarget(CommandTarget.Assignment);
	    create.setTargetId(getAssignmentToken());
	    create.setEventDate(new Date());
	    events.addDeviceCommandInvocation(null, create);
	    LOGGER.info("Executed command invocation job.");
	} catch (SiteWhereException e) {
	    throw new JobExecutionException("Unable to create command invocation.", e);
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
	    if (JobConstants.CommandInvocation.ASSIGNMENT_TOKEN.equals(key)) {
		this.assignmentToken = value;
	    } else if (JobConstants.CommandInvocation.COMMAND_TOKEN.equals(key)) {
		this.commandToken = value;
	    } else if (key.startsWith(JobConstants.CommandInvocation.PARAMETER_PREFIX)) {
		String paramKey = key.substring(JobConstants.CommandInvocation.PARAMETER_PREFIX.length());
		getParameters().put(paramKey, value);
	    }
	}
    }

    public String getAssignmentToken() {
	return assignmentToken;
    }

    public void setAssignmentToken(String assignmentToken) {
	this.assignmentToken = assignmentToken;
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