/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.tenant;

import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.service.TenantManagementGrpc;
import com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementBlockingStub;
import com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementStub;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * Channel that allows for communication with a remote tenant management GRPC
 * server.
 */
public class TenantManagementGrpcChannel extends GrpcChannel<TenantManagementBlockingStub, TenantManagementStub> {

    public TenantManagementGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	super(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public TenantManagementBlockingStub createBlockingStub() {
	return TenantManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public TenantManagementStub createAsyncStub() {
	return TenantManagementGrpc.newStub(getChannel());
    }
}