/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.microservice;

import com.sitewhere.grpc.client.ApiChannel;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.common.converter.MicroserviceModelConverter;
import com.sitewhere.grpc.client.spi.client.IMicroserviceManagementApiChannel;
import com.sitewhere.grpc.service.GGetConfigurationModelRequest;
import com.sitewhere.grpc.service.GGetConfigurationModelResponse;
import com.sitewhere.grpc.service.MicroserviceManagementGrpc;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.configuration.model.IConfigurationModel;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * Supports SiteWhere microservice management APIs on top of a
 * {@link MicroserviceManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class MicroserviceManagementApiChannel extends ApiChannel<MicroserviceManagementGrpcChannel>
	implements IMicroserviceManagementApiChannel<MicroserviceManagementGrpcChannel> {

    public MicroserviceManagementApiChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	super(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .microservice.instance.IInstanceSettings,
     * com.sitewhere.spi.microservice.IFunctionIdentifier, int)
     */
    @Override
    public MicroserviceManagementGrpcChannel createGrpcChannel(IInstanceSettings settings,
	    IFunctionIdentifier identifier, IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	return new MicroserviceManagementGrpcChannel(settings, identifier, grpcServiceIdentifier, port);
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
}