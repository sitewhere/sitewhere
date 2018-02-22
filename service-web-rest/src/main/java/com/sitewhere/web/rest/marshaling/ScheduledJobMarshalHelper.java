/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.rest.marshaling;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.device.marshaling.CommandHtmlHelper;
import com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper;
import com.sitewhere.device.marshaling.DeviceTypeMarshalHelper;
import com.sitewhere.rest.model.batch.request.BatchCommandForCriteriaRequest;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.scheduling.ScheduledJob;
import com.sitewhere.schedule.BatchCommandInvocationJobParser;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.JobConstants;

/**
 * Configurable helper class that allows {@link ScheduledJob} model objects to
 * be created from {@link IScheduledJob} SPI objects.
 * 
 * @author dadams
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
	this.deviceTypeHelper = new DeviceTypeMarshalHelper(deviceManagement).setIncludeAsset(false);
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
	MetadataProviderEntity.copy(source, job);

	job.setToken(source.getToken());
	job.setScheduleToken(source.getScheduleToken());
	job.setJobType(source.getJobType());
	job.getJobConfiguration().putAll(source.getJobConfiguration());

	if (isIncludeContextInfo()) {
	    job.setContext(new HashMap<String, Object>());
	    ISchedule schedule = getScheduleManagement().getScheduleByToken(job.getScheduleToken());
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
	if (assnToken != null) {
	    IDeviceAssignment assignment = getDeviceManagement().getDeviceAssignmentByToken(assnToken);
	    if (assignment != null) {
		job.getContext().put("assignment", getAssignmentHelper().convert(assignment, getAssetManagement()));
	    }
	}
	if (commandToken != null) {
	    IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(commandToken);
	    if (command != null) {
		Map<String, String> paramValues = new HashMap<String, String>();
		for (String key : job.getJobConfiguration().keySet()) {
		    if (key.startsWith(JobConstants.CommandInvocation.PARAMETER_PREFIX)) {
			String paramKey = key.substring(JobConstants.CommandInvocation.PARAMETER_PREFIX.length());
			paramValues.put(paramKey, job.getJobConfiguration().get(key));
		    }
		}

		// Emulate an invocation to produce sample html.
		DeviceCommandInvocation invocation = new DeviceCommandInvocation();
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
	String deviceTypeToken = job.getJobConfiguration().get(JobConstants.BatchCommandInvocation.DEVICE_TYPE_TOKEN);
	if (deviceTypeToken != null) {
	    IDeviceType deviceType = getDeviceManagement().getDeviceTypeByToken(deviceTypeToken);
	    if (deviceType != null) {
		job.getContext().put("deviceType", getDeviceTypeHelper().convert(deviceType, getAssetManagement()));
	    }
	    BatchCommandForCriteriaRequest criteria = BatchCommandInvocationJobParser.parse(job.getJobConfiguration());
	    String html = CommandHtmlHelper.getHtml(criteria, getDeviceManagement(), "..");
	    job.getContext().put("criteriaHtml", html);
	}
	String commandToken = job.getJobConfiguration().get(JobConstants.CommandInvocation.COMMAND_TOKEN);
	if (commandToken != null) {
	    IDeviceCommand command = getDeviceManagement().getDeviceCommandByToken(commandToken);
	    if (command != null) {
		Map<String, String> paramValues = new HashMap<String, String>();
		for (String key : job.getJobConfiguration().keySet()) {
		    if (key.startsWith(JobConstants.CommandInvocation.PARAMETER_PREFIX)) {
			String paramKey = key.substring(JobConstants.CommandInvocation.PARAMETER_PREFIX.length());
			paramValues.put(paramKey, job.getJobConfiguration().get(key));
		    }
		}

		// Emulate an invocation to produce sample html.
		DeviceCommandInvocation invocation = new DeviceCommandInvocation();
		invocation.setCommand(DeviceCommand.copy(command));
		invocation.setParameterValues(paramValues);
		String html = CommandHtmlHelper.getHtml(invocation);

		job.getContext().put("command", command);
		job.getContext().put("invocationHtml", html);
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