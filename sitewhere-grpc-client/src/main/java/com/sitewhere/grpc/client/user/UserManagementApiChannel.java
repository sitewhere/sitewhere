/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.user;

import java.util.List;

import com.sitewhere.grpc.client.ApiChannel;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.client.IUserManagementApiChannel;
import com.sitewhere.grpc.service.GAddGrantedAuthoritiesRequest;
import com.sitewhere.grpc.service.GAddGrantedAuthoritiesResponse;
import com.sitewhere.grpc.service.GAuthenticateRequest;
import com.sitewhere.grpc.service.GAuthenticateResponse;
import com.sitewhere.grpc.service.GCreateGrantedAuthorityRequest;
import com.sitewhere.grpc.service.GCreateGrantedAuthorityResponse;
import com.sitewhere.grpc.service.GCreateUserRequest;
import com.sitewhere.grpc.service.GCreateUserResponse;
import com.sitewhere.grpc.service.GDeleteGrantedAuthorityRequest;
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
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IFunctionIdentifier;
import com.sitewhere.spi.microservice.MicroserviceIdentifier;
import com.sitewhere.spi.microservice.grpc.GrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier;
import com.sitewhere.spi.microservice.grpc.IGrpcSettings;
import com.sitewhere.spi.microservice.instance.IInstanceSettings;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * Supports SiteWhere user management APIs on top of a
 * {@link UserManagementGrpcChannel}.
 */
