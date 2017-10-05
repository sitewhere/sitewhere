package com.sitewhere.device.grpc;

import java.util.List;

import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignmentSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupElementsSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSpecificationSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStreamSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GSiteSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GZoneSearchResults;
import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.converter.DeviceModelConverter;
import com.sitewhere.grpc.service.*;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.device.IDeviceSpecification;
import com.sitewhere.spi.device.IDeviceStatus;
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
import com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest;
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
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
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceSpecification(com.sitewhere.grpc.service.
     * GCreateDeviceSpecificationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceSpecification(GCreateDeviceSpecificationRequest request,
	    StreamObserver<GCreateDeviceSpecificationResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_SPECIFICATION);
	    IDeviceSpecificationCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceSpecificationCreateRequest(request.getRequest());
	    IDeviceSpecification apiResult = getDeviceManagement().createDeviceSpecification(apiRequest);
	    GCreateDeviceSpecificationResponse.Builder response = GCreateDeviceSpecificationResponse.newBuilder();
	    response.setSpecification(DeviceModelConverter.asGrpcDeviceSpecification(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_SPECIFICATION, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceSpecificationByToken(com.sitewhere.grpc.service.
     * GGetDeviceSpecificationByTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceSpecificationByToken(GGetDeviceSpecificationByTokenRequest request,
	    StreamObserver<GGetDeviceSpecificationByTokenResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_SPECIFICATION_BY_TOKEN);
	    IDeviceSpecification apiResult = getDeviceManagement().getDeviceSpecificationByToken(request.getToken());
	    GGetDeviceSpecificationByTokenResponse.Builder response = GGetDeviceSpecificationByTokenResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.setSpecification(DeviceModelConverter.asGrpcDeviceSpecification(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_SPECIFICATION_BY_TOKEN, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceSpecification(com.sitewhere.grpc.service.
     * GUpdateDeviceSpecificationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceSpecification(GUpdateDeviceSpecificationRequest request,
	    StreamObserver<GUpdateDeviceSpecificationResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_SPECIFICATION);
	    IDeviceSpecificationCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceSpecificationCreateRequest(request.getRequest());
	    IDeviceSpecification apiResult = getDeviceManagement().updateDeviceSpecification(request.getToken(),
		    apiRequest);
	    GUpdateDeviceSpecificationResponse.Builder response = GUpdateDeviceSpecificationResponse.newBuilder();
	    response.setSpecification(DeviceModelConverter.asGrpcDeviceSpecification(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_SPECIFICATION, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceSpecifications(com.sitewhere.grpc.service.
     * GListDeviceSpecificationsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceSpecifications(GListDeviceSpecificationsRequest request,
	    StreamObserver<GListDeviceSpecificationsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_SPECIFICATIONS);
	    boolean includeDeleted = request.getCriteria().hasIncludeDeleted()
		    ? request.getCriteria().getIncludeDeleted().getValue() : false;
	    ISearchResults<IDeviceSpecification> apiResult = getDeviceManagement().listDeviceSpecifications(
		    includeDeleted, CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GListDeviceSpecificationsResponse.Builder response = GListDeviceSpecificationsResponse.newBuilder();
	    GDeviceSpecificationSearchResults.Builder results = GDeviceSpecificationSearchResults.newBuilder();
	    for (IDeviceSpecification apiSpecification : apiResult.getResults()) {
		results.addSpecifications(DeviceModelConverter.asGrpcDeviceSpecification(apiSpecification));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_SPECIFICATIONS, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceSpecification(com.sitewhere.grpc.service.
     * GDeleteDeviceSpecificationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceSpecification(GDeleteDeviceSpecificationRequest request,
	    StreamObserver<GDeleteDeviceSpecificationResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_SPECIFICATION);
	    IDeviceSpecification apiResult = getDeviceManagement().deleteDeviceSpecification(request.getToken(),
		    request.getForce());
	    GDeleteDeviceSpecificationResponse.Builder response = GDeleteDeviceSpecificationResponse.newBuilder();
	    response.setSpecification(DeviceModelConverter.asGrpcDeviceSpecification(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_SPECIFICATION, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceCommand(com.sitewhere.grpc.service.
     * GCreateDeviceCommandRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceCommand(GCreateDeviceCommandRequest request,
	    StreamObserver<GCreateDeviceCommandResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_COMMAND);
	    IDeviceCommandCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceCommandCreateRequest(request.getRequest());
	    IDeviceCommand apiResult = getDeviceManagement().createDeviceCommand(request.getSpecificationToken(),
		    apiRequest);
	    GCreateDeviceCommandResponse.Builder response = GCreateDeviceCommandResponse.newBuilder();
	    response.setCommand(DeviceModelConverter.asGrpcDeviceCommand(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_COMMAND, e);
	    responseObserver.onError(e);
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND_BY_TOKEN, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceCommand(com.sitewhere.grpc.service.
     * GUpdateDeviceCommandRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceCommand(GUpdateDeviceCommandRequest request,
	    StreamObserver<GUpdateDeviceCommandResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_COMMAND);
	    IDeviceCommandCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceCommandCreateRequest(request.getRequest());
	    IDeviceCommand apiResult = getDeviceManagement().updateDeviceCommand(request.getToken(), apiRequest);
	    GUpdateDeviceCommandResponse.Builder response = GUpdateDeviceCommandResponse.newBuilder();
	    response.setCommand(DeviceModelConverter.asGrpcDeviceCommand(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_COMMAND, e);
	    responseObserver.onError(e);
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
		    ? request.getCriteria().getIncludeDeleted().getValue() : false;
	    List<IDeviceCommand> apiResult = getDeviceManagement().listDeviceCommands(request.getSpecificationToken(),
		    includeDeleted);
	    GListDeviceCommandsResponse.Builder response = GListDeviceCommandsResponse.newBuilder();
	    for (IDeviceCommand api : apiResult) {
		response.addCommands(DeviceModelConverter.asGrpcDeviceCommand(api));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_COMMANDS, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceCommand(com.sitewhere.grpc.service.
     * GDeleteDeviceCommandRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceCommand(GDeleteDeviceCommandRequest request,
	    StreamObserver<GDeleteDeviceCommandResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_COMMAND);
	    IDeviceCommand apiResult = getDeviceManagement().deleteDeviceCommand(request.getSpecificationToken(),
		    request.getForce());
	    GDeleteDeviceCommandResponse.Builder response = GDeleteDeviceCommandResponse.newBuilder();
	    response.setCommand(DeviceModelConverter.asGrpcDeviceCommand(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_COMMAND, e);
	    responseObserver.onError(e);
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
	    IDeviceStatus apiResult = getDeviceManagement().createDeviceStatus(request.getSpecificationToken(),
		    apiRequest);
	    GCreateDeviceStatusResponse.Builder response = GCreateDeviceStatusResponse.newBuilder();
	    response.setStatus(DeviceModelConverter.asGrpcDeviceStatus(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STATUS, e);
	    responseObserver.onError(e);
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
	    IDeviceStatus apiResult = getDeviceManagement().getDeviceStatusByCode(request.getSpecificationToken(),
		    request.getCode());
	    GGetDeviceStatusByCodeResponse.Builder response = GGetDeviceStatusByCodeResponse.newBuilder();
	    if (apiResult != null) {
		response.setStatus(DeviceModelConverter.asGrpcDeviceStatus(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_STATUS_BY_CODE, e);
	    responseObserver.onError(e);
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
	    IDeviceStatus apiResult = getDeviceManagement().updateDeviceStatus(request.getSpecificationToken(),
		    request.getCode(), apiRequest);
	    GUpdateDeviceStatusResponse.Builder response = GUpdateDeviceStatusResponse.newBuilder();
	    response.setStatus(DeviceModelConverter.asGrpcDeviceStatus(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_STATUS, e);
	    responseObserver.onError(e);
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
	    List<IDeviceStatus> apiResult = getDeviceManagement().listDeviceStatuses(request.getSpecificationToken());
	    GListDeviceStatusesResponse.Builder response = GListDeviceStatusesResponse.newBuilder();
	    for (IDeviceStatus api : apiResult) {
		response.addStatus(DeviceModelConverter.asGrpcDeviceStatus(api));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_STATUSES, e);
	    responseObserver.onError(e);
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
	    IDeviceStatus apiResult = getDeviceManagement().deleteDeviceStatus(request.getSpecificationToken(),
		    request.getCode());
	    GDeleteDeviceStatusResponse.Builder response = GDeleteDeviceStatusResponse.newBuilder();
	    response.setStatus(DeviceModelConverter.asGrpcDeviceStatus(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_STATUS, e);
	    responseObserver.onError(e);
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceByHardwareId(com.sitewhere.grpc.service.
     * GGetDeviceByaHardwareIdRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceByHardwareId(GGetDeviceByaHardwareIdRequest request,
	    StreamObserver<GGetDeviceByaHardwareIdResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_BY_HARDWARE_ID);
	    IDevice apiResult = getDeviceManagement().getDeviceByHardwareId(request.getHardwareId());
	    GGetDeviceByaHardwareIdResponse.Builder response = GGetDeviceByaHardwareIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_BY_HARDWARE_ID, e);
	    responseObserver.onError(e);
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
	    IDevice apiResult = getDeviceManagement().updateDevice(request.getHardwareId(), apiRequest);
	    GUpdateDeviceResponse.Builder response = GUpdateDeviceResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE, e);
	    responseObserver.onError(e);
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
		    ? request.getCriteria().getIncludeDeleted().getValue() : false;
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICES, e);
	    responseObserver.onError(e);
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
	    IDevice apiResult = getDeviceManagement().createDeviceElementMapping(request.getHardwareId(), apiRequest);
	    GCreateDeviceElementMappingResponse.Builder response = GCreateDeviceElementMappingResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ELEMENT_MAPPING, e);
	    responseObserver.onError(e);
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
	    IDevice apiResult = getDeviceManagement().deleteDeviceElementMapping(request.getHardwareId(),
		    request.getPath());
	    GDeleteDeviceElementMappingResponse.Builder response = GDeleteDeviceElementMappingResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_ELEMENT_MAPPING, e);
	    responseObserver.onError(e);
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
	    IDevice apiResult = getDeviceManagement().deleteDevice(request.getHardwareId(), request.getForce());
	    GDeleteDeviceResponse.Builder response = GDeleteDeviceResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE, e);
	    responseObserver.onError(e);
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_GROUP, e);
	    responseObserver.onError(e);
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
	    IDeviceGroup apiResult = getDeviceManagement().getDeviceGroup(request.getToken());
	    GGetDeviceGroupByTokenResponse.Builder response = GGetDeviceGroupByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setGroup(DeviceModelConverter.asGrpcDeviceGroup(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_GROUP_BY_TOKEN, e);
	    responseObserver.onError(e);
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
	    IDeviceGroup apiResult = getDeviceManagement().updateDeviceGroup(request.getToken(), apiRequest);
	    GUpdateDeviceGroupResponse.Builder response = GUpdateDeviceGroupResponse.newBuilder();
	    response.setGroup(DeviceModelConverter.asGrpcDeviceGroup(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_GROUP, e);
	    responseObserver.onError(e);
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
		    ? request.getCriteria().getIncludeDeleted().getValue() : false;
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS, e);
	    responseObserver.onError(e);
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
		    ? request.getCriteria().getIncludeDeleted().getValue() : false;
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS, e);
	    responseObserver.onError(e);
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
	    IDeviceGroup apiResult = getDeviceManagement().deleteDeviceGroup(request.getToken(), request.getForce());
	    GDeleteDeviceGroupResponse.Builder response = GDeleteDeviceGroupResponse.newBuilder();
	    response.setGroup(DeviceModelConverter.asGrpcDeviceGroup(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_GROUP, e);
	    responseObserver.onError(e);
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
	    List<IDeviceGroupElement> apiResult = getDeviceManagement().addDeviceGroupElements(request.getGroupToken(),
		    apiRequest, request.getIgnoreDuplicates());
	    GAddDeviceGroupElementsResponse.Builder response = GAddDeviceGroupElementsResponse.newBuilder();
	    for (IDeviceGroupElement apiElement : apiResult) {
		response.addElements(DeviceModelConverter.asGrpcDeviceGroupElement(apiElement));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_ADD_DEVICE_GROUP_ELEMENTS, e);
	    responseObserver.onError(e);
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
		    .removeDeviceGroupElements(request.getGroupToken(), apiRequest);
	    GRemoveDeviceGroupElementsResponse.Builder response = GRemoveDeviceGroupElementsResponse.newBuilder();
	    for (IDeviceGroupElement apiElement : apiResult) {
		response.addElements(DeviceModelConverter.asGrpcDeviceGroupElement(apiElement));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_REMOVE_DEVICE_GROUP_ELEMENTS, e);
	    responseObserver.onError(e);
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
		    request.getGroupToken(),
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUP_ELEMENTS, e);
	    responseObserver.onError(e);
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ASSIGNMENT, e);
	    responseObserver.onError(e);
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_BY_TOKEN, e);
	    responseObserver.onError(e);
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
	    IDeviceAssignment apiResult = getDeviceManagement().getCurrentDeviceAssignment(request.getHardwareId());
	    GGetCurrentAssignmentForDeviceResponse.Builder response = GGetCurrentAssignmentForDeviceResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_CURRENT_ASSIGNMENT_FOR_DEVICE, e);
	    responseObserver.onError(e);
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
	    IDeviceAssignment apiResult = getDeviceManagement().deleteDeviceAssignment(request.getToken(),
		    request.getForce());
	    GDeleteDeviceAssignmentResponse.Builder response = GDeleteDeviceAssignmentResponse.newBuilder();
	    response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_ASSIGNMENT, e);
	    responseObserver.onError(e);
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
	    IDeviceAssignment apiResult = getDeviceManagement().updateDeviceAssignmentMetadata(request.getToken(),
		    request.getMetadataMap());
	    GUpdateDeviceAssignmentMetadataResponse.Builder response = GUpdateDeviceAssignmentMetadataResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT_METADATA, e);
	    responseObserver.onError(e);
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
	    IDeviceAssignment apiResult = getDeviceManagement().updateDeviceAssignmentStatus(request.getToken(),
		    DeviceModelConverter.asApiDeviceAssignmentStatus(request.getStatus()));
	    GUpdateDeviceAssignmentStatusResponse.Builder response = GUpdateDeviceAssignmentStatusResponse.newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT_STATUS, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * endDeviceAssignment(com.sitewhere.grpc.service.
     * GEndDeviceAssignmentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void endDeviceAssignment(GEndDeviceAssignmentRequest request,
	    StreamObserver<GEndDeviceAssignmentResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(DeviceManagementGrpc.METHOD_END_DEVICE_ASSIGNMENT);
	    IDeviceAssignment apiResult = getDeviceManagement().endDeviceAssignment(request.getToken());
	    GEndDeviceAssignmentResponse.Builder response = GEndDeviceAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_END_DEVICE_ASSIGNMENT, e);
	    responseObserver.onError(e);
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
		    request.getHardwareId(),
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_HISTORY, e);
	    responseObserver.onError(e);
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
		    request.getSiteToken(), DeviceModelConverter.asApiAssignmentSearchCriteria(request.getCriteria()));
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENTS_FOR_SITE, e);
	    responseObserver.onError(e);
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
	    ISearchResults<IDeviceAssignment> apiResult = getDeviceManagement().getDeviceAssignmentsForAsset(
		    request.getAssetModuleId(), request.getAssetId(),
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENTS_FOR_ASSET, e);
	    responseObserver.onError(e);
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
	    IDeviceStream apiResult = getDeviceManagement().createDeviceStream(request.getAssignmentToken(),
		    apiRequest);
	    GCreateDeviceStreamResponse.Builder response = GCreateDeviceStreamResponse.newBuilder();
	    response.setDeviceStream(DeviceModelConverter.asGrpcDeviceStream(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STREAM, e);
	    responseObserver.onError(e);
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
	    IDeviceStream apiResult = getDeviceManagement().getDeviceStream(request.getAssignmentToken(),
		    request.getStreamId());
	    GGetDeviceStreamByStreamIdResponse.Builder response = GGetDeviceStreamByStreamIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setDeviceStream(DeviceModelConverter.asGrpcDeviceStream(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_STREAM_BY_STREAM_ID, e);
	    responseObserver.onError(e);
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
		    request.getAssignmentToken(),
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_STREAMS, e);
	    responseObserver.onError(e);
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_CREATE_SITE, e);
	    responseObserver.onError(e);
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_SITE_BY_TOKEN, e);
	    responseObserver.onError(e);
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
	    ISite apiResult = getDeviceManagement().updateSite(request.getToken(), update);
	    GUpdateSiteResponse.Builder response = GUpdateSiteResponse.newBuilder();
	    if (apiResult != null) {
		response.setSite(DeviceModelConverter.asGrpcSite(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_SITE, e);
	    responseObserver.onError(e);
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_LIST_SITES, e);
	    responseObserver.onError(e);
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
	    ISite apiResult = getDeviceManagement().deleteSite(request.getToken(), request.getForce());
	    GDeleteSiteResponse.Builder response = GDeleteSiteResponse.newBuilder();
	    response.setSite(DeviceModelConverter.asGrpcSite(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_DELETE_SITE, e);
	    responseObserver.onError(e);
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
	    IZone apiResult = getDeviceManagement().createZone(request.getSiteToken(), apiRequest);
	    GCreateZoneResponse.Builder response = GCreateZoneResponse.newBuilder();
	    response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_CREATE_ZONE, e);
	    responseObserver.onError(e);
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
	    IZone apiResult = getDeviceManagement().getZone(request.getToken());
	    GGetZoneByTokenResponse.Builder response = GGetZoneByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_GET_ZONE_BY_TOKEN, e);
	    responseObserver.onError(e);
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
	    IZone apiResult = getDeviceManagement().updateZone(request.getSiteToken(), update);
	    GUpdateZoneResponse.Builder response = GUpdateZoneResponse.newBuilder();
	    if (apiResult != null) {
		response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_UPDATE_ZONE, e);
	    responseObserver.onError(e);
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
	    ISearchResults<IZone> apiResult = getDeviceManagement().listZones(request.getSiteToken(),
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
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_LIST_ZONES, e);
	    responseObserver.onError(e);
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
	    IZone apiResult = getDeviceManagement().deleteZone(request.getToken(), request.getForce());
	    GDeleteZoneResponse.Builder response = GDeleteZoneResponse.newBuilder();
	    response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(DeviceManagementGrpc.METHOD_DELETE_ZONE, e);
	    responseObserver.onError(e);
	}
    }

    public IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }

    public void setDeviceManagement(IDeviceManagement deviceManagement) {
	this.deviceManagement = deviceManagement;
    }
}