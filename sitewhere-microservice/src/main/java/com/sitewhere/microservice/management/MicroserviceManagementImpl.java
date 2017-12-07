/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.management;

import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.MicroserviceModelConverter;
import com.sitewhere.grpc.service.GGetConfigurationModelRequest;
import com.sitewhere.grpc.service.GGetConfigurationModelResponse;
import com.sitewhere.grpc.service.MicroserviceManagementGrpc;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for microservice management GRPC requests.
 * 
 * @author Derek
 */
public class MicroserviceManagementImpl extends MicroserviceManagementGrpc.MicroserviceManagementImplBase {

    /** Microservice */
    private IMicroservice microservice;

    public MicroserviceManagementImpl(IMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.grpc.service.MicroserviceManagementGrpc.
     * MicroserviceManagementImplBase#getConfigurationModel(com.sitewhere.grpc.
     * service.GGetConfigurationModelRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getConfigurationModel(GGetConfigurationModelRequest request,
	    StreamObserver<GGetConfigurationModelResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(MicroserviceManagementGrpc.METHOD_GET_CONFIGURATION_MODEL);
	    IConfigurationModel apiResult = getMicroservice().getConfigurationModel();
	    GGetConfigurationModelResponse.Builder response = GGetConfigurationModelResponse.newBuilder();
	    response.setConfiguration(MicroserviceModelConverter.asGrpcConfigurationModel(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(MicroserviceManagementGrpc.METHOD_GET_CONFIGURATION_MODEL, e);
	    responseObserver.onError(e);
	}
    }

    public IMicroservice getMicroservice() {
	return microservice;
    }

    public void setMicroservice(IMicroservice microservice) {
	this.microservice = microservice;
    }
}