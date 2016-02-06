/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.scripting;

import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceMeasurements;

/**
 * Provides support for common operations on device events in scripting operations.
 * 
 * @author Derek
 */
public class DeviceEventSupport {

	/** Wrapped event */
	private IDeviceEvent event;

	public DeviceEventSupport(IDeviceEvent event) {
		this.event = event;
	}

	/**
	 * Indicates if event is a location.
	 * 
	 * @return
	 */
	public boolean isLocation() {
		return event.getEventType() == DeviceEventType.Location;
	}

	/**
	 * Indicates if event has a measurement with the given name.
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasMeasurement(String name) {
		return (getMeasurement(name) != null);
	}

	/**
	 * Attempts to get the named measurement if the event is a measurements event.
	 * 
	 * @param name
	 * @return
	 */
	public Double getMeasurement(String name) {
		if (event.getEventType() != DeviceEventType.Measurements) {
			return null;
		}
		IDeviceMeasurements mxs = (IDeviceMeasurements) event;
		Double mx = mxs.getMeasurement(name);
		if (mx == null) {
			return null;
		}
		return mx;
	}

	public IDeviceEvent data() {
		return event;
	}
}