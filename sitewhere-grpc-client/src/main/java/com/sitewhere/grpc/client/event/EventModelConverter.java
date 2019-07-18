/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.event;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.model.CommonModel.GDeviceCommandInitiator;
import com.sitewhere.grpc.model.CommonModel.GDeviceCommandStatus;
import com.sitewhere.grpc.model.CommonModel.GDeviceCommandTarget;
import com.sitewhere.grpc.model.CommonModel.GOptionalBoolean;
import com.sitewhere.grpc.model.CommonModel.GOptionalDouble;
import com.sitewhere.grpc.model.CommonModel.GOptionalString;
import com.sitewhere.grpc.model.DeviceEventModel.GAlertLevel;
import com.sitewhere.grpc.model.DeviceEventModel.GAlertSource;
import com.sitewhere.grpc.model.DeviceEventModel.GAnyDeviceEvent;
import com.sitewhere.grpc.model.DeviceEventModel.GAnyDeviceEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDecodedEventPayload;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAlert;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAlertCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAlertSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAssignmentEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandInvocation;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandInvocationCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandInvocationSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandResponse;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandResponseCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandResponseSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEvent;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEventBatchCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEventBatchResponse;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEventContext;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEventIndex;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEventSearchCriteria;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEventSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEventType;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceLocation;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceLocationCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceLocationSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceMeasurement;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceMeasurementCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceMeasurementSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceStateChange;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceStateChangeCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceStateChangeSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GEnrichedEventPayload;
import com.sitewhere.grpc.model.DeviceEventModel.GPreprocessedEventPayload;
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceEvent;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.rest.model.device.event.DeviceEventBatchResponse;
import com.sitewhere.rest.model.device.event.DeviceEventContext;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
import com.sitewhere.rest.model.device.event.kafka.DecodedEventPayload;
import com.sitewhere.rest.model.device.event.kafka.EnrichedEventPayload;
import com.sitewhere.rest.model.device.event.kafka.PreprocessedEventPayload;
import com.sitewhere.rest.model.device.event.request.DeviceAlertCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceAssignmentEventCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandInvocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceCommandResponseCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceEventCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceLocationCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceMeasurementCreateRequest;
import com.sitewhere.rest.model.device.event.request.DeviceStateChangeCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.AlertLevel;
import com.sitewhere.spi.device.event.AlertSource;
import com.sitewhere.spi.device.event.CommandInitiator;
import com.sitewhere.spi.device.event.CommandStatus;
import com.sitewhere.spi.device.event.CommandTarget;
import com.sitewhere.spi.device.event.DeviceEventIndex;
import com.sitewhere.spi.device.event.DeviceEventType;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventContext;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.kafka.IDecodedEventPayload;
import com.sitewhere.spi.device.event.kafka.IEnrichedEventPayload;
import com.sitewhere.spi.device.event.kafka.IPreprocessedEventPayload;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceAssignmentEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Convert device event entities between SiteWhere API model and GRPC model.
 * 
 * @author Derek
 */
public class EventModelConverter {

    /**
     * Copy common device event create request fields from GRPC to API.
     * 
     * @param grpc
     * @param api
     * @throws SiteWhereException
     */
    public static void copyApiDeviceEventCreateRequest(GDeviceEventCreateRequest grpc, DeviceEventCreateRequest api)
	    throws SiteWhereException {
	api.setAlternateId(grpc.hasAlternateId() ? grpc.getAlternateId().getValue() : null);
	api.setEventDate(CommonModelConverter.asApiDate(grpc.getEventDate()));
	api.setUpdateState(grpc.hasUpdateState() ? grpc.getUpdateState().getValue() : false);
	api.setMetadata(grpc.getMetadataMap());
    }

    /**
     * Convert generic device event create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceEventCreateRequest asApiDeviceEventCreateRequest(GDeviceEventCreateRequest grpc)
	    throws SiteWhereException {
	DeviceEventCreateRequest api = new DeviceEventCreateRequest();
	EventModelConverter.copyApiDeviceEventCreateRequest(grpc, api);
	return api;
    }

    /**
     * Create GRPC device event create request common base values.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceEventCreateRequest createGrpcDeviceEventCreateRequest(IDeviceEventCreateRequest api)
	    throws SiteWhereException {
	GDeviceEventCreateRequest.Builder grpc = GDeviceEventCreateRequest.newBuilder();
	if (api.getAlternateId() != null) {
	    grpc.setAlternateId(GOptionalString.newBuilder().setValue(api.getAlternateId()).build());
	}
	grpc.setEventDate(CommonModelConverter.asGrpcDate(api.getEventDate()));
	if (api.isUpdateState()) {
	    grpc.setUpdateState(GOptionalBoolean.newBuilder().setValue(true).build());
	}
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert device event search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceEventSearchCriteria asGrpcDeviceEventSearchCriteria(IDateRangeSearchCriteria api)
	    throws SiteWhereException {
	GDeviceEventSearchCriteria.Builder grpc = GDeviceEventSearchCriteria.newBuilder();
	grpc.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(api));
	return grpc.build();
    }

    /**
     * Convert device event index from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceEventIndex asApiDeviceEventIndex(GDeviceEventIndex grpc) throws SiteWhereException {
	switch (grpc) {
	case EVENT_INDEX_AREA:
	    return DeviceEventIndex.Area;
	case EVENT_INDEX_ASSET:
	    return DeviceEventIndex.Asset;
	case EVENT_INDEX_ASSIGNMENT:
	    return DeviceEventIndex.Assignment;
	case EVENT_INDEX_CUSTOMER:
	    return DeviceEventIndex.Customer;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown event index: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert device event index from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceEventIndex asGrpcDeviceEventIndex(DeviceEventIndex api) throws SiteWhereException {
	switch (api) {
	case Area:
	    return GDeviceEventIndex.EVENT_INDEX_AREA;
	case Asset:
	    return GDeviceEventIndex.EVENT_INDEX_ASSET;
	case Assignment:
	    return GDeviceEventIndex.EVENT_INDEX_ASSIGNMENT;
	case Customer:
	    return GDeviceEventIndex.EVENT_INDEX_CUSTOMER;
	}
	throw new SiteWhereException("Unknown event index: " + api.name());
    }

    /**
     * Convert event type from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceEventType asApiDeviceEventType(GDeviceEventType grpc) throws SiteWhereException {
	switch (grpc) {
	case EVENT_TYPE_ALERT:
	    return DeviceEventType.Alert;
	case EVENT_TYPE_COMMAND_INVOCATION:
	    return DeviceEventType.CommandInvocation;
	case EVENT_TYPE_COMMAND_RESPONSE:
	    return DeviceEventType.CommandResponse;
	case EVENT_TYPE_LOCATION:
	    return DeviceEventType.Location;
	case EVENT_TYPE_MEASUREMENT:
	    return DeviceEventType.Measurement;
	case EVENT_TYPE_STATE_CHANGE:
	    return DeviceEventType.StateChange;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown event type: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert event type from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceEventType asGrpcDeviceEventType(DeviceEventType api) throws SiteWhereException {
	switch (api) {
	case Alert:
	    return GDeviceEventType.EVENT_TYPE_ALERT;
	case CommandInvocation:
	    return GDeviceEventType.EVENT_TYPE_COMMAND_INVOCATION;
	case CommandResponse:
	    return GDeviceEventType.EVENT_TYPE_COMMAND_RESPONSE;
	case Location:
	    return GDeviceEventType.EVENT_TYPE_LOCATION;
	case Measurement:
	    return GDeviceEventType.EVENT_TYPE_MEASUREMENT;
	case StateChange:
	    return GDeviceEventType.EVENT_TYPE_STATE_CHANGE;
	}
	throw new SiteWhereException("Unknown event type: " + api.name());
    }

    /**
     * Copy common device event fields from GRPC to API.
     * 
     * @param grpc
     * @param api
     * @throws SiteWhereException
     */
    public static void copyApiDeviceEvent(GDeviceEvent grpc, DeviceEvent api) throws SiteWhereException {
	api.setId(CommonModelConverter.asApiUuid(grpc.getId()));
	api.setAlternateId(grpc.hasAlternateId() ? grpc.getAlternateId().getValue() : null);
	api.setEventType(EventModelConverter.asApiDeviceEventType(grpc.getEventType()));
	api.setDeviceId(CommonModelConverter.asApiUuid(grpc.getDeviceId()));
	api.setDeviceAssignmentId(CommonModelConverter.asApiUuid(grpc.getDeviceAssignmentId()));
	api.setCustomerId(CommonModelConverter.asApiUuid(grpc.getCustomerId()));
	api.setAreaId(CommonModelConverter.asApiUuid(grpc.getAreaId()));
	api.setAssetId(CommonModelConverter.asApiUuid(grpc.getAssetId()));
	api.setEventDate(CommonModelConverter.asApiDate(grpc.getEventDate()));
	api.setReceivedDate(CommonModelConverter.asApiDate(grpc.getReceivedDate()));
	api.setMetadata(grpc.getMetadataMap());
    }

