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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.event.spi.microservice.IEventManagementMicroservice;
import com.sitewhere.event.spi.microservice.IEventManagementTenantEngine;
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
import com.sitewhere.microservice.grpc.GrpcTenantEngineProvider;
import com.sitewhere.spi.microservice.grpc.ITenantEngineCallback;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 */
public class EventManagementRouter extends DeviceEventManagementGrpc.DeviceEventManagementImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(EventManagementRouter.class);

    /** Parent microservice */
    private IEventManagementMicroservice microservice;

    /** Tenant engine provider */
    private GrpcTenantEngineProvider<IEventManagementTenantEngine> grpcTenantEngineProvider;

    public EventManagementRouter(IEventManagementMicroservice microservice) {
	this.microservice = microservice;
	this.grpcTenantEngineProvider = new GrpcTenantEngineProvider<>(microservice);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().addDeviceEventBatch(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().getDeviceEventById(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().getDeviceEventByAlternateId(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addMeasurements(com.sitewhere.grpc.service.
     * GAddMeasurementsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addMeasurements(GAddMeasurementsRequest request,
	    StreamObserver<GAddMeasurementsResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().addMeasurements(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listMeasurementsForIndex(com.sitewhere.grpc.
     * service.GListMeasurementsForIndexRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listMeasurementsForIndex(GListMeasurementsForIndexRequest request,
	    StreamObserver<GListMeasurementsForIndexResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().listMeasurementsForIndex(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addLocations(com.sitewhere.grpc.service.
     * GAddLocationsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addLocations(GAddLocationsRequest request, StreamObserver<GAddLocationsResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().addLocations(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listLocationsForIndex(com.sitewhere.grpc.
     * service.GListLocationsForIndexRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listLocationsForIndex(GListLocationsForIndexRequest request,
	    StreamObserver<GListLocationsForIndexResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().listLocationsForIndex(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addAlerts(com.sitewhere.grpc.service.
     * GAddAlertsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addAlerts(GAddAlertsRequest request, StreamObserver<GAddAlertsResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().addAlerts(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listAlertsForIndex(com.sitewhere.grpc.service.
     * GListAlertsForIndexRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAlertsForIndex(GListAlertsForIndexRequest request,
	    StreamObserver<GListAlertsForIndexResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().listAlertsForIndex(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addCommandInvocations(com.sitewhere.grpc.
     * service.GAddCommandInvocationsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addCommandInvocations(GAddCommandInvocationsRequest request,
	    StreamObserver<GAddCommandInvocationsResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().addCommandInvocations(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().listCommandInvocationsForIndex(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addCommandResponses(com.sitewhere.grpc.service.
     * GAddCommandResponsesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addCommandResponses(GAddCommandResponsesRequest request,
	    StreamObserver<GAddCommandResponsesResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().addCommandResponses(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().listCommandResponsesForInvocation(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listCommandResponsesForIndex(com.sitewhere.grpc
     * .service.GListCommandResponsesForIndexRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCommandResponsesForIndex(GListCommandResponsesForIndexRequest request,
	    StreamObserver<GListCommandResponsesForIndexResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().listCommandResponsesForIndex(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#addStateChanges(com.sitewhere.grpc.service.
     * GAddStateChangesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addStateChanges(GAddStateChangesRequest request,
	    StreamObserver<GAddStateChangesResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().addStateChanges(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceEventManagementGrpc.
     * DeviceEventManagementImplBase#listStateChangesForIndex(com.sitewhere.grpc.
     * service.GListStateChangesForIndexRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listStateChangesForIndex(GListStateChangesForIndexRequest request,
	    StreamObserver<GListStateChangesForIndexResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IEventManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IEventManagementTenantEngine tenantEngine) {
		tenantEngine.getEventManagementImpl().listStateChangesForIndex(request, responseObserver);
	    }
	}, responseObserver);
    }

    protected IEventManagementMicroservice getMicroservice() {
	return microservice;
    }

    protected GrpcTenantEngineProvider<IEventManagementTenantEngine> getGrpcTenantEngineProvider() {
	return grpcTenantEngineProvider;
    }
}