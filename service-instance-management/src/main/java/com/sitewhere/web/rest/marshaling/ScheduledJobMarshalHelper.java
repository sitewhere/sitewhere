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
package com.sitewhere.web.rest.marshaling;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.CommandHtmlHelper;
import com.sitewhere.microservice.api.device.DeviceAssignmentMarshalHelper;
import com.sitewhere.microservice.api.device.DeviceTypeMarshalHelper;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.microservice.api.schedule.IScheduleManagement;
import com.sitewhere.rest.model.common.PersistentEntity;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.marshaling.MarshaledDeviceCommandInvocation;
import com.sitewhere.rest.model.scheduling.ScheduledJob;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.JobConstants;

/**
 * Configurable helper class that allows {@link ScheduledJob} model objects to
 * be created from {@link IScheduledJob} SPI objects.
 */
public class ScheduledJobMarshalHelper {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(ScheduledJobMarshalHelper.class);

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Schedule management */
    private IScheduleManagement scheduleManagement;

    /** Asset management */
    private IAssetManagement assetManagement;

    /** Indicates whether to include context information */
    private boolean includeContextInfo = false;

    /** Used for marshaling device assignment info */
    private DeviceAssignmentMarshalHelper assignmentHelper;

    /** Used for marshaling device type info */
    private DeviceTypeMarshalHelper deviceTypeHelper;

    public ScheduledJobMarshalHelper(IScheduleManagement scheduleManagement, IDeviceManagement deviceManagement,
	    IAssetManagement assetManagement) {
	this(scheduleManagement, deviceManagement, assetManagement, false);
    }

    public ScheduledJobMarshalHelper(IScheduleManagement scheduleManagement, IDeviceManagement deviceManagement,
	    IAssetManagement assetManagement, boolean includeContextInfo) {
	this.scheduleManagement = scheduleManagement;
	this.deviceManagement = deviceManagement;
	this.assetManagement = assetManagement;
	this.includeContextInfo = includeContextInfo;
	this.assignmentHelper = new DeviceAssignmentMarshalHelper(deviceManagement).setIncludeDevice(true)
		.setIncludeAsset(false);
	this.deviceTypeHelper = new DeviceTypeMarshalHelper(deviceManagement);
    }

    /**
     * Convert an {@link IScheduledJob} to a {@link ScheduledJob} using the
     * specified marshaling parameters.
     * 
     * @param source
     * @return
     * @throws SiteWhereException
     */
    public ScheduledJob convert(IScheduledJob source) throws SiteWhereException {
	ScheduledJob job = new ScheduledJob();
	PersistentEntity.copy(source, job);

	job.setToken(source.getToken());
	job.setScheduleId(source.getScheduleId());
	job.setJobType(source.getJobType());
	job.getJobConfiguration().putAll(source.getJobConfiguration());

	if (isIncludeContextInfo()) {
	    job.setContext(new HashMap<String, Object>());
	    ISchedule schedule = getScheduleManagement().getSchedule(job.getScheduleId());
	    if (schedule != null) {
		job.getContext().put("schedule", schedule);
	    }
	    switch (job.getJobType()) {
	    case CommandInvocation: {
		includeCommandInvocationContext(job);
		break;
	    }
	    case BatchCommandInvocation: {
		includeBatchCommandInvocationContext(job);
		break;
	    }
	    default: {
	    }
	    }
	}

	return job;
    }

    /**
     * Includes contextual information specific to a command invocation. This data
     * is useful for displaying the job in a user interface.
     * 
     * @param job
     * @throws SiteWhereException
     */
    protected void includeCommandInvocationContext(ScheduledJob job) throws SiteWhereException {
	String assnToken = job.getJobConfiguration().get(JobConstants.CommandInvocation.ASSIGNMENT_TOKEN);
	String commandToken = job.getJobConfiguration().get(JobConstants.CommandInvocation.COMMAND_TOKEN);
	IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(assnToken);
	if (assignment != null) {
	    job.getContext().put("assignment", getAssignmentHelper().convert(assignment, getAssetManagement()));
	}
	if (commandToken != null) {
	    IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(assignment.getDeviceTypeId(),
		    commandToken);
	    if (command != null) {
		Map<String, String> paramValues = new HashMap<String, String>();
		for (String key : job.getJobConfiguration().keySet()) {
		    if (key.startsWith(JobConstants.CommandInvocation.PARAMETER_PREFIX)) {
			String paramKey = key.substring(JobConstants.CommandInvocation.PARAMETER_PREFIX.length());
			paramValues.put(paramKey, job.getJobConfiguration().get(key));
		    }
		}

		// Emulate an invocation to produce sample html.
		MarshaledDeviceCommandInvocation invocation = new MarshaledDeviceCommandInvocation();
		invocation.setCommand(DeviceCommand.copy(command));
		invocation.setParameterValues(paramValues);
		String html = CommandHtmlHelper.getHtml(invocation);

		job.getContext().put("command", command);
		job.getContext().put("invocationHtml", html);
	    }
	}
    }

    /**
     * Includes contextual information specific to a batch command invocation. This
     * data is useful for displaying the job in a user interface.
     * 
     * @param job
     * @throws SiteWhereException
     */
    protected void includeBatchCommandInvocationContext(ScheduledJob job) throws SiteWhereException {
	String deviceTypeToken = job.getJobConfiguration()
		.get(JobConstants.InvocationByDeviceCriteria.DEVICE_TYPE_TOKEN);
	IDeviceType deviceType = getDeviceManagement().getDeviceTypeByToken(deviceTypeToken);
	if (deviceType != null) {
	    job.getContext().put("deviceType", getDeviceTypeHelper().convert(deviceType));
	}
	String commandToken = job.getJobConfiguration().get(JobConstants.CommandInvocation.COMMAND_TOKEN);
	if (commandToken != null) {
	    IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(deviceType.getId(), commandToken);
	    if (command != null) {
		Map<String, String> paramValues = new HashMap<String, String>();
		for (String key : job.getJobConfiguration().keySet()) {
		    if (key.startsWith(JobConstants.CommandInvocation.PARAMETER_PREFIX)) {
			String paramKey = key.substring(JobConstants.CommandInvocation.PARAMETER_PREFIX.length());
			paramValues.put(paramKey, job.getJobConfiguration().get(key));
		    }
		}

		// Emulate an invocation to produce sample html.
		MarshaledDeviceCommandInvocation invocation = new MarshaledDeviceCommandInvocation();
		invocation.setCommand(DeviceCommand.copy(command));
		invocation.setParameterValues(paramValues);

		job.getContext().put("command", command);
	    }
	}
    }

    /**
     * Get helper class for marshaling device assignment information.
     * 
     * @return
     */
    protected DeviceAssignmentMarshalHelper getAssignmentHelper() {
	return assignmentHelper;
    }

    /**
     * Get helper class for marshaling device type information.
     * 
     * @return
     */
    protected DeviceTypeMarshalHelper getDeviceTypeHelper() {
	return deviceTypeHelper;
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    public IScheduleManagement getScheduleManagement() {
	return scheduleManagement;
    }

    public void setScheduleManagement(IScheduleManagement scheduleManagement) {
	this.scheduleManagement = scheduleManagement;
    }

    public IAssetManagement getAssetManagement() {
	return assetManagement;
    }

    public void setAssetManagement(IAssetManagement assetManagement) {
	this.assetManagement = assetManagement;
    }

    public boolean isIncludeContextInfo() {
	return includeContextInfo;
    }

    public void setIncludeContextInfo(boolean includeContextInfo) {
	this.includeContextInfo = includeContextInfo;
    }
}