/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.grpc;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.converter.DeviceStateModelConverter;
import com.sitewhere.grpc.service.DeviceStateGrpc;
import com.sitewhere.grpc.service.GCreateDeviceStateRequest;
import com.sitewhere.grpc.service.GCreateDeviceStateResponse;
import com.sitewhere.grpc.service.GDeleteDeviceStateRequest;
import com.sitewhere.grpc.service.GDeleteDeviceStateResponse;
import com.sitewhere.grpc.service.GGetDeviceStateByDeviceAssignmentIdRequest;
import com.sitewhere.grpc.service.GGetDeviceStateByDeviceAssignmentIdResponse;
import com.sitewhere.grpc.service.GGetDeviceStateRequest;
import com.sitewhere.grpc.service.GGetDeviceStateResponse;
import com.sitewhere.grpc.service.GUpdateDeviceStateRequest;
import com.sitewhere.grpc.service.GUpdateDeviceStateResponse;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.device.state.IDeviceStateManagement;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for device state GRPC requests.
 * 
 * @author Derek
 */
public class DeviceStateImpl extends DeviceStateGrpc.DeviceStateImplBase {

    /** Device state management persistence */
    private IDeviceStateManagement deviceStateManagement;

    public DeviceStateImpl(IDeviceStateManagement deviceStateManagement) {
	this.deviceStateManagement = deviceStateManagement;
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * createDeviceState(com.sitewhere.grpc.service.GCreateDeviceStateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceState(GCreateDeviceStateRequest request,
	    StreamObserver<GCreateDeviceStateResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceStateGrpc.METHOD_CREATE_DEVICE_STATE);
	    IDeviceStateCreateRequest apiRequest = DeviceStateModelConverter
		    .asApiDeviceStateCreateRequest(request.getRequest());
	    IDeviceState apiResult = getDeviceStateManagement().createDeviceState(apiRequest);
	    GCreateDeviceStateResponse.Builder response = GCreateDeviceStateResponse.newBuilder();
	    response.setDeviceState(DeviceStateModelConverter.asGrpcDeviceState(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.METHOD_CREATE_DEVICE_STATE, e, responseObserver);
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
	try {
	    GrpcUtils.logServerMethodEntry(DeviceStateGrpc.METHOD_GET_DEVICE_STATE);
	    IDeviceState apiResult = getDeviceStateManagement()
		    .getDeviceState(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceStateResponse.Builder response = GGetDeviceStateResponse.newBuilder();
	    if (apiResult != null) {
		response.setDeviceState(DeviceStateModelConverter.asGrpcDeviceState(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.METHOD_GET_DEVICE_STATE, e, responseObserver);
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
	try {
	    GrpcUtils.logServerMethodEntry(DeviceStateGrpc.METHOD_GET_DEVICE_STATE_BY_DEVICE_ASSIGNMENT_ID);
	    IDeviceState apiResult = getDeviceStateManagement().getDeviceStateByDeviceAssignmentId(
		    CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()));
	    GGetDeviceStateByDeviceAssignmentIdResponse.Builder response = GGetDeviceStateByDeviceAssignmentIdResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.setDeviceState(DeviceStateModelConverter.asGrpcDeviceState(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.METHOD_GET_DEVICE_STATE_BY_DEVICE_ASSIGNMENT_ID, e,
		    responseObserver);
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
	try {
	    GrpcUtils.logServerMethodEntry(DeviceStateGrpc.METHOD_UPDATE_DEVICE_STATE);
	    IDeviceStateCreateRequest update = DeviceStateModelConverter
		    .asApiDeviceStateCreateRequest(request.getRequest());
	    IDeviceState apiResult = getDeviceStateManagement()
		    .updateDeviceState(CommonModelConverter.asApiUuid(request.getId()), update);
	    GUpdateDeviceStateResponse.Builder response = GUpdateDeviceStateResponse.newBuilder();
	    if (apiResult != null) {
		response.setDeviceState(DeviceStateModelConverter.asGrpcDeviceState(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.METHOD_UPDATE_DEVICE_STATE, e, responseObserver);
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
	try {
	    GrpcUtils.logServerMethodEntry(DeviceStateGrpc.METHOD_DELETE_DEVICE_STATE);
	    IDeviceState apiResult = getDeviceStateManagement()
		    .deleteDeviceState(CommonModelConverter.asApiUuid(request.getId()), request.getForce());
	    GDeleteDeviceStateResponse.Builder response = GDeleteDeviceStateResponse.newBuilder();
	    response.setDeviceState(DeviceStateModelConverter.asGrpcDeviceState(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.METHOD_DELETE_DEVICE_STATE, e, responseObserver);
	}
    }

    protected IDeviceStateManagement getDeviceStateManagement() {
	return deviceStateManagement;
    }

    protected void setDeviceStateManagement(IDeviceStateManagement deviceStateManagement) {
	this.deviceStateManagement = deviceStateManagement;
    }
}