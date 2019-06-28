/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.label;

import java.util.UUID;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.MultitenantApiChannel;
import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.client.spi.client.ILabelGenerationApiChannel;
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
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.grpc.GrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * Supports SiteWhere label generation APIs on top of a
 * {@link LabelGenerationGrpcChannel}.
 * 
 * @author Derek
 */
public class LabelGenerationApiChannel extends MultitenantApiChannel<LabelGenerationGrpcChannel>
	implements ILabelGenerationApiChannel<LabelGenerationGrpcChannel> {

    public LabelGenerationApiChannel(IInstanceSettings settings) {
	super(settings, MicroserviceIdentifier.LabelGeneration, GrpcServiceIdentifier.LabelGeneration,
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
    public LabelGenerationGrpcChannel createGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	return new LabelGenerationGrpcChannel(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * @see com.sitewhere.spi.label.ILabelGeneration#getCustomerTypeLabel(java.lang.
     * String, java.util.UUID)
     */
    @Override
    public ILabel getCustomerTypeLabel(String labelGeneratorId, UUID customerTypeId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, LabelGenerationGrpc.getGetCustomerTypeLabelMethod());
	    GGetCustomerTypeLabelRequest.Builder grequest = GGetCustomerTypeLabelRequest.newBuilder();
	    grequest.setGeneratorId(labelGeneratorId);
	    grequest.setCustomerTypeId(CommonModelConverter.asGrpcUuid(customerTypeId));
	    GGetCustomerTypeLabelResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getCustomerTypeLabel(grequest.build());
	    ILabel response = (gresponse.hasLabel()) ? LabelGenerationModelConverter.asApiLabel(gresponse.getLabel())
		    : null;
	    GrpcUtils.logClientMethodResponse(LabelGenerationGrpc.getGetCustomerTypeLabelMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(LabelGenerationGrpc.getGetCustomerTypeLabelMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGeneration#getCustomerLabel(java.lang.String,
     * java.util.UUID)
     */
    @Override
    public ILabel getCustomerLabel(String labelGeneratorId, UUID customerId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, LabelGenerationGrpc.getGetCustomerLabelMethod());
	    GGetCustomerLabelRequest.Builder grequest = GGetCustomerLabelRequest.newBuilder();
	    grequest.setGeneratorId(labelGeneratorId);
	    grequest.setCustomerId(CommonModelConverter.asGrpcUuid(customerId));
	    GGetCustomerLabelResponse gresponse = getGrpcChannel().getBlockingStub().getCustomerLabel(grequest.build());
	    ILabel response = (gresponse.hasLabel()) ? LabelGenerationModelConverter.asApiLabel(gresponse.getLabel())
		    : null;
	    GrpcUtils.logClientMethodResponse(LabelGenerationGrpc.getGetCustomerLabelMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(LabelGenerationGrpc.getGetCustomerLabelMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGeneration#getAreaTypeLabel(java.lang.String,
     * java.util.UUID)
     */
    @Override
    public ILabel getAreaTypeLabel(String labelGeneratorId, UUID areaTypeId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, LabelGenerationGrpc.getGetAreaTypeLabelMethod());
	    GGetAreaTypeLabelRequest.Builder grequest = GGetAreaTypeLabelRequest.newBuilder();
	    grequest.setGeneratorId(labelGeneratorId);
	    grequest.setAreaTypeId(CommonModelConverter.asGrpcUuid(areaTypeId));
	    GGetAreaTypeLabelResponse gresponse = getGrpcChannel().getBlockingStub().getAreaTypeLabel(grequest.build());
	    ILabel response = (gresponse.hasLabel()) ? LabelGenerationModelConverter.asApiLabel(gresponse.getLabel())
		    : null;
	    GrpcUtils.logClientMethodResponse(LabelGenerationGrpc.getGetAreaTypeLabelMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(LabelGenerationGrpc.getGetAreaTypeLabelMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.label.ILabelGeneration#getAreaLabel(java.lang.String,
     * java.util.UUID)
     */
    @Override
    public ILabel getAreaLabel(String labelGeneratorId, UUID areaId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, LabelGenerationGrpc.getGetAreaLabelMethod());
	    GGetAreaLabelRequest.Builder grequest = GGetAreaLabelRequest.newBuilder();
	    grequest.setGeneratorId(labelGeneratorId);
	    grequest.setAreaId(CommonModelConverter.asGrpcUuid(areaId));
	    GGetAreaLabelResponse gresponse = getGrpcChannel().getBlockingStub().getAreaLabel(grequest.build());
	    ILabel response = (gresponse.hasLabel()) ? LabelGenerationModelConverter.asApiLabel(gresponse.getLabel())
		    : null;
	    GrpcUtils.logClientMethodResponse(LabelGenerationGrpc.getGetAreaLabelMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(LabelGenerationGrpc.getGetAreaLabelMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGeneration#getDeviceTypeLabel(java.lang.String,
     * java.util.UUID)
     */
    @Override
    public ILabel getDeviceTypeLabel(String labelGeneratorId, UUID deviceTypeId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, LabelGenerationGrpc.getGetDeviceTypeLabelMethod());
	    GGetDeviceTypeLabelRequest.Builder grequest = GGetDeviceTypeLabelRequest.newBuilder();
	    grequest.setGeneratorId(labelGeneratorId);
	    grequest.setDeviceTypeId(CommonModelConverter.asGrpcUuid(deviceTypeId));
	    GGetDeviceTypeLabelResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceTypeLabel(grequest.build());
	    ILabel response = (gresponse.hasLabel()) ? LabelGenerationModelConverter.asApiLabel(gresponse.getLabel())
		    : null;
	    GrpcUtils.logClientMethodResponse(LabelGenerationGrpc.getGetDeviceTypeLabelMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(LabelGenerationGrpc.getGetDeviceTypeLabelMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGeneration#getDeviceLabel(java.lang.String,
     * java.util.UUID)
     */
    @Override
    public ILabel getDeviceLabel(String labelGeneratorId, UUID deviceId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, LabelGenerationGrpc.getGetDeviceLabelMethod());
	    GGetDeviceLabelRequest.Builder grequest = GGetDeviceLabelRequest.newBuilder();
	    grequest.setGeneratorId(labelGeneratorId);
	    grequest.setDeviceId(CommonModelConverter.asGrpcUuid(deviceId));
	    GGetDeviceLabelResponse gresponse = getGrpcChannel().getBlockingStub().getDeviceLabel(grequest.build());
	    ILabel response = (gresponse.hasLabel()) ? LabelGenerationModelConverter.asApiLabel(gresponse.getLabel())
		    : null;
	    GrpcUtils.logClientMethodResponse(LabelGenerationGrpc.getGetDeviceLabelMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(LabelGenerationGrpc.getGetDeviceLabelMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.label.ILabelGeneration#getDeviceGroupLabel(java.lang.
     * String, java.util.UUID)
     */
    @Override
    public ILabel getDeviceGroupLabel(String labelGeneratorId, UUID deviceGroupId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, LabelGenerationGrpc.getGetDeviceGroupLabelMethod());
	    GGetDeviceGroupLabelRequest.Builder grequest = GGetDeviceGroupLabelRequest.newBuilder();
	    grequest.setGeneratorId(labelGeneratorId);
	    grequest.setDeviceGroupId(CommonModelConverter.asGrpcUuid(deviceGroupId));
	    GGetDeviceGroupLabelResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceGroupLabel(grequest.build());
	    ILabel response = (gresponse.hasLabel()) ? LabelGenerationModelConverter.asApiLabel(gresponse.getLabel())
		    : null;
	    GrpcUtils.logClientMethodResponse(LabelGenerationGrpc.getGetDeviceGroupLabelMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(LabelGenerationGrpc.getGetDeviceGroupLabelMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGeneration#getDeviceAssignmentLabel(java.lang.
     * String, java.util.UUID)
     */
    @Override
    public ILabel getDeviceAssignmentLabel(String labelGeneratorId, UUID deviceAssignmentId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, LabelGenerationGrpc.getGetDeviceAssignmentLabelMethod());
	    GGetDeviceAssignmentLabelRequest.Builder grequest = GGetDeviceAssignmentLabelRequest.newBuilder();
	    grequest.setGeneratorId(labelGeneratorId);
	    grequest.setDeviceAssignmentId(CommonModelConverter.asGrpcUuid(deviceAssignmentId));
	    GGetDeviceAssignmentLabelResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getDeviceAssignmentLabel(grequest.build());
	    ILabel response = (gresponse.hasLabel()) ? LabelGenerationModelConverter.asApiLabel(gresponse.getLabel())
		    : null;
	    GrpcUtils.logClientMethodResponse(LabelGenerationGrpc.getGetDeviceAssignmentLabelMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(LabelGenerationGrpc.getGetDeviceAssignmentLabelMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGeneration#getAssetTypeLabel(java.lang.String,
     * java.util.UUID)
     */
    @Override
    public ILabel getAssetTypeLabel(String labelGeneratorId, UUID assetTypeId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, LabelGenerationGrpc.getGetAssetLabelMethod());
	    GGetAssetTypeLabelRequest.Builder grequest = GGetAssetTypeLabelRequest.newBuilder();
	    grequest.setGeneratorId(labelGeneratorId);
	    grequest.setAssetTypeId(CommonModelConverter.asGrpcUuid(assetTypeId));
	    GGetAssetTypeLabelResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getAssetTypeLabel(grequest.build());
	    ILabel response = (gresponse.hasLabel()) ? LabelGenerationModelConverter.asApiLabel(gresponse.getLabel())
		    : null;
	    GrpcUtils.logClientMethodResponse(LabelGenerationGrpc.getGetAssetLabelMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(LabelGenerationGrpc.getGetAssetLabelMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.label.ILabelGeneration#getAssetLabel(java.lang.String,
     * java.util.UUID)
     */
    @Override
    public ILabel getAssetLabel(String labelGeneratorId, UUID assetId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, LabelGenerationGrpc.getGetAssetLabelMethod());
	    GGetAssetLabelRequest.Builder grequest = GGetAssetLabelRequest.newBuilder();
	    grequest.setGeneratorId(labelGeneratorId);
	    grequest.setAssetId(CommonModelConverter.asGrpcUuid(assetId));
	    GGetAssetLabelResponse gresponse = getGrpcChannel().getBlockingStub().getAssetLabel(grequest.build());
	    ILabel response = (gresponse.hasLabel()) ? LabelGenerationModelConverter.asApiLabel(gresponse.getLabel())
		    : null;
	    GrpcUtils.logClientMethodResponse(LabelGenerationGrpc.getGetAssetLabelMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(LabelGenerationGrpc.getGetAssetLabelMethod(), t);
	}
    }
}