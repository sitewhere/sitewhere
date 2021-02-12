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
package com.sitewhere.devicestate.grpc;

import java.util.List;

import com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.devicestate.DeviceStateModelConverter;
import com.sitewhere.grpc.model.DeviceStateModel.GDeviceStateSearchResults;
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
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;
import com.sitewhere.spi.device.state.request.IDeviceStateEventMergeRequest;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.search.ISearchResults;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for device state GRPC requests.
 */
public class DeviceStateImpl extends DeviceStateGrpc.DeviceStateImplBase implements IGrpcApiImplementation {

    /** Parent microservice */
    private IDeviceStateMicroservice microservice;

    /** Device state management persistence */
    private IDeviceStateManagement deviceStateManagement;

    public DeviceStateImpl(IDeviceStateMicroservice microservice, IDeviceStateManagement deviceStateManagement) {
	this.microservice = microservice;
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceStateGrpc.getCreateDeviceStateMethod());
	    IDeviceStateCreateRequest apiRequest = DeviceStateModelConverter
		    .asApiDeviceStateCreateRequest(request.getRequest());
	    IDeviceState apiResult = getDeviceStateManagement().createDeviceState(apiRequest);
	    GCreateDeviceStateResponse.Builder response = GCreateDeviceStateResponse.newBuilder();
	    response.setDeviceState(DeviceStateModelConverter.asGrpcDeviceState(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.getCreateDeviceStateMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceStateGrpc.getCreateDeviceStateMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceStateGrpc.getGetDeviceStateMethod());
	    IDeviceState apiResult = getDeviceStateManagement()
		    .getDeviceState(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceStateResponse.Builder response = GGetDeviceStateResponse.newBuilder();
	    if (apiResult != null) {
		response.setDeviceState(DeviceStateModelConverter.asGrpcDeviceState(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.getGetDeviceStateMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceStateGrpc.getGetDeviceStateMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * getDeviceStateByAssignment(com.sitewhere.grpc.service.
     * GGetDeviceStateByAssignmentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceStateByAssignment(GGetDeviceStateByAssignmentRequest request,
	    StreamObserver<GGetDeviceStateByAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceStateGrpc.getGetDeviceStateByAssignmentMethod());
	    IDeviceState apiResult = getDeviceStateManagement()
		    .getDeviceStateByDeviceAssignment(CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()));
	    GGetDeviceStateByAssignmentResponse.Builder response = GGetDeviceStateByAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setDeviceState(DeviceStateModelConverter.asGrpcDeviceState(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.getGetDeviceStateByAssignmentMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceStateGrpc.getGetDeviceStateByAssignmentMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * getDeviceStatesByDevice(com.sitewhere.grpc.service.
     * GGetDeviceStatesByDeviceRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceStatesByDevice(GGetDeviceStatesByDeviceRequest request,
	    StreamObserver<GGetDeviceStatesByDeviceResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceStateGrpc.getGetDeviceStatesByDeviceMethod());
	    List<? extends IDeviceState> apiResult = getDeviceStateManagement()
		    .getDeviceStatesForDevice(CommonModelConverter.asApiUuid(request.getDeviceId()));
	    GGetDeviceStatesByDeviceResponse.Builder response = GGetDeviceStatesByDeviceResponse.newBuilder();
	    response.addAllDeviceStates(DeviceStateModelConverter.asGrpcDeviceStates(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.getGetDeviceStatesByDeviceMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceStateGrpc.getGetDeviceStatesByDeviceMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * searchDeviceStates(com.sitewhere.grpc.service.GSearchDeviceStatesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void searchDeviceStates(GSearchDeviceStatesRequest request,
	    StreamObserver<GSearchDeviceStatesResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceStateGrpc.getSearchDeviceStatesMethod());
	    ISearchResults<? extends IDeviceState> apiResult = getDeviceStateManagement().searchDeviceStates(
		    DeviceStateModelConverter.asApiDeviceStateSearchCriteria(request.getCriteria()));
	    GSearchDeviceStatesResponse.Builder response = GSearchDeviceStatesResponse.newBuilder();
	    GDeviceStateSearchResults.Builder results = GDeviceStateSearchResults.newBuilder();
	    for (IDeviceState api : apiResult.getResults()) {
		results.addDeviceStates(DeviceStateModelConverter.asGrpcDeviceState(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.getSearchDeviceStatesMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceStateGrpc.getSearchDeviceStatesMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceStateGrpc.getUpdateDeviceStateMethod());
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
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.getUpdateDeviceStateMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceStateGrpc.getUpdateDeviceStateMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * mergeDeviceState(com.sitewhere.grpc.service.GMergeDeviceStateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void mergeDeviceState(GMergeDeviceStateRequest request,
	    StreamObserver<GMergeDeviceStateResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceStateGrpc.getMergeDeviceStateMethod());
	    IDeviceStateEventMergeRequest update = DeviceStateModelConverter
		    .asApiDeviceStateEventMergeRequest(request.getRequest());
	    IDeviceState apiResult = getDeviceStateManagement().merge(CommonModelConverter.asApiUuid(request.getId()),
		    update);
	    GMergeDeviceStateResponse.Builder response = GMergeDeviceStateResponse.newBuilder();
	    if (apiResult != null) {
		response.setDeviceState(DeviceStateModelConverter.asGrpcDeviceState(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.getMergeDeviceStateMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceStateGrpc.getMergeDeviceStateMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceStateGrpc.getDeleteDeviceStateMethod());
	    IDeviceState apiResult = getDeviceStateManagement()
		    .deleteDeviceState(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteDeviceStateResponse.Builder response = GDeleteDeviceStateResponse.newBuilder();
	    response.setDeviceState(DeviceStateModelConverter.asGrpcDeviceState(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.getDeleteDeviceStateMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceStateGrpc.getDeleteDeviceStateMethod());
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation#getMicroservice()
     */
    @Override
    public IMicroservice<?, ?> getMicroservice() {
	return microservice;
    }

    protected IDeviceStateManagement getDeviceStateManagement() {
	return deviceStateManagement;
    }
}