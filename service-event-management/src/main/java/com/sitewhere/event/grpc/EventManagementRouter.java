/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.grpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
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
import com.sitewhere.microservice.grpc.TenantTokenServerInterceptor;
import com.sitewhere.spi.SiteWhereException;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 * 
 * @author Derek
 */
public class EventManagementRouter extends DeviceEventManagementGrpc.DeviceEventManagementImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** Parent microservice */
    private IEventManagementMicroservice microservice;

    public EventManagementRouter(IEventManagementMicroservice microservice) {
	this.microservice = microservice;
    }

    /**
     * Based on token passed via GRPC header, look up service implementation
     * running in tenant engine.
     * 
     * @return
     */
    protected DeviceEventManagementGrpc.DeviceEventManagementImplBase getTenantImplementation() {
	String tenantToken = TenantTokenServerInterceptor.TENANT_TOKEN_KEY.get();
	if (tenantToken == null) {
	    throw new RuntimeException("Tenant token not found in event management request.");
	}
	try {
	    IEventManagementTenantEngine engine = getMicroservice().getTenantEngineByTenantId(tenantToken);
	    if (engine != null) {
		return engine.getEventManagementImpl();
	    }
	    throw new RuntimeException("Tenant engine not found.");
	} catch (SiteWhereException e) {
	    throw new RuntimeException("Error locating tenant engine.", e);
	}
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
	getTenantImplementation().addDeviceEventBatch(request, responseObserver);
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
	getTenantImplementation().getDeviceEventById(request, responseObserver);
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
	getTenantImplementation().getDeviceEventByAlternateId(request, responseObserver);
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
	getTenantImplementation().listDeviceEvents(request, responseObserver);
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
	getTenantImplementation().updateDeviceEvent(request, responseObserver);
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
	getTenantImplementation().addMeasurementsForAssignment(request, responseObserver);
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
	getTenantImplementation().listMeasurementsForAssignment(request, responseObserver);
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
	getTenantImplementation().listMeasurementsForSite(request, responseObserver);
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
	getTenantImplementation().addLocationForAssignment(request, responseObserver);
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
	getTenantImplementation().listLocationsForAssignment(request, responseObserver);
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
	getTenantImplementation().listLocationsForSite(request, responseObserver);
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
	getTenantImplementation().addAlertForAssignment(request, responseObserver);
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
	getTenantImplementation().listAlertsForAssignment(request, responseObserver);
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
	getTenantImplementation().listAlertsForSite(request, responseObserver);
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
	getTenantImplementation().addStreamDataForAssignment(request, responseObserver);
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
	getTenantImplementation().getStreamDataForAssignment(request, responseObserver);
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
	getTenantImplementation().listStreamDataForAssignment(request, responseObserver);
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
	getTenantImplementation().addCommandInvocationForAssignment(request, responseObserver);
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
	getTenantImplementation().listCommandInvocationsForAssignment(request, responseObserver);
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
	getTenantImplementation().listCommandInvocationsForSite(request, responseObserver);
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
	getTenantImplementation().addCommandResponseForAssignment(request, responseObserver);
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
	getTenantImplementation().listCommandResponsesForInvocation(request, responseObserver);
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
	getTenantImplementation().listCommandResponsesForAssignment(request, responseObserver);
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
	getTenantImplementation().listCommandResponsesForSite(request, responseObserver);
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
	getTenantImplementation().addStateChangeForAssignment(request, responseObserver);
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
	getTenantImplementation().listStateChangesForAssignment(request, responseObserver);
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
	getTenantImplementation().listStateChangesForSite(request, responseObserver);
    }

    public IEventManagementMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IEventManagementMicroservice microservice) {
	this.microservice = microservice;
    }
}