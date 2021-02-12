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
package com.sitewhere.labels.grpc;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.grpc.common.CommonModelConverter;
import com.sitewhere.grpc.label.LabelGenerationModelConverter;
import com.sitewhere.grpc.service.GGetAreaLabelRequest;
import com.sitewhere.grpc.service.GGetAreaLabelResponse;
import com.sitewhere.grpc.service.GGetAreaTypeLabelRequest;
import com.sitewhere.grpc.service.GGetAreaTypeLabelResponse;
import com.sitewhere.grpc.service.GGetAssetLabelRequest;
import com.sitewhere.grpc.service.GGetAssetLabelResponse;
import com.sitewhere.grpc.service.GGetAssetTypeLabelRequest;
import com.sitewhere.grpc.service.GGetAssetTypeLabelResponse;
import com.sitewhere.grpc.service.GGetCustomerLabelRequest;
import com.sitewhere.grpc.service.GGetCustomerLabelResponse;
import com.sitewhere.grpc.service.GGetCustomerTypeLabelRequest;
import com.sitewhere.grpc.service.GGetCustomerTypeLabelResponse;
import com.sitewhere.grpc.service.GGetDeviceAssignmentLabelRequest;
import com.sitewhere.grpc.service.GGetDeviceAssignmentLabelResponse;
import com.sitewhere.grpc.service.GGetDeviceGroupLabelRequest;
import com.sitewhere.grpc.service.GGetDeviceGroupLabelResponse;
import com.sitewhere.grpc.service.GGetDeviceLabelRequest;
import com.sitewhere.grpc.service.GGetDeviceLabelResponse;
import com.sitewhere.grpc.service.GGetDeviceTypeLabelRequest;
import com.sitewhere.grpc.service.GGetDeviceTypeLabelResponse;
import com.sitewhere.grpc.service.LabelGenerationGrpc;
import com.sitewhere.labels.manager.DefaultEntityUriProvider;
import com.sitewhere.labels.spi.IEntityUriProvider;
import com.sitewhere.labels.spi.ILabelGenerator;
import com.sitewhere.labels.spi.microservice.ILabelGenerationMicroservice;
import com.sitewhere.labels.spi.microservice.ILabelGenerationTenantEngine;
import com.sitewhere.microservice.api.asset.IAssetManagement;
import com.sitewhere.microservice.api.device.IDeviceManagement;
import com.sitewhere.rest.model.label.Label;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IArea;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.asset.IAsset;
import com.sitewhere.spi.asset.IAssetType;
import com.sitewhere.spi.customer.ICustomer;
import com.sitewhere.spi.customer.ICustomerType;
import com.sitewhere.spi.device.IDevice;
import com.sitewhere.spi.device.IDeviceAssignment;
import com.sitewhere.spi.device.IDeviceType;
import com.sitewhere.spi.device.group.IDeviceGroup;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.microservice.IMicroservice;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for label generation GRPC requests.
 */
public class LabelGenerationImpl extends LabelGenerationGrpc.LabelGenerationImplBase implements IGrpcApiImplementation {

    /** Tenant engine */
    private ILabelGenerationTenantEngine labelGenerationTenantEngine;

    /** Entity URI provider */
    private IEntityUriProvider entityUriProvider = DefaultEntityUriProvider.getInstance();

