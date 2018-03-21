/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi.server;

import io.grpc.stub.StreamObserver;

/**
 * Common logic for GRPC routers.
 * 
 * @author Derek
 *
 * @param <T>
 */
public interface IGrpcRouter<T> {

    /**
     * Get tenant implementation to route to.
     * 
     * @return
     * @throws Throwable
     */
    public T getTenantImplementation(StreamObserver<?> observer);
}
