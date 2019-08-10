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

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.MultitenantApiChannel;
import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.grpc.service.*;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.area.IZone;
import com.sitewhere.spi.area.request.IAreaCreateRequest;
import com.sitewhere.spi.area.request.IAreaTypeCreateRequest;
import com.sitewhere.spi.area.request.IZoneCreateRequest;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.customer.request.ICustomerCreateRequest;
import com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAlarm;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.command.IDeviceCommand;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.device.group.IDeviceGroupElement;
import com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest;
import com.sitewhere.spi.device.request.IDeviceAssignmentCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCommandCreateRequest;
import com.sitewhere.spi.device.request.IDeviceCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupCreateRequest;
import com.sitewhere.spi.device.request.IDeviceGroupElementCreateRequest;
import com.sitewhere.spi.device.request.IDeviceStatusCreateRequest;
import com.sitewhere.spi.device.request.IDeviceTypeCreateRequest;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.grpc.GrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.ITreeNode;
import com.sitewhere.spi.search.area.IAreaSearchCriteria;
import com.sitewhere.spi.search.customer.ICustomerSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceAlarmSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceAssignmentSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceCommandSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceSearchCriteria;
import com.sitewhere.spi.search.device.IDeviceStatusSearchCriteria;
import com.sitewhere.spi.search.device.IZoneSearchCriteria;

/**
 * Supports SiteWhere device management APIs on top of a
 * {@link DeviceManagementGrpcChannel}.
 */
