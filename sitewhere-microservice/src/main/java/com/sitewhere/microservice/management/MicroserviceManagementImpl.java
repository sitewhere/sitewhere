/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.management;

import java.util.List;

import com.google.protobuf.ByteString;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.grpc.model.MicroserviceModel.GBinaryContent;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.converter.MicroserviceModelConverter;
import com.sitewhere.grpc.service.GGetConfigurationModelRequest;
import com.sitewhere.grpc.service.GGetConfigurationModelResponse;
import com.sitewhere.grpc.service.GGetGlobalConfigurationRequest;
import com.sitewhere.grpc.service.GGetGlobalConfigurationResponse;
import com.sitewhere.grpc.service.GGetScriptTemplateContentRequest;
import com.sitewhere.grpc.service.GGetScriptTemplateContentResponse;
import com.sitewhere.grpc.service.GGetScriptTemplatesRequest;
import com.sitewhere.grpc.service.GGetScriptTemplatesResponse;
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
import com.sitewhere.spi.microservice.scripting.IScriptTemplate;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for microservice management GRPC requests.
 * 
 * @author Derek
 */
public class MicroserviceManagementImpl extends MicroserviceManagementGrpc.MicroserviceManagementImplBase
	implements IGrpcApiImplementation {

    /** Microservice */
    private IMicroservice<?> microservice;

    public MicroserviceManagementImpl(IMicroservice<?> microservice) {
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
	    GrpcUtils.handleServerMethodEntry(this, MicroserviceManagementGrpc.getGetConfigurationModelMethod());
	    IConfigurationModel apiResult = getMicroservice().getConfigurationModel();
	    GGetConfigurationModelResponse.Builder response = GGetConfigurationModelResponse.newBuilder();
	    response.setModel(MicroserviceModelConverter.asGrpcConfigurationModel(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(MicroserviceManagementGrpc.getGetConfigurationModelMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(MicroserviceManagementGrpc.getGetConfigurationModelMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, MicroserviceManagementGrpc.getGetGlobalConfigurationMethod());
	    GGetGlobalConfigurationResponse.Builder response = GGetGlobalConfigurationResponse.newBuilder();
	    GBinaryContent.Builder configuration = GBinaryContent.newBuilder();

	    if (getMicroservice() instanceof IGlobalMicroservice) {
		byte[] content = ((IGlobalMicroservice<?>) getMicroservice()).getConfiguration();
		configuration.setContent(ByteString.copyFrom(content));
	    } else {
		throw new SiteWhereException("Requesting global configuration from a tenant microservice.");
	    }

	    response.setConfiguration(configuration.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(MicroserviceManagementGrpc.getGetGlobalConfigurationMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(MicroserviceManagementGrpc.getGetGlobalConfigurationMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, MicroserviceManagementGrpc.getGetTenantConfigurationMethod());
	    GGetTenantConfigurationResponse.Builder response = GGetTenantConfigurationResponse.newBuilder();
	    GBinaryContent.Builder configuration = GBinaryContent.newBuilder();

	    if (getMicroservice() instanceof IMultitenantMicroservice) {
		byte[] content = ((IMultitenantMicroservice<?, ?>) getMicroservice())
			.getTenantConfiguration(CommonModelConverter.asApiUuid(request.getTenantId()));
		configuration.setContent(ByteString.copyFrom(content));
	    } else {
		throw new SiteWhereException("Requesting tenant configuration from a global microservice.");
	    }

	    response.setConfiguration(configuration.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(MicroserviceManagementGrpc.getGetTenantConfigurationMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(MicroserviceManagementGrpc.getGetTenantConfigurationMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, MicroserviceManagementGrpc.getUpdateGlobalConfigurationMethod());
	    byte[] content = request.getConfiguration().getContent().toByteArray();

	    if (getMicroservice() instanceof IGlobalMicroservice) {
		((IGlobalMicroservice<?>) getMicroservice()).updateConfiguration(content);
	    } else {
		throw new SiteWhereException("Requesting global configuration update from a tenant microservice.");
	    }

	    GUpdateGlobalConfigurationResponse.Builder response = GUpdateGlobalConfigurationResponse.newBuilder();
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(MicroserviceManagementGrpc.getUpdateGlobalConfigurationMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(MicroserviceManagementGrpc.getUpdateGlobalConfigurationMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, MicroserviceManagementGrpc.getUpdateTenantConfigurationMethod());
	    byte[] content = request.getConfiguration().getContent().toByteArray();

	    if (getMicroservice() instanceof IMultitenantMicroservice) {
		((IMultitenantMicroservice<?, ?>) getMicroservice())
			.updateTenantConfiguration(CommonModelConverter.asApiUuid(request.getTenantId()), content);
	    } else {
		throw new SiteWhereException("Requesting tenant configuration from a global microservice.");
	    }

	    GUpdateTenantConfigurationResponse.Builder response = GUpdateTenantConfigurationResponse.newBuilder();
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(MicroserviceManagementGrpc.getUpdateTenantConfigurationMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(MicroserviceManagementGrpc.getUpdateTenantConfigurationMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.MicroserviceManagementGrpc.
     * MicroserviceManagementImplBase#getScriptTemplates(com.sitewhere.grpc.service.
     * GGetScriptTemplatesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getScriptTemplates(GGetScriptTemplatesRequest request,
	    StreamObserver<GGetScriptTemplatesResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, MicroserviceManagementGrpc.getGetScriptTemplatesMethod());
	    List<IScriptTemplate> templates = getMicroservice().getScriptTemplateManager().getScriptTemplates();

	    GGetScriptTemplatesResponse.Builder response = GGetScriptTemplatesResponse.newBuilder();
	    for (IScriptTemplate template : templates) {
		response.addTemplates(MicroserviceModelConverter.asGrpcScriptTemplate(template));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(MicroserviceManagementGrpc.getGetScriptTemplatesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(MicroserviceManagementGrpc.getGetScriptTemplatesMethod());
	}
    }

    /*
     * @see com.sitewhere.grpc.service.MicroserviceManagementGrpc.
     * MicroserviceManagementImplBase#getScriptTemplateContent(com.sitewhere.grpc.
     * service.GGetScriptTemplateContentRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getScriptTemplateContent(GGetScriptTemplateContentRequest request,
	    StreamObserver<GGetScriptTemplateContentResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, MicroserviceManagementGrpc.getGetScriptTemplateContentMethod());
	    byte[] content = getMicroservice().getScriptTemplateManager().getScriptTemplateContent(request.getId());

	    GGetScriptTemplateContentResponse.Builder response = GGetScriptTemplateContentResponse.newBuilder();
	    GBinaryContent binary = GBinaryContent.newBuilder().setContent(ByteString.copyFrom(content)).build();
	    response.setTemplate(binary);
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(MicroserviceManagementGrpc.getGetScriptTemplateContentMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(MicroserviceManagementGrpc.getGetScriptTemplateContentMethod());
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation#getMicroservice()
     */
    @Override
    public IMicroservice<?> getMicroservice() {
	return microservice;
    }
}