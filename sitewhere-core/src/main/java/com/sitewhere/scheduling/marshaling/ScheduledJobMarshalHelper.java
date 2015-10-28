/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.scheduling.marshaling;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sitewhere.SiteWhere;
import com.sitewhere.device.marshaling.CommandHtmlHelper;
import com.sitewhere.device.marshaling.DeviceAssignmentMarshalHelper;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.rest.model.device.command.DeviceCommand;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.scheduling.ScheduledJob;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetModuleManager;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.JobConstants;
import com.sitewhere.spi.user.ITenant;

/**
 * Configurable helper class that allows {@link ScheduledJob} model objects to be created
 * from {@link IScheduledJob} SPI objects.
 * 
 * @author dadams
 */
public class ScheduledJobMarshalHelper {

	/** Static logger instance */
	@SuppressWarnings("unused")
	private static Logger LOGGER = Logger.getLogger(ScheduledJobMarshalHelper.class);

	/** Tenant */
	private ITenant tenant;

	/** Indicates whether to include context information */
	private boolean includeContextInfo = false;

	/** Used for marshaling device assignment info */
	private DeviceAssignmentMarshalHelper assignmentHelper;

	public ScheduledJobMarshalHelper(ITenant tenant) {
		this(tenant, false);
	}

	public ScheduledJobMarshalHelper(ITenant tenant, boolean includeContextInfo) {
		this.tenant = tenant;
		this.includeContextInfo = includeContextInfo;
		this.assignmentHelper =
				new DeviceAssignmentMarshalHelper(tenant).setIncludeDevice(true).setIncludeAsset(false);
	}

	/**
	 * Convert an {@link IScheduledJob} to a {@link ScheduledJob} using the specified
	 * marshaling parameters.
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
			ISchedule schedule = getScheduleManagement(tenant).getScheduleByToken(job.getScheduleToken());
			if (schedule != null) {
				job.getContext().put("schedule", schedule);
			}
			switch (job.getJobType()) {
			case CommandInvocation: {
				includeCommandInvocationContext(job);
				break;
			}
			default: {
			}
			}
		}

		return job;
	}

	/**
	 * Includes contextual information specific to a command invocation. This data is
	 * useful for displaying the job in a user interface.
	 * 
	 * @param job
	 * @throws SiteWhereException
	 */
	protected void includeCommandInvocationContext(ScheduledJob job) throws SiteWhereException {
		String assnToken = job.getJobConfiguration().get(JobConstants.CommandInvocation.ASSIGNMENT_TOKEN);
		String commandToken = job.getJobConfiguration().get(JobConstants.CommandInvocation.COMMAND_TOKEN);
		if (assnToken != null) {
			IDeviceAssignment assignment =
					getDeviceManagement(getTenant()).getDeviceAssignmentByToken(assnToken);
			if (assignment != null) {
				job.getContext().put("assignment",
						getAssignmentHelper().convert(assignment, getAssetModuleManager(getTenant())));
			}
		}
		if (commandToken != null) {
			IDeviceCommand command = getDeviceManagement(getTenant()).getDeviceCommandByToken(commandToken);
			if (command != null) {
				Map<String, String> paramValues = new HashMap<String, String>();
				for (String key : job.getJobConfiguration().keySet()) {
					if (key.startsWith(JobConstants.CommandInvocation.PARAMETER_PREFIX)) {
						String paramKey =
								key.substring(JobConstants.CommandInvocation.PARAMETER_PREFIX.length());
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
	 * Get the device management implementation.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	protected IDeviceManagement getDeviceManagement(ITenant tenant) throws SiteWhereException {
		return SiteWhere.getServer().getDeviceManagement(tenant);
	}

	/**
	 * Get the schedule management implementation.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	protected IScheduleManagement getScheduleManagement(ITenant tenant) throws SiteWhereException {
		return SiteWhere.getServer().getScheduleManagement(tenant);
	}

	/**
	 * Get the asset module manager implementation.
	 * 
	 * @param tenant
	 * @return
	 * @throws SiteWhereException
	 */
	protected IAssetModuleManager getAssetModuleManager(ITenant tenant) throws SiteWhereException {
		return SiteWhere.getServer().getAssetModuleManager(tenant);
	}

	/**
	 * Get helper class for marshaling device assignment information.
	 * 
	 * @return
	 */
	protected DeviceAssignmentMarshalHelper getAssignmentHelper() {
		return assignmentHelper;
	}

	public ITenant getTenant() {
		return tenant;
	}

	public void setTenant(ITenant tenant) {
		this.tenant = tenant;
	}

	public boolean isIncludeContextInfo() {
		return includeContextInfo;
	}

	public void setIncludeContextInfo(boolean includeContextInfo) {
		this.includeContextInfo = includeContextInfo;
	}
}