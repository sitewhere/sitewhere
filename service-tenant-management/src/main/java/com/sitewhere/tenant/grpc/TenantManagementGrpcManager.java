package com.sitewhere.tenant.grpc;

import com.sitewhere.microservice.grpc.ManagedGrpcServer;
import com.sitewhere.tenant.spi.grpc.ITenantManagementGrpcManager;

import io.grpc.ServerServiceDefinition;

/**
 * Hosts a GRPC server that handles tenant management requests.
 * 
 * @author Derek
 */
public class TenantManagementGrpcManager extends ManagedGrpcServer implements ITenantManagementGrpcManager {

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.grpc.IManagedGrpcServer#
     * getServerServiceDefinition()
     */
    @Override
    public ServerServiceDefinition getServerServiceDefinition() {
	return new TenantManagementService().bindService();
    }
}