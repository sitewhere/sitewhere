/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.grpc;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcRouter;
import com.sitewhere.grpc.model.DeviceEventModel.GDeviceAssignmentEventCreateRequest;
import com.sitewhere.grpc.model.DeviceEventModel.GEventStreamAck;
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
import com.sitewhere.microservice.grpc.TenantTokenServerInterceptor;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 * 
 * @author Derek
 */
public class EventManagementRouter extends DeviceEventManagementGrpc.DeviceEventManagementImplBase
	implements IGrpcRouter<DeviceEventManagementGrpc.DeviceEventManagementImplBase> {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(EventManagementRouter.class);

    /** Parent microservice */
    private IEventManagementMicroservice microservice;

    public EventManagementRouter(IEventManagementMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.spi.grpc.IGrpcRouter#getTenantImplementation()
     */
    @Override
    public DeviceEventManagementGrpc.DeviceEventManagementImplBase getTenantImplementation(StreamObserver<?> observer) {
	String tenantId = TenantTokenServerInterceptor.TENANT_ID_KEY.get();
	if (tenantId == null) {
	    throw new RuntimeException("Tenant id not found in event management request.");
	}
	try {
	    IEventManagementTenantEngine engine = getMicroservice()
		    .assureTenantEngineAvailable(UUID.fromString(tenantId));
	    UserContextManager.setCurrentTenant(engine.getTenant());
	    return engine.getEventManagementImpl();
	} catch (TenantEngineNotAvailableException e) {
	    observer.onError(GrpcUtils.convertServerException(e));
	    return null;
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addDeviceEventBatch(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceEventById(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceEventByAlternateId(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    return engine.streamDeviceAssignmentEventCreateRequests(responseObserver);
	}
	return null;
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addMeasurementsForAssignment(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listMeasurementsForAssignments(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listMeasurementsForAreas(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addLocationForAssignment(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listLocationsForAssignments(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listLocationsForAreas(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addAlertForAssignment(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listAlertsForAssignments(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listAlertsForAreas(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addStreamDataForAssignment(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getStreamDataForAssignment(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listStreamDataForAssignment(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addCommandInvocationForAssignment(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listCommandInvocationsForAssignments(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listCommandInvocationsForAreas(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addCommandResponseForAssignment(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listCommandResponsesForInvocation(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listCommandResponsesForAssignments(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listCommandResponsesForAreas(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addStateChangeForAssignment(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listStateChangesForAssignments(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listStateChangesForAreas(request, responseObserver);
	}
    }

    public IEventManagementMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IEventManagementMicroservice microservice) {
	this.microservice = microservice;
    }
}