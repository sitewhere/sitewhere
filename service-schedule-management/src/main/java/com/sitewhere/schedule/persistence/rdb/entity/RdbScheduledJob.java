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
package com.sitewhere.schedule.persistence.rdb.entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.sitewhere.rdb.entities.RdbPersistentEntity;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.ScheduledJobState;
import com.sitewhere.spi.scheduling.ScheduledJobType;

@Entity
@Table(name = "scheduled_job")
@NamedQuery(name = Queries.QUERY_SCHEDULED_JOB_BY_TOKEN, query = "SELECT s FROM RdbScheduledJob s WHERE s.token = :token")
public class RdbScheduledJob extends RdbPersistentEntity implements IScheduledJob {

    /** Serial version UID */
    private static final long serialVersionUID = -8957546167032611759L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "schedule_id")
    private UUID scheduleId;

    @Column(name = "job_type")
    @Enumerated(EnumType.STRING)
    private ScheduledJobType jobType;

    @Column(name = "job_state")
    @Enumerated(EnumType.STRING)
    private ScheduledJobState jobState;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "scheduled_job_metadata", joinColumns = @JoinColumn(name = "scheduled_job_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> metadata = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "scheduled_job_configuration", joinColumns = @JoinColumn(name = "scheduled_job_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> jobConfiguration = new HashMap<>();

    /*
     * @see com.sitewhere.spi.common.IPersistentEntity#getId()
     */
    @Override
    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see com.sitewhere.spi.scheduling.IScheduledJob#getScheduleId()
     */
    @Override
    public UUID getScheduleId() {
	return scheduleId;
    }

    public void setScheduleId(UUID scheduleId) {
	this.scheduleId = scheduleId;
    }

    /*
     * @see com.sitewhere.spi.scheduling.IScheduledJob#getJobType()
     */
    @Override
    public ScheduledJobType getJobType() {
	return jobType;
    }

    public void setJobType(ScheduledJobType jobType) {
	this.jobType = jobType;
    }

    /*
     * @see com.sitewhere.spi.scheduling.IScheduledJob#getJobState()
     */
    @Override
    public ScheduledJobState getJobState() {
	return jobState;
    }

    public void setJobState(ScheduledJobState jobState) {
	this.jobState = jobState;
    }

    /*
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    /*
     * @see
     * com.sitewhere.rdb.entities.RdbPersistentEntity#setMetadata(java.util.Map)
     */
    @Override
    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    /*
     * @see com.sitewhere.spi.scheduling.IScheduledJob#getJobConfiguration()
     */
    @Override
    public Map<String, String> getJobConfiguration() {
	return jobConfiguration;
    }

    public void setJobConfiguration(Map<String, String> jobConfiguration) {
	this.jobConfiguration = jobConfiguration;
    }

    public static void copy(IScheduledJob source, RdbScheduledJob target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getScheduleId() != null) {
	    target.setScheduleId(source.getScheduleId());
	}
	if (source.getJobType() != null) {
	    target.setJobType(source.getJobType());
	}
	if (source.getJobState() != null) {
	    target.setJobState(source.getJobState());
	}
	if (source.getJobConfiguration() != null) {
	    target.getJobConfiguration().clear();
	    target.getJobConfiguration().putAll(source.getJobConfiguration());
	}
	RdbPersistentEntity.copy(source, target);
    }
}
