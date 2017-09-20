package com.sitewhere.user.grpc;

import com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase;
import com.sitewhere.microservice.grpc.ManagedGrpcServer;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.user.spi.grpc.IUserManagementGrpcServer;

import io.grpc.BindableService;

/**
 * Hosts a GRPC server that handles user management requests.
 * 
 * @author Derek
 */
public class UserManagementGrpcServer extends ManagedGrpcServer implements IUserManagementGrpcServer {

    /** Service that provides user management implementation */
    private UserManagementImplBase userManagementService;

    public UserManagementGrpcServer(IUserManagement userManagement) {
	this.userManagementService = new UserManagementImpl(userManagement);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.microservice.spi.grpc.IManagedGrpcServer#
     * getServiceImplementation()
     */
    @Override
    public BindableService getServiceImplementation() {
	return getUserManagementService();
    }

    public UserManagementImplBase getUserManagementService() {
	return userManagementService;
    }

    public void setUserManagementService(UserManagementImplBase userManagementService) {
	this.userManagementService = userManagementService;
    }
}