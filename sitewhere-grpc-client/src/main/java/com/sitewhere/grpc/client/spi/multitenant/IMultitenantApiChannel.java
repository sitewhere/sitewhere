/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi.multitenant;

import com.sitewhere.grpc.client.MultitenantGrpcChannel;
import com.sitewhere.grpc.client.spi.IApiChannel;

/**
 * Extends {@link IApiChannel} with support for interacting with external
 * microservices that support multiple tenants.
 * 
 * @author Derek
 *
 * @param <T>
 */
public interface IMultitenantApiChannel<T extends MultitenantGrpcChannel<?, ?>> extends IApiChannel<T> {
}