/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.instance.user.grpc;

import java.util.List;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.grpc.client.user.UserModelConverter;
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
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.rest.model.search.user.UserSearchCriteria;
import com.sitewhere.rest.model.user.GrantedAuthoritySearchCriteria;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.search.ISearchResults;
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
public class UserManagementImpl extends UserManagementGrpc.UserManagementImplBase implements IGrpcApiImplementation {

    /** Parent microservice */
    private IInstanceManagementMicroservice<?> microservice;

    /** User management persistence */
    private IUserManagement userMangagement;

    public UserManagementImpl(IInstanceManagementMicroservice<?> microservice, IUserManagement userMangagement) {
	this.microservice = microservice;
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getCreateUserMethod());
	    IUserCreateRequest apiRequest = UserModelConverter.asApiUserCreateRequest(request.getRequest());
	    IUser apiResult = getUserMangagement().createUser(apiRequest, request.getEncodePassword());
	    GCreateUserResponse.Builder response = GCreateUserResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getCreateUserMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getCreateUserMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getImportUserMethod());
	    IUser apiUser = UserModelConverter.asApiUser(request.getUser());
	    IUser apiResult = getUserMangagement().importUser(apiUser, request.getOverwrite());
	    GImportUserResponse.Builder response = GImportUserResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getImportUserMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getImportUserMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getAuthenticateMethod());
	    IUser apiResult = getUserMangagement().authenticate(request.getUsername(), request.getPassword(),
		    request.getUpdateLastLogin());
	    GAuthenticateResponse.Builder response = GAuthenticateResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getAuthenticateMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getAuthenticateMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getUpdateUserMethod());
	    IUserCreateRequest apiRequest = UserModelConverter.asApiUserCreateRequest(request.getRequest());
	    IUser apiResult = getUserMangagement().updateUser(request.getUsername(), apiRequest,
		    request.getEncodePassword());
	    GUpdateUserResponse.Builder response = GUpdateUserResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getUpdateUserMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getUpdateUserMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getGetUserByUsernameMethod());
	    IUser apiResult = getUserMangagement().getUserByUsername(request.getUsername());
	    GGetUserByUsernameResponse.Builder response = GGetUserByUsernameResponse.newBuilder();
	    if (apiResult != null) {
		response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getGetUserByUsernameMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getGetUserByUsernameMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getListUsersMethod());
	    UserSearchCriteria criteria = new UserSearchCriteria();
	    ISearchResults<IUser> apiResult = getUserMangagement().listUsers(criteria);
	    GListUsersResponse.Builder response = GListUsersResponse.newBuilder();
	    response.setResults(UserModelConverter.asGrpcUserSearchResults(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getListUsersMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getListUsersMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getDeleteUserMethod());
	    IUser apiResult = getUserMangagement().deleteUser(request.getUsername());
	    GDeleteUserResponse.Builder response = GDeleteUserResponse.newBuilder();
	    response.setUser(UserModelConverter.asGrpcUser(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getDeleteUserMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getDeleteUserMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getCreateGrantedAuthorityMethod());
	    IGrantedAuthority apiResult = getUserMangagement().createGrantedAuthority(
		    UserModelConverter.asApiGrantedAuthorityCreateRequest(request.getRequest()));
	    GCreateGrantedAuthorityResponse.Builder response = GCreateGrantedAuthorityResponse.newBuilder();
	    response.setAuthority(UserModelConverter.asGrpcGrantedAuthority(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getCreateGrantedAuthorityMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getCreateGrantedAuthorityMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getGetGrantedAuthorityByNameMethod());
	    IGrantedAuthority apiResult = getUserMangagement().getGrantedAuthorityByName(request.getName());
	    GGetGrantedAuthorityByNameResponse.Builder response = GGetGrantedAuthorityByNameResponse.newBuilder();
	    if (apiResult != null) {
		response.setAuthority(UserModelConverter.asGrpcGrantedAuthority(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getGetGrantedAuthorityByNameMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getGetGrantedAuthorityByNameMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getUpdateGrantedAuthorityMethod());
	    IGrantedAuthority apiResult = getUserMangagement().updateGrantedAuthority(request.getName(),
		    UserModelConverter.asApiGrantedAuthorityCreateRequest(request.getRequest()));
	    GUpdateGrantedAuthorityResponse.Builder response = GUpdateGrantedAuthorityResponse.newBuilder();
	    response.setAuthority(UserModelConverter.asGrpcGrantedAuthority(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getUpdateGrantedAuthorityMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getUpdateGrantedAuthorityMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getListGrantedAuthoritiesMethod());
	    ISearchResults<IGrantedAuthority> apiResult = getUserMangagement()
		    .listGrantedAuthorities(new GrantedAuthoritySearchCriteria());
	    GListGrantedAuthoritiesResponse.Builder response = GListGrantedAuthoritiesResponse.newBuilder();
	    response.setResults(UserModelConverter.asGrpcGrantedAuthoritySearchResults(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getListGrantedAuthoritiesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getListGrantedAuthoritiesMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getDeleteGrantedAuthorityMethod());
	    getUserMangagement().deleteGrantedAuthority(request.getName());
	    GDeleteGrantedAuthorityResponse.Builder response = GDeleteGrantedAuthorityResponse.newBuilder();
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getDeleteGrantedAuthorityMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getDeleteGrantedAuthorityMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getGetGrantedAuthoritiesForUserMethod());
	    List<IGrantedAuthority> apiResult = getUserMangagement().getGrantedAuthorities(request.getUsername());
	    GGetGrantedAuthoritiesResponse.Builder response = GGetGrantedAuthoritiesResponse.newBuilder();
	    for (IGrantedAuthority apiAuth : apiResult) {
		response.addAuthorities(UserModelConverter.asGrpcGrantedAuthority(apiAuth));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getGetGrantedAuthoritiesForUserMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getGetGrantedAuthoritiesForUserMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getAddGrantedAuthoritiesForUserMethod());
	    List<IGrantedAuthority> apiResult = getUserMangagement().addGrantedAuthorities(request.getUsername(),
		    request.getAuthoritiesList());
	    GAddGrantedAuthoritiesResponse.Builder response = GAddGrantedAuthoritiesResponse.newBuilder();
	    for (IGrantedAuthority apiAuth : apiResult) {
		response.addAuthorities(UserModelConverter.asGrpcGrantedAuthority(apiAuth));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getAddGrantedAuthoritiesForUserMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getAddGrantedAuthoritiesForUserMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, UserManagementGrpc.getRemoveGrantedAuthoritiesForUserMethod());
	    List<IGrantedAuthority> apiResult = getUserMangagement().removeGrantedAuthorities(request.getUsername(),
		    request.getAuthoritiesList());
	    GRemoveGrantedAuthoritiesResponse.Builder response = GRemoveGrantedAuthoritiesResponse.newBuilder();
	    for (IGrantedAuthority apiAuth : apiResult) {
		response.addAuthorities(UserModelConverter.asGrpcGrantedAuthority(apiAuth));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(UserManagementGrpc.getRemoveGrantedAuthoritiesForUserMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(UserManagementGrpc.getRemoveGrantedAuthoritiesForUserMethod());
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation#getMicroservice()
     */
    @Override
    public IMicroservice<?> getMicroservice() {
	return microservice;
    }

    protected IUserManagement getUserMangagement() {
	return userMangagement;
    }
}