/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi.server;

import com.sitewhere.spi.microservice.IMicroservice;

/**
 * Provides a common interface for gRPC API implementations.
 * 
 * @author Derek
 */
public interface IGrpcApiImplementation {

    /**
     * Get parent microservice.
     * 
     * @return
     */
    public IMicroservice<?> getMicroservice();
}