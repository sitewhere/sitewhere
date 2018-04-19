/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.spi.provider;

import com.sitewhere.grpc.client.spi.client.ITenantManagementApiDemux;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IMicroservice;

/**
 * Interface declared by microservices that provide GRPC access to tenant
 * management.
 * 
 * @author Derek
 *
 * @param <T>
 */
public interface ITenantManagementDemuxProvider<T extends IFunctionIdentifier> extends IMicroservice<T> {

    /**
     * Tenant management API access via GRPC channel.
     * 
     * @return
     */
    public ITenantManagementApiDemux getTenantManagementApiDemux();
}