    public LabelGenerationImpl(ILabelGenerationTenantEngine labelGenerationTenantEngine) {
	this.labelGenerationTenantEngine = labelGenerationTenantEngine;
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getCustomerTypeLabel(com.sitewhere.grpc.service.GGetCustomerTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCustomerTypeLabel(GGetCustomerTypeLabelRequest request,
	    StreamObserver<GGetCustomerTypeLabelResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, LabelGenerationGrpc.getGetCustomerTypeLabelMethod());

	    ILabelGenerator generator = getLabelGeneratorById(request.getGeneratorId());
	    ICustomerType customerType = getDeviceManagement()
		    .getCustomerType(CommonModelConverter.asApiUuid(request.getCustomerTypeId()));
	    byte[] content = generator.getCustomerTypeLabel(customerType, getEntityUriProvider());
	    Label label = new Label();
	    label.setContent(content);

	    GGetCustomerTypeLabelResponse.Builder response = GGetCustomerTypeLabelResponse.newBuilder();
	    response.setLabel(LabelGenerationModelConverter.asGrpcLabel(label));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(LabelGenerationGrpc.getGetCustomerTypeLabelMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(LabelGenerationGrpc.getGetCustomerTypeLabelMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getCustomerLabel(com.sitewhere.grpc.service.GGetCustomerLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCustomerLabel(GGetCustomerLabelRequest request,
	    StreamObserver<GGetCustomerLabelResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, LabelGenerationGrpc.getGetCustomerLabelMethod());

	    ILabelGenerator generator = getLabelGeneratorById(request.getGeneratorId());
	    ICustomer customer = getDeviceManagement()
		    .getCustomer(CommonModelConverter.asApiUuid(request.getCustomerId()));
	    byte[] content = generator.getCustomerLabel(customer, getEntityUriProvider());
	    Label label = new Label();
	    label.setContent(content);

	    GGetCustomerLabelResponse.Builder response = GGetCustomerLabelResponse.newBuilder();
	    response.setLabel(LabelGenerationModelConverter.asGrpcLabel(label));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(LabelGenerationGrpc.getGetCustomerLabelMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(LabelGenerationGrpc.getGetCustomerLabelMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAreaTypeLabel(com.sitewhere.grpc.service.GGetAreaTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreaTypeLabel(GGetAreaTypeLabelRequest request,
	    StreamObserver<GGetAreaTypeLabelResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, LabelGenerationGrpc.getGetAreaTypeLabelMethod());

	    ILabelGenerator generator = getLabelGeneratorById(request.getGeneratorId());
	    IAreaType areaType = getDeviceManagement()
		    .getAreaType(CommonModelConverter.asApiUuid(request.getAreaTypeId()));
	    byte[] content = generator.getAreaTypeLabel(areaType, getEntityUriProvider());
	    Label label = new Label();
	    label.setContent(content);

	    GGetAreaTypeLabelResponse.Builder response = GGetAreaTypeLabelResponse.newBuilder();
	    response.setLabel(LabelGenerationModelConverter.asGrpcLabel(label));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(LabelGenerationGrpc.getGetAreaTypeLabelMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(LabelGenerationGrpc.getGetAreaTypeLabelMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAreaLabel(com.sitewhere.grpc.service.GGetAreaLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreaLabel(GGetAreaLabelRequest request, StreamObserver<GGetAreaLabelResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, LabelGenerationGrpc.getGetAreaLabelMethod());

	    ILabelGenerator generator = getLabelGeneratorById(request.getGeneratorId());
	    IArea area = getDeviceManagement().getArea(CommonModelConverter.asApiUuid(request.getAreaId()));
	    byte[] content = generator.getAreaLabel(area, getEntityUriProvider());
	    Label label = new Label();
	    label.setContent(content);

	    GGetAreaLabelResponse.Builder response = GGetAreaLabelResponse.newBuilder();
	    response.setLabel(LabelGenerationModelConverter.asGrpcLabel(label));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(LabelGenerationGrpc.getGetAreaLabelMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(LabelGenerationGrpc.getGetAreaLabelMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceTypeLabel(com.sitewhere.grpc.service.GGetDeviceTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceTypeLabel(GGetDeviceTypeLabelRequest request,
	    StreamObserver<GGetDeviceTypeLabelResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, LabelGenerationGrpc.getGetDeviceTypeLabelMethod());

	    ILabelGenerator generator = getLabelGeneratorById(request.getGeneratorId());
	    IDeviceType deviceType = getDeviceManagement()
		    .getDeviceType(CommonModelConverter.asApiUuid(request.getDeviceTypeId()));
	    byte[] content = generator.getDeviceTypeLabel(deviceType, getEntityUriProvider());
	    Label label = new Label();
	    label.setContent(content);

	    GGetDeviceTypeLabelResponse.Builder response = GGetDeviceTypeLabelResponse.newBuilder();
	    response.setLabel(LabelGenerationModelConverter.asGrpcLabel(label));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(LabelGenerationGrpc.getGetDeviceTypeLabelMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(LabelGenerationGrpc.getGetDeviceTypeLabelMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceLabel(com.sitewhere.grpc.service.GGetDeviceLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceLabel(GGetDeviceLabelRequest request,
	    StreamObserver<GGetDeviceLabelResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, LabelGenerationGrpc.getGetDeviceLabelMethod());

	    ILabelGenerator generator = getLabelGeneratorById(request.getGeneratorId());
	    IDevice device = getDeviceManagement().getDevice(CommonModelConverter.asApiUuid(request.getDeviceId()));
	    byte[] content = generator.getDeviceLabel(device, getEntityUriProvider());
	    Label label = new Label();
	    label.setContent(content);

	    GGetDeviceLabelResponse.Builder response = GGetDeviceLabelResponse.newBuilder();
	    response.setLabel(LabelGenerationModelConverter.asGrpcLabel(label));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(LabelGenerationGrpc.getGetDeviceLabelMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(LabelGenerationGrpc.getGetDeviceLabelMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceGroupLabel(com.sitewhere.grpc.service.GGetDeviceGroupLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceGroupLabel(GGetDeviceGroupLabelRequest request,
	    StreamObserver<GGetDeviceGroupLabelResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, LabelGenerationGrpc.getGetDeviceGroupLabelMethod());

	    ILabelGenerator generator = getLabelGeneratorById(request.getGeneratorId());
	    IDeviceGroup deviceGroup = getDeviceManagement()
		    .getDeviceGroup(CommonModelConverter.asApiUuid(request.getDeviceGroupId()));
	    byte[] content = generator.getDeviceGroupLabel(deviceGroup, getEntityUriProvider());
	    Label label = new Label();
	    label.setContent(content);

	    GGetDeviceGroupLabelResponse.Builder response = GGetDeviceGroupLabelResponse.newBuilder();
	    response.setLabel(LabelGenerationModelConverter.asGrpcLabel(label));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(LabelGenerationGrpc.getGetDeviceGroupLabelMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(LabelGenerationGrpc.getGetDeviceGroupLabelMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceAssignmentLabel(com.sitewhere.grpc.service.
     * GGetDeviceAssignmentLabelRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceAssignmentLabel(GGetDeviceAssignmentLabelRequest request,
	    StreamObserver<GGetDeviceAssignmentLabelResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, LabelGenerationGrpc.getGetDeviceAssignmentLabelMethod());

	    ILabelGenerator generator = getLabelGeneratorById(request.getGeneratorId());
	    IDeviceAssignment assignment = getDeviceManagement()
		    .getDeviceAssignment(CommonModelConverter.asApiUuid(request.getDeviceAssignmentId()));
	    byte[] content = generator.getDeviceAssignmentLabel(assignment, getEntityUriProvider());
	    Label label = new Label();
	    label.setContent(content);

	    GGetDeviceAssignmentLabelResponse.Builder response = GGetDeviceAssignmentLabelResponse.newBuilder();
	    response.setLabel(LabelGenerationModelConverter.asGrpcLabel(label));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(LabelGenerationGrpc.getGetDeviceAssignmentLabelMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(LabelGenerationGrpc.getGetDeviceAssignmentLabelMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAssetTypeLabel(com.sitewhere.grpc.service.GGetAssetTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetTypeLabel(GGetAssetTypeLabelRequest request,
	    StreamObserver<GGetAssetTypeLabelResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, LabelGenerationGrpc.getGetAssetTypeLabelMethod());

	    ILabelGenerator generator = getLabelGeneratorById(request.getGeneratorId());
	    IAssetType assetType = getAssetManagement()
		    .getAssetType(CommonModelConverter.asApiUuid(request.getAssetTypeId()));
	    byte[] content = generator.getAssetTypeLabel(assetType, getEntityUriProvider());
	    Label label = new Label();
	    label.setContent(content);

	    GGetAssetTypeLabelResponse.Builder response = GGetAssetTypeLabelResponse.newBuilder();
	    response.setLabel(LabelGenerationModelConverter.asGrpcLabel(label));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(LabelGenerationGrpc.getGetAssetTypeLabelMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(LabelGenerationGrpc.getGetAssetTypeLabelMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAssetLabel(com.sitewhere.grpc.service.GGetAssetLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetLabel(GGetAssetLabelRequest request, StreamObserver<GGetAssetLabelResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, LabelGenerationGrpc.getGetAssetLabelMethod());

	    ILabelGenerator generator = getLabelGeneratorById(request.getGeneratorId());
	    IAsset asset = getAssetManagement().getAsset(CommonModelConverter.asApiUuid(request.getAssetId()));
	    byte[] content = generator.getAssetLabel(asset, getEntityUriProvider());
	    Label label = new Label();
	    label.setContent(content);

	    GGetAssetLabelResponse.Builder response = GGetAssetLabelResponse.newBuilder();
	    response.setLabel(LabelGenerationModelConverter.asGrpcLabel(label));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(LabelGenerationGrpc.getGetAssetLabelMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(LabelGenerationGrpc.getGetAssetLabelMethod());
	}
    }

    /**
     * Get device management interface.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IDeviceManagement getDeviceManagement() throws SiteWhereException {
	return ((ILabelGenerationMicroservice) getLabelGenerationTenantEngine().getMicroservice())
		.getDeviceManagement();
    }

    /**
     * Get asset management interface.
     * 
     * @return
     * @throws SiteWhereException
     */
    protected IAssetManagement getAssetManagement() throws SiteWhereException {
	return ((ILabelGenerationMicroservice) getLabelGenerationTenantEngine().getMicroservice()).getAssetManagement();
    }

    /**
     * Get generator by id or throw exception if not found.
     * 
     * @param id
     * @return
     * @throws SiteWhereException
     */
    protected ILabelGenerator getLabelGeneratorById(String id) throws SiteWhereException {
	ILabelGenerator generator = getLabelGenerationTenantEngine().getLabelGeneratorManager().getLabelGenerator(id);
	if (generator == null) {
	    throw new SiteWhereSystemException(ErrorCode.LabelGeneratorNotFound, ErrorLevel.ERROR);
	}
	return generator;
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation#getMicroservice()
     */
    @Override
    public IMicroservice<?, ?> getMicroservice() {
	return getLabelGenerationTenantEngine().getMicroservice();
    }

    protected ILabelGenerationTenantEngine getLabelGenerationTenantEngine() {
	return labelGenerationTenantEngine;
    }

    protected IEntityUriProvider getEntityUriProvider() {
	return entityUriProvider;
    }
}