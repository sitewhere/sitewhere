/*
 * DeviceAssignmentState.java 
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
import com.sitewhere.spi.device.IDeviceAssignmentState;
import com.sitewhere.spi.device.IDeviceLocation;
import com.sitewhere.spi.device.IDeviceMeasurement;

/**
 * Model object for device assignment state.
 * 
 * @author Derek
 */
public class DeviceAssignmentState implements IDeviceAssignmentState {

	/** Last location event */
	private DeviceLocation lastLocation;

	/** Last measurement event for each measurement id */
	private List<DeviceMeasurement> latestMeasurements = new ArrayList<DeviceMeasurement>();

	/** Last alert event for each alert type */
	private List<DeviceAlert> latestAlerts = new ArrayList<DeviceAlert>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceAssignmentState#getLastLocation()
	 */
	@Override
	public IDeviceLocation getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(DeviceLocation lastLocation) {
		this.lastLocation = lastLocation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceAssignmentState#getLatestMeasurements()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<IDeviceMeasurement> getLatestMeasurements() {
		return (List<IDeviceMeasurement>) (List<? extends IDeviceMeasurement>) latestMeasurements;
	}

	public void setLatestMeasurements(List<DeviceMeasurement> latestMeasurements) {
		this.latestMeasurements = latestMeasurements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceAssignmentState#getLatestAlerts()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<IDeviceAlert> getLatestAlerts() {
		return (List<IDeviceAlert>) (List<? extends IDeviceAlert>) latestAlerts;
	}

	public void setLatestAlerts(List<DeviceAlert> latestAlerts) {
		this.latestAlerts = latestAlerts;
	}

	public static DeviceAssignmentState copy(IDeviceAssignmentState source) {
		DeviceAssignmentState target = new DeviceAssignmentState();
		if (source.getLastLocation() != null) {
			target.setLastLocation(DeviceLocation.copy(source.getLastLocation()));
		}
		List<DeviceMeasurement> measurements = new ArrayList<DeviceMeasurement>();
		for (IDeviceMeasurement sm : source.getLatestMeasurements()) {
			measurements.add(DeviceMeasurement.copy(sm));
		}
		target.setLatestMeasurements(measurements);
		List<DeviceAlert> alerts = new ArrayList<DeviceAlert>();
		for (IDeviceAlert sa : source.getLatestAlerts()) {
			alerts.add(DeviceAlert.copy(sa));
		}
		target.setLatestAlerts(alerts);
		return target;
	}
}