/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.grpc;

import java.util.List;
import java.util.UUID;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignmentSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupElementsSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStreamSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceTypeSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GSiteSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GZoneSearchResults;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.converter.DeviceModelConverter;
import com.sitewhere.grpc.service.*;
import com.sitewhere.rest.model.asset.AssetReference;
import com.sitewhere.spi.asset.IAssetReference;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.ISite;
import com.sitewhere.spi.device.IZone;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
import com.sitewhere.spi.device.request.IDeviceTypeCreateRequest;
import com.sitewhere.spi.device.request.ISiteCreateRequest;
import com.sitewhere.spi.device.request.IZoneCreateRequest;
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.search.ISearchResults;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for device management GRPC requests.
 * 
 * @author Derek
 */
public class DeviceManagementImpl extends DeviceManagementGrpc.DeviceManagementImplBase {

    /** Device management persistence */
    private IDeviceManagement deviceManagement;

    public DeviceManagementImpl(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceType(com.sitewhere.grpc.service.GCreateDeviceTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceType(GCreateDeviceTypeRequest request,
	    StreamObserver<GCreateDeviceTypeResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_TYPE);
	    IDeviceTypeCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceTypeCreateRequest(request.getRequest());
	    IDeviceType apiResult = getDeviceManagement().createDeviceType(apiRequest);
	    GCreateDeviceTypeResponse.Builder response = GCreateDeviceTypeResponse.newBuilder();
	    response.setDeviceType(DeviceModelConverter.asGrpcDeviceType(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_TYPE, e, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceType(com.sitewhere.grpc.service.GGetDeviceTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceType(GGetDeviceTypeRequest request, StreamObserver<GGetDeviceTypeResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_TYPE);
	    IDeviceType apiResult = getDeviceManagement()
		    .getDeviceType(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceTypeResponse.Builder response = GGetDeviceTypeResponse.newBuilder();
	    if (apiResult != null) {
		response.setDeviceType(DeviceModelConverter.asGrpcDeviceType(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_TYPE, e, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceTypeByToken(com.sitewhere.grpc.service.GGetDeviceTypeByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceTypeByToken(GGetDeviceTypeByTokenRequest request,
	    StreamObserver<GGetDeviceTypeByTokenResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_TYPE_BY_TOKEN);
	    IDeviceType apiResult = getDeviceManagement().getDeviceTypeByToken(request.getToken());
	    GGetDeviceTypeByTokenResponse.Builder response = GGetDeviceTypeByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setDeviceType(DeviceModelConverter.asGrpcDeviceType(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_TYPE_BY_TOKEN, e,
		    responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceType(com.sitewhere.grpc.service.GUpdateDeviceTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceType(GUpdateDeviceTypeRequest request,
	    StreamObserver<GUpdateDeviceTypeResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_TYPE);
	    IDeviceTypeCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceTypeCreateRequest(request.getRequest());
	    IDeviceType apiResult = getDeviceManagement()
		    .updateDeviceType(CommonModelConverter.asApiUuid(request.getId()), apiRequest);
	    GUpdateDeviceTypeResponse.Builder response = GUpdateDeviceTypeResponse.newBuilder();
	    response.setDeviceType(DeviceModelConverter.asGrpcDeviceType(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_TYPE, e, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceTypes(com.sitewhere.grpc.service.GListDeviceTypesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceTypes(GListDeviceTypesRequest request,
	    StreamObserver<GListDeviceTypesResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_TYPES);
	    boolean includeDeleted = request.getCriteria().hasIncludeDeleted()
		    ? request.getCriteria().getIncludeDeleted().getValue()
		    : false;
	    ISearchResults<IDeviceType> apiResult = getDeviceManagement().listDeviceTypes(includeDeleted,
		    CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GListDeviceTypesResponse.Builder response = GListDeviceTypesResponse.newBuilder();
	    GDeviceTypeSearchResults.Builder results = GDeviceTypeSearchResults.newBuilder();
	    for (IDeviceType apiType : apiResult.getResults()) {
		results.addDeviceTypes(DeviceModelConverter.asGrpcDeviceType(apiType));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_TYPES, e, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceType(com.sitewhere.grpc.service.GDeleteDeviceTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceType(GDeleteDeviceTypeRequest request,
	    StreamObserver<GDeleteDeviceTypeResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_TYPE);
	    IDeviceType apiResult = getDeviceManagement()
		    .deleteDeviceType(CommonModelConverter.asApiUuid(request.getId()), request.getForce());
	    GDeleteDeviceTypeResponse.Builder response = GDeleteDeviceTypeResponse.newBuilder();
	    response.setDeviceType(DeviceModelConverter.asGrpcDeviceType(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_TYPE, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceCommand(com.sitewhere.grpc.service. GCreateDeviceCommandRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceCommand(GCreateDeviceCommandRequest request,
	    StreamObserver<GCreateDeviceCommandResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_COMMAND);
	    IDeviceCommandCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceCommandCreateRequest(request.getRequest());
	    IDeviceCommand apiResult = getDeviceManagement()
		    .createDeviceCommand(CommonModelConverter.asApiUuid(request.getDeviceTypeId()), apiRequest);
	    GCreateDeviceCommandResponse.Builder response = GCreateDeviceCommandResponse.newBuilder();
	    response.setCommand(DeviceModelConverter.asGrpcDeviceCommand(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_COMMAND, e,
		    responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceCommand(com.sitewhere.grpc.service.GGetDeviceCommandRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceCommand(GGetDeviceCommandRequest request,
	    StreamObserver<GGetDeviceCommandResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND);
	    IDeviceCommand apiResult = getDeviceManagement()
		    .getDeviceCommand(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceCommandResponse.Builder response = GGetDeviceCommandResponse.newBuilder();
	    if (apiResult != null) {
		response.setCommand(DeviceModelConverter.asGrpcDeviceCommand(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceCommandByToken(com.sitewhere.grpc.service.
     * GGetDeviceCommandByTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceCommandByToken(GGetDeviceCommandByTokenRequest request,
	    StreamObserver<GGetDeviceCommandByTokenResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND_BY_TOKEN);
	    IDeviceCommand apiResult = getDeviceManagement().getDeviceCommandByToken(request.getToken());
	    GGetDeviceCommandByTokenResponse.Builder response = GGetDeviceCommandByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setCommand(DeviceModelConverter.asGrpcDeviceCommand(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND_BY_TOKEN, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceCommand(com.sitewhere.grpc.service. GUpdateDeviceCommandRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceCommand(GUpdateDeviceCommandRequest request,
	    StreamObserver<GUpdateDeviceCommandResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_COMMAND);
	    IDeviceCommandCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceCommandCreateRequest(request.getRequest());
	    IDeviceCommand apiResult = getDeviceManagement()
		    .updateDeviceCommand(CommonModelConverter.asApiUuid(request.getId()), apiRequest);
	    GUpdateDeviceCommandResponse.Builder response = GUpdateDeviceCommandResponse.newBuilder();
	    response.setCommand(DeviceModelConverter.asGrpcDeviceCommand(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_COMMAND, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceCommands(com.sitewhere.grpc.service.GListDeviceCommandsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceCommands(GListDeviceCommandsRequest request,
	    StreamObserver<GListDeviceCommandsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_COMMANDS);
	    boolean includeDeleted = request.getCriteria().hasIncludeDeleted()
		    ? request.getCriteria().getIncludeDeleted().getValue()
		    : false;

	    UUID deviceTypeId = CommonModelConverter.asApiUuid(request.getCriteria().getDeviceTypeId());
	    List<IDeviceCommand> apiResult = getDeviceManagement().listDeviceCommands(deviceTypeId, includeDeleted);
	    GListDeviceCommandsResponse.Builder response = GListDeviceCommandsResponse.newBuilder();
	    for (IDeviceCommand api : apiResult) {
		response.addCommands(DeviceModelConverter.asGrpcDeviceCommand(api));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_COMMANDS, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceCommand(com.sitewhere.grpc.service. GDeleteDeviceCommandRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceCommand(GDeleteDeviceCommandRequest request,
	    StreamObserver<GDeleteDeviceCommandResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_COMMAND);
	    IDeviceCommand apiResult = getDeviceManagement()
		    .deleteDeviceCommand(CommonModelConverter.asApiUuid(request.getId()), request.getForce());
	    GDeleteDeviceCommandResponse.Builder response = GDeleteDeviceCommandResponse.newBuilder();
	    response.setCommand(DeviceModelConverter.asGrpcDeviceCommand(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_COMMAND, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceStatus(com.sitewhere.grpc.service.GCreateDeviceStatusRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceStatus(GCreateDeviceStatusRequest request,
	    StreamObserver<GCreateDeviceStatusResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STATUS);
	    IDeviceStatusCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceStatusCreateRequest(request.getRequest());
	    IDeviceStatus apiResult = getDeviceManagement()
		    .createDeviceStatus(CommonModelConverter.asApiUuid(request.getDeviceTypeId()), apiRequest);
	    GCreateDeviceStatusResponse.Builder response = GCreateDeviceStatusResponse.newBuilder();
	    response.setStatus(DeviceModelConverter.asGrpcDeviceStatus(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STATUS, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceStatusByCode(com.sitewhere.grpc.service.
     * GGetDeviceStatusByCodeRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceStatusByCode(GGetDeviceStatusByCodeRequest request,
	    StreamObserver<GGetDeviceStatusByCodeResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_STATUS_BY_CODE);
	    IDeviceStatus apiResult = getDeviceManagement().getDeviceStatusByCode(
		    CommonModelConverter.asApiUuid(request.getDeviceTypeId()), request.getCode());
	    GGetDeviceStatusByCodeResponse.Builder response = GGetDeviceStatusByCodeResponse.newBuilder();
	    if (apiResult != null) {
		response.setStatus(DeviceModelConverter.asGrpcDeviceStatus(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_STATUS_BY_CODE, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceStatus(com.sitewhere.grpc.service.GUpdateDeviceStatusRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceStatus(GUpdateDeviceStatusRequest request,
	    StreamObserver<GUpdateDeviceStatusResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_STATUS);
	    IDeviceStatusCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceStatusCreateRequest(request.getRequest());
	    IDeviceStatus apiResult = getDeviceManagement().updateDeviceStatus(
		    CommonModelConverter.asApiUuid(request.getDeviceTypeId()), request.getCode(), apiRequest);
	    GUpdateDeviceStatusResponse.Builder response = GUpdateDeviceStatusResponse.newBuilder();
	    response.setStatus(DeviceModelConverter.asGrpcDeviceStatus(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_STATUS, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceStatuses(com.sitewhere.grpc.service.GListDeviceStatusesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceStatuses(GListDeviceStatusesRequest request,
	    StreamObserver<GListDeviceStatusesResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_STATUSES);
	    List<IDeviceStatus> apiResult = getDeviceManagement()
		    .listDeviceStatuses(CommonModelConverter.asApiUuid(request.getCriteria().getDeviceTypeId()));
	    GListDeviceStatusesResponse.Builder response = GListDeviceStatusesResponse.newBuilder();
	    for (IDeviceStatus api : apiResult) {
		response.addStatus(DeviceModelConverter.asGrpcDeviceStatus(api));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_STATUSES, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceStatus(com.sitewhere.grpc.service.GDeleteDeviceStatusRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceStatus(GDeleteDeviceStatusRequest request,
	    StreamObserver<GDeleteDeviceStatusResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_STATUS);
	    IDeviceStatus apiResult = getDeviceManagement()
		    .deleteDeviceStatus(CommonModelConverter.asApiUuid(request.getDeviceTypeId()), request.getCode());
	    GDeleteDeviceStatusResponse.Builder response = GDeleteDeviceStatusResponse.newBuilder();
	    response.setStatus(DeviceModelConverter.asGrpcDeviceStatus(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_STATUS, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDevice(com.sitewhere.grpc.service.GCreateDeviceRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDevice(GCreateDeviceRequest request, StreamObserver<GCreateDeviceResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE);
	    IDeviceCreateRequest apiRequest = DeviceModelConverter.asApiDeviceCreateRequest(request.getRequest());
	    IDevice apiResult = getDeviceManagement().createDevice(apiRequest);
	    GCreateDeviceResponse.Builder response = GCreateDeviceResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE, e, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDevice(com.sitewhere.grpc.service.GGetDeviceRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDevice(GGetDeviceRequest request, StreamObserver<GGetDeviceResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE);
	    IDevice apiResult = getDeviceManagement().getDevice(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceResponse.Builder response = GGetDeviceResponse.newBuilder();
	    if (apiResult != null) {
		response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE, e, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceByHardwareId(com.sitewhere.grpc.service.
     * GGetDeviceByHardwareIdRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceByHardwareId(GGetDeviceByHardwareIdRequest request,
	    StreamObserver<GGetDeviceByHardwareIdResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_BY_HARDWARE_ID);
	    IDevice apiResult = getDeviceManagement().getDeviceByHardwareId(request.getHardwareId());
	    GGetDeviceByHardwareIdResponse.Builder response = GGetDeviceByHardwareIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_BY_HARDWARE_ID, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDevice(com.sitewhere.grpc.service.GUpdateDeviceRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDevice(GUpdateDeviceRequest request, StreamObserver<GUpdateDeviceResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE);
	    IDeviceCreateRequest apiRequest = DeviceModelConverter.asApiDeviceCreateRequest(request.getRequest());
	    IDevice apiResult = getDeviceManagement().updateDevice(CommonModelConverter.asApiUuid(request.getId()),
		    apiRequest);
	    GUpdateDeviceResponse.Builder response = GUpdateDeviceResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDevices(com.sitewhere.grpc.service.GListDevicesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDevices(GListDevicesRequest request, StreamObserver<GListDevicesResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICES);
	    boolean includeDeleted = request.getCriteria().hasIncludeDeleted()
		    ? request.getCriteria().getIncludeDeleted().getValue()
		    : false;
	    ISearchResults<IDevice> apiResult = getDeviceManagement().listDevices(includeDeleted,
		    DeviceModelConverter.asApiDeviceSearchCriteria(request.getCriteria()));
	    GListDevicesResponse.Builder response = GListDevicesResponse.newBuilder();
	    GDeviceSearchResults.Builder results = GDeviceSearchResults.newBuilder();
	    for (IDevice apiDevice : apiResult.getResults()) {
		results.addDevices(DeviceModelConverter.asGrpcDevice(apiDevice));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICES, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceElementMapping(com.sitewhere.grpc.service.
     * GCreateDeviceElementMappingRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceElementMapping(GCreateDeviceElementMappingRequest request,
	    StreamObserver<GCreateDeviceElementMappingResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ELEMENT_MAPPING);
	    IDeviceElementMapping apiRequest = DeviceModelConverter.asApiDeviceElementMapping(request.getMapping());
	    IDevice apiResult = getDeviceManagement()
		    .createDeviceElementMapping(CommonModelConverter.asApiUuid(request.getId()), apiRequest);
	    GCreateDeviceElementMappingResponse.Builder response = GCreateDeviceElementMappingResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ELEMENT_MAPPING, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceElementMapping(com.sitewhere.grpc.service.
     * GDeleteDeviceElementMappingRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceElementMapping(GDeleteDeviceElementMappingRequest request,
	    StreamObserver<GDeleteDeviceElementMappingResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_ELEMENT_MAPPING);
	    IDevice apiResult = getDeviceManagement()
		    .deleteDeviceElementMapping(CommonModelConverter.asApiUuid(request.getId()), request.getPath());
	    GDeleteDeviceElementMappingResponse.Builder response = GDeleteDeviceElementMappingResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_ELEMENT_MAPPING, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDevice(com.sitewhere.grpc.service.GDeleteDeviceRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDevice(GDeleteDeviceRequest request, StreamObserver<GDeleteDeviceResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE);
	    IDevice apiResult = getDeviceManagement().deleteDevice(CommonModelConverter.asApiUuid(request.getId()),
		    request.getForce());
	    GDeleteDeviceResponse.Builder response = GDeleteDeviceResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceGroup(com.sitewhere.grpc.service.GCreateDeviceGroupRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceGroup(GCreateDeviceGroupRequest request,
	    StreamObserver<GCreateDeviceGroupResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_GROUP);
	    IDeviceGroupCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceGroupCreateRequest(request.getRequest());
	    IDeviceGroup apiResult = getDeviceManagement().createDeviceGroup(apiRequest);
	    GCreateDeviceGroupResponse.Builder response = GCreateDeviceGroupResponse.newBuilder();
	    response.setGroup(DeviceModelConverter.asGrpcDeviceGroup(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_GROUP, e, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceGroup(com.sitewhere.grpc.service.GGetDeviceGroupRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceGroup(GGetDeviceGroupRequest request,
	    StreamObserver<GGetDeviceGroupResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_GROUP);
	    IDeviceGroup apiResult = getDeviceManagement()
		    .getDeviceGroup(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceGroupResponse.Builder response = GGetDeviceGroupResponse.newBuilder();
	    if (apiResult != null) {
		response.setGroup(DeviceModelConverter.asGrpcDeviceGroup(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_GROUP, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceGroupByToken(com.sitewhere.grpc.service.
     * GGetDeviceGroupByTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceGroupByToken(GGetDeviceGroupByTokenRequest request,
	    StreamObserver<GGetDeviceGroupByTokenResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_GROUP_BY_TOKEN);
	    IDeviceGroup apiResult = getDeviceManagement().getDeviceGroupByToken(request.getToken());
	    GGetDeviceGroupByTokenResponse.Builder response = GGetDeviceGroupByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setGroup(DeviceModelConverter.asGrpcDeviceGroup(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_GROUP_BY_TOKEN, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceGroup(com.sitewhere.grpc.service.GUpdateDeviceGroupRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceGroup(GUpdateDeviceGroupRequest request,
	    StreamObserver<GUpdateDeviceGroupResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_GROUP);
	    IDeviceGroupCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceGroupCreateRequest(request.getRequest());
	    IDeviceGroup apiResult = getDeviceManagement()
		    .updateDeviceGroup(CommonModelConverter.asApiUuid(request.getId()), apiRequest);
	    GUpdateDeviceGroupResponse.Builder response = GUpdateDeviceGroupResponse.newBuilder();
	    response.setGroup(DeviceModelConverter.asGrpcDeviceGroup(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_GROUP, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceGroups(com.sitewhere.grpc.service.GListDeviceGroupsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceGroups(GListDeviceGroupsRequest request,
	    StreamObserver<GListDeviceGroupsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS);
	    boolean includeDeleted = request.getCriteria().hasIncludeDeleted()
		    ? request.getCriteria().getIncludeDeleted().getValue()
		    : false;
	    ISearchResults<IDeviceGroup> apiResult = getDeviceManagement().listDeviceGroups(includeDeleted,
		    CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GListDeviceGroupsResponse.Builder response = GListDeviceGroupsResponse.newBuilder();
	    GDeviceGroupSearchResults.Builder results = GDeviceGroupSearchResults.newBuilder();
	    for (IDeviceGroup apiGroup : apiResult.getResults()) {
		results.addDeviceGroups(DeviceModelConverter.asGrpcDeviceGroup(apiGroup));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceGroupsWithRole(com.sitewhere.grpc.service.
     * GListDeviceGroupsWithRoleRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceGroupsWithRole(GListDeviceGroupsWithRoleRequest request,
	    StreamObserver<GListDeviceGroupsWithRoleResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS);
	    boolean includeDeleted = request.getCriteria().hasIncludeDeleted()
		    ? request.getCriteria().getIncludeDeleted().getValue()
		    : false;
	    ISearchResults<IDeviceGroup> apiResult = getDeviceManagement().listDeviceGroupsWithRole(
		    request.getCriteria().getRole(), includeDeleted,
		    CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GListDeviceGroupsWithRoleResponse.Builder response = GListDeviceGroupsWithRoleResponse.newBuilder();
	    GDeviceGroupSearchResults.Builder results = GDeviceGroupSearchResults.newBuilder();
	    for (IDeviceGroup apiGroup : apiResult.getResults()) {
		results.addDeviceGroups(DeviceModelConverter.asGrpcDeviceGroup(apiGroup));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceGroup(com.sitewhere.grpc.service.GDeleteDeviceGroupRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceGroup(GDeleteDeviceGroupRequest request,
	    StreamObserver<GDeleteDeviceGroupResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_GROUP);
	    IDeviceGroup apiResult = getDeviceManagement()
		    .deleteDeviceGroup(CommonModelConverter.asApiUuid(request.getId()), request.getForce());
	    GDeleteDeviceGroupResponse.Builder response = GDeleteDeviceGroupResponse.newBuilder();
	    response.setGroup(DeviceModelConverter.asGrpcDeviceGroup(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_GROUP, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * addDeviceGroupElements(com.sitewhere.grpc.service.
     * GAddDeviceGroupElementsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addDeviceGroupElements(GAddDeviceGroupElementsRequest request,
	    StreamObserver<GAddDeviceGroupElementsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_ADD_DEVICE_GROUP_ELEMENTS);
	    List<IDeviceGroupElementCreateRequest> apiRequest = DeviceModelConverter
		    .asApiDeviceGroupElementCreateRequests(request.getRequestsList());
	    List<IDeviceGroupElement> apiResult = getDeviceManagement().addDeviceGroupElements(
		    CommonModelConverter.asApiUuid(request.getGroupId()), apiRequest, request.getIgnoreDuplicates());
	    GAddDeviceGroupElementsResponse.Builder response = GAddDeviceGroupElementsResponse.newBuilder();
	    for (IDeviceGroupElement apiElement : apiResult) {
		response.addElements(DeviceModelConverter.asGrpcDeviceGroupElement(apiElement));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_ADD_DEVICE_GROUP_ELEMENTS, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * removeDeviceGroupElements(com.sitewhere.grpc.service.
     * GRemoveDeviceGroupElementsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void removeDeviceGroupElements(GRemoveDeviceGroupElementsRequest request,
	    StreamObserver<GRemoveDeviceGroupElementsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_REMOVE_DEVICE_GROUP_ELEMENTS);
	    List<IDeviceGroupElementCreateRequest> apiRequest = DeviceModelConverter
		    .asApiDeviceGroupElementCreateRequests(request.getRequestsList());
	    List<IDeviceGroupElement> apiResult = getDeviceManagement()
		    .removeDeviceGroupElements(CommonModelConverter.asApiUuid(request.getGroupId()), apiRequest);
	    GRemoveDeviceGroupElementsResponse.Builder response = GRemoveDeviceGroupElementsResponse.newBuilder();
	    for (IDeviceGroupElement apiElement : apiResult) {
		response.addElements(DeviceModelConverter.asGrpcDeviceGroupElement(apiElement));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_REMOVE_DEVICE_GROUP_ELEMENTS, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceGroupElements(com.sitewhere.grpc.service.
     * GListDeviceGroupElementsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceGroupElements(GListDeviceGroupElementsRequest request,
	    StreamObserver<GListDeviceGroupElementsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUP_ELEMENTS);
	    ISearchResults<IDeviceGroupElement> apiResult = getDeviceManagement().listDeviceGroupElements(
		    CommonModelConverter.asApiUuid(request.getGroupId()),
		    CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GListDeviceGroupElementsResponse.Builder response = GListDeviceGroupElementsResponse.newBuilder();
	    GDeviceGroupElementsSearchResults.Builder results = GDeviceGroupElementsSearchResults.newBuilder();
	    for (IDeviceGroupElement apiElement : apiResult.getResults()) {
		results.addElements(DeviceModelConverter.asGrpcDeviceGroupElement(apiElement));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUP_ELEMENTS, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceAssignment(com.sitewhere.grpc.service.
     * GCreateDeviceAssignmentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceAssignment(GCreateDeviceAssignmentRequest request,
	    StreamObserver<GCreateDeviceAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ASSIGNMENT);
	    IDeviceAssignmentCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceAssignmentCreateRequest(request.getRequest());
	    IDeviceAssignment apiResult = getDeviceManagement().createDeviceAssignment(apiRequest);
	    GCreateDeviceAssignmentResponse.Builder response = GCreateDeviceAssignmentResponse.newBuilder();
	    response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ASSIGNMENT, e,
		    responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceAssignment(com.sitewhere.grpc.service.GGetDeviceAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceAssignment(GGetDeviceAssignmentRequest request,
	    StreamObserver<GGetDeviceAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT);
	    IDeviceAssignment apiResult = getDeviceManagement()
		    .getDeviceAssignment(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceAssignmentResponse.Builder response = GGetDeviceAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceAssignmentByToken(com.sitewhere.grpc.service.
     * GGetDeviceAssignmentByTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceAssignmentByToken(GGetDeviceAssignmentByTokenRequest request,
	    StreamObserver<GGetDeviceAssignmentByTokenResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_BY_TOKEN);
	    IDeviceAssignment apiResult = getDeviceManagement().getDeviceAssignmentByToken(request.getToken());
	    GGetDeviceAssignmentByTokenResponse.Builder response = GGetDeviceAssignmentByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_BY_TOKEN, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getCurrentAssignmentForDevice(com.sitewhere.grpc.service.
     * GGetCurrentAssignmentForDeviceRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCurrentAssignmentForDevice(GGetCurrentAssignmentForDeviceRequest request,
	    StreamObserver<GGetCurrentAssignmentForDeviceResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_CURRENT_ASSIGNMENT_FOR_DEVICE);
	    IDeviceAssignment apiResult = getDeviceManagement()
		    .getCurrentDeviceAssignment(CommonModelConverter.asApiUuid(request.getId()));
	    GGetCurrentAssignmentForDeviceResponse.Builder response = GGetCurrentAssignmentForDeviceResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_CURRENT_ASSIGNMENT_FOR_DEVICE, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceAssignment(com.sitewhere.grpc.service.
     * GDeleteDeviceAssignmentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceAssignment(GDeleteDeviceAssignmentRequest request,
	    StreamObserver<GDeleteDeviceAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_ASSIGNMENT);
	    IDeviceAssignment apiResult = getDeviceManagement()
		    .deleteDeviceAssignment(CommonModelConverter.asApiUuid(request.getId()), request.getForce());
	    GDeleteDeviceAssignmentResponse.Builder response = GDeleteDeviceAssignmentResponse.newBuilder();
	    response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_ASSIGNMENT, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceAssignmentMetadata(com.sitewhere.grpc.service.
     * GUpdateDeviceAssignmentMetadataRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceAssignmentMetadata(GUpdateDeviceAssignmentMetadataRequest request,
	    StreamObserver<GUpdateDeviceAssignmentMetadataResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT_METADATA);
	    IDeviceAssignment apiResult = getDeviceManagement().updateDeviceAssignmentMetadata(
		    CommonModelConverter.asApiUuid(request.getId()), request.getMetadataMap());
	    GUpdateDeviceAssignmentMetadataResponse.Builder response = GUpdateDeviceAssignmentMetadataResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT_METADATA, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceAssignmentStatus(com.sitewhere.grpc.service.
     * GUpdateDeviceAssignmentStatusRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceAssignmentStatus(GUpdateDeviceAssignmentStatusRequest request,
	    StreamObserver<GUpdateDeviceAssignmentStatusResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT_STATUS);
	    IDeviceAssignment apiResult = getDeviceManagement().updateDeviceAssignmentStatus(
		    CommonModelConverter.asApiUuid(request.getId()),
		    DeviceModelConverter.asApiDeviceAssignmentStatus(request.getStatus()));
	    GUpdateDeviceAssignmentStatusResponse.Builder response = GUpdateDeviceAssignmentStatusResponse.newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT_STATUS, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * endDeviceAssignment(com.sitewhere.grpc.service. GEndDeviceAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void endDeviceAssignment(GEndDeviceAssignmentRequest request,
	    StreamObserver<GEndDeviceAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_END_DEVICE_ASSIGNMENT);
	    IDeviceAssignment apiResult = getDeviceManagement()
		    .endDeviceAssignment(CommonModelConverter.asApiUuid(request.getId()));
	    GEndDeviceAssignmentResponse.Builder response = GEndDeviceAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_END_DEVICE_ASSIGNMENT, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceAssignmentHistory(com.sitewhere.grpc.service.
     * GGetDeviceAssignmentHistoryRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceAssignmentHistory(GGetDeviceAssignmentHistoryRequest request,
	    StreamObserver<GGetDeviceAssignmentHistoryResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_HISTORY);
	    ISearchResults<IDeviceAssignment> apiResult = getDeviceManagement().getDeviceAssignmentHistory(
		    CommonModelConverter.asApiUuid(request.getDeviceId()),
		    CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GGetDeviceAssignmentHistoryResponse.Builder response = GGetDeviceAssignmentHistoryResponse.newBuilder();
	    GDeviceAssignmentSearchResults.Builder results = GDeviceAssignmentSearchResults.newBuilder();
	    for (IDeviceAssignment api : apiResult.getResults()) {
		results.addAssignments(DeviceModelConverter.asGrpcDeviceAssignment(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_HISTORY, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceAssignmentsForSite(com.sitewhere.grpc.service.
     * GGetDeviceAssignmentsForSiteRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceAssignmentsForSite(GGetDeviceAssignmentsForSiteRequest request,
	    StreamObserver<GGetDeviceAssignmentsForSiteResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENTS_FOR_SITE);
	    ISearchResults<IDeviceAssignment> apiResult = getDeviceManagement().getDeviceAssignmentsForSite(
		    CommonModelConverter.asApiUuid(request.getSiteId()),
		    DeviceModelConverter.asApiAssignmentSearchCriteria(request.getCriteria()));
	    GGetDeviceAssignmentsForSiteResponse.Builder response = GGetDeviceAssignmentsForSiteResponse.newBuilder();
	    GDeviceAssignmentSearchResults.Builder results = GDeviceAssignmentSearchResults.newBuilder();
	    for (IDeviceAssignment api : apiResult.getResults()) {
		results.addAssignments(DeviceModelConverter.asGrpcDeviceAssignment(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENTS_FOR_SITE, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceAssignmentsForAsset(com.sitewhere.grpc.service.
     * GGetDeviceAssignmentsForAssetRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceAssignmentsForAsset(GGetDeviceAssignmentsForAssetRequest request,
	    StreamObserver<GGetDeviceAssignmentsForAssetResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENTS_FOR_ASSET);
	    IAssetReference reference = new AssetReference.Builder(request.getAssetModuleId(), request.getAssetId())
		    .build();
	    ISearchResults<IDeviceAssignment> apiResult = getDeviceManagement().getDeviceAssignmentsForAsset(reference,
		    DeviceModelConverter.asApiAssignmentsForAssetSearchCriteria(request.getCriteria()));
	    GGetDeviceAssignmentsForAssetResponse.Builder response = GGetDeviceAssignmentsForAssetResponse.newBuilder();
	    GDeviceAssignmentSearchResults.Builder results = GDeviceAssignmentSearchResults.newBuilder();
	    for (IDeviceAssignment api : apiResult.getResults()) {
		results.addAssignments(DeviceModelConverter.asGrpcDeviceAssignment(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENTS_FOR_ASSET, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceStream(com.sitewhere.grpc.service.GCreateDeviceStreamRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceStream(GCreateDeviceStreamRequest request,
	    StreamObserver<GCreateDeviceStreamResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STREAM);
	    IDeviceStreamCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceStreamCreateRequest(request.getRequest());
	    IDeviceStream apiResult = getDeviceManagement()
		    .createDeviceStream(CommonModelConverter.asApiUuid(request.getAssignmentId()), apiRequest);
	    GCreateDeviceStreamResponse.Builder response = GCreateDeviceStreamResponse.newBuilder();
	    response.setDeviceStream(DeviceModelConverter.asGrpcDeviceStream(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STREAM, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceStreamByStreamId(com.sitewhere.grpc.service.
     * GGetDeviceStreamByStreamIdRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceStreamByStreamId(GGetDeviceStreamByStreamIdRequest request,
	    StreamObserver<GGetDeviceStreamByStreamIdResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_STREAM_BY_STREAM_ID);
	    IDeviceStream apiResult = getDeviceManagement()
		    .getDeviceStream(CommonModelConverter.asApiUuid(request.getAssignmentId()), request.getStreamId());
	    GGetDeviceStreamByStreamIdResponse.Builder response = GGetDeviceStreamByStreamIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setDeviceStream(DeviceModelConverter.asGrpcDeviceStream(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_STREAM_BY_STREAM_ID, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceStreams(com.sitewhere.grpc.service.GListDeviceStreamsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceStreams(GListDeviceStreamsRequest request,
	    StreamObserver<GListDeviceStreamsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_STREAMS);
	    ISearchResults<IDeviceStream> apiResult = getDeviceManagement().listDeviceStreams(
		    CommonModelConverter.asApiUuid(request.getAssignmentId()),
		    CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GListDeviceStreamsResponse.Builder response = GListDeviceStreamsResponse.newBuilder();
	    GDeviceStreamSearchResults.Builder results = GDeviceStreamSearchResults.newBuilder();
	    for (IDeviceStream api : apiResult.getResults()) {
		results.addStreams(DeviceModelConverter.asGrpcDeviceStream(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_STREAMS, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createSite(com.sitewhere.grpc.service.GCreateSiteRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createSite(GCreateSiteRequest request, StreamObserver<GCreateSiteResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_CREATE_SITE);
	    ISiteCreateRequest apiRequest = DeviceModelConverter.asApiSiteCreateRequest(request.getRequest());
	    ISite apiResult = getDeviceManagement().createSite(apiRequest);
	    GCreateSiteResponse.Builder response = GCreateSiteResponse.newBuilder();
	    response.setSite(DeviceModelConverter.asGrpcSite(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_CREATE_SITE, e, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getSite(com.sitewhere.grpc.service.GGetSiteRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getSite(GGetSiteRequest request, StreamObserver<GGetSiteResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_SITE);
	    ISite apiResult = getDeviceManagement().getSite(CommonModelConverter.asApiUuid(request.getId()));
	    GGetSiteResponse.Builder response = GGetSiteResponse.newBuilder();
	    if (apiResult != null) {
		response.setSite(DeviceModelConverter.asGrpcSite(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_SITE, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getSiteByToken(com.sitewhere.grpc.service.GGetSiteByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getSiteByToken(GGetSiteByTokenRequest request,
	    StreamObserver<GGetSiteByTokenResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_SITE_BY_TOKEN);
	    ISite apiResult = getDeviceManagement().getSiteByToken(request.getToken());
	    GGetSiteByTokenResponse.Builder response = GGetSiteByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setSite(DeviceModelConverter.asGrpcSite(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_SITE_BY_TOKEN, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateSite(com.sitewhere.grpc.service.GUpdateSiteRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateSite(GUpdateSiteRequest request, StreamObserver<GUpdateSiteResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_SITE);
	    ISiteCreateRequest update = DeviceModelConverter.asApiSiteCreateRequest(request.getRequest());
	    ISite apiResult = getDeviceManagement().updateSite(CommonModelConverter.asApiUuid(request.getId()), update);
	    GUpdateSiteResponse.Builder response = GUpdateSiteResponse.newBuilder();
	    if (apiResult != null) {
		response.setSite(DeviceModelConverter.asGrpcSite(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_SITE, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listSites(com.sitewhere.grpc.service.GListSitesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listSites(GListSitesRequest request, StreamObserver<GListSitesResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_LIST_SITES);
	    ISearchResults<ISite> apiResult = getDeviceManagement()
		    .listSites(CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GListSitesResponse.Builder response = GListSitesResponse.newBuilder();
	    GSiteSearchResults.Builder results = GSiteSearchResults.newBuilder();
	    for (ISite api : apiResult.getResults()) {
		results.addSites(DeviceModelConverter.asGrpcSite(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_LIST_SITES, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteSite(com.sitewhere.grpc.service.GDeleteSiteRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteSite(GDeleteSiteRequest request, StreamObserver<GDeleteSiteResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_DELETE_SITE);
	    ISite apiResult = getDeviceManagement().deleteSite(CommonModelConverter.asApiUuid(request.getId()),
		    request.getForce());
	    GDeleteSiteResponse.Builder response = GDeleteSiteResponse.newBuilder();
	    response.setSite(DeviceModelConverter.asGrpcSite(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_DELETE_SITE, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createZone(com.sitewhere.grpc.service.GCreateZoneRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createZone(GCreateZoneRequest request, StreamObserver<GCreateZoneResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_CREATE_ZONE);
	    IZoneCreateRequest apiRequest = DeviceModelConverter.asApiZoneCreateRequest(request.getRequest());
	    IZone apiResult = getDeviceManagement().createZone(CommonModelConverter.asApiUuid(request.getSiteId()),
		    apiRequest);
	    GCreateZoneResponse.Builder response = GCreateZoneResponse.newBuilder();
	    response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_CREATE_ZONE, e, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getZone(com.sitewhere.grpc.service.GGetZoneRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getZone(GGetZoneRequest request, StreamObserver<GGetZoneResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_ZONE);
	    IZone apiResult = getDeviceManagement().getZone(CommonModelConverter.asApiUuid(request.getId()));
	    GGetZoneResponse.Builder response = GGetZoneResponse.newBuilder();
	    if (apiResult != null) {
		response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_ZONE, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getZoneByToken(com.sitewhere.grpc.service.GGetZoneByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getZoneByToken(GGetZoneByTokenRequest request,
	    StreamObserver<GGetZoneByTokenResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_ZONE_BY_TOKEN);
	    IZone apiResult = getDeviceManagement().getZoneByToken(request.getToken());
	    GGetZoneByTokenResponse.Builder response = GGetZoneByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_GET_ZONE_BY_TOKEN, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateZone(com.sitewhere.grpc.service.GUpdateZoneRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateZone(GUpdateZoneRequest request, StreamObserver<GUpdateZoneResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_ZONE);
	    IZoneCreateRequest update = DeviceModelConverter.asApiZoneCreateRequest(request.getRequest());
	    IZone apiResult = getDeviceManagement().updateZone(CommonModelConverter.asApiUuid(request.getSiteId()),
		    update);
	    GUpdateZoneResponse.Builder response = GUpdateZoneResponse.newBuilder();
	    if (apiResult != null) {
		response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_ZONE, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listZones(com.sitewhere.grpc.service.GListZonesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listZones(GListZonesRequest request, StreamObserver<GListZonesResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_LIST_ZONES);
	    ISearchResults<IZone> apiResult = getDeviceManagement().listZones(
		    CommonModelConverter.asApiUuid(request.getSiteId()),
		    CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GListZonesResponse.Builder response = GListZonesResponse.newBuilder();
	    GZoneSearchResults.Builder results = GZoneSearchResults.newBuilder();
	    for (IZone api : apiResult.getResults()) {
		results.addZones(DeviceModelConverter.asGrpcZone(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_LIST_ZONES, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteZone(com.sitewhere.grpc.service.GDeleteZoneRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteZone(GDeleteZoneRequest request, StreamObserver<GDeleteZoneResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_DELETE_ZONE);
	    IZone apiResult = getDeviceManagement().deleteZone(CommonModelConverter.asApiUuid(request.getId()),
		    request.getForce());
	    GDeleteZoneResponse.Builder response = GDeleteZoneResponse.newBuilder();
	    response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.METHOD_DELETE_ZONE, e, responseObserver);
	}
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}