/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.scheduling;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.MetadataProviderEntity;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.ScheduledJobState;
import com.sitewhere.spi.scheduling.ScheduledJobType;

/**
 * Model object for a scheduled job.
 * 
 * @author Derek
 */
@JsonInclude(Include.NON_NULL)
public class ScheduledJob extends MetadataProviderEntity implements IScheduledJob {

	/** Serial version UID */
	private static final long serialVersionUID = -8440919585518011992L;

	/** Unique token */
	private String token;

	/** Token for associated schedule */
	private String scheduleToken;

	/** Job type */
	private ScheduledJobType jobType;

	/** Job configuration */
	private Map<String, String> jobConfiguration = new HashMap<String, String>();

	/** Job state */
	private ScheduledJobState jobState;

	/** FIELDS BELOW DEPEND ON MARSHALING PARAMETERS */

	/** Extra context information based on job type */
	private Map<String, Object> context;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.scheduling.IScheduledJob#getToken()
	 */
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.scheduling.IScheduledJob#getScheduleToken()
	 */
	public String getScheduleToken() {
		return scheduleToken;
	}

	public void setScheduleToken(String scheduleToken) {
		this.scheduleToken = scheduleToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.scheduling.IScheduledJob#getJobType()
	 */
	public ScheduledJobType getJobType() {
		return jobType;
	}

	public void setJobType(ScheduledJobType jobType) {
		this.jobType = jobType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.scheduling.IScheduledJob#getJobConfiguration()
	 */
	public Map<String, String> getJobConfiguration() {
		return jobConfiguration;
	}

	public void setJobConfiguration(Map<String, String> jobConfiguration) {
		this.jobConfiguration = jobConfiguration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.scheduling.IScheduledJob#getJobState()
	 */
	public ScheduledJobState getJobState() {
		return jobState;
	}

	public void setJobState(ScheduledJobState jobState) {
		this.jobState = jobState;
	}

	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}
}