/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.management;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.ByteString;
import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.MicroserviceModel.GConfigurationContent;
import com.sitewhere.grpc.model.converter.MicroserviceModelConverter;
import com.sitewhere.grpc.service.GGetConfigurationModelRequest;
import com.sitewhere.grpc.service.GGetConfigurationModelResponse;
import com.sitewhere.grpc.service.GGetGlobalConfigurationRequest;
import com.sitewhere.grpc.service.GGetGlobalConfigurationResponse;
import com.sitewhere.grpc.service.GGetTenantConfigurationRequest;
import com.sitewhere.grpc.service.GGetTenantConfigurationResponse;
import com.sitewhere.grpc.service.GUpdateGlobalConfigurationRequest;
import com.sitewhere.grpc.service.GUpdateGlobalConfigurationResponse;
import com.sitewhere.grpc.service.GUpdateTenantConfigurationRequest;
import com.sitewhere.grpc.service.GUpdateTenantConfigurationResponse;
import com.sitewhere.grpc.service.MicroserviceManagementGrpc;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IGlobalMicroservice;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for microservice management GRPC requests.
 * 
 * @author Derek
 */
public class MicroserviceManagementImpl extends MicroserviceManagementGrpc.MicroserviceManagementImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

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
	    response.setModel(MicroserviceModelConverter.asGrpcConfigurationModel(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(MicroserviceManagementGrpc.METHOD_GET_CONFIGURATION_MODEL, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.MicroserviceManagementGrpc.
     * MicroserviceManagementImplBase#getGlobalConfiguration(com.sitewhere.grpc.
     * service.GGetGlobalConfigurationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getGlobalConfiguration(GGetGlobalConfigurationRequest request,
	    StreamObserver<GGetGlobalConfigurationResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(MicroserviceManagementGrpc.METHOD_GET_GLOBAL_CONFIGURATION);
	    GGetGlobalConfigurationResponse.Builder response = GGetGlobalConfigurationResponse.newBuilder();
	    GConfigurationContent.Builder configuration = GConfigurationContent.newBuilder();

	    if (getMicroservice() instanceof IGlobalMicroservice) {
		byte[] content = ((IGlobalMicroservice) getMicroservice()).getConfiguration();
		configuration.setContent(ByteString.copyFrom(content));
	    } else {
		throw new SiteWhereException("Requesting global configuration from a tenant microservice.");
	    }

	    response.setConfiguration(configuration.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(MicroserviceManagementGrpc.METHOD_GET_GLOBAL_CONFIGURATION, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.MicroserviceManagementGrpc.
     * MicroserviceManagementImplBase#getTenantConfiguration(com.sitewhere.grpc.
     * service.GGetTenantConfigurationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getTenantConfiguration(GGetTenantConfigurationRequest request,
	    StreamObserver<GGetTenantConfigurationResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(MicroserviceManagementGrpc.METHOD_GET_TENANT_CONFIGURATION);
	    GGetTenantConfigurationResponse.Builder response = GGetTenantConfigurationResponse.newBuilder();
	    GConfigurationContent.Builder configuration = GConfigurationContent.newBuilder();

	    if (getMicroservice() instanceof IMultitenantMicroservice) {
		byte[] content = ((IMultitenantMicroservice<?>) getMicroservice())
			.getTenantConfiguration(request.getTenantId());
		configuration.setContent(ByteString.copyFrom(content));
	    } else {
		throw new SiteWhereException("Requesting tenant configuration from a global microservice.");
	    }

	    response.setConfiguration(configuration.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(MicroserviceManagementGrpc.METHOD_GET_TENANT_CONFIGURATION, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.MicroserviceManagementGrpc.
     * MicroserviceManagementImplBase#updateGlobalConfiguration(com.sitewhere.grpc.
     * service.GUpdateGlobalConfigurationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateGlobalConfiguration(GUpdateGlobalConfigurationRequest request,
	    StreamObserver<GUpdateGlobalConfigurationResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(MicroserviceManagementGrpc.METHOD_UPDATE_GLOBAL_CONFIGURATION);
	    byte[] content = request.getConfiguration().getContent().toByteArray();

	    if (getMicroservice() instanceof IGlobalMicroservice) {
		((IGlobalMicroservice) getMicroservice()).updateConfiguration(content);
	    } else {
		throw new SiteWhereException("Requesting global configuration update from a tenant microservice.");
	    }

	    GUpdateGlobalConfigurationResponse.Builder response = GUpdateGlobalConfigurationResponse.newBuilder();
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(MicroserviceManagementGrpc.METHOD_UPDATE_GLOBAL_CONFIGURATION, e);
	    responseObserver.onError(e);
	}
    }

    /*
     * @see com.sitewhere.grpc.service.MicroserviceManagementGrpc.
     * MicroserviceManagementImplBase#updateTenantConfiguration(com.sitewhere.grpc.
     * service.GUpdateTenantConfigurationRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateTenantConfiguration(GUpdateTenantConfigurationRequest request,
	    StreamObserver<GUpdateTenantConfigurationResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(MicroserviceManagementGrpc.METHOD_UPDATE_TENANT_CONFIGURATION);
	    byte[] content = request.getConfiguration().getContent().toByteArray();

	    if (getMicroservice() instanceof IMultitenantMicroservice) {
		((IMultitenantMicroservice<?>) getMicroservice()).updateTenantConfiguration(request.getTenantId(),
			content);
	    } else {
		throw new SiteWhereException("Requesting tenant configuration from a global microservice.");
	    }

	    GUpdateTenantConfigurationResponse.Builder response = GUpdateTenantConfigurationResponse.newBuilder();
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.logServerMethodException(MicroserviceManagementGrpc.METHOD_UPDATE_TENANT_CONFIGURATION, e);
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