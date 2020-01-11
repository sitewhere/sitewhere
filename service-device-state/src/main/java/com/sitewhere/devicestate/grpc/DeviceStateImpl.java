/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.devicestate.grpc;

import com.sitewhere.devicestate.spi.microservice.IDeviceStateMicroservice;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.client.devicestate.DeviceStateModelConverter;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.grpc.model.DeviceStateModel.GDeviceStateSearchResults;
import com.sitewhere.grpc.model.DeviceStateModel.GRecentStateEventSearchResults;
import com.sitewhere.grpc.service.DeviceStateGrpc;
import com.sitewhere.grpc.service.GCreateDeviceStateRequest;
import com.sitewhere.grpc.service.GCreateDeviceStateResponse;
import com.sitewhere.grpc.service.GCreateRecentStateEventRequest;
import com.sitewhere.grpc.service.GCreateRecentStateEventResponse;
import com.sitewhere.grpc.service.GDeleteDeviceStateRequest;
import com.sitewhere.grpc.service.GDeleteDeviceStateResponse;
import com.sitewhere.grpc.service.GDeleteRecentStateEventRequest;
import com.sitewhere.grpc.service.GDeleteRecentStateEventResponse;
import com.sitewhere.grpc.service.GGetDeviceStateRequest;
import com.sitewhere.grpc.service.GGetDeviceStateResponse;
import com.sitewhere.grpc.service.GGetRecentStateEventRequest;
import com.sitewhere.grpc.service.GGetRecentStateEventResponse;
import com.sitewhere.grpc.service.GSearchDeviceStatesRequest;
import com.sitewhere.grpc.service.GSearchDeviceStatesResponse;
import com.sitewhere.grpc.service.GSearchRecentStateEventsRequest;
import com.sitewhere.grpc.service.GSearchRecentStateEventsResponse;
import com.sitewhere.grpc.service.GUpdateDeviceStateRequest;
import com.sitewhere.grpc.service.GUpdateDeviceStateResponse;
import com.sitewhere.grpc.service.GUpdateRecentStateEventRequest;
import com.sitewhere.grpc.service.GUpdateRecentStateEventResponse;
import com.sitewhere.microservice.api.state.IDeviceStateManagement;
import com.sitewhere.spi.device.state.IDeviceState;
import com.sitewhere.spi.device.state.IRecentStateEvent;
import com.sitewhere.spi.device.state.request.IDeviceStateCreateRequest;
import com.sitewhere.spi.device.state.request.IRecentStateEventCreateRequest;
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
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * createRecentStateEvent(com.sitewhere.grpc.service.
     * GCreateRecentStateEventRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createRecentStateEvent(GCreateRecentStateEventRequest request,
	    StreamObserver<GCreateRecentStateEventResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceStateGrpc.getCreateRecentStateEventMethod());
	    IRecentStateEventCreateRequest apiRequest = DeviceStateModelConverter
		    .asApiRecentStateEventCreateRequest(request.getRequest());
	    IRecentStateEvent apiResult = getDeviceStateManagement().createRecentStateEvent(apiRequest);
	    GCreateRecentStateEventResponse.Builder response = GCreateRecentStateEventResponse.newBuilder();
	    response.setRecentStateEvent(DeviceStateModelConverter.asGrpcRecentStateEvent(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.getCreateRecentStateEventMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceStateGrpc.getCreateRecentStateEventMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * getRecentStateEvent(com.sitewhere.grpc.service.GGetRecentStateEventRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getRecentStateEvent(GGetRecentStateEventRequest request,
	    StreamObserver<GGetRecentStateEventResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceStateGrpc.getGetRecentStateEventMethod());
	    IRecentStateEvent apiResult = getDeviceStateManagement()
		    .getRecentStateEvent(CommonModelConverter.asApiUuid(request.getId()));
	    GGetRecentStateEventResponse.Builder response = GGetRecentStateEventResponse.newBuilder();
	    if (apiResult != null) {
		response.setRecentStateEvent(DeviceStateModelConverter.asGrpcRecentStateEvent(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.getGetRecentStateEventMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceStateGrpc.getGetRecentStateEventMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * searchRecentStateEvents(com.sitewhere.grpc.service.
     * GSearchRecentStateEventsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void searchRecentStateEvents(GSearchRecentStateEventsRequest request,
	    StreamObserver<GSearchRecentStateEventsResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceStateGrpc.getSearchRecentStateEventsMethod());
	    ISearchResults<? extends IRecentStateEvent> apiResult = getDeviceStateManagement().searchRecentStateEvents(
		    DeviceStateModelConverter.asApiRecentStateEventSearchCriteria(request.getCriteria()));
	    GSearchRecentStateEventsResponse.Builder response = GSearchRecentStateEventsResponse.newBuilder();
	    GRecentStateEventSearchResults.Builder results = GRecentStateEventSearchResults.newBuilder();
	    for (IRecentStateEvent api : apiResult.getResults()) {
		results.addRecentStateEvents(DeviceStateModelConverter.asGrpcRecentStateEvent(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.getSearchRecentStateEventsMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceStateGrpc.getSearchRecentStateEventsMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * updateRecentStateEvent(com.sitewhere.grpc.service.
     * GUpdateRecentStateEventRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateRecentStateEvent(GUpdateRecentStateEventRequest request,
	    StreamObserver<GUpdateRecentStateEventResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceStateGrpc.getUpdateRecentStateEventMethod());
	    IRecentStateEventCreateRequest update = DeviceStateModelConverter
		    .asApiRecentStateEventCreateRequest(request.getRequest());
	    IRecentStateEvent apiResult = getDeviceStateManagement()
		    .updateRecentStateEvent(CommonModelConverter.asApiUuid(request.getId()), update);
	    GUpdateRecentStateEventResponse.Builder response = GUpdateRecentStateEventResponse.newBuilder();
	    if (apiResult != null) {
		response.setRecentStateEvent(DeviceStateModelConverter.asGrpcRecentStateEvent(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.getUpdateRecentStateEventMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceStateGrpc.getUpdateRecentStateEventMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.DeviceStateGrpc.DeviceStateImplBase#
     * deleteRecentStateEvent(com.sitewhere.grpc.service.
     * GDeleteRecentStateEventRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteRecentStateEvent(GDeleteRecentStateEventRequest request,
	    StreamObserver<GDeleteRecentStateEventResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceStateGrpc.getDeleteRecentStateEventMethod());
	    IRecentStateEvent apiResult = getDeviceStateManagement()
		    .deleteRecentStateEvent(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteRecentStateEventResponse.Builder response = GDeleteRecentStateEventResponse.newBuilder();
	    response.setRecentStateEvent(DeviceStateModelConverter.asGrpcRecentStateEvent(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceStateGrpc.getDeleteRecentStateEventMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceStateGrpc.getDeleteRecentStateEventMethod());
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