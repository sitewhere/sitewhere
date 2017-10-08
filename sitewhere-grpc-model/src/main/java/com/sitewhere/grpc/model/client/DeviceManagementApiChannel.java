package com.sitewhere.grpc.model.client;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.DeviceModelConverter;
import com.sitewhere.grpc.model.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.grpc.service.DeviceManagementGrpc;
import com.sitewhere.grpc.service.GCreateDeviceAssignmentRequest;
import com.sitewhere.grpc.service.GCreateDeviceAssignmentResponse;
import com.sitewhere.grpc.service.GCreateDeviceCommandRequest;
import com.sitewhere.grpc.service.GCreateDeviceCommandResponse;
import com.sitewhere.grpc.service.GCreateDeviceElementMappingRequest;
import com.sitewhere.grpc.service.GCreateDeviceElementMappingResponse;
import com.sitewhere.grpc.service.GCreateDeviceRequest;
import com.sitewhere.grpc.service.GCreateDeviceResponse;
import com.sitewhere.grpc.service.GCreateDeviceSpecificationRequest;
import com.sitewhere.grpc.service.GCreateDeviceSpecificationResponse;
import com.sitewhere.grpc.service.GCreateDeviceStatusRequest;
import com.sitewhere.grpc.service.GCreateDeviceStatusResponse;
import com.sitewhere.grpc.service.GDeleteDeviceAssignmentRequest;
import com.sitewhere.grpc.service.GDeleteDeviceAssignmentResponse;
import com.sitewhere.grpc.service.GDeleteDeviceCommandRequest;
import com.sitewhere.grpc.service.GDeleteDeviceCommandResponse;
import com.sitewhere.grpc.service.GDeleteDeviceElementMappingRequest;
import com.sitewhere.grpc.service.GDeleteDeviceElementMappingResponse;
import com.sitewhere.grpc.service.GDeleteDeviceRequest;
import com.sitewhere.grpc.service.GDeleteDeviceResponse;
import com.sitewhere.grpc.service.GDeleteDeviceSpecificationRequest;
import com.sitewhere.grpc.service.GDeleteDeviceSpecificationResponse;
import com.sitewhere.grpc.service.GDeleteDeviceStatusRequest;
import com.sitewhere.grpc.service.GDeleteDeviceStatusResponse;
import com.sitewhere.grpc.service.GEndDeviceAssignmentRequest;
import com.sitewhere.grpc.service.GEndDeviceAssignmentResponse;
import com.sitewhere.grpc.service.GGetCurrentAssignmentForDeviceRequest;
import com.sitewhere.grpc.service.GGetCurrentAssignmentForDeviceResponse;
import com.sitewhere.grpc.service.GGetDeviceAssignmentByTokenRequest;
import com.sitewhere.grpc.service.GGetDeviceAssignmentByTokenResponse;
import com.sitewhere.grpc.service.GGetDeviceAssignmentHistoryRequest;
import com.sitewhere.grpc.service.GGetDeviceAssignmentHistoryResponse;
import com.sitewhere.grpc.service.GGetDeviceByaHardwareIdRequest;
import com.sitewhere.grpc.service.GGetDeviceByaHardwareIdResponse;
import com.sitewhere.grpc.service.GGetDeviceCommandByTokenRequest;
import com.sitewhere.grpc.service.GGetDeviceCommandByTokenResponse;
import com.sitewhere.grpc.service.GGetDeviceSpecificationByTokenRequest;
import com.sitewhere.grpc.service.GGetDeviceSpecificationByTokenResponse;
import com.sitewhere.grpc.service.GGetDeviceStatusByCodeRequest;
import com.sitewhere.grpc.service.GGetDeviceStatusByCodeResponse;
import com.sitewhere.grpc.service.GListDeviceCommandsRequest;
import com.sitewhere.grpc.service.GListDeviceCommandsResponse;
import com.sitewhere.grpc.service.GListDeviceSpecificationsRequest;
import com.sitewhere.grpc.service.GListDeviceSpecificationsResponse;
import com.sitewhere.grpc.service.GListDeviceStatusesRequest;
import com.sitewhere.grpc.service.GListDeviceStatusesResponse;
import com.sitewhere.grpc.service.GListDevicesRequest;
import com.sitewhere.grpc.service.GListDevicesResponse;
import com.sitewhere.grpc.service.GUpdateDeviceAssignmentMetadataRequest;
import com.sitewhere.grpc.service.GUpdateDeviceAssignmentMetadataResponse;
import com.sitewhere.grpc.service.GUpdateDeviceAssignmentStatusRequest;
import com.sitewhere.grpc.service.GUpdateDeviceAssignmentStatusResponse;
import com.sitewhere.grpc.service.GUpdateDeviceCommandRequest;
import com.sitewhere.grpc.service.GUpdateDeviceCommandResponse;
import com.sitewhere.grpc.service.GUpdateDeviceRequest;
import com.sitewhere.grpc.service.GUpdateDeviceResponse;
import com.sitewhere.grpc.service.GUpdateDeviceSpecificationRequest;
import com.sitewhere.grpc.service.GUpdateDeviceSpecificationResponse;
import com.sitewhere.grpc.service.GUpdateDeviceStatusRequest;
import com.sitewhere.grpc.service.GUpdateDeviceStatusResponse;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.device.DeviceAssignmentStatus;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
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
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;

