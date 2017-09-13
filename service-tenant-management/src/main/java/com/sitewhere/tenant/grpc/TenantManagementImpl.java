package com.sitewhere.tenant.grpc;

import com.sitewhere.grpc.model.TenantManagementGrpc;
import com.sitewhere.grpc.model.TenantModel.GTenant;
import com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for tenant management GRPC requests.
 * 
 * @author Derek
 */
public class TenantManagementImpl extends TenantManagementGrpc.TenantManagementImplBase {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.model.TenantManagementGrpc.TenantManagementImplBase#
     * createTenant(com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createTenant(GTenantCreateRequest request, StreamObserver<GTenant> responseObserver) {
	// TODO Auto-generated method stub
	super.createTenant(request, responseObserver);
    }
}