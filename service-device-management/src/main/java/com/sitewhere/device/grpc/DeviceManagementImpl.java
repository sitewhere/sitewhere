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

import java.util.List;

import com.sitewhere.device.spi.microservice.IDeviceManagementMicroservice;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.device.DeviceModelConverter;
import com.sitewhere.grpc.model.DeviceModel.GAreaSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GAreaTypeSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GCustomerSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GCustomerTypeSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAlarmSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignmentSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceAssignmentSummarySearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceCommandSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupElementsSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceGroupSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceStatusSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceSummarySearchResults;
import com.sitewhere.grpc.model.DeviceModel.GDeviceTypeSearchResults;
import com.sitewhere.grpc.model.DeviceModel.GZoneSearchResults;
import com.sitewhere.grpc.service.*;
import com.sitewhere.microservice.api.device.IDeviceManagement;
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
import com.sitewhere.spi.device.IDeviceAssignmentSummary;
import com.sitewhere.spi.device.IDeviceElementMapping;
import com.sitewhere.spi.device.IDeviceStatus;
import com.sitewhere.spi.device.IDeviceSummary;
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
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.ITreeNode;
import com.sitewhere.spi.search.device.IDeviceCommandSearchCriteria;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for device management GRPC requests.
 */
public class DeviceManagementImpl extends DeviceManagementGrpc.DeviceManagementImplBase
	implements IGrpcApiImplementation {

    /** Parent microservice */
    private IDeviceManagementMicroservice microservice;

    /** Device management persistence */
    private IDeviceManagement deviceManagement;

    public DeviceManagementImpl(IDeviceManagementMicroservice microservice, IDeviceManagement deviceManagement) {
	this.microservice = microservice;
	this.deviceManagement = deviceManagement;
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getCreateCustomerTypeMethod());
	    ICustomerTypeCreateRequest apiRequest = DeviceModelConverter
		    .asApiCustomerTypeCreateRequest(request.getRequest());
	    ICustomerType apiResult = getDeviceManagement().createCustomerType(apiRequest);
	    GCreateCustomerTypeResponse.Builder response = GCreateCustomerTypeResponse.newBuilder();
	    response.setCustomerType(DeviceModelConverter.asGrpcCustomerType(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getCreateCustomerTypeMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getCreateCustomerTypeMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetCustomerTypeMethod());
	    ICustomerType apiResult = getDeviceManagement()
		    .getCustomerType(CommonModelConverter.asApiUuid(request.getId()));
	    GGetCustomerTypeResponse.Builder response = GGetCustomerTypeResponse.newBuilder();
	    if (apiResult != null) {
		response.setCustomerType(DeviceModelConverter.asGrpcCustomerType(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetCustomerTypeMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetCustomerTypeMethod());
	}
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetContainedCustomerTypesMethod());
	    List<? extends ICustomerType> apiResult = getDeviceManagement()
		    .getContainedCustomerTypes(CommonModelConverter.asApiUuid(request.getId()));
	    GGetContainedCustomerTypesResponse.Builder response = GGetContainedCustomerTypesResponse.newBuilder();
	    if (apiResult != null) {
		response.addAllCustomerTypes(DeviceModelConverter.asGrpcCustomerTypes(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetContainedCustomerTypesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetContainedCustomerTypesMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetCustomerTypeByTokenMethod());
	    ICustomerType apiResult = getDeviceManagement().getCustomerTypeByToken(request.getToken());
	    GGetCustomerTypeByTokenResponse.Builder response = GGetCustomerTypeByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setCustomerType(DeviceModelConverter.asGrpcCustomerType(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetCustomerTypeByTokenMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetCustomerTypeByTokenMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getUpdateCustomerTypeMethod());
	    ICustomerTypeCreateRequest update = DeviceModelConverter
		    .asApiCustomerTypeCreateRequest(request.getRequest());
	    ICustomerType apiResult = getDeviceManagement()
		    .updateCustomerType(CommonModelConverter.asApiUuid(request.getId()), update);
	    GUpdateCustomerTypeResponse.Builder response = GUpdateCustomerTypeResponse.newBuilder();
	    if (apiResult != null) {
		response.setCustomerType(DeviceModelConverter.asGrpcCustomerType(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getUpdateCustomerTypeMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getUpdateCustomerTypeMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListCustomerTypesMethod());
	    ISearchResults<? extends ICustomerType> apiResult = getDeviceManagement()
		    .listCustomerTypes(CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GListCustomerTypesResponse.Builder response = GListCustomerTypesResponse.newBuilder();
	    GCustomerTypeSearchResults.Builder results = GCustomerTypeSearchResults.newBuilder();
	    for (ICustomerType api : apiResult.getResults()) {
		results.addCustomerTypes(DeviceModelConverter.asGrpcCustomerType(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListCustomerTypesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListCustomerTypesMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getDeleteCustomerTypeMethod());
	    ICustomerType apiResult = getDeviceManagement()
		    .deleteCustomerType(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteCustomerTypeResponse.Builder response = GDeleteCustomerTypeResponse.newBuilder();
	    response.setCustomerType(DeviceModelConverter.asGrpcCustomerType(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getDeleteCustomerTypeMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getDeleteCustomerTypeMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getCreateCustomerMethod());
	    ICustomerCreateRequest apiRequest = DeviceModelConverter.asApiCustomerCreateRequest(request.getRequest());
	    ICustomer apiResult = getDeviceManagement().createCustomer(apiRequest);
	    GCreateCustomerResponse.Builder response = GCreateCustomerResponse.newBuilder();
	    response.setCustomer(DeviceModelConverter.asGrpcCustomer(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getCreateCustomerMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getCreateCustomerMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetCustomerMethod());
	    ICustomer apiResult = getDeviceManagement().getCustomer(CommonModelConverter.asApiUuid(request.getId()));
	    GGetCustomerResponse.Builder response = GGetCustomerResponse.newBuilder();
	    if (apiResult != null) {
		response.setCustomer(DeviceModelConverter.asGrpcCustomer(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetCustomerMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetCustomerMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetCustomerByTokenMethod());
	    ICustomer apiResult = getDeviceManagement().getCustomerByToken(request.getToken());
	    GGetCustomerByTokenResponse.Builder response = GGetCustomerByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setCustomer(DeviceModelConverter.asGrpcCustomer(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetCustomerByTokenMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetCustomerByTokenMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetCustomerChildrenMethod());
	    List<? extends ICustomer> apiResult = getDeviceManagement().getCustomerChildren(request.getToken());
	    GGetCustomerChildrenResponse.Builder response = GGetCustomerChildrenResponse.newBuilder();
	    if (apiResult != null) {
		response.addAllCustomers(DeviceModelConverter.asGrpcCustomers(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetCustomerChildrenMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetCustomerChildrenMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getUpdateCustomerMethod());
	    ICustomerCreateRequest update = DeviceModelConverter.asApiCustomerCreateRequest(request.getRequest());
	    ICustomer apiResult = getDeviceManagement().updateCustomer(CommonModelConverter.asApiUuid(request.getId()),
		    update);
	    GUpdateCustomerResponse.Builder response = GUpdateCustomerResponse.newBuilder();
	    if (apiResult != null) {
		response.setCustomer(DeviceModelConverter.asGrpcCustomer(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getUpdateCustomerMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getUpdateCustomerMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListCustomersMethod());
	    ISearchResults<? extends ICustomer> apiResult = getDeviceManagement()
		    .listCustomers(DeviceModelConverter.asApiCustomerSearchCriteria(request.getCriteria()));
	    GListCustomersResponse.Builder response = GListCustomersResponse.newBuilder();
	    GCustomerSearchResults.Builder results = GCustomerSearchResults.newBuilder();
	    for (ICustomer api : apiResult.getResults()) {
		results.addCustomers(DeviceModelConverter.asGrpcCustomer(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListCustomersMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListCustomersMethod());
	}
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetCustomersTreeMethod());
	    List<? extends ITreeNode> apiResult = getDeviceManagement().getCustomersTree();
	    GGetCustomersTreeResponse.Builder response = GGetCustomersTreeResponse.newBuilder();
	    for (ITreeNode node : apiResult) {
		response.addCustomers(DeviceModelConverter.asGrpcTreeNode(node));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetCustomersTreeMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetCustomersTreeMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getDeleteCustomerMethod());
	    ICustomer apiResult = getDeviceManagement().deleteCustomer(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteCustomerResponse.Builder response = GDeleteCustomerResponse.newBuilder();
	    response.setCustomer(DeviceModelConverter.asGrpcCustomer(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getDeleteCustomerMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getDeleteCustomerMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getCreateAreaTypeMethod());
	    IAreaTypeCreateRequest apiRequest = DeviceModelConverter.asApiAreaTypeCreateRequest(request.getRequest());
	    IAreaType apiResult = getDeviceManagement().createAreaType(apiRequest);
	    GCreateAreaTypeResponse.Builder response = GCreateAreaTypeResponse.newBuilder();
	    response.setAreaType(DeviceModelConverter.asGrpcAreaType(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getCreateAreaTypeMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getCreateAreaTypeMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetAreaTypeMethod());
	    IAreaType apiResult = getDeviceManagement().getAreaType(CommonModelConverter.asApiUuid(request.getId()));
	    GGetAreaTypeResponse.Builder response = GGetAreaTypeResponse.newBuilder();
	    if (apiResult != null) {
		response.setAreaType(DeviceModelConverter.asGrpcAreaType(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetAreaTypeMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetAreaTypeMethod());
	}
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetContainedAreaTypesMethod());
	    List<? extends IAreaType> apiResult = getDeviceManagement()
		    .getContainedAreaTypes(CommonModelConverter.asApiUuid(request.getId()));
	    GGetContainedAreaTypesResponse.Builder response = GGetContainedAreaTypesResponse.newBuilder();
	    if (apiResult != null) {
		response.addAllAreaTypes(DeviceModelConverter.asGrpcAreaTypes(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetContainedAreaTypesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetContainedAreaTypesMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetAreaTypeByTokenMethod());
	    IAreaType apiResult = getDeviceManagement().getAreaTypeByToken(request.getToken());
	    GGetAreaTypeByTokenResponse.Builder response = GGetAreaTypeByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setAreaType(DeviceModelConverter.asGrpcAreaType(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetAreaTypeByTokenMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetAreaTypeByTokenMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getUpdateAreaTypeMethod());
	    IAreaTypeCreateRequest update = DeviceModelConverter.asApiAreaTypeCreateRequest(request.getRequest());
	    IAreaType apiResult = getDeviceManagement().updateAreaType(CommonModelConverter.asApiUuid(request.getId()),
		    update);
	    GUpdateAreaTypeResponse.Builder response = GUpdateAreaTypeResponse.newBuilder();
	    if (apiResult != null) {
		response.setAreaType(DeviceModelConverter.asGrpcAreaType(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getUpdateAreaTypeMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getUpdateAreaTypeMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListAreaTypesMethod());
	    ISearchResults<? extends IAreaType> apiResult = getDeviceManagement()
		    .listAreaTypes(CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
	    GListAreaTypesResponse.Builder response = GListAreaTypesResponse.newBuilder();
	    GAreaTypeSearchResults.Builder results = GAreaTypeSearchResults.newBuilder();
	    for (IAreaType api : apiResult.getResults()) {
		results.addAreaTypes(DeviceModelConverter.asGrpcAreaType(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListAreaTypesMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListAreaTypesMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getDeleteAreaTypeMethod());
	    IAreaType apiResult = getDeviceManagement().deleteAreaType(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteAreaTypeResponse.Builder response = GDeleteAreaTypeResponse.newBuilder();
	    response.setAreaType(DeviceModelConverter.asGrpcAreaType(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getDeleteAreaTypeMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getDeleteAreaTypeMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getCreateAreaMethod());
	    IAreaCreateRequest apiRequest = DeviceModelConverter.asApiAreaCreateRequest(request.getRequest());
	    IArea apiResult = getDeviceManagement().createArea(apiRequest);
	    GCreateAreaResponse.Builder response = GCreateAreaResponse.newBuilder();
	    response.setArea(DeviceModelConverter.asGrpcArea(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getCreateAreaMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getCreateAreaMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetAreaMethod());
	    IArea apiResult = getDeviceManagement().getArea(CommonModelConverter.asApiUuid(request.getId()));
	    GGetAreaResponse.Builder response = GGetAreaResponse.newBuilder();
	    if (apiResult != null) {
		response.setArea(DeviceModelConverter.asGrpcArea(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetAreaMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetAreaMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetAreaByTokenMethod());
	    IArea apiResult = getDeviceManagement().getAreaByToken(request.getToken());
	    GGetAreaByTokenResponse.Builder response = GGetAreaByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setArea(DeviceModelConverter.asGrpcArea(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetAreaByTokenMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetAreaByTokenMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetAreaChildrenMethod());
	    List<? extends IArea> apiResult = getDeviceManagement().getAreaChildren(request.getToken());
	    GGetAreaChildrenResponse.Builder response = GGetAreaChildrenResponse.newBuilder();
	    if (apiResult != null) {
		response.addAllAreas(DeviceModelConverter.asGrpcAreas(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetAreaChildrenMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetAreaChildrenMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getUpdateAreaMethod());
	    IAreaCreateRequest update = DeviceModelConverter.asApiAreaCreateRequest(request.getRequest());
	    IArea apiResult = getDeviceManagement().updateArea(CommonModelConverter.asApiUuid(request.getId()), update);
	    GUpdateAreaResponse.Builder response = GUpdateAreaResponse.newBuilder();
	    if (apiResult != null) {
		response.setArea(DeviceModelConverter.asGrpcArea(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getUpdateAreaMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getUpdateAreaMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListAreasMethod());
	    ISearchResults<? extends IArea> apiResult = getDeviceManagement()
		    .listAreas(DeviceModelConverter.asApiAreaSearchCriteria(request.getCriteria()));
	    GListAreasResponse.Builder response = GListAreasResponse.newBuilder();
	    GAreaSearchResults.Builder results = GAreaSearchResults.newBuilder();
	    for (IArea api : apiResult.getResults()) {
		results.addAreas(DeviceModelConverter.asGrpcArea(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListAreasMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListAreasMethod());
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.DeviceManagementGrpc.DeviceManagementImplBase#
     * getAreasTree(com.sitewhere.grpc.service.GGetAreasTreeRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreasTree(GGetAreasTreeRequest request, StreamObserver<GGetAreasTreeResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetAreasTreeMethod());
	    List<? extends ITreeNode> apiResult = getDeviceManagement().getAreasTree();
	    GGetAreasTreeResponse.Builder response = GGetAreasTreeResponse.newBuilder();
	    for (ITreeNode node : apiResult) {
		response.addAreas(DeviceModelConverter.asGrpcTreeNode(node));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetAreasTreeMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetAreasTreeMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getDeleteAreaMethod());
	    IArea apiResult = getDeviceManagement().deleteArea(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteAreaResponse.Builder response = GDeleteAreaResponse.newBuilder();
	    response.setArea(DeviceModelConverter.asGrpcArea(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getDeleteAreaMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getDeleteAreaMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getCreateZoneMethod());
	    IZoneCreateRequest apiRequest = DeviceModelConverter.asApiZoneCreateRequest(request.getRequest());
	    IZone apiResult = getDeviceManagement().createZone(apiRequest);
	    GCreateZoneResponse.Builder response = GCreateZoneResponse.newBuilder();
	    response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getCreateZoneMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getCreateZoneMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetZoneMethod());
	    IZone apiResult = getDeviceManagement().getZone(CommonModelConverter.asApiUuid(request.getId()));
	    GGetZoneResponse.Builder response = GGetZoneResponse.newBuilder();
	    if (apiResult != null) {
		response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetZoneMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetZoneMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetZoneByTokenMethod());
	    IZone apiResult = getDeviceManagement().getZoneByToken(request.getToken());
	    GGetZoneByTokenResponse.Builder response = GGetZoneByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetZoneByTokenMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetZoneByTokenMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getUpdateZoneMethod());
	    IZoneCreateRequest update = DeviceModelConverter.asApiZoneCreateRequest(request.getRequest());
	    IZone apiResult = getDeviceManagement().updateZone(CommonModelConverter.asApiUuid(request.getZoneId()),
		    update);
	    GUpdateZoneResponse.Builder response = GUpdateZoneResponse.newBuilder();
	    if (apiResult != null) {
		response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getUpdateZoneMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getUpdateZoneMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListZonesMethod());
	    ISearchResults<? extends IZone> apiResult = getDeviceManagement()
		    .listZones(DeviceModelConverter.asApiZoneSearchCriteria(request.getCriteria()));
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
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListZonesMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListZonesMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getDeleteZoneMethod());
	    IZone apiResult = getDeviceManagement().deleteZone(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteZoneResponse.Builder response = GDeleteZoneResponse.newBuilder();
	    response.setZone(DeviceModelConverter.asGrpcZone(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getDeleteZoneMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getDeleteZoneMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getCreateDeviceTypeMethod());
	    IDeviceTypeCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceTypeCreateRequest(request.getRequest());
	    IDeviceType apiResult = getDeviceManagement().createDeviceType(apiRequest);
	    GCreateDeviceTypeResponse.Builder response = GCreateDeviceTypeResponse.newBuilder();
	    response.setDeviceType(DeviceModelConverter.asGrpcDeviceType(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getCreateDeviceTypeMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getCreateDeviceTypeMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetDeviceTypeMethod());
	    IDeviceType apiResult = getDeviceManagement()
		    .getDeviceType(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceTypeResponse.Builder response = GGetDeviceTypeResponse.newBuilder();
	    if (apiResult != null) {
		response.setDeviceType(DeviceModelConverter.asGrpcDeviceType(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetDeviceTypeMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetDeviceTypeMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetDeviceTypeByTokenMethod());
	    IDeviceType apiResult = getDeviceManagement().getDeviceTypeByToken(request.getToken());
	    GGetDeviceTypeByTokenResponse.Builder response = GGetDeviceTypeByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setDeviceType(DeviceModelConverter.asGrpcDeviceType(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetDeviceTypeByTokenMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetDeviceTypeByTokenMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceTypeMethod());
	    IDeviceTypeCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceTypeCreateRequest(request.getRequest());
	    IDeviceType apiResult = getDeviceManagement()
		    .updateDeviceType(CommonModelConverter.asApiUuid(request.getId()), apiRequest);
	    GUpdateDeviceTypeResponse.Builder response = GUpdateDeviceTypeResponse.newBuilder();
	    response.setDeviceType(DeviceModelConverter.asGrpcDeviceType(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getUpdateDeviceTypeMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getUpdateDeviceTypeMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListDeviceTypesMethod());
	    ISearchResults<? extends IDeviceType> apiResult = getDeviceManagement()
		    .listDeviceTypes(CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
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
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListDeviceTypesMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListDeviceTypesMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceTypeMethod());
	    IDeviceType apiResult = getDeviceManagement()
		    .deleteDeviceType(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteDeviceTypeResponse.Builder response = GDeleteDeviceTypeResponse.newBuilder();
	    response.setDeviceType(DeviceModelConverter.asGrpcDeviceType(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getDeleteDeviceTypeMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getDeleteDeviceTypeMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getCreateDeviceCommandMethod());
	    IDeviceCommandCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceCommandCreateRequest(request.getRequest());
	    IDeviceCommand apiResult = getDeviceManagement().createDeviceCommand(apiRequest);
	    GCreateDeviceCommandResponse.Builder response = GCreateDeviceCommandResponse.newBuilder();
	    response.setCommand(DeviceModelConverter.asGrpcDeviceCommand(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getCreateDeviceCommandMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getCreateDeviceCommandMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetDeviceCommandMethod());
	    IDeviceCommand apiResult = getDeviceManagement()
		    .getDeviceCommand(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceCommandResponse.Builder response = GGetDeviceCommandResponse.newBuilder();
	    if (apiResult != null) {
		response.setCommand(DeviceModelConverter.asGrpcDeviceCommand(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetDeviceCommandMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetDeviceCommandMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetDeviceCommandByTokenMethod());
	    IDeviceCommand apiResult = getDeviceManagement().getDeviceCommandByToken(
		    CommonModelConverter.asApiUuid(request.getDeviceTypeId()), request.getToken());
	    GGetDeviceCommandByTokenResponse.Builder response = GGetDeviceCommandByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setCommand(DeviceModelConverter.asGrpcDeviceCommand(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetDeviceCommandByTokenMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetDeviceCommandByTokenMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceCommandMethod());
	    IDeviceCommandCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceCommandCreateRequest(request.getRequest());
	    IDeviceCommand apiResult = getDeviceManagement()
		    .updateDeviceCommand(CommonModelConverter.asApiUuid(request.getId()), apiRequest);
	    GUpdateDeviceCommandResponse.Builder response = GUpdateDeviceCommandResponse.newBuilder();
	    response.setCommand(DeviceModelConverter.asGrpcDeviceCommand(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getUpdateDeviceCommandMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getUpdateDeviceCommandMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListDeviceCommandsMethod());

	    IDeviceCommandSearchCriteria criteria = DeviceModelConverter
		    .asApiDeviceCommandSearchCriteria(request.getCriteria());
	    ISearchResults<? extends IDeviceCommand> apiResult = getDeviceManagement().listDeviceCommands(criteria);
	    GListDeviceCommandsResponse.Builder response = GListDeviceCommandsResponse.newBuilder();
	    GDeviceCommandSearchResults.Builder results = GDeviceCommandSearchResults.newBuilder();
	    for (IDeviceCommand apiType : apiResult.getResults()) {
		results.addDeviceCommands(DeviceModelConverter.asGrpcDeviceCommand(apiType));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListDeviceCommandsMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListDeviceCommandsMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceCommandMethod());
	    IDeviceCommand apiResult = getDeviceManagement()
		    .deleteDeviceCommand(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteDeviceCommandResponse.Builder response = GDeleteDeviceCommandResponse.newBuilder();
	    response.setCommand(DeviceModelConverter.asGrpcDeviceCommand(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getDeleteDeviceCommandMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getDeleteDeviceCommandMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getCreateDeviceStatusMethod());
	    IDeviceStatus apiResult = getDeviceManagement()
		    .createDeviceStatus(DeviceModelConverter.asApiDeviceStatusCreateRequest(request.getRequest()));
	    GCreateDeviceStatusResponse.Builder response = GCreateDeviceStatusResponse.newBuilder();
	    response.setStatus(DeviceModelConverter.asGrpcDeviceStatus(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getCreateDeviceStatusMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getCreateDeviceStatusMethod());
	}
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetDeviceStatusMethod());
	    IDeviceStatus apiResult = getDeviceManagement()
		    .getDeviceStatus(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceStatusResponse.Builder response = GGetDeviceStatusResponse.newBuilder();
	    if (apiResult != null) {
		response.setStatus(DeviceModelConverter.asGrpcDeviceStatus(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetDeviceStatusMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetDeviceStatusMethod());
	}
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetDeviceStatusByTokenMethod());
	    IDeviceStatus apiResult = getDeviceManagement().getDeviceStatusByToken(
		    CommonModelConverter.asApiUuid(request.getDeviceTypeId()), request.getToken());
	    GGetDeviceStatusByTokenResponse.Builder response = GGetDeviceStatusByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setStatus(DeviceModelConverter.asGrpcDeviceStatus(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetDeviceStatusByTokenMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetDeviceStatusByTokenMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceStatusMethod());
	    IDeviceStatusCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceStatusCreateRequest(request.getRequest());
	    IDeviceStatus apiResult = getDeviceManagement()
		    .updateDeviceStatus(CommonModelConverter.asApiUuid(request.getId()), apiRequest);
	    GUpdateDeviceStatusResponse.Builder response = GUpdateDeviceStatusResponse.newBuilder();
	    response.setStatus(DeviceModelConverter.asGrpcDeviceStatus(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getUpdateDeviceStatusMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getUpdateDeviceStatusMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListDeviceStatusesMethod());
	    ISearchResults<? extends IDeviceStatus> apiResult = getDeviceManagement()
		    .listDeviceStatuses(DeviceModelConverter.asApiDeviceStatusSearchCriteria(request.getCriteria()));
	    GListDeviceStatusesResponse.Builder response = GListDeviceStatusesResponse.newBuilder();
	    GDeviceStatusSearchResults.Builder results = GDeviceStatusSearchResults.newBuilder();
	    for (IDeviceStatus apiType : apiResult.getResults()) {
		results.addDeviceStatuses(DeviceModelConverter.asGrpcDeviceStatus(apiType));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListDeviceStatusesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListDeviceStatusesMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceStatusMethod());
	    IDeviceStatus apiResult = getDeviceManagement()
		    .deleteDeviceStatus(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteDeviceStatusResponse.Builder response = GDeleteDeviceStatusResponse.newBuilder();
	    response.setStatus(DeviceModelConverter.asGrpcDeviceStatus(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getDeleteDeviceStatusMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getDeleteDeviceStatusMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getCreateDeviceMethod());
	    IDeviceCreateRequest apiRequest = DeviceModelConverter.asApiDeviceCreateRequest(request.getRequest());
	    IDevice apiResult = getDeviceManagement().createDevice(apiRequest);
	    GCreateDeviceResponse.Builder response = GCreateDeviceResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getCreateDeviceMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getCreateDeviceMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetDeviceMethod());
	    IDevice apiResult = getDeviceManagement().getDevice(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceResponse.Builder response = GGetDeviceResponse.newBuilder();
	    if (apiResult != null) {
		response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetDeviceMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetDeviceMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetDeviceByTokenMethod());
	    IDevice apiResult = getDeviceManagement().getDeviceByToken(request.getToken());
	    GGetDeviceByTokenResponse.Builder response = GGetDeviceByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetDeviceByTokenMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetDeviceByTokenMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceMethod());
	    IDeviceCreateRequest apiRequest = DeviceModelConverter.asApiDeviceCreateRequest(request.getRequest());
	    IDevice apiResult = getDeviceManagement().updateDevice(CommonModelConverter.asApiUuid(request.getId()),
		    apiRequest);
	    GUpdateDeviceResponse.Builder response = GUpdateDeviceResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getUpdateDeviceMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getUpdateDeviceMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListDevicesMethod());
	    ISearchResults<? extends IDevice> apiResult = getDeviceManagement()
		    .listDevices(DeviceModelConverter.asApiDeviceSearchCriteria(request.getCriteria()));
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
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListDevicesMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListDevicesMethod());
	}
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListDeviceSummariesMethod());
	    ISearchResults<? extends IDeviceSummary> apiResult = getDeviceManagement()
		    .listDeviceSummaries(DeviceModelConverter.asApiDeviceSearchCriteria(request.getCriteria()));
	    GListDeviceSummariesResponse.Builder response = GListDeviceSummariesResponse.newBuilder();
	    GDeviceSummarySearchResults.Builder results = GDeviceSummarySearchResults.newBuilder();
	    for (IDeviceSummary apiSummary : apiResult.getResults()) {
		results.addDeviceSummaries(DeviceModelConverter.asGrpcDeviceSummary(apiSummary));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListDeviceSummariesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListDeviceSummariesMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getCreateDeviceElementMappingMethod());
	    IDeviceElementMapping apiRequest = DeviceModelConverter.asApiDeviceElementMapping(request.getMapping());
	    IDevice apiResult = getDeviceManagement()
		    .createDeviceElementMapping(CommonModelConverter.asApiUuid(request.getId()), apiRequest);
	    GCreateDeviceElementMappingResponse.Builder response = GCreateDeviceElementMappingResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getCreateDeviceElementMappingMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getCreateDeviceElementMappingMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceElementMappingMethod());
	    IDevice apiResult = getDeviceManagement()
		    .deleteDeviceElementMapping(CommonModelConverter.asApiUuid(request.getId()), request.getPath());
	    GDeleteDeviceElementMappingResponse.Builder response = GDeleteDeviceElementMappingResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getDeleteDeviceElementMappingMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getDeleteDeviceElementMappingMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceMethod());
	    IDevice apiResult = getDeviceManagement().deleteDevice(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteDeviceResponse.Builder response = GDeleteDeviceResponse.newBuilder();
	    response.setDevice(DeviceModelConverter.asGrpcDevice(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getDeleteDeviceMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getDeleteDeviceMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getCreateDeviceGroupMethod());
	    IDeviceGroupCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceGroupCreateRequest(request.getRequest());
	    IDeviceGroup apiResult = getDeviceManagement().createDeviceGroup(apiRequest);
	    GCreateDeviceGroupResponse.Builder response = GCreateDeviceGroupResponse.newBuilder();
	    response.setGroup(DeviceModelConverter.asGrpcDeviceGroup(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getCreateDeviceGroupMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getCreateDeviceGroupMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetDeviceGroupMethod());
	    IDeviceGroup apiResult = getDeviceManagement()
		    .getDeviceGroup(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceGroupResponse.Builder response = GGetDeviceGroupResponse.newBuilder();
	    if (apiResult != null) {
		response.setGroup(DeviceModelConverter.asGrpcDeviceGroup(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetDeviceGroupMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetDeviceGroupMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetDeviceGroupByTokenMethod());
	    IDeviceGroup apiResult = getDeviceManagement().getDeviceGroupByToken(request.getToken());
	    GGetDeviceGroupByTokenResponse.Builder response = GGetDeviceGroupByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setGroup(DeviceModelConverter.asGrpcDeviceGroup(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetDeviceGroupByTokenMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetDeviceGroupByTokenMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceGroupMethod());
	    IDeviceGroupCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceGroupCreateRequest(request.getRequest());
	    IDeviceGroup apiResult = getDeviceManagement()
		    .updateDeviceGroup(CommonModelConverter.asApiUuid(request.getId()), apiRequest);
	    GUpdateDeviceGroupResponse.Builder response = GUpdateDeviceGroupResponse.newBuilder();
	    response.setGroup(DeviceModelConverter.asGrpcDeviceGroup(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getUpdateDeviceGroupMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getUpdateDeviceGroupMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListDeviceGroupsMethod());
	    ISearchResults<? extends IDeviceGroup> apiResult = getDeviceManagement()
		    .listDeviceGroups(CommonModelConverter.asApiSearchCriteria(request.getCriteria().getPaging()));
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
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListDeviceGroupsMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListDeviceGroupsMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListDeviceGroupsWithRoleMethod());
	    ISearchResults<? extends IDeviceGroup> apiResult = getDeviceManagement().listDeviceGroupsWithRole(
		    request.getCriteria().getRole(),
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
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListDeviceGroupsWithRoleMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListDeviceGroupsWithRoleMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceGroupMethod());
	    IDeviceGroup apiResult = getDeviceManagement()
		    .deleteDeviceGroup(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteDeviceGroupResponse.Builder response = GDeleteDeviceGroupResponse.newBuilder();
	    response.setGroup(DeviceModelConverter.asGrpcDeviceGroup(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getDeleteDeviceGroupMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getDeleteDeviceGroupMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getAddDeviceGroupElementsMethod());
	    List<IDeviceGroupElementCreateRequest> apiRequest = DeviceModelConverter
		    .asApiDeviceGroupElementCreateRequests(request.getRequestsList());
	    List<? extends IDeviceGroupElement> apiResult = getDeviceManagement().addDeviceGroupElements(
		    CommonModelConverter.asApiUuid(request.getGroupId()), apiRequest, request.getIgnoreDuplicates());
	    GAddDeviceGroupElementsResponse.Builder response = GAddDeviceGroupElementsResponse.newBuilder();
	    for (IDeviceGroupElement apiElement : apiResult) {
		response.addElements(DeviceModelConverter.asGrpcDeviceGroupElement(apiElement));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getAddDeviceGroupElementsMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getAddDeviceGroupElementsMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getRemoveDeviceGroupElementsMethod());
	    List<? extends IDeviceGroupElement> apiResult = getDeviceManagement()
		    .removeDeviceGroupElements(CommonModelConverter.asApiUuids(request.getElementIdsList()));
	    GRemoveDeviceGroupElementsResponse.Builder response = GRemoveDeviceGroupElementsResponse.newBuilder();
	    for (IDeviceGroupElement apiElement : apiResult) {
		response.addElements(DeviceModelConverter.asGrpcDeviceGroupElement(apiElement));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getRemoveDeviceGroupElementsMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getRemoveDeviceGroupElementsMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListDeviceGroupElementsMethod());
	    ISearchResults<? extends IDeviceGroupElement> apiResult = getDeviceManagement().listDeviceGroupElements(
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
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListDeviceGroupElementsMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListDeviceGroupElementsMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getCreateDeviceAssignmentMethod());
	    IDeviceAssignmentCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceAssignmentCreateRequest(request.getRequest());
	    IDeviceAssignment apiResult = getDeviceManagement().createDeviceAssignment(apiRequest);
	    GCreateDeviceAssignmentResponse.Builder response = GCreateDeviceAssignmentResponse.newBuilder();
	    response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getCreateDeviceAssignmentMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getCreateDeviceAssignmentMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetDeviceAssignmentMethod());
	    IDeviceAssignment apiResult = getDeviceManagement()
		    .getDeviceAssignment(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceAssignmentResponse.Builder response = GGetDeviceAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetDeviceAssignmentMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetDeviceAssignmentMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetDeviceAssignmentByTokenMethod());
	    IDeviceAssignment apiResult = getDeviceManagement().getDeviceAssignmentByToken(request.getToken());
	    GGetDeviceAssignmentByTokenResponse.Builder response = GGetDeviceAssignmentByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetDeviceAssignmentByTokenMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetDeviceAssignmentByTokenMethod());
	}
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetActiveAssignmentsForDeviceMethod());
	    List<? extends IDeviceAssignment> apiResult = getDeviceManagement()
		    .getActiveDeviceAssignments(CommonModelConverter.asApiUuid(request.getId()));
	    GGetActiveAssignmentsForDeviceResponse.Builder response = GGetActiveAssignmentsForDeviceResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.addAllAssignment(DeviceModelConverter.asGrpcDeviceAssignments(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetActiveAssignmentsForDeviceMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetActiveAssignmentsForDeviceMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceAssignmentMethod());
	    IDeviceAssignment apiResult = getDeviceManagement()
		    .deleteDeviceAssignment(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteDeviceAssignmentResponse.Builder response = GDeleteDeviceAssignmentResponse.newBuilder();
	    response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getDeleteDeviceAssignmentMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getDeleteDeviceAssignmentMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceAssignmentMethod());
	    IDeviceAssignment apiResult = getDeviceManagement().updateDeviceAssignment(
		    CommonModelConverter.asApiUuid(request.getId()),
		    DeviceModelConverter.asApiDeviceAssignmentCreateRequest(request.getRequest()));
	    GUpdateDeviceAssignmentResponse.Builder response = GUpdateDeviceAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getUpdateDeviceAssignmentMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getUpdateDeviceAssignmentMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getEndDeviceAssignmentMethod());
	    IDeviceAssignment apiResult = getDeviceManagement()
		    .endDeviceAssignment(CommonModelConverter.asApiUuid(request.getId()));
	    GEndDeviceAssignmentResponse.Builder response = GEndDeviceAssignmentResponse.newBuilder();
	    if (apiResult != null) {
		response.setAssignment(DeviceModelConverter.asGrpcDeviceAssignment(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getEndDeviceAssignmentMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getEndDeviceAssignmentMethod());
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListDeviceAssignmentsMethod());
	    ISearchResults<? extends IDeviceAssignment> apiResult = getDeviceManagement().listDeviceAssignments(
		    DeviceModelConverter.asApiDeviceAssignmentSearchCriteria(request.getCriteria()));
	    GListDeviceAssignmentsResponse.Builder response = GListDeviceAssignmentsResponse.newBuilder();
	    GDeviceAssignmentSearchResults.Builder results = GDeviceAssignmentSearchResults.newBuilder();
	    for (IDeviceAssignment api : apiResult.getResults()) {
		results.addAssignments(DeviceModelConverter.asGrpcDeviceAssignment(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListDeviceAssignmentsMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListDeviceAssignmentsMethod());
	}
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getListDeviceAssignmentSummariesMethod());
	    ISearchResults<? extends IDeviceAssignmentSummary> apiResult = getDeviceManagement()
		    .listDeviceAssignmentSummaries(
			    DeviceModelConverter.asApiDeviceAssignmentSearchCriteria(request.getCriteria()));
	    GListDeviceAssignmentSummariesResponse.Builder response = GListDeviceAssignmentSummariesResponse
		    .newBuilder();
	    GDeviceAssignmentSummarySearchResults.Builder results = GDeviceAssignmentSummarySearchResults.newBuilder();
	    for (IDeviceAssignmentSummary api : apiResult.getResults()) {
		results.addDeviceAssignmentSummaries(DeviceModelConverter.asGrpcDeviceAssignmentSummary(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getListDeviceAssignmentSummariesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getListDeviceAssignmentSummariesMethod());
	}
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getCreateDeviceAlarmMethod());
	    IDeviceAlarmCreateRequest apiRequest = DeviceModelConverter
		    .asApiDeviceAlarmCreateRequest(request.getRequest());
	    IDeviceAlarm apiResult = getDeviceManagement().createDeviceAlarm(apiRequest);
	    GCreateDeviceAlarmResponse.Builder response = GCreateDeviceAlarmResponse.newBuilder();
	    response.setAlarm(DeviceModelConverter.asGrpcDeviceAlarm(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getCreateDeviceAlarmMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getCreateDeviceAlarmMethod());
	}
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getGetDeviceAlarmMethod());
	    IDeviceAlarm apiResult = getDeviceManagement()
		    .getDeviceAlarm(CommonModelConverter.asApiUuid(request.getId()));
	    GGetDeviceAlarmResponse.Builder response = GGetDeviceAlarmResponse.newBuilder();
	    if (apiResult != null) {
		response.setAlarm(DeviceModelConverter.asGrpcDeviceAlarm(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getGetDeviceAlarmMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getGetDeviceAlarmMethod());
	}
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getUpdateDeviceAlarmMethod());
	    IDeviceAlarm apiResult = getDeviceManagement().updateDeviceAlarm(
		    CommonModelConverter.asApiUuid(request.getAlarmId()),
		    DeviceModelConverter.asApiDeviceAlarmCreateRequest(request.getRequest()));
	    GUpdateDeviceAlarmResponse.Builder response = GUpdateDeviceAlarmResponse.newBuilder();
	    if (apiResult != null) {
		response.setAlarm(DeviceModelConverter.asGrpcDeviceAlarm(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getUpdateDeviceAlarmMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getUpdateDeviceAlarmMethod());
	}
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getSearchDeviceAlarmsMethod());
	    ISearchResults<? extends IDeviceAlarm> apiResult = getDeviceManagement()
		    .searchDeviceAlarms(DeviceModelConverter.asApiDeviceAlarmSearchCriteria(request.getCriteria()));
	    GSearchDeviceAlarmsResponse.Builder response = GSearchDeviceAlarmsResponse.newBuilder();
	    GDeviceAlarmSearchResults.Builder results = GDeviceAlarmSearchResults.newBuilder();
	    for (IDeviceAlarm api : apiResult.getResults()) {
		results.addAlarms(DeviceModelConverter.asGrpcDeviceAlarm(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getSearchDeviceAlarmsMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getSearchDeviceAlarmsMethod());
	}
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
	try {
	    GrpcUtils.handleServerMethodEntry(this, DeviceManagementGrpc.getDeleteDeviceAlarmMethod());
	    IDeviceAlarm apiResult = getDeviceManagement()
		    .deleteDeviceAlarm(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteDeviceAlarmResponse.Builder response = GDeleteDeviceAlarmResponse.newBuilder();
	    response.setAlarm(DeviceModelConverter.asGrpcDeviceAlarm(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(DeviceManagementGrpc.getDeleteDeviceAlarmMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(DeviceManagementGrpc.getDeleteDeviceAlarmMethod());
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

    protected IDeviceManagement getDeviceManagement() {
	return deviceManagement;
    }
}