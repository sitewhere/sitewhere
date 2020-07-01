/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import com.sitewhere.microservice.util.MarshalUtils;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;

/**
 * Used to aggregate device events into an entity that can be used to update the
 * master database after the event window has elapsed.
 */
public class AggregatedDeviceState {

    /** Most recent location measurement for window */
    private DeviceLocation latestLocation;

    /** Latest measurements for window, indexed by name */
    private Map<String, DeviceMeasurement> latestMeasurementsByName = new HashMap<>();

    /**
     * Create a copy of the given device state.
     * 
     * @param original
     * @return
     */
    public static AggregatedDeviceState copy(AggregatedDeviceState original) {
	AggregatedDeviceState created = new AggregatedDeviceState();
	created.updateFromLocation(original.getLatestLocation());
	created.getLatestMeasurementsByName().putAll(original.getLatestMeasurementsByName());
	return created;
    }

    public DeviceLocation getLatestLocation() {
	return latestLocation;
    }

    public void updateFromLocation(DeviceLocation latestLocation) {
	this.latestLocation = latestLocation;
    }

    public Map<String, DeviceMeasurement> getLatestMeasurementsByName() {
	return latestMeasurementsByName;
    }

    public void updateFromMeasurement(DeviceMeasurement measurement) {
	this.latestMeasurementsByName.put(measurement.getName(), measurement);
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
