/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.event;

import java.util.List;
import java.util.UUID;

import org.reactivestreams.Processor;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.MultitenantApiChannel;
import com.sitewhere.grpc.client.event.streaming.DeviceAssignmentEventCreateProcessor;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.converter.DeviceModelConverter;
import com.sitewhere.grpc.model.converter.EventModelConverter;
import com.sitewhere.grpc.model.tracing.DebugParameter;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.grpc.service.GAddAlertForAssignmentRequest;
import com.sitewhere.grpc.service.GAddAlertForAssignmentResponse;
import com.sitewhere.grpc.service.GAddCommandInvocationForAssignmentRequest;
import com.sitewhere.grpc.service.GAddCommandInvocationForAssignmentResponse;
import com.sitewhere.grpc.service.GAddCommandResponseForAssignmentRequest;
import com.sitewhere.grpc.service.GAddCommandResponseForAssignmentResponse;
import com.sitewhere.grpc.service.GAddDeviceEventBatchRequest;
import com.sitewhere.grpc.service.GAddDeviceEventBatchResponse;
import com.sitewhere.grpc.service.GAddLocationForAssignmentRequest;
import com.sitewhere.grpc.service.GAddLocationForAssignmentResponse;
import com.sitewhere.grpc.service.GAddMeasurementsForAssignmentRequest;
import com.sitewhere.grpc.service.GAddMeasurementsForAssignmentResponse;
import com.sitewhere.grpc.service.GAddStateChangeForAssignmentRequest;
import com.sitewhere.grpc.service.GAddStateChangeForAssignmentResponse;
import com.sitewhere.grpc.service.GAddStreamDataForAssignmentRequest;
import com.sitewhere.grpc.service.GAddStreamDataForAssignmentResponse;
import com.sitewhere.grpc.service.GGetDeviceEventByAlternateIdRequest;
import com.sitewhere.grpc.service.GGetDeviceEventByAlternateIdResponse;
import com.sitewhere.grpc.service.GGetDeviceEventByIdRequest;
import com.sitewhere.grpc.service.GGetDeviceEventByIdResponse;
import com.sitewhere.grpc.service.GGetStreamDataForAssignmentRequest;
import com.sitewhere.grpc.service.GGetStreamDataForAssignmentResponse;
import com.sitewhere.grpc.service.GListAlertsForAreasRequest;
import com.sitewhere.grpc.service.GListAlertsForAreasResponse;
import com.sitewhere.grpc.service.GListAlertsForAssignmentRequest;
import com.sitewhere.grpc.service.GListAlertsForAssignmentResponse;
import com.sitewhere.grpc.service.GListCommandInvocationsForAreasRequest;
import com.sitewhere.grpc.service.GListCommandInvocationsForAreasResponse;
import com.sitewhere.grpc.service.GListCommandInvocationsForAssignmentRequest;
import com.sitewhere.grpc.service.GListCommandInvocationsForAssignmentResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForAreasRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForAreasResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForAssignmentRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForAssignmentResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForInvocationRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForInvocationResponse;
import com.sitewhere.grpc.service.GListDeviceEventsRequest;
import com.sitewhere.grpc.service.GListDeviceEventsResponse;
import com.sitewhere.grpc.service.GListLocationsForAreasRequest;
import com.sitewhere.grpc.service.GListLocationsForAreasResponse;
import com.sitewhere.grpc.service.GListLocationsForAssignmentRequest;
import com.sitewhere.grpc.service.GListLocationsForAssignmentResponse;
import com.sitewhere.grpc.service.GListMeasurementsForAreasRequest;
import com.sitewhere.grpc.service.GListMeasurementsForAreasResponse;
import com.sitewhere.grpc.service.GListMeasurementsForAssignmentRequest;
import com.sitewhere.grpc.service.GListMeasurementsForAssignmentResponse;
import com.sitewhere.grpc.service.GListStateChangesForAreasRequest;
import com.sitewhere.grpc.service.GListStateChangesForAreasResponse;
import com.sitewhere.grpc.service.GListStateChangesForAssignmentRequest;
import com.sitewhere.grpc.service.GListStateChangesForAssignmentResponse;
import com.sitewhere.grpc.service.GListStreamDataForAssignmentRequest;
import com.sitewhere.grpc.service.GListStreamDataForAssignmentResponse;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceAssignmentEventCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.event.streaming.IEventStreamAck;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Supports SiteWhere device event management APIs on top of a
 * {@link DeviceEventManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class DeviceEventManagementApiChannel extends MultitenantApiChannel<DeviceEventManagementGrpcChannel>
	implements IDeviceEventManagementApiChannel<DeviceEventManagementGrpcChannel> {

    public DeviceEventManagementApiChannel(IApiDemux<?> demux, String host, int port) {
	super(demux, host, port);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .tracing.ITracerProvider, java.lang.String, int)
     */
    @Override
    public DeviceEventManagementGrpcChannel createGrpcChannel(ITracerProvider tracerProvider, String host, int port) {
	return new DeviceEventManagementGrpcChannel(tracerProvider, host, port);
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceEventBatch(
     * java.util.UUID, com.sitewhere.spi.device.event.IDeviceEventBatch)
     */
    @Override
    public IDeviceEventBatchResponse addDeviceEventBatch(UUID deviceAssignmentId, IDeviceEventBatch batch)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_DEVICE_EVENT_BATCH,
		    DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Batch", batch));
	    GAddDeviceEventBatchRequest.Builder grequest = GAddDeviceEventBatchRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceEventBatch(batch));
	    GAddDeviceEventBatchResponse gresponse = getGrpcChannel().getBlockingStub().addDeviceEventBatch(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_ADD_DEVICE_EVENT_BATCH, grequest.build()));
	    IDeviceEventBatchResponse response = EventModelConverter
		    .asApiDeviceEventBatchResponse(gresponse.getResponse());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_DEVICE_EVENT_BATCH, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_DEVICE_EVENT_BATCH, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceEventById(java
     * .util.UUID, java.util.UUID)
     */
    @Override
    public IDeviceEvent getDeviceEventById(UUID deviceId, UUID eventId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ID,
		    DebugParameter.create("Device Id", deviceId), DebugParameter.create("Event Id", eventId));
	    GGetDeviceEventByIdRequest.Builder grequest = GGetDeviceEventByIdRequest.newBuilder();
	    grequest.setDeviceId(CommonModelConverter.asGrpcUuid(deviceId));
	    grequest.setEventId(CommonModelConverter.asGrpcUuid(eventId));
	    GGetDeviceEventByIdResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceEventById(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ID, grequest.build()));
	    IDeviceEvent response = (gresponse.hasEvent())
		    ? EventModelConverter.asApiGenericDeviceEvent(gresponse.getEvent())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ID, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ID, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * getDeviceEventByAlternateId(java.util.UUID, java.lang.String)
     */
    @Override
    public IDeviceEvent getDeviceEventByAlternateId(UUID deviceId, String alternateId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ALTERNATE_ID,
		    DebugParameter.create("Alternate Id", alternateId));
	    GGetDeviceEventByAlternateIdRequest.Builder grequest = GGetDeviceEventByAlternateIdRequest.newBuilder();
	    grequest.setAlternateId(alternateId);
	    GGetDeviceEventByAlternateIdResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceEventByAlternateId(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ALTERNATE_ID, grequest.build()));
	    IDeviceEvent response = (gresponse.hasEvent())
		    ? EventModelConverter.asApiGenericDeviceEvent(gresponse.getEvent())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ALTERNATE_ID,
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ALTERNATE_ID, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#listDeviceEvents(java.
     * util.UUID, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceEvent> listDeviceEvents(UUID deviceAssignmentId, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_DEVICE_EVENTS,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Criteria", criteria));
	    GListDeviceEventsRequest.Builder grequest = GListDeviceEventsRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setCriteria(EventModelConverter.asGrpcDeviceEventSearchCriteria(criteria));
	    GListDeviceEventsResponse gresponse = getGrpcChannel().getBlockingStub().listDeviceEvents(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_DEVICE_EVENTS, grequest.build()));
	    ISearchResults<IDeviceEvent> results = EventModelConverter
		    .asApiDeviceEventSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_DEVICE_EVENTS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_DEVICE_EVENTS, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * streamDeviceAssignmentCreateEvents()
     */
    @Override
    public Processor<IDeviceAssignmentEventCreateRequest, IEventStreamAck> streamDeviceAssignmentCreateEvents() {
	GrpcUtils.logClientMethodEntry(this,
		DeviceEventManagementGrpc.METHOD_STREAM_DEVICE_ASSIGNMENT_EVENT_CREATE_REQUESTS);
	return new DeviceAssignmentEventCreateProcessor(getGrpcChannel());
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceMeasurements(
     * java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest)
     */
    @Override
    public IDeviceMeasurements addDeviceMeasurements(UUID deviceAssignmentId,
	    IDeviceMeasurementsCreateRequest measurements) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", measurements));
	    GAddMeasurementsForAssignmentRequest.Builder grequest = GAddMeasurementsForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceMeasurementsCreateRequest(measurements));
	    GAddMeasurementsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addMeasurementsForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS_FOR_ASSIGNMENT, grequest.build()));
	    IDeviceMeasurements response = EventModelConverter.asApiDeviceMeasurements(gresponse.getMeasurements());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS_FOR_ASSIGNMENT,
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurementsForAssignment(java.util.UUID,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForAssignment(UUID deviceAssignmentId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Criteria", criteria));
	    GListMeasurementsForAssignmentRequest.Builder grequest = GListMeasurementsForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListMeasurementsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listMeasurementsForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_ASSIGNMENT, grequest.build()));
	    ISearchResults<IDeviceMeasurements> results = EventModelConverter
		    .asApiDeviceMeasurementsSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_ASSIGNMENT,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurementsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_AREAS,
		    DebugParameter.create("Area Ids", areaIds), DebugParameter.create("Criteria", criteria));
	    GListMeasurementsForAreasRequest.Builder grequest = GListMeasurementsForAreasRequest.newBuilder();
	    grequest.addAllAreaIds(CommonModelConverter.asGrpcUuids(areaIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListMeasurementsForAreasResponse gresponse = getGrpcChannel().getBlockingStub().listMeasurementsForAreas(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_AREAS,
			    grequest.build()));
	    ISearchResults<IDeviceMeasurements> results = EventModelConverter
		    .asApiDeviceMeasurementsSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_AREAS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_AREAS,
		    t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceLocation(java.
     * util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest)
     */
    @Override
    public IDeviceLocation addDeviceLocation(UUID deviceAssignmentId, IDeviceLocationCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_LOCATION_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddLocationForAssignmentRequest.Builder grequest = GAddLocationForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceLocationCreateRequest(request));
	    GAddLocationForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub().addLocationForAssignment(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_ADD_LOCATION_FOR_ASSIGNMENT,
			    grequest.build()));
	    IDeviceLocation response = EventModelConverter.asApiDeviceLocation(gresponse.getLocation());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_LOCATION_FOR_ASSIGNMENT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_LOCATION_FOR_ASSIGNMENT,
		    t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceLocationsForAssignment(java.util.UUID,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForAssignment(UUID deviceAssignmentId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Criteria", criteria));
	    GListLocationsForAssignmentRequest.Builder grequest = GListLocationsForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListLocationsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listLocationsForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_ASSIGNMENT, grequest.build()));
	    ISearchResults<IDeviceLocation> results = EventModelConverter
		    .asApiDeviceLocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_ASSIGNMENT, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_ASSIGNMENT,
		    t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceLocationsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_AREAS,
		    DebugParameter.create("Area Ids", areaIds), DebugParameter.create("Criteria", criteria));
	    GListLocationsForAreasRequest.Builder grequest = GListLocationsForAreasRequest.newBuilder();
	    grequest.addAllAreaIds(CommonModelConverter.asGrpcUuids(areaIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListLocationsForAreasResponse gresponse = getGrpcChannel().getBlockingStub().listLocationsForAreas(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_AREAS,
			    grequest.build()));
	    ISearchResults<IDeviceLocation> results = EventModelConverter
		    .asApiDeviceLocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_AREAS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_AREAS, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceAlert(java.
     * util.UUID, com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest)
     */
    @Override
    public IDeviceAlert addDeviceAlert(UUID deviceAssignmentId, IDeviceAlertCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_ALERT_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddAlertForAssignmentRequest.Builder grequest = GAddAlertForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceAlertCreateRequest(request));
	    GAddAlertForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub().addAlertForAssignment(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_ADD_ALERT_FOR_ASSIGNMENT,
			    grequest.build()));
	    IDeviceAlert response = EventModelConverter.asApiDeviceAlert(gresponse.getAlert());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_ALERT_FOR_ASSIGNMENT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_ALERT_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceAlertsForAssignment(java.util.UUID,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForAssignment(UUID deviceAssignmentId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Criteria", criteria));
	    GListAlertsForAssignmentRequest.Builder grequest = GListAlertsForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListAlertsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub().listAlertsForAssignment(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_ASSIGNMENT,
			    grequest.build()));
	    ISearchResults<IDeviceAlert> results = EventModelConverter
		    .asApiDeviceAlertSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_ASSIGNMENT, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceAlertsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForAreas(List<UUID> areaIds, IDateRangeSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_AREAS,
		    DebugParameter.create("Area Ids", areaIds), DebugParameter.create("Criteria", criteria));
	    GListAlertsForAreasRequest.Builder grequest = GListAlertsForAreasRequest.newBuilder();
	    grequest.addAllAreaIds(CommonModelConverter.asGrpcUuids(areaIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListAlertsForAreasResponse gresponse = getGrpcChannel().getBlockingStub().listAlertsForAreas(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_AREAS, grequest.build()));
	    ISearchResults<IDeviceAlert> results = EventModelConverter
		    .asApiDeviceAlertSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_AREAS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_AREAS, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceStreamData(
     * java.util.UUID, com.sitewhere.spi.device.streaming.IDeviceStream,
     * com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest)
     */
    @Override
    public IDeviceStreamData addDeviceStreamData(UUID deviceAssignmentId, IDeviceStream stream,
	    IDeviceStreamDataCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_STREAM_DATA_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Stream", stream),
		    DebugParameter.create("Request", request));
	    GAddStreamDataForAssignmentRequest.Builder grequest = GAddStreamDataForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setDeviceStream(DeviceModelConverter.asGrpcDeviceStream(stream));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceStreamDataCreateRequest(request));
	    GAddStreamDataForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addStreamDataForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_ADD_STREAM_DATA_FOR_ASSIGNMENT, grequest.build()));
	    IDeviceStreamData response = EventModelConverter.asApiDeviceStreamData(gresponse.getStreamData());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_STREAM_DATA_FOR_ASSIGNMENT,
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_STREAM_DATA_FOR_ASSIGNMENT,
		    t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#getDeviceStreamData(
     * java.util.UUID, java.lang.String, long)
     */
    @Override
    public IDeviceStreamData getDeviceStreamData(UUID deviceAssignmentId, String streamId, long sequenceNumber)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_GET_STREAM_DATA_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Stream Id", streamId),
		    DebugParameter.create("Sequence Number", String.valueOf(sequenceNumber)));
	    GGetStreamDataForAssignmentRequest.Builder grequest = GGetStreamDataForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setStreamId(streamId);
	    grequest.setSequenceNumber(sequenceNumber);
	    GGetStreamDataForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getStreamDataForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_GET_STREAM_DATA_FOR_ASSIGNMENT, grequest.build()));
	    IDeviceStreamData results = EventModelConverter.asApiDeviceStreamData(gresponse.getStreamData());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_GET_STREAM_DATA_FOR_ASSIGNMENT, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_GET_STREAM_DATA_FOR_ASSIGNMENT,
		    t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStreamDataForAssignment(java.util.UUID, java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStreamData> listDeviceStreamDataForAssignment(UUID deviceAssignmentId, String streamId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_STREAM_DATA_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Stream Id", streamId), DebugParameter.create("Criteria", criteria));
	    GListStreamDataForAssignmentRequest.Builder grequest = GListStreamDataForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setStreamId(streamId);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListStreamDataForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listStreamDataForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_STREAM_DATA_FOR_ASSIGNMENT, grequest.build()));
	    ISearchResults<IDeviceStreamData> results = EventModelConverter
		    .asApiDeviceStreamDataSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_STREAM_DATA_FOR_ASSIGNMENT,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_STREAM_DATA_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceCommandInvocation(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest)
     */
    @Override
    public IDeviceCommandInvocation addDeviceCommandInvocation(UUID deviceAssignmentId,
	    IDeviceCommandInvocationCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddCommandInvocationForAssignmentRequest.Builder grequest = GAddCommandInvocationForAssignmentRequest
		    .newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceCommandInvocationCreateRequest(request));
	    GAddCommandInvocationForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addCommandInvocationForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION_FOR_ASSIGNMENT, grequest.build()));
	    IDeviceCommandInvocation response = EventModelConverter
		    .asApiDeviceCommandInvocation(gresponse.getInvocation());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION_FOR_ASSIGNMENT,
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationsForAssignment(java.util.UUID,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForAssignment(UUID deviceAssignmentId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this,
		    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Criteria", criteria));
	    GListCommandInvocationsForAssignmentRequest.Builder grequest = GListCommandInvocationsForAssignmentRequest
		    .newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandInvocationsForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandInvocationsForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_ASSIGNMENT,
			    grequest.build()));
	    ISearchResults<IDeviceCommandInvocation> results = EventModelConverter
		    .asApiDeviceCommandInvocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_ASSIGNMENT,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationsForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_AREAS,
		    DebugParameter.create("Area Ids", areaIds), DebugParameter.create("Criteria", criteria));
	    GListCommandInvocationsForAreasRequest.Builder grequest = GListCommandInvocationsForAreasRequest
		    .newBuilder();
	    grequest.addAllAreaIds(CommonModelConverter.asGrpcUuids(areaIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandInvocationsForAreasResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandInvocationsForAreas(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_AREAS, grequest.build()));
	    ISearchResults<IDeviceCommandInvocation> results = EventModelConverter
		    .asApiDeviceCommandInvocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_AREAS,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_AREAS, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationResponses(java.util.UUID, java.util.UUID)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandInvocationResponses(UUID deviceId, UUID invocationId)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INVOCATION,
		    DebugParameter.create("Device Id", deviceId), DebugParameter.create("Invocation Id", invocationId));
	    GListCommandResponsesForInvocationRequest.Builder grequest = GListCommandResponsesForInvocationRequest
		    .newBuilder();
	    grequest.setDeviceId(CommonModelConverter.asGrpcUuid(deviceId));
	    grequest.setInvocationEventId(CommonModelConverter.asGrpcUuid(invocationId));
	    GListCommandResponsesForInvocationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForInvocation(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INVOCATION, grequest.build()));
	    ISearchResults<IDeviceCommandResponse> results = EventModelConverter
		    .asApiDeviceCommandResponseSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INVOCATION,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INVOCATION, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * addDeviceCommandResponse(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest)
     */
    @Override
    public IDeviceCommandResponse addDeviceCommandResponse(UUID deviceAssignmentId,
	    IDeviceCommandResponseCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddCommandResponseForAssignmentRequest.Builder grequest = GAddCommandResponseForAssignmentRequest
		    .newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceCommandResponseCreateRequest(request));
	    GAddCommandResponseForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addCommandResponseForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE_FOR_ASSIGNMENT, grequest.build()));
	    IDeviceCommandResponse response = EventModelConverter.asApiDeviceCommandResponse(gresponse.getResponse());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE_FOR_ASSIGNMENT,
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandResponsesForAssignment(java.util.UUID,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForAssignment(UUID deviceAssignmentId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Criteria", criteria));
	    GListCommandResponsesForAssignmentRequest.Builder grequest = GListCommandResponsesForAssignmentRequest
		    .newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandResponsesForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_ASSIGNMENT, grequest.build()));
	    ISearchResults<IDeviceCommandResponse> results = EventModelConverter
		    .asApiDeviceCommandResponseSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_ASSIGNMENT,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandResponsesForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_AREAS,
		    DebugParameter.create("Area Ids", areaIds), DebugParameter.create("Criteria", criteria));
	    GListCommandResponsesForAreasRequest.Builder grequest = GListCommandResponsesForAreasRequest.newBuilder();
	    grequest.addAllAreaIds(CommonModelConverter.asGrpcUuids(areaIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandResponsesForAreasResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForAreas(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_AREAS, grequest.build()));
	    ISearchResults<IDeviceCommandResponse> results = EventModelConverter
		    .asApiDeviceCommandResponseSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_AREAS,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_AREAS, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.event.IDeviceEventManagement#addDeviceStateChange(
     * java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest)
     */
    @Override
    public IDeviceStateChange addDeviceStateChange(UUID deviceAssignmentId, IDeviceStateChangeCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddStateChangeForAssignmentRequest.Builder grequest = GAddStateChangeForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceStateChangeCreateRequest(request));
	    GAddStateChangeForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addStateChangeForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE_FOR_ASSIGNMENT, grequest.build()));
	    IDeviceStateChange response = EventModelConverter.asApiDeviceStateChange(gresponse.getStateChange());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE_FOR_ASSIGNMENT,
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChangesForAssignment(java.util.UUID,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForAssignment(UUID deviceAssignmentId,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Criteria", criteria));
	    GListStateChangesForAssignmentRequest.Builder grequest = GListStateChangesForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListStateChangesForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listStateChangesForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENT, grequest.build()));
	    ISearchResults<IDeviceStateChange> results = EventModelConverter
		    .asApiDeviceStateChangeSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENT,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENT, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChangesForAreas(java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForAreas(List<UUID> areaIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_AREAS,
		    DebugParameter.create("Area Ids", areaIds), DebugParameter.create("Criteria", criteria));
	    GListStateChangesForAreasRequest.Builder grequest = GListStateChangesForAreasRequest.newBuilder();
	    grequest.addAllAreaIds(CommonModelConverter.asGrpcUuids(areaIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListStateChangesForAreasResponse gresponse = getGrpcChannel().getBlockingStub().listStateChangesForAreas(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_AREAS,
			    grequest.build()));
	    ISearchResults<IDeviceStateChange> results = EventModelConverter
		    .asApiDeviceStateChangeSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_AREAS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_AREAS,
		    t);
	}
    }
}