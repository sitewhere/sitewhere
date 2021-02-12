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
package com.sitewhere.schedule.jobs;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.microservice.tenant.ITenantManagement;
import com.sitewhere.spi.scheduling.JobConstants;
import com.sitewhere.spi.tenant.ITenant;

/**
 * Creates an {@link IDeviceCommandInvocation} as the result of a Quarz
 * schedule.
 */
public class CommandInvocationJob implements Job {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(CommandInvocationJob.class);

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
	    ITenant tenant = getTenantManagement().getTenant(context.getScheduler().getSchedulerName());
	    IDeviceEventManagement events = getDeviceEventManagement(tenant);
	    DeviceCommandInvocationCreateRequest create = new DeviceCommandInvocationCreateRequest();
	    create.setCommandToken(getCommandToken());
	    create.setParameterValues(getParameters());
	    create.setInitiator(CommandInitiator.Scheduler);
	    create.setInitiatorId(context.getJobDetail().getKey().getName());
	    create.setTarget(CommandTarget.Assignment);
	    create.setTargetId(getAssignmentToken());
	    create.setEventDate(new Date());
	    events.addDeviceCommandInvocations(null, create);
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

    private ITenantManagement getTenantManagement() {
	return null;
    }

    private IDeviceEventManagement getDeviceEventManagement(ITenant tenant) {
	return null;
    }
}