    /**
     * Create GRPC device event common base values.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceEvent createGrpcDeviceEvent(IDeviceEvent api) throws SiteWhereException {
	GDeviceEvent.Builder grpc = GDeviceEvent.newBuilder();
	grpc.setId(CommonModelConverter.asGrpcUuid(api.getId()));
	if (api.getAlternateId() != null) {
	    grpc.setAlternateId(GOptionalString.newBuilder().setValue(api.getAlternateId()).build());
	}
	grpc.setEventType(EventModelConverter.asGrpcDeviceEventType(api.getEventType()));
	grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	grpc.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(api.getDeviceAssignmentId()));
	if (api.getCustomerId() != null) {
	    grpc.setCustomerId(CommonModelConverter.asGrpcUuid(api.getCustomerId()));
	}
	if (api.getAreaId() != null) {
	    grpc.setAreaId(CommonModelConverter.asGrpcUuid(api.getAreaId()));
	}
	if (api.getAssetId() != null) {
	    grpc.setAssetId(CommonModelConverter.asGrpcUuid(api.getAssetId()));
	}
	grpc.setEventDate(CommonModelConverter.asGrpcDate(api.getEventDate()));
	grpc.setReceivedDate(CommonModelConverter.asGrpcDate(api.getReceivedDate()));
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert device measurement create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceMeasurementCreateRequest asApiDeviceMeasurementCreateRequest(
	    GDeviceMeasurementCreateRequest grpc) throws SiteWhereException {
	DeviceMeasurementCreateRequest api = new DeviceMeasurementCreateRequest();
	api.setName(grpc.getName());
	api.setValue(grpc.getValue());
	EventModelConverter.copyApiDeviceEventCreateRequest(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device measurement create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceMeasurementCreateRequest asGrpcDeviceMeasurementCreateRequest(
	    IDeviceMeasurementCreateRequest api) throws SiteWhereException {
	GDeviceMeasurementCreateRequest.Builder grpc = GDeviceMeasurementCreateRequest.newBuilder();
	grpc.setName(api.getName());
	grpc.setValue(api.getValue());
	grpc.setEvent(EventModelConverter.createGrpcDeviceEventCreateRequest(api));
	return grpc.build();
    }

    /**
     * Convert device measurement create requests from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceMeasurementCreateRequest> asApiDeviceMeasurementCreateRequests(
	    List<GDeviceMeasurementCreateRequest> grpcs) throws SiteWhereException {
	List<DeviceMeasurementCreateRequest> api = new ArrayList<DeviceMeasurementCreateRequest>();
	for (GDeviceMeasurementCreateRequest grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceMeasurementCreateRequest(grpc));
	}
	return api;
    }

    /**
     * Convert device measurement create requests from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceMeasurementCreateRequest> asGrpcDeviceMeasurementsCreateRequests(
	    List<IDeviceMeasurementCreateRequest> apis) throws SiteWhereException {
	List<GDeviceMeasurementCreateRequest> grpcs = new ArrayList<GDeviceMeasurementCreateRequest>();
	for (IDeviceMeasurementCreateRequest api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceMeasurementCreateRequest(api));
	}
	return grpcs;
    }

    /**
     * Convert device event results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceMeasurement> asApiDeviceMeasurementSearchResults(
	    GDeviceMeasurementSearchResults response) throws SiteWhereException {
	List<IDeviceMeasurement> results = new ArrayList<IDeviceMeasurement>();
	for (GDeviceMeasurement grpc : response.getMeasurementsList()) {
	    results.add(EventModelConverter.asApiDeviceMeasurement(grpc));
	}
	return new SearchResults<IDeviceMeasurement>(results, response.getCount());
    }

    /**
     * Convert device measurement event from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceMeasurement asApiDeviceMeasurement(GDeviceMeasurement grpc) throws SiteWhereException {
	DeviceMeasurement api = new DeviceMeasurement();
	api.setName(grpc.getName());
	api.setValue(grpc.getValue());
	EventModelConverter.copyApiDeviceEvent(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device measurement event from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceMeasurement asGrpcDeviceMeasurement(IDeviceMeasurement api) throws SiteWhereException {
	GDeviceMeasurement.Builder grpc = GDeviceMeasurement.newBuilder();
	grpc.setName(api.getName());
	grpc.setValue(api.getValue());
	grpc.setEvent(EventModelConverter.createGrpcDeviceEvent(api));
	return grpc.build();
    }

    /**
     * Convert device measurements from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceMeasurement> asApiDeviceMeasurements(List<GDeviceMeasurement> grpcs)
	    throws SiteWhereException {
	List<DeviceMeasurement> api = new ArrayList<DeviceMeasurement>();
	for (GDeviceMeasurement grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceMeasurement(grpc));
	}
	return api;
    }

    /**
     * Convert device measurements from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceMeasurement> asGrpcDeviceMeasurements(List<IDeviceMeasurement> apis)
	    throws SiteWhereException {
	List<GDeviceMeasurement> grpcs = new ArrayList<GDeviceMeasurement>();
	for (IDeviceMeasurement api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceMeasurement(api));
	}
	return grpcs;
    }

    /**
     * Convert alert source from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AlertSource asApiAlertSource(GAlertSource grpc) throws SiteWhereException {
	switch (grpc) {
	case ALERT_SOURCE_DEVICE:
	    return AlertSource.Device;
	case ALERT_SOURCE_SYSTEM:
	    return AlertSource.System;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown alert source: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert alert source from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAlertSource asGrpcAlertSource(AlertSource api) throws SiteWhereException {
	switch (api) {
	case Device:
	    return GAlertSource.ALERT_SOURCE_DEVICE;
	case System:
	    return GAlertSource.ALERT_SOURCE_SYSTEM;
	}
	throw new SiteWhereException("Unknown alert source: " + api.name());
    }

    /**
     * Convert alert level from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AlertLevel asApiAlertLevel(GAlertLevel grpc) throws SiteWhereException {
	switch (grpc) {
	case ALERT_LEVEL_CRITICAL:
	    return AlertLevel.Critical;
	case ALERT_LEVEL_ERROR:
	    return AlertLevel.Error;
	case ALERT_LEVEL_INFO:
	    return AlertLevel.Info;
	case ALERT_LEVEL_WARNING:
	    return AlertLevel.Warning;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown alert level: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert alert level from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAlertLevel asGrpcAlertLevel(AlertLevel api) throws SiteWhereException {
	switch (api) {
	case Critical:
	    return GAlertLevel.ALERT_LEVEL_CRITICAL;
	case Error:
	    return GAlertLevel.ALERT_LEVEL_ERROR;
	case Info:
	    return GAlertLevel.ALERT_LEVEL_INFO;
	case Warning:
	    return GAlertLevel.ALERT_LEVEL_WARNING;
	}
	throw new SiteWhereException("Unknown alert level: " + api.name());
    }

    /**
     * Convert device alert create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAlertCreateRequest asApiDeviceAlertCreateRequest(GDeviceAlertCreateRequest grpc)
	    throws SiteWhereException {
	DeviceAlertCreateRequest api = new DeviceAlertCreateRequest();
	api.setSource(EventModelConverter.asApiAlertSource(grpc.getSource()));
	api.setLevel(EventModelConverter.asApiAlertLevel(grpc.getLevel()));
	api.setType(grpc.getType());
	api.setMessage(grpc.getAlertMessage());
	EventModelConverter.copyApiDeviceEventCreateRequest(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device alert create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAlertCreateRequest asGrpcDeviceAlertCreateRequest(IDeviceAlertCreateRequest api)
	    throws SiteWhereException {
	GDeviceAlertCreateRequest.Builder grpc = GDeviceAlertCreateRequest.newBuilder();
	grpc.setSource(EventModelConverter.asGrpcAlertSource(api.getSource()));
	grpc.setLevel(EventModelConverter.asGrpcAlertLevel(api.getLevel()));
	grpc.setType(api.getType());
	grpc.setAlertMessage(api.getMessage());
	grpc.setEvent(EventModelConverter.createGrpcDeviceEventCreateRequest(api));
	return grpc.build();
    }

    /**
     * Convert device alert create requests from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceAlertCreateRequest> asApiDeviceAlertCreateRequests(List<GDeviceAlertCreateRequest> grpcs)
	    throws SiteWhereException {
	List<DeviceAlertCreateRequest> api = new ArrayList<DeviceAlertCreateRequest>();
	for (GDeviceAlertCreateRequest grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceAlertCreateRequest(grpc));
	}
	return api;
    }

    /**
     * Convert device alert create requests from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceAlertCreateRequest> asGrpcDeviceAlertCreateRequests(List<IDeviceAlertCreateRequest> apis)
	    throws SiteWhereException {
	List<GDeviceAlertCreateRequest> grpcs = new ArrayList<GDeviceAlertCreateRequest>();
	for (IDeviceAlertCreateRequest api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceAlertCreateRequest(api));
	}
	return grpcs;
    }

    /**
     * Convert device alert results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceAlert> asApiDeviceAlertSearchResults(GDeviceAlertSearchResults response)
	    throws SiteWhereException {
	List<IDeviceAlert> results = new ArrayList<IDeviceAlert>();
	for (GDeviceAlert grpc : response.getAlertsList()) {
	    results.add(EventModelConverter.asApiDeviceAlert(grpc));
	}
	return new SearchResults<IDeviceAlert>(results, response.getCount());
    }

    /**
     * Convert device alert from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAlert asApiDeviceAlert(GDeviceAlert grpc) throws SiteWhereException {
	DeviceAlert api = new DeviceAlert();
	api.setSource(EventModelConverter.asApiAlertSource(grpc.getSource()));
	api.setLevel(EventModelConverter.asApiAlertLevel(grpc.getLevel()));
	api.setType(grpc.getType());
	api.setMessage(grpc.getAlertMessage());
	EventModelConverter.copyApiDeviceEvent(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device alert from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAlert asGrpcDeviceAlert(IDeviceAlert api) throws SiteWhereException {
	GDeviceAlert.Builder grpc = GDeviceAlert.newBuilder();
	grpc.setSource(EventModelConverter.asGrpcAlertSource(api.getSource()));
	grpc.setLevel(EventModelConverter.asGrpcAlertLevel(api.getLevel()));
	grpc.setType(api.getType());
	grpc.setAlertMessage(api.getMessage());
	grpc.setEvent(EventModelConverter.createGrpcDeviceEvent(api));
	return grpc.build();
    }

    /**
     * Convert device alerts from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceAlert> asApiDeviceAlerts(List<GDeviceAlert> grpcs) throws SiteWhereException {
	List<DeviceAlert> api = new ArrayList<DeviceAlert>();
	for (GDeviceAlert grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceAlert(grpc));
	}
	return api;
    }

    /**
     * Convert device alerts from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceAlert> asGrpcDeviceAlerts(List<IDeviceAlert> apis) throws SiteWhereException {
	List<GDeviceAlert> grpcs = new ArrayList<GDeviceAlert>();
	for (IDeviceAlert api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceAlert(api));
	}
	return grpcs;
    }

    /**
     * Convert device location create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceLocationCreateRequest asApiDeviceLocationCreateRequest(GDeviceLocationCreateRequest grpc)
	    throws SiteWhereException {
	DeviceLocationCreateRequest api = new DeviceLocationCreateRequest();
	api.setLatitude(grpc.hasLatitude() ? grpc.getLatitude().getValue() : null);
	api.setLongitude(grpc.hasLongitude() ? grpc.getLongitude().getValue() : null);
	api.setElevation(grpc.hasElevation() ? grpc.getElevation().getValue() : null);
	EventModelConverter.copyApiDeviceEventCreateRequest(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device location create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceLocationCreateRequest asGrpcDeviceLocationCreateRequest(IDeviceLocationCreateRequest api)
	    throws SiteWhereException {
	GDeviceLocationCreateRequest.Builder grpc = GDeviceLocationCreateRequest.newBuilder();
	if (api.getLatitude() != null) {
	    grpc.setLatitude(GOptionalDouble.newBuilder().setValue(api.getLatitude()).build());
	}
	if (api.getLongitude() != null) {
	    grpc.setLongitude(GOptionalDouble.newBuilder().setValue(api.getLongitude()).build());
	}
	if (api.getElevation() != null) {
	    grpc.setElevation(GOptionalDouble.newBuilder().setValue(api.getElevation()).build());
	}
	grpc.setEvent(EventModelConverter.createGrpcDeviceEventCreateRequest(api));
	return grpc.build();
    }

    /**
     * Convert device location create requests from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceLocationCreateRequest> asApiDeviceLocationCreateRequests(
	    List<GDeviceLocationCreateRequest> grpcs) throws SiteWhereException {
	List<DeviceLocationCreateRequest> api = new ArrayList<DeviceLocationCreateRequest>();
	for (GDeviceLocationCreateRequest grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceLocationCreateRequest(grpc));
	}
	return api;
    }

    /**
     * Convert device location create requests from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceLocationCreateRequest> asGrpcDeviceLocationCreateRequests(
	    List<IDeviceLocationCreateRequest> apis) throws SiteWhereException {
	List<GDeviceLocationCreateRequest> grpcs = new ArrayList<GDeviceLocationCreateRequest>();
	for (IDeviceLocationCreateRequest api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceLocationCreateRequest(api));
	}
	return grpcs;
    }

    /**
     * Convert device location results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceLocation> asApiDeviceLocationSearchResults(
	    GDeviceLocationSearchResults response) throws SiteWhereException {
	List<IDeviceLocation> results = new ArrayList<IDeviceLocation>();
	for (GDeviceLocation grpc : response.getLocationsList()) {
	    results.add(EventModelConverter.asApiDeviceLocation(grpc));
	}
	return new SearchResults<IDeviceLocation>(results, response.getCount());
    }

    /**
     * Convert device location from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceLocation asApiDeviceLocation(GDeviceLocation grpc) throws SiteWhereException {
	DeviceLocation api = new DeviceLocation();
	api.setLatitude(grpc.hasLatitude() ? grpc.getLatitude().getValue() : null);
	api.setLongitude(grpc.hasLongitude() ? grpc.getLongitude().getValue() : null);
	api.setElevation(grpc.hasElevation() ? grpc.getElevation().getValue() : null);
	EventModelConverter.copyApiDeviceEvent(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device location create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceLocation asGrpcDeviceLocation(IDeviceLocation api) throws SiteWhereException {
	GDeviceLocation.Builder grpc = GDeviceLocation.newBuilder();
	if (api.getLatitude() != null) {
	    grpc.setLatitude(GOptionalDouble.newBuilder().setValue(api.getLatitude()).build());
	}
	if (api.getLongitude() != null) {
	    grpc.setLongitude(GOptionalDouble.newBuilder().setValue(api.getLongitude()).build());
	}
	if (api.getElevation() != null) {
	    grpc.setElevation(GOptionalDouble.newBuilder().setValue(api.getElevation()).build());
	}
	grpc.setEvent(EventModelConverter.createGrpcDeviceEvent(api));
	return grpc.build();
    }

    /**
     * Convert device locations from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceLocation> asApiDeviceLocations(List<GDeviceLocation> grpcs) throws SiteWhereException {
	List<DeviceLocation> api = new ArrayList<DeviceLocation>();
	for (GDeviceLocation grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceLocation(grpc));
	}
	return api;
    }

    /**
     * Convert device locations from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceLocation> asGrpcDeviceLocations(List<IDeviceLocation> apis) throws SiteWhereException {
	List<GDeviceLocation> grpcs = new ArrayList<GDeviceLocation>();
	for (IDeviceLocation api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceLocation(api));
	}
	return grpcs;
    }

    /**
     * Convert command initiator from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static CommandInitiator asApiCommandInitiator(GDeviceCommandInitiator grpc) throws SiteWhereException {
	switch (grpc) {
	case CMD_INITIATOR_BATCH_OPERATION:
	    return CommandInitiator.BatchOperation;
	case CMD_INITIATOR_REST:
	    return CommandInitiator.REST;
	case CMD_INITIATOR_SCHEDULER:
	    return CommandInitiator.Scheduler;
	case CMD_INITIATOR_SCRIPT:
	    return CommandInitiator.Script;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown command initiator: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert command initiator from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceCommandInitiator asGrpcCommandInitiator(CommandInitiator api) throws SiteWhereException {
	switch (api) {
	case BatchOperation:
	    return GDeviceCommandInitiator.CMD_INITIATOR_BATCH_OPERATION;
	case REST:
	    return GDeviceCommandInitiator.CMD_INITIATOR_REST;
	case Scheduler:
	    return GDeviceCommandInitiator.CMD_INITIATOR_SCHEDULER;
	case Script:
	    return GDeviceCommandInitiator.CMD_INITIATOR_SCRIPT;
	}
	throw new SiteWhereException("Unknown command initiator: " + api.name());
    }

    /**
     * Convert command target from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static CommandTarget asApiCommandTarget(GDeviceCommandTarget grpc) throws SiteWhereException {
	switch (grpc) {
	case CMD_TARGET_ASSIGNMENT:
	    return CommandTarget.Assignment;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown command target: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert command target from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceCommandTarget asGrpcCommandTarget(CommandTarget api) throws SiteWhereException {
	switch (api) {
	case Assignment:
	    return GDeviceCommandTarget.CMD_TARGET_ASSIGNMENT;
	}
	throw new SiteWhereException("Unknown command target: " + api.name());
    }

    /**
     * Convert command status from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static CommandStatus asApiCommandStatus(GDeviceCommandStatus grpc) throws SiteWhereException {
	switch (grpc) {
	case CMD_STATUS_PENDING:
	    return CommandStatus.Pending;
	case CMD_STATUS_PROCESSING:
	    return CommandStatus.Processing;
	case CMD_STATUS_RESPONDED:
	    return CommandStatus.Responded;
	case CMD_STATUS_SENT:
	    return CommandStatus.Sent;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown command status: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert command status from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceCommandStatus asGrpcCommandStatus(CommandStatus api) throws SiteWhereException {
	switch (api) {
	case Pending:
	    return GDeviceCommandStatus.CMD_STATUS_PENDING;
	case Processing:
	    return GDeviceCommandStatus.CMD_STATUS_PROCESSING;
	case Responded:
	    return GDeviceCommandStatus.CMD_STATUS_RESPONDED;
	case Sent:
	    return GDeviceCommandStatus.CMD_STATUS_SENT;
	}
	throw new SiteWhereException("Unknown command status: " + api.name());
    }

    /**
     * Convert device command invocation create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandInvocationCreateRequest asApiDeviceCommandInvocationCreateRequest(
	    GDeviceCommandInvocationCreateRequest grpc) throws SiteWhereException {
	DeviceCommandInvocationCreateRequest api = new DeviceCommandInvocationCreateRequest();
	api.setInitiator(EventModelConverter.asApiCommandInitiator(grpc.getInitiator()));
	api.setInitiatorId(grpc.getInitiatorId());
	api.setTarget(EventModelConverter.asApiCommandTarget(grpc.getTarget()));
	if (grpc.hasTargetId()) {
	    api.setTargetId(grpc.getTargetId().getValue());
	}
	api.setCommandToken(grpc.getCommandToken());
	api.setParameterValues(grpc.getParameterValuesMap());
	EventModelConverter.copyApiDeviceEventCreateRequest(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device command invocation create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceCommandInvocationCreateRequest asGrpcDeviceCommandInvocationCreateRequest(
	    IDeviceCommandInvocationCreateRequest api) throws SiteWhereException {
	GDeviceCommandInvocationCreateRequest.Builder grpc = GDeviceCommandInvocationCreateRequest.newBuilder();
	grpc.setInitiator(EventModelConverter.asGrpcCommandInitiator(api.getInitiator()));
	grpc.setInitiatorId(api.getInitiatorId());
	grpc.setTarget(EventModelConverter.asGrpcCommandTarget(api.getTarget()));
	if (api.getTargetId() != null) {
	    grpc.setTargetId(GOptionalString.newBuilder().setValue(api.getTargetId()));
	}
	grpc.setCommandToken(api.getCommandToken());
	grpc.putAllParameterValues(api.getParameterValues());
	grpc.setEvent(EventModelConverter.createGrpcDeviceEventCreateRequest(api));
	return grpc.build();
    }

    /**
     * Convert device command invocation create requests from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceCommandInvocationCreateRequest> asApiDeviceCommandInvocationCreateRequests(
	    List<GDeviceCommandInvocationCreateRequest> grpcs) throws SiteWhereException {
	List<DeviceCommandInvocationCreateRequest> api = new ArrayList<DeviceCommandInvocationCreateRequest>();
	for (GDeviceCommandInvocationCreateRequest grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceCommandInvocationCreateRequest(grpc));
	}
	return api;
    }

    /**
     * Convert device command invocation create requests from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceCommandInvocationCreateRequest> asGrpcDeviceCommandInvocationCreateRequests(
	    List<IDeviceCommandInvocationCreateRequest> apis) throws SiteWhereException {
	List<GDeviceCommandInvocationCreateRequest> grpcs = new ArrayList<GDeviceCommandInvocationCreateRequest>();
	for (IDeviceCommandInvocationCreateRequest api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceCommandInvocationCreateRequest(api));
	}
	return grpcs;
    }

    /**
     * Convert device command invocation results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceCommandInvocation> asApiDeviceCommandInvocationSearchResults(
	    GDeviceCommandInvocationSearchResults response) throws SiteWhereException {
	List<IDeviceCommandInvocation> results = new ArrayList<IDeviceCommandInvocation>();
	for (GDeviceCommandInvocation grpc : response.getInvocationsList()) {
	    results.add(EventModelConverter.asApiDeviceCommandInvocation(grpc));
	}
	return new SearchResults<IDeviceCommandInvocation>(results, response.getCount());
    }

    /**
     * Convert command invocation from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandInvocation asApiDeviceCommandInvocation(GDeviceCommandInvocation grpc)
	    throws SiteWhereException {
	DeviceCommandInvocation api = new DeviceCommandInvocation();
	api.setInitiator(EventModelConverter.asApiCommandInitiator(grpc.getInitiator()));
	api.setInitiatorId(grpc.getInitiatorId());
	api.setTarget(EventModelConverter.asApiCommandTarget(grpc.getTarget()));
	if (grpc.hasTargetId()) {
	    api.setTargetId(grpc.getTargetId().getValue());
	}
	api.setDeviceCommandId(CommonModelConverter.asApiUuid(grpc.getDeviceCommandId()));
	api.setParameterValues(grpc.getParameterValuesMap());
	EventModelConverter.copyApiDeviceEvent(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert command invocation from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceCommandInvocation asGrpcDeviceCommandInvocation(IDeviceCommandInvocation api)
	    throws SiteWhereException {
	GDeviceCommandInvocation.Builder grpc = GDeviceCommandInvocation.newBuilder();
	grpc.setInitiator(EventModelConverter.asGrpcCommandInitiator(api.getInitiator()));
	grpc.setInitiatorId(api.getInitiatorId());
	grpc.setTarget(EventModelConverter.asGrpcCommandTarget(api.getTarget()));
	if (api.getTargetId() != null) {
	    grpc.setTargetId(GOptionalString.newBuilder().setValue(api.getTargetId()));
	}
	grpc.setDeviceCommandId(CommonModelConverter.asGrpcUuid(api.getDeviceCommandId()));
	grpc.putAllParameterValues(api.getParameterValues());
	grpc.setEvent(EventModelConverter.createGrpcDeviceEvent(api));
	return grpc.build();
    }

    /**
     * Convert device command invocation from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceCommandInvocation> asApiDeviceCommandInvocations(List<GDeviceCommandInvocation> grpcs)
	    throws SiteWhereException {
	List<DeviceCommandInvocation> api = new ArrayList<DeviceCommandInvocation>();
	for (GDeviceCommandInvocation grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceCommandInvocation(grpc));
	}
	return api;
    }

    /**
     * Convert device command invocations from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceCommandInvocation> asGrpcDeviceCommandInvocations(List<IDeviceCommandInvocation> apis)
	    throws SiteWhereException {
	List<GDeviceCommandInvocation> grpcs = new ArrayList<GDeviceCommandInvocation>();
	for (IDeviceCommandInvocation api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceCommandInvocation(api));
	}
	return grpcs;
    }

    /**
     * Convert device command response create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandResponseCreateRequest asApiDeviceCommandResponseCreateRequest(
	    GDeviceCommandResponseCreateRequest grpc) throws SiteWhereException {
	DeviceCommandResponseCreateRequest api = new DeviceCommandResponseCreateRequest();
	api.setOriginatingEventId(
		grpc.hasOriginatingEventId() ? CommonModelConverter.asApiUuid(grpc.getOriginatingEventId()) : null);
	api.setResponseEventId(
		grpc.hasResponseEventId() ? CommonModelConverter.asApiUuid(grpc.getResponseEventId()) : null);
	api.setResponse(grpc.hasResponse() ? grpc.getResponse().getValue() : null);
	EventModelConverter.copyApiDeviceEventCreateRequest(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device command response create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceCommandResponseCreateRequest asGrpcDeviceCommandResponseCreateRequest(
	    IDeviceCommandResponseCreateRequest api) throws SiteWhereException {
	GDeviceCommandResponseCreateRequest.Builder grpc = GDeviceCommandResponseCreateRequest.newBuilder();
	if (api.getOriginatingEventId() != null) {
	    grpc.setOriginatingEventId(CommonModelConverter.asGrpcUuid(api.getOriginatingEventId()));
	}
	if (api.getResponseEventId() != null) {
	    grpc.setResponseEventId(CommonModelConverter.asGrpcUuid(api.getResponseEventId()));
	}
	if (api.getResponse() != null) {
	    grpc.setResponse(GOptionalString.newBuilder().setValue(api.getResponse()).build());
	}
	grpc.setEvent(EventModelConverter.createGrpcDeviceEventCreateRequest(api));
	return grpc.build();
    }

    /**
     * Convert device command response create requests from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceCommandResponseCreateRequest> asApiDeviceCommandResponseCreateRequests(
	    List<GDeviceCommandResponseCreateRequest> grpcs) throws SiteWhereException {
	List<DeviceCommandResponseCreateRequest> api = new ArrayList<DeviceCommandResponseCreateRequest>();
	for (GDeviceCommandResponseCreateRequest grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceCommandResponseCreateRequest(grpc));
	}
	return api;
    }

    /**
     * Convert device command response create requests from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceCommandResponseCreateRequest> asGrpcDeviceCommandResponseCreateRequests(
	    List<IDeviceCommandResponseCreateRequest> apis) throws SiteWhereException {
	List<GDeviceCommandResponseCreateRequest> grpcs = new ArrayList<GDeviceCommandResponseCreateRequest>();
	for (IDeviceCommandResponseCreateRequest api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceCommandResponseCreateRequest(api));
	}
	return grpcs;
    }

    /**
     * Convert device command response results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceCommandResponse> asApiDeviceCommandResponseSearchResults(
	    GDeviceCommandResponseSearchResults response) throws SiteWhereException {
	List<IDeviceCommandResponse> results = new ArrayList<IDeviceCommandResponse>();
	for (GDeviceCommandResponse grpc : response.getResponsesList()) {
	    results.add(EventModelConverter.asApiDeviceCommandResponse(grpc));
	}
	return new SearchResults<IDeviceCommandResponse>(results, response.getCount());
    }

    /**
     * Convert device command response from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceCommandResponse asApiDeviceCommandResponse(GDeviceCommandResponse grpc)
	    throws SiteWhereException {
	DeviceCommandResponse api = new DeviceCommandResponse();
	api.setOriginatingEventId(
		grpc.hasOriginatingEventId() ? CommonModelConverter.asApiUuid(grpc.getOriginatingEventId()) : null);
	api.setResponseEventId(
		grpc.hasResponseEventId() ? CommonModelConverter.asApiUuid(grpc.getResponseEventId()) : null);
	api.setResponse(grpc.hasResponse() ? grpc.getResponse().getValue() : null);
	EventModelConverter.copyApiDeviceEvent(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device command response from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceCommandResponse asGrpcDeviceCommandResponse(IDeviceCommandResponse api)
	    throws SiteWhereException {
	GDeviceCommandResponse.Builder grpc = GDeviceCommandResponse.newBuilder();
	if (api.getOriginatingEventId() != null) {
	    grpc.setOriginatingEventId(CommonModelConverter.asGrpcUuid(api.getOriginatingEventId()));
	}
	if (api.getResponseEventId() != null) {
	    grpc.setResponseEventId(CommonModelConverter.asGrpcUuid(api.getResponseEventId()));
	}
	if (api.getResponse() != null) {
	    grpc.setResponse(GOptionalString.newBuilder().setValue(api.getResponse()).build());
	}
	grpc.setEvent(EventModelConverter.createGrpcDeviceEvent(api));
	return grpc.build();
    }

    /**
     * Convert device command response from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceCommandResponse> asApiDeviceCommandResponses(List<GDeviceCommandResponse> grpcs)
	    throws SiteWhereException {
	List<DeviceCommandResponse> api = new ArrayList<DeviceCommandResponse>();
	for (GDeviceCommandResponse grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceCommandResponse(grpc));
	}
	return api;
    }

    /**
     * Convert device command response from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceCommandResponse> asGrpcDeviceCommandResponses(List<IDeviceCommandResponse> apis)
	    throws SiteWhereException {
	List<GDeviceCommandResponse> grpcs = new ArrayList<GDeviceCommandResponse>();
	for (IDeviceCommandResponse api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceCommandResponse(api));
	}
	return grpcs;
    }

    /**
     * Convert device state change create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStateChangeCreateRequest asApiDeviceStateChangeCreateRequest(
	    GDeviceStateChangeCreateRequest grpc) throws SiteWhereException {
	DeviceStateChangeCreateRequest api = new DeviceStateChangeCreateRequest();
	api.setAttribute(grpc.getAttribute());
	api.setType(grpc.getType());
	api.setPreviousState(grpc.hasPreviousState() ? grpc.getPreviousState().getValue() : null);
	api.setNewState(grpc.hasNewState() ? grpc.getNewState().getValue() : null);
	EventModelConverter.copyApiDeviceEventCreateRequest(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device state change create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStateChangeCreateRequest asGrpcDeviceStateChangeCreateRequest(
	    IDeviceStateChangeCreateRequest api) throws SiteWhereException {
	GDeviceStateChangeCreateRequest.Builder grpc = GDeviceStateChangeCreateRequest.newBuilder();
	grpc.setAttribute(api.getAttribute());
	grpc.setType(api.getType());
	if (api.getPreviousState() != null) {
	    grpc.setPreviousState(GOptionalString.newBuilder().setValue(api.getPreviousState()));
	}
	if (api.getNewState() != null) {
	    grpc.setNewState(GOptionalString.newBuilder().setValue(api.getNewState()));
	}
	grpc.setEvent(EventModelConverter.createGrpcDeviceEventCreateRequest(api));
	return grpc.build();
    }

    /**
     * Convert device state change create requests from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceStateChangeCreateRequest> asApiDeviceStateChangeCreateRequests(
	    List<GDeviceStateChangeCreateRequest> grpcs) throws SiteWhereException {
	List<DeviceStateChangeCreateRequest> api = new ArrayList<DeviceStateChangeCreateRequest>();
	for (GDeviceStateChangeCreateRequest grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceStateChangeCreateRequest(grpc));
	}
	return api;
    }

    /**
     * Convert device state change create requests from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceStateChangeCreateRequest> asGrpcDeviceStateChangeCreateRequests(
	    List<IDeviceStateChangeCreateRequest> apis) throws SiteWhereException {
	List<GDeviceStateChangeCreateRequest> grpcs = new ArrayList<GDeviceStateChangeCreateRequest>();
	for (IDeviceStateChangeCreateRequest api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceStateChangeCreateRequest(api));
	}
	return grpcs;
    }

    /**
     * Convert device state change results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceStateChange> asApiDeviceStateChangeSearchResults(
	    GDeviceStateChangeSearchResults response) throws SiteWhereException {
	List<IDeviceStateChange> results = new ArrayList<IDeviceStateChange>();
	for (GDeviceStateChange grpc : response.getStateChangesList()) {
	    results.add(EventModelConverter.asApiDeviceStateChange(grpc));
	}
	return new SearchResults<IDeviceStateChange>(results, response.getCount());
    }

    /**
     * Convert device state change from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceStateChange asApiDeviceStateChange(GDeviceStateChange grpc) throws SiteWhereException {
	DeviceStateChange api = new DeviceStateChange();
	api.setAttribute(grpc.getAttribute());
	api.setType(grpc.getType());
	api.setPreviousState(grpc.hasPreviousState() ? grpc.getPreviousState().getValue() : null);
	api.setNewState(grpc.hasNewState() ? grpc.getNewState().getValue() : null);
	EventModelConverter.copyApiDeviceEvent(grpc.getEvent(), api);
	return api;
    }

    /**
     * Convert device state change from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceStateChange asGrpcDeviceStateChange(IDeviceStateChange api) throws SiteWhereException {
	GDeviceStateChange.Builder grpc = GDeviceStateChange.newBuilder();
	grpc.setAttribute(api.getAttribute());
	grpc.setType(api.getType());
	if (api.getPreviousState() != null) {
	    grpc.setPreviousState(GOptionalString.newBuilder().setValue(api.getPreviousState()));
	}
	if (api.getNewState() != null) {
	    grpc.setNewState(GOptionalString.newBuilder().setValue(api.getNewState()));
	}
	grpc.setEvent(EventModelConverter.createGrpcDeviceEvent(api));
	return grpc.build();
    }

    /**
     * Convert device state changes from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<DeviceStateChange> asApiDeviceStateChanges(List<GDeviceStateChange> grpcs)
	    throws SiteWhereException {
	List<DeviceStateChange> api = new ArrayList<DeviceStateChange>();
	for (GDeviceStateChange grpc : grpcs) {
	    api.add(EventModelConverter.asApiDeviceStateChange(grpc));
	}
	return api;
    }

    /**
     * Convert device state changes from API to GRPC.
     * 
     * @param apis
     * @return
     * @throws SiteWhereException
     */
    public static List<GDeviceStateChange> asGrpcDeviceStateChanges(List<IDeviceStateChange> apis)
	    throws SiteWhereException {
	List<GDeviceStateChange> grpcs = new ArrayList<GDeviceStateChange>();
	for (IDeviceStateChange api : apis) {
	    grpcs.add(EventModelConverter.asGrpcDeviceStateChange(api));
	}
	return grpcs;
    }

