/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.microservice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.protobuf.ByteString;
import com.sitewhere.grpc.client.ApiChannel;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.client.common.converter.MicroserviceModelConverter;
import com.sitewhere.grpc.client.spi.client.IMicroserviceManagementApiChannel;
import com.sitewhere.grpc.model.MicroserviceModel.GBinaryContent;
import com.sitewhere.grpc.model.MicroserviceModel.GScriptTemplate;
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
import com.sitewhere.grpc.service.GUpdateTenantConfigurationRequest;
import com.sitewhere.grpc.service.MicroserviceManagementGrpc;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.microservice.scripting.IScriptTemplate;

/**
 * Supports SiteWhere microservice management APIs on top of a
 * {@link MicroserviceManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class MicroserviceManagementApiChannel extends ApiChannel<MicroserviceManagementGrpcChannel>
	implements IMicroserviceManagementApiChannel<MicroserviceManagementGrpcChannel> {

    public MicroserviceManagementApiChannel(IInstanceSettings settings, IFunctionIdentifier identifier, int port) {
	super(settings, identifier, port);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(java.lang.String,
     * int)
     */
    @Override
    public MicroserviceManagementGrpcChannel createGrpcChannel(String host, int port) {
	return new MicroserviceManagementGrpcChannel(host, port);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroserviceManagement#getConfigurationModel(
     * )
     */
    @Override
    public IConfigurationModel getConfigurationModel() throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, MicroserviceManagementGrpc.getGetConfigurationModelMethod());
	    GGetConfigurationModelRequest.Builder grequest = GGetConfigurationModelRequest.newBuilder();
	    GGetConfigurationModelResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getConfigurationModel(grequest.build());
	    IConfigurationModel response = MicroserviceModelConverter.asApiConfigurationModel(gresponse.getModel());
	    GrpcUtils.logClientMethodResponse(MicroserviceManagementGrpc.getGetConfigurationModelMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(MicroserviceManagementGrpc.getGetConfigurationModelMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroserviceManagement#getGlobalConfiguration
     * ()
     */
    @Override
    public byte[] getGlobalConfiguration() throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, MicroserviceManagementGrpc.getGetGlobalConfigurationMethod());
	    GGetGlobalConfigurationRequest.Builder grequest = GGetGlobalConfigurationRequest.newBuilder();
	    GGetGlobalConfigurationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getGlobalConfiguration(grequest.build());
	    byte[] response = gresponse.getConfiguration().getContent().toByteArray();
	    GrpcUtils.logClientMethodResponse(MicroserviceManagementGrpc.getGetGlobalConfigurationMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(MicroserviceManagementGrpc.getGetGlobalConfigurationMethod(),
		    t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroserviceManagement#getTenantConfiguration
     * (java.util.UUID)
     */
    @Override
    public byte[] getTenantConfiguration(UUID tenantId) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, MicroserviceManagementGrpc.getGetTenantConfigurationMethod());
	    GGetTenantConfigurationRequest.Builder grequest = GGetTenantConfigurationRequest.newBuilder();
	    grequest.setTenantId(CommonModelConverter.asGrpcUuid(tenantId));
	    GGetTenantConfigurationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getTenantConfiguration(grequest.build());
	    byte[] response = gresponse.getConfiguration().getContent().toByteArray();
	    GrpcUtils.logClientMethodResponse(MicroserviceManagementGrpc.getGetTenantConfigurationMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(MicroserviceManagementGrpc.getGetTenantConfigurationMethod(),
		    t);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroserviceManagement#
     * updateGlobalConfiguration(byte[])
     */
    @Override
    public void updateGlobalConfiguration(byte[] config) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, MicroserviceManagementGrpc.getUpdateGlobalConfigurationMethod());
	    GUpdateGlobalConfigurationRequest.Builder grequest = GUpdateGlobalConfigurationRequest.newBuilder();
	    GBinaryContent content = GBinaryContent.newBuilder().setContent(ByteString.copyFrom(config)).build();
	    grequest.setConfiguration(content);
	    getGrpcChannel().getBlockingStub().updateGlobalConfiguration(grequest.build());
	    return;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(MicroserviceManagementGrpc.getUpdateGlobalConfigurationMethod(),
		    t);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroserviceManagement#
     * updateTenantConfiguration(java.util.UUID, byte[])
     */
    @Override
    public void updateTenantConfiguration(UUID tenantId, byte[] config) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, MicroserviceManagementGrpc.getUpdateTenantConfigurationMethod());
	    GUpdateTenantConfigurationRequest.Builder grequest = GUpdateTenantConfigurationRequest.newBuilder();
	    GBinaryContent content = GBinaryContent.newBuilder().setContent(ByteString.copyFrom(config)).build();
	    grequest.setConfiguration(content);
	    grequest.setTenantId(CommonModelConverter.asGrpcUuid(tenantId));
	    getGrpcChannel().getBlockingStub().updateTenantConfiguration(grequest.build());
	    return;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(MicroserviceManagementGrpc.getUpdateTenantConfigurationMethod(),
		    t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroserviceManagement#getScriptTemplates()
     */
    @Override
    public List<IScriptTemplate> getScriptTemplates() throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, MicroserviceManagementGrpc.getGetScriptTemplatesMethod());
	    GGetScriptTemplatesRequest.Builder grequest = GGetScriptTemplatesRequest.newBuilder();
	    GGetScriptTemplatesResponse response = getGrpcChannel().getBlockingStub()
		    .getScriptTemplates(grequest.build());
	    List<IScriptTemplate> result = new ArrayList<>();
	    for (GScriptTemplate template : response.getTemplatesList()) {
		result.add(MicroserviceModelConverter.asApiScriptTemplate(template));
	    }
	    GrpcUtils.logClientMethodResponse(MicroserviceManagementGrpc.getGetScriptTemplatesMethod(), response);
	    return result;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(MicroserviceManagementGrpc.getGetScriptTemplatesMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroserviceManagement#
     * getScriptTemplateContent(java.lang.String)
     */
    @Override
    public byte[] getScriptTemplateContent(String id) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, MicroserviceManagementGrpc.getGetScriptTemplateContentMethod());
	    GGetScriptTemplateContentRequest.Builder grequest = GGetScriptTemplateContentRequest.newBuilder();
	    grequest.setId(id);
	    GGetScriptTemplateContentResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getScriptTemplateContent(grequest.build());
	    byte[] response = gresponse.getTemplate().getContent().toByteArray();
	    GrpcUtils.logClientMethodResponse(MicroserviceManagementGrpc.getGetScriptTemplateContentMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(MicroserviceManagementGrpc.getGetScriptTemplateContentMethod(),
		    t);
	}
    }
}