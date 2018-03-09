/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.labels.grpc;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.label.LabelGenerationModelConverter;
import com.sitewhere.grpc.service.GGetAreaTypeLabelRequest;
import com.sitewhere.grpc.service.GGetAreaTypeLabelResponse;
import com.sitewhere.grpc.service.LabelGenerationGrpc;
import com.sitewhere.labels.spi.microservice.ILabelGenerationMicroservice;
import com.sitewhere.labels.spi.microservice.ILabelGenerationTenantEngine;
import com.sitewhere.labels.symbology.DefaultEntityUriProvider;
import com.sitewhere.rest.model.label.Label;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.SiteWhereSystemException;
import com.sitewhere.spi.area.IAreaType;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.error.ErrorCode;
import com.sitewhere.spi.error.ErrorLevel;
import com.sitewhere.spi.label.IEntityUriProvider;
import com.sitewhere.spi.label.ILabelGenerator;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for label generation GRPC requests.
 * 
 * @author Derek
 */
public class LabelGenerationImpl extends LabelGenerationGrpc.LabelGenerationImplBase {

    /** Tenant engine */
    private ILabelGenerationTenantEngine labelGenerationTenantEngine;

    /** Entity URI provider */
    private IEntityUriProvider entityUriProvider = DefaultEntityUriProvider.getInstance();

    public LabelGenerationImpl(ILabelGenerationTenantEngine labelGenerationTenantEngine) {
	this.labelGenerationTenantEngine = labelGenerationTenantEngine;
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
	    GrpcUtils.logServerMethodEntry(LabelGenerationGrpc.METHOD_GET_AREA_TYPE_LABEL);

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
	    GrpcUtils.handleServerMethodException(LabelGenerationGrpc.METHOD_GET_AREA_TYPE_LABEL, e, responseObserver);
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
		.getDeviceManagementApiDemux().getApiChannel();
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

    protected ILabelGenerationTenantEngine getLabelGenerationTenantEngine() {
	return labelGenerationTenantEngine;
    }

    protected void setLabelGenerationTenantEngine(ILabelGenerationTenantEngine labelGenerationTenantEngine) {
	this.labelGenerationTenantEngine = labelGenerationTenantEngine;
    }

    public IEntityUriProvider getEntityUriProvider() {
	return entityUriProvider;
    }

    public void setEntityUriProvider(IEntityUriProvider entityUriProvider) {
	this.entityUriProvider = entityUriProvider;
    }
}