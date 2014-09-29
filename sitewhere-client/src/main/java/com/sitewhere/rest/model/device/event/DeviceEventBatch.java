/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * A batch of new events to create for a given device.
 * 
 * @author Derek
 */
public class DeviceEventBatch implements IDeviceEventBatch {

	/** Device hardware id */
	private String hardwareId;

	/** Contains information about sending responses */
	private String replyTo;

	/** List of measurements requests */
	private List<DeviceMeasurementsCreateRequest> measurements = new ArrayList<DeviceMeasurementsCreateRequest>();

	/** List of location requests */
	private List<DeviceLocationCreateRequest> locations = new ArrayList<DeviceLocationCreateRequest>();

	/** List of alert requests */
	private List<DeviceAlertCreateRequest> alerts = new ArrayList<DeviceAlertCreateRequest>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEventBatch#getHardwareId()
	 */
	public String getHardwareId() {
		return hardwareId;
	}

	public void setHardwareId(String hardwareId) {
		this.hardwareId = hardwareId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEventBatch#getReplyTo()
	 */
	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEventBatch#getMeasurements()
	 */
	@SuppressWarnings("unchecked")
	public List<IDeviceMeasurementsCreateRequest> getMeasurements() {
		return (List<IDeviceMeasurementsCreateRequest>) (List<? extends IDeviceMeasurementsCreateRequest>) measurements;
	}

	public void setMeasurements(List<DeviceMeasurementsCreateRequest> measurements) {
		this.measurements = measurements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEventBatch#getLocations()
	 */
	@SuppressWarnings("unchecked")
	public List<IDeviceLocationCreateRequest> getLocations() {
		return (List<IDeviceLocationCreateRequest>) (List<? extends IDeviceLocationCreateRequest>) locations;
	}

	public void setLocations(List<DeviceLocationCreateRequest> locations) {
		this.locations = locations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.device.IDeviceEventBatch#getAlerts()
	 */
	@SuppressWarnings("unchecked")
	public List<IDeviceAlertCreateRequest> getAlerts() {
		return (List<IDeviceAlertCreateRequest>) (List<? extends IDeviceAlertCreateRequest>) alerts;
	}

	public void setAlerts(List<DeviceAlertCreateRequest> alerts) {
		this.alerts = alerts;
	}
}