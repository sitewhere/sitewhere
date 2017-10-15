/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.device.spi.microservice;

import com.sitewhere.grpc.service.DeviceManagementGrpc;
import com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine;
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
}