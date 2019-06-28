/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi;

import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.server.lifecycle.ITenantEngineLifecycleComponent;

/**
 * Common interface for GRPC channels that handle API calls.
 * 
 * @author Derek
 */
public interface IApiChannel<T extends GrpcChannel<?, ?>> extends ITenantEngineLifecycleComponent {

    /**
     * Get function identifier.
     * 
     * @return
     */
    public IFunctionIdentifier getFunctionIdentifier();

    /**
     * Get gRPC service identifier.
     * 
     * @return
     */
    public IGrpcServiceIdentifier getGrpcServiceIdentifier();

    /**
     * Get target port.
     * 
     * @return
     */
    public int getPort();

    /**
     * Create underlying GRPC channel.
     * 
     * @param settings
     * @param identifier
     * @param grpcServiceIdentifier
     * @param port
     * @return
     */
    public T createGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port);

    /**
     * Get underlying GRPC channel.
     * 
     * @return
     */
    public T getGrpcChannel();
}