/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.grpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.grpc.service.DeviceStateGrpc;
import com.sitewhere.grpc.service.GCreateDeviceStateRequest;
import com.sitewhere.grpc.service.GCreateDeviceStateResponse;
import com.sitewhere.grpc.service.GDeleteDeviceStateRequest;
import com.sitewhere.grpc.service.GDeleteDeviceStateResponse;
import com.sitewhere.grpc.service.GGetDeviceStateByAssignmentRequest;
import com.sitewhere.grpc.service.GGetDeviceStateByAssignmentResponse;
import com.sitewhere.grpc.service.GGetDeviceStateRequest;
import com.sitewhere.grpc.service.GGetDeviceStateResponse;
import com.sitewhere.grpc.service.GGetDeviceStatesByDeviceRequest;
import com.sitewhere.grpc.service.GGetDeviceStatesByDeviceResponse;
import com.sitewhere.grpc.service.GMergeDeviceStateRequest;
import com.sitewhere.grpc.service.GMergeDeviceStateResponse;
import com.sitewhere.grpc.service.GSearchDeviceStatesRequest;
import com.sitewhere.grpc.service.GSearchDeviceStatesResponse;
import com.sitewhere.grpc.service.GUpdateDeviceStateRequest;
import com.sitewhere.grpc.service.GUpdateDeviceStateResponse;
import com.sitewhere.microservice.grpc.GrpcTenantEngineProvider;
import com.sitewhere.spi.microservice.grpc.ITenantEngineCallback;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 */
public class DeviceStateRouter extends DeviceStateGrpc.DeviceStateImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceStateRouter.class);

    /** Parent microservice */
    private IDeviceStateMicroservice microservice;

    /** Tenant engine provider */
    private GrpcTenantEngineProvider<IDeviceStateTenantEngine> grpcTenantEngineProvider;

    public DeviceStateRouter(IDeviceStateMicroservice microservice) {
	this.microservice = microservice;
	this.grpcTenantEngineProvider = new GrpcTenantEngineProvider<>(microservice);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * createDeviceState(com.sitewhere.grpc.service.GCreateDeviceStateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceState(GCreateDeviceStateRequest request,
	    StreamObserver<GCreateDeviceStateResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceStateTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceStateTenantEngine tenantEngine) {
		tenantEngine.getDeviceStateImpl().createDeviceState(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#getDeviceState
     * (com.sitewhere.grpc.service.GGetDeviceStateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceState(GGetDeviceStateRequest request,
	    StreamObserver<GGetDeviceStateResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceStateTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceStateTenantEngine tenantEngine) {
		tenantEngine.getDeviceStateImpl().getDeviceState(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * getDeviceStateByAssignment(com.sitewhere.grpc.service.
     * GGetDeviceStateByAssignmentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceStateByAssignment(GGetDeviceStateByAssignmentRequest request,
	    StreamObserver<GGetDeviceStateByAssignmentResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceStateTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceStateTenantEngine tenantEngine) {
		tenantEngine.getDeviceStateImpl().getDeviceStateByAssignment(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * getDeviceStatesByDevice(com.sitewhere.grpc.service.
     * GGetDeviceStatesByDeviceRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceStatesByDevice(GGetDeviceStatesByDeviceRequest request,
	    StreamObserver<GGetDeviceStatesByDeviceResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceStateTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceStateTenantEngine tenantEngine) {
		tenantEngine.getDeviceStateImpl().getDeviceStatesByDevice(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * searchDeviceStates(com.sitewhere.grpc.service.GSearchDeviceStatesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void searchDeviceStates(GSearchDeviceStatesRequest request,
	    StreamObserver<GSearchDeviceStatesResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceStateTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceStateTenantEngine tenantEngine) {
		tenantEngine.getDeviceStateImpl().searchDeviceStates(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * updateDeviceState(com.sitewhere.grpc.service.GUpdateDeviceStateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceState(GUpdateDeviceStateRequest request,
	    StreamObserver<GUpdateDeviceStateResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceStateTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceStateTenantEngine tenantEngine) {
		tenantEngine.getDeviceStateImpl().updateDeviceState(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * mergeDeviceState(com.sitewhere.grpc.service.GMergeDeviceStateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void mergeDeviceState(GMergeDeviceStateRequest request,
	    StreamObserver<GMergeDeviceStateResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceStateTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceStateTenantEngine tenantEngine) {
		tenantEngine.getDeviceStateImpl().mergeDeviceState(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * deleteDeviceState(com.sitewhere.grpc.service.GDeleteDeviceStateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceState(GDeleteDeviceStateRequest request,
	    StreamObserver<GDeleteDeviceStateResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceStateTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceStateTenantEngine tenantEngine) {
		tenantEngine.getDeviceStateImpl().deleteDeviceState(request, responseObserver);
	    }
	}, responseObserver);
    }

    protected IDeviceStateMicroservice getMicroservice() {
	return microservice;
    }

    protected void setMicroservice(IDeviceStateMicroservice microservice) {
	this.microservice = microservice;
    }

    protected GrpcTenantEngineProvider<IDeviceStateTenantEngine> getGrpcTenantEngineProvider() {
	return grpcTenantEngineProvider;
    }
}
