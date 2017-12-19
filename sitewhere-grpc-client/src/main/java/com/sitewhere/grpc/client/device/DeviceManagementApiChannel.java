/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.device;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.client.ApiChannel;
import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.DeviceModelConverter;
import com.sitewhere.grpc.service.*;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.asset.IAssetReference;
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
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.device.IAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.tracing.ITracerProvider;

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

    public DeviceManagementApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
	super(demux, microservice, host);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .tracing.ITracerProvider, java.lang.String, int)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public GrpcChannel createGrpcChannel(ITracerProvider tracerProvider, String host, int port) {
	return new DeviceManagementGrpcChannel(tracerProvider, host, port);
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
		    ? DeviceModelConverter.asApiDeviceSpecification(gresponse.getSpecification())
		    : null;
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
		    ? DeviceModelConverter.asApiDeviceSpecification(gresponse.getSpecification())
		    : null;
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
		    ? DeviceModelConverter.asApiDeviceSpecification(gresponse.getSpecification())
		    : null;
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
		    ? DeviceModelConverter.asApiDeviceSpecification(gresponse.getSpecification())
		    : null;
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
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE_COMMAND, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_COMMAND, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceCommandByToken(java.
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
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand())
		    : null;
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
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_COMMAND, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_COMMAND, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceCommands(java.lang.
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
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_DEVICE_COMMAND, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_COMMAND, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceStatus(java.lang.
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
		    ? DeviceModelConverter.asApiDeviceStatus(gresponse.getStatus())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STATUS, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STATUS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceStatusByCode(java.
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
		    ? DeviceModelConverter.asApiDeviceStatus(gresponse.getStatus())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_STATUS_BY_CODE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_STATUS_BY_CODE, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#updateDeviceStatus(java.lang.
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
		    ? DeviceModelConverter.asApiDeviceStatus(gresponse.getStatus())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_STATUS, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_STATUS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceStatuses(java.lang.
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
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteDeviceStatus(java.lang.
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
		    ? DeviceModelConverter.asApiDeviceStatus(gresponse.getStatus())
		    : null;
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
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceByHardwareId(java.
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
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceElementMapping(
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
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteDeviceElementMapping(
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
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceAssignment(com.
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
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ASSIGNMENT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_ASSIGNMENT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentByToken(
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
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_BY_TOKEN, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getCurrentDeviceAssignment(
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
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
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
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteDeviceAssignment(java.
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
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
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
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
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
     * @see com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignmentStatus(
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
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
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
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_END_DEVICE_ASSIGNMENT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_END_DEVICE_ASSIGNMENT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentHistory(
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

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForSite(
     * java.lang.String, com.sitewhere.spi.search.device.IAssignmentSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForSite(String siteToken,
	    IAssignmentSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENTS_FOR_SITE);
	    GGetDeviceAssignmentsForSiteRequest.Builder grequest = GGetDeviceAssignmentsForSiteRequest.newBuilder();
	    grequest.setSiteToken(siteToken);
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceAssignmentSearchCriteria(criteria));
	    GGetDeviceAssignmentsForSiteResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceAssignmentsForSite(grequest.build());
	    ISearchResults<IDeviceAssignment> results = DeviceModelConverter
		    .asApiDeviceAssignmentSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENTS_FOR_SITE, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENTS_FOR_SITE, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignmentsForAsset(com.
     * sitewhere.spi.asset.IAssetReference,
     * com.sitewhere.spi.search.device.IAssignmentsForAssetSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAssignment> getDeviceAssignmentsForAsset(IAssetReference assetReference,
	    IAssignmentsForAssetSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENTS_FOR_ASSET);
	    GGetDeviceAssignmentsForAssetRequest.Builder grequest = GGetDeviceAssignmentsForAssetRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceAssignmentSearchCriteria(criteria));
	    GGetDeviceAssignmentsForAssetResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceAssignmentsForAsset(grequest.build());
	    ISearchResults<IDeviceAssignment> results = DeviceModelConverter
		    .asApiDeviceAssignmentSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENTS_FOR_ASSET, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENTS_FOR_ASSET,
		    t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceStream(java.lang.
     * String, com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest)
     */
    @Override
    public IDeviceStream createDeviceStream(String assignmentToken, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STREAM);
	    GCreateDeviceStreamRequest.Builder grequest = GCreateDeviceStreamRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceStreamCreateRequest(request));
	    GCreateDeviceStreamResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceStream(grequest.build());
	    IDeviceStream response = (gresponse.hasDeviceStream())
		    ? DeviceModelConverter.asApiDeviceStream(gresponse.getDeviceStream())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STREAM, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_STREAM, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceStream(java.lang.
     * String, java.lang.String)
     */
    @Override
    public IDeviceStream getDeviceStream(String assignmentToken, String streamId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_STREAM_BY_STREAM_ID);
	    GGetDeviceStreamByStreamIdRequest.Builder grequest = GGetDeviceStreamByStreamIdRequest.newBuilder();
	    grequest.setAssignmentToken(assignmentToken);
	    grequest.setStreamId(streamId);
	    GGetDeviceStreamByStreamIdResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceStreamByStreamId(grequest.build());
	    IDeviceStream response = (gresponse.hasDeviceStream())
		    ? DeviceModelConverter.asApiDeviceStream(gresponse.getDeviceStream())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_STREAM_BY_STREAM_ID, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_STREAM_BY_STREAM_ID, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceStreams(java.lang.
     * String, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStream> listDeviceStreams(String assignmentToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_STREAMS);
	    GListDeviceStreamsRequest.Builder grequest = GListDeviceStreamsRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceStreamSearchCriteria(criteria));
	    GListDeviceStreamsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceStreams(grequest.build());
	    ISearchResults<IDeviceStream> results = DeviceModelConverter
		    .asApiDeviceStreamSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_DEVICE_STREAMS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_STREAMS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createSite(com.sitewhere.spi.
     * device.request.ISiteCreateRequest)
     */
    @Override
    public ISite createSite(ISiteCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_CREATE_SITE);
	    GCreateSiteRequest.Builder grequest = GCreateSiteRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcSiteCreateRequest(request));
	    GCreateSiteResponse gresponse = getGrpcChannel().getBlockingStub().createSite(grequest.build());
	    ISite response = (gresponse.hasSite()) ? DeviceModelConverter.asApiSite(gresponse.getSite()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_SITE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_SITE, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getSiteByToken(java.lang.
     * String)
     */
    @Override
    public ISite getSiteByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_SITE_BY_TOKEN);
	    GGetSiteByTokenRequest.Builder grequest = GGetSiteByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetSiteByTokenResponse gresponse = getGrpcChannel().getBlockingStub().getSiteByToken(grequest.build());
	    ISite response = (gresponse.hasSite()) ? DeviceModelConverter.asApiSite(gresponse.getSite()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_SITE_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_SITE_BY_TOKEN, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#updateSite(java.lang.String,
     * com.sitewhere.spi.device.request.ISiteCreateRequest)
     */
    @Override
    public ISite updateSite(String siteToken, ISiteCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_ZONE);
	    GUpdateSiteRequest.Builder grequest = GUpdateSiteRequest.newBuilder();
	    grequest.setToken(siteToken);
	    grequest.setRequest(DeviceModelConverter.asGrpcSiteCreateRequest(request));
	    GUpdateSiteResponse gresponse = getGrpcChannel().getBlockingStub().updateSite(grequest.build());
	    ISite response = (gresponse.hasSite()) ? DeviceModelConverter.asApiSite(gresponse.getSite()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_ZONE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_ZONE, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listSites(com.sitewhere.spi.
     * search.ISearchCriteria)
     */
    @Override
    public ISearchResults<ISite> listSites(ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_LIST_SITES);
	    GListSitesRequest.Builder grequest = GListSitesRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiSiteSearchCriteria(criteria));
	    GListSitesResponse gresponse = getGrpcChannel().getBlockingStub().listSites(grequest.build());
	    ISearchResults<ISite> results = DeviceModelConverter.asApiSiteSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_SITES, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_SITES, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteSite(java.lang.String,
     * boolean)
     */
    @Override
    public ISite deleteSite(String siteToken, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_DELETE_SITE);
	    GDeleteSiteRequest.Builder grequest = GDeleteSiteRequest.newBuilder();
	    grequest.setToken(siteToken);
	    grequest.setForce(force);
	    GDeleteSiteResponse gresponse = getGrpcChannel().getBlockingStub().deleteSite(grequest.build());
	    ISite response = (gresponse.hasSite()) ? DeviceModelConverter.asApiSite(gresponse.getSite()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_SITE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_SITE, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createZone(java.lang.String,
     * com.sitewhere.spi.device.request.IZoneCreateRequest)
     */
    @Override
    public IZone createZone(String siteToken, IZoneCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_CREATE_ZONE);
	    GCreateZoneRequest.Builder grequest = GCreateZoneRequest.newBuilder();
	    grequest.setSiteToken(siteToken);
	    grequest.setRequest(DeviceModelConverter.asGrpcZoneCreateRequest(request));
	    GCreateZoneResponse gresponse = getGrpcChannel().getBlockingStub().createZone(grequest.build());
	    IZone response = (gresponse.hasZone()) ? DeviceModelConverter.asApiZone(gresponse.getZone()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_ZONE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_ZONE, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getZone(java.lang.String)
     */
    @Override
    public IZone getZone(String zoneToken) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_ZONE_BY_TOKEN);
	    GGetZoneByTokenRequest.Builder grequest = GGetZoneByTokenRequest.newBuilder();
	    grequest.setToken(zoneToken);
	    GGetZoneByTokenResponse gresponse = getGrpcChannel().getBlockingStub().getZoneByToken(grequest.build());
	    IZone response = (gresponse.hasZone()) ? DeviceModelConverter.asApiZone(gresponse.getZone()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_ZONE_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_ZONE_BY_TOKEN, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#updateZone(java.lang.String,
     * com.sitewhere.spi.device.request.IZoneCreateRequest)
     */
    @Override
    public IZone updateZone(String token, IZoneCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_ZONE);
	    GUpdateZoneRequest.Builder grequest = GUpdateZoneRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setRequest(DeviceModelConverter.asGrpcZoneCreateRequest(request));
	    GUpdateZoneResponse gresponse = getGrpcChannel().getBlockingStub().updateZone(grequest.build());
	    IZone response = (gresponse.hasZone()) ? DeviceModelConverter.asApiZone(gresponse.getZone()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_ZONE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_ZONE, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listZones(java.lang.String,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IZone> listZones(String siteToken, ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_LIST_ZONES);
	    GListZonesRequest.Builder grequest = GListZonesRequest.newBuilder();
	    grequest.setSiteToken(siteToken);
	    grequest.setCriteria(DeviceModelConverter.asApiZoneSearchCriteria(criteria));
	    GListZonesResponse gresponse = getGrpcChannel().getBlockingStub().listZones(grequest.build());
	    ISearchResults<IZone> results = DeviceModelConverter.asApiZoneSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_ZONES, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_ZONES, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteZone(java.lang.String,
     * boolean)
     */
    @Override
    public IZone deleteZone(String zoneToken, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_DELETE_ZONE);
	    GDeleteZoneRequest.Builder grequest = GDeleteZoneRequest.newBuilder();
	    grequest.setToken(zoneToken);
	    grequest.setForce(force);
	    GDeleteZoneResponse gresponse = getGrpcChannel().getBlockingStub().deleteZone(grequest.build());
	    IZone response = (gresponse.hasZone()) ? DeviceModelConverter.asApiZone(gresponse.getZone()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_ZONE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_ZONE, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#createDeviceGroup(com.
     * sitewhere.spi.device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public IDeviceGroup createDeviceGroup(IDeviceGroupCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_CREATE_DEVICE_GROUP);
	    GCreateDeviceGroupRequest.Builder grequest = GCreateDeviceGroupRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceGroupCreateRequest(request));
	    GCreateDeviceGroupResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceGroup(grequest.build());
	    IDeviceGroup response = (gresponse.hasGroup()) ? DeviceModelConverter.asApiDeviceGroup(gresponse.getGroup())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE_GROUP, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_GROUP, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceGroup(java.lang.
     * String)
     */
    @Override
    public IDeviceGroup getDeviceGroup(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_GET_DEVICE_GROUP_BY_TOKEN);
	    GGetDeviceGroupByTokenRequest.Builder grequest = GGetDeviceGroupByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetDeviceGroupByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceGroupByToken(grequest.build());
	    IDeviceGroup response = (gresponse.hasGroup()) ? DeviceModelConverter.asApiDeviceGroup(gresponse.getGroup())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_GROUP_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_GROUP_BY_TOKEN, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#updateDeviceGroup(java.lang.
     * String, com.sitewhere.spi.device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public IDeviceGroup updateDeviceGroup(String token, IDeviceGroupCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_GROUP);
	    GUpdateDeviceGroupRequest.Builder grequest = GUpdateDeviceGroupRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceGroupCreateRequest(request));
	    GUpdateDeviceGroupResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceGroup(grequest.build());
	    IDeviceGroup response = (gresponse.hasGroup()) ? DeviceModelConverter.asApiDeviceGroup(gresponse.getGroup())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_GROUP, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_GROUP, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceGroups(boolean,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroups(boolean includeDeleted, ISearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS);
	    GListDeviceGroupsRequest.Builder grequest = GListDeviceGroupsRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceGroupSearchCriteria(criteria));
	    GListDeviceGroupsResponse gresponse = getGrpcChannel().getBlockingStub().listDeviceGroups(grequest.build());
	    ISearchResults<IDeviceGroup> results = DeviceModelConverter
		    .asApiDeviceGroupSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupsWithRole(java.
     * lang.String, boolean, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, boolean includeDeleted,
	    ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS_WITH_ROLE);
	    GListDeviceGroupsWithRoleRequest.Builder grequest = GListDeviceGroupsWithRoleRequest.newBuilder();
	    grequest.setCriteria(
		    DeviceModelConverter.asApiDeviceGroupsWithRoleSearchCriteria(role, includeDeleted, criteria));
	    GListDeviceGroupsWithRoleResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceGroupsWithRole(grequest.build());
	    ISearchResults<IDeviceGroup> results = DeviceModelConverter
		    .asApiDeviceGroupSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS_WITH_ROLE, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS_WITH_ROLE, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteDeviceGroup(java.lang.
     * String, boolean)
     */
    @Override
    public IDeviceGroup deleteDeviceGroup(String token, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_DELETE_DEVICE_GROUP);
	    GDeleteDeviceGroupRequest.Builder grequest = GDeleteDeviceGroupRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setForce(force);
	    GDeleteDeviceGroupResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceGroup(grequest.build());
	    IDeviceGroup response = (gresponse.hasGroup()) ? DeviceModelConverter.asApiDeviceGroup(gresponse.getGroup())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_DEVICE_GROUP, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_GROUP, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#addDeviceGroupElements(java.
     * lang.String, java.util.List, boolean)
     */
    @Override
    public List<IDeviceGroupElement> addDeviceGroupElements(String groupToken,
	    List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_ADD_DEVICE_GROUP_ELEMENTS);
	    GAddDeviceGroupElementsRequest.Builder grequest = GAddDeviceGroupElementsRequest.newBuilder();
	    grequest.setGroupToken(groupToken);
	    grequest.addAllRequests(DeviceModelConverter.asGrpcDeviceGroupElementCreateRequests(elements));
	    grequest.setIgnoreDuplicates(ignoreDuplicates);
	    GAddDeviceGroupElementsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addDeviceGroupElements(grequest.build());
	    List<IDeviceGroupElement> results = DeviceModelConverter
		    .asApiDeviceGroupElements(gresponse.getElementsList());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_ADD_DEVICE_GROUP_ELEMENTS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_ADD_DEVICE_GROUP_ELEMENTS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#removeDeviceGroupElements(java
     * .lang.String, java.util.List)
     */
    @Override
    public List<IDeviceGroupElement> removeDeviceGroupElements(String groupToken,
	    List<IDeviceGroupElementCreateRequest> elements) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_REMOVE_DEVICE_GROUP_ELEMENTS);
	    GRemoveDeviceGroupElementsRequest.Builder grequest = GRemoveDeviceGroupElementsRequest.newBuilder();
	    grequest.setGroupToken(groupToken);
	    grequest.addAllRequests(DeviceModelConverter.asGrpcDeviceGroupElementCreateRequests(elements));
	    GRemoveDeviceGroupElementsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .removeDeviceGroupElements(grequest.build());
	    List<IDeviceGroupElement> results = DeviceModelConverter
		    .asApiDeviceGroupElements(gresponse.getElementsList());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_REMOVE_DEVICE_GROUP_ELEMENTS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_REMOVE_DEVICE_GROUP_ELEMENTS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupElements(java.
     * lang.String, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroupElement> listDeviceGroupElements(String groupToken, ISearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUP_ELEMENTS);
	    GListDeviceGroupElementsRequest.Builder grequest = GListDeviceGroupElementsRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceGroupElementSearchCriteria(criteria));
	    GListDeviceGroupElementsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceGroupElements(grequest.build());
	    ISearchResults<IDeviceGroupElement> results = DeviceModelConverter
		    .asApiDeviceGroupElementsSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUP_ELEMENTS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUP_ELEMENTS, t);
	}
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