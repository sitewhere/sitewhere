package com.sitewhere.user.grpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.UserManagementGrpc;
import com.sitewhere.grpc.model.UserModel.GUser;
import com.sitewhere.grpc.model.UserModel.GUserCreateRequest;
import com.sitewhere.spi.user.IUserManagement;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for user management GRPC requests.
 * 
 * @author Derek
 */
public class UserManagementImpl extends UserManagementGrpc.UserManagementImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** Uesr management persistence */
    private IUserManagement userMangagement;

    public UserManagementImpl(IUserManagement userMangagement) {
	this.userMangagement = userMangagement;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.UserManagementGrpc.UserManagementImplBase#
     * createUser(com.sitewhere.grpc.model.UserModel.GUserCreateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createUser(GUserCreateRequest request, StreamObserver<GUser> responseObserver) {
	// TODO Auto-generated method stub
	super.createUser(request, responseObserver);
    }

    public IUserManagement getUserMangagement() {
	return userMangagement;
    }

    public void setUserMangagement(IUserManagement userMangagement) {
	this.userMangagement = userMangagement;
    }
}