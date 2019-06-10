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
import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.client.common.tracing.DebugParameter;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.grpc.service.GAddAlertsRequest;
import com.sitewhere.grpc.service.GAddAlertsResponse;
import com.sitewhere.grpc.service.GAddCommandInvocationsRequest;
import com.sitewhere.grpc.service.GAddCommandInvocationsResponse;
import com.sitewhere.grpc.service.GAddCommandResponsesRequest;
import com.sitewhere.grpc.service.GAddCommandResponsesResponse;
import com.sitewhere.grpc.service.GAddDeviceEventBatchRequest;
import com.sitewhere.grpc.service.GAddDeviceEventBatchResponse;
import com.sitewhere.grpc.service.GAddLocationsRequest;
import com.sitewhere.grpc.service.GAddLocationsResponse;
import com.sitewhere.grpc.service.GAddMeasurementsRequest;
import com.sitewhere.grpc.service.GAddMeasurementsResponse;
import com.sitewhere.grpc.service.GAddStateChangesRequest;
import com.sitewhere.grpc.service.GAddStateChangesResponse;
import com.sitewhere.grpc.service.GGetDeviceEventByAlternateIdRequest;
import com.sitewhere.grpc.service.GGetDeviceEventByAlternateIdResponse;
import com.sitewhere.grpc.service.GGetDeviceEventByIdRequest;
import com.sitewhere.grpc.service.GGetDeviceEventByIdResponse;
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
import com.sitewhere.rest.model.device.event.DeviceAlert;
import com.sitewhere.rest.model.device.event.DeviceCommandInvocation;
import com.sitewhere.rest.model.device.event.DeviceCommandResponse;
import com.sitewhere.rest.model.device.event.DeviceLocation;
import com.sitewhere.rest.model.device.event.DeviceMeasurement;
import com.sitewhere.rest.model.device.event.DeviceStateChange;
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
import com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest;
import com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.grpc.GrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.search.IDateRangeSearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

import io.grpc.stub.StreamObserver;

/**
 * Supports SiteWhere device event management APIs on top of a
 * {@link DeviceEventManagementGrpcChannel}.
 */