public class DeviceManagementApiChannel extends MultitenantApiChannel<DeviceManagementGrpcChannel>
	implements IDeviceManagementApiChannel<DeviceManagementGrpcChannel> {

    public DeviceManagementApiChannel(IInstanceSettings settings) {
	super(settings, MicroserviceIdentifier.DeviceManagement, GrpcServiceIdentifier.DeviceManagement,
		IGrpcSettings.DEFAULT_API_PORT);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .microservice.instance.IInstanceSettings,
     * com.sitewhere.spi.microservice.IFunctionIdentifier,
     * com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier, int)
     */
    @Override
    public DeviceManagementGrpcChannel createGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	return new DeviceManagementGrpcChannel(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createCustomerType(com.sitewhere.
     * spi.customer.request.ICustomerTypeCreateRequest)
     */
    @Override
    public ICustomerType createCustomerType(ICustomerTypeCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getCreateCustomerTypeMethod());
	    GCreateCustomerTypeRequest.Builder grequest = GCreateCustomerTypeRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcCustomerTypeCreateRequest(request));
	    GCreateCustomerTypeResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createCustomerType(grequest.build());
	    ICustomerType response = (gresponse.hasCustomerType())
		    ? DeviceModelConverter.asApiCustomerType(gresponse.getCustomerType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getCreateCustomerTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getCreateCustomerTypeMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getCustomerType(java.util.UUID)
     */
    @Override
    public ICustomerType getCustomerType(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetCustomerTypeMethod());
	    GGetCustomerTypeRequest.Builder grequest = GGetCustomerTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetCustomerTypeResponse gresponse = getGrpcChannel().getBlockingStub().getCustomerType(grequest.build());
	    ICustomerType response = (gresponse.hasCustomerType())
		    ? DeviceModelConverter.asApiCustomerType(gresponse.getCustomerType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetCustomerTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetCustomerTypeMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getCustomerTypeByToken(java.lang.
     * String)
     */
    @Override
    public ICustomerType getCustomerTypeByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetCustomerTypeByTokenMethod());
	    GGetCustomerTypeByTokenRequest.Builder grequest = GGetCustomerTypeByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetCustomerTypeByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getCustomerTypeByToken(grequest.build());
	    ICustomerType response = (gresponse.hasCustomerType())
		    ? DeviceModelConverter.asApiCustomerType(gresponse.getCustomerType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetCustomerTypeByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetCustomerTypeByTokenMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateCustomerType(java.util.UUID,
     * com.sitewhere.spi.customer.request.ICustomerTypeCreateRequest)
     */
    @Override
    public ICustomerType updateCustomerType(UUID id, ICustomerTypeCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getUpdateCustomerTypeMethod());
	    GUpdateCustomerTypeRequest.Builder grequest = GUpdateCustomerTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcCustomerTypeCreateRequest(request));
	    GUpdateCustomerTypeResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateCustomerType(grequest.build());
	    ICustomerType response = (gresponse.hasCustomerType())
		    ? DeviceModelConverter.asApiCustomerType(gresponse.getCustomerType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getUpdateCustomerTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getUpdateCustomerTypeMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listCustomerTypes(com.sitewhere.
     * spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<ICustomerType> listCustomerTypes(ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getListCustomerTypesMethod());
	    GListCustomerTypesRequest.Builder grequest = GListCustomerTypesRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiCustomerTypeSearchCriteria(criteria));
	    GListCustomerTypesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listCustomerTypes(grequest.build());
	    ISearchResults<ICustomerType> results = DeviceModelConverter
		    .asApiCustomerTypeSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getListCustomerTypesMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getListCustomerTypesMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteCustomerType(java.util.UUID)
     */
    @Override
    public ICustomerType deleteCustomerType(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getDeleteCustomerTypeMethod());
	    GDeleteCustomerTypeRequest.Builder grequest = GDeleteCustomerTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteCustomerTypeResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteCustomerType(grequest.build());
	    ICustomerType response = (gresponse.hasCustomerType())
		    ? DeviceModelConverter.asApiCustomerType(gresponse.getCustomerType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getDeleteCustomerTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getDeleteCustomerTypeMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createCustomer(com.sitewhere.spi.
     * customer.request.ICustomerCreateRequest)
     */
    @Override
    public ICustomer createCustomer(ICustomerCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getCreateCustomerMethod());
	    GCreateCustomerRequest.Builder grequest = GCreateCustomerRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcCustomerCreateRequest(request));
	    GCreateCustomerResponse gresponse = getGrpcChannel().getBlockingStub().createCustomer(grequest.build());
	    ICustomer response = (gresponse.hasCustomer()) ? DeviceModelConverter.asApiCustomer(gresponse.getCustomer())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getCreateCustomerMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getCreateCustomerMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getCustomer(java.util.UUID)
     */
    @Override
    public ICustomer getCustomer(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetCustomerMethod());
	    GGetCustomerRequest.Builder grequest = GGetCustomerRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetCustomerResponse gresponse = getGrpcChannel().getBlockingStub().getCustomer(grequest.build());
	    ICustomer response = (gresponse.hasCustomer()) ? DeviceModelConverter.asApiCustomer(gresponse.getCustomer())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetCustomerMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetCustomerMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getCustomerByToken(java.lang.
     * String)
     */
    @Override
    public ICustomer getCustomerByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetCustomerByTokenMethod());
	    GGetCustomerByTokenRequest.Builder grequest = GGetCustomerByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetCustomerByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getCustomerByToken(grequest.build());
	    ICustomer response = (gresponse.hasCustomer()) ? DeviceModelConverter.asApiCustomer(gresponse.getCustomer())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetCustomerByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetCustomerByTokenMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getCustomerChildren(java.lang.
     * String)
     */
    @Override
    public List<ICustomer> getCustomerChildren(String token) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetCustomerChildrenMethod());
	    GGetCustomerChildrenRequest.Builder grequest = GGetCustomerChildrenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetCustomerChildrenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getCustomerChildren(grequest.build());
	    List<ICustomer> response = DeviceModelConverter.asApiCustomers(gresponse.getCustomersList());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetCustomerChildrenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetCustomerChildrenMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateCustomer(java.util.UUID,
     * com.sitewhere.spi.customer.request.ICustomerCreateRequest)
     */
    @Override
    public ICustomer updateCustomer(UUID id, ICustomerCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getUpdateCustomerMethod());
	    GUpdateCustomerRequest.Builder grequest = GUpdateCustomerRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcCustomerCreateRequest(request));
	    GUpdateCustomerResponse gresponse = getGrpcChannel().getBlockingStub().updateCustomer(grequest.build());
	    ICustomer response = (gresponse.hasCustomer()) ? DeviceModelConverter.asApiCustomer(gresponse.getCustomer())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getUpdateCustomerMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getUpdateCustomerMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listCustomers(com.sitewhere.spi.
     * search.customer.ICustomerSearchCriteria)
     */
    @Override
    public ISearchResults<ICustomer> listCustomers(ICustomerSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getListCustomersMethod());
	    GListCustomersRequest.Builder grequest = GListCustomersRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asGrpcCustomerSearchCriteria(criteria));
	    GListCustomersResponse gresponse = getGrpcChannel().getBlockingStub().listCustomers(grequest.build());
	    ISearchResults<ICustomer> results = DeviceModelConverter.asApiCustomerSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getListCustomersMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getListCustomersMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getCustomersTree()
     */
    @Override
    public List<ITreeNode> getCustomersTree() throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetCustomersTreeMethod());
	    GGetCustomersTreeRequest.Builder grequest = GGetCustomersTreeRequest.newBuilder();
	    GGetCustomersTreeResponse gresponse = getGrpcChannel().getBlockingStub().getCustomersTree(grequest.build());
	    List<ITreeNode> results = DeviceModelConverter.asApiTreeNodes(gresponse.getCustomersList());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetCustomersTreeMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetCustomersTreeMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteCustomer(java.util.UUID)
     */
    @Override
    public ICustomer deleteCustomer(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getDeleteCustomerMethod());
	    GDeleteCustomerRequest.Builder grequest = GDeleteCustomerRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteCustomerResponse gresponse = getGrpcChannel().getBlockingStub().deleteCustomer(grequest.build());
	    ICustomer response = (gresponse.hasCustomer()) ? DeviceModelConverter.asApiCustomer(gresponse.getCustomer())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getDeleteCustomerMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getDeleteCustomerMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getCreateAreaTypeMethod());
	    GCreateAreaTypeRequest.Builder grequest = GCreateAreaTypeRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcAreaTypeCreateRequest(request));
	    GCreateAreaTypeResponse gresponse = getGrpcChannel().getBlockingStub().createAreaType(grequest.build());
	    IAreaType response = (gresponse.hasAreaType()) ? DeviceModelConverter.asApiAreaType(gresponse.getAreaType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getCreateAreaTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getCreateAreaTypeMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getAreaType(java.util.UUID)
     */
    @Override
    public IAreaType getAreaType(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetAreaTypeMethod());
	    GGetAreaTypeRequest.Builder grequest = GGetAreaTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetAreaTypeResponse gresponse = getGrpcChannel().getBlockingStub().getAreaType(grequest.build());
	    IAreaType response = (gresponse.hasAreaType()) ? DeviceModelConverter.asApiAreaType(gresponse.getAreaType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetAreaTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetAreaTypeMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getAreaTypeByToken(java.lang.
     * String)
     */
    @Override
    public IAreaType getAreaTypeByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetAreaTypeByTokenMethod());
	    GGetAreaTypeByTokenRequest.Builder grequest = GGetAreaTypeByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetAreaTypeByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getAreaTypeByToken(grequest.build());
	    IAreaType response = (gresponse.hasAreaType()) ? DeviceModelConverter.asApiAreaType(gresponse.getAreaType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetAreaTypeByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetAreaTypeByTokenMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getUpdateAreaTypeMethod());
	    GUpdateAreaTypeRequest.Builder grequest = GUpdateAreaTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcAreaTypeCreateRequest(request));
	    GUpdateAreaTypeResponse gresponse = getGrpcChannel().getBlockingStub().updateAreaType(grequest.build());
	    IAreaType response = (gresponse.hasAreaType()) ? DeviceModelConverter.asApiAreaType(gresponse.getAreaType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getUpdateAreaTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getUpdateAreaTypeMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getListAreaTypesMethod());
	    GListAreaTypesRequest.Builder grequest = GListAreaTypesRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiAreaTypeSearchCriteria(criteria));
	    GListAreaTypesResponse gresponse = getGrpcChannel().getBlockingStub().listAreaTypes(grequest.build());
	    ISearchResults<IAreaType> results = DeviceModelConverter.asApiAreaTypeSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getListAreaTypesMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getListAreaTypesMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteAreaType(java.util.UUID)
     */
    @Override
    public IAreaType deleteAreaType(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getDeleteAreaTypeMethod());
	    GDeleteAreaTypeRequest.Builder grequest = GDeleteAreaTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteAreaTypeResponse gresponse = getGrpcChannel().getBlockingStub().deleteAreaType(grequest.build());
	    IAreaType response = (gresponse.hasAreaType()) ? DeviceModelConverter.asApiAreaType(gresponse.getAreaType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getDeleteAreaTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getDeleteAreaTypeMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getCreateAreaMethod());
	    GCreateAreaRequest.Builder grequest = GCreateAreaRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcAreaCreateRequest(request));
	    GCreateAreaResponse gresponse = getGrpcChannel().getBlockingStub().createArea(grequest.build());
	    IArea response = (gresponse.hasArea()) ? DeviceModelConverter.asApiArea(gresponse.getArea()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getCreateAreaMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getCreateAreaMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getArea(java.util.UUID)
     */
    @Override
    public IArea getArea(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetAreaMethod());
	    GGetAreaRequest.Builder grequest = GGetAreaRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetAreaResponse gresponse = getGrpcChannel().getBlockingStub().getArea(grequest.build());
	    IArea response = (gresponse.hasArea()) ? DeviceModelConverter.asApiArea(gresponse.getArea()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetAreaMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetAreaMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getAreaByToken(java.lang.String)
     */
    @Override
    public IArea getAreaByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetAreaByTokenMethod());
	    GGetAreaByTokenRequest.Builder grequest = GGetAreaByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetAreaByTokenResponse gresponse = getGrpcChannel().getBlockingStub().getAreaByToken(grequest.build());
	    IArea response = (gresponse.hasArea()) ? DeviceModelConverter.asApiArea(gresponse.getArea()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetAreaByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetAreaByTokenMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getAreaChildren(java.lang.String)
     */
    @Override
    public List<IArea> getAreaChildren(String token) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetAreaChildrenMethod());
	    GGetAreaChildrenRequest.Builder grequest = GGetAreaChildrenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetAreaChildrenResponse gresponse = getGrpcChannel().getBlockingStub().getAreaChildren(grequest.build());
	    List<IArea> response = DeviceModelConverter.asApiAreas(gresponse.getAreasList());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetAreaChildrenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetAreaChildrenMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateArea(java.util.UUID,
     * com.sitewhere.spi.area.request.IAreaCreateRequest)
     */
    @Override
    public IArea updateArea(UUID id, IAreaCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getUpdateAreaMethod());
	    GUpdateAreaRequest.Builder grequest = GUpdateAreaRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcAreaCreateRequest(request));
	    GUpdateAreaResponse gresponse = getGrpcChannel().getBlockingStub().updateArea(grequest.build());
	    IArea response = (gresponse.hasArea()) ? DeviceModelConverter.asApiArea(gresponse.getArea()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getUpdateAreaMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getUpdateAreaMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getListAreasMethod());
	    GListAreasRequest.Builder grequest = GListAreasRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asGrpcAreaSearchCriteria(criteria));
	    GListAreasResponse gresponse = getGrpcChannel().getBlockingStub().listAreas(grequest.build());
	    ISearchResults<IArea> results = DeviceModelConverter.asApiAreaSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getListAreasMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getListAreasMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getAreasTree()
     */
    @Override
    public List<ITreeNode> getAreasTree() throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetAreasTreeMethod());
	    GGetAreasTreeRequest.Builder grequest = GGetAreasTreeRequest.newBuilder();
	    GGetAreasTreeResponse gresponse = getGrpcChannel().getBlockingStub().getAreasTree(grequest.build());
	    List<ITreeNode> results = DeviceModelConverter.asApiTreeNodes(gresponse.getAreasList());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetAreasTreeMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetAreasTreeMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteArea(java.util.UUID)
     */
    @Override
    public IArea deleteArea(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getDeleteAreaMethod());
	    GDeleteAreaRequest.Builder grequest = GDeleteAreaRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteAreaResponse gresponse = getGrpcChannel().getBlockingStub().deleteArea(grequest.build());
	    IArea response = (gresponse.hasArea()) ? DeviceModelConverter.asApiArea(gresponse.getArea()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getDeleteAreaMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getDeleteAreaMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createZone(com.sitewhere.spi.area.
     * request.IZoneCreateRequest)
     */
    @Override
    public IZone createZone(IZoneCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getCreateZoneMethod());
	    GCreateZoneRequest.Builder grequest = GCreateZoneRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcZoneCreateRequest(request));
	    GCreateZoneResponse gresponse = getGrpcChannel().getBlockingStub().createZone(grequest.build());
	    IZone response = (gresponse.hasZone()) ? DeviceModelConverter.asApiZone(gresponse.getZone()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getCreateZoneMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getCreateZoneMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getZone(java.util.UUID)
     */
    @Override
    public IZone getZone(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetZoneMethod());
	    GGetZoneRequest.Builder grequest = GGetZoneRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetZoneResponse gresponse = getGrpcChannel().getBlockingStub().getZone(grequest.build());
	    IZone response = (gresponse.hasZone()) ? DeviceModelConverter.asApiZone(gresponse.getZone()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetZoneMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetZoneMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getZoneByToken(java.lang.String)
     */
    @Override
    public IZone getZoneByToken(String zoneToken) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetZoneByTokenMethod());
	    GGetZoneByTokenRequest.Builder grequest = GGetZoneByTokenRequest.newBuilder();
	    grequest.setToken(zoneToken);
	    GGetZoneByTokenResponse gresponse = getGrpcChannel().getBlockingStub().getZoneByToken(grequest.build());
	    IZone response = (gresponse.hasZone()) ? DeviceModelConverter.asApiZone(gresponse.getZone()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetZoneByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetZoneByTokenMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateZone(java.util.UUID,
     * com.sitewhere.spi.area.request.IZoneCreateRequest)
     */
    @Override
    public IZone updateZone(UUID id, IZoneCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getUpdateZoneMethod());
	    GUpdateZoneRequest.Builder grequest = GUpdateZoneRequest.newBuilder();
	    grequest.setZoneId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcZoneCreateRequest(request));
	    GUpdateZoneResponse gresponse = getGrpcChannel().getBlockingStub().updateZone(grequest.build());
	    IZone response = (gresponse.hasZone()) ? DeviceModelConverter.asApiZone(gresponse.getZone()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getUpdateZoneMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getUpdateZoneMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listZones(com.sitewhere.spi.search
     * .device.IZoneSearchCriteria)
     */
    @Override
    public ISearchResults<IZone> listZones(IZoneSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getListZonesMethod());
	    GListZonesRequest.Builder grequest = GListZonesRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asGrpcZoneSearchCriteria(criteria));
	    GListZonesResponse gresponse = getGrpcChannel().getBlockingStub().listZones(grequest.build());
	    ISearchResults<IZone> results = DeviceModelConverter.asApiZoneSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getListZonesMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getListZonesMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteZone(java.util.UUID)
     */
    @Override
    public IZone deleteZone(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getDeleteZoneMethod());
	    GDeleteZoneRequest.Builder grequest = GDeleteZoneRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteZoneResponse gresponse = getGrpcChannel().getBlockingStub().deleteZone(grequest.build());
	    IZone response = (gresponse.hasZone()) ? DeviceModelConverter.asApiZone(gresponse.getZone()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getDeleteZoneMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getDeleteZoneMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceType(com.sitewhere.spi
     * .device.request.IDeviceTypeCreateRequest)
     */
    @Override
    public IDeviceType createDeviceType(IDeviceTypeCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getCreateDeviceTypeMethod());
	    GCreateDeviceTypeRequest.Builder grequest = GCreateDeviceTypeRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceTypeCreateRequest(request));
	    GCreateDeviceTypeResponse gresponse = getGrpcChannel().getBlockingStub().createDeviceType(grequest.build());
	    IDeviceType response = (gresponse.hasDeviceType())
		    ? DeviceModelConverter.asApiDeviceType(gresponse.getDeviceType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getCreateDeviceTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getCreateDeviceTypeMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getDeviceType(java.util.UUID)
     */
    @Override
    public IDeviceType getDeviceType(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetDeviceTypeMethod());
	    GGetDeviceTypeRequest.Builder grequest = GGetDeviceTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetDeviceTypeResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceType(grequest.build());
	    IDeviceType response = (gresponse.hasDeviceType())
		    ? DeviceModelConverter.asApiDeviceType(gresponse.getDeviceType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetDeviceTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetDeviceTypeMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetDeviceTypeByTokenMethod());
	    GGetDeviceTypeByTokenRequest.Builder grequest = GGetDeviceTypeByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetDeviceTypeByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceTypeByToken(grequest.build());
	    IDeviceType response = (gresponse.hasDeviceType())
		    ? DeviceModelConverter.asApiDeviceType(gresponse.getDeviceType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetDeviceTypeByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetDeviceTypeByTokenMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceTypeMethod());
	    GUpdateDeviceTypeRequest.Builder grequest = GUpdateDeviceTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceTypeCreateRequest(request));
	    GUpdateDeviceTypeResponse gresponse = getGrpcChannel().getBlockingStub().updateDeviceType(grequest.build());
	    IDeviceType response = (gresponse.hasDeviceType())
		    ? DeviceModelConverter.asApiDeviceType(gresponse.getDeviceType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getUpdateDeviceTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getUpdateDeviceTypeMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceTypes(com.sitewhere.spi.
     * search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceType> listDeviceTypes(ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getListDeviceTypesMethod());
	    GListDeviceTypesRequest.Builder grequest = GListDeviceTypesRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceTypeSearchCriteria(criteria));
	    GListDeviceTypesResponse gresponse = getGrpcChannel().getBlockingStub().listDeviceTypes(grequest.build());
	    ISearchResults<IDeviceType> results = DeviceModelConverter
		    .asApiDeviceTypeSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getListDeviceTypesMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getListDeviceTypesMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceType(java.util.UUID)
     */
    @Override
    public IDeviceType deleteDeviceType(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceTypeMethod());
	    GDeleteDeviceTypeRequest.Builder grequest = GDeleteDeviceTypeRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteDeviceTypeResponse gresponse = getGrpcChannel().getBlockingStub().deleteDeviceType(grequest.build());
	    IDeviceType response = (gresponse.hasDeviceType())
		    ? DeviceModelConverter.asApiDeviceType(gresponse.getDeviceType())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getDeleteDeviceTypeMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getDeleteDeviceTypeMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceCommand(com.sitewhere.
     * spi.device.request.IDeviceCommandCreateRequest)
     */
    @Override
    public IDeviceCommand createDeviceCommand(IDeviceCommandCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getCreateDeviceCommandMethod());
	    GCreateDeviceCommandRequest.Builder grequest = GCreateDeviceCommandRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceCommandCreateRequest(request));
	    GCreateDeviceCommandResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceCommand(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getCreateDeviceCommandMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getCreateDeviceCommandMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceCommand(java.util.UUID)
     */
    @Override
    public IDeviceCommand getDeviceCommand(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetDeviceCommandMethod());
	    GGetDeviceCommandRequest.Builder grequest = GGetDeviceCommandRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetDeviceCommandResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceCommand(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetDeviceCommandMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetDeviceCommandMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceCommandByToken(java.util.
     * UUID, java.lang.String)
     */
    @Override
    public IDeviceCommand getDeviceCommandByToken(UUID deviceTypeId, String token) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetDeviceCommandByTokenMethod());
	    GGetDeviceCommandByTokenRequest.Builder grequest = GGetDeviceCommandByTokenRequest.newBuilder();
	    grequest.setDeviceTypeId(CommonModelConverter.asGrpcUuid(deviceTypeId));
	    grequest.setToken(token);
	    GGetDeviceCommandByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceCommandByToken(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetDeviceCommandByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetDeviceCommandByTokenMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceCommandMethod());
	    GUpdateDeviceCommandRequest.Builder grequest = GUpdateDeviceCommandRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceCommandCreateRequest(request));
	    GUpdateDeviceCommandResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceCommand(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getUpdateDeviceCommandMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getUpdateDeviceCommandMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceCommands(com.sitewhere.
     * spi.search.device.IDeviceCommandSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceCommand> listDeviceCommands(IDeviceCommandSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getListDeviceCommandsMethod());
	    GListDeviceCommandsRequest.Builder grequest = GListDeviceCommandsRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asGrpcDeviceCommandSearchCriteria(criteria));
	    GListDeviceCommandsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceCommands(grequest.build());
	    ISearchResults<IDeviceCommand> results = DeviceModelConverter
		    .asApiDeviceCommandSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getListDeviceCommandsMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getListDeviceCommandsMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceCommand(java.util.
     * UUID)
     */
    @Override
    public IDeviceCommand deleteDeviceCommand(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceCommandMethod());
	    GDeleteDeviceCommandRequest.Builder grequest = GDeleteDeviceCommandRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteDeviceCommandResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceCommand(grequest.build());
	    IDeviceCommand response = (gresponse.hasCommand())
		    ? DeviceModelConverter.asApiDeviceCommand(gresponse.getCommand())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getDeleteDeviceCommandMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getDeleteDeviceCommandMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceStatus(com.sitewhere.
     * spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus createDeviceStatus(IDeviceStatusCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getCreateDeviceStatusMethod());
	    GCreateDeviceStatusRequest.Builder grequest = GCreateDeviceStatusRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceStatusCreateRequest(request));
	    GCreateDeviceStatusResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceStatus(grequest.build());
	    IDeviceStatus response = (gresponse.hasStatus())
		    ? DeviceModelConverter.asApiDeviceStatus(gresponse.getStatus())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getCreateDeviceStatusMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getCreateDeviceStatusMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStatus(java.util.UUID)
     */
    @Override
    public IDeviceStatus getDeviceStatus(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetDeviceStatusMethod());
	    GGetDeviceStatusRequest.Builder grequest = GGetDeviceStatusRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetDeviceStatusResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceStatus(grequest.build());
	    IDeviceStatus response = (gresponse.hasStatus())
		    ? DeviceModelConverter.asApiDeviceStatus(gresponse.getStatus())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetDeviceStatusMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetDeviceStatusMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceStatusByToken(java.util.
     * UUID, java.lang.String)
     */
    @Override
    public IDeviceStatus getDeviceStatusByToken(UUID deviceTypeId, String token) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetDeviceStatusMethod());
	    GGetDeviceStatusByTokenRequest.Builder grequest = GGetDeviceStatusByTokenRequest.newBuilder();
	    grequest.setDeviceTypeId(CommonModelConverter.asGrpcUuid(deviceTypeId));
	    grequest.setToken(token);
	    GGetDeviceStatusByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceStatusByToken(grequest.build());
	    IDeviceStatus response = (gresponse.hasStatus())
		    ? DeviceModelConverter.asApiDeviceStatus(gresponse.getStatus())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetDeviceStatusMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetDeviceStatusMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceStatus(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceStatusCreateRequest)
     */
    @Override
    public IDeviceStatus updateDeviceStatus(UUID id, IDeviceStatusCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceStatusMethod());
	    GUpdateDeviceStatusRequest.Builder grequest = GUpdateDeviceStatusRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceStatusCreateRequest(request));
	    GUpdateDeviceStatusResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceStatus(grequest.build());
	    IDeviceStatus response = (gresponse.hasStatus())
		    ? DeviceModelConverter.asApiDeviceStatus(gresponse.getStatus())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getUpdateDeviceStatusMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getUpdateDeviceStatusMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceStatuses(com.sitewhere.
     * spi.search.device.IDeviceStatusSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceStatus> listDeviceStatuses(IDeviceStatusSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getListDeviceStatusesMethod());
	    GListDeviceStatusesRequest.Builder grequest = GListDeviceStatusesRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asGrpcDeviceStatusSearchCriteria(criteria));
	    GListDeviceStatusesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceStatuses(grequest.build());
	    ISearchResults<IDeviceStatus> results = DeviceModelConverter
		    .asApiDeviceStatusSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getListDeviceStatusesMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getListDeviceStatusesMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceStatus(java.util.UUID)
     */
    @Override
    public IDeviceStatus deleteDeviceStatus(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceStatusMethod());
	    GDeleteDeviceStatusRequest.Builder grequest = GDeleteDeviceStatusRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteDeviceStatusResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceStatus(grequest.build());
	    IDeviceStatus response = (gresponse.hasStatus())
		    ? DeviceModelConverter.asApiDeviceStatus(gresponse.getStatus())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getDeleteDeviceStatusMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getDeleteDeviceStatusMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getCreateDeviceMethod());
	    GCreateDeviceRequest.Builder grequest = GCreateDeviceRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceCreateRequest(device));
	    GCreateDeviceResponse gresponse = getGrpcChannel().getBlockingStub().createDevice(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getCreateDeviceMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getCreateDeviceMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#getDevice(java.util.UUID)
     */
    @Override
    public IDevice getDevice(UUID deviceId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetDeviceMethod());
	    GGetDeviceRequest.Builder grequest = GGetDeviceRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(deviceId));
	    GGetDeviceResponse gresponse = getGrpcChannel().getBlockingStub().getDevice(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetDeviceMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetDeviceMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceByToken(java.lang.String)
     */
    @Override
    public IDevice getDeviceByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetDeviceByTokenMethod());
	    GGetDeviceByTokenRequest.Builder grequest = GGetDeviceByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetDeviceByTokenResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceByToken(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetDeviceByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetDeviceByTokenMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#updateDevice(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceCreateRequest)
     */
    @Override
    public IDevice updateDevice(UUID id, IDeviceCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceMethod());
	    GUpdateDeviceRequest.Builder grequest = GUpdateDeviceRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceCreateRequest(request));
	    GUpdateDeviceResponse gresponse = getGrpcChannel().getBlockingStub().updateDevice(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getUpdateDeviceMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getUpdateDeviceMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDevices(com.sitewhere.spi.
     * search.device.IDeviceSearchCriteria)
     */
    @Override
    public ISearchResults<IDevice> listDevices(IDeviceSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getListDevicesMethod());
	    GListDevicesRequest.Builder grequest = GListDevicesRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asGrpcDeviceSearchCriteria(criteria));
	    GListDevicesResponse gresponse = getGrpcChannel().getBlockingStub().listDevices(grequest.build());
	    ISearchResults<IDevice> results = DeviceModelConverter.asApiDeviceSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getListDevicesMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getListDevicesMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getCreateDeviceElementMappingMethod());
	    GCreateDeviceElementMappingRequest.Builder grequest = GCreateDeviceElementMappingRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setMapping(DeviceModelConverter.asGrpcDeviceElementMapping(mapping));
	    GCreateDeviceElementMappingResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceElementMapping(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getCreateDeviceElementMappingMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getCreateDeviceElementMappingMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceElementMappingMethod());
	    GDeleteDeviceElementMappingRequest.Builder grequest = GDeleteDeviceElementMappingRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setPath(path);
	    GDeleteDeviceElementMappingResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceElementMapping(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getDeleteDeviceElementMappingMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getDeleteDeviceElementMappingMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.device.IDeviceManagement#deleteDevice(java.util.UUID)
     */
    @Override
    public IDevice deleteDevice(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceMethod());
	    GDeleteDeviceRequest.Builder grequest = GDeleteDeviceRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteDeviceResponse gresponse = getGrpcChannel().getBlockingStub().deleteDevice(grequest.build());
	    IDevice response = (gresponse.hasDevice()) ? DeviceModelConverter.asApiDevice(gresponse.getDevice()) : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getDeleteDeviceMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getDeleteDeviceMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getCreateDeviceAssignmentMethod());
	    GCreateDeviceAssignmentRequest.Builder grequest = GCreateDeviceAssignmentRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceAssignmentCreateRequest(request));
	    GCreateDeviceAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceAssignment(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getCreateDeviceAssignmentMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getCreateDeviceAssignmentMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetDeviceAssignmentMethod());
	    GGetDeviceAssignmentRequest.Builder grequest = GGetDeviceAssignmentRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetDeviceAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceAssignment(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetDeviceAssignmentMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetDeviceAssignmentMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetDeviceAssignmentByTokenMethod());
	    GGetDeviceAssignmentByTokenRequest.Builder grequest = GGetDeviceAssignmentByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetDeviceAssignmentByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceAssignmentByToken(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetDeviceAssignmentByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetDeviceAssignmentByTokenMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getActiveDeviceAssignments(java.
     * util.UUID)
     */
    @Override
    public List<IDeviceAssignment> getActiveDeviceAssignments(UUID deviceId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetActiveAssignmentsForDeviceMethod());
	    GGetActiveAssignmentsForDeviceRequest.Builder grequest = GGetActiveAssignmentsForDeviceRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(deviceId));
	    GGetActiveAssignmentsForDeviceResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getActiveAssignmentsForDevice(grequest.build());
	    List<IDeviceAssignment> response = DeviceModelConverter
		    .asApiDeviceAssignments(gresponse.getAssignmentList());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetActiveAssignmentsForDeviceMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetActiveAssignmentsForDeviceMethod(),
		    t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceAssignment(java.util.
     * UUID)
     */
    @Override
    public IDeviceAssignment deleteDeviceAssignment(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceAssignmentMethod());
	    GDeleteDeviceAssignmentRequest.Builder grequest = GDeleteDeviceAssignmentRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteDeviceAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceAssignment(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getDeleteDeviceAssignmentMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getDeleteDeviceAssignmentMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceAssignmentMethod());
	    GUpdateDeviceAssignmentRequest.Builder grequest = GUpdateDeviceAssignmentRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceAssignmentCreateRequest(request));
	    GUpdateDeviceAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceAssignment(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getUpdateDeviceAssignmentMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getUpdateDeviceAssignmentMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getEndDeviceAssignmentMethod());
	    GEndDeviceAssignmentRequest.Builder grequest = GEndDeviceAssignmentRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GEndDeviceAssignmentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .endDeviceAssignment(grequest.build());
	    IDeviceAssignment response = (gresponse.hasAssignment())
		    ? DeviceModelConverter.asApiDeviceAssignment(gresponse.getAssignment())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getEndDeviceAssignmentMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getEndDeviceAssignmentMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getListDeviceAssignmentsMethod());
	    GListDeviceAssignmentsRequest.Builder grequest = GListDeviceAssignmentsRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asGrpcDeviceAssignmentSearchCriteria(criteria));
	    GListDeviceAssignmentsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceAssignments(grequest.build());
	    ISearchResults<IDeviceAssignment> response = DeviceModelConverter
		    .asApiDeviceAssignmentSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getListDeviceAssignmentsMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getListDeviceAssignmentsMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#createDeviceAlarm(com.sitewhere.
     * spi.device.request.IDeviceAlarmCreateRequest)
     */
    @Override
    public IDeviceAlarm createDeviceAlarm(IDeviceAlarmCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getCreateDeviceAlarmMethod());
	    GCreateDeviceAlarmRequest.Builder grequest = GCreateDeviceAlarmRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceAlarmCreateRequest(request));
	    GCreateDeviceAlarmResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceAlarm(grequest.build());
	    IDeviceAlarm response = (gresponse.hasAlarm()) ? DeviceModelConverter.asApiDeviceAlarm(gresponse.getAlarm())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getCreateDeviceAlarmMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getCreateDeviceAlarmMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#updateDeviceAlarm(java.util.UUID,
     * com.sitewhere.spi.device.request.IDeviceAlarmCreateRequest)
     */
    @Override
    public IDeviceAlarm updateDeviceAlarm(UUID id, IDeviceAlarmCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceAlarmMethod());
	    GUpdateDeviceAlarmRequest.Builder grequest = GUpdateDeviceAlarmRequest.newBuilder();
	    grequest.setAlarmId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceAlarmCreateRequest(request));
	    GUpdateDeviceAlarmResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceAlarm(grequest.build());
	    IDeviceAlarm response = (gresponse.hasAlarm()) ? DeviceModelConverter.asApiDeviceAlarm(gresponse.getAlarm())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getUpdateDeviceAlarmMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getUpdateDeviceAlarmMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceAlarm(java.util.UUID)
     */
    @Override
    public IDeviceAlarm getDeviceAlarm(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetDeviceAlarmMethod());
	    GGetDeviceAlarmRequest.Builder grequest = GGetDeviceAlarmRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetDeviceAlarmResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceAlarm(grequest.build());
	    IDeviceAlarm response = (gresponse.hasAlarm()) ? DeviceModelConverter.asApiDeviceAlarm(gresponse.getAlarm())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetDeviceAlarmMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetDeviceAlarmMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#searchDeviceAlarms(com.sitewhere.
     * spi.search.device.IDeviceAlarmSearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceAlarm> searchDeviceAlarms(IDeviceAlarmSearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getSearchDeviceAlarmsMethod());
	    GSearchDeviceAlarmsRequest.Builder grequest = GSearchDeviceAlarmsRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asGrpcDeviceAlarmSearchCriteria(criteria));
	    GSearchDeviceAlarmsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .searchDeviceAlarms(grequest.build());
	    ISearchResults<IDeviceAlarm> results = DeviceModelConverter
		    .asApiDeviceAlarmSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getSearchDeviceAlarmsMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getSearchDeviceAlarmsMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceAlarm(java.util.UUID)
     */
    @Override
    public IDeviceAlarm deleteDeviceAlarm(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceAlarmMethod());
	    GDeleteDeviceAlarmRequest.Builder grequest = GDeleteDeviceAlarmRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteDeviceAlarmResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceAlarm(grequest.build());
	    IDeviceAlarm response = (gresponse.hasAlarm()) ? DeviceModelConverter.asApiDeviceAlarm(gresponse.getAlarm())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getDeleteDeviceAlarmMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getDeleteDeviceAlarmMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getCreateDeviceGroupMethod());
	    GCreateDeviceGroupRequest.Builder grequest = GCreateDeviceGroupRequest.newBuilder();
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceGroupCreateRequest(request));
	    GCreateDeviceGroupResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createDeviceGroup(grequest.build());
	    IDeviceGroup response = (gresponse.hasGroup()) ? DeviceModelConverter.asApiDeviceGroup(gresponse.getGroup())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getCreateDeviceGroupMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getCreateDeviceGroupMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#getDeviceGroup(java.util.UUID)
     */
    @Override
    public IDeviceGroup getDeviceGroup(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetDeviceGroupMethod());
	    GGetDeviceGroupRequest.Builder grequest = GGetDeviceGroupRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetDeviceGroupResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceGroup(grequest.build());
	    IDeviceGroup response = (gresponse.hasGroup()) ? DeviceModelConverter.asApiDeviceGroup(gresponse.getGroup())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetDeviceGroupMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetDeviceGroupMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getGetDeviceGroupByTokenMethod());
	    GGetDeviceGroupByTokenRequest.Builder grequest = GGetDeviceGroupByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetDeviceGroupByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceGroupByToken(grequest.build());
	    IDeviceGroup response = (gresponse.hasGroup()) ? DeviceModelConverter.asApiDeviceGroup(gresponse.getGroup())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getGetDeviceGroupByTokenMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getGetDeviceGroupByTokenMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceGroupMethod());
	    GUpdateDeviceGroupRequest.Builder grequest = GUpdateDeviceGroupRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    grequest.setRequest(DeviceModelConverter.asGrpcDeviceGroupCreateRequest(request));
	    GUpdateDeviceGroupResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateDeviceGroup(grequest.build());
	    IDeviceGroup response = (gresponse.hasGroup()) ? DeviceModelConverter.asApiDeviceGroup(gresponse.getGroup())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getUpdateDeviceGroupMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getUpdateDeviceGroupMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroups(com.sitewhere.spi
     * .search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroups(ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getListDeviceGroupsMethod());
	    GListDeviceGroupsRequest.Builder grequest = GListDeviceGroupsRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceGroupSearchCriteria(criteria));
	    GListDeviceGroupsResponse gresponse = getGrpcChannel().getBlockingStub().listDeviceGroups(grequest.build());
	    ISearchResults<IDeviceGroup> results = DeviceModelConverter
		    .asApiDeviceGroupSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getListDeviceGroupsMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getListDeviceGroupsMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#listDeviceGroupsWithRole(java.lang
     * .String, com.sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IDeviceGroup> listDeviceGroupsWithRole(String role, ISearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getListDeviceGroupsWithRoleMethod());
	    GListDeviceGroupsWithRoleRequest.Builder grequest = GListDeviceGroupsWithRoleRequest.newBuilder();
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceGroupsWithRoleSearchCriteria(role, criteria));
	    GListDeviceGroupsWithRoleResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceGroupsWithRole(grequest.build());
	    ISearchResults<IDeviceGroup> results = DeviceModelConverter
		    .asApiDeviceGroupSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getListDeviceGroupsWithRoleMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getListDeviceGroupsWithRoleMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.device.IDeviceManagement#deleteDeviceGroup(java.util.UUID)
     */
    @Override
    public IDeviceGroup deleteDeviceGroup(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceGroupMethod());
	    GDeleteDeviceGroupRequest.Builder grequest = GDeleteDeviceGroupRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GDeleteDeviceGroupResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteDeviceGroup(grequest.build());
	    IDeviceGroup response = (gresponse.hasGroup()) ? DeviceModelConverter.asApiDeviceGroup(gresponse.getGroup())
		    : null;
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getDeleteDeviceGroupMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getDeleteDeviceGroupMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getAddDeviceGroupElementsMethod());
	    GAddDeviceGroupElementsRequest.Builder grequest = GAddDeviceGroupElementsRequest.newBuilder();
	    grequest.setGroupId(CommonModelConverter.asGrpcUuid(groupId));
	    grequest.addAllRequests(DeviceModelConverter.asGrpcDeviceGroupElementCreateRequests(elements));
	    grequest.setIgnoreDuplicates(ignoreDuplicates);
	    GAddDeviceGroupElementsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addDeviceGroupElements(grequest.build());
	    List<IDeviceGroupElement> results = DeviceModelConverter
		    .asApiDeviceGroupElements(gresponse.getElementsList());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getAddDeviceGroupElementsMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getAddDeviceGroupElementsMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getRemoveDeviceGroupElementsMethod());
	    GRemoveDeviceGroupElementsRequest.Builder grequest = GRemoveDeviceGroupElementsRequest.newBuilder();
	    grequest.addAllElementIds(CommonModelConverter.asGrpcUuids(elements));
	    GRemoveDeviceGroupElementsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .removeDeviceGroupElements(grequest.build());
	    List<IDeviceGroupElement> results = DeviceModelConverter
		    .asApiDeviceGroupElements(gresponse.getElementsList());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getRemoveDeviceGroupElementsMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getRemoveDeviceGroupElementsMethod(), t);
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
	    GrpcUtils.handleClientMethodEntry(this, DeviceManagementGrpc.getListDeviceGroupElementsMethod());
	    GListDeviceGroupElementsRequest.Builder grequest = GListDeviceGroupElementsRequest.newBuilder();
	    grequest.setGroupId(CommonModelConverter.asGrpcUuid(groupId));
	    grequest.setCriteria(DeviceModelConverter.asApiDeviceGroupElementSearchCriteria(criteria));
	    GListDeviceGroupElementsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listDeviceGroupElements(grequest.build());
	    ISearchResults<IDeviceGroupElement> results = DeviceModelConverter
		    .asApiDeviceGroupElementsSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(DeviceManagementGrpc.getListDeviceGroupElementsMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(DeviceManagementGrpc.getListDeviceGroupElementsMethod(), t);
	}
    }
}