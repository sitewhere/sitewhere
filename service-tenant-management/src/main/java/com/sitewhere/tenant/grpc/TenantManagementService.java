package com.sitewhere.tenant.grpc;

import com.sitewhere.grpc.model.TenantManagementGrpc;
import com.sitewhere.grpc.model.TenantModel.GTenant;
import com.sitewhere.grpc.model.TenantModel.GTenantCreateRequest;

import io.grpc.stub.StreamObserver;

/**
 * 
 * @author Derek
 *
 */
public class TenantManagementService extends TenantManagementGrpc.TenantManagementImplBase {

    @Override
    public void createTenant(GTenantCreateRequest request, StreamObserver<GTenant> responseObserver) {
	// TODO Auto-generated method stub
	super.createTenant(request, responseObserver);
    }
}