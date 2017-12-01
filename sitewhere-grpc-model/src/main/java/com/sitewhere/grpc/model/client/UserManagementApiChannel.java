/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.client;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.UserModelConverter;
import com.sitewhere.grpc.model.spi.client.IUserManagementApiChannel;
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
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.tracing.ITracerProvider;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * Supports SiteWhere user management APIs on top of a
 * {@link UserManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class UserManagementApiChannel extends ApiChannel<UserManagementGrpcChannel>
	implements IUserManagementApiChannel {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public UserManagementApiChannel(IMicroservice microservice, String host) {
	super(microservice, host);
    }

    /*
     * @see
     * com.sitewhere.grpc.model.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi.
     * tracing.ITracerProvider, java.lang.String, int)
     */
    @Override
    public GrpcChannel createGrpcChannel(ITracerProvider tracerProvider, String host) {
	return new UserManagementGrpcChannel(tracerProvider, host,
		getMicroservice().getInstanceSettings().getGrpcPort());
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
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_CREATE_USER);
	    GCreateUserRequest.Builder grequest = GCreateUserRequest.newBuilder();
	    grequest.setRequest(UserModelConverter.asGrpcUserCreateRequest(request));
	    grequest.setEncodePassword(((encodePassword != null) && (encodePassword == false)) ? false : true);
	    GCreateUserResponse gresponse = getGrpcChannel().getBlockingStub().createUser(grequest.build());
	    IUser response = UserModelConverter.asApiUser(gresponse.getUser());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_CREATE_USER, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_CREATE_USER, t);
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
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_IMPORT_USER);
	    GImportUserRequest.Builder grequest = GImportUserRequest.newBuilder();
	    grequest.setUser(UserModelConverter.asGrpcUser(user));
	    grequest.setOverwrite(overwrite);
	    GImportUserResponse gresponse = getGrpcChannel().getBlockingStub().importUser(grequest.build());
	    IUser response = UserModelConverter.asApiUser(gresponse.getUser());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_IMPORT_USER, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_IMPORT_USER, t);
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
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_AUTHENTICATE);
	    GAuthenticateRequest.Builder grequest = GAuthenticateRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.setPassword(password);
	    grequest.setUpdateLastLogin(updateLastLogin);
	    GAuthenticateResponse gresponse = getGrpcChannel().getBlockingStub().authenticate(grequest.build());
	    IUser response = UserModelConverter.asApiUser(gresponse.getUser());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_AUTHENTICATE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_AUTHENTICATE, t);
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
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_UPDATE_USER);
	    GUpdateUserRequest.Builder grequest = GUpdateUserRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.setRequest(UserModelConverter.asGrpcUserCreateRequest(request));
	    grequest.setEncodePassword(encodePassword);
	    GUpdateUserResponse gresponse = getGrpcChannel().getBlockingStub().updateUser(grequest.build());
	    IUser response = UserModelConverter.asApiUser(gresponse.getUser());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_UPDATE_USER, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_UPDATE_USER, t);
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
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_GET_USER_BY_USERNAME);
	    GGetUserByUsernameRequest.Builder grequest = GGetUserByUsernameRequest.newBuilder();
	    grequest.setUsername(username);
	    GGetUserByUsernameResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getUserByUsername(grequest.build());
	    IUser response = (gresponse.hasUser()) ? UserModelConverter.asApiUser(gresponse.getUser()) : null;
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_GET_USER_BY_USERNAME, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_GET_USER_BY_USERNAME, t);
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
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_GET_GRANTED_AUTHORITIES_FOR_USER);
	    GGetGrantedAuthoritiesRequest.Builder grequest = GGetGrantedAuthoritiesRequest.newBuilder();
	    grequest.setUsername(username);
	    GGetGrantedAuthoritiesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getGrantedAuthoritiesForUser(grequest.build());
	    List<IGrantedAuthority> response = UserModelConverter
		    .asApiGrantedAuthorities(gresponse.getAuthoritiesList());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_GET_GRANTED_AUTHORITIES_FOR_USER, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_GET_GRANTED_AUTHORITIES_FOR_USER, t);
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
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_ADD_GRANTED_AUTHORITIES_FOR_USER);
	    GAddGrantedAuthoritiesRequest.Builder grequest = GAddGrantedAuthoritiesRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.getAuthoritiesList().addAll(authorities);
	    GAddGrantedAuthoritiesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addGrantedAuthoritiesForUser(grequest.build());
	    List<IGrantedAuthority> response = UserModelConverter
		    .asApiGrantedAuthorities(gresponse.getAuthoritiesList());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_ADD_GRANTED_AUTHORITIES_FOR_USER, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_ADD_GRANTED_AUTHORITIES_FOR_USER, t);
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
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_REMOVE_GRANTED_AUTHORITIES_FOR_USER);
	    GRemoveGrantedAuthoritiesRequest.Builder grequest = GRemoveGrantedAuthoritiesRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.getAuthoritiesList().addAll(authorities);
	    GRemoveGrantedAuthoritiesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .removeGrantedAuthoritiesForUser(grequest.build());
	    List<IGrantedAuthority> response = UserModelConverter
		    .asApiGrantedAuthorities(gresponse.getAuthoritiesList());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_REMOVE_GRANTED_AUTHORITIES_FOR_USER, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_REMOVE_GRANTED_AUTHORITIES_FOR_USER,
		    t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.IUserManagement#listUsers(com.sitewhere.spi.user.
     * IUserSearchCriteria)
     */
    @Override
    public List<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_LIST_USERS);
	    GListUsersRequest.Builder grequest = GListUsersRequest.newBuilder();
	    grequest.setCriteria(UserModelConverter.asGrpcUserSearchCriteria(criteria));
	    GListUsersResponse gresponse = getGrpcChannel().getBlockingStub().listUsers(grequest.build());
	    List<IUser> response = UserModelConverter.asApiUsers(gresponse.getUserList());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_LIST_USERS, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_LIST_USERS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.IUserManagement#deleteUser(java.lang.String,
     * boolean)
     */
    @Override
    public IUser deleteUser(String username, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_DELETE_USER);
	    GDeleteUserRequest.Builder grequest = GDeleteUserRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.setForce(force);
	    GDeleteUserResponse gresponse = getGrpcChannel().getBlockingStub().deleteUser(grequest.build());
	    IUser response = UserModelConverter.asApiUser(gresponse.getUser());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_DELETE_USER, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_DELETE_USER, t);
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
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_CREATE_GRANTED_AUTHORITY);
	    GCreateGrantedAuthorityRequest.Builder grequest = GCreateGrantedAuthorityRequest.newBuilder();
	    grequest.setRequest(UserModelConverter.asGrpcGrantedAuthorityCreateRequest(request));
	    GCreateGrantedAuthorityResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createGrantedAuthority(grequest.build());
	    IGrantedAuthority response = UserModelConverter.asApiGrantedAuthority(gresponse.getAuthority());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_GET_GRANTED_AUTHORITY_BY_NAME, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_CREATE_GRANTED_AUTHORITY, t);
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
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_GET_GRANTED_AUTHORITY_BY_NAME);
	    GGetGrantedAuthorityByNameRequest.Builder grequest = GGetGrantedAuthorityByNameRequest.newBuilder();
	    grequest.setName(name);
	    GGetGrantedAuthorityByNameResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getGrantedAuthorityByName(grequest.build());
	    if (gresponse.hasAuthority()) {
		IGrantedAuthority response = UserModelConverter.asApiGrantedAuthority(gresponse.getAuthority());
		GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_GET_GRANTED_AUTHORITY_BY_NAME, response);
		return response;
	    }
	    return null;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_GET_GRANTED_AUTHORITY_BY_NAME, t);
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
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_UPDATE_GRANTED_AUTHORITY);
	    GUpdateGrantedAuthorityRequest.Builder grequest = GUpdateGrantedAuthorityRequest.newBuilder();
	    grequest.setName(name);
	    grequest.setRequest(UserModelConverter.asGrpcGrantedAuthorityCreateRequest(request));
	    GUpdateGrantedAuthorityResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateGrantedAuthority(grequest.build());
	    IGrantedAuthority response = UserModelConverter.asApiGrantedAuthority(gresponse.getAuthority());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_UPDATE_GRANTED_AUTHORITY, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_UPDATE_GRANTED_AUTHORITY, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.user.IUserManagement#listGrantedAuthorities(com.
     * sitewhere.spi.user.IGrantedAuthoritySearchCriteria)
     */
    @Override
    public List<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_LIST_GRANTED_AUTHORITIES);
	    GListGrantedAuthoritiesRequest.Builder grequest = GListGrantedAuthoritiesRequest.newBuilder();
	    GListGrantedAuthoritiesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listGrantedAuthorities(grequest.build());
	    List<IGrantedAuthority> response = UserModelConverter
		    .asApiGrantedAuthorities(gresponse.getAuthoritiesList());
	    GrpcUtils.logClientMethodResponse(UserManagementGrpc.METHOD_LIST_GRANTED_AUTHORITIES, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_LIST_GRANTED_AUTHORITIES, t);
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
	    GrpcUtils.logClientMethodEntry(UserManagementGrpc.METHOD_DELETE_GRANTED_AUTHORITY);
	    GDeleteGrantedAuthorityRequest.Builder grequest = GDeleteGrantedAuthorityRequest.newBuilder();
	    grequest.setName(authority);
	    GDeleteGrantedAuthorityResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteGrantedAuthority(grequest.build());
	    return;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(UserManagementGrpc.METHOD_DELETE_GRANTED_AUTHORITY, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Logger getLogger() {
	return LOGGER;
    }
}