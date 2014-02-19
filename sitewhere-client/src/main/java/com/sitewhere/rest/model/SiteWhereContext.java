/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) Reveal Technologies, LLC. All rights reserved. http://www.reveal-tech.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package com.sitewhere.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.spi.ISiteWhereContext;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;

/**
 * Default context implementation.
 * 
 * @author dadams
 */
public class SiteWhereContext implements ISiteWhereContext {

	/** Current assignment for device */
	private IDeviceAssignment deviceAssignment;

	/** Measurements that have not been persisted */
	private List<IDeviceMeasurementsCreateRequest> unsavedDeviceMeasurements =
			new ArrayList<IDeviceMeasurementsCreateRequest>();

	/** Locations that have not been persisted */
	private List<IDeviceLocationCreateRequest> unsavedDeviceLocations =
			new ArrayList<IDeviceLocationCreateRequest>();

	/** Alerts that have not been persisted */
	private List<IDeviceAlertCreateRequest> unsavedDeviceAlerts = new ArrayList<IDeviceAlertCreateRequest>();

	/** Measurements that have been persisted */
	private List<IDeviceMeasurements> deviceMeasurements = new ArrayList<IDeviceMeasurements>();

	/** Locations that have been persisted */
	private List<IDeviceLocation> deviceLocations = new ArrayList<IDeviceLocation>();

	/** Alerts that have been persisted */
	private List<IDeviceAlert> deviceAlerts = new ArrayList<IDeviceAlert>();

	/** Command invocations that have been persisted */
	private List<IDeviceCommandInvocation> deviceCommandInvocations =
			new ArrayList<IDeviceCommandInvocation>();

	/** Information for replying to originator */
	private String replyTo;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereContext#getDeviceAssignment()
	 */
	public IDeviceAssignment getDeviceAssignment() {
		return deviceAssignment;
	}

	public void setDeviceAssignment(IDeviceAssignment deviceAssignment) {
		this.deviceAssignment = deviceAssignment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereContext#getUnsavedDeviceMeasurements()
	 */
	public List<IDeviceMeasurementsCreateRequest> getUnsavedDeviceMeasurements() {
		return unsavedDeviceMeasurements;
	}

	public void setUnsavedDeviceMeasurements(List<IDeviceMeasurementsCreateRequest> unsavedDeviceMeasurements) {
		this.unsavedDeviceMeasurements = unsavedDeviceMeasurements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereContext#getUnsavedDeviceLocations()
	 */
	public List<IDeviceLocationCreateRequest> getUnsavedDeviceLocations() {
		return unsavedDeviceLocations;
	}

	public void setUnsavedDeviceLocations(List<IDeviceLocationCreateRequest> unsavedDeviceLocations) {
		this.unsavedDeviceLocations = unsavedDeviceLocations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereContext#getUnsavedDeviceAlerts()
	 */
	public List<IDeviceAlertCreateRequest> getUnsavedDeviceAlerts() {
		return unsavedDeviceAlerts;
	}

	public void setUnsavedDeviceAlerts(List<IDeviceAlertCreateRequest> unsavedDeviceAlerts) {
		this.unsavedDeviceAlerts = unsavedDeviceAlerts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereContext#getDeviceMeasurements()
	 */
	public List<IDeviceMeasurements> getDeviceMeasurements() {
		return deviceMeasurements;
	}

	public void setDeviceMeasurements(List<IDeviceMeasurements> deviceMeasurements) {
		this.deviceMeasurements = deviceMeasurements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereContext#getDeviceLocations()
	 */
	public List<IDeviceLocation> getDeviceLocations() {
		return deviceLocations;
	}

	public void setDeviceLocations(List<IDeviceLocation> deviceLocations) {
		this.deviceLocations = deviceLocations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereContext#getDeviceAlerts()
	 */
	public List<IDeviceAlert> getDeviceAlerts() {
		return deviceAlerts;
	}

	public void setDeviceAlerts(List<IDeviceAlert> deviceAlerts) {
		this.deviceAlerts = deviceAlerts;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereContext#getDeviceCommandInvocations()
	 */
	public List<IDeviceCommandInvocation> getDeviceCommandInvocations() {
		return deviceCommandInvocations;
	}

	public void setDeviceCommandInvocations(List<IDeviceCommandInvocation> deviceCommandInvocations) {
		this.deviceCommandInvocations = deviceCommandInvocations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sitewhere.spi.ISiteWhereContext#getReplyTo()
	 */
	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
}