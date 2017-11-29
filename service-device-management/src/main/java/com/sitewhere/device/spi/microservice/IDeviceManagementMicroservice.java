/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.spi.microservice;

import com.sitewhere.device.spi.grpc.IDeviceManagementGrpcServer;
import com.sitewhere.grpc.model.client.AssetManagementGrpcChannel;
import com.sitewhere.grpc.model.client.DeviceEventManagementGrpcChannel;
import com.sitewhere.grpc.model.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.model.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

/**
 * Microservice that provides device management functionality.
 * 
 * @author Derek
 */
public interface IDeviceManagementMicroservice extends IMultitenantMicroservice<IDeviceManagementTenantEngine> {

    /**
     * Get device management GRPC server.
     * 
     * @return
     */
    public IDeviceManagementGrpcServer getDeviceManagementGrpcServer();

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