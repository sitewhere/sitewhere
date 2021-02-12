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
package com.sitewhere.device.grpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice;
import com.sitewhere.device.spi.microservice.IDeviceManagementTenantEngine;
import com.sitewhere.grpc.service.*;
import com.sitewhere.microservice.grpc.GrpcTenantEngineProvider;
import com.sitewhere.spi.microservice.grpc.ITenantEngineCallback;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 */
public class DeviceManagementRouter extends DeviceManagementGrpc.DeviceManagementImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(DeviceManagementRouter.class);

    /** Parent microservice */
    private IDeviceManagementMicroservice microservice;

    /** Tenant engine provider */
    private GrpcTenantEngineProvider<IDeviceManagementTenantEngine> grpcTenantEngineProvider;

    public DeviceManagementRouter(IDeviceManagementMicroservice microservice) {
	this.microservice = microservice;
	this.grpcTenantEngineProvider = new GrpcTenantEngineProvider<>(microservice);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().createCustomerType(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getCustomerType(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getContainedCustomerTypes(com.sitewhere.grpc.service.
     * GGetContainedCustomerTypesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getContainedCustomerTypes(GGetContainedCustomerTypesRequest request,
	    StreamObserver<GGetContainedCustomerTypesResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getContainedCustomerTypes(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getCustomerTypeByToken(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().updateCustomerType(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listCustomerTypes(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().deleteCustomerType(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().createCustomer(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getCustomer(com.sitewhere.grpc.service.GGetCustomerRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCustomer(GGetCustomerRequest request, StreamObserver<GGetCustomerResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getCustomer(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getCustomerByToken(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getCustomerChildren(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().updateCustomer(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listCustomers(com.sitewhere.grpc.service.GListCustomersRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listCustomers(GListCustomersRequest request, StreamObserver<GListCustomersResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listCustomers(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getCustomersTree(com.sitewhere.grpc.service.GGetCustomersTreeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCustomersTree(GGetCustomersTreeRequest request,
	    StreamObserver<GGetCustomersTreeResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getCustomersTree(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().deleteCustomer(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().createAreaType(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getAreaType(com.sitewhere.grpc.service.GGetAreaTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreaType(GGetAreaTypeRequest request, StreamObserver<GGetAreaTypeResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getAreaType(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getContainedAreaTypes(com.sitewhere.grpc.service.
     * GGetContainedAreaTypesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getContainedAreaTypes(GGetContainedAreaTypesRequest request,
	    StreamObserver<GGetContainedAreaTypesResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getContainedAreaTypes(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getAreaTypeByToken(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().updateAreaType(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listAreaTypes(com.sitewhere.grpc.service.GListAreaTypesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAreaTypes(GListAreaTypesRequest request, StreamObserver<GListAreaTypesResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listAreaTypes(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().deleteAreaType(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createArea(com.sitewhere.grpc.service.GCreateAreaRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createArea(GCreateAreaRequest request, StreamObserver<GCreateAreaResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().createArea(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getArea(com.sitewhere.grpc.service.GGetAreaRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getArea(GGetAreaRequest request, StreamObserver<GGetAreaResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getArea(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getAreaByToken(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getAreaChildren(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateArea(com.sitewhere.grpc.service.GUpdateAreaRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateArea(GUpdateAreaRequest request, StreamObserver<GUpdateAreaResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().updateArea(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listAreas(com.sitewhere.grpc.service.GListAreasRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listAreas(GListAreasRequest request, StreamObserver<GListAreasResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listAreas(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getAreasTree(com.sitewhere.grpc.service.GGetAreasTreeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreasTree(GGetAreasTreeRequest request, StreamObserver<GGetAreasTreeResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getAreasTree(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteArea(com.sitewhere.grpc.service.GDeleteAreaRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteArea(GDeleteAreaRequest request, StreamObserver<GDeleteAreaResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().deleteArea(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createZone(com.sitewhere.grpc.service.GCreateZoneRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createZone(GCreateZoneRequest request, StreamObserver<GCreateZoneResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().createZone(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getZone(com.sitewhere.grpc.service.GGetZoneRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getZone(GGetZoneRequest request, StreamObserver<GGetZoneResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getZone(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getZoneByToken(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateZone(com.sitewhere.grpc.service.GUpdateZoneRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateZone(GUpdateZoneRequest request, StreamObserver<GUpdateZoneResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().updateZone(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listZones(com.sitewhere.grpc.service.GListZonesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listZones(GListZonesRequest request, StreamObserver<GListZonesResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listZones(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteZone(com.sitewhere.grpc.service.GDeleteZoneRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteZone(GDeleteZoneRequest request, StreamObserver<GDeleteZoneResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().deleteZone(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().createDeviceType(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceType(com.sitewhere.grpc.service.GGetDeviceTypeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceType(GGetDeviceTypeRequest request, StreamObserver<GGetDeviceTypeResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getDeviceType(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getDeviceTypeByToken(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().updateDeviceType(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listDeviceTypes(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().deleteDeviceType(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().createDeviceCommand(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getDeviceCommand(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getDeviceCommandByToken(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().updateDeviceCommand(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listDeviceCommands(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().deleteDeviceCommand(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().createDeviceStatus(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceStatus(com.sitewhere.grpc.service.GGetDeviceStatusRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceStatus(GGetDeviceStatusRequest request,
	    StreamObserver<GGetDeviceStatusResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getDeviceStatus(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceStatusByToken(com.sitewhere.grpc.service.
     * GGetDeviceStatusByTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceStatusByToken(GGetDeviceStatusByTokenRequest request,
	    StreamObserver<GGetDeviceStatusByTokenResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getDeviceStatusByToken(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().updateDeviceStatus(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listDeviceStatuses(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().deleteDeviceStatus(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDevice(com.sitewhere.grpc.service.GCreateDeviceRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDevice(GCreateDeviceRequest request, StreamObserver<GCreateDeviceResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().createDevice(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDevice(com.sitewhere.grpc.service.GGetDeviceRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDevice(GGetDeviceRequest request, StreamObserver<GGetDeviceResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getDevice(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getDeviceByToken(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDevice(com.sitewhere.grpc.service.GUpdateDeviceRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDevice(GUpdateDeviceRequest request, StreamObserver<GUpdateDeviceResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().updateDevice(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDevices(com.sitewhere.grpc.service.GListDevicesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDevices(GListDevicesRequest request, StreamObserver<GListDevicesResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listDevices(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceSummaries(com.sitewhere.grpc.service.GListDeviceSummariesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceSummaries(GListDeviceSummariesRequest request,
	    StreamObserver<GListDeviceSummariesResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listDeviceSummaries(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().createDeviceElementMapping(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().deleteDeviceElementMapping(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDevice(com.sitewhere.grpc.service.GDeleteDeviceRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDevice(GDeleteDeviceRequest request, StreamObserver<GDeleteDeviceResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().deleteDevice(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().createDeviceGroup(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getDeviceGroup(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getDeviceGroupByToken(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().updateDeviceGroup(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listDeviceGroups(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listDeviceGroupsWithRole(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().deleteDeviceGroup(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().addDeviceGroupElements(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().removeDeviceGroupElements(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listDeviceGroupElements(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().createDeviceAssignment(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getDeviceAssignment(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getDeviceAssignmentByToken(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getActiveAssignmentsForDevice(com.sitewhere.grpc.service.
     * GGetActiveAssignmentsForDeviceRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getActiveAssignmentsForDevice(GGetActiveAssignmentsForDeviceRequest request,
	    StreamObserver<GGetActiveAssignmentsForDeviceResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getActiveAssignmentsForDevice(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().deleteDeviceAssignment(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().updateDeviceAssignment(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().endDeviceAssignment(request, responseObserver);
	    }
	}, responseObserver);
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
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listDeviceAssignments(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * listDeviceAssignmentSummaries(com.sitewhere.grpc.service.
     * GListDeviceAssignmentSummariesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listDeviceAssignmentSummaries(GListDeviceAssignmentSummariesRequest request,
	    StreamObserver<GListDeviceAssignmentSummariesResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().listDeviceAssignmentSummaries(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * createDeviceAlarm(com.sitewhere.grpc.service.GCreateDeviceAlarmRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createDeviceAlarm(GCreateDeviceAlarmRequest request,
	    StreamObserver<GCreateDeviceAlarmResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().createDeviceAlarm(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getDeviceAlarm(com.sitewhere.grpc.service.GGetDeviceAlarmRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceAlarm(GGetDeviceAlarmRequest request,
	    StreamObserver<GGetDeviceAlarmResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().getDeviceAlarm(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * updateDeviceAlarm(com.sitewhere.grpc.service.GUpdateDeviceAlarmRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateDeviceAlarm(GUpdateDeviceAlarmRequest request,
	    StreamObserver<GUpdateDeviceAlarmResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().updateDeviceAlarm(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * searchDeviceAlarms(com.sitewhere.grpc.service.GSearchDeviceAlarmsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void searchDeviceAlarms(GSearchDeviceAlarmsRequest request,
	    StreamObserver<GSearchDeviceAlarmsResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().searchDeviceAlarms(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * deleteDeviceAlarm(com.sitewhere.grpc.service.GDeleteDeviceAlarmRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteDeviceAlarm(GDeleteDeviceAlarmRequest request,
	    StreamObserver<GDeleteDeviceAlarmResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<IDeviceManagementTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(IDeviceManagementTenantEngine tenantEngine) {
		tenantEngine.getDeviceManagementImpl().deleteDeviceAlarm(request, responseObserver);
	    }
	}, responseObserver);
    }

    protected IDeviceManagementMicroservice getMicroservice() {
	return microservice;
    }

    protected GrpcTenantEngineProvider<IDeviceManagementTenantEngine> getGrpcTenantEngineProvider() {
	return grpcTenantEngineProvider;
    }
}