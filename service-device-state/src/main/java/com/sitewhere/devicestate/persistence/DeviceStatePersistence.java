/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.persistence;

import java.util.UUID;

import com.sitewhere.rest.model.device.state.DeviceState;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;

/**
 * Common methods needed by device state management implementations.
 * 
 * @author Derek
 */
public class DeviceStatePersistence {

    /**
     * Common logic for creating new device state object and populating it from
     * request.
     * 
     * @param request
     * @param device
     * @param deviceAssignment
     * @return
     * @throws SiteWhereException
     */
    public static DeviceState deviceStateCreateLogic(IDeviceStateCreateRequest request, IDevice device,
	    IDeviceAssignment deviceAssignment) throws SiteWhereException {
	DeviceState state = new DeviceState();
	state.setId(UUID.randomUUID());
	state.setDeviceId(device.getId());
	state.setDeviceAssignmentId(deviceAssignment.getId());
	state.setLastInteractionDate(request.getLastInteractionDate());
	state.setPresenceMissingDate(request.getPresenceMissingDate());
	state.setLastLocationEventId(request.getLastLocationEventId());
	state.setLastMeasurementEventIds(request.getLastMeasurementEventIds());
	state.setLastAlertEventIds(request.getLastAlertEventIds());
	return state;
    }

    /**
     * Common logic for updating an existing device state object.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void deviceStateUpdateLogic(IDeviceStateCreateRequest request, DeviceState target)
	    throws SiteWhereException {
	if (request.getLastInteractionDate() != null) {
	    target.setLastInteractionDate(request.getLastInteractionDate());
	}
	if (request.getPresenceMissingDate() != null) {
	    target.setPresenceMissingDate(request.getPresenceMissingDate());
	}
	if (request.getLastLocationEventId() != null) {
	    target.setLastLocationEventId(request.getLastLocationEventId());
	}
	if (request.getLastMeasurementEventIds() != null) {
	    target.setLastMeasurementEventIds(request.getLastMeasurementEventIds());
	}
	if (request.getLastAlertEventIds() != null) {
	    target.setLastAlertEventIds(request.getLastAlertEventIds());
	}
    }
}
