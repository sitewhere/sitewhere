/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rdb.entities;

import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.ScheduledJobState;
import com.sitewhere.spi.scheduling.ScheduledJobType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "scheduled_job")
public class ScheduledJob implements IScheduledJob {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "schedule_token")
    private String scheduleToken;

    @Column(name = "token")
    private String token;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "created_date")
    private Date createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_date")
    private Date updatedDate;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "job_type")
    @Enumerated(EnumType.STRING)
    private ScheduledJobType jobType;

    @Column(name = "job_state")
    @Enumerated(EnumType.STRING)
    private ScheduledJobState jobState;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name="scheduled_job_metadata", joinColumns = @JoinColumn(name = "schedule_job_id"))
    @MapKeyColumn(name="prop_key")
    @Column(name="prop_value")
    private Map<String, String> metadata = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name="scheduled_job_configuration", joinColumns = @JoinColumn(name = "schedule_job_id"))
    @MapKeyColumn(name="prop_key")
    @Column(name="prop_value")
    private Map<String, String> jobConfiguration = new HashMap<>();

    @Override
    public String getScheduleToken() {
        return scheduleToken;
    }

    @Override
    public ScheduledJobType getJobType() {
        return jobType;
    }

    @Override
    public Map<String, String> getJobConfiguration() {
        return jobConfiguration;
    }

    @Override
    public ScheduledJobState getJobState() {
        return jobState;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getToken() {
        return token;
    }

    @Override
    public Date getCreatedDate() {
        return createdDate;
    }

    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    @Override
    public Date getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScheduleToken(String scheduleToken) {
        this.scheduleToken = scheduleToken;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setJobType(ScheduledJobType jobType) {
        this.jobType = jobType;
    }

    public void setJobState(ScheduledJobState jobState) {
        this.jobState = jobState;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void setJobConfiguration(Map<String, String> jobConfiguration) {
        this.jobConfiguration = jobConfiguration;
    }
}
