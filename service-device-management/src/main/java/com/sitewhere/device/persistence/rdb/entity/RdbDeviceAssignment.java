/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.persistence.rdb.entity;

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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.sitewhere.rdb.entities.RdbPersistentEntity;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignment;

@Entity
@Table(name = "device_assignment")
@NamedQueries({
	@NamedQuery(name = Queries.QUERY_DEVICE_ASSIGNMENT_BY_TOKEN, query = "SELECT a FROM RdbDeviceAssignment a WHERE a.token = :token"),
	@NamedQuery(name = Queries.QUERY_DEVICE_ASSIGNMENT_BY_DEVICE_AND_STATUS, query = "SELECT a FROM RdbDeviceAssignment a WHERE a.deviceId = :deviceId AND a.status = :status") })
public class RdbDeviceAssignment extends RdbPersistentEntity implements IDeviceAssignment {

    /** Serial version UID */
    private static final long serialVersionUID = -862944333442234495L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    /** Device id */
    @Column(name = "device_id")
    private UUID deviceId;

    /** Device type id */
    @Column(name = "device_type_id")
    private UUID deviceTypeId;

    /** Id of assigned customer */
    @Column(name = "customer_id")
    private UUID customerId;

    /** Id of assigned area */
    @Column(name = "area_id")
    private UUID areaId;

    /** Id of assigned asset */
    @Column(name = "asset_id")
    private UUID assetId;

    /** Assignment status */
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DeviceAssignmentStatus status;

    /** Assignment start date */
    @Column(name = "active_date")
    private Date activeDate;

    /** Assignment end date */
    @Column(name = "released_date")
    private Date releasedDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @CollectionTable(name = "device_assignment_metadata", joinColumns = @JoinColumn(name = "device_assignment_id"))
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
     * @see com.sitewhere.spi.device.IDeviceAssignment#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignment#getDeviceTypeId()
     */
    @Override
    public UUID getDeviceTypeId() {
	return deviceTypeId;
    }

    public void setDeviceTypeId(UUID deviceTypeId) {
	this.deviceTypeId = deviceTypeId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignment#getCustomerId()
     */
    @Override
    public UUID getCustomerId() {
	return customerId;
    }

    public void setCustomerId(UUID customerId) {
	this.customerId = customerId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignment#getAreaId()
     */
    @Override
    public UUID getAreaId() {
	return areaId;
    }

    public void setAreaId(UUID areaId) {
	this.areaId = areaId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignment#getAssetId()
     */
    @Override
    public UUID getAssetId() {
	return assetId;
    }

    public void setAssetId(UUID assetId) {
	this.assetId = assetId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignment#getStatus()
     */
    @Override
    public DeviceAssignmentStatus getStatus() {
	return status;
    }

    public void setStatus(DeviceAssignmentStatus status) {
	this.status = status;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignment#getActiveDate()
     */
    @Override
    public Date getActiveDate() {
	return activeDate;
    }

    public void setActiveDate(Date activeDate) {
	this.activeDate = activeDate;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignment#getReleasedDate()
     */
    @Override
    public Date getReleasedDate() {
	return releasedDate;
    }

    public void setReleasedDate(Date releasedDate) {
	this.releasedDate = releasedDate;
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

    public static void copy(IDeviceAssignment source, RdbDeviceAssignment target) {
	if (source.getId() != null) {
	    target.setId(source.getId());
	}
	if (source.getDeviceId() != null) {
	    target.setDeviceId(source.getDeviceId());
	}
	if (source.getDeviceTypeId() != null) {
	    target.setDeviceTypeId(source.getDeviceTypeId());
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
	if (source.getStatus() != null) {
	    target.setStatus(source.getStatus());
	}
	if (source.getActiveDate() != null) {
	    target.setActiveDate(source.getActiveDate());
	}
	if (source.getReleasedDate() != null) {
	    target.setReleasedDate(source.getReleasedDate());
	}
	if (source.getMetadata() != null) {
	    target.setMetadata(source.getMetadata());
	}
	RdbPersistentEntity.copy(source, target);
    }
}
