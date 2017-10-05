package com.sitewhere.event.grpc;

import com.sitewhere.grpc.model.GrpcUtils;
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
import com.sitewhere.grpc.service.GListStreamDataForSiteRequest;
import com.sitewhere.grpc.service.GListStreamDataForSiteResponse;
import com.sitewhere.grpc.service.GUpdateDeviceEventRequest;
import com.sitewhere.grpc.service.GUpdateDeviceEventResponse;
import com.sitewhere.rest.model.device.event.DeviceEventBatch;
import com.sitewhere.spi.device.event.IDeviceEventBatchResponse;
import com.sitewhere.spi.device.event.IDeviceEventManagement;

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

    @Override
    public void getDeviceEventById(GGetDeviceEventByIdRequest request,
	    StreamObserver<GGetDeviceEventByIdResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getDeviceEventById(request, responseObserver);
    }

    @Override
    public void getDeviceEventByAlternateId(GGetDeviceEventByAlternateIdRequest request,
	    StreamObserver<GGetDeviceEventByAlternateIdResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getDeviceEventByAlternateId(request, responseObserver);
    }

    @Override
    public void listDeviceEvents(GListDeviceEventsRequest request,
	    StreamObserver<GListDeviceEventsResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listDeviceEvents(request, responseObserver);
    }

    @Override
    public void updateDeviceEvent(GUpdateDeviceEventRequest request,
	    StreamObserver<GUpdateDeviceEventResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.updateDeviceEvent(request, responseObserver);
    }

    @Override
    public void addMeasurementsForAssignment(GAddMeasurementsForAssignmentRequest request,
	    StreamObserver<GAddMeasurementsForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.addMeasurementsForAssignment(request, responseObserver);
    }

    @Override
    public void listMeasurementsForAssignment(GListMeasurementsForAssignmentRequest request,
	    StreamObserver<GListMeasurementsForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listMeasurementsForAssignment(request, responseObserver);
    }

    @Override
    public void listMeasurementsForSite(GListMeasurementsForSiteRequest request,
	    StreamObserver<GListMeasurementsForSiteResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listMeasurementsForSite(request, responseObserver);
    }

    @Override
    public void addLocationForAssignment(GAddLocationForAssignmentRequest request,
	    StreamObserver<GAddLocationForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.addLocationForAssignment(request, responseObserver);
    }

    @Override
    public void listLocationsForAssignment(GListLocationsForAssignmentRequest request,
	    StreamObserver<GListLocationsForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listLocationsForAssignment(request, responseObserver);
    }

    @Override
    public void listLocationsForSite(GListLocationsForSiteRequest request,
	    StreamObserver<GListLocationsForSiteResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listLocationsForSite(request, responseObserver);
    }

    @Override
    public void addAlertForAssignment(GAddAlertForAssignmentRequest request,
	    StreamObserver<GAddAlertForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.addAlertForAssignment(request, responseObserver);
    }

    @Override
    public void listAlertsForAssignment(GListAlertsForAssignmentRequest request,
	    StreamObserver<GListAlertsForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listAlertsForAssignment(request, responseObserver);
    }

    @Override
    public void listAlertsForSite(GListAlertsForSiteRequest request,
	    StreamObserver<GListAlertsForSiteResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listAlertsForSite(request, responseObserver);
    }

    @Override
    public void addStreamDataForAssignment(GAddStreamDataForAssignmentRequest request,
	    StreamObserver<GAddStreamDataForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.addStreamDataForAssignment(request, responseObserver);
    }

    @Override
    public void getStreamDataForAssignment(GGetStreamDataForAssignmentRequest request,
	    StreamObserver<GGetStreamDataForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.getStreamDataForAssignment(request, responseObserver);
    }

    @Override
    public void listStreamDataForAssignment(GListStreamDataForAssignmentRequest request,
	    StreamObserver<GListStreamDataForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listStreamDataForAssignment(request, responseObserver);
    }

    @Override
    public void listStreamDataForSite(GListStreamDataForSiteRequest request,
	    StreamObserver<GListStreamDataForSiteResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listStreamDataForSite(request, responseObserver);
    }

    @Override
    public void addCommandInvocationForAssignment(GAddCommandInvocationForAssignmentRequest request,
	    StreamObserver<GAddCommandInvocationForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.addCommandInvocationForAssignment(request, responseObserver);
    }

    @Override
    public void listCommandInvocationsForAssignment(GListCommandInvocationsForAssignmentRequest request,
	    StreamObserver<GListCommandInvocationsForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listCommandInvocationsForAssignment(request, responseObserver);
    }

    @Override
    public void listCommandInvocationsForSite(GListCommandInvocationsForSiteRequest request,
	    StreamObserver<GListCommandInvocationsForSiteResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listCommandInvocationsForSite(request, responseObserver);
    }

    @Override
    public void addCommandResponseForAssignment(GAddCommandResponseForAssignmentRequest request,
	    StreamObserver<GAddCommandResponseForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.addCommandResponseForAssignment(request, responseObserver);
    }

    @Override
    public void listCommandResponsesForInvocation(GListCommandResponsesForInvocationRequest request,
	    StreamObserver<GListCommandResponsesForInvocationResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listCommandResponsesForInvocation(request, responseObserver);
    }

    @Override
    public void listCommandResponsesForAssignment(GListCommandResponsesForAssignmentRequest request,
	    StreamObserver<GListCommandResponsesForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listCommandResponsesForAssignment(request, responseObserver);
    }

    @Override
    public void listCommandResponsesForSite(GListCommandResponsesForSiteRequest request,
	    StreamObserver<GListCommandResponsesForSiteResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listCommandResponsesForSite(request, responseObserver);
    }

    @Override
    public void addStateChangeForAssignment(GAddStateChangeForAssignmentRequest request,
	    StreamObserver<GAddStateChangeForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.addStateChangeForAssignment(request, responseObserver);
    }

    @Override
    public void listStateChangesForAssignment(GListStateChangesForAssignmentRequest request,
	    StreamObserver<GListStateChangesForAssignmentResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listStateChangesForAssignment(request, responseObserver);
    }

    @Override
    public void listStateChangesForSite(GListStateChangesForSiteRequest request,
	    StreamObserver<GListStateChangesForSiteResponse> responseObserver) {
	// TODO Auto-generated method stub
	super.listStateChangesForSite(request, responseObserver);
    }

    public void setDeviceEventManagement(IDeviceEventManagement deviceEventManagement) {
	this.deviceEventManagement = deviceEventManagement;
    }
}