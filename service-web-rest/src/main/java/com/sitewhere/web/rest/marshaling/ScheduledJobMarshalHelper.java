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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.device.marshaling.CommandHtmlHelper;
import com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper;
import com.sitewhere.device.marshaling.DeviceSpecificationMarshalHelper;
import com.sitewhere.rest.model.batch.request.BatchCommandForCriteriaRequest;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.scheduling.ScheduledJob;
import com.sitewhere.schedule.BatchCommandInvocationJobParser;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceSpecification;
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
    private static Logger LOGGER = LogManager.getLogger();

    /** Device management */
    private IDeviceManagement deviceManagement;

    /** Schedule management */
    private IScheduleManagement scheduleManagement;

    /** Asset resolver */
    private IAssetResolver assetResolver;

    /** Indicates whether to include context information */
    private boolean includeContextInfo = false;

    /** Used for marshaling device assignment info */
    private DeviceAssignmentMarshalHelper assignmentHelper;

    /** Used for marshaling device specification info */
    private DeviceSpecificationMarshalHelper specificationHelper;

    public ScheduledJobMarshalHelper(IScheduleManagement scheduleManagement, IDeviceManagement deviceManagement,
	    IAssetResolver assetResolver) {
	this(scheduleManagement, deviceManagement, assetResolver, false);
    }

    public ScheduledJobMarshalHelper(IScheduleManagement scheduleManagement, IDeviceManagement deviceManagement,
	    IAssetResolver assetResolver, boolean includeContextInfo) {
	this.scheduleManagement = scheduleManagement;
	this.deviceManagement = deviceManagement;
	this.assetResolver = assetResolver;
	this.includeContextInfo = includeContextInfo;
	this.assignmentHelper = new DeviceAssignmentMarshalHelper(deviceManagement).setIncludeDevice(true)
		.setIncludeAsset(false);
	this.specificationHelper = new DeviceSpecificationMarshalHelper(deviceManagement).setIncludeAsset(false);
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
     * Includes contextual information specific to a command invocation. This
     * data is useful for displaying the job in a user interface.
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
		job.getContext().put("assignment", getAssignmentHelper().convert(assignment, getAssetResolver()));
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
     * Includes contextual information specific to a batch command invocation.
     * This data is useful for displaying the job in a user interface.
     * 
     * @param job
     * @throws SiteWhereException
     */
    protected void includeBatchCommandInvocationContext(ScheduledJob job) throws SiteWhereException {
	String specToken = job.getJobConfiguration().get(JobConstants.BatchCommandInvocation.SPECIFICATION_TOKEN);
	if (specToken != null) {
	    IDeviceSpecification specification = getDeviceManagement().getDeviceSpecificationByToken(specToken);
	    if (specification != null) {
		job.getContext().put("specification",
			getSpecificationHelper().convert(specification, getAssetResolver()));
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
     * Get helper class for marshaling device specification information.
     * 
     * @return
     */
    protected DeviceSpecificationMarshalHelper getSpecificationHelper() {
	return specificationHelper;
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

    public IAssetResolver getAssetResolver() {
	return assetResolver;
    }

    public void setAssetResolver(IAssetResolver assetResolver) {
	this.assetResolver = assetResolver;
    }

    public boolean isIncludeContextInfo() {
	return includeContextInfo;
    }

    public void setIncludeContextInfo(boolean includeContextInfo) {
	this.includeContextInfo = includeContextInfo;
    }
}