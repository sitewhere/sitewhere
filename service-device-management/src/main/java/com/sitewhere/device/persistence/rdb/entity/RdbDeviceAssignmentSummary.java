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
package com.sitewhere.device.persistence.rdb.entity;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sitewhere.rdb.entities.RdbPersistentEntity;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDeviceAssignmentSummary;

@Entity
@Table(name = "device_assignment_summary")
public class RdbDeviceAssignmentSummary extends RdbPersistentEntity implements IDeviceAssignmentSummary {

    /** Serial version UID */
    private static final long serialVersionUID = 623049598444832384L;

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "device_id", nullable = false)
    private UUID deviceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "device_id", insertable = false, updatable = false)
    private RdbDeviceSummary device;

    @Column(name = "device_token")
    private String deviceToken;

    @Column(name = "device_type_id", nullable = false)
    private UUID deviceTypeId;

    @Column(name = "device_type_name")
    private String deviceTypeName;

    @Column(name = "device_type_image_url")
    private String deviceTypeImageUrl;

    @Column(name = "customer_id", nullable = true)
    private UUID customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_image_url")
    private String customerImageUrl;

    @Column(name = "area_id", nullable = true)
    private UUID areaId;

    @Column(name = "area_name")
    private String areaName;

    @Column(name = "area_image_url")
    private String areaImageUrl;

    /** Id of assigned asset */
    @Column(name = "asset_id", nullable = true)
    private UUID assetId;

    @Transient
    private String assetName;

    @Transient
    private String assetImageUrl;

    @Transient
    private Map<String, String> metadata;

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
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getDeviceId()
     */
    @Override
    public UUID getDeviceId() {
	return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
	this.deviceId = deviceId;
    }

    public RdbDeviceSummary getDevice() {
	return device;
    }

    public void setDevice(RdbDeviceSummary device) {
	this.device = device;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getDeviceToken()
     */
    @Override
    public String getDeviceToken() {
	return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
	this.deviceToken = deviceToken;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getDeviceTypeId()
     */
    @Override
    public UUID getDeviceTypeId() {
	return deviceTypeId;
    }

    public void setDeviceTypeId(UUID deviceTypeId) {
	this.deviceTypeId = deviceTypeId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getDeviceTypeName()
     */
    @Override
    public String getDeviceTypeName() {
	return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
	this.deviceTypeName = deviceTypeName;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceAssignmentSummary#getDeviceTypeImageUrl()
     */
    @Override
    public String getDeviceTypeImageUrl() {
	return deviceTypeImageUrl;
    }

    public void setDeviceTypeImageUrl(String deviceTypeImageUrl) {
	this.deviceTypeImageUrl = deviceTypeImageUrl;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getCustomerId()
     */
    @Override
    public UUID getCustomerId() {
	return customerId;
    }

    public void setCustomerId(UUID customerId) {
	this.customerId = customerId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getCustomerName()
     */
    @Override
    public String getCustomerName() {
	return customerName;
    }

    public void setCustomerName(String customerName) {
	this.customerName = customerName;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getCustomerImageUrl()
     */
    @Override
    public String getCustomerImageUrl() {
	return customerImageUrl;
    }

    public void setCustomerImageUrl(String customerImageUrl) {
	this.customerImageUrl = customerImageUrl;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getAreaId()
     */
    @Override
    public UUID getAreaId() {
	return areaId;
    }

    public void setAreaId(UUID areaId) {
	this.areaId = areaId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getAreaName()
     */
    @Override
    public String getAreaName() {
	return areaName;
    }

    public void setAreaName(String areaName) {
	this.areaName = areaName;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getAreaImageUrl()
     */
    @Override
    public String getAreaImageUrl() {
	return areaImageUrl;
    }

    public void setAreaImageUrl(String areaImageUrl) {
	this.areaImageUrl = areaImageUrl;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getAssetId()
     */
    @Override
    public UUID getAssetId() {
	return assetId;
    }

    public void setAssetId(UUID assetId) {
	this.assetId = assetId;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getAssetName()
     */
    @Override
    public String getAssetName() {
	return assetName;
    }

    public void setAssetName(String assetName) {
	this.assetName = assetName;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getAssetImageUrl()
     */
    @Override
    public String getAssetImageUrl() {
	return assetImageUrl;
    }

    public void setAssetImageUrl(String assetImageUrl) {
	this.assetImageUrl = assetImageUrl;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getStatus()
     */
    @Override
    public DeviceAssignmentStatus getStatus() {
	return status;
    }

    public void setStatus(DeviceAssignmentStatus status) {
	this.status = status;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getActiveDate()
     */
    @Override
    public Date getActiveDate() {
	return activeDate;
    }

    public void setActiveDate(Date activeDate) {
	this.activeDate = activeDate;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceAssignmentSummary#getReleasedDate()
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
}
