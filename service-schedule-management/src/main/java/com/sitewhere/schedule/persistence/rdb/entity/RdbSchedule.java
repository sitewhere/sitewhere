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

import java.util.Date;
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
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.TriggerType;

@Entity
@Table(name = "schedule")
@NamedQuery(name = Queries.QUERY_SCHEDULE_BY_TOKEN, query = "SELECT s FROM RdbSchedule s WHERE s.token = :token")
public class RdbSchedule extends RdbPersistentEntity implements ISchedule {

    /** Serial version UID */
    private static final long serialVersionUID = 8080348627065046644L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "trigger_type")
    @Enumerated(EnumType.STRING)
    private TriggerType triggerType;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "schedule_metadata", joinColumns = @JoinColumn(name = "schedule_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> metadata = new HashMap<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "trigger_configuration", joinColumns = @JoinColumn(name = "schedule_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> triggerConfiguration = new HashMap<>();

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
     * @see com.sitewhere.spi.scheduling.ISchedule#getName()
     */
    @Override
    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    /*
     * @see com.sitewhere.spi.scheduling.ISchedule#getStartDate()
     */
    @Override
    public Date getStartDate() {
	return startDate;
    }

    public void setStartDate(Date startDate) {
	this.startDate = startDate;
    }

    /*
     * @see com.sitewhere.spi.scheduling.ISchedule#getEndDate()
     */
    @Override
    public Date getEndDate() {
	return endDate;
    }

    public void setEndDate(Date endDate) {
	this.endDate = endDate;
    }

    /*
     * @see com.sitewhere.spi.scheduling.ISchedule#getTriggerType()
     */
    @Override
    public TriggerType getTriggerType() {
	return triggerType;
    }

    public void setTriggerType(TriggerType triggerType) {
	this.triggerType = triggerType;
    }

    /*
     * @see com.sitewhere.spi.common.IMetadataProvider#getMetadata()
     */
    @Override
    public Map<String, String> getMetadata() {
	return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
	this.metadata = metadata;
    }

    /*
     * @see com.sitewhere.spi.scheduling.ISchedule#getTriggerConfiguration()
     */
    @Override
    public Map<String, String> getTriggerConfiguration() {
	return triggerConfiguration;
    }

    public void setTriggerConfiguration(Map<String, String> triggerConfiguration) {
	this.triggerConfiguration = triggerConfiguration;
    }

    public static void copy(ISchedule source, RdbSchedule target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getName() != null) {
	    target.setName(source.getName());
	}
	if (source.getStartDate() != null) {
	    target.setStartDate(source.getStartDate());
	}
	if (source.getEndDate() != null) {
	    target.setEndDate(source.getEndDate());
	}
	if (source.getTriggerType() != null) {
	    target.setTriggerType(source.getTriggerType());
	}
	if (source.getTriggerConfiguration() != null) {
	    target.getTriggerConfiguration().clear();
	    target.getTriggerConfiguration().putAll(source.getTriggerConfiguration());
	}
	RdbPersistentEntity.copy(source, target);
    }
}
