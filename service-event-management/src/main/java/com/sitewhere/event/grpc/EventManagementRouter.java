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
import com.sitewhere.grpc.client.GrpcContextKeys;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcRouter;
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
	String tenantId = GrpcContextKeys.TENANT_ID_KEY.get();
	if (tenantId == null) {
	    throw new RuntimeException("Tenant id not found in event management request.");
	}
	try {
	    IEventManagementTenantEngine engine = getMicroservice()
		    .assureTenantEngineAvailable(UUID.fromString(tenantId));
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
     * DeviceEventManagementImplBase#addMeasurements(com.sitewhere.grpc.service.
     * GAddMeasurementsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addMeasurements(GAddMeasurementsRequest request,
	    StreamObserver<GAddMeasurementsResponse> responseObserver) {
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addMeasurements(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listMeasurementsForIndex(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addLocation(com.sitewhere.grpc.service.
     * GAddLocationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addLocation(GAddLocationRequest request, StreamObserver<GAddLocationResponse> responseObserver) {
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addLocation(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listLocationsForIndex(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addAlert(com.sitewhere.grpc.service.
     * GAddAlertRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addAlert(GAddAlertRequest request, StreamObserver<GAddAlertResponse> responseObserver) {
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addAlert(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listAlertsForIndex(request, responseObserver);
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
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addCommandInvocation(com.sitewhere.grpc.service
     * .GAddCommandInvocationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addCommandInvocation(GAddCommandInvocationRequest request,
	    StreamObserver<GAddCommandInvocationResponse> responseObserver) {
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addCommandInvocation(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listCommandInvocationsForIndex(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addCommandResponse(request, responseObserver);
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
     * DeviceEventManagementImplBase#listCommandResponsesForIndex(com.sitewhere.grpc
     * .service.GListCommandResponsesForIndexRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCommandResponsesForIndex(GListCommandResponsesForIndexRequest request,
	    StreamObserver<GListCommandResponsesForIndexResponse> responseObserver) {
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listCommandResponsesForIndex(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addStateChange(request, responseObserver);
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
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listStateChangesForIndex(request, responseObserver);
	}
    }

    public IEventManagementMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IEventManagementMicroservice microservice) {
	this.microservice = microservice;
    }
}