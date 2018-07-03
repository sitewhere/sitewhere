/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.web.spi.microservice;

import com.sitewhere.grpc.client.spi.client.IAssetManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.IBatchManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceEventManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.IDeviceStateApiDemux;
import com.sitewhere.grpc.client.spi.client.ILabelGenerationApiDemux;
import com.sitewhere.grpc.client.spi.client.IScheduleManagementApiDemux;
import com.sitewhere.grpc.client.spi.provider.ITenantManagementDemuxProvider;
import com.sitewhere.grpc.client.spi.provider.IUserManagementDemuxProvider;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.IGlobalMicroservice;
import com.sitewhere.spi.microservice.management.IMicroserviceManagementCoordinator;

/**
 * Microservice that provides web/REST functionality.
 * 
 * @author Derek
 */
public interface IWebRestMicroservice<T extends IFunctionIdentifier>
	extends IGlobalMicroservice<T>, IUserManagementDemuxProvider<T>, ITenantManagementDemuxProvider<T> {

    /**
     * Device management API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceManagementApiDemux getDeviceManagementApiDemux();

    /**
     * Device event management API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceEventManagementApiDemux getDeviceEventManagementApiDemux();

    /**
     * Asset management API demux.
     * 
     * @return
     */
    public IAssetManagementApiDemux getAssetManagementApiDemux();

    /**
     * Batch management API access via GRPC channel.
     * 
     * @return
     */
    public IBatchManagementApiDemux getBatchManagementApiDemux();

    /**
     * Schedule management API access via GRPC channel.
     * 
     * @return
     */
    public IScheduleManagementApiDemux getScheduleManagementApiDemux();

    /**
     * Label generation API access via GRPC channel.
     * 
     * @return
     */
    public ILabelGenerationApiDemux getLabelGenerationApiDemux();

    /**
     * Device state API access via GRPC channel.
     * 
     * @return
     */
    public IDeviceStateApiDemux getDeviceStateApiDemux();

    /**
     * Get microservice management coordinator.
     * 
     * @return
     */
    public IMicroserviceManagementCoordinator getMicroserviceManagementCoordinator();
}