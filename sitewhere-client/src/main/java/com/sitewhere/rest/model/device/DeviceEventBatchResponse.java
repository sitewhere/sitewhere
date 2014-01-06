/*
 * DeviceEventBatchResponse.java 
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.spi.device.IDeviceAlert;
import com.sitewhere.spi.device.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.IDeviceLocation;
import com.sitewhere.spi.device.IDeviceMeasurements;

/**
 * Response from device event batch create operation.
 * 
 * @author Derek
 */
public class DeviceEventBatchResponse implements IDeviceEventBatchResponse {

	/** List of measurements that were created */
	private List<DeviceMeasurements> createdMeasurements = new ArrayList<DeviceMeasurements>();

	/** List of locations that were created */
	private List<DeviceLocation> createdLocations = new ArrayList<DeviceLocation>();

	/** List of alerts that were created */
	private List<DeviceAlert> createdAlerts = new ArrayList<DeviceAlert>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEventBatchResponse#getCreatedMeasurements()
	 */
	@SuppressWarnings("unchecked")
	public List<IDeviceMeasurements> getCreatedMeasurements() {
		return (List<IDeviceMeasurements>) (List<? extends IDeviceMeasurements>) createdMeasurements;
	}

	public void setCreatedMeasurements(List<DeviceMeasurements> createdMeasurements) {
		this.createdMeasurements = createdMeasurements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEventBatchResponse#getCreatedLocations()
	 */
	@SuppressWarnings("unchecked")
	public List<IDeviceLocation> getCreatedLocations() {
		return (List<IDeviceLocation>) (List<? extends IDeviceLocation>) createdLocations;
	}

	public void setCreatedLocations(List<DeviceLocation> createdLocations) {
		this.createdLocations = createdLocations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEventBatchResponse#getCreatedAlerts()
	 */
	@SuppressWarnings("unchecked")
	public List<IDeviceAlert> getCreatedAlerts() {
		return (List<IDeviceAlert>) (List<? extends IDeviceAlert>) createdAlerts;
	}

	public void setCreatedAlerts(List<DeviceAlert> createdAlerts) {
		this.createdAlerts = createdAlerts;
	}
}