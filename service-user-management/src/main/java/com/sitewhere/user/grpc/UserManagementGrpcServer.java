package com.sitewhere.user.grpc;

import com.sitewhere.microservice.grpc.GrpcServer;
import com.sitewhere.microservice.spi.IMicroservice;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.user.spi.grpc.IUserManagementGrpcServer;

/**
 * Hosts a GRPC server that handles user management requests.
 * 
 * @author Derek
 */
public class UserManagementGrpcServer extends GrpcServer implements IUserManagementGrpcServer {

    public UserManagementGrpcServer(IMicroservice microservice, IUserManagement userManagement) {
	super(microservice, new UserManagementImpl(userManagement));
    }
}