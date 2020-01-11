/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.persistence;

import com.sitewhere.microservice.persistence.Persistence;
import com.sitewhere.rest.model.device.state.DeviceState;
import com.sitewhere.rest.model.device.state.RecentStateEvent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;
import com.sitewhere.spi.device.state.request.IRecentStateEventCreateRequest;

/**
 * Common methods needed by device state management implementations.
 */
public class DeviceStatePersistence extends Persistence {

    /**
     * Common logic for creating new device state object and populating it from
     * request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static DeviceState deviceStateCreateLogic(IDeviceStateCreateRequest request) throws SiteWhereException {
	DeviceState state = new DeviceState();

	requireNotNull("Device id", request.getDeviceId());
	state.setDeviceId(request.getDeviceId());

	requireNotNull("Device type id", request.getDeviceTypeId());
	state.setDeviceTypeId(request.getDeviceTypeId());

	requireNotNull("Device assignment id", request.getDeviceAssignmentId());
	state.setDeviceAssignmentId(request.getDeviceAssignmentId());

	state.setCustomerId(request.getCustomerId());
	state.setAreaId(request.getAreaId());
	state.setAssetId(request.getAssetId());
	state.setLastInteractionDate(request.getLastInteractionDate());
	state.setPresenceMissingDate(request.getPresenceMissingDate());
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
	target.setLastInteractionDate(request.getLastInteractionDate());
	target.setPresenceMissingDate(request.getPresenceMissingDate());
	if (request.getDeviceTypeId() != null) {
	    target.setDeviceTypeId(request.getDeviceTypeId());
	}
	if (request.getCustomerId() != null) {
	    target.setCustomerId(request.getCustomerId());
	}
	if (request.getAreaId() != null) {
	    target.setAreaId(request.getAreaId());
	}
	if (request.getAssetId() != null) {
	    target.setAssetId(request.getAssetId());
	}
    }

    /**
     * Common logic for creating new recent state event object and populating it
     * from request.
     * 
     * @param request
     * @return
     * @throws SiteWhereException
     */
    public static RecentStateEvent recentStateEventCreateLogic(IRecentStateEventCreateRequest request)
	    throws SiteWhereException {
	RecentStateEvent recent = new RecentStateEvent();

	requireNotNull("Device state id", request.getDeviceStateId());
	recent.setDeviceStateId(request.getDeviceStateId());

	requireNotNull("Event type", request.getEventType());
	recent.setEventType(request.getEventType());

	recent.setClassifier(request.getClassifier());
	recent.setValue(request.getValue());
	recent.setEventDate(request.getEventDate());
	return recent;
    }

    /**
     * Common logic for updating an existing recent state event object.
     * 
     * @param request
     * @param target
     * @throws SiteWhereException
     */
    public static void recentStateEventUpdateLogic(IRecentStateEventCreateRequest request, RecentStateEvent target)
	    throws SiteWhereException {
	if (request.getValue() != null) {
	    target.setValue(request.getValue());
	}
	if (request.getEventId() != null) {
	    target.setEventId(request.getEventId());
	}
	if (request.getEventDate() != null) {
	    target.setEventDate(request.getEventDate());
	}
    }
}
