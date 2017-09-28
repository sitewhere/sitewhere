package com.sitewhere.user.grpc;

import com.sitewhere.microservice.grpc.ManagedGrpcServer;
import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.user.spi.grpc.IUserManagementGrpcServer;

/**
 * Hosts a GRPC server that handles user management requests.
 * 
 * @author Derek
 */
public class UserManagementGrpcServer extends ManagedGrpcServer implements IUserManagementGrpcServer {

    public UserManagementGrpcServer(IMicroservice microservice, IUserManagement userManagement) {
	super(microservice, new UserManagementImpl(userManagement));
    }
}