package com.sitewhere.event.grpc;

import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAlertSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandInvocationSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceCommandResponseSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceEventSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceLocationSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceMeasurementsSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceStateChangeSearchResults;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceStreamDataSearchResults;
import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
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
import com.sitewhere.grpc.service.GListAlertsForAssignmentRequest;
import com.sitewhere.grpc.service.GListAlertsForAssignmentResponse;
import com.sitewhere.grpc.service.GListAlertsForSiteRequest;
import com.sitewhere.grpc.service.GListAlertsForSiteResponse;
import com.sitewhere.grpc.service.GListCommandInvocationsForAssignmentRequest;
import com.sitewhere.grpc.service.GListCommandInvocationsForAssignmentResponse;
import com.sitewhere.grpc.service.GListCommandInvocationsForSiteRequest;
import com.sitewhere.grpc.service.GListCommandInvocationsForSiteResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForAssignmentRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForAssignmentResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForInvocationRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForInvocationResponse;
import com.sitewhere.grpc.service.GListCommandResponsesForSiteRequest;
import com.sitewhere.grpc.service.GListCommandResponsesForSiteResponse;
import com.sitewhere.grpc.service.GListDeviceEventsRequest;
import com.sitewhere.grpc.service.GListDeviceEventsResponse;
import com.sitewhere.grpc.service.GListLocationsForAssignmentRequest;
import com.sitewhere.grpc.service.GListLocationsForAssignmentResponse;
import com.sitewhere.grpc.service.GListLocationsForSiteRequest;
import com.sitewhere.grpc.service.GListLocationsForSiteResponse;
import com.sitewhere.grpc.service.GListMeasurementsForAssignmentRequest;
import com.sitewhere.grpc.service.GListMeasurementsForAssignmentResponse;
import com.sitewhere.grpc.service.GListMeasurementsForSiteRequest;
import com.sitewhere.grpc.service.GListMeasurementsForSiteResponse;
import com.sitewhere.grpc.service.GListStateChangesForAssignmentRequest;
import com.sitewhere.grpc.service.GListStateChangesForAssignmentResponse;
import com.sitewhere.grpc.service.GListStateChangesForSiteRequest;
import com.sitewhere.grpc.service.GListStateChangesForSiteResponse;
import com.sitewhere.grpc.service.GListStreamDataForAssignmentRequest;
import com.sitewhere.grpc.service.GListStreamDataForAssignmentResponse;
import com.sitewhere.grpc.service.GUpdateDeviceEventRequest;
import com.sitewhere.grpc.service.GUpdateDeviceEventResponse;
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
		    .addDeviceEventBatch(request.getAssignmentToken(), apiRequest);
	    GAddDeviceEventBatchResponse.Builder response = GAddDeviceEventBatchResponse.newBuilder();
	    response.setResponse(EventModelConverter.asGrpcDeviceEventBatchResponse(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_DEVICE_EVENT_BATCH, e);
	    responseObserver.onError(e);
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
	    IDeviceEvent apiResult = getDeviceEventManagement().getDeviceEventById(request.getId());
	    GGetDeviceEventByIdResponse.Builder response = GGetDeviceEventByIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setEvent(EventModelConverter.asGrpcGenericDeviceEvent(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ID, e);
	    responseObserver.onError(e);
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
	    IDeviceEvent apiResult = getDeviceEventManagement().getDeviceEventById(request.getAlternateId());
	    GGetDeviceEventByAlternateIdResponse.Builder response = GGetDeviceEventByAlternateIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setEvent(EventModelConverter.asGrpcGenericDeviceEvent(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_GET_DEVICE_EVENT_BY_ALTERNATE_ID, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listDeviceEvents(com.sitewhere.grpc.service
     * .GListDeviceEventsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceEvents(GListDeviceEventsRequest request,
	    StreamObserver<GListDeviceEventsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_DEVICE_EVENTS);
	    ISearchResults<IDeviceEvent> apiResult = getDeviceEventManagement().listDeviceEvents(
		    request.getAssignmentToken(),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria().getCriteria()));
	    GListDeviceEventsResponse.Builder response = GListDeviceEventsResponse.newBuilder();
	    GDeviceEventSearchResults.Builder results = GDeviceEventSearchResults.newBuilder();
	    for (IDeviceEvent api : apiResult.getResults()) {
		results.addEvents(EventModelConverter.asGrpcGenericDeviceEvent(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_DEVICE_EVENTS, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#updateDeviceEvent(com.sitewhere.grpc.
     * service.GUpdateDeviceEventRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceEvent(GUpdateDeviceEventRequest request,
	    StreamObserver<GUpdateDeviceEventResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_UPDATE_DEVICE_EVENT);
	    IDeviceEvent apiResult = getDeviceEventManagement().updateDeviceEvent(request.getId(),
		    EventModelConverter.asApiDeviceEventCreateRequest(request.getEvent()));
	    GUpdateDeviceEventResponse.Builder response = GUpdateDeviceEventResponse.newBuilder();
	    if (apiResult != null) {
		response.setEvent(EventModelConverter.asGrpcGenericDeviceEvent(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_UPDATE_DEVICE_EVENT, e);
	    responseObserver.onError(e);
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
		    request.getAssignmentToken(),
		    EventModelConverter.asApiDeviceMeasurementsCreateRequest(request.getRequest()));
	    GAddMeasurementsForAssignmentResponse.Builder response = GAddMeasurementsForAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setMeasurements(EventModelConverter.asGrpcDeviceMeasurements(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_MEASUREMENTS_FOR_ASSIGNMENT, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listMeasurementsForAssignment(com.sitewhere
     * .grpc.service.GListMeasurementsForAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listMeasurementsForAssignment(GListMeasurementsForAssignmentRequest request,
	    StreamObserver<GListMeasurementsForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_ASSIGNMENT);
	    ISearchResults<IDeviceMeasurements> apiResult = getDeviceEventManagement().listDeviceMeasurements(
		    request.getAssignmentToken(),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListMeasurementsForAssignmentResponse.Builder response = GListMeasurementsForAssignmentResponse
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
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_ASSIGNMENT, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listMeasurementsForSite(com.sitewhere.grpc.
     * service.GListMeasurementsForSiteRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listMeasurementsForSite(GListMeasurementsForSiteRequest request,
	    StreamObserver<GListMeasurementsForSiteResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_SITE);
	    ISearchResults<IDeviceMeasurements> apiResult = getDeviceEventManagement().listDeviceMeasurementsForSite(
		    request.getSiteToken(), CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListMeasurementsForSiteResponse.Builder response = GListMeasurementsForSiteResponse.newBuilder();
	    GDeviceMeasurementsSearchResults.Builder results = GDeviceMeasurementsSearchResults.newBuilder();
	    for (IDeviceMeasurements api : apiResult.getResults()) {
		results.addMeasurements(EventModelConverter.asGrpcDeviceMeasurements(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_MEASUREMENTS_FOR_SITE, e);
	    responseObserver.onError(e);
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
	    IDeviceLocation apiResult = getDeviceEventManagement().addDeviceLocation(request.getAssignmentToken(),
		    EventModelConverter.asApiDeviceLocationCreateRequest(request.getRequest()));
	    GAddLocationForAssignmentResponse.Builder response = GAddLocationForAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setLocation(EventModelConverter.asGrpcDeviceLocation(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_LOCATION_FOR_ASSIGNMENT, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listLocationsForAssignment(com.sitewhere.
     * grpc.service.GListLocationsForAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listLocationsForAssignment(GListLocationsForAssignmentRequest request,
	    StreamObserver<GListLocationsForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_ASSIGNMENT);
	    ISearchResults<IDeviceLocation> apiResult = getDeviceEventManagement().listDeviceLocations(
		    request.getAssignmentToken(),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListLocationsForAssignmentResponse.Builder response = GListLocationsForAssignmentResponse.newBuilder();
	    GDeviceLocationSearchResults.Builder results = GDeviceLocationSearchResults.newBuilder();
	    for (IDeviceLocation api : apiResult.getResults()) {
		results.addLocations(EventModelConverter.asGrpcDeviceLocation(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_ASSIGNMENT, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listLocationsForSite(com.sitewhere.grpc.
     * service.GListLocationsForSiteRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listLocationsForSite(GListLocationsForSiteRequest request,
	    StreamObserver<GListLocationsForSiteResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_SITE);
	    ISearchResults<IDeviceLocation> apiResult = getDeviceEventManagement().listDeviceLocationsForSite(
		    request.getSiteToken(), CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListLocationsForSiteResponse.Builder response = GListLocationsForSiteResponse.newBuilder();
	    GDeviceLocationSearchResults.Builder results = GDeviceLocationSearchResults.newBuilder();
	    for (IDeviceLocation api : apiResult.getResults()) {
		results.addLocations(EventModelConverter.asGrpcDeviceLocation(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_LOCATIONS_FOR_SITE, e);
	    responseObserver.onError(e);
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
	    IDeviceAlert apiResult = getDeviceEventManagement().addDeviceAlert(request.getAssignmentToken(),
		    EventModelConverter.asApiDeviceAlertCreateRequest(request.getRequest()));
	    GAddAlertForAssignmentResponse.Builder response = GAddAlertForAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setAlert(EventModelConverter.asGrpcDeviceAlert(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_ALERT_FOR_ASSIGNMENT, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listAlertsForAssignment(com.sitewhere.grpc.
     * service.GListAlertsForAssignmentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAlertsForAssignment(GListAlertsForAssignmentRequest request,
	    StreamObserver<GListAlertsForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_ASSIGNMENT);
	    ISearchResults<IDeviceAlert> apiResult = getDeviceEventManagement().listDeviceAlerts(
		    request.getAssignmentToken(),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListAlertsForAssignmentResponse.Builder response = GListAlertsForAssignmentResponse.newBuilder();
	    GDeviceAlertSearchResults.Builder results = GDeviceAlertSearchResults.newBuilder();
	    for (IDeviceAlert api : apiResult.getResults()) {
		results.addAlerts(EventModelConverter.asGrpcDeviceAlert(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_ASSIGNMENT, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listAlertsForSite(com.sitewhere.grpc.
     * service.GListAlertsForSiteRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAlertsForSite(GListAlertsForSiteRequest request,
	    StreamObserver<GListAlertsForSiteResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_SITE);
	    ISearchResults<IDeviceAlert> apiResult = getDeviceEventManagement().listDeviceAlerts(request.getSiteToken(),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListAlertsForSiteResponse.Builder response = GListAlertsForSiteResponse.newBuilder();
	    GDeviceAlertSearchResults.Builder results = GDeviceAlertSearchResults.newBuilder();
	    for (IDeviceAlert api : apiResult.getResults()) {
		results.addAlerts(EventModelConverter.asGrpcDeviceAlert(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_SITE, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addStreamDataForAssignment(com.sitewhere.
     * grpc.service.GAddStreamDataForAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void addStreamDataForAssignment(GAddStreamDataForAssignmentRequest request,
	    StreamObserver<GAddStreamDataForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_ADD_STREAM_DATA_FOR_ASSIGNMENT);
	    IDeviceStreamData apiResult = getDeviceEventManagement().addDeviceStreamData(request.getAssignmentToken(),
		    EventModelConverter.asApiDeviceStreamDataCreateRequest(request.getRequest()));
	    GAddStreamDataForAssignmentResponse.Builder response = GAddStreamDataForAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setStreamData(EventModelConverter.asGrpcDeviceStreamData(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_STREAM_DATA_FOR_ASSIGNMENT, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#getStreamDataForAssignment(com.sitewhere.
     * grpc.service.GGetStreamDataForAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getStreamDataForAssignment(GGetStreamDataForAssignmentRequest request,
	    StreamObserver<GGetStreamDataForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_GET_STREAM_DATA_FOR_ASSIGNMENT);
	    IDeviceStreamData apiResult = getDeviceEventManagement().getDeviceStreamData(request.getAssignmentToken(),
		    request.getStreamId(), request.getSequenceNumber());
	    GGetStreamDataForAssignmentResponse.Builder response = GGetStreamDataForAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setStreamData(EventModelConverter.asGrpcDeviceStreamData(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_GET_STREAM_DATA_FOR_ASSIGNMENT, e);
	    responseObserver.onError(e);
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
	    ISearchResults<IDeviceStreamData> apiResult = getDeviceEventManagement().listDeviceStreamData(
		    request.getAssignmentToken(), request.getStreamId(),
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
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_ALERTS_FOR_SITE, e);
	    responseObserver.onError(e);
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
		    request.getAssignmentToken(),
		    EventModelConverter.asApiDeviceCommandInvocationCreateRequest(request.getRequest()));
	    GAddCommandInvocationForAssignmentResponse.Builder response = GAddCommandInvocationForAssignmentResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.setInvocation(EventModelConverter.asGrpcDeviceCommandInvocation(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_INVOCATION_FOR_ASSIGNMENT,
		    e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listCommandInvocationsForAssignment(com.
     * sitewhere.grpc.service.GListCommandInvocationsForAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCommandInvocationsForAssignment(GListCommandInvocationsForAssignmentRequest request,
	    StreamObserver<GListCommandInvocationsForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_ASSIGNMENT);
	    ISearchResults<IDeviceCommandInvocation> apiResult = getDeviceEventManagement()
		    .listDeviceCommandInvocations(request.getAssignmentToken(),
			    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListCommandInvocationsForAssignmentResponse.Builder response = GListCommandInvocationsForAssignmentResponse
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
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_ASSIGNMENT,
		    e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listCommandInvocationsForSite(com.sitewhere
     * .grpc.service.GListCommandInvocationsForSiteRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCommandInvocationsForSite(GListCommandInvocationsForSiteRequest request,
	    StreamObserver<GListCommandInvocationsForSiteResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_SITE);
	    ISearchResults<IDeviceCommandInvocation> apiResult = getDeviceEventManagement()
		    .listDeviceCommandInvocationsForSite(request.getSiteToken(),
			    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListCommandInvocationsForSiteResponse.Builder response = GListCommandInvocationsForSiteResponse
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
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_INVOCATIONS_FOR_SITE, e);
	    responseObserver.onError(e);
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
		    request.getAssignmentToken(),
		    EventModelConverter.asApiDeviceCommandResponseCreateRequest(request.getRequest()));
	    GAddCommandResponseForAssignmentResponse.Builder response = GAddCommandResponseForAssignmentResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.setResponse(EventModelConverter.asGrpcDeviceCommandResponse(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_COMMAND_RESPONSE_FOR_ASSIGNMENT, e);
	    responseObserver.onError(e);
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
		    .listDeviceCommandInvocationResponses(request.getInvocationId());
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
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_INVOCATION,
		    e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listCommandResponsesForAssignment(com.
     * sitewhere.grpc.service.GListCommandResponsesForAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCommandResponsesForAssignment(GListCommandResponsesForAssignmentRequest request,
	    StreamObserver<GListCommandResponsesForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_ASSIGNMENT);
	    ISearchResults<IDeviceCommandResponse> apiResult = getDeviceEventManagement().listDeviceCommandResponses(
		    request.getAssignmentToken(),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListCommandResponsesForAssignmentResponse.Builder response = GListCommandResponsesForAssignmentResponse
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
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_ASSIGNMENT,
		    e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listCommandResponsesForSite(com.sitewhere.
     * grpc.service.GListCommandResponsesForSiteRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCommandResponsesForSite(GListCommandResponsesForSiteRequest request,
	    StreamObserver<GListCommandResponsesForSiteResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_SITE);
	    ISearchResults<IDeviceCommandResponse> apiResult = getDeviceEventManagement()
		    .listDeviceCommandResponsesForSite(request.getSiteToken(),
			    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListCommandResponsesForSiteResponse.Builder response = GListCommandResponsesForSiteResponse.newBuilder();
	    GDeviceCommandResponseSearchResults.Builder results = GDeviceCommandResponseSearchResults.newBuilder();
	    for (IDeviceCommandResponse api : apiResult.getResults()) {
		results.addResponses(EventModelConverter.asGrpcDeviceCommandResponse(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_COMMAND_RESPONSES_FOR_SITE, e);
	    responseObserver.onError(e);
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
	    IDeviceStateChange apiResult = getDeviceEventManagement().addDeviceStateChange(request.getAssignmentToken(),
		    EventModelConverter.asApiDeviceStateChangeCreateRequest(request.getRequest()));
	    GAddStateChangeForAssignmentResponse.Builder response = GAddStateChangeForAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setStateChange(EventModelConverter.asGrpcDeviceStateChange(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_ADD_STATE_CHANGE_FOR_ASSIGNMENT, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listStateChangesForAssignment(com.sitewhere
     * .grpc.service.GListStateChangesForAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listStateChangesForAssignment(GListStateChangesForAssignmentRequest request,
	    StreamObserver<GListStateChangesForAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENT);
	    ISearchResults<IDeviceStateChange> apiResult = getDeviceEventManagement().listDeviceStateChanges(
		    request.getAssignmentToken(),
		    CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListStateChangesForAssignmentResponse.Builder response = GListStateChangesForAssignmentResponse
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
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENT, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listStateChangesForSite(com.sitewhere.grpc.
     * service.GListStateChangesForSiteRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listStateChangesForSite(GListStateChangesForSiteRequest request,
	    StreamObserver<GListStateChangesForSiteResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENT);
	    ISearchResults<IDeviceStateChange> apiResult = getDeviceEventManagement().listDeviceStateChanges(
		    request.getSiteToken(), CommonModelConverter.asDateRangeSearchCriteria(request.getCriteria()));
	    GListStateChangesForSiteResponse.Builder response = GListStateChangesForSiteResponse.newBuilder();
	    GDeviceStateChangeSearchResults.Builder results = GDeviceStateChangeSearchResults.newBuilder();
	    for (IDeviceStateChange api : apiResult.getResults()) {
		results.addStateChanges(EventModelConverter.asGrpcDeviceStateChange(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceEventManagementGrpc.METHOD_LIST_STATE_CHANGES_FOR_ASSIGNMENT, e);
	    responseObserver.onError(e);
	}
    }

    public void setDeviceEventManagement(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
    }
}