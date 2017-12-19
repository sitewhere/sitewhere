/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.microservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.protobuf.ByteString;
import com.sitewhere.grpc.client.ApiChannel;
import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.client.IMicroserviceManagementApiChannel;
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
import com.sitewhere.grpc.service.GUpdateTenantConfigurationRequest;
import com.sitewhere.grpc.service.MicroserviceManagementGrpc;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Supports SiteWhere microservice management APIs on top of a
 * {@link MicroserviceManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class MicroserviceManagementApiChannel extends ApiChannel<MicroserviceManagementGrpcChannel>
	implements IMicroserviceManagementApiChannel {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public MicroserviceManagementApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
	super(demux, microservice, host, microservice.getInstanceSettings().getManagementGrpcPort());
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .tracing.ITracerProvider, java.lang.String, int)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public GrpcChannel createGrpcChannel(ITracerProvider tracerProvider, String host, int port) {
	return new MicroserviceManagementGrpcChannel(tracerProvider, host, port);
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroserviceManagement#getConfigurationModel(
     * )
     */
    @Override
    public IConfigurationModel getConfigurationModel() throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(MicroserviceManagementGrpc.METHOD_GET_CONFIGURATION_MODEL);
	    GGetConfigurationModelRequest.Builder grequest = GGetConfigurationModelRequest.newBuilder();
	    GGetConfigurationModelResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getConfigurationModel(grequest.build());
	    IConfigurationModel response = MicroserviceModelConverter.asApiConfigurationModel(gresponse.getModel());
	    GrpcUtils.logClientMethodResponse(MicroserviceManagementGrpc.METHOD_GET_CONFIGURATION_MODEL, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(MicroserviceManagementGrpc.METHOD_GET_CONFIGURATION_MODEL, t);
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
	    GrpcUtils.logClientMethodEntry(MicroserviceManagementGrpc.METHOD_GET_GLOBAL_CONFIGURATION);
	    GGetGlobalConfigurationRequest.Builder grequest = GGetGlobalConfigurationRequest.newBuilder();
	    GGetGlobalConfigurationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getGlobalConfiguration(grequest.build());
	    byte[] response = gresponse.getConfiguration().getContent().toByteArray();
	    GrpcUtils.logClientMethodResponse(MicroserviceManagementGrpc.METHOD_GET_GLOBAL_CONFIGURATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(MicroserviceManagementGrpc.METHOD_GET_GLOBAL_CONFIGURATION, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.microservice.IMicroserviceManagement#getTenantConfiguration
     * (java.lang.String)
     */
    @Override
    public byte[] getTenantConfiguration(String tenantId) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(MicroserviceManagementGrpc.METHOD_GET_TENANT_CONFIGURATION);
	    GGetTenantConfigurationRequest.Builder grequest = GGetTenantConfigurationRequest.newBuilder();
	    grequest.setTenantId(tenantId);
	    GGetTenantConfigurationResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getTenantConfiguration(grequest.build());
	    byte[] response = gresponse.getConfiguration().getContent().toByteArray();
	    GrpcUtils.logClientMethodResponse(MicroserviceManagementGrpc.METHOD_GET_TENANT_CONFIGURATION, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(MicroserviceManagementGrpc.METHOD_GET_TENANT_CONFIGURATION, t);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroserviceManagement#
     * updateGlobalConfiguration(byte[])
     */
    @Override
    public void updateGlobalConfiguration(byte[] config) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(MicroserviceManagementGrpc.METHOD_UPDATE_GLOBAL_CONFIGURATION);
	    GUpdateGlobalConfigurationRequest.Builder grequest = GUpdateGlobalConfigurationRequest.newBuilder();
	    GConfigurationContent content = GConfigurationContent.newBuilder().setContent(ByteString.copyFrom(config))
		    .build();
	    grequest.setConfiguration(content);
	    getGrpcChannel().getBlockingStub().updateGlobalConfiguration(grequest.build());
	    return;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(MicroserviceManagementGrpc.METHOD_UPDATE_GLOBAL_CONFIGURATION,
		    t);
	}
    }

    /*
     * @see com.sitewhere.spi.microservice.IMicroserviceManagement#
     * updateTenantConfiguration(java.lang.String, byte[])
     */
    @Override
    public void updateTenantConfiguration(String tenantId, byte[] config) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(MicroserviceManagementGrpc.METHOD_UPDATE_TENANT_CONFIGURATION);
	    GUpdateTenantConfigurationRequest.Builder grequest = GUpdateTenantConfigurationRequest.newBuilder();
	    GConfigurationContent content = GConfigurationContent.newBuilder().setContent(ByteString.copyFrom(config))
		    .build();
	    grequest.setConfiguration(content);
	    grequest.setTenantId(tenantId);
	    getGrpcChannel().getBlockingStub().updateTenantConfiguration(grequest.build());
	    return;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(MicroserviceManagementGrpc.METHOD_UPDATE_TENANT_CONFIGURATION,
		    t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}