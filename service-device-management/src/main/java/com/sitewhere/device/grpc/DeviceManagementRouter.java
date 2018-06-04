/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.grpc;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice;
import com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcRouter;
import com.sitewhere.grpc.service.*;
import com.sitewhere.microservice.grpc.TenantTokenServerInterceptor;
import com.sitewhere.security.UserContextManager;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 * 
 * @author Derek
 */
public class DeviceManagementRouter extends DeviceManagementGrpc.DeviceManagementImplBase
	implements IGrpcRouter<DeviceManagementGrpc.DeviceManagementImplBase> {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceManagementRouter.class);

    /** Parent microservice */
    private IDeviceManagementMicroservice microservice;

    public DeviceManagementRouter(IDeviceManagementMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.spi.grpc.IGrpcRouter#getTenantImplementation()
     */
    @Override
    public DeviceManagementGrpc.DeviceManagementImplBase getTenantImplementation(StreamObserver<?> observer) {
	String tenantId = TenantTokenServerInterceptor.TENANT_ID_KEY.get();
	if (tenantId == null) {
	    throw new RuntimeException("Tenant id not found in device management request.");
	}
	try {
	    IDeviceManagementTenantEngine engine = getMicroservice()
		    .assureTenantEngineAvailable(UUID.fromString(tenantId));
	    UserContextManager.setCurrentTenant(engine.getTenant(), engine.getLogger());
	    return engine.getDeviceManagementImpl();
	} catch (TenantEngineNotAvailableException e) {
	    observer.onError(GrpcUtils.convertServerException(e));
	    return null;
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createCustomerType(com.sitewhere.grpc.service.GCreateCustomerTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createCustomerType(GCreateCustomerTypeRequest request,
	    StreamObserver<GCreateCustomerTypeResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createCustomerType(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getCustomerType(com.sitewhere.grpc.service.GGetCustomerTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCustomerType(GGetCustomerTypeRequest request,
	    StreamObserver<GGetCustomerTypeResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getCustomerType(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getCustomerTypeByToken(com.sitewhere.grpc.service.
     * GGetCustomerTypeByTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCustomerTypeByToken(GGetCustomerTypeByTokenRequest request,
	    StreamObserver<GGetCustomerTypeByTokenResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getCustomerTypeByToken(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateCustomerType(com.sitewhere.grpc.service.GUpdateCustomerTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateCustomerType(GUpdateCustomerTypeRequest request,
	    StreamObserver<GUpdateCustomerTypeResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateCustomerType(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listCustomerTypes(com.sitewhere.grpc.service.GListCustomerTypesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCustomerTypes(GListCustomerTypesRequest request,
	    StreamObserver<GListCustomerTypesResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listCustomerTypes(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteCustomerType(com.sitewhere.grpc.service.GDeleteCustomerTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteCustomerType(GDeleteCustomerTypeRequest request,
	    StreamObserver<GDeleteCustomerTypeResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteCustomerType(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createCustomer(com.sitewhere.grpc.service.GCreateCustomerRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createCustomer(GCreateCustomerRequest request,
	    StreamObserver<GCreateCustomerResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createCustomer(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getCustomer(com.sitewhere.grpc.service.GGetCustomerRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCustomer(GGetCustomerRequest request, StreamObserver<GGetCustomerResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getCustomer(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getCustomerByToken(com.sitewhere.grpc.service.GGetCustomerByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCustomerByToken(GGetCustomerByTokenRequest request,
	    StreamObserver<GGetCustomerByTokenResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getCustomerByToken(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getCustomerChildren(com.sitewhere.grpc.service.GGetCustomerChildrenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCustomerChildren(GGetCustomerChildrenRequest request,
	    StreamObserver<GGetCustomerChildrenResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getCustomerChildren(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateCustomer(com.sitewhere.grpc.service.GUpdateCustomerRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateCustomer(GUpdateCustomerRequest request,
	    StreamObserver<GUpdateCustomerResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateCustomer(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listCustomers(com.sitewhere.grpc.service.GListCustomersRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCustomers(GListCustomersRequest request, StreamObserver<GListCustomersResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listCustomers(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteCustomer(com.sitewhere.grpc.service.GDeleteCustomerRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteCustomer(GDeleteCustomerRequest request,
	    StreamObserver<GDeleteCustomerResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteCustomer(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createAreaType(com.sitewhere.grpc.service.GCreateAreaTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createAreaType(GCreateAreaTypeRequest request,
	    StreamObserver<GCreateAreaTypeResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createAreaType(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getAreaType(com.sitewhere.grpc.service.GGetAreaTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreaType(GGetAreaTypeRequest request, StreamObserver<GGetAreaTypeResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getAreaType(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getAreaTypeByToken(com.sitewhere.grpc.service.GGetAreaTypeByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreaTypeByToken(GGetAreaTypeByTokenRequest request,
	    StreamObserver<GGetAreaTypeByTokenResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getAreaTypeByToken(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateAreaType(com.sitewhere.grpc.service.GUpdateAreaTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateAreaType(GUpdateAreaTypeRequest request,
	    StreamObserver<GUpdateAreaTypeResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateAreaType(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listAreaTypes(com.sitewhere.grpc.service.GListAreaTypesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAreaTypes(GListAreaTypesRequest request, StreamObserver<GListAreaTypesResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listAreaTypes(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteAreaType(com.sitewhere.grpc.service.GDeleteAreaTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteAreaType(GDeleteAreaTypeRequest request,
	    StreamObserver<GDeleteAreaTypeResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteAreaType(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createArea(com.sitewhere.grpc.service.GCreateAreaRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createArea(GCreateAreaRequest request, StreamObserver<GCreateAreaResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createArea(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getArea(com.sitewhere.grpc.service.GGetAreaRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getArea(GGetAreaRequest request, StreamObserver<GGetAreaResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getArea(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getAreaByToken(com.sitewhere.grpc.service.GGetAreaByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreaByToken(GGetAreaByTokenRequest request,
	    StreamObserver<GGetAreaByTokenResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getAreaByToken(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getAreaChildren(com.sitewhere.grpc.service.GGetAreaChildrenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreaChildren(GGetAreaChildrenRequest request,
	    StreamObserver<GGetAreaChildrenResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getAreaChildren(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateArea(com.sitewhere.grpc.service.GUpdateAreaRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateArea(GUpdateAreaRequest request, StreamObserver<GUpdateAreaResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateArea(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listAreas(com.sitewhere.grpc.service.GListAreasRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAreas(GListAreasRequest request, StreamObserver<GListAreasResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listAreas(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteArea(com.sitewhere.grpc.service.GDeleteAreaRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteArea(GDeleteAreaRequest request, StreamObserver<GDeleteAreaResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteArea(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createZone(com.sitewhere.grpc.service.GCreateZoneRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createZone(GCreateZoneRequest request, StreamObserver<GCreateZoneResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createZone(request, responseObserver);
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
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getZone(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getZoneByToken(com.sitewhere.grpc.service.GGetZoneByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getZoneByToken(GGetZoneByTokenRequest request,
	    StreamObserver<GGetZoneByTokenResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getZoneByToken(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateZone(com.sitewhere.grpc.service.GUpdateZoneRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateZone(GUpdateZoneRequest request, StreamObserver<GUpdateZoneResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateZone(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listZones(com.sitewhere.grpc.service.GListZonesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listZones(GListZonesRequest request, StreamObserver<GListZonesResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listZones(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteZone(com.sitewhere.grpc.service.GDeleteZoneRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteZone(GDeleteZoneRequest request, StreamObserver<GDeleteZoneResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteZone(request, responseObserver);
	}
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
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createDeviceType(request, responseObserver);
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
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceType(request, responseObserver);
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
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceTypeByToken(request, responseObserver);
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
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateDeviceType(request, responseObserver);
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
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listDeviceTypes(request, responseObserver);
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
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteDeviceType(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceCommand(com.sitewhere.grpc.service. GCreateDeviceCommandRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceCommand(GCreateDeviceCommandRequest request,
	    StreamObserver<GCreateDeviceCommandResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createDeviceCommand(request, responseObserver);
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
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceCommand(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceCommandByToken(com.sitewhere.grpc.service.
     * GGetDeviceCommandByTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceCommandByToken(GGetDeviceCommandByTokenRequest request,
	    StreamObserver<GGetDeviceCommandByTokenResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceCommandByToken(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceCommand(com.sitewhere.grpc.service. GUpdateDeviceCommandRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceCommand(GUpdateDeviceCommandRequest request,
	    StreamObserver<GUpdateDeviceCommandResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateDeviceCommand(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceCommands(com.sitewhere.grpc.service.GListDeviceCommandsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceCommands(GListDeviceCommandsRequest request,
	    StreamObserver<GListDeviceCommandsResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listDeviceCommands(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceCommand(com.sitewhere.grpc.service. GDeleteDeviceCommandRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceCommand(GDeleteDeviceCommandRequest request,
	    StreamObserver<GDeleteDeviceCommandResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteDeviceCommand(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceStatus(com.sitewhere.grpc.service.GCreateDeviceStatusRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceStatus(GCreateDeviceStatusRequest request,
	    StreamObserver<GCreateDeviceStatusResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createDeviceStatus(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceStatusByCode(com.sitewhere.grpc.service.
     * GGetDeviceStatusByCodeRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceStatusByCode(GGetDeviceStatusByCodeRequest request,
	    StreamObserver<GGetDeviceStatusByCodeResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceStatusByCode(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceStatus(com.sitewhere.grpc.service.GUpdateDeviceStatusRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceStatus(GUpdateDeviceStatusRequest request,
	    StreamObserver<GUpdateDeviceStatusResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateDeviceStatus(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceStatuses(com.sitewhere.grpc.service.GListDeviceStatusesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceStatuses(GListDeviceStatusesRequest request,
	    StreamObserver<GListDeviceStatusesResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listDeviceStatuses(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceStatus(com.sitewhere.grpc.service.GDeleteDeviceStatusRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceStatus(GDeleteDeviceStatusRequest request,
	    StreamObserver<GDeleteDeviceStatusResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteDeviceStatus(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDevice(com.sitewhere.grpc.service.GCreateDeviceRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDevice(GCreateDeviceRequest request, StreamObserver<GCreateDeviceResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createDevice(request, responseObserver);
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
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDevice(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceByToken(com.sitewhere.grpc.service.GGetDeviceByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceByToken(GGetDeviceByTokenRequest request,
	    StreamObserver<GGetDeviceByTokenResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceByToken(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDevice(com.sitewhere.grpc.service.GUpdateDeviceRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDevice(GUpdateDeviceRequest request, StreamObserver<GUpdateDeviceResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateDevice(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDevices(com.sitewhere.grpc.service.GListDevicesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDevices(GListDevicesRequest request, StreamObserver<GListDevicesResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listDevices(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceElementMapping(com.sitewhere.grpc.service.
     * GCreateDeviceElementMappingRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceElementMapping(GCreateDeviceElementMappingRequest request,
	    StreamObserver<GCreateDeviceElementMappingResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createDeviceElementMapping(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceElementMapping(com.sitewhere.grpc.service.
     * GDeleteDeviceElementMappingRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceElementMapping(GDeleteDeviceElementMappingRequest request,
	    StreamObserver<GDeleteDeviceElementMappingResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteDeviceElementMapping(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDevice(com.sitewhere.grpc.service.GDeleteDeviceRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDevice(GDeleteDeviceRequest request, StreamObserver<GDeleteDeviceResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteDevice(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceGroup(com.sitewhere.grpc.service.GCreateDeviceGroupRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceGroup(GCreateDeviceGroupRequest request,
	    StreamObserver<GCreateDeviceGroupResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createDeviceGroup(request, responseObserver);
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
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceGroup(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceGroupByToken(com.sitewhere.grpc.service.
     * GGetDeviceGroupByTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceGroupByToken(GGetDeviceGroupByTokenRequest request,
	    StreamObserver<GGetDeviceGroupByTokenResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceGroupByToken(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceGroup(com.sitewhere.grpc.service.GUpdateDeviceGroupRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceGroup(GUpdateDeviceGroupRequest request,
	    StreamObserver<GUpdateDeviceGroupResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateDeviceGroup(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceGroups(com.sitewhere.grpc.service.GListDeviceGroupsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceGroups(GListDeviceGroupsRequest request,
	    StreamObserver<GListDeviceGroupsResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listDeviceGroups(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceGroupsWithRole(com.sitewhere.grpc.service.
     * GListDeviceGroupsWithRoleRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceGroupsWithRole(GListDeviceGroupsWithRoleRequest request,
	    StreamObserver<GListDeviceGroupsWithRoleResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listDeviceGroupsWithRole(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceGroup(com.sitewhere.grpc.service.GDeleteDeviceGroupRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceGroup(GDeleteDeviceGroupRequest request,
	    StreamObserver<GDeleteDeviceGroupResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteDeviceGroup(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * addDeviceGroupElements(com.sitewhere.grpc.service.
     * GAddDeviceGroupElementsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addDeviceGroupElements(GAddDeviceGroupElementsRequest request,
	    StreamObserver<GAddDeviceGroupElementsResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.addDeviceGroupElements(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * removeDeviceGroupElements(com.sitewhere.grpc.service.
     * GRemoveDeviceGroupElementsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void removeDeviceGroupElements(GRemoveDeviceGroupElementsRequest request,
	    StreamObserver<GRemoveDeviceGroupElementsResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.removeDeviceGroupElements(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceGroupElements(com.sitewhere.grpc.service.
     * GListDeviceGroupElementsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceGroupElements(GListDeviceGroupElementsRequest request,
	    StreamObserver<GListDeviceGroupElementsResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listDeviceGroupElements(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceAssignment(com.sitewhere.grpc.service.
     * GCreateDeviceAssignmentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceAssignment(GCreateDeviceAssignmentRequest request,
	    StreamObserver<GCreateDeviceAssignmentResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createDeviceAssignment(request, responseObserver);
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
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceAssignment(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceAssignmentByToken(com.sitewhere.grpc.service.
     * GGetDeviceAssignmentByTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceAssignmentByToken(GGetDeviceAssignmentByTokenRequest request,
	    StreamObserver<GGetDeviceAssignmentByTokenResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceAssignmentByToken(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getCurrentAssignmentForDevice(com.sitewhere.grpc.service.
     * GGetCurrentAssignmentForDeviceRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCurrentAssignmentForDevice(GGetCurrentAssignmentForDeviceRequest request,
	    StreamObserver<GGetCurrentAssignmentForDeviceResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getCurrentAssignmentForDevice(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceAssignment(com.sitewhere.grpc.service.
     * GDeleteDeviceAssignmentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceAssignment(GDeleteDeviceAssignmentRequest request,
	    StreamObserver<GDeleteDeviceAssignmentResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteDeviceAssignment(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceAssignment(com.sitewhere.grpc.service.
     * GUpdateDeviceAssignmentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceAssignment(GUpdateDeviceAssignmentRequest request,
	    StreamObserver<GUpdateDeviceAssignmentResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateDeviceAssignment(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * endDeviceAssignment(com.sitewhere.grpc.service. GEndDeviceAssignmentRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void endDeviceAssignment(GEndDeviceAssignmentRequest request,
	    StreamObserver<GEndDeviceAssignmentResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.endDeviceAssignment(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceAssignments(com.sitewhere.grpc.service.
     * GListDeviceAssignmentsRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceAssignments(GListDeviceAssignmentsRequest request,
	    StreamObserver<GListDeviceAssignmentsResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listDeviceAssignments(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceStream(com.sitewhere.grpc.service.GCreateDeviceStreamRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceStream(GCreateDeviceStreamRequest request,
	    StreamObserver<GCreateDeviceStreamResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createDeviceStream(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceStreamByStreamId(com.sitewhere.grpc.service.
     * GGetDeviceStreamByStreamIdRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceStreamByStreamId(GGetDeviceStreamByStreamIdRequest request,
	    StreamObserver<GGetDeviceStreamByStreamIdResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getDeviceStreamByStreamId(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceStreams(com.sitewhere.grpc.service.GListDeviceStreamsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceStreams(GListDeviceStreamsRequest request,
	    StreamObserver<GListDeviceStreamsResponse> responseObserver) {
	DeviceManagementGrpc.DeviceManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listDeviceStreams(request, responseObserver);
	}
    }

    public IDeviceManagementMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IDeviceManagementMicroservice microservice) {
	this.microservice = microservice;
    }
}