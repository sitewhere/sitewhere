package com.sitewhere.grpc.model.client;

import com.sitewhere.grpc.model.TenantManagementGrpc;
import com.sitewhere.grpc.model.TenantManagementGrpc.TenantManagementBlockingStub;
import com.sitewhere.grpc.model.TenantManagementGrpc.TenantManagementStub;

/**
 * Channel that allows for communication with a remote tenant mangement GRPC
 * server.
 * 
 * @author Derek
 */
public class TenantManagementChannel extends GrpcChannel<TenantManagementBlockingStub, TenantManagementStub> {

    public TenantManagementChannel(String host, int port) {
	super(host, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public TenantManagementBlockingStub createBlockingStub() {
	return TenantManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public TenantManagementStub createAsyncStub() {
	return TenantManagementGrpc.newStub(getChannel());
    }
}