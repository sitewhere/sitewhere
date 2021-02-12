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
package com.sitewhere.devicestate.persistence.rdb.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.spi.device.state.IDeviceState;

@Entity
@Table(name = "device_state", indexes = {
	@Index(name = "device_state_device", columnList = "device_id", unique = false),
	@Index(name = "device_state_device_assignment", columnList = "device_assignment_id", unique = true) })
public class RdbDeviceState implements IDeviceState {

    /** Serial version UID */
    private static final long serialVersionUID = -44935949900753350L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @Column(name = "device_type_id", nullable = false)
    private UUID deviceTypeId;

    @Column(name = "device_assignment_id", nullable = false)
    private UUID deviceAssignmentId;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "area_id", nullable = false)
    private UUID areaId;

    @Column(name = "asset_id", nullable = false)
    private UUID assetId;

    @Column(name = "last_interaction_date ")
    private Date lastInteractionDate;

    @Column(name = "presence_missing_date ")
    private Date presenceMissingDate;

    @JsonIgnore
    @OneToMany(mappedBy = "deviceState", fetch = FetchType.LAZY)
    private List<RdbRecentLocationEvent> recentLocations;

    @JsonIgnore
    @OneToMany(mappedBy = "deviceState", fetch = FetchType.LAZY)
    private List<RdbRecentMeasurementEvent> recentMeasurements;

    @JsonIgnore
    @OneToMany(mappedBy = "deviceState", fetch = FetchType.LAZY)
    private List<RdbRecentAlertEvent> recentAlerts;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "device_state_metadata", joinColumns = @JoinColumn(name = "device_state_id"))
    @MapKeyColumn(name = "prop_key")
    @Column(name = "prop_value")
    private Map<String, String> metadata = new HashMap<>();

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
     * @see com.sitewhere.spi.device.state.IDeviceState#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getDeviceTypeId()
     */
    @Override
    public UUID getDeviceTypeId() {
	return deviceTypeId;
    }

    public void setDeviceTypeId(UUID deviceTypeId) {
	this.deviceTypeId = deviceTypeId;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getDeviceAssignmentId()
     */
    @Override
    public UUID getDeviceAssignmentId() {
	return deviceAssignmentId;
    }

    public void setDeviceAssignmentId(UUID deviceAssignmentId) {
	this.deviceAssignmentId = deviceAssignmentId;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getCustomerId()
     */
    @Override
    public UUID getCustomerId() {
	return customerId;
    }

    public void setCustomerId(UUID customerId) {
	this.customerId = customerId;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getAreaId()
     */
    @Override
    public UUID getAreaId() {
	return areaId;
    }

    public void setAreaId(UUID areaId) {
	this.areaId = areaId;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getAssetId()
     */
    @Override
    public UUID getAssetId() {
	return assetId;
    }

    public void setAssetId(UUID assetId) {
	this.assetId = assetId;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getLastInteractionDate()
     */
    @Override
    public Date getLastInteractionDate() {
	return lastInteractionDate;
    }

    public void setLastInteractionDate(Date lastInteractionDate) {
	this.lastInteractionDate = lastInteractionDate;
    }

    /*
     * @see com.sitewhere.spi.device.state.IDeviceState#getPresenceMissingDate()
     */
    @Override
    public Date getPresenceMissingDate() {
	return presenceMissingDate;
    }

    public void setPresenceMissingDate(Date presenceMissingDate) {
	this.presenceMissingDate = presenceMissingDate;
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

    public List<RdbRecentLocationEvent> getRecentLocations() {
	return recentLocations;
    }

    public void setRecentLocations(List<RdbRecentLocationEvent> recentLocations) {
	this.recentLocations = recentLocations;
    }

    public List<RdbRecentMeasurementEvent> getRecentMeasurements() {
	return recentMeasurements;
    }

    public void setRecentMeasurements(List<RdbRecentMeasurementEvent> recentMeasurements) {
	this.recentMeasurements = recentMeasurements;
    }

    public List<RdbRecentAlertEvent> getRecentAlerts() {
	return recentAlerts;
    }

    public void setRecentAlerts(List<RdbRecentAlertEvent> recentAlerts) {
	this.recentAlerts = recentAlerts;
    }

    public static void copy(IDeviceState source, RdbDeviceState target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getDeviceId() != null) {
	    target.setDeviceId(source.getDeviceId());
	}
	if (source.getDeviceTypeId() != null) {
	    target.setDeviceTypeId(source.getDeviceTypeId());
	}
	if (source.getDeviceAssignmentId() != null) {
	    target.setDeviceAssignmentId(source.getDeviceAssignmentId());
	}
	if (source.getCustomerId() != null) {
	    target.setCustomerId(source.getCustomerId());
	}
	if (source.getAreaId() != null) {
	    target.setAreaId(source.getAreaId());
	}
	if (source.getAssetId() != null) {
	    target.setAssetId(source.getAssetId());
	}
	if (source.getLastInteractionDate() != null) {
	    target.setLastInteractionDate(source.getLastInteractionDate());
	}
	if (source.getPresenceMissingDate() != null) {
	    target.setPresenceMissingDate(source.getPresenceMissingDate());
	}
	if (source.getMetadata() != null) {
	    target.setMetadata(source.getMetadata());
	}
    }
}
