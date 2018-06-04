/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.user.grpc;

import java.util.List;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.model.converter.UserModelConverter;
import com.sitewhere.grpc.service.GAddGrantedAuthoritiesRequest;
import com.sitewhere.grpc.service.GAddGrantedAuthoritiesResponse;
import com.sitewhere.grpc.service.GAuthenticateRequest;
import com.sitewhere.grpc.service.GAuthenticateResponse;
import com.sitewhere.grpc.service.GCreateGrantedAuthorityRequest;
import com.sitewhere.grpc.service.GCreateGrantedAuthorityResponse;
import com.sitewhere.grpc.service.GCreateUserRequest;
import com.sitewhere.grpc.service.GCreateUserResponse;
import com.sitewhere.grpc.service.GDeleteGrantedAuthorityRequest;
import com.sitewhere.grpc.service.GDeleteGrantedAuthorityResponse;
import com.sitewhere.grpc.service.GDeleteUserRequest;
import com.sitewhere.grpc.service.GDeleteUserResponse;
import com.sitewhere.grpc.service.GGetGrantedAuthoritiesRequest;
import com.sitewhere.grpc.service.GGetGrantedAuthoritiesResponse;
import com.sitewhere.grpc.service.GGetGrantedAuthorityByNameRequest;
import com.sitewhere.grpc.service.GGetGrantedAuthorityByNameResponse;
import com.sitewhere.grpc.service.GGetUserByUsernameRequest;
import com.sitewhere.grpc.service.GGetUserByUsernameResponse;
import com.sitewhere.grpc.service.GImportUserRequest;
import com.sitewhere.grpc.service.GImportUserResponse;
import com.sitewhere.grpc.service.GListGrantedAuthoritiesRequest;
import com.sitewhere.grpc.service.GListGrantedAuthoritiesResponse;
import com.sitewhere.grpc.service.GListUsersRequest;
import com.sitewhere.grpc.service.GListUsersResponse;
import com.sitewhere.grpc.service.GRemoveGrantedAuthoritiesRequest;
import com.sitewhere.grpc.service.GRemoveGrantedAuthoritiesResponse;
import com.sitewhere.grpc.service.GUpdateGrantedAuthorityRequest;
import com.sitewhere.grpc.service.GUpdateGrantedAuthorityResponse;
import com.sitewhere.grpc.service.GUpdateUserRequest;
import com.sitewhere.grpc.service.GUpdateUserResponse;
import com.sitewhere.grpc.service.UserManagementGrpc;
import com.sitewhere.rest.model.search.user.UserSearchCriteria;
import com.sitewhere.rest.model.user.GrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.IGrantedAuthority;
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

    /** User management persistence */
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
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_CREATE_USER);
	    IUserCreateRequest apiRequest = UserModelConverter.asApiUserCreateRequest(request.getRequest());
	    IUser apiResult = getUserMangagement().createUser(apiRequest, request.getEncodePassword());
	    GCreateUserResponse.Builder response = GCreateUserResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_CREATE_USER, e, responseObserver);
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
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_IMPORT_USER);
	    IUser apiUser = UserModelConverter.asApiUser(request.getUser());
	    IUser apiResult = getUserMangagement().importUser(apiUser, request.getOverwrite());
	    GImportUserResponse.Builder response = GImportUserResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_IMPORT_USER, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * authenticate(com.sitewhere.grpc.service.GAuthenticateRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void authenticate(GAuthenticateRequest request, StreamObserver<GAuthenticateResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_AUTHENTICATE);
	    IUser apiResult = getUserMangagement().authenticate(request.getUsername(), request.getPassword(),
		    request.getUpdateLastLogin());
	    GAuthenticateResponse.Builder response = GAuthenticateResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_AUTHENTICATE, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * updateUser(com.sitewhere.grpc.service.GUpdateUserRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateUser(GUpdateUserRequest request, StreamObserver<GUpdateUserResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_UPDATE_USER);
	    IUserCreateRequest apiRequest = UserModelConverter.asApiUserCreateRequest(request.getRequest());
	    IUser apiResult = getUserMangagement().updateUser(request.getUsername(), apiRequest,
		    request.getEncodePassword());
	    GUpdateUserResponse.Builder response = GUpdateUserResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_UPDATE_USER, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * getUserByUsername(com.sitewhere.grpc.service.GGetUserByUsernameRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getUserByUsername(GGetUserByUsernameRequest request,
	    StreamObserver<GGetUserByUsernameResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_GET_USER_BY_USERNAME);
	    IUser apiResult = getUserMangagement().getUserByUsername(request.getUsername());
	    GGetUserByUsernameResponse.Builder response = GGetUserByUsernameResponse.newBuilder();
	    if (apiResult != null) {
		response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_GET_USER_BY_USERNAME, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * listUsers(com.sitewhere.grpc.service.GListUsersRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listUsers(GListUsersRequest request, StreamObserver<GListUsersResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_LIST_USERS);
	    UserSearchCriteria criteria = new UserSearchCriteria();
	    criteria.setIncludeDeleted(request.getCriteria().getIncludeDeleted());
	    List<IUser> apiResult = getUserMangagement().listUsers(criteria);
	    GListUsersResponse.Builder response = GListUsersResponse.newBuilder();
	    for (IUser apiUser : apiResult) {
		response.addUser(UserModelConverter.asGrpcUser(apiUser));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_LIST_USERS, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * deleteUser(com.sitewhere.grpc.service.GDeleteUserRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteUser(GDeleteUserRequest request, StreamObserver<GDeleteUserResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_DELETE_USER);
	    IUser apiResult = getUserMangagement().deleteUser(request.getUsername(), request.getForce());
	    GDeleteUserResponse.Builder response = GDeleteUserResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_DELETE_USER, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * createGrantedAuthority(com.sitewhere.grpc.service.
     * GCreateGrantedAuthorityRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void createGrantedAuthority(GCreateGrantedAuthorityRequest request,
	    StreamObserver<GCreateGrantedAuthorityResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_CREATE_GRANTED_AUTHORITY);
	    IGrantedAuthority apiResult = getUserMangagement().createGrantedAuthority(
		    UserModelConverter.asApiGrantedAuthorityCreateRequest(request.getRequest()));
	    GCreateGrantedAuthorityResponse.Builder response = GCreateGrantedAuthorityResponse.newBuilder();
	    response.setAuthority(UserModelConverter.asGrpcGrantedAuthority(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_CREATE_GRANTED_AUTHORITY, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * getGrantedAuthorityByName(com.sitewhere.grpc.service.
     * GGetGrantedAuthorityByNameRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getGrantedAuthorityByName(GGetGrantedAuthorityByNameRequest request,
	    StreamObserver<GGetGrantedAuthorityByNameResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_GET_GRANTED_AUTHORITY_BY_NAME);
	    IGrantedAuthority apiResult = getUserMangagement().getGrantedAuthorityByName(request.getName());
	    GGetGrantedAuthorityByNameResponse.Builder response = GGetGrantedAuthorityByNameResponse.newBuilder();
	    if (apiResult != null) {
		response.setAuthority(UserModelConverter.asGrpcGrantedAuthority(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_GET_GRANTED_AUTHORITY_BY_NAME, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * updateGrantedAuthority(com.sitewhere.grpc.service.
     * GUpdateGrantedAuthorityRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateGrantedAuthority(GUpdateGrantedAuthorityRequest request,
	    StreamObserver<GUpdateGrantedAuthorityResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_UPDATE_GRANTED_AUTHORITY);
	    IGrantedAuthority apiResult = getUserMangagement().updateGrantedAuthority(request.getName(),
		    UserModelConverter.asApiGrantedAuthorityCreateRequest(request.getRequest()));
	    GUpdateGrantedAuthorityResponse.Builder response = GUpdateGrantedAuthorityResponse.newBuilder();
	    response.setAuthority(UserModelConverter.asGrpcGrantedAuthority(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_UPDATE_GRANTED_AUTHORITY, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * listGrantedAuthorities(com.sitewhere.grpc.service.
     * GListGrantedAuthoritiesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void listGrantedAuthorities(GListGrantedAuthoritiesRequest request,
	    StreamObserver<GListGrantedAuthoritiesResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_LIST_GRANTED_AUTHORITIES);
	    List<IGrantedAuthority> apiResult = getUserMangagement()
		    .listGrantedAuthorities(new GrantedAuthoritySearchCriteria());
	    GListGrantedAuthoritiesResponse.Builder response = GListGrantedAuthoritiesResponse.newBuilder();
	    for (IGrantedAuthority apiAuth : apiResult) {
		response.addAuthorities(UserModelConverter.asGrpcGrantedAuthority(apiAuth));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_LIST_GRANTED_AUTHORITIES, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * deleteGrantedAuthority(com.sitewhere.grpc.service.
     * GDeleteGrantedAuthorityRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteGrantedAuthority(GDeleteGrantedAuthorityRequest request,
	    StreamObserver<GDeleteGrantedAuthorityResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_DELETE_GRANTED_AUTHORITY);
	    getUserMangagement().deleteGrantedAuthority(request.getName());
	    GDeleteGrantedAuthorityResponse.Builder response = GDeleteGrantedAuthorityResponse.newBuilder();
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_DELETE_GRANTED_AUTHORITY, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * getGrantedAuthoritiesForUser(com.sitewhere.grpc.service.
     * GGetGrantedAuthoritiesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getGrantedAuthoritiesForUser(GGetGrantedAuthoritiesRequest request,
	    StreamObserver<GGetGrantedAuthoritiesResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_GET_GRANTED_AUTHORITIES_FOR_USER);
	    List<IGrantedAuthority> apiResult = getUserMangagement().getGrantedAuthorities(request.getUsername());
	    GGetGrantedAuthoritiesResponse.Builder response = GGetGrantedAuthoritiesResponse.newBuilder();
	    for (IGrantedAuthority apiAuth : apiResult) {
		response.addAuthorities(UserModelConverter.asGrpcGrantedAuthority(apiAuth));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_GET_GRANTED_AUTHORITIES_FOR_USER, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * addGrantedAuthoritiesForUser(com.sitewhere.grpc.service.
     * GAddGrantedAuthoritiesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void addGrantedAuthoritiesForUser(GAddGrantedAuthoritiesRequest request,
	    StreamObserver<GAddGrantedAuthoritiesResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_ADD_GRANTED_AUTHORITIES_FOR_USER);
	    List<IGrantedAuthority> apiResult = getUserMangagement().addGrantedAuthorities(request.getUsername(),
		    request.getAuthoritiesList());
	    GAddGrantedAuthoritiesResponse.Builder response = GAddGrantedAuthoritiesResponse.newBuilder();
	    for (IGrantedAuthority apiAuth : apiResult) {
		response.addAuthorities(UserModelConverter.asGrpcGrantedAuthority(apiAuth));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_ADD_GRANTED_AUTHORITIES_FOR_USER, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.service.UserManagementGrpc.UserManagementImplBase#
     * removeGrantedAuthoritiesForUser(com.sitewhere.grpc.service.
     * GRemoveGrantedAuthoritiesRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void removeGrantedAuthoritiesForUser(GRemoveGrantedAuthoritiesRequest request,
	    StreamObserver<GRemoveGrantedAuthoritiesResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(UserManagementGrpc.METHOD_REMOVE_GRANTED_AUTHORITIES_FOR_USER);
	    List<IGrantedAuthority> apiResult = getUserMangagement().removeGrantedAuthorities(request.getUsername(),
		    request.getAuthoritiesList());
	    GRemoveGrantedAuthoritiesResponse.Builder response = GRemoveGrantedAuthoritiesResponse.newBuilder();
	    for (IGrantedAuthority apiAuth : apiResult) {
		response.addAuthorities(UserModelConverter.asGrpcGrantedAuthority(apiAuth));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.METHOD_REMOVE_GRANTED_AUTHORITIES_FOR_USER, e,
		    responseObserver);
	}
    }

    public IUserManagement getUserMangagement() {
	return userMangagement;
    }

    public void setUserMangagement(IUserManagement userMangagement) {
	this.userMangagement = userMangagement;
    }
}