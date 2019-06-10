/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.microservice;

import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.service.MicroserviceManagementGrpc;
import com.sitewhere.grpc.service.MicroserviceManagementGrpc.MicroserviceManagementBlockingStub;
import com.sitewhere.grpc.service.MicroserviceManagementGrpc.MicroserviceManagementStub;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * Channel that allows for communication with a remote microservice mangement
 * GRPC server.
 * 
 * @author Derek
 */
public class MicroserviceManagementGrpcChannel
	extends GrpcChannel<MicroserviceManagementBlockingStub, MicroserviceManagementStub> {

    public MicroserviceManagementGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	super(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public MicroserviceManagementBlockingStub createBlockingStub() {
	return MicroserviceManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public MicroserviceManagementStub createAsyncStub() {
	return MicroserviceManagementGrpc.newStub(getChannel());
    }
}