package com.sitewhere.tenant.grpc;

import com.sitewhere.microservice.grpc.ManagedGrpcServer;
import com.sitewhere.tenant.spi.grpc.ITenantManagementGrpcServer;

import io.grpc.BindableService;

/**
 * Hosts a GRPC server that handles tenant management requests.
 * 
 * @author Derek
 */
public class TenantManagementGrpcServer extends ManagedGrpcServer implements ITenantManagementGrpcServer {

    /** Service that provides tenant management implementation */
    private TenantManagementImpl tenantManagementService = new TenantManagementImpl();

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

    public TenantManagementImpl getTenantManagementService() {
	return tenantManagementService;
    }

    public void setTenantManagementService(TenantManagementImpl tenantManagementService) {
	this.tenantManagementService = tenantManagementService;
    }
}