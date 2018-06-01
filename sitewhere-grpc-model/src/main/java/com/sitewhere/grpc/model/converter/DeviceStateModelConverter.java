/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.converter;

import java.util.Map;

import com.sitewhere.grpc.model.CommonModel.GUUID;
import com.sitewhere.grpc.model.DeviceStateModel.GDeviceState;
import com.sitewhere.grpc.model.DeviceStateModel.GDeviceStateCreateRequest;
import com.sitewhere.rest.model.device.state.DeviceState;
import com.sitewhere.rest.model.device.state.request.DeviceStateCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;

/**
 * Convert device state entities between SiteWhere API model and GRPC model.
 * 
 * @author Derek
 */
public class DeviceStateModelConverter {

    /**
     * Convert device state create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceStateCreateRequest asApiDeviceStateCreateRequest(GDeviceStateCreateRequest grpc)
	    throws SiteWhereException {
	DeviceStateCreateRequest api = new DeviceStateCreateRequest();
	api.setDeviceId(CommonModelConverter.asApiUuid(grpc.getDeviceId()));
	api.setDeviceAssignmentId(CommonModelConverter.asApiUuid(grpc.getDeviceAssignmentId()));
	api.setLastInteractionDate(CommonModelConverter.asApiDate(grpc.getLastInteractionDate()));
	api.setPresenceMissingDate(CommonModelConverter.asApiDate(grpc.getPresenceMissingDate()));
	api.setLastLocationEventId(CommonModelConverter.asApiUuid(grpc.getLastLocationEventId()));

	Map<String, GUUID> lastMeasurementIds = grpc.getLastMeasurementEventIdsMap();
	for (String key : lastMeasurementIds.keySet()) {
	    api.getLastMeasurementEventIds().put(key, CommonModelConverter.asApiUuid(lastMeasurementIds.get(key)));
	}

	Map<String, GUUID> lastAlertIds = grpc.getLastAlertEventIdsMap();
	for (String key : lastAlertIds.keySet()) {
	    api.getLastAlertEventIds().put(key, CommonModelConverter.asApiUuid(lastAlertIds.get(key)));
	}

	return api;
    }

    /**
     * Convert device state create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStateCreateRequest asGrpcDeviceStateCreateRequest(IDeviceStateCreateRequest api)
	    throws SiteWhereException {
	GDeviceStateCreateRequest.Builder grpc = GDeviceStateCreateRequest.newBuilder();
	if (api.getDeviceId() != null) {
	    grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	}
	if (api.getDeviceAssignmentId() != null) {
	    grpc.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(api.getDeviceAssignmentId()));
	}
	if (api.getLastInteractionDate() != null) {
	    grpc.setLastInteractionDate(CommonModelConverter.asGrpcDate(api.getLastInteractionDate()));
	}
	if (api.getPresenceMissingDate() != null) {
	    grpc.setPresenceMissingDate(CommonModelConverter.asGrpcDate(api.getPresenceMissingDate()));
	}
	if (api.getLastLocationEventId() != null) {
	    grpc.setLastLocationEventId(CommonModelConverter.asGrpcUuid(api.getLastLocationEventId()));
	}
	if (api.getLastMeasurementEventIds() != null) {
	    for (String key : api.getLastMeasurementEventIds().keySet()) {
		grpc.putLastMeasurementEventIds(key,
			CommonModelConverter.asGrpcUuid(api.getLastMeasurementEventIds().get(key)));
	    }
	}
	if (api.getLastAlertEventIds() != null) {
	    for (String key : api.getLastAlertEventIds().keySet()) {
		grpc.putLastAlertEventIds(key, CommonModelConverter.asGrpcUuid(api.getLastAlertEventIds().get(key)));
	    }
	}
	return grpc.build();
    }

    /**
     * Convert device state from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceState asApiDeviceState(GDeviceState grpc) throws SiteWhereException {
	DeviceState api = new DeviceState();
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setDeviceId(CommonModelConverter.asApiUuid(grpc.getDeviceId()));
	api.setDeviceAssignmentId(CommonModelConverter.asApiUuid(grpc.getDeviceAssignmentId()));
	api.setLastInteractionDate(CommonModelConverter.asApiDate(grpc.getLastInteractionDate()));
	api.setPresenceMissingDate(CommonModelConverter.asApiDate(grpc.getPresenceMissingDate()));
	api.setLastLocationEventId(CommonModelConverter.asApiUuid(grpc.getLastLocationEventId()));

	Map<String, GUUID> lastMeasurementIds = grpc.getLastMeasurementEventIdsMap();
	for (String key : lastMeasurementIds.keySet()) {
	    api.getLastMeasurementEventIds().put(key, CommonModelConverter.asApiUuid(lastMeasurementIds.get(key)));
	}

	Map<String, GUUID> lastAlertIds = grpc.getLastAlertEventIdsMap();
	for (String key : lastAlertIds.keySet()) {
	    api.getLastAlertEventIds().put(key, CommonModelConverter.asApiUuid(lastAlertIds.get(key)));
	}

	return api;
    }

    /**
     * Convert device state from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceState asGrpcDeviceState(IDeviceState api) throws SiteWhereException {
	GDeviceState.Builder grpc = GDeviceState.newBuilder();
	if (api.getId() != null) {
	    grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	}
	if (api.getDeviceId() != null) {
	    grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	}
	if (api.getDeviceAssignmentId() != null) {
	    grpc.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(api.getDeviceAssignmentId()));
	}
	if (api.getLastInteractionDate() != null) {
	    grpc.setLastInteractionDate(CommonModelConverter.asGrpcDate(api.getLastInteractionDate()));
	}
	if (api.getPresenceMissingDate() != null) {
	    grpc.setPresenceMissingDate(CommonModelConverter.asGrpcDate(api.getPresenceMissingDate()));
	}
	if (api.getLastLocationEventId() != null) {
	    grpc.setLastLocationEventId(CommonModelConverter.asGrpcUuid(api.getLastLocationEventId()));
	}
	if (api.getLastMeasurementEventIds() != null) {
	    for (String key : api.getLastMeasurementEventIds().keySet()) {
		grpc.putLastMeasurementEventIds(key,
			CommonModelConverter.asGrpcUuid(api.getLastMeasurementEventIds().get(key)));
	    }
	}
	if (api.getLastAlertEventIds() != null) {
	    for (String key : api.getLastAlertEventIds().keySet()) {
		grpc.putLastAlertEventIds(key, CommonModelConverter.asGrpcUuid(api.getLastAlertEventIds().get(key)));
	    }
	}
	return grpc.build();
    }
}