public class DeviceEventManagementApiChannel extends MultitenantApiChannel<DeviceEventManagementGrpcChannel>
	implements IDeviceEventManagementApiChannel<DeviceEventManagementGrpcChannel> {

    public DeviceEventManagementApiChannel(IInstanceSettings settings) {
	super(settings, MicroserviceIdentifier.EventManagement, GrpcServiceIdentifier.EventManagement,
		IGrpcSettings.DEFAULT_API_PORT);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .microservice.instance.IInstanceSettings,
     * com.sitewhere.spi.microservice.IFunctionIdentifier,
     * com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier, int)
     */
    @Override
    public DeviceEventManagementGrpcChannel createGrpcChannel(IInstanceSettings settings,
	    IFunctionIdentifier identifier, IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	return new DeviceEventManagementGrpcChannel(settings, identifier, grpcServiceIdentifier, port);
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
     * getDeviceEventById(java.util.UUID, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceEventById(UUID eventId, StreamObserver<IDeviceEvent> observer) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getGetDeviceEventByIdMethod(),
		DebugParameter.create("Event Id", eventId));
	GGetDeviceEventByIdRequest.Builder grequest = GGetDeviceEventByIdRequest.newBuilder();
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
     * getDeviceEventByAlternateId(java.lang.String, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceEventByAlternateId(String alternateId, StreamObserver<IDeviceEvent> observer)
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
     * addDeviceMeasurements(java.util.UUID, io.grpc.stub.StreamObserver,
     * com.sitewhere.spi.device.event.request.IDeviceMeasurementCreateRequest[])
     */
    @Override
    public void addDeviceMeasurements(UUID deviceAssignmentId, StreamObserver<IDeviceMeasurement> observer,
	    IDeviceMeasurementCreateRequest... requests) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddMeasurementsMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Request", requests));
	GAddMeasurementsRequest.Builder grequest = GAddMeasurementsRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	for (IDeviceMeasurementCreateRequest request : requests) {
	    grequest.addRequests(EventModelConverter.asGrpcDeviceMeasurementCreateRequest(request));
	}
	getGrpcChannel().getAsyncStub().addMeasurements(
		GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getAddMeasurementsMethod(), grequest.build()),
		new StreamObserver<GAddMeasurementsResponse>() {

		    @Override
		    public void onNext(GAddMeasurementsResponse gresponse) {
			try {
			    List<DeviceMeasurement> response = EventModelConverter
				    .asApiDeviceMeasurements(gresponse.getMeasurementsList());
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddMeasurementsMethod(),
				    response);
			    for (DeviceMeasurement mx : response) {
				observer.onNext(mx);
			    }
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getAddMeasurementsMethod(), t));
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
     * addDeviceLocations(java.util.UUID, io.grpc.stub.StreamObserver,
     * com.sitewhere.spi.device.event.request.IDeviceLocationCreateRequest[])
     */
    @Override
    public void addDeviceLocations(UUID deviceAssignmentId, StreamObserver<IDeviceLocation> observer,
	    IDeviceLocationCreateRequest... requests) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddLocationsMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Request", requests));
	GAddLocationsRequest.Builder grequest = GAddLocationsRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	for (IDeviceLocationCreateRequest request : requests) {
	    grequest.addRequests(EventModelConverter.asGrpcDeviceLocationCreateRequest(request));
	}
	getGrpcChannel().getAsyncStub().addLocations(
		GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getAddLocationsMethod(), grequest.build()),
		new StreamObserver<GAddLocationsResponse>() {

		    @Override
		    public void onNext(GAddLocationsResponse gresponse) {
			try {
			    List<DeviceLocation> response = EventModelConverter
				    .asApiDeviceLocations(gresponse.getLocationsList());
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddLocationsMethod(),
				    response);
			    for (DeviceLocation location : response) {
				observer.onNext(location);
			    }
			} catch (Throwable t) {
			    observer.onError(GrpcUtils
				    .handleClientMethodException(DeviceEventManagementGrpc.getAddLocationsMethod(), t));
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
     * addDeviceAlerts(java.util.UUID, io.grpc.stub.StreamObserver,
     * com.sitewhere.spi.device.event.request.IDeviceAlertCreateRequest[])
     */
    @Override
    public void addDeviceAlerts(UUID deviceAssignmentId, StreamObserver<IDeviceAlert> observer,
	    IDeviceAlertCreateRequest... requests) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddAlertsMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Request", requests));
	GAddAlertsRequest.Builder grequest = GAddAlertsRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	for (IDeviceAlertCreateRequest request : requests) {
	    grequest.addRequests(EventModelConverter.asGrpcDeviceAlertCreateRequest(request));
	}
	getGrpcChannel().getAsyncStub().addAlerts(
		GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getAddAlertsMethod(), grequest.build()),
		new StreamObserver<GAddAlertsResponse>() {

		    @Override
		    public void onNext(GAddAlertsResponse gresponse) {
			try {
			    List<DeviceAlert> response = EventModelConverter
				    .asApiDeviceAlerts(gresponse.getAlertsList());
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddAlertsMethod(), response);
			    for (DeviceAlert alert : response) {
				observer.onNext(alert);
			    }
			} catch (Throwable t) {
			    observer.onError(GrpcUtils
				    .handleClientMethodException(DeviceEventManagementGrpc.getAddAlertsMethod(), t));
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
     * addDeviceCommandInvocations(java.util.UUID, io.grpc.stub.StreamObserver,
     * com.sitewhere.spi.device.event.request.IDeviceCommandInvocationCreateRequest[
     * ])
     */
    @Override
    public void addDeviceCommandInvocations(UUID deviceAssignmentId, StreamObserver<IDeviceCommandInvocation> observer,
	    IDeviceCommandInvocationCreateRequest... requests) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddCommandInvocationsMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId),
		DebugParameter.create("Requests", requests));
	GAddCommandInvocationsRequest.Builder grequest = GAddCommandInvocationsRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	for (IDeviceCommandInvocationCreateRequest request : requests) {
	    grequest.addRequests(EventModelConverter.asGrpcDeviceCommandInvocationCreateRequest(request));
	}
	getGrpcChannel().getAsyncStub().addCommandInvocations(GrpcUtils
		.logGrpcClientRequest(DeviceEventManagementGrpc.getAddCommandInvocationsMethod(), grequest.build()),
		new StreamObserver<GAddCommandInvocationsResponse>() {

		    @Override
		    public void onNext(GAddCommandInvocationsResponse gresponse) {
			try {
			    List<DeviceCommandInvocation> response = EventModelConverter
				    .asApiDeviceCommandInvocations(gresponse.getInvocationsList());
			    GrpcUtils.logClientMethodResponse(
				    DeviceEventManagementGrpc.getAddCommandInvocationsMethod(), response);
			    for (DeviceCommandInvocation invocation : response) {
				observer.onNext(invocation);
			    }
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getAddCommandInvocationsMethod(), t));
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
     * listDeviceCommandInvocationResponses(java.util.UUID,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceCommandInvocationResponses(UUID invocationId,
	    StreamObserver<ISearchResults<IDeviceCommandResponse>> observer) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod(),
		DebugParameter.create("Invocation Id", invocationId));
	GListCommandResponsesForInvocationRequest.Builder grequest = GListCommandResponsesForInvocationRequest
		.newBuilder();
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
     * addDeviceCommandResponses(java.util.UUID, io.grpc.stub.StreamObserver,
     * com.sitewhere.spi.device.event.request.IDeviceCommandResponseCreateRequest[])
     */
    @Override
    public void addDeviceCommandResponses(UUID deviceAssignmentId, StreamObserver<IDeviceCommandResponse> observer,
	    IDeviceCommandResponseCreateRequest... requests) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddCommandResponsesMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId), DebugParameter.create("Request", requests));
	GAddCommandResponsesRequest.Builder grequest = GAddCommandResponsesRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	for (IDeviceCommandResponseCreateRequest request : requests) {
	    grequest.addRequests(EventModelConverter.asGrpcDeviceCommandResponseCreateRequest(request));
	}
	getGrpcChannel().getAsyncStub().addCommandResponses(GrpcUtils
		.logGrpcClientRequest(DeviceEventManagementGrpc.getAddCommandResponsesMethod(), grequest.build()),
		new StreamObserver<GAddCommandResponsesResponse>() {

		    @Override
		    public void onNext(GAddCommandResponsesResponse gresponse) {
			try {
			    List<DeviceCommandResponse> response = EventModelConverter
				    .asApiDeviceCommandResponses(gresponse.getResponsesList());
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddCommandResponsesMethod(),
				    response);
			    for (DeviceCommandResponse cmdresponse : response) {
				observer.onNext(cmdresponse);
			    }
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getAddCommandResponsesMethod(), t));
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
     * addDeviceStateChanges(java.util.UUID, io.grpc.stub.StreamObserver,
     * com.sitewhere.spi.device.event.request.IDeviceStateChangeCreateRequest[])
     */
    @Override
    public void addDeviceStateChanges(UUID deviceAssignmentId, StreamObserver<IDeviceStateChange> observer,
	    IDeviceStateChangeCreateRequest... requests) throws SiteWhereException {
	GrpcUtils.handleClientMethodEntry(this, DeviceEventManagementGrpc.getAddStateChangesMethod(),
		DebugParameter.create("Assignment Id", deviceAssignmentId),
		DebugParameter.create("Requests", requests));
	GAddStateChangesRequest.Builder grequest = GAddStateChangesRequest.newBuilder();
	grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	for (IDeviceStateChangeCreateRequest request : requests) {
	    grequest.addRequests(EventModelConverter.asGrpcDeviceStateChangeCreateRequest(request));
	}
	getGrpcChannel().getAsyncStub().addStateChanges(
		GrpcUtils.logGrpcClientRequest(DeviceEventManagementGrpc.getAddStateChangesMethod(), grequest.build()),
		new StreamObserver<GAddStateChangesResponse>() {

		    @Override
		    public void onNext(GAddStateChangesResponse gresponse) {
			try {
			    List<DeviceStateChange> response = EventModelConverter
				    .asApiDeviceStateChanges(gresponse.getStateChangesList());
			    GrpcUtils.logClientMethodResponse(DeviceEventManagementGrpc.getAddStateChangesMethod(),
				    response);
			    for (DeviceStateChange statechange : response) {
				observer.onNext(statechange);
			    }
			} catch (Throwable t) {
			    observer.onError(GrpcUtils.handleClientMethodException(
				    DeviceEventManagementGrpc.getAddStateChangesMethod(), t));
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