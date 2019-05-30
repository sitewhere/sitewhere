/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi.multitenant;

import com.sitewhere.grpc.client.spi.IGrpcChannel;

/**
 * Extends {@link IGrpcChannel} to add support for multitenant operations.
 * 
 * @author Derek
 *
 * @param <B>
 * @param <A>
 */
public interface IMultitenantGrpcChannel<B, A> extends IGrpcChannel<B, A> {
}