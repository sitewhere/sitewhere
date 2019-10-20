/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
import com.sitewhere.grpc.client.GrpcContextKeys;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcRouter;
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
	String token = GrpcContextKeys.TENANT_TOKEN_KEY.get();
	if (token == null) {
	    throw new RuntimeException("Tenant token not found in request.");
	}
	try {
	    IEventManagementTenantEngine engine = getMicroservice().assureTenantEngineAvailable(token);
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
     * DeviceEventManagementImplBase#addLocations(com.sitewhere.grpc.service.
     * GAddLocationsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addLocations(GAddLocationsRequest request, StreamObserver<GAddLocationsResponse> responseObserver) {
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addLocations(request, responseObserver);
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
     * DeviceEventManagementImplBase#addAlerts(com.sitewhere.grpc.service.
     * GAddAlertsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addAlerts(GAddAlertsRequest request, StreamObserver<GAddAlertsResponse> responseObserver) {
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addAlerts(request, responseObserver);
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
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addCommandInvocations(com.sitewhere.grpc.
     * service.GAddCommandInvocationsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addCommandInvocations(GAddCommandInvocationsRequest request,
	    StreamObserver<GAddCommandInvocationsResponse> responseObserver) {
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addCommandInvocations(request, responseObserver);
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
     * DeviceEventManagementImplBase#addCommandResponses(com.sitewhere.grpc.service.
     * GAddCommandResponsesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addCommandResponses(GAddCommandResponsesRequest request,
	    StreamObserver<GAddCommandResponsesResponse> responseObserver) {
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addCommandResponses(request, responseObserver);
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
     * DeviceEventManagementImplBase#addStateChanges(com.sitewhere.grpc.service.
     * GAddStateChangesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addStateChanges(GAddStateChangesRequest request,
	    StreamObserver<GAddStateChangesResponse> responseObserver) {
	DeviceEventManagementGrpc.DeviceEventManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addStateChanges(request, responseObserver);
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