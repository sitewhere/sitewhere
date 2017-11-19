/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.spi.microservice;

import com.sitewhere.grpc.model.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.ITenantManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IUserManagementApiChannel;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.microservice.IGlobalMicroservice;

/**
 * Microservice that provides web/REST functionality.
 * 
 * @author Derek
 */
public interface IWebRestMicroservice extends IGlobalMicroservice {

    /**
     * User management API access via GRPC channel.
     * 
     * @return
     */
    public IUserManagementApiChannel getUserManagementApiChannel();

    /**
     * Tenant management API access via GRPC channel.
     * 
     * @return
     */
    public ITenantManagementApiChannel getTenantManagementApiChannel();

    /**
     * Device management API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceManagementApiChannel getDeviceManagementApiChannel();

    /**
     * Device event management API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceEventManagementApiChannel getDeviceEventManagementApiChannel();

    /**
     * Asset management API access via GRPC channel.
     * 
     * @return
     */
    public IAssetManagementApiChannel getAssetManagementApiChannel();

    /**
     * Get asset resolver implementation.
     * 
     * @return
     */
    public IAssetResolver getAssetResolver();
}