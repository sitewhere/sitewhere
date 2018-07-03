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

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.MultitenantApiChannel;
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
import com.sitewhere.grpc.service.GAddMeasurementRequest;
import com.sitewhere.grpc.service.GAddMeasurementResponse;
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
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tracing.ITracerProvider;

import io.grpc.stub.StreamObserver;

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
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * addDeviceEventBatch(java.util.UUID,
     * com.sitewhere.spi.device.event.IDeviceEventBatch,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void addDeviceEventBatch(UUID deviceAssignmentId, IDeviceEventBatch batch,
	    StreamObserver<IDeviceEventBatchResponse> observer) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddDeviceEventBatchMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Batch", batch));
	GAddDeviceEventBatchRequest.Builder grequest = GAddDeviceEventBatchRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	grequest.setRequest(EventModelConverter.asGrpcDeviceEventBatch(batch));
	getGrpcChannel().getAsyncStub().addDeviceEventBatch(GrpcUtils
		.logGrpcClientRequest(DeviceEventManagementGrpc.getAddDeviceEventBatchMethod(), grequest.build()),
		new StreamObserver<GAddDeviceEventBatchResponse>() {

		    @Override
		    public void onNext(GAddDeviceEventBatchResponse gresponse) {
			try {
			    IDeviceEventBatchResponse response = EventModelConverter
				    .asApiDeviceEventBatchResponse(gresponse.getResponse());
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddDeviceEventBatchMethod(),
				    response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getAddDeviceEventBatchMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * getDeviceEventById(java.util.UUID, java.util.UUID,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceEventById(UUID deviceId, UUID eventId, StreamObserver<IDeviceEvent> observer)
	    throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getGetDeviceEventByIdMethod(),
		DebugParameter.create("Device Id", deviceId), DebugParameter.create("Event Id", eventId));
	GGetDeviceEventByIdRequest.Builder grequest = GGetDeviceEventByIdRequest.newBuilder();
	grequest.setDeviceId(CommonModelConverter.asGrpcUuid(deviceId));
	grequest.setEventId(CommonModelConverter.asGrpcUuid(eventId));
	getGrpcChannel().getAsyncStub().getDeviceEventById(GrpcUtils
		.logGrpcClientRequest(DeviceEventManagementGrpc.getGetDeviceEventByIdMethod(), grequest.build()),
		new StreamObserver<GGetDeviceEventByIdResponse>() {

		    @Override
		    public void onNext(GGetDeviceEventByIdResponse gresponse) {
			try {
			    IDeviceEvent response = (gresponse.hasEvent())
				    ? EventModelConverter.asApiGenericDeviceEvent(gresponse.getEvent())
				    : null;
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getGetDeviceEventByIdMethod(),
				    response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getGetDeviceEventByIdMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * getDeviceEventByAlternateId(java.util.UUID, java.lang.String,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceEventByAlternateId(UUID deviceId, String alternateId, StreamObserver<IDeviceEvent> observer)
	    throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod(),
		DebugParameter.create("Alternate Id", alternateId));
	GGetDeviceEventByAlternateIdRequest.Builder grequest = GGetDeviceEventByAlternateIdRequest.newBuilder();
	grequest.setAlternateId(alternateId);
	getGrpcChannel().getAsyncStub().getDeviceEventByAlternateId(
		GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod(),
			grequest.build()),
		new StreamObserver<GGetDeviceEventByAlternateIdResponse>() {

		    @Override
		    public void onNext(GGetDeviceEventByAlternateIdResponse gresponse) {
			try {
			    IDeviceEvent response = (gresponse.hasEvent())
				    ? EventModelConverter.asApiGenericDeviceEvent(gresponse.getEvent())
				    : null;
			    GrpcUtils.logClientMethodResponse(
				    DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod(), response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * addDeviceMeasurements(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementsCreateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void addDeviceMeasurements(UUID deviceAssignmentId, IDeviceMeasurementCreateRequest measurements,
	    StreamObserver<IDeviceMeasurement> observer) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddMeasurementMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId),
		DebugParameter.create("Request", measurements));
	GAddMeasurementRequest.Builder grequest = GAddMeasurementRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	grequest.setRequest(EventModelConverter.asGrpcDeviceMeasurementCreateRequest(measurements));
	getGrpcChannel().getAsyncStub().addMeasurement(
		GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getAddMeasurementMethod(), grequest.build()),
		new StreamObserver<GAddMeasurementResponse>() {

		    @Override
		    public void onNext(GAddMeasurementResponse gresponse) {
			try {
			    IDeviceMeasurement response = EventModelConverter
				    .asApiDeviceMeasurement(gresponse.getMeasurement());
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddMeasurementMethod(),
				    response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getAddMeasurementMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * listDeviceMeasurementsForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceMeasurementsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceMeasurement>> observer)
	    throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListMeasurementsForIndexMethod(),
		DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		DebugParameter.create("Criteria", criteria));
	GListMeasurementsForIndexRequest.Builder grequest = GListMeasurementsForIndexRequest.newBuilder();
	grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	getGrpcChannel().getAsyncStub().listMeasurementsForIndex(GrpcUtils
		.logGrpcClientRequest(DeviceEventManagementGrpc.getListMeasurementsForIndexMethod(), grequest.build()),
		new StreamObserver<GListMeasurementsForIndexResponse>() {

		    @Override
		    public void onNext(GListMeasurementsForIndexResponse gresponse) {
			try {
			    ISearchResults<IDeviceMeasurement> response = EventModelConverter
				    .asApiDeviceMeasurementSearchResults(gresponse.getResults());
			    GrpcUtils.logClientMethodResponse(
				    DeviceEventManagementGrpc.getListMeasurementsForIndexMethod(), response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getListMeasurementsForIndexMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * addDeviceLocation(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void addDeviceLocation(UUID deviceAssignmentId, IDeviceLocationCreateRequest request,
	    StreamObserver<IDeviceLocation> observer) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddLocationMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Request", request));
	GAddLocationRequest.Builder grequest = GAddLocationRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	grequest.setRequest(EventModelConverter.asGrpcDeviceLocationCreateRequest(request));
	getGrpcChannel().getAsyncStub().addLocation(
		GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getAddLocationMethod(), grequest.build()),
		new StreamObserver<GAddLocationResponse>() {

		    @Override
		    public void onNext(GAddLocationResponse gresponse) {
			try {
			    IDeviceLocation response = EventModelConverter.asApiDeviceLocation(gresponse.getLocation());
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddLocationMethod(),
				    response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils
				    .handleClientMethodException(DeviceEventManagementGrpc.getAddLocationMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * listDeviceLocationsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceLocationsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceLocation>> observer)
	    throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListLocationsForIndexMethod(),
		DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		DebugParameter.create("Criteria", criteria));
	GListLocationsForIndexRequest.Builder grequest = GListLocationsForIndexRequest.newBuilder();
	grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	getGrpcChannel().getAsyncStub().listLocationsForIndex(GrpcUtils
		.logGrpcClientRequest(DeviceEventManagementGrpc.getListLocationsForIndexMethod(), grequest.build()),
		new StreamObserver<GListLocationsForIndexResponse>() {

		    @Override
		    public void onNext(GListLocationsForIndexResponse gresponse) {
			try {
			    ISearchResults<IDeviceLocation> response = EventModelConverter
				    .asApiDeviceLocationSearchResults(gresponse.getResults());
			    GrpcUtils.logClientMethodResponse(
				    DeviceEventManagementGrpc.getListLocationsForIndexMethod(), response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getListLocationsForIndexMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * addDeviceAlert(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void addDeviceAlert(UUID deviceAssignmentId, IDeviceAlertCreateRequest request,
	    StreamObserver<IDeviceAlert> observer) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddAlertMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Request", request));
	GAddAlertRequest.Builder grequest = GAddAlertRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	grequest.setRequest(EventModelConverter.asGrpcDeviceAlertCreateRequest(request));
	getGrpcChannel().getAsyncStub().addAlert(
		GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getAddAlertMethod(), grequest.build()),
		new StreamObserver<GAddAlertResponse>() {

		    @Override
		    public void onNext(GAddAlertResponse gresponse) {
			try {
			    IDeviceAlert response = EventModelConverter.asApiDeviceAlert(gresponse.getAlert());
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddAlertMethod(), response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils
				    .handleClientMethodException(DeviceEventManagementGrpc.getAddAlertMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * listDeviceAlertsForIndex(com.sitewhere.spi.device.event.DeviceEventIndex,
     * java.util.List, com.sitewhere.spi.search.IDateRangeSearchCriteria,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceAlertsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceAlert>> observer)
	    throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListAlertsForIndexMethod(),
		DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		DebugParameter.create("Criteria", criteria));
	GListAlertsForIndexRequest.Builder grequest = GListAlertsForIndexRequest.newBuilder();
	grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	getGrpcChannel().getAsyncStub().listAlertsForIndex(GrpcUtils
		.logGrpcClientRequest(DeviceEventManagementGrpc.getListAlertsForIndexMethod(), grequest.build()),
		new StreamObserver<GListAlertsForIndexResponse>() {

		    @Override
		    public void onNext(GListAlertsForIndexResponse gresponse) {
			try {
			    ISearchResults<IDeviceAlert> response = EventModelConverter
				    .asApiDeviceAlertSearchResults(gresponse.getResults());
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getListAlertsForIndexMethod(),
				    response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getListAlertsForIndexMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * addDeviceStreamData(java.util.UUID,
     * com.sitewhere.spi.device.streaming.IDeviceStream,
     * com.sitewhere.spi.device.event.request.IDeviceStreamDataCreateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void addDeviceStreamData(UUID deviceAssignmentId, IDeviceStream stream,
	    IDeviceStreamDataCreateRequest request, StreamObserver<IDeviceStreamData> observer)
	    throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddStreamDataForAssignmentMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Stream", stream),
		DebugParameter.create("Request", request));
	GAddStreamDataForAssignmentRequest.Builder grequest = GAddStreamDataForAssignmentRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	grequest.setDeviceStream(DeviceModelConverter.asGrpcDeviceStream(stream));
	grequest.setRequest(EventModelConverter.asGrpcDeviceStreamDataCreateRequest(request));
	getGrpcChannel().getAsyncStub().addStreamDataForAssignment(
		GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getAddStreamDataForAssignmentMethod(),
			grequest.build()),
		new StreamObserver<GAddStreamDataForAssignmentResponse>() {

		    @Override
		    public void onNext(GAddStreamDataForAssignmentResponse gresponse) {
			try {
			    IDeviceStreamData response = EventModelConverter
				    .asApiDeviceStreamData(gresponse.getStreamData());
			    GrpcUtils.logClientMethodResponse(
				    DeviceEventManagementGrpc.getAddStreamDataForAssignmentMethod(), response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getAddStreamDataForAssignmentMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * getDeviceStreamData(java.util.UUID, java.lang.String, long,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceStreamData(UUID deviceAssignmentId, String streamId, long sequenceNumber,
	    StreamObserver<IDeviceStreamData> observer) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getGetStreamDataForAssignmentMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId),
		DebugParameter.create("Stream Id", streamId),
		DebugParameter.create("Sequence Number", String.valueOf(sequenceNumber)));
	GGetStreamDataForAssignmentRequest.Builder grequest = GGetStreamDataForAssignmentRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	grequest.setStreamId(streamId);
	grequest.setSequenceNumber(sequenceNumber);
	getGrpcChannel().getAsyncStub().getStreamDataForAssignment(
		GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getGetStreamDataForAssignmentMethod(),
			grequest.build()),
		new StreamObserver<GGetStreamDataForAssignmentResponse>() {

		    @Override
		    public void onNext(GGetStreamDataForAssignmentResponse gresponse) {
			try {
			    IDeviceStreamData response = EventModelConverter
				    .asApiDeviceStreamData(gresponse.getStreamData());
			    GrpcUtils.logClientMethodResponse(
				    DeviceEventManagementGrpc.getGetStreamDataForAssignmentMethod(), response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getGetStreamDataForAssignmentMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * listDeviceStreamDataForAssignment(java.util.UUID, java.lang.String,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceStreamDataForAssignment(UUID assignmentId, String streamId, IDateRangeSearchCriteria criteria,
	    StreamObserver<ISearchResults<IDeviceStreamData>> observer) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListStreamDataForAssignmentMethod(),
		DebugParameter.create("Assignment Id", assignmentId), DebugParameter.create("Stream Id", streamId),
		DebugParameter.create("Criteria", criteria));
	GListStreamDataForAssignmentRequest.Builder grequest = GListStreamDataForAssignmentRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(assignmentId));
	grequest.setStreamId(streamId);
	grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	getGrpcChannel().getAsyncStub().listStreamDataForAssignment(
		GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getListStreamDataForAssignmentMethod(),
			grequest.build()),
		new StreamObserver<GListStreamDataForAssignmentResponse>() {

		    @Override
		    public void onNext(GListStreamDataForAssignmentResponse gresponse) {
			try {
			    ISearchResults<IDeviceStreamData> response = EventModelConverter
				    .asApiDeviceStreamDataSearchResults(gresponse.getResults());
			    GrpcUtils.logClientMethodResponse(
				    DeviceEventManagementGrpc.getListStreamDataForAssignmentMethod(), response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getListStreamDataForAssignmentMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * addDeviceCommandInvocation(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void addDeviceCommandInvocation(UUID deviceAssignmentId, IDeviceCommandInvocationCreateRequest request,
	    StreamObserver<IDeviceCommandInvocation> observer) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddCommandInvocationMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Request", request));
	GAddCommandInvocationRequest.Builder grequest = GAddCommandInvocationRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	grequest.setRequest(EventModelConverter.asGrpcDeviceCommandInvocationCreateRequest(request));
	getGrpcChannel().getAsyncStub().addCommandInvocation(GrpcUtils
		.logGrpcClientRequest(DeviceEventManagementGrpc.getAddCommandInvocationMethod(), grequest.build()),
		new StreamObserver<GAddCommandInvocationResponse>() {

		    @Override
		    public void onNext(GAddCommandInvocationResponse gresponse) {
			try {
			    IDeviceCommandInvocation response = EventModelConverter
				    .asApiDeviceCommandInvocation(gresponse.getInvocation());
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddCommandInvocationMethod(),
				    response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getAddCommandInvocationMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * listDeviceCommandInvocationsForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceCommandInvocationsForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceCommandInvocation>> observer)
	    throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod(),
		DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		DebugParameter.create("Criteria", criteria));
	GListCommandInvocationsForIndexRequest.Builder grequest = GListCommandInvocationsForIndexRequest.newBuilder();
	grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	getGrpcChannel().getAsyncStub()
		.listCommandInvocationsForIndex(
			GrpcUtils.logGrpcClientRequest(
				DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod(), grequest.build()),
			new StreamObserver<GListCommandInvocationsForIndexResponse>() {

			    @Override
			    public void onNext(GListCommandInvocationsForIndexResponse gresponse) {
				try {
				    ISearchResults<IDeviceCommandInvocation> response = EventModelConverter
					    .asApiDeviceCommandInvocationSearchResults(gresponse.getResults());
				    GrpcUtils.logClientMethodResponse(
					    DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod(),
					    response);
				    observer.onNext(response);
				} catch (Throwable t) {
				    observer.onError(GrpcUtils.handleClientMethodException(
					    DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod(), t));
				}
			    }

			    @Override
			    public void onError(Throwable t) {
				observer.onError(t);
			    }

			    @Override
			    public void onCompleted() {
				observer.onCompleted();
			    }
			});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * listDeviceCommandInvocationResponses(java.util.UUID, java.util.UUID,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceCommandInvocationResponses(UUID deviceId, UUID invocationId,
	    StreamObserver<ISearchResults<IDeviceCommandResponse>> observer) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod(),
		DebugParameter.create("Device Id", deviceId), DebugParameter.create("Invocation Id", invocationId));
	GListCommandResponsesForInvocationRequest.Builder grequest = GListCommandResponsesForInvocationRequest
		.newBuilder();
	grequest.setDeviceId(CommonModelConverter.asGrpcUuid(deviceId));
	grequest.setInvocationEventId(CommonModelConverter.asGrpcUuid(invocationId));
	getGrpcChannel().getAsyncStub()
		.listCommandResponsesForInvocation(GrpcUtils.logGrpcClientRequest(
			DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod(), grequest.build()),
			new StreamObserver<GListCommandResponsesForInvocationResponse>() {

			    @Override
			    public void onNext(GListCommandResponsesForInvocationResponse gresponse) {
				try {
				    ISearchResults<IDeviceCommandResponse> response = EventModelConverter
					    .asApiDeviceCommandResponseSearchResults(gresponse.getResults());
				    GrpcUtils.logClientMethodResponse(
					    DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod(),
					    response);
				    observer.onNext(response);
				} catch (Throwable t) {
				    observer.onError(GrpcUtils.handleClientMethodException(
					    DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod(), t));
				}
			    }

			    @Override
			    public void onError(Throwable t) {
				observer.onError(t);
			    }

			    @Override
			    public void onCompleted() {
				observer.onCompleted();
			    }
			});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * addDeviceCommandResponse(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void addDeviceCommandResponse(UUID deviceAssignmentId, IDeviceCommandResponseCreateRequest request,
	    StreamObserver<IDeviceCommandResponse> observer) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddCommandResponseMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Request", request));
	GAddCommandResponseRequest.Builder grequest = GAddCommandResponseRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	grequest.setRequest(EventModelConverter.asGrpcDeviceCommandResponseCreateRequest(request));
	getGrpcChannel().getAsyncStub().addCommandResponse(GrpcUtils
		.logGrpcClientRequest(DeviceEventManagementGrpc.getAddCommandResponseMethod(), grequest.build()),
		new StreamObserver<GAddCommandResponseResponse>() {

		    @Override
		    public void onNext(GAddCommandResponseResponse gresponse) {
			try {
			    IDeviceCommandResponse response = EventModelConverter
				    .asApiDeviceCommandResponse(gresponse.getResponse());
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddCommandResponseMethod(),
				    response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getAddCommandResponseMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * listDeviceCommandResponsesForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceCommandResponsesForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceCommandResponse>> observer)
	    throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod(),
		DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		DebugParameter.create("Criteria", criteria));
	GListCommandResponsesForIndexRequest.Builder grequest = GListCommandResponsesForIndexRequest.newBuilder();
	grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	getGrpcChannel().getAsyncStub()
		.listCommandResponsesForIndex(
			GrpcUtils.logGrpcClientRequest(
				DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod(), grequest.build()),
			new StreamObserver<GListCommandResponsesForIndexResponse>() {

			    @Override
			    public void onNext(GListCommandResponsesForIndexResponse gresponse) {
				try {
				    ISearchResults<IDeviceCommandResponse> response = EventModelConverter
					    .asApiDeviceCommandResponseSearchResults(gresponse.getResults());
				    GrpcUtils.logClientMethodResponse(
					    DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod(),
					    response);
				    observer.onNext(response);
				} catch (Throwable t) {
				    observer.onError(GrpcUtils.handleClientMethodException(
					    DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod(), t));
				}
			    }

			    @Override
			    public void onError(Throwable t) {
				observer.onError(t);
			    }

			    @Override
			    public void onCompleted() {
				observer.onCompleted();
			    }
			});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * addDeviceStateChange(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void addDeviceStateChange(UUID deviceAssignmentId, IDeviceStateChangeCreateRequest request,
	    StreamObserver<IDeviceStateChange> observer) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddStateChangeMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Request", request));
	GAddStateChangeRequest.Builder grequest = GAddStateChangeRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	grequest.setRequest(EventModelConverter.asGrpcDeviceStateChangeCreateRequest(request));
	getGrpcChannel().getAsyncStub().addStateChange(
		GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getAddStateChangeMethod(), grequest.build()),
		new StreamObserver<GAddStateChangeResponse>() {

		    @Override
		    public void onNext(GAddStateChangeResponse gresponse) {
			try {
			    IDeviceStateChange response = EventModelConverter
				    .asApiDeviceStateChange(gresponse.getStateChange());
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddStateChangeMethod(),
				    response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getAddStateChangeMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }

    /*
     * @see com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel#
     * listDeviceStateChangesForIndex(com.sitewhere.spi.device.event.
     * DeviceEventIndex, java.util.List,
     * com.sitewhere.spi.search.IDateRangeSearchCriteria,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceStateChangesForIndex(DeviceEventIndex index, List<UUID> entityIds,
	    IDateRangeSearchCriteria criteria, StreamObserver<ISearchResults<IDeviceStateChange>> observer)
	    throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListStateChangesForIndexMethod(),
		DebugParameter.create("Index", index), DebugParameter.create("Entity Ids", entityIds),
		DebugParameter.create("Criteria", criteria));
	GListStateChangesForIndexRequest.Builder grequest = GListStateChangesForIndexRequest.newBuilder();
	grequest.setIndex(EventModelConverter.asGrpcDeviceEventIndex(index));
	grequest.addAllEntityIds(CommonModelConverter.asGrpcUuids(entityIds));
	grequest.setCriteria(CommonModelConverter.asGrpcDateRangeSearchCriteria(criteria));
	getGrpcChannel().getAsyncStub().listStateChangesForIndex(GrpcUtils
		.logGrpcClientRequest(DeviceEventManagementGrpc.getListStateChangesForIndexMethod(), grequest.build()),
		new StreamObserver<GListStateChangesForIndexResponse>() {

		    @Override
		    public void onNext(GListStateChangesForIndexResponse gresponse) {
			try {
			    ISearchResults<IDeviceStateChange> response = EventModelConverter
				    .asApiDeviceStateChangeSearchResults(gresponse.getResults());
			    GrpcUtils.logClientMethodResponse(
				    DeviceEventManagementGrpc.getListStateChangesForIndexMethod(), response);
			    observer.onNext(response);
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getListStateChangesForIndexMethod(), t));
			}
		    }

		    @Override
		    public void onError(Throwable t) {
			observer.onError(t);
		    }

		    @Override
		    public void onCompleted() {
			observer.onCompleted();
		    }
		});
    }
}