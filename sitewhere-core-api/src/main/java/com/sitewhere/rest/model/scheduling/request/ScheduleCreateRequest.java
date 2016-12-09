/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.scheduling.request;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.TriggerConstants;
import com.sitewhere.spi.scheduling.TriggerType;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;

/**
 * Holds fields needed to create a new schedule.
 * 
 * @author Derek Adams
 */
@JsonInclude(Include.NON_NULL)
public class ScheduleCreateRequest implements IScheduleCreateRequest {

    /** Serial version UID */
    private static final long serialVersionUID = 1453554726838184776L;

    /** Unique token */
    private String token;

    /** Name */
    private String name;

    /** Trigger type */
    private TriggerType triggerType;

    /** Trigger configuration */
    private Map<String, String> triggerConfiguration;

    /** Date schedule takes effect */
    private Date startDate;

    /** Date schedule is no longer in effect */
    private Date endDate;

    /** Metadata */
    private Map<String, String> metadata;

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.request.IScheduleCreateRequest#getToken()
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
     * @see
     * com.sitewhere.spi.scheduling.request.IScheduleCreateRequest#getName()
     */
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.request.IScheduleCreateRequest#
     * getTriggerType()
     */
    public TriggerType getTriggerType() {
	return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
	this.triggerType = triggerType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.scheduling.request.IScheduleCreateRequest#
     * getTriggerConfiguration ()
     */
    public Map<String, String> getTriggerConfiguration() {
	return triggerConfiguration;
    }

    public void setTriggerConfiguration(Map<String, String> triggerConfiguration) {
	this.triggerConfiguration = triggerConfiguration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.request.IScheduleCreateRequest#getStartDate(
     * )
     */
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.request.IScheduleCreateRequest#getEndDate()
     */
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.scheduling.request.IScheduleCreateRequest#getMetadata()
     */
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    public static class Builder {

	/** Request being built */
	private ScheduleCreateRequest request = new ScheduleCreateRequest();

	public Builder(ISchedule schedule) {
	    request.setToken(schedule.getToken());
	    request.setName(schedule.getName());
	    request.setTriggerType(schedule.getTriggerType());
	    request.setTriggerConfiguration(schedule.getTriggerConfiguration());
	    request.setStartDate(schedule.getStartDate());
	    request.setEndDate(schedule.getEndDate());
	    request.setMetadata(schedule.getMetadata());
	}

	public Builder(String name) {
	    this(UUID.randomUUID().toString(), name);
	}

	public Builder(String token, String name) {
	    request.setToken(token);
	    request.setName(name);
	}

	public Builder withSimpleSchedule(Long interval, Integer count) {
	    request.setTriggerType(TriggerType.SimpleTrigger);
	    Map<String, String> config = new HashMap<String, String>();
	    if (interval != null) {
		config.put(TriggerConstants.SimpleTrigger.REPEAT_INTERVAL, String.valueOf(interval));
	    }
	    if (count != null) {
		config.put(TriggerConstants.SimpleTrigger.REPEAT_COUNT, String.valueOf(count));
	    }
	    request.setTriggerConfiguration(config);
	    return this;
	}

	public Builder withCronSchedule(String cronExpression) {
	    request.setTriggerType(TriggerType.CronTrigger);
	    Map<String, String> config = new HashMap<String, String>();
	    config.put(TriggerConstants.CronTrigger.CRON_EXPRESSION, cronExpression);
	    request.setTriggerConfiguration(config);
	    return this;
	}

	public Builder withStartDate(Date startDate) {
	    request.setStartDate(startDate);
	    return this;
	}

	public Builder withEndDate(Date endDate) {
	    request.setEndDate(endDate);
	    return this;
	}

	public ScheduleCreateRequest build() {
	    return request;
	}
    }
}