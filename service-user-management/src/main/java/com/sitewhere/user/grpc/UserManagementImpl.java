package com.sitewhere.user.grpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.service.GCreateUserRequest;
import com.sitewhere.grpc.service.GCreateUserResponse;
import com.sitewhere.grpc.service.GImportUserRequest;
import com.sitewhere.grpc.service.GImportUserResponse;
import com.sitewhere.grpc.service.UserManagementGrpc;
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
     * createUser(com.sitewhere.grpc.model.GCreateUserRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createUser(GCreateUserRequest request, StreamObserver<GCreateUserResponse> responseObserver) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.UserManagementGrpc.UserManagementImplBase#
     * importUser(com.sitewhere.grpc.model.GImportUserRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void importUser(GImportUserRequest request, StreamObserver<GImportUserResponse> responseObserver) {
    }

    public IUserManagement getUserMangagement() {
	return userMangagement;
    }

    public void setUserMangagement(IUserManagement userMangagement) {
	this.userMangagement = userMangagement;
    }
}