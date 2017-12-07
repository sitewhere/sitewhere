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
import com.sitewhere.grpc.client.spi.client.IScheduleManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.ITenantManagementApiDemux;
import com.sitewhere.grpc.client.spi.client.IUserManagementApiDemux;
import com.sitewhere.spi.asset.IAssetResolver;
import com.sitewhere.spi.microservice.IGlobalMicroservice;
import com.sitewhere.spi.microservice.management.IMicroserviceManagementCoordinator;

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
    public IUserManagementApiDemux getUserManagementApiDemux();

    /**
     * Tenant management API access via GRPC channel.
     * 
     * @return
     */
    public ITenantManagementApiDemux getTenantManagementApiDemux();

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
     * Get asset resolver implementation.
     * 
     * @return
     */
    public IAssetResolver getAssetResolver();

    /**
     * Get microservice management coordinator.
     * 
     * @return
     */
    public IMicroserviceManagementCoordinator getMicroserviceManagementCoordinator();
}