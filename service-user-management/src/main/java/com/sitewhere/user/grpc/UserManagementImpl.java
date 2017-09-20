package com.sitewhere.user.grpc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.converter.UserModelConverter;
import com.sitewhere.grpc.service.GAuthenticateRequest;
import com.sitewhere.grpc.service.GAuthenticateResponse;
import com.sitewhere.grpc.service.GCreateUserRequest;
import com.sitewhere.grpc.service.GCreateUserResponse;
import com.sitewhere.grpc.service.GGetUserByUsernameRequest;
import com.sitewhere.grpc.service.GGetUserByUsernameResponse;
import com.sitewhere.grpc.service.GImportUserRequest;
import com.sitewhere.grpc.service.GImportUserResponse;
import com.sitewhere.grpc.service.GUpdateUserRequest;
import com.sitewhere.grpc.service.GUpdateUserResponse;
import com.sitewhere.grpc.service.UserManagementGrpc;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.request.IUserCreateRequest;

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
	try {
	    IUserCreateRequest apiRequest = UserModelConverter.asApiUserCreateRequest(request.getRequest());
	    IUser apiResult = getUserMangagement().createUser(apiRequest, request.getEncodePassword());
	    GCreateUserResponse.Builder response = GCreateUserResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (SiteWhereException e) {
	    responseObserver.onError(e);
	}
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
	try {
	    IUser apiUser = UserModelConverter.asApiUser(request.getUser());
	    IUser apiResult = getUserMangagement().importUser(apiUser, request.getOverwrite());
	    GImportUserResponse.Builder response = GImportUserResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (SiteWhereException e) {
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * authenticate(com.sitewhere.grpc.service.GAuthenticateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void authenticate(GAuthenticateRequest request, StreamObserver<GAuthenticateResponse> responseObserver) {
	try {
	    IUser apiResult = getUserMangagement().authenticate(request.getUsername(), request.getPassword(),
		    request.getUpdateLastLogin());
	    GAuthenticateResponse.Builder response = GAuthenticateResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (SiteWhereException e) {
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * updateUser(com.sitewhere.grpc.service.GUpdateUserRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateUser(GUpdateUserRequest request, StreamObserver<GUpdateUserResponse> responseObserver) {
	try {
	    IUserCreateRequest apiRequest = UserModelConverter.asApiUserCreateRequest(request.getRequest());
	    IUser apiResult = getUserMangagement().updateUser(request.getUsername(), apiRequest,
		    request.getEncodePassword());
	    GUpdateUserResponse.Builder response = GUpdateUserResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (SiteWhereException e) {
	    responseObserver.onError(e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * getUserByUsername(com.sitewhere.grpc.service.GGetUserByUsernameRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getUserByUsername(GGetUserByUsernameRequest request,
	    StreamObserver<GGetUserByUsernameResponse> responseObserver) {
	try {
	    IUser apiResult = getUserMangagement().getUserByUsername(request.getUsername());
	    GGetUserByUsernameResponse.Builder response = GGetUserByUsernameResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (SiteWhereException e) {
	    responseObserver.onError(e);
	}
    }

    public IUserManagement getUserMangagement() {
	return userMangagement;
    }

    public void setUserMangagement(IUserManagement userMangagement) {
	this.userMangagement = userMangagement;
    }
}