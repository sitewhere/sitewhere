/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.asset.spi.microservice;

import com.sitewhere.asset.configuration.AssetManagementConfiguration;
import com.sitewhere.asset.spi.grpc.IAssetManagementGrpcServer;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.multitenant.IMultitenantMicroservice;

/**
 * Microservice that provides asset management functionality.
 */
public interface IAssetManagementMicroservice extends
	IMultitenantMicroservice<MicroserviceIdentifier, AssetManagementConfiguration, IAssetManagementTenantEngine> {

    /**
     * Get asset management GRPC server.
     * 
     * @return
     */
    public IAssetManagementGrpcServer getAssetManagementGrpcServer();

    /**
     * Get device management API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceManagementApiChannel<?> getDeviceManagementApiChannel();
}