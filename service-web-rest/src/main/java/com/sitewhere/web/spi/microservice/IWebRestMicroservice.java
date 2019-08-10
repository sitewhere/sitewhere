/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.spi.microservice;

import com.sitewhere.grpc.client.spi.client.IAssetManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IBatchManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IDeviceStateApiChannel;
import com.sitewhere.grpc.client.spi.client.IInstanceManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.ILabelGenerationApiChannel;
import com.sitewhere.grpc.client.spi.client.IScheduleManagementApiChannel;
import com.sitewhere.grpc.client.spi.client.IUserManagementApiChannel;
import com.sitewhere.spi.asset.IAssetManagement;
import com.sitewhere.spi.device.IDeviceManagement;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IGlobalMicroservice;
import com.sitewhere.spi.microservice.state.ITopologyStateAggregator;
import com.sitewhere.spi.user.IUserManagement;

/**
 * Microservice that provides web/REST functionality.
 * 
 * @author Derek
 */
public interface IWebRestMicroservice<T extends IFunctionIdentifier> extends IGlobalMicroservice<T> {

    /**
     * Instance API access via GRPC channel.
     * 
     * @return
     */
    public IInstanceManagementApiChannel<?> getInstanceManagementApiChannel();

    /**
     * User API access via GRPC channel.
     * 
     * @return
     */
    public IUserManagementApiChannel<?> getUserManagementApiChannel();

    /**
     * Get wrapper for caching data from the user management API channel.
     * 
     * @return
     */
    public IUserManagement getCachedUserManagement();

    /**
     * Device management API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceManagementApiChannel<?> getDeviceManagementApiChannel();

    /**
     * Caching wrapper around device management API channel.
     * 
     * @return
     */
    public IDeviceManagement getCachedDeviceManagement();

    /**
     * Device event management API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceEventManagementApiChannel<?> getDeviceEventManagementApiChannel();

    /**
     * Asset management API access via GRPC channel.
     * 
     * @return
     */
    public IAssetManagementApiChannel<?> getAssetManagementApiChannel();

    /**
     * Get wrapper for caching data from the asset management API channel.
     * 
     * @return
     */
    public IAssetManagement getCachedAssetManagement();

    /**
     * Batch management API access via GRPC channel.
     * 
     * @return
     */
    public IBatchManagementApiChannel<?> getBatchManagementApiChannel();

    /**
     * Schedule management API access via GRPC channel.
     * 
     * @return
     */
    public IScheduleManagementApiChannel<?> getScheduleManagementApiChannel();

    /**
     * Label generation API access via GRPC channel.
     * 
     * @return
     */
    public ILabelGenerationApiChannel<?> getLabelGenerationApiChannel();

    /**
     * Device state API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceStateApiChannel<?> getDeviceStateApiChannel();

    /**
     * Get topology state aggregator.
     * 
     * @return
     */
    public ITopologyStateAggregator getTopologyStateAggregator();
}