package com.sitewhere.grpc.model.client;

import com.sitewhere.grpc.service.UserManagementGrpc;
import com.sitewhere.grpc.service.UserManagementGrpc.UserManagementBlockingStub;
import com.sitewhere.grpc.service.UserManagementGrpc.UserManagementStub;

/**
 * Channel that allows for communication with a remote user mangement GRPC
 * server.
 * 
 * @author Derek
 */
public class UserManagementGrpcChannel extends GrpcChannel<UserManagementBlockingStub, UserManagementStub> {

    public UserManagementGrpcChannel(String host, int port) {
	super(host, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createBlockingStub()
     */
    @Override
    public UserManagementBlockingStub createBlockingStub() {
	return UserManagementGrpc.newBlockingStub(getChannel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.GrpcChannel#createAsyncStub()
     */
    @Override
    public UserManagementStub createAsyncStub() {
	return UserManagementGrpc.newStub(getChannel());
    }
}