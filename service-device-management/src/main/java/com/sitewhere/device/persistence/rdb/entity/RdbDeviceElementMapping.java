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

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.sitewhere.spi.device.IDeviceElementMapping;

@Entity
@Table(name = "device_element_mapping")
public class RdbDeviceElementMapping implements IDeviceElementMapping {

    /** Serial version UID */
    private static final long serialVersionUID = -7213699772411424925L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @Column(name = "device_element_schema_path")
    private String deviceElementSchemaPath;

    @Column(name = "device_token")
    private String deviceToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", nullable = false)
    RdbDevice device;

    public RdbDeviceElementMapping() {
    }

    public RdbDeviceElementMapping(String deviceElementSchemaPath, String deviceToken) {
	this.deviceElementSchemaPath = deviceElementSchemaPath;
	this.deviceToken = deviceToken;
    }

    public UUID getId() {
	return id;
    }

    public void setId(UUID id) {
	this.id = id;
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceElementMapping#getDeviceElementSchemaPath()
     */
    @Override
    public String getDeviceElementSchemaPath() {
	return deviceElementSchemaPath;
    }

    public void setDeviceElementSchemaPath(String deviceElementSchemaPath) {
	this.deviceElementSchemaPath = deviceElementSchemaPath;
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceElementMapping#getDeviceToken()
     */
    @Override
    public String getDeviceToken() {
	return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
	this.deviceToken = deviceToken;
    }

    public RdbDevice getDevice() {
	return device;
    }

    public void setDevice(RdbDevice device) {
	this.device = device;
    }
}
