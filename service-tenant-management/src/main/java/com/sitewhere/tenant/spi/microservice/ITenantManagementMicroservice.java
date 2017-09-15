package com.sitewhere.tenant.spi.microservice;

import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.tenant.spi.grpc.ITenantManagementGrpcServer;

/**
 * Microservice that provides tenant management functionality.
 * 
 * @author Derek
 */
public interface ITenantManagementMicroservice {

    /**
     * Get GRPC server for tenant managment APIS.
     * 
     * @return
     */
    public ITenantManagementGrpcServer getTenantManagementGrpcServer();

    /**
     * Get tenant management persistence API.
     * 
     * @return
     */
    public ITenantManagement getTenantManagement();
}