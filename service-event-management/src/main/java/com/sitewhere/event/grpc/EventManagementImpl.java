/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.grpc;

import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAlertSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandInvocationSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandResponseSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceLocationSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceMeasurementSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceStateChangeSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceStreamDataSearchResults;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.converter.DeviceModelConverter;
import com.sitewhere.grpc.model.converter.EventModelConverter;
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
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;
import com.sitewhere.spi.device.event.IDeviceLocation;
import com.sitewhere.spi.device.event.IDeviceMeasurement;
import com.sitewhere.spi.device.event.IDeviceStateChange;
import com.sitewhere.spi.device.event.IDeviceStreamData;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.search.ISearchResults;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for device event management GRPC requests.
 * 
 * @author Derek
 */
public class EventManagementImpl extends DeviceEventManagementGrpc.DeviceEventManagementImplBase
	implements IGrpcApiImplementation {

    /** Parent microservice */
    private IEventManagementMicroservice microservice;

    /** Device management persistence */
    private IDeviceEventManagement deviceEventManagement;

    public EventManagementImpl(IEventManagementMicroservice microservice,
	    IDeviceEventManagement deviceEventManagement) {
	this.microservice = microservice;
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddDeviceEventBatchMethod());
	    DeviceEventBatch apiRequest = EventModelConverter.asApiDeviceEventBatch(request.getRequest());
	    IDeviceEventBatchResponse apiResult = getDeviceEventManagement()
		    .addDeviceEventBatch(CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()), apiRequest);
	    GAddDeviceEventBatchResponse.Builder response = GAddDeviceEventBatchResponse.newBuilder();
	    response.setResponse(EventModelConverter.asGrpcDeviceEventBatchResponse(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddDeviceEventBatchMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddDeviceEventBatchMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getGetDeviceEventByIdMethod());
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
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getGetDeviceEventByIdMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getGetDeviceEventByIdMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod());
	    IDeviceEvent apiResult = getDeviceEventManagement().getDeviceEventByAlternateId(
		    CommonModelConverter.asApiUuid(request.getDeviceId()), request.getAlternateId());
	    GGetDeviceEventByAlternateIdResponse.Builder response = GGetDeviceEventByAlternateIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setEvent(EventModelConverter.asGrpcGenericDeviceEvent(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getGetDeviceEventByAlternateIdMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addMeasurement(com.sitewhere.grpc.service.
     * GAddMeasurementRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addMeasurement(GAddMeasurementRequest request,
	    StreamObserver<GAddMeasurementResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddMeasurementMethod());
	    IDeviceMeasurement apiResult = getDeviceEventManagement().addDeviceMeasurement(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()),
		    EventModelConverter.asApiDeviceMeasurementCreateRequest(request.getRequest()));
	    GAddMeasurementResponse.Builder response = GAddMeasurementResponse.newBuilder();
	    if (apiResult != null) {
		response.setMeasurement(EventModelConverter.asGrpcDeviceMeasurement(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddMeasurementMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddMeasurementMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listMeasurementsForIndex(com.sitewhere.grpc.
     * service.GListMeasurementsForIndexRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listMeasurementsForIndex(GListMeasurementsForIndexRequest request,
	    StreamObserver<GListMeasurementsForIndexResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getListMeasurementsForIndexMethod());
	    ISearchResults<IDeviceMeasurement> apiResult = getDeviceEventManagement().listDeviceMeasurementsForIndex(
		    EventModelConverter.asApiDeviceEventIndex(request.getIndex()),
		    CommonModelConverter.asApiUuids(request.getEntityIdsList()),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListMeasurementsForIndexResponse.Builder response = GListMeasurementsForIndexResponse.newBuilder();
	    GDeviceMeasurementSearchResults.Builder results = GDeviceMeasurementSearchResults.newBuilder();
	    for (IDeviceMeasurement api : apiResult.getResults()) {
		results.addMeasurements(EventModelConverter.asGrpcDeviceMeasurement(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getListMeasurementsForIndexMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getListMeasurementsForIndexMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addLocation(com.sitewhere.grpc.service.
     * GAddLocationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addLocation(GAddLocationRequest request, StreamObserver<GAddLocationResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddLocationMethod());
	    IDeviceLocation apiResult = getDeviceEventManagement().addDeviceLocation(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()),
		    EventModelConverter.asApiDeviceLocationCreateRequest(request.getRequest()));
	    GAddLocationResponse.Builder response = GAddLocationResponse.newBuilder();
	    if (apiResult != null) {
		response.setLocation(EventModelConverter.asGrpcDeviceLocation(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddLocationMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddLocationMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listLocationsForIndex(com.sitewhere.grpc.
     * service.GListLocationsForIndexRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listLocationsForIndex(GListLocationsForIndexRequest request,
	    StreamObserver<GListLocationsForIndexResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getListLocationsForIndexMethod());
	    ISearchResults<IDeviceLocation> apiResult = getDeviceEventManagement().listDeviceLocationsForIndex(
		    EventModelConverter.asApiDeviceEventIndex(request.getIndex()),
		    CommonModelConverter.asApiUuids(request.getEntityIdsList()),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListLocationsForIndexResponse.Builder response = GListLocationsForIndexResponse.newBuilder();
	    GDeviceLocationSearchResults.Builder results = GDeviceLocationSearchResults.newBuilder();
	    for (IDeviceLocation api : apiResult.getResults()) {
		results.addLocations(EventModelConverter.asGrpcDeviceLocation(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getListLocationsForIndexMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getListLocationsForIndexMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addAlert(com.sitewhere.grpc.service.
     * GAddAlertRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addAlert(GAddAlertRequest request, StreamObserver<GAddAlertResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddAlertMethod());
	    IDeviceAlert apiResult = getDeviceEventManagement().addDeviceAlert(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()),
		    EventModelConverter.asApiDeviceAlertCreateRequest(request.getRequest()));
	    GAddAlertResponse.Builder response = GAddAlertResponse.newBuilder();
	    if (apiResult != null) {
		response.setAlert(EventModelConverter.asGrpcDeviceAlert(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddAlertMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddAlertMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listAlertsForIndex(com.sitewhere.grpc.service.
     * GListAlertsForIndexRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAlertsForIndex(GListAlertsForIndexRequest request,
	    StreamObserver<GListAlertsForIndexResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getListAlertsForIndexMethod());
	    ISearchResults<IDeviceAlert> apiResult = getDeviceEventManagement().listDeviceAlertsForIndex(
		    EventModelConverter.asApiDeviceEventIndex(request.getIndex()),
		    CommonModelConverter.asApiUuids(request.getEntityIdsList()),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListAlertsForIndexResponse.Builder response = GListAlertsForIndexResponse.newBuilder();
	    GDeviceAlertSearchResults.Builder results = GDeviceAlertSearchResults.newBuilder();
	    for (IDeviceAlert api : apiResult.getResults()) {
		results.addAlerts(EventModelConverter.asGrpcDeviceAlert(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getListAlertsForIndexMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getListAlertsForIndexMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddStreamDataForAssignmentMethod());
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
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddStreamDataForAssignmentMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddStreamDataForAssignmentMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getGetStreamDataForAssignmentMethod());
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
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getGetStreamDataForAssignmentMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getGetStreamDataForAssignmentMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getListStreamDataForAssignmentMethod());
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
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getListStreamDataForAssignmentMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getListStreamDataForAssignmentMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addCommandInvocation(com.sitewhere.grpc.service
     * .GAddCommandInvocationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addCommandInvocation(GAddCommandInvocationRequest request,
	    StreamObserver<GAddCommandInvocationResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddCommandInvocationMethod());
	    IDeviceCommandInvocation apiResult = getDeviceEventManagement().addDeviceCommandInvocation(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()),
		    EventModelConverter.asApiDeviceCommandInvocationCreateRequest(request.getRequest()));
	    GAddCommandInvocationResponse.Builder response = GAddCommandInvocationResponse.newBuilder();
	    if (apiResult != null) {
		response.setInvocation(EventModelConverter.asGrpcDeviceCommandInvocation(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddCommandInvocationMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddCommandInvocationMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listCommandInvocationsForIndex(com.sitewhere.
     * grpc.service.GListCommandInvocationsForIndexRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCommandInvocationsForIndex(GListCommandInvocationsForIndexRequest request,
	    StreamObserver<GListCommandInvocationsForIndexResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this,
		    DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod());
	    ISearchResults<IDeviceCommandInvocation> apiResult = getDeviceEventManagement()
		    .listDeviceCommandInvocationsForIndex(EventModelConverter.asApiDeviceEventIndex(request.getIndex()),
			    CommonModelConverter.asApiUuids(request.getEntityIdsList()),
			    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListCommandInvocationsForIndexResponse.Builder response = GListCommandInvocationsForIndexResponse
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
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod(),
		    e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getListCommandInvocationsForIndexMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addCommandResponse(com.sitewhere.grpc.service.
     * GAddCommandResponseRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addCommandResponse(GAddCommandResponseRequest request,
	    StreamObserver<GAddCommandResponseResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddCommandResponseMethod());
	    IDeviceCommandResponse apiResult = getDeviceEventManagement().addDeviceCommandResponse(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()),
		    EventModelConverter.asApiDeviceCommandResponseCreateRequest(request.getRequest()));
	    GAddCommandResponseResponse.Builder response = GAddCommandResponseResponse.newBuilder();
	    if (apiResult != null) {
		response.setResponse(EventModelConverter.asGrpcDeviceCommandResponse(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddCommandResponseMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddCommandResponseMethod());
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
	    GrpcUtils.handleServerMethodEntry(this,
		    DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod());
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
		    DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getListCommandResponsesForInvocationMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listCommandResponsesForIndex(com.sitewhere.grpc
     * .service.GListCommandResponsesForIndexRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCommandResponsesForIndex(GListCommandResponsesForIndexRequest request,
	    StreamObserver<GListCommandResponsesForIndexResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod());
	    ISearchResults<IDeviceCommandResponse> apiResult = getDeviceEventManagement()
		    .listDeviceCommandResponsesForIndex(EventModelConverter.asApiDeviceEventIndex(request.getIndex()),
			    CommonModelConverter.asApiUuids(request.getEntityIdsList()),
			    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListCommandResponsesForIndexResponse.Builder response = GListCommandResponsesForIndexResponse.newBuilder();
	    GDeviceCommandResponseSearchResults.Builder results = GDeviceCommandResponseSearchResults.newBuilder();
	    for (IDeviceCommandResponse api : apiResult.getResults()) {
		results.addResponses(EventModelConverter.asGrpcDeviceCommandResponse(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getListCommandResponsesForIndexMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addStateChange(com.sitewhere.grpc.service.
     * GAddStateChangeRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addStateChange(GAddStateChangeRequest request,
	    StreamObserver<GAddStateChangeResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddStateChangeMethod());
	    IDeviceStateChange apiResult = getDeviceEventManagement().addDeviceStateChange(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()),
		    EventModelConverter.asApiDeviceStateChangeCreateRequest(request.getRequest()));
	    GAddStateChangeResponse.Builder response = GAddStateChangeResponse.newBuilder();
	    if (apiResult != null) {
		response.setStateChange(EventModelConverter.asGrpcDeviceStateChange(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddStateChangeMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddStateChangeMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listStateChangesForIndex(com.sitewhere.grpc.
     * service.GListStateChangesForIndexRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listStateChangesForIndex(GListStateChangesForIndexRequest request,
	    StreamObserver<GListStateChangesForIndexResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getListStateChangesForIndexMethod());
	    ISearchResults<IDeviceStateChange> apiResult = getDeviceEventManagement().listDeviceStateChangesForIndex(
		    EventModelConverter.asApiDeviceEventIndex(request.getIndex()),
		    CommonModelConverter.asApiUuids(request.getEntityIdsList()),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListStateChangesForIndexResponse.Builder response = GListStateChangesForIndexResponse.newBuilder();
	    GDeviceStateChangeSearchResults.Builder results = GDeviceStateChangeSearchResults.newBuilder();
	    for (IDeviceStateChange api : apiResult.getResults()) {
		results.addStateChanges(EventModelConverter.asGrpcDeviceStateChange(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getListStateChangesForIndexMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getListStateChangesForIndexMethod());
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation#getMicroservice()
     */
    @Override
    public IMicroservice<?> getMicroservice() {
	return microservice;
    }

    public void setDeviceEventManagement(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
    }
}