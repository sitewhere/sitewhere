/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.grpc;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice;
import com.sitewhere.devicestate.spi.microservice.IDeviceStateTenantEngine;
import com.sitewhere.grpc.client.GrpcContextKeys;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcRouter;
import com.sitewhere.grpc.service.DeviceStateGrpc;
import com.sitewhere.grpc.service.GCreateDeviceStateRequest;
import com.sitewhere.grpc.service.GCreateDeviceStateResponse;
import com.sitewhere.grpc.service.GDeleteDeviceStateRequest;
import com.sitewhere.grpc.service.GDeleteDeviceStateResponse;
import com.sitewhere.grpc.service.GGetDeviceStateByDeviceAssignmentIdRequest;
import com.sitewhere.grpc.service.GGetDeviceStateByDeviceAssignmentIdResponse;
import com.sitewhere.grpc.service.GGetDeviceStateRequest;
import com.sitewhere.grpc.service.GGetDeviceStateResponse;
import com.sitewhere.grpc.service.GListDeviceStatesRequest;
import com.sitewhere.grpc.service.GListDeviceStatesResponse;
import com.sitewhere.grpc.service.GUpdateDeviceStateRequest;
import com.sitewhere.grpc.service.GUpdateDeviceStateResponse;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 * 
 * @author Derek
 */
public class DeviceStateRouter extends DeviceStateGrpc.DeviceStateImplBase
	implements IGrpcRouter<DeviceStateGrpc.DeviceStateImplBase> {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceStateRouter.class);

    /** Parent microservice */
    private IDeviceStateMicroservice microservice;

    public DeviceStateRouter(IDeviceStateMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.spi.grpc.IGrpcRouter#getTenantImplementation()
     */
    @Override
    public DeviceStateGrpc.DeviceStateImplBase getTenantImplementation(StreamObserver<?> observer) {
	String tenantId = GrpcContextKeys.TENANT_ID_KEY.get();
	if (tenantId == null) {
	    throw new RuntimeException("Tenant id not found in device state request.");
	}
	try {
	    IDeviceStateTenantEngine engine = getMicroservice().assureTenantEngineAvailable(UUID.fromString(tenantId));
	    return engine.getDeviceStateImpl();
	} catch (TenantEngineNotAvailableException e) {
	    observer.onError(GrpcUtils.convertServerException(e));
	    return null;
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * createDeviceState(com.sitewhere.grpc.service.GCreateDeviceStateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceState(GCreateDeviceStateRequest request,
	    StreamObserver<GCreateDeviceStateResponse> responseObserver) {
	DeviceStateGrpc.DeviceStateImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createDeviceState(request, responseObserver);
	}
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
	DeviceStateGrpc.DeviceStateImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceState(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * getDeviceStateByDeviceAssignmentId(com.sitewhere.grpc.service.
     * GGetDeviceStateByDeviceAssignmentIdRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceStateByDeviceAssignmentId(GGetDeviceStateByDeviceAssignmentIdRequest request,
	    StreamObserver<GGetDeviceStateByDeviceAssignmentIdResponse> responseObserver) {
	DeviceStateGrpc.DeviceStateImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceStateByDeviceAssignmentId(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * listDeviceStates(com.sitewhere.grpc.service.GListDeviceStatesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceStates(GListDeviceStatesRequest request,
	    StreamObserver<GListDeviceStatesResponse> responseObserver) {
	DeviceStateGrpc.DeviceStateImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listDeviceStates(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * updateDeviceState(com.sitewhere.grpc.service.GUpdateDeviceStateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceState(GUpdateDeviceStateRequest request,
	    StreamObserver<GUpdateDeviceStateResponse> responseObserver) {
	DeviceStateGrpc.DeviceStateImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateDeviceState(request, responseObserver);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * deleteDeviceState(com.sitewhere.grpc.service.GDeleteDeviceStateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceState(GDeleteDeviceStateRequest request,
	    StreamObserver<GDeleteDeviceStateResponse> responseObserver) {
	DeviceStateGrpc.DeviceStateImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteDeviceState(request, responseObserver);
	}
    }

    protected IDeviceStateMicroservice getMicroservice() {
	return microservice;
    }

    protected void setMicroservice(IDeviceStateMicroservice microservice) {
	this.microservice = microservice;
    }
}
