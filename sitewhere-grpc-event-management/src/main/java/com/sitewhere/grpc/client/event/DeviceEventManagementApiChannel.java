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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddDeviceEventBatchMethod(),
		    DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Batch", batch));
	    GAddDeviceEventBatchRequest.Builder grequest = GAddDeviceEventBatchRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceEventBatch(batch));
	    GAddDeviceEventBatchResponse gresponse = getGrpcChannel().getBlockingStub().addDeviceEventBatch(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.getAddDeviceEventBatchMethod(), grequest.build()));
	    IDeviceEventBatchResponse response = EventModelConverter
		    .asApiDeviceEventBatchResponse(gresponse.getResponse());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddDeviceEventBatchMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddDeviceEventBatchMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getGetDeviceEventByIdMethod(),
		    DebugParameter.create("Device Id", deviceId), DebugParameter.create("Event Id", eventId));
	    GGetDeviceEventByIdRequest.Builder grequest = GGetDeviceEventByIdRequest.newBuilder();
	    grequest.setDeviceId(CommonModelConverter.asGrpcUuid(deviceId));
	    grequest.setEventId(CommonModelConverter.asGrpcUuid(eventId));
	    GGetDeviceEventByIdResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceEventById(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.getGetDeviceEventByIdMethod(), grequest.build()));
	    IDeviceEvent response = (gresponse.hasEvent())
		    ? EventModelConverter.asApiGenericDeviceEvent(gresponse.getEvent())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getGetDeviceEventByIdMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getGetDeviceEventByIdMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * getDeviceEventByAlternateId(java.util.UUID, java.lang.String)
     */
    @Override
    public IDeviceEvent getDeviceEventByAlternateId(UUID deviceId, String alternateId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod(),
		    DebugParameter.create("Alternate Id", alternateId));
	    GGetDeviceEventByAlternateIdRequest.Builder grequest = GGetDeviceEventByAlternateIdRequest.newBuilder();
	    grequest.setAlternateId(alternateId);
	    GGetDeviceEventByAlternateIdResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceEventByAlternateId(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod(), grequest.build()));
	    IDeviceEvent response = (gresponse.hasEvent())
		    ? EventModelConverter.asApiGenericDeviceEvent(gresponse.getEvent())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod(),
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.event.IDeviceEventManagement#
     * streamDeviceAssignmentCreateEvents()
     */
    @Override
    public Processor<IDeviceAssignmentEventCreateRequest, IEventStreamAck> streamDeviceAssignmentCreateEvents() {
	GrpcUtils.handleClientMethodEntry(this,
		DeviceEventManagementGrpc.getStreamDeviceAssignmentEventCreateRequestsMethod());
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddMeasurementsMethod(),
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", measurements));
	    GAddMeasurementsRequest.Builder grequest = GAddMeasurementsRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceMeasurementsCreateRequest(measurements));
	    GAddMeasurementsResponse gresponse = getGrpcChannel().getBlockingStub().addMeasurements(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.getAddMeasurementsMethod(), grequest.build()));
	    IDeviceMeasurements response = EventModelConverter.asApiDeviceMeasurements(gresponse.getMeasurements());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddMeasurementsMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddMeasurementsMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListMeasurementsForIndexMethod(),
		    DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		    DebugParameter.create("Criteria", criteria));
	    GListMeasurementsForIndexRequest.Builder grequest = GListMeasurementsForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListMeasurementsForIndexResponse gresponse = getGrpcChannel().getBlockingStub().listMeasurementsForIndex(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getListMeasurementsForIndexMethod(),
			    grequest.build()));
	    ISearchResults<IDeviceMeasurements> results = EventModelConverter
		    .asApiDeviceMeasurementsSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListMeasurementsForIndexMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getListMeasurementsForIndexMethod(),
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddLocationMethod(),
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddLocationRequest.Builder grequest = GAddLocationRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceLocationCreateRequest(request));
	    GAddLocationResponse gresponse = getGrpcChannel().getBlockingStub().addLocation(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getAddLocationMethod(), grequest.build()));
	    IDeviceLocation response = EventModelConverter.asApiDeviceLocation(gresponse.getLocation());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddLocationMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddLocationMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListLocationsForIndexMethod(),
		    DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		    DebugParameter.create("Criteria", criteria));
	    GListLocationsForIndexRequest.Builder grequest = GListLocationsForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListLocationsForIndexResponse gresponse = getGrpcChannel().getBlockingStub().listLocationsForIndex(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getListLocationsForIndexMethod(),
			    grequest.build()));
	    ISearchResults<IDeviceLocation> results = EventModelConverter
		    .asApiDeviceLocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListLocationsForIndexMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getListLocationsForIndexMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddAlertMethod(),
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddAlertRequest.Builder grequest = GAddAlertRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceAlertCreateRequest(request));
	    GAddAlertResponse gresponse = getGrpcChannel().getBlockingStub().addAlert(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getAddAlertMethod(), grequest.build()));
	    IDeviceAlert response = EventModelConverter.asApiDeviceAlert(gresponse.getAlert());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddAlertMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddAlertMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListAlertsForIndexMethod(),
		    DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		    DebugParameter.create("Criteria", criteria));
	    GListAlertsForIndexRequest.Builder grequest = GListAlertsForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListAlertsForIndexResponse gresponse = getGrpcChannel().getBlockingStub().listAlertsForIndex(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.getListAlertsForIndexMethod(), grequest.build()));
	    ISearchResults<IDeviceAlert> results = EventModelConverter
		    .asApiDeviceAlertSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListAlertsForIndexMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getListAlertsForIndexMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddStreamDataForAssignmentMethod(),
		    DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Stream", stream),
		    DebugParameter.create("Request", request));
	    GAddStreamDataForAssignmentRequest.Builder grequest = GAddStreamDataForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setDeviceStream(DeviceModelConverter.asGrpcDeviceStream(stream));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceStreamDataCreateRequest(request));
	    GAddStreamDataForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addStreamDataForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.getAddStreamDataForAssignmentMethod(), grequest.build()));
	    IDeviceStreamData response = EventModelConverter.asApiDeviceStreamData(gresponse.getStreamData());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddStreamDataForAssignmentMethod(),
		    response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddStreamDataForAssignmentMethod(),
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getGetStreamDataForAssignmentMethod(),
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Stream Id", streamId),
		    DebugParameter.create("Sequence Number", String.valueOf(sequenceNumber)));
	    GGetStreamDataForAssignmentRequest.Builder grequest = GGetStreamDataForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setStreamId(streamId);
	    grequest.setSequenceNumber(sequenceNumber);
	    GGetStreamDataForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getStreamDataForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.getGetStreamDataForAssignmentMethod(), grequest.build()));
	    IDeviceStreamData results = EventModelConverter.asApiDeviceStreamData(gresponse.getStreamData());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getGetStreamDataForAssignmentMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getGetStreamDataForAssignmentMethod(),
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListStreamDataForAssignmentMethod(),
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Stream Id", streamId), DebugParameter.create("Criteria", criteria));
	    GListStreamDataForAssignmentRequest.Builder grequest = GListStreamDataForAssignmentRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setStreamId(streamId);
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListStreamDataForAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listStreamDataForAssignment(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.getListStreamDataForAssignmentMethod(), grequest.build()));
	    ISearchResults<IDeviceStreamData> results = EventModelConverter
		    .asApiDeviceStreamDataSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListStreamDataForAssignmentMethod(),
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.getListStreamDataForAssignmentMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddCommandInvocationMethod(),
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddCommandInvocationRequest.Builder grequest = GAddCommandInvocationRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceCommandInvocationCreateRequest(request));
	    GAddCommandInvocationResponse gresponse = getGrpcChannel().getBlockingStub().addCommandInvocation(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.getAddCommandInvocationMethod(), grequest.build()));
	    IDeviceCommandInvocation response = EventModelConverter
		    .asApiDeviceCommandInvocation(gresponse.getInvocation());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddCommandInvocationMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddCommandInvocationMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod(),
		    DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		    DebugParameter.create("Criteria", criteria));
	    GListCommandInvocationsForIndexRequest.Builder grequest = GListCommandInvocationsForIndexRequest
		    .newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandInvocationsForIndexResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandInvocationsForIndex(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod(), grequest.build()));
	    ISearchResults<IDeviceCommandInvocation> results = EventModelConverter
		    .asApiDeviceCommandInvocationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod(),
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod(),
		    DebugParameter.create("Device Id", deviceId), DebugParameter.create("Invocation Id", invocationId));
	    GListCommandResponsesForInvocationRequest.Builder grequest = GListCommandResponsesForInvocationRequest
		    .newBuilder();
	    grequest.setDeviceId(CommonModelConverter.asGrpcUuid(deviceId));
	    grequest.setInvocationEventId(CommonModelConverter.asGrpcUuid(invocationId));
	    GListCommandResponsesForInvocationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForInvocation(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod(), grequest.build()));
	    ISearchResults<IDeviceCommandResponse> results = EventModelConverter
		    .asApiDeviceCommandResponseSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod(),
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(
		    DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddCommandResponseMethod(),
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddCommandResponseRequest.Builder grequest = GAddCommandResponseRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceCommandResponseCreateRequest(request));
	    GAddCommandResponseResponse gresponse = getGrpcChannel().getBlockingStub().addCommandResponse(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.getAddCommandResponseMethod(), grequest.build()));
	    IDeviceCommandResponse response = EventModelConverter.asApiDeviceCommandResponse(gresponse.getResponse());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddCommandResponseMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddCommandResponseMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod(),
		    DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		    DebugParameter.create("Criteria", criteria));
	    GListCommandResponsesForIndexRequest.Builder grequest = GListCommandResponsesForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListCommandResponsesForIndexResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCommandResponsesForIndex(GrpcUtils.logGrpcClientRequest(
			    DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod(), grequest.build()));
	    ISearchResults<IDeviceCommandResponse> results = EventModelConverter
		    .asApiDeviceCommandResponseSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod(),
		    results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils
		    .handleClientMethodException(DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddStateChangeMethod(),
		    DebugParameter.create("Assignment Id", deviceAssignmentId),
		    DebugParameter.create("Request", request));
	    GAddStateChangeRequest.Builder grequest = GAddStateChangeRequest.newBuilder();
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    grequest.setRequest(EventModelConverter.asGrpcDeviceStateChangeCreateRequest(request));
	    GAddStateChangeResponse gresponse = getGrpcChannel().getBlockingStub().addStateChange(GrpcUtils
		    .logGrpcClientRequest(DeviceEventManagementGrpc.getAddStateChangeMethod(), grequest.build()));
	    IDeviceStateChange response = EventModelConverter.asApiDeviceStateChange(gresponse.getStateChange());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddStateChangeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getAddStateChangeMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListStateChangesForIndexMethod(),
		    DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		    DebugParameter.create("Criteria", criteria));
	    GListStateChangesForIndexRequest.Builder grequest = GListStateChangesForIndexRequest.newBuilder();
	    grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	    grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	    grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	    GListStateChangesForIndexResponse gresponse = getGrpcChannel().getBlockingStub().listStateChangesForIndex(
		    GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getListStateChangesForIndexMethod(),
			    grequest.build()));
	    ISearchResults<IDeviceStateChange> results = EventModelConverter
		    .asApiDeviceStateChangeSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListStateChangesForIndexMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceEventManagementGrpc.getListStateChangesForIndexMethod(),
		    t);
	}
    }
}