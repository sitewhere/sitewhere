/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.event.grpc;

import java.util.List;

import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.event.EventModelConverter;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAlertSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandInvocationSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandResponseSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceLocationSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceMeasurementSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceStateChangeSearchResults;
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
import com.sitewhere.microservice.api.event.IDeviceEventManagement;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceAlert;
import com.sitewhere.spi.device.event.IDeviceCommandInvocation;
import com.sitewhere.spi.device.event.IDeviceCommandResponse;
import com.sitewhere.spi.device.event.IDeviceEvent;
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
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.search.ISearchResults;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for device event management GRPC requests.
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
		    .addDeviceEventBatch(EventModelConverter.asApiDeviceEventContext(request.getContext()), apiRequest);
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
	    IDeviceEvent apiResult = getDeviceEventManagement()
		    .getDeviceEventById(CommonModelConverter.asApiUuid(request.getEventId()));
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
	    IDeviceEvent apiResult = getDeviceEventManagement().getDeviceEventByAlternateId(request.getAlternateId());
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
     * DeviceEventManagementImplBase#addMeasurements(com.sitewhere.grpc.service.
     * GAddMeasurementsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addMeasurements(GAddMeasurementsRequest request,
	    StreamObserver<GAddMeasurementsResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddMeasurementsMethod());
	    List<? extends IDeviceMeasurement> apiResult = getDeviceEventManagement().addDeviceMeasurements(
		    EventModelConverter.asApiDeviceEventContext(request.getContext()),
		    EventModelConverter.asApiDeviceMeasurementCreateRequests(request.getRequestsList())
			    .toArray(new IDeviceMeasurementCreateRequest[0]));
	    GAddMeasurementsResponse.Builder response = GAddMeasurementsResponse.newBuilder();
	    if (apiResult != null) {
		response.addAllMeasurements(EventModelConverter.asGrpcDeviceMeasurements(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddMeasurementsMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddMeasurementsMethod());
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
     * DeviceEventManagementImplBase#addLocations(com.sitewhere.grpc.service.
     * GAddLocationsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addLocations(GAddLocationsRequest request, StreamObserver<GAddLocationsResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddLocationsMethod());
	    List<? extends IDeviceLocation> apiResult = getDeviceEventManagement().addDeviceLocations(
		    EventModelConverter.asApiDeviceEventContext(request.getContext()),
		    EventModelConverter.asApiDeviceLocationCreateRequests(request.getRequestsList())
			    .toArray(new IDeviceLocationCreateRequest[0]));
	    GAddLocationsResponse.Builder response = GAddLocationsResponse.newBuilder();
	    if (apiResult != null) {
		response.addAllLocations(EventModelConverter.asGrpcDeviceLocations(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddLocationsMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddLocationsMethod());
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
     * DeviceEventManagementImplBase#addAlerts(com.sitewhere.grpc.service.
     * GAddAlertsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addAlerts(GAddAlertsRequest request, StreamObserver<GAddAlertsResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddAlertsMethod());
	    List<? extends IDeviceAlert> apiResult = getDeviceEventManagement().addDeviceAlerts(
		    EventModelConverter.asApiDeviceEventContext(request.getContext()),
		    EventModelConverter.asApiDeviceAlertCreateRequests(request.getRequestsList())
			    .toArray(new IDeviceAlertCreateRequest[0]));
	    GAddAlertsResponse.Builder response = GAddAlertsResponse.newBuilder();
	    if (apiResult != null) {
		response.addAllAlerts(EventModelConverter.asGrpcDeviceAlerts(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddAlertsMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddAlertsMethod());
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
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addCommandInvocations(com.sitewhere.grpc.
     * service.GAddCommandInvocationsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addCommandInvocations(GAddCommandInvocationsRequest request,
	    StreamObserver<GAddCommandInvocationsResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddCommandInvocationsMethod());
	    List<? extends IDeviceCommandInvocation> apiResult = getDeviceEventManagement().addDeviceCommandInvocations(
		    EventModelConverter.asApiDeviceEventContext(request.getContext()),
		    EventModelConverter.asApiDeviceCommandInvocationCreateRequests(request.getRequestsList())
			    .toArray(new IDeviceCommandInvocationCreateRequest[0]));
	    GAddCommandInvocationsResponse.Builder response = GAddCommandInvocationsResponse.newBuilder();
	    if (apiResult != null) {
		response.addAllInvocations(EventModelConverter.asGrpcDeviceCommandInvocations(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddCommandInvocationsMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddCommandInvocationsMethod());
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
     * DeviceEventManagementImplBase#addCommandResponses(com.sitewhere.grpc.service.
     * GAddCommandResponsesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addCommandResponses(GAddCommandResponsesRequest request,
	    StreamObserver<GAddCommandResponsesResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddCommandResponsesMethod());
	    List<? extends IDeviceCommandResponse> apiResult = getDeviceEventManagement().addDeviceCommandResponses(
		    EventModelConverter.asApiDeviceEventContext(request.getContext()),
		    EventModelConverter.asApiDeviceCommandResponseCreateRequests(request.getRequestsList())
			    .toArray(new IDeviceCommandResponseCreateRequest[0]));
	    GAddCommandResponsesResponse.Builder response = GAddCommandResponsesResponse.newBuilder();
	    if (apiResult != null) {
		response.addAllResponses(EventModelConverter.asGrpcDeviceCommandResponses(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddCommandResponsesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddCommandResponsesMethod());
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
		    .listDeviceCommandInvocationResponses(
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
     * DeviceEventManagementImplBase#addStateChanges(com.sitewhere.grpc.service.
     * GAddStateChangesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addStateChanges(GAddStateChangesRequest request,
	    StreamObserver<GAddStateChangesResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceEventManagementGrpc.getAddStateChangesMethod());
	    List<? extends IDeviceStateChange> apiResult = getDeviceEventManagement().addDeviceStateChanges(
		    EventModelConverter.asApiDeviceEventContext(request.getContext()),
		    EventModelConverter.asApiDeviceStateChangeCreateRequests(request.getRequestsList())
			    .toArray(new IDeviceStateChangeCreateRequest[0]));
	    GAddStateChangesResponse.Builder response = GAddStateChangesResponse.newBuilder();
	    if (apiResult != null) {
		response.addAllStateChanges(EventModelConverter.asGrpcDeviceStateChanges(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceEventManagementGrpc.getAddStateChangesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceEventManagementGrpc.getAddStateChangesMethod());
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
    public IMicroservice<?, ?> getMicroservice() {
	return microservice;
    }

    public void setDeviceEventManagement(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
    }
}