/**
 * Supports SiteWhere device management APIs on top of a
 * {@link DeviceManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class DeviceManagementApiChannel extends ApiChannel<DeviceManagementGrpcChannel>
	implements IDeviceManagementApiChannel {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant management GRPC channel */
    private DeviceManagementGrpcChannel grpcChannel;

    public DeviceManagementApiChannel(DeviceManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceSpecification(com.
     * sitewhere.spi.device.request.IDeviceSpecificationCreateRequest)
     */
    @Override
    public IDeviceSpecification createDeviceSpecification(IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_SPECIFICATION);
	    GCreateDeviceSpecificationRequest.Builder grequest = GCreateDeviceSpecificationRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceSpecificationCreateRequest(request));
	    GCreateDeviceSpecificationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceSpecification(grequest.build());
	    IDeviceSpecification response = (gresponse.hasSpecification())
		    ? DeviceModelConverter.asApiDeviceSpecification(gresponse.getSpecification()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE_SPECIFICATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_SPECIFICATION, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceSpecificationByToken(
     * java.lang.String)
     */
    @Override
    public IDeviceSpecification getDeviceSpecificationByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_SPECIFICATION_BY_TOKEN);
	    GGetDeviceSpecificationByTokenRequest.Builder grequest = GGetDeviceSpecificationByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetDeviceSpecificationByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceSpecificationByToken(grequest.build());
	    IDeviceSpecification response = (gresponse.hasSpecification())
		    ? DeviceModelConverter.asApiDeviceSpecification(gresponse.getSpecification()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_SPECIFICATION_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_SPECIFICATION_BY_TOKEN,
		    t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceSpecification(java
     * .lang.String,
     * com.sitewhere.spi.device.request.IDeviceSpecificationCreateRequest)
     */
    @Override
    public IDeviceSpecification updateDeviceSpecification(String token, IDeviceSpecificationCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_SPECIFICATION);
	    GUpdateDeviceSpecificationRequest.Builder grequest = GUpdateDeviceSpecificationRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceSpecificationCreateRequest(request));
	    GUpdateDeviceSpecificationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceSpecification(grequest.build());
	    IDeviceSpecification response = (gresponse.hasSpecification())
		    ? DeviceModelConverter.asApiDeviceSpecification(gresponse.getSpecification()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_SPECIFICATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_SPECIFICATION, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceSpecifications(
     * boolean, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceSpecification> listDeviceSpecifications(boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_SPECIFICATIONS);
	    GListDeviceSpecificationsRequest.Builder grequest = GListDeviceSpecificationsRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceSpecificationSearchCriteria(includeDeleted, criteria));
	    GListDeviceSpecificationsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceSpecifications(grequest.build());
	    ISearchResults<IDeviceSpecification> results = DeviceModelConverter
		    .asApiDeviceSpecificationSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_DEVICE_SPECIFICATIONS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_SPECIFICATIONS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceSpecification(java
     * .lang.String, boolean)
     */
    @Override
    public IDeviceSpecification deleteDeviceSpecification(String token, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_SPECIFICATION);
	    GDeleteDeviceSpecificationRequest.Builder grequest = GDeleteDeviceSpecificationRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setForce(force);
	    GDeleteDeviceSpecificationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceSpecification(grequest.build());
	    IDeviceSpecification response = (gresponse.hasSpecification())
		    ? DeviceModelConverter.asApiDeviceSpecification(gresponse.getSpecification()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_DEVICE_SPECIFICATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_SPECIFICATION, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceCommand(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand createDeviceCommand(String specificationToken, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_COMMAND);
	    GCreateDeviceCommandRequest.Builder grequest = GCreateDeviceCommandRequest.newBuilder();
	    grequest.setSpecificationToken(specificationToken);
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceCommandCreateRequest(request));
	    GCreateDeviceCommandResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceCommand(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE_COMMAND, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_COMMAND, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceCommandByToken(java.
     * lang.String)
     */
    @Override
    public IDeviceCommand getDeviceCommandByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND_BY_TOKEN);
	    GGetDeviceCommandByTokenRequest.Builder grequest = GGetDeviceCommandByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetDeviceCommandByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceCommandByToken(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND_BY_TOKEN, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceCommand(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand updateDeviceCommand(String token, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_COMMAND);
	    GUpdateDeviceCommandRequest.Builder grequest = GUpdateDeviceCommandRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceCommandCreateRequest(request));
	    GUpdateDeviceCommandResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceCommand(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_COMMAND, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_COMMAND, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommands(java.lang.
     * String, boolean)
     */
    @Override
    public List<IDeviceCommand> listDeviceCommands(String specToken, boolean includeDeleted) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_COMMANDS);
	    GListDeviceCommandsRequest.Builder grequest = GListDeviceCommandsRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceCommandSearchCriteria(includeDeleted, specToken));
	    GListDeviceCommandsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceCommands(grequest.build());
	    List<IDeviceCommand> results = DeviceModelConverter
		    .asApiDeviceCommandSearchResults(gresponse.getCommandsList());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_DEVICE_COMMANDS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_COMMANDS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceCommand(java.lang.
     * String, boolean)
     */
    @Override
    public IDeviceCommand deleteDeviceCommand(String token, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_COMMAND);
	    GDeleteDeviceCommandRequest.Builder grequest = GDeleteDeviceCommandRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setForce(force);
	    GDeleteDeviceCommandResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceCommand(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_DEVICE_COMMAND, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_COMMAND, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceStatus(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus createDeviceStatus(String specToken, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STATUS);
	    GCreateDeviceStatusRequest.Builder grequest = GCreateDeviceStatusRequest.newBuilder();
	    grequest.setSpecificationToken(specToken);
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceStatusCreateRequest(request));
	    GCreateDeviceStatusResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceStatus(grequest.build());
	    IDeviceStatus response = (gresponse.hasStatus())
		    ? DeviceModelConverter.asApiDeviceStatus(gresponse.getStatus()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STATUS, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STATUS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStatusByCode(java.
     * lang.String, java.lang.String)
     */
    @Override
    public IDeviceStatus getDeviceStatusByCode(String specToken, String code) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_STATUS_BY_CODE);
	    GGetDeviceStatusByCodeRequest.Builder grequest = GGetDeviceStatusByCodeRequest.newBuilder();
	    grequest.setSpecificationToken(specToken);
	    grequest.setCode(code);
	    GGetDeviceStatusByCodeResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceStatusByCode(grequest.build());
	    IDeviceStatus response = (gresponse.hasStatus())
		    ? DeviceModelConverter.asApiDeviceStatus(gresponse.getStatus()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_STATUS_BY_CODE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_STATUS_BY_CODE, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceStatus(java.lang.
     * String, java.lang.String,
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus updateDeviceStatus(String specToken, String code, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_STATUS);
	    GUpdateDeviceStatusRequest.Builder grequest = GUpdateDeviceStatusRequest.newBuilder();
	    grequest.setSpecificationToken(specToken);
	    grequest.setCode(code);
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceStatusCreateRequest(request));
	    GUpdateDeviceStatusResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceStatus(grequest.build());
	    IDeviceStatus response = (gresponse.hasStatus())
		    ? DeviceModelConverter.asApiDeviceStatus(gresponse.getStatus()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_STATUS, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_STATUS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceStatuses(java.lang.
     * String)
     */
    @Override
    public List<IDeviceStatus> listDeviceStatuses(String specToken) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_STATUSES);
	    GListDeviceStatusesRequest.Builder grequest = GListDeviceStatusesRequest.newBuilder();
	    grequest.setSpecificationToken(specToken);
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceStatusSearchCriteria(null));
	    GListDeviceStatusesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceStatuses(grequest.build());
	    List<IDeviceStatus> results = DeviceModelConverter
		    .asApiDeviceStatusSearchResults(gresponse.getStatusList());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_DEVICE_STATUSES, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_STATUSES, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceStatus(java.lang.
     * String, java.lang.String)
     */
    @Override
    public IDeviceStatus deleteDeviceStatus(String specToken, String code) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_STATUS);
	    GDeleteDeviceStatusRequest.Builder grequest = GDeleteDeviceStatusRequest.newBuilder();
	    grequest.setSpecificationToken(specToken);
	    grequest.setCode(code);
	    GDeleteDeviceStatusResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceStatus(grequest.build());
	    IDeviceStatus response = (gresponse.hasStatus())
		    ? DeviceModelConverter.asApiDeviceStatus(gresponse.getStatus()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_DEVICE_STATUS, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_STATUS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDevice(com.sitewhere.spi
     * .device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice createDevice(IDeviceCreateRequest device) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE);
	    GCreateDeviceRequest.Builder grequest = GCreateDeviceRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceCreateRequest(device));
	    GCreateDeviceResponse gresponse = getGrpcChannel().getBlockingStub().createDevice(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceByHardwareId(java.
     * lang.String)
     */
    @Override
    public IDevice getDeviceByHardwareId(String hardwareId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_BY_HARDWARE_ID);
	    GGetDeviceByaHardwareIdRequest.Builder grequest = GGetDeviceByaHardwareIdRequest.newBuilder();
	    grequest.setHardwareId(hardwareId);
	    GGetDeviceByaHardwareIdResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceByHardwareId(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_BY_HARDWARE_ID, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_BY_HARDWARE_ID, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDevice(java.lang.String,
     * com.sitewhere.spi.device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice updateDevice(String hardwareId, IDeviceCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE);
	    GUpdateDeviceRequest.Builder grequest = GUpdateDeviceRequest.newBuilder();
	    grequest.setHardwareId(hardwareId);
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceCreateRequest(request));
	    GUpdateDeviceResponse gresponse = getGrpcChannel().getBlockingStub().updateDevice(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_DEVICE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listDevices(boolean,
     * com.sitewhere.spi.search.device.IDeviceSearchCriteria)
     */
    @Override
    public ISearchResults<IDevice> listDevices(boolean includeDeleted, IDeviceSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICES);
	    GListDevicesRequest.Builder grequest = GListDevicesRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceSearchCriteria(criteria));
	    GListDevicesResponse gresponse = getGrpcChannel().getBlockingStub().listDevices(grequest.build());
	    ISearchResults<IDevice> results = DeviceModelConverter.asApiDeviceSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_DEVICES, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICES, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceElementMapping(
     * java.lang.String, com.sitewhere.spi.device.IDeviceElementMapping)
     */
    @Override
    public IDevice createDeviceElementMapping(String hardwareId, IDeviceElementMapping mapping)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ELEMENT_MAPPING);
	    GCreateDeviceElementMappingRequest.Builder grequest = GCreateDeviceElementMappingRequest.newBuilder();
	    grequest.setHardwareId(hardwareId);
	    grequest.setMapping(DeviceModelConverter.asGrpcDeviceElementMapping(mapping));
	    GCreateDeviceElementMappingResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceElementMapping(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ELEMENT_MAPPING, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ELEMENT_MAPPING, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceElementMapping(
     * java.lang.String, java.lang.String)
     */
    @Override
    public IDevice deleteDeviceElementMapping(String hardwareId, String path) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_ELEMENT_MAPPING);
	    GDeleteDeviceElementMappingRequest.Builder grequest = GDeleteDeviceElementMappingRequest.newBuilder();
	    grequest.setHardwareId(hardwareId);
	    grequest.setPath(path);
	    GDeleteDeviceElementMappingResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceElementMapping(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_DEVICE_ELEMENT_MAPPING, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_ELEMENT_MAPPING, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDevice(java.lang.String,
     * boolean)
     */
    @Override
    public IDevice deleteDevice(String hardwareId, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE);
	    GDeleteDeviceRequest.Builder grequest = GDeleteDeviceRequest.newBuilder();
	    grequest.setHardwareId(hardwareId);
	    grequest.setForce(force);
	    GDeleteDeviceResponse gresponse = getGrpcChannel().getBlockingStub().deleteDevice(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_DEVICE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceAssignment(com.
     * sitewhere.spi.device.request.IDeviceAssignmentCreateRequest)
     */
    @Override
    public IDeviceAssignment createDeviceAssignment(IDeviceAssignmentCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ASSIGNMENT);
	    GCreateDeviceAssignmentRequest.Builder grequest = GCreateDeviceAssignmentRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceAssignmentCreateRequest(request));
	    GCreateDeviceAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceAssignment(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ASSIGNMENT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ASSIGNMENT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentByToken(
     * java.lang.String)
     */
    @Override
    public IDeviceAssignment getDeviceAssignmentByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_BY_TOKEN);
	    GGetDeviceAssignmentByTokenRequest.Builder grequest = GGetDeviceAssignmentByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetDeviceAssignmentByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceAssignmentByToken(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_BY_TOKEN, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getCurrentDeviceAssignment(
     * java.lang.String)
     */
    @Override
    public IDeviceAssignment getCurrentDeviceAssignment(String hardwareId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_CURRENT_ASSIGNMENT_FOR_DEVICE);
	    GGetCurrentAssignmentForDeviceRequest.Builder grequest = GGetCurrentAssignmentForDeviceRequest.newBuilder();
	    grequest.setHardwareId(hardwareId);
	    GGetCurrentAssignmentForDeviceResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getCurrentAssignmentForDevice(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_CURRENT_ASSIGNMENT_FOR_DEVICE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_CURRENT_ASSIGNMENT_FOR_DEVICE,
		    t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceAssignment(java.
     * lang.String, boolean)
     */
    @Override
    public IDeviceAssignment deleteDeviceAssignment(String token, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_ASSIGNMENT);
	    GDeleteDeviceAssignmentRequest.Builder grequest = GDeleteDeviceAssignmentRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setForce(force);
	    GDeleteDeviceAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceAssignment(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_DEVICE_ASSIGNMENT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_ASSIGNMENT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentMetadata
     * (java.lang.String, java.util.Map)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignmentMetadata(String token, Map<String, String> metadata)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT_METADATA);
	    GUpdateDeviceAssignmentMetadataRequest.Builder grequest = GUpdateDeviceAssignmentMetadataRequest
		    .newBuilder();
	    grequest.setToken(token);
	    grequest.putAllMetadata(metadata);
	    GUpdateDeviceAssignmentMetadataResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceAssignmentMetadata(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT_METADATA, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT_METADATA,
		    t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentStatus(
     * java.lang.String, com.sitewhere.spi.device.DeviceAssignmentStatus)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignmentStatus(String token, DeviceAssignmentStatus status)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT_STATUS);
	    GUpdateDeviceAssignmentStatusRequest.Builder grequest = GUpdateDeviceAssignmentStatusRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setStatus(DeviceModelConverter.asGrpcDeviceAssignmentStatus(status));
	    GUpdateDeviceAssignmentStatusResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceAssignmentStatus(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT_STATUS, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT_STATUS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#endDeviceAssignment(java.lang.
     * String)
     */
    @Override
    public IDeviceAssignment endDeviceAssignment(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_END_DEVICE_ASSIGNMENT);
	    GEndDeviceAssignmentRequest.Builder grequest = GEndDeviceAssignmentRequest.newBuilder();
	    grequest.setToken(token);
	    GEndDeviceAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .endDeviceAssignment(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_END_DEVICE_ASSIGNMENT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_END_DEVICE_ASSIGNMENT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentHistory(
     * java.lang.String, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentHistory(String hardwareId, ISearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_HISTORY);
	    GGetDeviceAssignmentHistoryRequest.Builder grequest = GGetDeviceAssignmentHistoryRequest.newBuilder();
	    grequest.setHardwareId(hardwareId);
	    grequest.setCriteria(DeviceModelConverter.asGrpcDeviceAssignmentHistoryCriteria(criteria));
	    GGetDeviceAssignmentHistoryResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceAssignmentHistory(grequest.build());
	    ISearchResults<IDeviceAssignment> response = DeviceModelConverter
		    .asApiDeviceAssignmentSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_HISTORY, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_HISTORY, t);
	}
    }

    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
	    IAssignmentSearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForAsset(String assetModuleId, String assetId,
	    IAssignmentsForAssetSearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceStream createDeviceStream(String assignmentToken, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceStream getDeviceStream(String assignmentToken, String streamId) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDeviceStream> listDeviceStreams(String assignmentToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISite getSiteByToken(String token) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISite updateSite(String siteToken, ISiteCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IZone createZone(String siteToken, IZoneCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IZone getZone(String zoneToken) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IZone updateZone(String token, IZoneCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IZone deleteZone(String zoneToken, boolean force) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceGroup getDeviceGroup(String token) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceGroup updateDeviceGroup(String token, IDeviceGroupCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroups(boolean includeDeleted, ISearchCriteria criteria)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IDeviceGroup deleteDeviceGroup(String token, boolean force) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<IDeviceGroupElement> addDeviceGroupElements(String groupToken,
	    List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<IDeviceGroupElement> removeDeviceGroupElements(String groupToken,
	    List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public ISearchResults<IDeviceGroupElement> listDeviceGroupElements(String groupToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.ApiChannel#getGrpcChannel()
     */
    @Override
    public DeviceManagementGrpcChannel getGrpcChannel() {
	return grpcChannel;
    }

    public void setGrpcChannel(DeviceManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}