public class UserManagementApiChannel extends ApiChannel<UserManagementGrpcChannel>
	implements IUserManagementApiChannel<UserManagementGrpcChannel> {

    public UserManagementApiChannel(IInstanceSettings settings) {
	super(settings, MicroserviceIdentifier.InstanceManagement, GrpcServiceIdentifier.UserManagement,
		IGrpcSettings.USER_MANAGEMENT_API_PORT);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .microservice.instance.IInstanceSettings,
     * com.sitewhere.spi.microservice.IFunctionIdentifier,
     * com.sitewhere.spi.microservice.grpc.IGrpcServiceIdentifier, int)
     */
    @Override
    public UserManagementGrpcChannel createGrpcChannel(IInstanceSettings settings, IFunctionIdentifier identifier,
	    IGrpcServiceIdentifier grpcServiceIdentifier, int port) {
	return new UserManagementGrpcChannel(settings, identifier, grpcServiceIdentifier, port);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.user.IUserManagement#createUser(com.sitewhere.spi.user.
     * request.IUserCreateRequest, java.lang.Boolean)
     */
    @Override
    public IUser createUser(IUserCreateRequest request, Boolean encodePassword) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getCreateUserMethod());
	    GCreateUserRequest.Builder grequest = GCreateUserRequest.newBuilder();
	    grequest.setRequest(UserModelConverter.asGrpcUserCreateRequest(request));
	    grequest.setEncodePassword(((encodePassword != null) && (encodePassword == false)) ? false : true);
	    GCreateUserResponse gresponse = getGrpcChannel().getBlockingStub().createUser(grequest.build());
	    IUser response = UserModelConverter.asApiUser(gresponse.getUser());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.getCreateUserMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getCreateUserMethod(), t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.user.IUserManagement#importUser(com.sitewhere.spi.user.
     * IUser, boolean)
     */
    @Override
    public IUser importUser(IUser user, boolean overwrite) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getImportUserMethod());
	    GImportUserRequest.Builder grequest = GImportUserRequest.newBuilder();
	    grequest.setUser(UserModelConverter.asGrpcUser(user));
	    grequest.setOverwrite(overwrite);
	    GImportUserResponse gresponse = getGrpcChannel().getBlockingStub().importUser(grequest.build());
	    IUser response = UserModelConverter.asApiUser(gresponse.getUser());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.getImportUserMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getImportUserMethod(), t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.IUserManagement#authenticate(java.lang.String,
     * java.lang.String, boolean)
     */
    @Override
    public IUser authenticate(String username, String password, boolean updateLastLogin) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getAuthenticateMethod());
	    GAuthenticateRequest.Builder grequest = GAuthenticateRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.setPassword(password);
	    grequest.setUpdateLastLogin(updateLastLogin);
	    GAuthenticateResponse gresponse = getGrpcChannel().getBlockingStub().authenticate(grequest.build());
	    IUser response = UserModelConverter.asApiUser(gresponse.getUser());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.getAuthenticateMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getAuthenticateMethod(), t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.IUserManagement#updateUser(java.lang.String,
     * com.sitewhere.spi.user.request.IUserCreateRequest, boolean)
     */
    @Override
    public IUser updateUser(String username, IUserCreateRequest request, boolean encodePassword)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getUpdateUserMethod());
	    GUpdateUserRequest.Builder grequest = GUpdateUserRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.setRequest(UserModelConverter.asGrpcUserCreateRequest(request));
	    grequest.setEncodePassword(encodePassword);
	    GUpdateUserResponse gresponse = getGrpcChannel().getBlockingStub().updateUser(grequest.build());
	    IUser response = UserModelConverter.asApiUser(gresponse.getUser());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.getUpdateUserMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getUpdateUserMethod(), t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.IUserManagement#getUserByUsername(java.lang.
     * String)
     */
    @Override
    public IUser getUserByUsername(String username) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getGetUserByUsernameMethod());
	    GGetUserByUsernameRequest.Builder grequest = GGetUserByUsernameRequest.newBuilder();
	    grequest.setUsername(username);
	    GGetUserByUsernameResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getUserByUsername(grequest.build());
	    IUser response = (gresponse.hasUser()) ? UserModelConverter.asApiUser(gresponse.getUser()) : null;
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.getGetUserByUsernameMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getGetUserByUsernameMethod(), t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.IUserManagement#getGrantedAuthorities(java.lang.
     * String)
     */
    @Override
    public List<IGrantedAuthority> getGrantedAuthorities(String username) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getGetGrantedAuthoritiesForUserMethod());
	    GGetGrantedAuthoritiesRequest.Builder grequest = GGetGrantedAuthoritiesRequest.newBuilder();
	    grequest.setUsername(username);
	    GGetGrantedAuthoritiesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getGrantedAuthoritiesForUser(grequest.build());
	    List<IGrantedAuthority> response = UserModelConverter
		    .asApiGrantedAuthorities(gresponse.getAuthoritiesList());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.getGetGrantedAuthoritiesForUserMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getGetGrantedAuthoritiesForUserMethod(), t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.IUserManagement#addGrantedAuthorities(java.lang.
     * String, java.util.List)
     */
    @Override
    public List<IGrantedAuthority> addGrantedAuthorities(String username, List<String> authorities)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getAddGrantedAuthoritiesForUserMethod());
	    GAddGrantedAuthoritiesRequest.Builder grequest = GAddGrantedAuthoritiesRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.getAuthoritiesList().addAll(authorities);
	    GAddGrantedAuthoritiesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addGrantedAuthoritiesForUser(grequest.build());
	    List<IGrantedAuthority> response = UserModelConverter
		    .asApiGrantedAuthorities(gresponse.getAuthoritiesList());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.getAddGrantedAuthoritiesForUserMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getAddGrantedAuthoritiesForUserMethod(), t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.user.IUserManagement#removeGrantedAuthorities(java.lang
     * .String, java.util.List)
     */
    @Override
    public List<IGrantedAuthority> removeGrantedAuthorities(String username, List<String> authorities)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getRemoveGrantedAuthoritiesForUserMethod());
	    GRemoveGrantedAuthoritiesRequest.Builder grequest = GRemoveGrantedAuthoritiesRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.getAuthoritiesList().addAll(authorities);
	    GRemoveGrantedAuthoritiesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .removeGrantedAuthoritiesForUser(grequest.build());
	    List<IGrantedAuthority> response = UserModelConverter
		    .asApiGrantedAuthorities(gresponse.getAuthoritiesList());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.getRemoveGrantedAuthoritiesForUserMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getRemoveGrantedAuthoritiesForUserMethod(),
		    t);
	}
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#listUsers(com.sitewhere.spi.user.
     * IUserSearchCriteria)
     */
    @Override
    public ISearchResults<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getListUsersMethod());
	    GListUsersRequest.Builder grequest = GListUsersRequest.newBuilder();
	    grequest.setCriteria(UserModelConverter.asGrpcUserSearchCriteria(criteria));
	    GListUsersResponse gresponse = getGrpcChannel().getBlockingStub().listUsers(grequest.build());
	    ISearchResults<IUser> results = UserModelConverter.asApiUserSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.getListUsersMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getListUsersMethod(), t);
	}
    }

    /*
     * @see com.sitewhere.spi.user.IUserManagement#deleteUser(java.lang.String)
     */
    @Override
    public IUser deleteUser(String username) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getDeleteUserMethod());
	    GDeleteUserRequest.Builder grequest = GDeleteUserRequest.newBuilder();
	    grequest.setUsername(username);
	    GDeleteUserResponse gresponse = getGrpcChannel().getBlockingStub().deleteUser(grequest.build());
	    IUser response = UserModelConverter.asApiUser(gresponse.getUser());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.getDeleteUserMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getDeleteUserMethod(), t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.IUserManagement#createGrantedAuthority(com.
     * sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getCreateGrantedAuthorityMethod());
	    GCreateGrantedAuthorityRequest.Builder grequest = GCreateGrantedAuthorityRequest.newBuilder();
	    grequest.setRequest(UserModelConverter.asGrpcGrantedAuthorityCreateRequest(request));
	    GCreateGrantedAuthorityResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createGrantedAuthority(grequest.build());
	    IGrantedAuthority response = UserModelConverter.asApiGrantedAuthority(gresponse.getAuthority());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.getCreateGrantedAuthorityMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getCreateGrantedAuthorityMethod(), t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.IUserManagement#getGrantedAuthorityByName(java.
     * lang.String)
     */
    @Override
    public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getGetGrantedAuthorityByNameMethod());
	    GGetGrantedAuthorityByNameRequest.Builder grequest = GGetGrantedAuthorityByNameRequest.newBuilder();
	    grequest.setName(name);
	    GGetGrantedAuthorityByNameResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getGrantedAuthorityByName(grequest.build());
	    if (gresponse.hasAuthority()) {
		IGrantedAuthority response = UserModelConverter.asApiGrantedAuthority(gresponse.getAuthority());
		GrpcUtils.logClientMethodResponse(UserManagementGrpc.getGetGrantedAuthorityByNameMethod(), response);
		return response;
	    }
	    return null;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getGetGrantedAuthorityByNameMethod(), t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.IUserManagement#updateGrantedAuthority(java.lang.
     * String, com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getUpdateGrantedAuthorityMethod());
	    GUpdateGrantedAuthorityRequest.Builder grequest = GUpdateGrantedAuthorityRequest.newBuilder();
	    grequest.setName(name);
	    grequest.setRequest(UserModelConverter.asGrpcGrantedAuthorityCreateRequest(request));
	    GUpdateGrantedAuthorityResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateGrantedAuthority(grequest.build());
	    IGrantedAuthority response = UserModelConverter.asApiGrantedAuthority(gresponse.getAuthority());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.getUpdateGrantedAuthorityMethod(), response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getUpdateGrantedAuthorityMethod(), t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.user.IUserManagement#listGrantedAuthorities(com.sitewhere.
     * spi.user.IGrantedAuthoritySearchCriteria)
     */
    @Override
    public ISearchResults<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getListGrantedAuthoritiesMethod());
	    GListGrantedAuthoritiesRequest.Builder grequest = GListGrantedAuthoritiesRequest.newBuilder();
	    GListGrantedAuthoritiesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listGrantedAuthorities(grequest.build());
	    ISearchResults<IGrantedAuthority> results = UserModelConverter
		    .asApiGrantedAuthoritySearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.getListGrantedAuthoritiesMethod(), results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getListGrantedAuthoritiesMethod(), t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.IUserManagement#deleteGrantedAuthority(java.lang.
     * String)
     */
    @Override
    public void deleteGrantedAuthority(String authority) throws SiteWhereException {
	try {
	    GrpcUtils.handleClientMethodEntry(this, UserManagementGrpc.getDeleteGrantedAuthorityMethod());
	    GDeleteGrantedAuthorityRequest.Builder grequest = GDeleteGrantedAuthorityRequest.newBuilder();
	    grequest.setName(authority);
	    getGrpcChannel().getBlockingStub().deleteGrantedAuthority(grequest.build());
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.getDeleteGrantedAuthorityMethod(), t);
	}
    }
}