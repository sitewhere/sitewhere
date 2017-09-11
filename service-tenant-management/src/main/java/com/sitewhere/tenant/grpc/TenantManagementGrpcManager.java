package com.sitewhere.tenant.grpc;

import com.sitewhere.microservice.grpc.ManagedGrpcServer;
import com.sitewhere.tenant.spi.grpc.ITenantManagementGrpcManager;

import io.grpc.BindableService;
import io.grpc.ServerServiceDefinition;

/**
 * Hosts a GRPC server that handles tenant management requests.
 * 
 * @author Derek
 */
public class TenantManagementGrpcManager extends ManagedGrpcServer implements ITenantManagementGrpcManager {

    /** Service that provides tenant management implementation */
    private TenantManagementService tenantManagementService = new TenantManagementService();

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.grpc.IManagedGrpcServer#
     * getServiceImplementation()
     */
    @Override
    public BindableService getServiceImplementation() {
	return getTenantManagementService();
    }

    public TenantManagementService getTenantManagementService() {
	return tenantManagementService;
    }

    public void setTenantManagementService(TenantManagementService tenantManagementService) {
	this.tenantManagementService = tenantManagementService;
    }
}