/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.spi.microservice;

import com.sitewhere.grpc.model.client.AssetManagementGrpcChannel;
import com.sitewhere.grpc.model.client.DeviceEventManagementGrpcChannel;
import com.sitewhere.grpc.model.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.service.DeviceManagementGrpc;
import com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.device.IDeviceManagement;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to device
 * management.
 * 
 * @author Derek
 */
public interface IDeviceManagementTenantEngine extends IMicroserviceTenantEngine {

    /**
     * Get associated device management implementation.
     * 
     * @return
     */
    public IDeviceManagement getDeviceManagement();

    /**
     * Get implementation class that wraps device management with GRPC
     * conversions.
     * 
     * @return
     */
    public DeviceManagementGrpc.DeviceManagementImplBase getDeviceManagementImpl();

    /**
     * Get device event mangement GRPC channel.
     * 
     * @return
     */
    public DeviceEventManagementGrpcChannel getEventManagementGrpcChannel();

    /**
     * Get device event management API channel.
     * 
     * @return
     */
    public IDeviceEventManagementApiChannel getEventManagementApiChannel();

    /**
     * Get asset management GRPC channel.
     * 
     * @return
     */
    public AssetManagementGrpcChannel getAssetManagementGrpcChannel();

    /**
     * Get asset management API channel.
     * 
     * @return
     */
    public IAssetManagementApiChannel getAssetManagementApiChannel();

    /**
     * Get asset resolver.
     * 
     * @return
     */
    public IAssetResolver getAssetResolver();
}