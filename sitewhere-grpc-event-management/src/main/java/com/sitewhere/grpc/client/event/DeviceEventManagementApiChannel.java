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
import com.sitewhere.grpc.service.GAddAlertRequest;
import com.sitewhere.grpc.service.GAddAlertResponse;
import com.sitewhere.grpc.service.GAddCommandInvocationRequest;
import com.sitewhere.grpc.service.GAddCommandInvocationResponse;
import com.sitewhere.grpc.service.GAddCommandResponseRequest;
import com.sitewhere.grpc.service.GAddCommandResponseResponse;
import com.sitewhere.grpc.service.GAddDeviceEventBatchRequest;
import com.sitewhere.grpc.service.GAddDeviceEventBatchResponse;
import com.sitewhere.grpc.service.GAddLocationRequest;
import com.sitewhere.grpc.service.GAddLocationResponse;
import com.sitewhere.grpc.service.GAddMeasurementsRequest;
import com.sitewhere.grpc.service.GAddMeasurementsResponse;
import com.sitewhere.grpc.service.GAddStateChangeRequest;
import com.sitewhere.grpc.service.GAddStateChangeResponse;
import com.sitewhere.grpc.service.GAddStreamDataForAssignmentRequest;
import com.sitewhere.grpc.service.GAddStreamDataForAssignmentResponse;
import com.sitewhere.grpc.service.GGetDeviceEventByAlternateIdRequest;
import com.sitewhere.grpc.service.GGetDeviceEventByAlternateIdResponse;
import com.sitewhere.grpc.service.GGetDeviceEventByIdRequest;
import com.sitewhere.grpc.service.GGetDeviceEventByIdResponse;
import com.sitewhere.grpc.service.GGetStreamDataForAssignmentRequest;
import com.sitewhere.grpc.service.GGetStreamDataForAssignmentResponse;
import com.sitewhere.grpc.service.GListAlertsForIndexRequest;
import com.sitewhere.grpc.service.GListAlertsForIndexResponse;
import com.sitewhere.grpc.service.GListCommandInvocationsForIndexRequest;
import com.sitewhere.grpc.service.GListCommandInvocationsForIndexResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForIndexRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForIndexResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForInvocationRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForInvocationResponse;
import com.sitewhere.grpc.service.GListLocationsForIndexRequest;
import com.sitewhere.grpc.service.GListLocationsForIndexResponse;
import com.sitewhere.grpc.service.GListMeasurementsForIndexRequest;
import com.sitewhere.grpc.service.GListMeasurementsForIndexResponse;
import com.sitewhere.grpc.service.GListStateChangesForIndexRequest;
import com.sitewhere.grpc.service.GListStateChangesForIndexResponse;
import com.sitewhere.grpc.service.GListStreamDataForAssignmentRequest;
import com.sitewhere.grpc.service.GListStreamDataForAssignmentResponse;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.event.DeviceEventIndex;
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
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", measurements));
	    GAddMeasurementsRequest.Builder grequest = GAddMeasurementsRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceMeasurementsCreateRequest(measurements));
	    GAddMeasurementsResponse gresponse = getGrpcChannel().getBlockingStub().addMeasurements(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS, grequest.build()));
	    IDeviceMeasurements response = EventModelConverter.asApiDeviceMeasurements(gresponse.getMeasurements());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceMeasurementsForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceMeasurements> listDeviceMeasurementsForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_INDEX,
		    DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		    DebugParameter.create("Criteria", criteria));
	    GListMeasurementsForIndexRequest.Builder grequest = GListMeasurementsForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListMeasurementsForIndexResponse gresponse = getGrpcChannel().getBlockingStub().listMeasurementsForIndex(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_INDEX,
			    grequest.build()));
	    ISearchResults<IDeviceMeasurements> results = EventModelConverter
		    .asApiDeviceMeasurementsSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_INDEX, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_INDEX,
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
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_LOCATION,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddLocationRequest.Builder grequest = GAddLocationRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceLocationCreateRequest(request));
	    GAddLocationResponse gresponse = getGrpcChannel().getBlockingStub().addLocation(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_ADD_LOCATION, grequest.build()));
	    IDeviceLocation response = EventModelConverter.asApiDeviceLocation(gresponse.getLocation());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_LOCATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_LOCATION, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceLocationsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceLocation> listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_INDEX,
		    DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		    DebugParameter.create("Criteria", criteria));
	    GListLocationsForIndexRequest.Builder grequest = GListLocationsForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListLocationsForIndexResponse gresponse = getGrpcChannel().getBlockingStub().listLocationsForIndex(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_INDEX,
			    grequest.build()));
	    ISearchResults<IDeviceLocation> results = EventModelConverter
		    .asApiDeviceLocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_INDEX, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_INDEX, t);
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
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_ALERT,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddAlertRequest.Builder grequest = GAddAlertRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceAlertCreateRequest(request));
	    GAddAlertResponse gresponse = getGrpcChannel().getBlockingStub().addAlert(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_ADD_ALERT, grequest.build()));
	    IDeviceAlert response = EventModelConverter.asApiDeviceAlert(gresponse.getAlert());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_ALERT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_ALERT, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceAlertsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlert> listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_INDEX,
		    DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		    DebugParameter.create("Criteria", criteria));
	    GListAlertsForIndexRequest.Builder grequest = GListAlertsForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListAlertsForIndexResponse gresponse = getGrpcChannel().getBlockingStub().listAlertsForIndex(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_INDEX, grequest.build()));
	    ISearchResults<IDeviceAlert> results = EventModelConverter
		    .asApiDeviceAlertSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_INDEX, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_INDEX, t);
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
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddCommandInvocationRequest.Builder grequest = GAddCommandInvocationRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceCommandInvocationCreateRequest(request));
	    GAddCommandInvocationResponse gresponse = getGrpcChannel().getBlockingStub().addCommandInvocation(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION, grequest.build()));
	    IDeviceCommandInvocation response = EventModelConverter
		    .asApiDeviceCommandInvocation(gresponse.getInvocation());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandInvocationsForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandInvocation> listDeviceCommandInvocationsForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_INDEX,
		    DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		    DebugParameter.create("Criteria", criteria));
	    GListCommandInvocationsForIndexRequest.Builder grequest = GListCommandInvocationsForIndexRequest
		    .newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandInvocationsForIndexResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandInvocationsForIndex(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_INDEX, grequest.build()));
	    ISearchResults<IDeviceCommandInvocation> results = EventModelConverter
		    .asApiDeviceCommandInvocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_INDEX,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_INDEX, t);
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
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddCommandResponseRequest.Builder grequest = GAddCommandResponseRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceCommandResponseCreateRequest(request));
	    GAddCommandResponseResponse gresponse = getGrpcChannel().getBlockingStub().addCommandResponse(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE, grequest.build()));
	    IDeviceCommandResponse response = EventModelConverter.asApiDeviceCommandResponse(gresponse.getResponse());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceCommandResponsesForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommandResponse> listDeviceCommandResponsesForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INDEX,
		    DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		    DebugParameter.create("Criteria", criteria));
	    GListCommandResponsesForIndexRequest.Builder grequest = GListCommandResponsesForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandResponsesForIndexResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForIndex(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INDEX, grequest.build()));
	    ISearchResults<IDeviceCommandResponse> results = EventModelConverter
		    .asApiDeviceCommandResponseSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INDEX,
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INDEX, t);
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
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE,
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddStateChangeRequest.Builder grequest = GAddStateChangeRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceStateChangeCreateRequest(request));
	    GAddStateChangeResponse gresponse = getGrpcChannel().getBlockingStub().addStateChange(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE, grequest.build()));
	    IDeviceStateChange response = EventModelConverter.asApiDeviceStateChange(gresponse.getStateChange());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * listDeviceStateChangesForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStateChange> listDeviceStateChangesForIndex(DeviceEventIndex index,
	    List<UUID> entityIds, IDateRangeSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_INDEX,
		    DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		    DebugParameter.create("Criteria", criteria));
	    GListStateChangesForIndexRequest.Builder grequest = GListStateChangesForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListStateChangesForIndexResponse gresponse = getGrpcChannel().getBlockingStub().listStateChangesForIndex(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_INDEX,
			    grequest.build()));
	    ISearchResults<IDeviceStateChange> results = EventModelConverter
		    .asApiDeviceStateChangeSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_INDEX, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_INDEX,
		    t);
	}
    }
}