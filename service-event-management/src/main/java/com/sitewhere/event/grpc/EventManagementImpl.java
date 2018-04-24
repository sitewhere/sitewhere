/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.grpc;

import com.sitewhere.event.grpc.streaming.DeviceAssignmentEventCreateStreamObserver;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAlertSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAssignmentEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandInvocationSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandResponseSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceLocationSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceMeasurementsSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceStateChangeSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceStreamDataSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GEventStreamAck;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.converter.DeviceModelConverter;
import com.sitewhere.grpc.model.converter.EventModelConverter;
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
import com.sitewhere.grpc.service.GListAlertsForAssignmentsRequest;
import com.sitewhere.grpc.service.GListAlertsForAssignmentsResponse;
import com.sitewhere.grpc.service.GListCommandInvocationsForAreasRequest;
import com.sitewhere.grpc.service.GListCommandInvocationsForAreasResponse;
import com.sitewhere.grpc.service.GListCommandInvocationsForAssignmentsRequest;
import com.sitewhere.grpc.service.GListCommandInvocationsForAssignmentsResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForAreasRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForAreasResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForAssignmentsRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForAssignmentsResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForInvocationRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForInvocationResponse;
import com.sitewhere.grpc.service.GListLocationsForAreasRequest;
import com.sitewhere.grpc.service.GListLocationsForAreasResponse;
import com.sitewhere.grpc.service.GListLocationsForAssignmentsRequest;
import com.sitewhere.grpc.service.GListLocationsForAssignmentsResponse;
import com.sitewhere.grpc.service.GListMeasurementsForAreasRequest;
import com.sitewhere.grpc.service.GListMeasurementsForAreasResponse;
import com.sitewhere.grpc.service.GListMeasurementsForAssignmentsRequest;
import com.sitewhere.grpc.service.GListMeasurementsForAssignmentsResponse;
import com.sitewhere.grpc.service.GListStateChangesForAreasRequest;
import com.sitewhere.grpc.service.GListStateChangesForAreasResponse;
import com.sitewhere.grpc.service.GListStateChangesForAssignmentsRequest;
import com.sitewhere.grpc.service.GListStateChangesForAssignmentsResponse;
import com.sitewhere.grpc.service.GListStreamDataForAssignmentRequest;
import com.sitewhere.grpc.service.GListStreamDataForAssignmentResponse;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurements;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.search.ISearchResults;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for device event management GRPC requests.
 * 
 * @author Derek
 */
public class EventManagementImpl extends DeviceEventManagementGrpc.DeviceEventManagementImplBase {

    /** Device management persistence */
    private IDeviceEventManagement deviceEventManagement;

    public EventManagementImpl(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
    }

