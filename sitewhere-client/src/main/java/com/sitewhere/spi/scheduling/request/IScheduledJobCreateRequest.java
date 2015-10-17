/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.spi.scheduling.request;

import java.io.Serializable;
import java.util.Map;

import com.sitewhere.spi.scheduling.ScheduledJobState;
import com.sitewhere.spi.scheduling.ScheduledJobType;

/**
 * Includes values needed to create a scheduled job.
 * 
 * @author Derek
 */
public interface IScheduledJobCreateRequest extends Serializable {

	/**
	 * Get unique job token.
	 * 
	 * @return
	 */
	public String getToken();

	/**
	 * Get unique schedule token.
	 * 
	 * @return
	 */
	public String getScheduleToken();

	/**
	 * Get job type.
	 * 
	 * @return
	 */
	public ScheduledJobType getJobType();

	/**
	 * Get job configuration values.
	 * 
	 * @return
	 */
	public Map<String, String> getJobConfiguration();

	/**
	 * Get job scheduling state.
	 * 
	 * @return
	 */
	public ScheduledJobState getJobState();

	/**
	 * Get metadata values.
	 * 
	 * @return
	 */
	public Map<String, String> getMetadata();
}