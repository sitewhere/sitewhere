/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.scheduling.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.rest.model.common.request.PersistentEntityCreateRequest;
import com.sitewhere.spi.scheduling.ScheduledJobState;
import com.sitewhere.spi.scheduling.ScheduledJobType;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;

/**
 * Holds fields needed to create a new scheduled job.
 * 
 * @author Derek Adams
 */
@JsonInclude(Include.NON_NULL)
public class ScheduledJobCreateRequest extends PersistentEntityCreateRequest implements IScheduledJobCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = -627595397893118687L;

    /** Token for associated schedule */
    private String scheduleToken;

    /** Job type */
    private ScheduledJobType jobType;

    /** Job configuration */
    private Map<String, String> jobConfiguration;

    /** Job state */
    private ScheduledJobState jobState;

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest#
     * getScheduleToken()
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
     * @see com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest#
     * getJobType()
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
     * @see com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest#
     * getJobConfiguration ()
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
     * @see com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest#
     * getJobState()
     */
    public ScheduledJobState getJobState() {
	return jobState;
    }

    public void setJobState(ScheduledJobState jobState) {
	this.jobState = jobState;
    }
}