    public IDeviceEventManagement getDeviceEventManagement() {
	return deviceEventManagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addDeviceEventBatch(com.sitewhere.grpc.
     * service.GAddDeviceEventBatchRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addDeviceEventBatch(GAddDeviceEventBatchRequest request,
	    StreamObserver<GAddDeviceEventBatchResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_DEVICE_EVENT_BATCH);
	    DeviceEventBatch apiRequest = EventModelConverter.asApiDeviceEventBatch(request.getRequest());
	    IDeviceEventBatchResponse apiResult = getDeviceEventManagement()
		    .addDeviceEventBatch(CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()), apiRequest);
	    GAddDeviceEventBatchResponse.Builder response = GAddDeviceEventBatchResponse.newBuilder();
	    response.setResponse(EventModelConverter.asGrpcDeviceEventBatchResponse(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_DEVICE_EVENT_BATCH, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#getDeviceEventById(com.sitewhere.grpc.
     * service.GGetDeviceEventByIdRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceEventById(GGetDeviceEventByIdRequest request,
	    StreamObserver<GGetDeviceEventByIdResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ID);
	    IDeviceEvent apiResult = getDeviceEventManagement().getDeviceEventById(
		    CommonModelConverter.asApiUuid(request.getDeviceId()),
		    CommonModelConverter.asApiUuid(request.getEventId()));
	    GGetDeviceEventByIdResponse.Builder response = GGetDeviceEventByIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setEvent(EventModelConverter.asGrpcGenericDeviceEvent(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ID, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#getDeviceEventByAlternateId(com.sitewhere.
     * grpc.service.GGetDeviceEventByAlternateIdRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceEventByAlternateId(GGetDeviceEventByAlternateIdRequest request,
	    StreamObserver<GGetDeviceEventByAlternateIdResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ALTERNATE_ID);
	    IDeviceEvent apiResult = getDeviceEventManagement().getDeviceEventByAlternateId(
		    CommonModelConverter.asApiUuid(request.getDeviceId()), request.getAlternateId());
	    GGetDeviceEventByAlternateIdResponse.Builder response = GGetDeviceEventByAlternateIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setEvent(EventModelConverter.asGrpcGenericDeviceEvent(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ALTERNATE_ID, e,
		    responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#streamDeviceAssignmentEventCreateRequests(io.
     * grpc.stub.StreamObserver)
     */
    @Override
    public StreamObserver<GDeviceAssignmentEventCreateRequest> streamDeviceAssignmentEventCreateRequests(
	    StreamObserver<GEventStreamAck> responseObserver) {
	try {
	    DeviceAssignmentEventCreateStreamObserver observer = new DeviceAssignmentEventCreateStreamObserver(
		    getDeviceEventManagement(), responseObserver);
	    observer.start();
	    return observer;
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(
		    DeviceEventManagementGrpc.METHOD_STREAM_DEVICE_ASSIGNMENT_EVENT_CREATE_REQUESTS, e,
		    responseObserver);
	    return null;
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addMeasurementsForAssignment(com.sitewhere.
     * grpc.service.GAddMeasurementsForAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void addMeasurementsForAssignment(GAddMeasurementsForAssignmentRequest request,
	    StreamObserver<GAddMeasurementsForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS_FOR_ASSIGNMENT);
	    IDeviceMeasurements apiResult = getDeviceEventManagement().addDeviceMeasurements(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()),
		    EventModelConverter.asApiDeviceMeasurementsCreateRequest(request.getRequest()));
	    GAddMeasurementsForAssignmentResponse.Builder response = GAddMeasurementsForAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setMeasurements(EventModelConverter.asGrpcDeviceMeasurements(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS_FOR_ASSIGNMENT, e,
		    responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listMeasurementsForAssignments(com.sitewhere.
     * grpc.service.GListMeasurementsForAssignmentsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listMeasurementsForAssignments(GListMeasurementsForAssignmentsRequest request,
	    StreamObserver<GListMeasurementsForAssignmentsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_ASSIGNMENTS);
	    ISearchResults<IDeviceMeasurements> apiResult = getDeviceEventManagement()
		    .listDeviceMeasurementsForAssignments(
			    CommonModelConverter.asApiUuids(request.getDeviceAssignmentIdsList()),
			    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListMeasurementsForAssignmentsResponse.Builder response = GListMeasurementsForAssignmentsResponse
		    .newBuilder();
	    GDeviceMeasurementsSearchResults.Builder results = GDeviceMeasurementsSearchResults.newBuilder();
	    for (IDeviceMeasurements api : apiResult.getResults()) {
		results.addMeasurements(EventModelConverter.asGrpcDeviceMeasurements(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_ASSIGNMENTS, e,
		    responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listMeasurementsForAreas(com.sitewhere.grpc.
     * service.GListMeasurementsForAreasRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listMeasurementsForAreas(GListMeasurementsForAreasRequest request,
	    StreamObserver<GListMeasurementsForAreasResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_AREAS);
	    ISearchResults<IDeviceMeasurements> apiResult = getDeviceEventManagement().listDeviceMeasurementsForAreas(
		    CommonModelConverter.asApiUuids(request.getAreaIdsList()),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GrpcUtils.logServerApiResult(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_AREAS, apiResult);
	    GListMeasurementsForAreasResponse.Builder response = GListMeasurementsForAreasResponse.newBuilder();
	    GDeviceMeasurementsSearchResults.Builder results = GDeviceMeasurementsSearchResults.newBuilder();
	    for (IDeviceMeasurements api : apiResult.getResults()) {
		results.addMeasurements(EventModelConverter.asGrpcDeviceMeasurements(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_AREAS, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addLocationForAssignment(com.sitewhere.grpc
     * .service.GAddLocationForAssignmentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addLocationForAssignment(GAddLocationForAssignmentRequest request,
	    StreamObserver<GAddLocationForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_LOCATION_FOR_ASSIGNMENT);
	    IDeviceLocation apiResult = getDeviceEventManagement().addDeviceLocation(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()),
		    EventModelConverter.asApiDeviceLocationCreateRequest(request.getRequest()));
	    GAddLocationForAssignmentResponse.Builder response = GAddLocationForAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setLocation(EventModelConverter.asGrpcDeviceLocation(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_LOCATION_FOR_ASSIGNMENT, e,
		    responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listLocationsForAssignments(com.sitewhere.grpc.
     * service.GListLocationsForAssignmentsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listLocationsForAssignments(GListLocationsForAssignmentsRequest request,
	    StreamObserver<GListLocationsForAssignmentsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_ASSIGNMENTS);
	    ISearchResults<IDeviceLocation> apiResult = getDeviceEventManagement().listDeviceLocationsForAssignments(
		    CommonModelConverter.asApiUuids(request.getDeviceAssignmentIdsList()),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListLocationsForAssignmentsResponse.Builder response = GListLocationsForAssignmentsResponse.newBuilder();
	    GDeviceLocationSearchResults.Builder results = GDeviceLocationSearchResults.newBuilder();
	    for (IDeviceLocation api : apiResult.getResults()) {
		results.addLocations(EventModelConverter.asGrpcDeviceLocation(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_ASSIGNMENTS, e,
		    responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listLocationsForAreas(com.sitewhere.grpc.
     * service.GListLocationsForAreasRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listLocationsForAreas(GListLocationsForAreasRequest request,
	    StreamObserver<GListLocationsForAreasResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_AREAS);
	    ISearchResults<IDeviceLocation> apiResult = getDeviceEventManagement().listDeviceLocationsForAreas(
		    CommonModelConverter.asApiUuids(request.getAreaIdsList()),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListLocationsForAreasResponse.Builder response = GListLocationsForAreasResponse.newBuilder();
	    GDeviceLocationSearchResults.Builder results = GDeviceLocationSearchResults.newBuilder();
	    for (IDeviceLocation api : apiResult.getResults()) {
		results.addLocations(EventModelConverter.asGrpcDeviceLocation(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_AREAS, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addAlertForAssignment(com.sitewhere.grpc.
     * service.GAddAlertForAssignmentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addAlertForAssignment(GAddAlertForAssignmentRequest request,
	    StreamObserver<GAddAlertForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_ALERT_FOR_ASSIGNMENT);
	    IDeviceAlert apiResult = getDeviceEventManagement().addDeviceAlert(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()),
		    EventModelConverter.asApiDeviceAlertCreateRequest(request.getRequest()));
	    GAddAlertForAssignmentResponse.Builder response = GAddAlertForAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setAlert(EventModelConverter.asGrpcDeviceAlert(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_ALERT_FOR_ASSIGNMENT, e,
		    responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listAlertsForAssignments(com.sitewhere.grpc.
     * service.GListAlertsForAssignmentsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAlertsForAssignments(GListAlertsForAssignmentsRequest request,
	    StreamObserver<GListAlertsForAssignmentsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_ASSIGNMENTS);
	    ISearchResults<IDeviceAlert> apiResult = getDeviceEventManagement().listDeviceAlertsForAssignments(
		    CommonModelConverter.asApiUuids(request.getDeviceAssignmentIdsList()),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListAlertsForAssignmentsResponse.Builder response = GListAlertsForAssignmentsResponse.newBuilder();
	    GDeviceAlertSearchResults.Builder results = GDeviceAlertSearchResults.newBuilder();
	    for (IDeviceAlert api : apiResult.getResults()) {
		results.addAlerts(EventModelConverter.asGrpcDeviceAlert(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_ASSIGNMENTS, e,
		    responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listAlertsForAreas(com.sitewhere.grpc.service.
     * GListAlertsForAreasRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAlertsForAreas(GListAlertsForAreasRequest request,
	    StreamObserver<GListAlertsForAreasResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_AREAS);
	    ISearchResults<IDeviceAlert> apiResult = getDeviceEventManagement().listDeviceAlertsForAreas(
		    CommonModelConverter.asApiUuids(request.getAreaIdsList()),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListAlertsForAreasResponse.Builder response = GListAlertsForAreasResponse.newBuilder();
	    GDeviceAlertSearchResults.Builder results = GDeviceAlertSearchResults.newBuilder();
	    for (IDeviceAlert api : apiResult.getResults()) {
		results.addAlerts(EventModelConverter.asGrpcDeviceAlert(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_AREAS, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addStreamDataForAssignment(com.sitewhere.
     * grpc.service.GAddStreamDataForAssignmentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addStreamDataForAssignment(GAddStreamDataForAssignmentRequest request,
	    StreamObserver<GAddStreamDataForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_STREAM_DATA_FOR_ASSIGNMENT);
	    IDeviceStreamData apiResult = getDeviceEventManagement().addDeviceStreamData(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()),
		    DeviceModelConverter.asApiDeviceStream(request.getDeviceStream()),
		    EventModelConverter.asApiDeviceStreamDataCreateRequest(request.getRequest()));
	    GAddStreamDataForAssignmentResponse.Builder response = GAddStreamDataForAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setStreamData(EventModelConverter.asGrpcDeviceStreamData(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_STREAM_DATA_FOR_ASSIGNMENT, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#getStreamDataForAssignment(com.sitewhere.
     * grpc.service.GGetStreamDataForAssignmentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getStreamDataForAssignment(GGetStreamDataForAssignmentRequest request,
	    StreamObserver<GGetStreamDataForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_GET_STREAM_DATA_FOR_ASSIGNMENT);
	    IDeviceStreamData apiResult = getDeviceEventManagement().getDeviceStreamData(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()), request.getStreamId(),
		    request.getSequenceNumber());
	    GGetStreamDataForAssignmentResponse.Builder response = GGetStreamDataForAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setStreamData(EventModelConverter.asGrpcDeviceStreamData(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_GET_STREAM_DATA_FOR_ASSIGNMENT, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listStreamDataForAssignment(com.sitewhere.
     * grpc.service.GListStreamDataForAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listStreamDataForAssignment(GListStreamDataForAssignmentRequest request,
	    StreamObserver<GListStreamDataForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_STREAM_DATA_FOR_ASSIGNMENT);
	    ISearchResults<IDeviceStreamData> apiResult = getDeviceEventManagement().listDeviceStreamDataForAssignment(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()), request.getStreamId(),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListStreamDataForAssignmentResponse.Builder response = GListStreamDataForAssignmentResponse.newBuilder();
	    GDeviceStreamDataSearchResults.Builder results = GDeviceStreamDataSearchResults.newBuilder();
	    for (IDeviceStreamData api : apiResult.getResults()) {
		results.addStreamData(EventModelConverter.asGrpcDeviceStreamData(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_STREAM_DATA_FOR_ASSIGNMENT, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addCommandInvocationForAssignment(com.
     * sitewhere.grpc.service.GAddCommandInvocationForAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void addCommandInvocationForAssignment(GAddCommandInvocationForAssignmentRequest request,
	    StreamObserver<GAddCommandInvocationForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION_FOR_ASSIGNMENT);
	    IDeviceCommandInvocation apiResult = getDeviceEventManagement().addDeviceCommandInvocation(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()),
		    EventModelConverter.asApiDeviceCommandInvocationCreateRequest(request.getRequest()));
	    GAddCommandInvocationForAssignmentResponse.Builder response = GAddCommandInvocationForAssignmentResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.setInvocation(EventModelConverter.asGrpcDeviceCommandInvocation(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(
		    DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION_FOR_ASSIGNMENT, e, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listCommandInvocationsForAssignments(com.
     * sitewhere.grpc.service.GListCommandInvocationsForAssignmentsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCommandInvocationsForAssignments(GListCommandInvocationsForAssignmentsRequest request,
	    StreamObserver<GListCommandInvocationsForAssignmentsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_ASSIGNMENTS);
	    ISearchResults<IDeviceCommandInvocation> apiResult = getDeviceEventManagement()
		    .listDeviceCommandInvocationsForAssignments(
			    CommonModelConverter.asApiUuids(request.getDeviceAssignmentIdsList()),
			    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListCommandInvocationsForAssignmentsResponse.Builder response = GListCommandInvocationsForAssignmentsResponse
		    .newBuilder();
	    GDeviceCommandInvocationSearchResults.Builder results = GDeviceCommandInvocationSearchResults.newBuilder();
	    for (IDeviceCommandInvocation api : apiResult.getResults()) {
		results.addInvocations(EventModelConverter.asGrpcDeviceCommandInvocation(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(
		    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_ASSIGNMENTS, e, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listCommandInvocationsForAreas(com.sitewhere.
     * grpc.service.GListCommandInvocationsForAreasRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCommandInvocationsForAreas(GListCommandInvocationsForAreasRequest request,
	    StreamObserver<GListCommandInvocationsForAreasResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_AREAS);
	    ISearchResults<IDeviceCommandInvocation> apiResult = getDeviceEventManagement()
		    .listDeviceCommandInvocationsForAreas(CommonModelConverter.asApiUuids(request.getAreaIdsList()),
			    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListCommandInvocationsForAreasResponse.Builder response = GListCommandInvocationsForAreasResponse
		    .newBuilder();
	    GDeviceCommandInvocationSearchResults.Builder results = GDeviceCommandInvocationSearchResults.newBuilder();
	    for (IDeviceCommandInvocation api : apiResult.getResults()) {
		results.addInvocations(EventModelConverter.asGrpcDeviceCommandInvocation(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_AREAS,
		    e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addCommandResponseForAssignment(com.
     * sitewhere.grpc.service.GAddCommandResponseForAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void addCommandResponseForAssignment(GAddCommandResponseForAssignmentRequest request,
	    StreamObserver<GAddCommandResponseForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE_FOR_ASSIGNMENT);
	    IDeviceCommandResponse apiResult = getDeviceEventManagement().addDeviceCommandResponse(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()),
		    EventModelConverter.asApiDeviceCommandResponseCreateRequest(request.getRequest()));
	    GAddCommandResponseForAssignmentResponse.Builder response = GAddCommandResponseForAssignmentResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.setResponse(EventModelConverter.asGrpcDeviceCommandResponse(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE_FOR_ASSIGNMENT,
		    e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listCommandResponsesForInvocation(com.
     * sitewhere.grpc.service.GListCommandResponsesForInvocationRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCommandResponsesForInvocation(GListCommandResponsesForInvocationRequest request,
	    StreamObserver<GListCommandResponsesForInvocationResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INVOCATION);
	    ISearchResults<IDeviceCommandResponse> apiResult = getDeviceEventManagement()
		    .listDeviceCommandInvocationResponses(CommonModelConverter.asApiUuid(request.getDeviceId()),
			    CommonModelConverter.asApiUuid(request.getInvocationEventId()));
	    GListCommandResponsesForInvocationResponse.Builder response = GListCommandResponsesForInvocationResponse
		    .newBuilder();
	    GDeviceCommandResponseSearchResults.Builder results = GDeviceCommandResponseSearchResults.newBuilder();
	    for (IDeviceCommandResponse api : apiResult.getResults()) {
		results.addResponses(EventModelConverter.asGrpcDeviceCommandResponse(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(
		    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INVOCATION, e, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listCommandResponsesForAssignments(com.
     * sitewhere.grpc.service.GListCommandResponsesForAssignmentsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCommandResponsesForAssignments(GListCommandResponsesForAssignmentsRequest request,
	    StreamObserver<GListCommandResponsesForAssignmentsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_ASSIGNMENTS);
	    ISearchResults<IDeviceCommandResponse> apiResult = getDeviceEventManagement()
		    .listDeviceCommandResponsesForAssignments(
			    CommonModelConverter.asApiUuids(request.getDeviceAssignmentIdsList()),
			    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListCommandResponsesForAssignmentsResponse.Builder response = GListCommandResponsesForAssignmentsResponse
		    .newBuilder();
	    GDeviceCommandResponseSearchResults.Builder results = GDeviceCommandResponseSearchResults.newBuilder();
	    for (IDeviceCommandResponse api : apiResult.getResults()) {
		results.addResponses(EventModelConverter.asGrpcDeviceCommandResponse(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(
		    DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_ASSIGNMENTS, e, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listCommandResponsesForAreas(com.sitewhere.grpc
     * .service.GListCommandResponsesForAreasRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCommandResponsesForAreas(GListCommandResponsesForAreasRequest request,
	    StreamObserver<GListCommandResponsesForAreasResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_AREAS);
	    ISearchResults<IDeviceCommandResponse> apiResult = getDeviceEventManagement()
		    .listDeviceCommandResponsesForAreas(CommonModelConverter.asApiUuids(request.getAreaIdsList()),
			    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListCommandResponsesForAreasResponse.Builder response = GListCommandResponsesForAreasResponse.newBuilder();
	    GDeviceCommandResponseSearchResults.Builder results = GDeviceCommandResponseSearchResults.newBuilder();
	    for (IDeviceCommandResponse api : apiResult.getResults()) {
		results.addResponses(EventModelConverter.asGrpcDeviceCommandResponse(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_AREAS, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addStateChangeForAssignment(com.sitewhere.
     * grpc.service.GAddStateChangeForAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void addStateChangeForAssignment(GAddStateChangeForAssignmentRequest request,
	    StreamObserver<GAddStateChangeForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE_FOR_ASSIGNMENT);
	    IDeviceStateChange apiResult = getDeviceEventManagement().addDeviceStateChange(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()),
		    EventModelConverter.asApiDeviceStateChangeCreateRequest(request.getRequest()));
	    GAddStateChangeForAssignmentResponse.Builder response = GAddStateChangeForAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setStateChange(EventModelConverter.asGrpcDeviceStateChange(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE_FOR_ASSIGNMENT, e,
		    responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listStateChangesForAssignments(com.sitewhere.
     * grpc.service.GListStateChangesForAssignmentsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listStateChangesForAssignments(GListStateChangesForAssignmentsRequest request,
	    StreamObserver<GListStateChangesForAssignmentsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENTS);
	    ISearchResults<IDeviceStateChange> apiResult = getDeviceEventManagement()
		    .listDeviceStateChangesForAssignments(
			    CommonModelConverter.asApiUuids(request.getDeviceAssignmentIdsList()),
			    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListStateChangesForAssignmentsResponse.Builder response = GListStateChangesForAssignmentsResponse
		    .newBuilder();
	    GDeviceStateChangeSearchResults.Builder results = GDeviceStateChangeSearchResults.newBuilder();
	    for (IDeviceStateChange api : apiResult.getResults()) {
		results.addStateChanges(EventModelConverter.asGrpcDeviceStateChange(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENTS,
		    e, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listStateChangesForAreas(com.sitewhere.grpc.
     * service.GListStateChangesForAreasRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listStateChangesForAreas(GListStateChangesForAreasRequest request,
	    StreamObserver<GListStateChangesForAreasResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_AREAS);
	    ISearchResults<IDeviceStateChange> apiResult = getDeviceEventManagement().listDeviceStateChangesForAreas(
		    CommonModelConverter.asApiUuids(request.getAreaIdsList()),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListStateChangesForAreasResponse.Builder response = GListStateChangesForAreasResponse.newBuilder();
	    GDeviceStateChangeSearchResults.Builder results = GDeviceStateChangeSearchResults.newBuilder();
	    for (IDeviceStateChange api : apiResult.getResults()) {
		results.addStateChanges(EventModelConverter.asGrpcDeviceStateChange(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_AREAS, e,
		    responseObserver);
	}
    }

    public void setDeviceEventManagement(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
    }
}