    /**
     * Convert device event batch from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceEventBatch asApiDeviceEventBatch(GDeviceEventBatchCreateRequest grpc)
	    throws SiteWhereException {
	DeviceEventBatch api = new DeviceEventBatch();
	api.setDeviceToken(grpc.getHardwareId());
	api.setMeasurements(EventModelConverter.asApiDeviceMeasurementCreateRequests(grpc.getMeasurementsList()));
	api.setAlerts(EventModelConverter.asApiDeviceAlertCreateRequests(grpc.getAlertsList()));
	api.setLocations(EventModelConverter.asApiDeviceLocationCreateRequests(grpc.getLocationsList()));
	return api;
    }

    /**
     * Convert device event batch from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceEventBatchCreateRequest asGrpcDeviceEventBatch(IDeviceEventBatch api)
	    throws SiteWhereException {
	GDeviceEventBatchCreateRequest.Builder grpc = GDeviceEventBatchCreateRequest.newBuilder();
	grpc.setHardwareId(api.getDeviceToken());
	grpc.addAllMeasurements(EventModelConverter.asGrpcDeviceMeasurementsCreateRequests(api.getMeasurements()));
	grpc.addAllAlerts(EventModelConverter.asGrpcDeviceAlertCreateRequests(api.getAlerts()));
	grpc.addAllLocations(EventModelConverter.asGrpcDeviceLocationCreateRequests(api.getLocations()));
	return grpc.build();
    }

    /**
     * Convert device event batch response from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceEventBatchResponse asApiDeviceEventBatchResponse(GDeviceEventBatchResponse grpc)
	    throws SiteWhereException {
	DeviceEventBatchResponse api = new DeviceEventBatchResponse();
	api.setCreatedAlerts(EventModelConverter.asApiDeviceAlerts(grpc.getAlertsList()));
	api.setCreatedLocations(EventModelConverter.asApiDeviceLocations(grpc.getLocationsList()));
	api.setCreatedMeasurements(EventModelConverter.asApiDeviceMeasurements(grpc.getMeasurementsList()));
	return api;
    }

    /**
     * Convert device event batch response from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceEventBatchResponse asGrpcDeviceEventBatchResponse(IDeviceEventBatchResponse api)
	    throws SiteWhereException {
	GDeviceEventBatchResponse.Builder grpc = GDeviceEventBatchResponse.newBuilder();
	grpc.addAllMeasurements(EventModelConverter.asGrpcDeviceMeasurements(api.getCreatedMeasurements()));
	grpc.addAllAlerts(EventModelConverter.asGrpcDeviceAlerts(api.getCreatedAlerts()));
	grpc.addAllLocations(EventModelConverter.asGrpcDeviceLocations(api.getCreatedLocations()));
	return grpc.build();
    }

    /**
     * Convert generic event create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceEventCreateRequest asApiDeviceEventCreateRequest(GAnyDeviceEventCreateRequest grpc)
	    throws SiteWhereException {
	switch (grpc.getEventCase()) {
	case ALERT: {
	    return EventModelConverter.asApiDeviceAlertCreateRequest(grpc.getAlert());
	}
	case LOCATION: {
	    return EventModelConverter.asApiDeviceLocationCreateRequest(grpc.getLocation());
	}
	case MEASUREMENT: {
	    return EventModelConverter.asApiDeviceMeasurementCreateRequest(grpc.getMeasurement());
	}
	case COMMANDINVOCATION: {
	    return EventModelConverter.asApiDeviceCommandInvocationCreateRequest(grpc.getCommandInvocation());
	}
	case COMMANDRESPONSE: {
	    return EventModelConverter.asApiDeviceCommandResponseCreateRequest(grpc.getCommandResponse());
	}
	case STATECHANGE: {
	    return EventModelConverter.asApiDeviceStateChangeCreateRequest(grpc.getStateChange());
	}
	case EVENT_NOT_SET: {
	    break;
	}
	}
	throw new SiteWhereException(
		"Unable to convert event create request to API. " + grpc.getEventCase().toString());
    }

    /**
     * Convert generic event create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAnyDeviceEventCreateRequest asGrpcDeviceEventCreateRequest(IDeviceEventCreateRequest api)
	    throws SiteWhereException {
	GAnyDeviceEventCreateRequest.Builder grpc = GAnyDeviceEventCreateRequest.newBuilder();
	switch (api.getEventType()) {
	case Measurement:
	    grpc.setMeasurement(
		    EventModelConverter.asGrpcDeviceMeasurementCreateRequest((IDeviceMeasurementCreateRequest) api));
	    break;
	case Alert:
	    grpc.setAlert(EventModelConverter.asGrpcDeviceAlertCreateRequest((IDeviceAlertCreateRequest) api));
	    break;
	case Location:
	    grpc.setLocation(EventModelConverter.asGrpcDeviceLocationCreateRequest((IDeviceLocationCreateRequest) api));
	    break;
	case CommandInvocation: {
	    grpc.setCommandInvocation(EventModelConverter
		    .asGrpcDeviceCommandInvocationCreateRequest((IDeviceCommandInvocationCreateRequest) api));
	    break;
	}
	case CommandResponse: {
	    grpc.setCommandResponse(EventModelConverter
		    .asGrpcDeviceCommandResponseCreateRequest((IDeviceCommandResponseCreateRequest) api));
	    break;
	}
	case StateChange: {
	    grpc.setStateChange(
		    EventModelConverter.asGrpcDeviceStateChangeCreateRequest((IDeviceStateChangeCreateRequest) api));
	    break;
	}
	default:
	    throw new SiteWhereException("Unable to convert event create request to GRPC. " + api.getClass().getName());
	}

	return grpc.build();
    }

    /**
     * Convert generic event from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IDeviceEvent asApiGenericDeviceEvent(GAnyDeviceEvent grpc) throws SiteWhereException {
	switch (grpc.getEventCase()) {
	case ALERT: {
	    return EventModelConverter.asApiDeviceAlert(grpc.getAlert());
	}
	case LOCATION: {
	    return EventModelConverter.asApiDeviceLocation(grpc.getLocation());
	}
	case MEASUREMENT: {
	    return EventModelConverter.asApiDeviceMeasurement(grpc.getMeasurement());
	}
	case COMMANDINVOCATION: {
	    return EventModelConverter.asApiDeviceCommandInvocation(grpc.getCommandInvocation());
	}
	case COMMANDRESPONSE: {
	    return EventModelConverter.asApiDeviceCommandResponse(grpc.getCommandResponse());
	}
	case STATECHANGE: {
	    return EventModelConverter.asApiDeviceStateChange(grpc.getStateChange());
	}
	case EVENT_NOT_SET: {
	    break;
	}
	}
	throw new SiteWhereException("Unable to convert event to API. " + grpc.getEventCase().toString());
    }

    /**
     * Convert device event results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IDeviceEvent> asApiDeviceEventSearchResults(GDeviceEventSearchResults response)
	    throws SiteWhereException {
	List<IDeviceEvent> results = new ArrayList<IDeviceEvent>();
	for (GAnyDeviceEvent grpc : response.getEventsList()) {
	    results.add(EventModelConverter.asApiGenericDeviceEvent(grpc));
	}
	return new SearchResults<IDeviceEvent>(results, response.getCount());
    }

    /**
     * Convert generic event from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GAnyDeviceEvent asGrpcGenericDeviceEvent(IDeviceEvent api) throws SiteWhereException {
	GAnyDeviceEvent.Builder grpc = GAnyDeviceEvent.newBuilder();
	switch (api.getEventType()) {
	case Measurement:
	    grpc.setMeasurement(EventModelConverter.asGrpcDeviceMeasurement((IDeviceMeasurement) api));
	    break;
	case Alert:
	    grpc.setAlert(EventModelConverter.asGrpcDeviceAlert((IDeviceAlert) api));
	    break;
	case Location:
	    grpc.setLocation(EventModelConverter.asGrpcDeviceLocation((IDeviceLocation) api));
	    break;
	case CommandInvocation: {
	    grpc.setCommandInvocation(
		    EventModelConverter.asGrpcDeviceCommandInvocation((IDeviceCommandInvocation) api));
	    break;
	}
	case CommandResponse: {
	    grpc.setCommandResponse(EventModelConverter.asGrpcDeviceCommandResponse((IDeviceCommandResponse) api));
	    break;
	}
	case StateChange: {
	    grpc.setStateChange(EventModelConverter.asGrpcDeviceStateChange((IDeviceStateChange) api));
	    break;
	}
	default:
	    throw new SiteWhereException("Unable to convert event to GRPC. " + api.getClass().getName());
	}

	return grpc.build();
    }

    /**
     * Convert device event context from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceEventContext asApiDeviceEventContext(GDeviceEventContext grpc) throws SiteWhereException {
	DeviceEventContext api = new DeviceEventContext();
	api.setDeviceId(CommonModelConverter.asApiUuid(grpc.getDeviceId()));
	api.setDeviceTypeId(CommonModelConverter.asApiUuid(grpc.getDeviceTypeId()));
	api.setParentDeviceId(
		grpc.hasParentDeviceId() ? CommonModelConverter.asApiUuid(grpc.getParentDeviceId()) : null);
	api.setDeviceStatus(grpc.hasDeviceStatus() ? grpc.getDeviceStatus().getValue() : null);
	api.setDeviceMetadata(grpc.getDeviceMetadataMap());
	api.setAssignmentStatus(CommonModelConverter.asApiDeviceAssignmentStatus(grpc.getAssignmentStatus()));
	api.setAssignmentMetadata(grpc.getAssignmentMetadataMap());
	return api;
    }

    /**
     * Convert device event context from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceEventContext asGrpcDeviceEventContext(IDeviceEventContext api) throws SiteWhereException {
	GDeviceEventContext.Builder grpc = GDeviceEventContext.newBuilder();
	grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	grpc.setDeviceTypeId(CommonModelConverter.asGrpcUuid(api.getDeviceTypeId()));
	if (api.getParentDeviceId() != null) {
	    grpc.setParentDeviceId(CommonModelConverter.asGrpcUuid(api.getParentDeviceId()));
	}
	if (api.getDeviceStatus() != null) {
	    grpc.setDeviceStatus(GOptionalString.newBuilder().setValue(api.getDeviceStatus()));
	}
	grpc.putAllDeviceMetadata(api.getDeviceMetadata());
	grpc.setAssignmentStatus(CommonModelConverter.asGrpcDeviceAssignmentStatus(api.getAssignmentStatus()));
	grpc.putAllAssignmentMetadata(api.getAssignmentMetadata());
	return grpc.build();
    }

    /**
     * Convert device assignment event create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DeviceAssignmentEventCreateRequest asApiDeviceAssignmentEventCreateRequest(
	    GDeviceAssignmentEventCreateRequest grpc) throws SiteWhereException {
	DeviceAssignmentEventCreateRequest api = new DeviceAssignmentEventCreateRequest();
	api.setDeviceAssignmentId(CommonModelConverter.asApiUuid(grpc.getDeviceAssignmentId()));
	api.setRequest(EventModelConverter.asApiDeviceEventCreateRequest(grpc.getRequest()));
	return api;
    }

    /**
     * Convert device assignment event create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDeviceAssignmentEventCreateRequest asGrpcDeviceAssignmentEventCreateRequest(
	    IDeviceAssignmentEventCreateRequest api) throws SiteWhereException {
	GDeviceAssignmentEventCreateRequest.Builder grpc = GDeviceAssignmentEventCreateRequest.newBuilder();
	grpc.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(api.getDeviceAssignmentId()));
	grpc.setRequest(EventModelConverter.asGrpcDeviceEventCreateRequest(api.getRequest()));
	return grpc.build();
    }

    /**
     * Convert decoded event payload from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static DecodedEventPayload asApiDecodedEventPayload(GDecodedEventPayload grpc) throws SiteWhereException {
	DecodedEventPayload api = new DecodedEventPayload();
	api.setSourceId(grpc.getSourceId());
	api.setDeviceToken(grpc.getDeviceToken());
	api.setOriginator(grpc.hasOriginator() ? grpc.getOriginator().getValue() : null);
	api.setEventCreateRequest(EventModelConverter.asApiDeviceEventCreateRequest(grpc.getEvent()));
	return api;
    }

    /**
     * Convert decoded event payload from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GDecodedEventPayload asGrpcDecodedEventPayload(IDecodedEventPayload api) throws SiteWhereException {
	GDecodedEventPayload.Builder grpc = GDecodedEventPayload.newBuilder();
	grpc.setSourceId(api.getSourceId());
	grpc.setDeviceToken(api.getDeviceToken());
	if (api.getOriginator() != null) {
	    grpc.setOriginator(GOptionalString.newBuilder().setValue(api.getOriginator()));
	}
	grpc.setEvent(EventModelConverter.asGrpcDeviceEventCreateRequest(api.getEventCreateRequest()));
	return grpc.build();
    }

    /**
     * Convert preprocessed event payload from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GPreprocessedEventPayload asGrpcPreprocessedEventPayload(IPreprocessedEventPayload api)
	    throws SiteWhereException {
	GPreprocessedEventPayload.Builder grpc = GPreprocessedEventPayload.newBuilder();
	grpc.setSourceId(api.getSourceId());
	grpc.setDeviceToken(api.getDeviceToken());
	if (api.getOriginator() != null) {
	    grpc.setOriginator(GOptionalString.newBuilder().setValue(api.getOriginator()));
	}
	grpc.setDeviceId(CommonModelConverter.asGrpcUuid(api.getDeviceId()));
	grpc.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(api.getDeviceAssignmentId()));
	grpc.setEvent(EventModelConverter.asGrpcDeviceEventCreateRequest(api.getEventCreateRequest()));
	return grpc.build();
    }

    /**
     * Convert preprocessed event payload from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static PreprocessedEventPayload asApiPreprocessedEventPayload(GPreprocessedEventPayload grpc)
	    throws SiteWhereException {
	PreprocessedEventPayload api = new PreprocessedEventPayload();
	api.setSourceId(grpc.getSourceId());
	api.setDeviceToken(grpc.getDeviceToken());
	api.setOriginator(grpc.hasOriginator() ? grpc.getOriginator().getValue() : null);
	api.setDeviceId(CommonModelConverter.asApiUuid(grpc.getDeviceId()));
	api.setDeviceAssignmentId(CommonModelConverter.asApiUuid(grpc.getDeviceAssignmentId()));
	api.setEventCreateRequest(EventModelConverter.asApiDeviceEventCreateRequest(grpc.getEvent()));
	return api;
    }

    /**
     * Convert enriched event payload from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static EnrichedEventPayload asApiEnrichedEventPayload(GEnrichedEventPayload grpc) throws SiteWhereException {
	EnrichedEventPayload api = new EnrichedEventPayload();
	api.setEventContext(EventModelConverter.asApiDeviceEventContext(grpc.getContext()));
	api.setEvent(EventModelConverter.asApiGenericDeviceEvent(grpc.getEvent()));
	return api;
    }

    /**
     * Convert enriched event payload from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GEnrichedEventPayload asGrpcEnrichedEventPayload(IEnrichedEventPayload api)
	    throws SiteWhereException {
	GEnrichedEventPayload.Builder grpc = GEnrichedEventPayload.newBuilder();
	grpc.setContext(EventModelConverter.asGrpcDeviceEventContext(api.getEventContext()));
	grpc.setEvent(EventModelConverter.asGrpcGenericDeviceEvent(api.getEvent()));
	return grpc.build();
    }
}