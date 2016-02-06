/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.rest.model.device.event.request.scripting;

import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementsCreateRequest;
import com.sitewhere.rest.model.device.event.scripting.DeviceEventSupport;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventManagement;

/**
 * Exposes builders for creating SiteWhere events.
 * 
 * @author Derek
 */
public class DeviceEventRequestBuilder {

	/** Event management interface */
	private IDeviceEventManagement events;

	public DeviceEventRequestBuilder(IDeviceEventManagement events) {
		this.events = events;
	}

	public DeviceLocationCreateRequest.Builder newLocation(double latitude, double longitude) {
		return new DeviceLocationCreateRequest.Builder(latitude, longitude);
	}

	public DeviceMeasurementsCreateRequest.Builder newMeasurements() {
		return new DeviceMeasurementsCreateRequest.Builder();
	}

	public DeviceAlertCreateRequest.Builder newAlert(String type, String message) {
		return new DeviceAlertCreateRequest.Builder(type, message);
	}

	public AssignmentScope forSameAssignmentAs(DeviceEventSupport support) {
		return new AssignmentScope(events, support.data().getDeviceAssignmentToken());
	}

	public AssignmentScope forSameAssignmentAs(IDeviceEvent event) {
		return new AssignmentScope(events, event.getDeviceAssignmentToken());
	}

	public AssignmentScope forAssignment(String assignmentToken) {
		return new AssignmentScope(events, assignmentToken);
	}

	public static class AssignmentScope {

		/** Event management interface */
		private IDeviceEventManagement events;

		/** Assignment token */
		private String assignmentToken;

		public AssignmentScope(IDeviceEventManagement events, String assignmentToken) {
			this.events = events;
			this.assignmentToken = assignmentToken;
		}

		public AssignmentScope persist(DeviceLocationCreateRequest.Builder builder)
				throws SiteWhereException {
			DeviceLocationCreateRequest request = builder.build();
			events.addDeviceLocation(getAssignmentToken(), request);
			return this;
		}

		public AssignmentScope persist(DeviceMeasurementsCreateRequest.Builder builder)
				throws SiteWhereException {
			DeviceMeasurementsCreateRequest request = builder.build();
			events.addDeviceMeasurements(getAssignmentToken(), request);
			return this;
		}

		public AssignmentScope persist(DeviceAlertCreateRequest.Builder builder) throws SiteWhereException {
			DeviceAlertCreateRequest request = builder.build();
			events.addDeviceAlert(getAssignmentToken(), request);
			return this;
		}

		public String getAssignmentToken() {
			return assignmentToken;
		}

		public void setAssignmentToken(String assignmentToken) {
			this.assignmentToken = assignmentToken;
		}
	}
}