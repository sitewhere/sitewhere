/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.inbound.spi.microservice;

import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiDemux;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

/**
 * Microservice that provides inbound event processing functionality.
 * 
 * @author Derek
 */
public interface IInboundProcessingMicroservice extends IMultitenantMicroservice<IInboundProcessingTenantEngine> {

    /**
     * Get device management API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceManagementApiDemux getDeviceManagementApiDemux();

    /**
     * Get device event management API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceEventManagementApiDemux getDeviceEventManagementApiDemux();
}