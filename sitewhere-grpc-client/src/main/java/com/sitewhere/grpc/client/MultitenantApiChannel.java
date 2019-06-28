/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client;

import com.sitewhere.grpc.client.spi.multitenant.IMultitenantApiChannel;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;

/**
 * Extends {@link ApiChannel} with methods for dealing with multitenant
 * microservices.
 *
 * @param <T>
 */
public abstract class MultitenantApiChannel<T extends MultitenantGrpcChannel<?, ?>> extends ApiChannel<T>
	implements IMultitenantApiChannel<T> {

    public MultitenantApiChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	super(settings, identifier, grpcServiceIdentifier, port);
    }
}