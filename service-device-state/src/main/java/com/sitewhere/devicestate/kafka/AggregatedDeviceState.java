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
package com.sitewhere.devicestate.kafka;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;

/**
 * Used to aggregate device events into an entity that can be used to update the
 * master database after the event window has elapsed.
 */
public class AggregatedDeviceState {

    /** List of device locations for window */
    private List<DeviceLocation> deviceLocations = new ArrayList<>();

    /** List of device measurements for window */
    private List<DeviceMeasurement> deviceMeasurements = new ArrayList<>();

    /** List of device alerts for window */
    private List<DeviceAlert> deviceAlerts = new ArrayList<>();

    /**
     * Create a copy of the given device state.
     * 
     * @param original
     * @return
     */
    public static AggregatedDeviceState copy(AggregatedDeviceState original) {
	AggregatedDeviceState created = new AggregatedDeviceState();
	created.getDeviceLocations().addAll(original.getDeviceLocations());
	created.getDeviceMeasurements().addAll(original.getDeviceMeasurements());
	created.getDeviceAlerts().addAll(original.getDeviceAlerts());
	return created;
    }

    public void updateFromLocation(DeviceLocation location) {
	getDeviceLocations().add(location);
    }

    public void updateFromMeasurement(DeviceMeasurement measurement) {
	getDeviceMeasurements().add(measurement);
    }

    public void updateFromAlert(DeviceAlert alert) {
	getDeviceAlerts().add(alert);
    }

    public List<DeviceLocation> getDeviceLocations() {
	return deviceLocations;
    }

    public void setDeviceLocations(List<DeviceLocation> deviceLocations) {
	this.deviceLocations = deviceLocations;
    }

    public List<DeviceMeasurement> getDeviceMeasurements() {
	return deviceMeasurements;
    }

    public void setDeviceMeasurements(List<DeviceMeasurement> deviceMeasurements) {
	this.deviceMeasurements = deviceMeasurements;
    }

    public List<DeviceAlert> getDeviceAlerts() {
	return deviceAlerts;
    }

    public void setDeviceAlerts(List<DeviceAlert> deviceAlerts) {
	this.deviceAlerts = deviceAlerts;
    }

    /**
     * Default Kafka serializer implementation.
     */
    public static class DeviceStateSerializer implements Serializer<AggregatedDeviceState> {

	/*
	 * @see
	 * org.apache.kafka.common.serialization.Serializer#serialize(java.lang.String,
	 * java.lang.Object)
	 */
	@Override
	public byte[] serialize(String topic, AggregatedDeviceState data) {
	    return MarshalUtils.marshalJson(data);
	}
    }

    /**
     * Default Kafka deserializer implementation.
     */
    public static class DeviceStateDeserializer implements Deserializer<AggregatedDeviceState> {

	/*
	 * @see
	 * org.apache.kafka.common.serialization.Deserializer#deserialize(java.lang.
	 * String, byte[])
	 */
	@Override
	public AggregatedDeviceState deserialize(String topic, byte[] data) {
	    return MarshalUtils.unmarshalJson(data, AggregatedDeviceState.class);
	}
    }
}
