/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.device;

import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.MultitenantApiChannel;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.converter.DeviceModelConverter;
import com.sitewhere.grpc.service.*;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.area.request.IAreaCreateRequest;
import com.sitewhere.spi.area.request.IAreaTypeCreateRequest;
import com.sitewhere.spi.area.request.IZoneCreateRequest;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.IDeviceType;
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
import com.sitewhere.spi.device.streaming.IDeviceStream;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.area.IAreaSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Supports SiteWhere device management APIs on top of a
 * {@link DeviceManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class DeviceManagementApiChannel extends MultitenantApiChannel<DeviceManagementGrpcChannel>
	implements IDeviceManagementApiChannel<DeviceManagementGrpcChannel> {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(DeviceManagementApiChannel.class);

    public DeviceManagementApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
	super(demux, microservice, host);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .tracing.ITracerProvider, java.lang.String, int)
     */
    @Override
    public DeviceManagementGrpcChannel createGrpcChannel(ITracerProvider tracerProvider, String host, int port) {
	return new DeviceManagementGrpcChannel(tracerProvider, host, port);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceType(com.sitewhere.spi
     * .device.request.IDeviceTypeCreateRequest)
     */
    @Override
    public IDeviceType createDeviceType(IDeviceTypeCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_CREATE_DEVICE_TYPE);
	    GCreateDeviceTypeRequest.Builder grequest = GCreateDeviceTypeRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceTypeCreateRequest(request));
	    GCreateDeviceTypeResponse gresponse = getGrpcChannel().getBlockingStub().createDeviceType(grequest.build());
	    IDeviceType response = (gresponse.hasDeviceType())
		    ? DeviceModelConverter.asApiDeviceType(gresponse.getDeviceType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_DEVICE_TYPE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_DEVICE_TYPE, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceType(java.util.UUID)
     */
    @Override
    public IDeviceType getDeviceType(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_DEVICE_TYPE);
	    GGetDeviceTypeRequest.Builder grequest = GGetDeviceTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetDeviceTypeResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceType(grequest.build());
	    IDeviceType response = (gresponse.hasDeviceType())
		    ? DeviceModelConverter.asApiDeviceType(gresponse.getDeviceType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_TYPE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_TYPE, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceTypeByToken(java.lang.
     * String)
     */
    @Override
    public IDeviceType getDeviceTypeByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_DEVICE_TYPE_BY_TOKEN);
	    GGetDeviceTypeByTokenRequest.Builder grequest = GGetDeviceTypeByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetDeviceTypeByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceTypeByToken(grequest.build());
	    IDeviceType response = (gresponse.hasDeviceType())
		    ? DeviceModelConverter.asApiDeviceType(gresponse.getDeviceType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_TYPE_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_TYPE_BY_TOKEN, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceType(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceTypeCreateRequest)
     */
    @Override
    public IDeviceType updateDeviceType(UUID id, IDeviceTypeCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_UPDATE_DEVICE_TYPE);
	    GUpdateDeviceTypeRequest.Builder grequest = GUpdateDeviceTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceTypeCreateRequest(request));
	    GUpdateDeviceTypeResponse gresponse = getGrpcChannel().getBlockingStub().updateDeviceType(grequest.build());
	    IDeviceType response = (gresponse.hasDeviceType())
		    ? DeviceModelConverter.asApiDeviceType(gresponse.getDeviceType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_TYPE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_TYPE, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceTypes(boolean,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceType> listDeviceTypes(boolean includeDeleted, ISearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_LIST_DEVICE_TYPES);
	    GListDeviceTypesRequest.Builder grequest = GListDeviceTypesRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceTypeSearchCriteria(includeDeleted, criteria));
	    GListDeviceTypesResponse gresponse = getGrpcChannel().getBlockingStub().listDeviceTypes(grequest.build());
	    ISearchResults<IDeviceType> results = DeviceModelConverter
		    .asApiDeviceTypeSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_DEVICE_TYPES, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_TYPES, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceType(java.util.UUID,
     * boolean)
     */
    @Override
    public IDeviceType deleteDeviceType(UUID id, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_DELETE_DEVICE_TYPE);
	    GDeleteDeviceTypeRequest.Builder grequest = GDeleteDeviceTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setForce(force);
	    GDeleteDeviceTypeResponse gresponse = getGrpcChannel().getBlockingStub().deleteDeviceType(grequest.build());
	    IDeviceType response = (gresponse.hasDeviceType())
		    ? DeviceModelConverter.asApiDeviceType(gresponse.getDeviceType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_DEVICE_TYPE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_DEVICE_TYPE, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceCommand(java.util.
     * UUID, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand createDeviceCommand(UUID deviceTypeId, IDeviceCommandCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_CREATE_DEVICE_COMMAND);
	    GCreateDeviceCommandRequest.Builder grequest = GCreateDeviceCommandRequest.newBuilder();
	    grequest.setDeviceTypeId(CommonModelConverter.asGrpcUuid(deviceTypeId));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceCommand(java.util.UUID)
     */
    @Override
    public IDeviceCommand getDeviceCommand(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND);
	    GGetDeviceCommandRequest.Builder grequest = GGetDeviceCommandRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetDeviceCommandResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceCommand(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND, t);
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
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_DEVICE_COMMAND_BY_TOKEN);
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceCommand(java.util.
     * UUID, com.sitewhere.spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand updateDeviceCommand(UUID id, IDeviceCommandCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_UPDATE_DEVICE_COMMAND);
	    GUpdateDeviceCommandRequest.Builder grequest = GUpdateDeviceCommandRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommands(java.util.UUID,
     * boolean)
     */
    @Override
    public List<IDeviceCommand> listDeviceCommands(UUID deviceTypeId, boolean includeDeleted)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_LIST_DEVICE_COMMANDS);
	    GListDeviceCommandsRequest.Builder grequest = GListDeviceCommandsRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceCommandSearchCriteria(includeDeleted, deviceTypeId));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceCommand(java.util.
     * UUID, boolean)
     */
    @Override
    public IDeviceCommand deleteDeviceCommand(UUID id, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_DELETE_DEVICE_COMMAND);
	    GDeleteDeviceCommandRequest.Builder grequest = GDeleteDeviceCommandRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceStatus(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus createDeviceStatus(UUID deviceTypeId, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_CREATE_DEVICE_STATUS);
	    GCreateDeviceStatusRequest.Builder grequest = GCreateDeviceStatusRequest.newBuilder();
	    grequest.setDeviceTypeId(CommonModelConverter.asGrpcUuid(deviceTypeId));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStatusByCode(java.util.
     * UUID, java.lang.String)
     */
    @Override
    public IDeviceStatus getDeviceStatusByCode(UUID deviceTypeId, String code) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_DEVICE_STATUS_BY_CODE);
	    GGetDeviceStatusByCodeRequest.Builder grequest = GGetDeviceStatusByCodeRequest.newBuilder();
	    grequest.setDeviceTypeId(CommonModelConverter.asGrpcUuid(deviceTypeId));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceStatus(java.util.UUID,
     * java.lang.String,
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus updateDeviceStatus(UUID deviceTypeId, String code, IDeviceStatusCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_UPDATE_DEVICE_STATUS);
	    GUpdateDeviceStatusRequest.Builder grequest = GUpdateDeviceStatusRequest.newBuilder();
	    grequest.setDeviceTypeId(CommonModelConverter.asGrpcUuid(deviceTypeId));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceStatuses(java.util.UUID)
     */
    @Override
    public List<IDeviceStatus> listDeviceStatuses(UUID deviceTypeId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_LIST_DEVICE_STATUSES);
	    GListDeviceStatusesRequest.Builder grequest = GListDeviceStatusesRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceStatusSearchCriteria(deviceTypeId, null));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceStatus(java.util.UUID,
     * java.lang.String)
     */
    @Override
    public IDeviceStatus deleteDeviceStatus(UUID deviceTypeId, String code) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_DELETE_DEVICE_STATUS);
	    GDeleteDeviceStatusRequest.Builder grequest = GDeleteDeviceStatusRequest.newBuilder();
	    grequest.setDeviceTypeId(CommonModelConverter.asGrpcUuid(deviceTypeId));
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
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_CREATE_DEVICE);
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
     * @see com.sitewhere.spi.device.IDeviceManagement#getDevice(java.util.UUID)
     */
    @Override
    public IDevice getDevice(UUID deviceId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_DEVICE);
	    GGetDeviceRequest.Builder grequest = GGetDeviceRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(deviceId));
	    GGetDeviceResponse gresponse = getGrpcChannel().getBlockingStub().getDevice(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceByToken(java.lang.String)
     */
    @Override
    public IDevice getDeviceByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_DEVICE_BY_TOKEN);
	    GGetDeviceByTokenRequest.Builder grequest = GGetDeviceByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetDeviceByTokenResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceByToken(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_BY_TOKEN, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateDevice(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice updateDevice(UUID id, IDeviceCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_UPDATE_DEVICE);
	    GUpdateDeviceRequest.Builder grequest = GUpdateDeviceRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
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
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_LIST_DEVICES);
	    GListDevicesRequest.Builder grequest = GListDevicesRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asGrpcDeviceSearchCriteria(criteria));
	    GListDevicesResponse gresponse = getGrpcChannel().getBlockingStub().listDevices(grequest.build());
	    ISearchResults<IDevice> results = DeviceModelConverter.asApiDeviceSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_DEVICES, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICES, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceElementMapping(java.
     * util.UUID, com.sitewhere.spi.device.IDeviceElementMapping)
     */
    @Override
    public IDevice createDeviceElementMapping(UUID id, IDeviceElementMapping mapping) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_CREATE_DEVICE_ELEMENT_MAPPING);
	    GCreateDeviceElementMappingRequest.Builder grequest = GCreateDeviceElementMappingRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceElementMapping(java.
     * util.UUID, java.lang.String)
     */
    @Override
    public IDevice deleteDeviceElementMapping(UUID id, String path) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_DELETE_DEVICE_ELEMENT_MAPPING);
	    GDeleteDeviceElementMappingRequest.Builder grequest = GDeleteDeviceElementMappingRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
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
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteDevice(java.util.UUID,
     * boolean)
     */
    @Override
    public IDevice deleteDevice(UUID id, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_DELETE_DEVICE);
	    GDeleteDeviceRequest.Builder grequest = GDeleteDeviceRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
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
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_CREATE_DEVICE_ASSIGNMENT);
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAssignment(java.util.
     * UUID)
     */
    @Override
    public IDeviceAssignment getDeviceAssignment(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT);
	    GGetDeviceAssignmentRequest.Builder grequest = GGetDeviceAssignmentRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetDeviceAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceAssignment(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT, t);
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
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_DEVICE_ASSIGNMENT_BY_TOKEN);
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getCurrentDeviceAssignment(java.
     * util.UUID)
     */
    @Override
    public IDeviceAssignment getCurrentDeviceAssignment(UUID deviceId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_CURRENT_ASSIGNMENT_FOR_DEVICE);
	    GGetCurrentAssignmentForDeviceRequest.Builder grequest = GGetCurrentAssignmentForDeviceRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(deviceId));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceAssignment(java.util.
     * UUID, boolean)
     */
    @Override
    public IDeviceAssignment deleteDeviceAssignment(UUID id, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_DELETE_DEVICE_ASSIGNMENT);
	    GDeleteDeviceAssignmentRequest.Builder grequest = GDeleteDeviceAssignmentRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAssignment(java.util.
     * UUID, com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest)
     */
    @Override
    public IDeviceAssignment updateDeviceAssignment(UUID id, IDeviceAssignmentCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT);
	    GUpdateDeviceAssignmentRequest.Builder grequest = GUpdateDeviceAssignmentRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceAssignmentCreateRequest(request));
	    GUpdateDeviceAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceAssignment(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_DEVICE_ASSIGNMENT, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#endDeviceAssignment(java.util.
     * UUID)
     */
    @Override
    public IDeviceAssignment endDeviceAssignment(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_END_DEVICE_ASSIGNMENT);
	    GEndDeviceAssignmentRequest.Builder grequest = GEndDeviceAssignmentRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
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
     * @see com.sitewhere.spi.device.IDeviceManagement#listDeviceAssignments(com.
     * sitewhere.spi.search.device.IDeviceAssignmentSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAssignment> listDeviceAssignments(IDeviceAssignmentSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_LIST_DEVICE_ASSIGNMENTS);
	    GListDeviceAssignmentsRequest.Builder grequest = GListDeviceAssignmentsRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asGrpcDeviceAssignmentSearchCriteria(criteria));
	    GListDeviceAssignmentsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceAssignments(grequest.build());
	    ISearchResults<IDeviceAssignment> response = DeviceModelConverter
		    .asApiDeviceAssignmentSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_DEVICE_ASSIGNMENTS, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_DEVICE_ASSIGNMENTS, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceStream(java.util.UUID,
     * com.sitewhere.spi.device.event.request.IDeviceStreamCreateRequest)
     */
    @Override
    public IDeviceStream createDeviceStream(UUID assignmentId, IDeviceStreamCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_CREATE_DEVICE_STREAM);
	    GCreateDeviceStreamRequest.Builder grequest = GCreateDeviceStreamRequest.newBuilder();
	    grequest.setAssignmentId(CommonModelConverter.asGrpcUuid(assignmentId));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStream(java.util.UUID,
     * java.lang.String)
     */
    @Override
    public IDeviceStream getDeviceStream(UUID assignmentId, String streamId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_DEVICE_STREAM_BY_STREAM_ID);
	    GGetDeviceStreamByStreamIdRequest.Builder grequest = GGetDeviceStreamByStreamIdRequest.newBuilder();
	    grequest.setAssignmentId(CommonModelConverter.asGrpcUuid(assignmentId));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceStreams(java.util.UUID,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStream> listDeviceStreams(UUID assignmentId, ISearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_LIST_DEVICE_STREAMS);
	    GListDeviceStreamsRequest.Builder grequest = GListDeviceStreamsRequest.newBuilder();
	    grequest.setAssignmentId(CommonModelConverter.asGrpcUuid(assignmentId));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createAreaType(com.sitewhere.spi.
     * area.request.IAreaTypeCreateRequest)
     */
    @Override
    public IAreaType createAreaType(IAreaTypeCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_CREATE_AREA_TYPE);
	    GCreateAreaTypeRequest.Builder grequest = GCreateAreaTypeRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcAreaTypeCreateRequest(request));
	    GCreateAreaTypeResponse gresponse = getGrpcChannel().getBlockingStub().createAreaType(grequest.build());
	    IAreaType response = (gresponse.hasAreaType()) ? DeviceModelConverter.asApiAreaType(gresponse.getAreaType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_AREA_TYPE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_AREA_TYPE, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getAreaType(java.util.UUID)
     */
    @Override
    public IAreaType getAreaType(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_AREA_TYPE);
	    GGetAreaTypeRequest.Builder grequest = GGetAreaTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetAreaTypeResponse gresponse = getGrpcChannel().getBlockingStub().getAreaType(grequest.build());
	    IAreaType response = (gresponse.hasAreaType()) ? DeviceModelConverter.asApiAreaType(gresponse.getAreaType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_AREA_TYPE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_AREA_TYPE, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getAreaTypeByToken(java.lang.
     * String)
     */
    @Override
    public IAreaType getAreaTypeByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_AREA_TYPE_BY_TOKEN);
	    GGetAreaTypeByTokenRequest.Builder grequest = GGetAreaTypeByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetAreaTypeByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getAreaTypeByToken(grequest.build());
	    IAreaType response = (gresponse.hasAreaType()) ? DeviceModelConverter.asApiAreaType(gresponse.getAreaType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_AREA_TYPE_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_AREA_TYPE_BY_TOKEN, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateAreaType(java.util.UUID,
     * com.sitewhere.spi.area.request.IAreaTypeCreateRequest)
     */
    @Override
    public IAreaType updateAreaType(UUID id, IAreaTypeCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_UPDATE_AREA_TYPE);
	    GUpdateAreaTypeRequest.Builder grequest = GUpdateAreaTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcAreaTypeCreateRequest(request));
	    GUpdateAreaTypeResponse gresponse = getGrpcChannel().getBlockingStub().updateAreaType(grequest.build());
	    IAreaType response = (gresponse.hasAreaType()) ? DeviceModelConverter.asApiAreaType(gresponse.getAreaType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_AREA_TYPE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_AREA_TYPE, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listAreaTypes(com.sitewhere.spi.
     * search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IAreaType> listAreaTypes(ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_LIST_AREA_TYPES);
	    GListAreaTypesRequest.Builder grequest = GListAreaTypesRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiAreaTypeSearchCriteria(criteria));
	    GListAreaTypesResponse gresponse = getGrpcChannel().getBlockingStub().listAreaTypes(grequest.build());
	    ISearchResults<IAreaType> results = DeviceModelConverter.asApiAreaTypeSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_AREA_TYPES, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_AREA_TYPES, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteAreaType(java.util.UUID,
     * boolean)
     */
    @Override
    public IAreaType deleteAreaType(UUID id, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_DELETE_AREA_TYPE);
	    GDeleteAreaTypeRequest.Builder grequest = GDeleteAreaTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setForce(force);
	    GDeleteAreaTypeResponse gresponse = getGrpcChannel().getBlockingStub().deleteAreaType(grequest.build());
	    IAreaType response = (gresponse.hasAreaType()) ? DeviceModelConverter.asApiAreaType(gresponse.getAreaType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_AREA_TYPE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_AREA_TYPE, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createArea(com.sitewhere.spi.area.
     * request.IAreaCreateRequest)
     */
    @Override
    public IArea createArea(IAreaCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_CREATE_AREA);
	    GCreateAreaRequest.Builder grequest = GCreateAreaRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcAreaCreateRequest(request));
	    GCreateAreaResponse gresponse = getGrpcChannel().getBlockingStub().createArea(grequest.build());
	    IArea response = (gresponse.hasArea()) ? DeviceModelConverter.asApiArea(gresponse.getArea()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_CREATE_AREA, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_CREATE_AREA, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getArea(java.util.UUID)
     */
    @Override
    public IArea getArea(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_AREA);
	    GGetAreaRequest.Builder grequest = GGetAreaRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetAreaResponse gresponse = getGrpcChannel().getBlockingStub().getArea(grequest.build());
	    IArea response = (gresponse.hasArea()) ? DeviceModelConverter.asApiArea(gresponse.getArea()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_AREA, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_AREA, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getAreaByToken(java.lang.String)
     */
    @Override
    public IArea getAreaByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_AREA_BY_TOKEN);
	    GGetAreaByTokenRequest.Builder grequest = GGetAreaByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetAreaByTokenResponse gresponse = getGrpcChannel().getBlockingStub().getAreaByToken(grequest.build());
	    IArea response = (gresponse.hasArea()) ? DeviceModelConverter.asApiArea(gresponse.getArea()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_AREA_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_AREA_BY_TOKEN, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getAreaChildren(java.lang.String)
     */
    @Override
    public List<IArea> getAreaChildren(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_AREA_CHILDREN);
	    GGetAreaChildrenRequest.Builder grequest = GGetAreaChildrenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetAreaChildrenResponse gresponse = getGrpcChannel().getBlockingStub().getAreaChildren(grequest.build());
	    List<IArea> response = DeviceModelConverter.asApiAreas(gresponse.getAreasList());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_AREA_CHILDREN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_AREA_CHILDREN, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateArea(java.util.UUID,
     * com.sitewhere.spi.area.request.IAreaCreateRequest)
     */
    @Override
    public IArea updateArea(UUID id, IAreaCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_UPDATE_AREA);
	    GUpdateAreaRequest.Builder grequest = GUpdateAreaRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcAreaCreateRequest(request));
	    GUpdateAreaResponse gresponse = getGrpcChannel().getBlockingStub().updateArea(grequest.build());
	    IArea response = (gresponse.hasArea()) ? DeviceModelConverter.asApiArea(gresponse.getArea()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_UPDATE_AREA, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_UPDATE_AREA, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listAreas(com.sitewhere.spi.search
     * .area.IAreaSearchCriteria)
     */
    @Override
    public ISearchResults<IArea> listAreas(IAreaSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_LIST_AREAS);
	    GListAreasRequest.Builder grequest = GListAreasRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asGrpcAreaSearchCriteria(criteria));
	    GListAreasResponse gresponse = getGrpcChannel().getBlockingStub().listAreas(grequest.build());
	    ISearchResults<IArea> results = DeviceModelConverter.asApiAreaSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_LIST_AREAS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_LIST_AREAS, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteArea(java.util.UUID,
     * boolean)
     */
    @Override
    public IArea deleteArea(UUID id, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_DELETE_AREA);
	    GDeleteAreaRequest.Builder grequest = GDeleteAreaRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setForce(force);
	    GDeleteAreaResponse gresponse = getGrpcChannel().getBlockingStub().deleteArea(grequest.build());
	    IArea response = (gresponse.hasArea()) ? DeviceModelConverter.asApiArea(gresponse.getArea()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_DELETE_AREA, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_DELETE_AREA, t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#createZone(java.util.UUID,
     * com.sitewhere.spi.area.request.IZoneCreateRequest)
     */
    @Override
    public IZone createZone(UUID siteId, IZoneCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_CREATE_ZONE);
	    GCreateZoneRequest.Builder grequest = GCreateZoneRequest.newBuilder();
	    grequest.setSiteId(CommonModelConverter.asGrpcUuid(siteId));
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
     * @see com.sitewhere.spi.device.IDeviceManagement#getZone(java.util.UUID)
     */
    @Override
    public IZone getZone(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_ZONE);
	    GGetZoneRequest.Builder grequest = GGetZoneRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetZoneResponse gresponse = getGrpcChannel().getBlockingStub().getZone(grequest.build());
	    IZone response = (gresponse.hasZone()) ? DeviceModelConverter.asApiZone(gresponse.getZone()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_ZONE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_ZONE, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getZoneByToken(java.lang.String)
     */
    @Override
    public IZone getZoneByToken(String zoneToken) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_ZONE_BY_TOKEN);
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
     * @see com.sitewhere.spi.device.IDeviceManagement#updateZone(java.util.UUID,
     * com.sitewhere.spi.area.request.IZoneCreateRequest)
     */
    @Override
    public IZone updateZone(UUID id, IZoneCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_UPDATE_ZONE);
	    GUpdateZoneRequest.Builder grequest = GUpdateZoneRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
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
     * @see com.sitewhere.spi.device.IDeviceManagement#listZones(java.util.UUID,
     * com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IZone> listZones(UUID siteId, ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_LIST_ZONES);
	    GListZonesRequest.Builder grequest = GListZonesRequest.newBuilder();
	    grequest.setSiteId(CommonModelConverter.asGrpcUuid(siteId));
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
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteZone(java.util.UUID,
     * boolean)
     */
    @Override
    public IZone deleteZone(UUID id, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_DELETE_ZONE);
	    GDeleteZoneRequest.Builder grequest = GDeleteZoneRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
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
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_CREATE_DEVICE_GROUP);
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceGroup(java.util.UUID)
     */
    @Override
    public IDeviceGroup getDeviceGroup(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_DEVICE_GROUP);
	    GGetDeviceGroupRequest.Builder grequest = GGetDeviceGroupRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetDeviceGroupResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceGroup(grequest.build());
	    IDeviceGroup response = (gresponse.hasGroup()) ? DeviceModelConverter.asApiDeviceGroup(gresponse.getGroup())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.METHOD_GET_DEVICE_GROUP, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.METHOD_GET_DEVICE_GROUP, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceGroupByToken(java.lang.
     * String)
     */
    @Override
    public IDeviceGroup getDeviceGroupByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_GET_DEVICE_GROUP_BY_TOKEN);
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceGroup(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceGroupCreateRequest)
     */
    @Override
    public IDeviceGroup updateDeviceGroup(UUID id, IDeviceGroupCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_UPDATE_DEVICE_GROUP);
	    GUpdateDeviceGroupRequest.Builder grequest = GUpdateDeviceGroupRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
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
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS);
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
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUPS_WITH_ROLE);
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceGroup(java.util.UUID,
     * boolean)
     */
    @Override
    public IDeviceGroup deleteDeviceGroup(UUID id, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_DELETE_DEVICE_GROUP);
	    GDeleteDeviceGroupRequest.Builder grequest = GDeleteDeviceGroupRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#addDeviceGroupElements(java.util.
     * UUID, java.util.List, boolean)
     */
    @Override
    public List<IDeviceGroupElement> addDeviceGroupElements(UUID groupId,
	    List<IDeviceGroupElementCreateRequest> elements, boolean ignoreDuplicates) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_ADD_DEVICE_GROUP_ELEMENTS);
	    GAddDeviceGroupElementsRequest.Builder grequest = GAddDeviceGroupElementsRequest.newBuilder();
	    grequest.setGroupId(CommonModelConverter.asGrpcUuid(groupId));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#removeDeviceGroupElements(java.
     * util.List)
     */
    @Override
    public List<IDeviceGroupElement> removeDeviceGroupElements(List<UUID> elements) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_REMOVE_DEVICE_GROUP_ELEMENTS);
	    GRemoveDeviceGroupElementsRequest.Builder grequest = GRemoveDeviceGroupElementsRequest.newBuilder();
	    grequest.addAllElementIds(CommonModelConverter.asGrpcUuids(elements));
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
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupElements(java.util.
     * UUID, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroupElement> listDeviceGroupElements(UUID groupId, ISearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, DeviceManagementGrpc.METHOD_LIST_DEVICE_GROUP_ELEMENTS);
	    GListDeviceGroupElementsRequest.Builder grequest = GListDeviceGroupElementsRequest.newBuilder();
	    grequest.setGroupId(CommonModelConverter.asGrpcUuid(groupId));
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
    public Log getLogger() {
	return LOGGER;
    }
}