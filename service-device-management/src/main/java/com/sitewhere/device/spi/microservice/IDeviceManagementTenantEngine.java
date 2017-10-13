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