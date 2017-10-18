/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.event.spi.microservice;

import com.sitewhere.grpc.service.DeviceEventManagementGrpc;
import com.sitewhere.microservice.spi.multitenant.IMicroserviceTenantEngine;
import com.sitewhere.spi.device.event.IDeviceEventManagement;

/**
 * Extends {@link IMicroserviceTenantEngine} with features specific to device
 * event management.
 * 
 * @author Derek
 */
public interface IEventManagementTenantEngine extends IMicroserviceTenantEngine {

    /**
     * Get associated event management implementation.
     * 
     * @return
     */
    public IDeviceEventManagement getEventManagement();

    /**
     * Get implementation class that wraps event management with GRPC
     * conversions.
     * 
     * @return
     */
    public DeviceEventManagementGrpc.DeviceEventManagementImplBase getEventManagementImpl();
}