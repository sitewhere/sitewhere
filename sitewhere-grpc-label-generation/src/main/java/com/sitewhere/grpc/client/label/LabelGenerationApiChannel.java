/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.label;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.ApiChannel;
import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.client.ILabelGenerationApiChannel;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.label.LabelGenerationModelConverter;
import com.sitewhere.grpc.service.GGetAreaTypeLabelRequest;
import com.sitewhere.grpc.service.GGetAreaTypeLabelResponse;
import com.sitewhere.grpc.service.LabelGenerationGrpc;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.label.ILabel;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Supports SiteWhere label generation APIs on top of a
 * {@link LabelGenerationGrpcChannel}.
 * 
 * @author Derek
 */
public class LabelGenerationApiChannel extends ApiChannel<LabelGenerationGrpcChannel>
	implements ILabelGenerationApiChannel {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(LabelGenerationApiChannel.class);

    public LabelGenerationApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
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
	return new LabelGenerationGrpcChannel(tracerProvider, host, port);
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGeneration#getAreaTypeLabel(java.lang.String,
     * java.util.UUID)
     */
    @Override
    public ILabel getAreaTypeLabel(String labelGeneratorId, UUID areaTypeId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, LabelGenerationGrpc.METHOD_GET_AREA_TYPE_LABEL);
	    GGetAreaTypeLabelRequest.Builder grequest = GGetAreaTypeLabelRequest.newBuilder();
	    grequest.setGeneratorId(labelGeneratorId);
	    grequest.setAreaTypeId(CommonModelConverter.asGrpcUuid(areaTypeId));
	    GGetAreaTypeLabelResponse gresponse = getGrpcChannel().getBlockingStub().getAreaTypeLabel(grequest.build());
	    ILabel response = (gresponse.hasLabel()) ? LabelGenerationModelConverter.asApiLabel(gresponse.getLabel())
		    : null;
	    GrpcUtils.logClientMethodResponse(LabelGenerationGrpc.METHOD_GET_AREA_TYPE_LABEL, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(LabelGenerationGrpc.METHOD_GET_AREA_TYPE_LABEL, t);
	}
    }

    /*
     * @see com.sitewhere.spi.label.ILabelGeneration#getAreaLabel(java.lang.String,
     * java.util.UUID)
     */
    @Override
    public ILabel getAreaLabel(String labelGeneratorId, UUID areaId) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGeneration#getDeviceTypeLabel(java.lang.String,
     * java.util.UUID)
     */
    @Override
    public ILabel getDeviceTypeLabel(String labelGeneratorId, UUID deviceTypeId) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGeneration#getDeviceLabel(java.lang.String,
     * java.util.UUID)
     */
    @Override
    public ILabel getDeviceLabel(String labelGeneratorId, UUID deviceId) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * @see com.sitewhere.spi.label.ILabelGeneration#getDeviceGroupLabel(java.lang.
     * String, java.util.UUID)
     */
    @Override
    public ILabel getDeviceGroupLabel(String labelGeneratorId, UUID deviceGroupId) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGeneration#getDeviceAssignmentLabel(java.lang.
     * String, java.util.UUID)
     */
    @Override
    public ILabel getDeviceAssignmentLabel(String labelGeneratorId, UUID deviceAssignmentId) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * @see
     * com.sitewhere.spi.label.ILabelGeneration#getAssetTypeLabel(java.lang.String,
     * java.util.UUID)
     */
    @Override
    public ILabel getAssetTypeLabel(String labelGeneratorId, UUID assetTypeId) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * @see com.sitewhere.spi.label.ILabelGeneration#getAssetLabel(java.lang.String,
     * java.util.UUID)
     */
    @Override
    public ILabel getAssetLabel(String labelGeneratorId, UUID assetId) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }
}