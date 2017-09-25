package com.sitewhere.grpc.model.client;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.converter.UserModelConverter;
import com.sitewhere.grpc.model.spi.ApiNotAvailableException;
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
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
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
public class UserManagementApiChannel extends LifecycleComponent implements IUserManagementApiChannel {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** User management GRPC channel */
    private UserManagementGrpcChannel grpcChannel;

    public UserManagementApiChannel(UserManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.spi.IApiChannel#waitForApiAvailable(long,
     * java.util.concurrent.TimeUnit)
     */
    @Override
    public void waitForApiAvailable(long duration, TimeUnit unit) throws ApiNotAvailableException {
	try {
	    GListGrantedAuthoritiesRequest.Builder request = GListGrantedAuthoritiesRequest.newBuilder();
	    getGrpcChannel().createBlockingStub().withDeadlineAfter(duration, unit)
		    .listGrantedAuthorities(request.build());
	} catch (Exception e) {
	    throw new ApiNotAvailableException("Error waiting for API to become available.", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.user.IUserManagement#createUser(com.sitewhere.spi.user.
     * request.IUserCreateRequest, boolean)
     */
    @Override
    public IUser createUser(IUserCreateRequest request, boolean encodePassword) throws SiteWhereException {
	try {
	    GCreateUserRequest.Builder grequest = GCreateUserRequest.newBuilder();
	    grequest.setRequest(UserModelConverter.asGrpcUserCreateRequest(request));
	    GCreateUserResponse gresponse = getGrpcChannel().getBlockingStub().createUser(grequest.build());
	    return UserModelConverter.asApiUser(gresponse.getUser());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:createUser failed.", t);
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
	    GImportUserRequest.Builder grequest = GImportUserRequest.newBuilder();
	    grequest.setUser(UserModelConverter.asGrpcUser(user));
	    grequest.setOverwrite(overwrite);
	    GImportUserResponse gresponse = getGrpcChannel().getBlockingStub().importUser(grequest.build());
	    return UserModelConverter.asApiUser(gresponse.getUser());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:importUser failed.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.user.IUserManagement#authenticate(java.lang.String,
     * java.lang.String, boolean)
     */
    @Override
    public IUser authenticate(String username, String password, boolean updateLastLogin) throws SiteWhereException {
	try {
	    GAuthenticateRequest.Builder grequest = GAuthenticateRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.setPassword(password);
	    grequest.setUpdateLastLogin(updateLastLogin);
	    GAuthenticateResponse gresponse = getGrpcChannel().getBlockingStub().authenticate(grequest.build());
	    return UserModelConverter.asApiUser(gresponse.getUser());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:authenticate failed.", t);
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
	    GUpdateUserRequest.Builder grequest = GUpdateUserRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.setRequest(UserModelConverter.asGrpcUserCreateRequest(request));
	    grequest.setEncodePassword(encodePassword);
	    GUpdateUserResponse gresponse = getGrpcChannel().getBlockingStub().updateUser(grequest.build());
	    return UserModelConverter.asApiUser(gresponse.getUser());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:updateUser failed.", t);
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
	    GGetUserByUsernameRequest.Builder grequest = GGetUserByUsernameRequest.newBuilder();
	    grequest.setUsername(username);
	    GGetUserByUsernameResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getUserByUsername(grequest.build());
	    return UserModelConverter.asApiUser(gresponse.getUser());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:getUserByUsername failed.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.user.IUserManagement#getGrantedAuthorities(java.lang.
     * String)
     */
    @Override
    public List<IGrantedAuthority> getGrantedAuthorities(String username) throws SiteWhereException {
	try {
	    GGetGrantedAuthoritiesRequest.Builder grequest = GGetGrantedAuthoritiesRequest.newBuilder();
	    grequest.setUsername(username);
	    GGetGrantedAuthoritiesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getGrantedAuthoritiesForUser(grequest.build());
	    return UserModelConverter.asApiGrantedAuthorities(gresponse.getAuthoritiesList());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:getGrantedAuthorities failed.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.user.IUserManagement#addGrantedAuthorities(java.lang.
     * String, java.util.List)
     */
    @Override
    public List<IGrantedAuthority> addGrantedAuthorities(String username, List<String> authorities)
	    throws SiteWhereException {
	try {
	    GAddGrantedAuthoritiesRequest.Builder grequest = GAddGrantedAuthoritiesRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.getAuthoritiesList().addAll(authorities);
	    GAddGrantedAuthoritiesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .addGrantedAuthoritiesForUser(grequest.build());
	    return UserModelConverter.asApiGrantedAuthorities(gresponse.getAuthoritiesList());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:addGrantedAuthorities failed.", t);
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
	    GRemoveGrantedAuthoritiesRequest.Builder grequest = GRemoveGrantedAuthoritiesRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.getAuthoritiesList().addAll(authorities);
	    GRemoveGrantedAuthoritiesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .removeGrantedAuthoritiesForUser(grequest.build());
	    return UserModelConverter.asApiGrantedAuthorities(gresponse.getAuthoritiesList());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:removeGrantedAuthorities failed.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.user.IUserManagement#listUsers(com.sitewhere.spi.user.
     * IUserSearchCriteria)
     */
    @Override
    public List<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
	try {
	    GListUsersRequest.Builder grequest = GListUsersRequest.newBuilder();
	    grequest.setCriteria(UserModelConverter.asGrpcUserSearchCriteria(criteria));
	    GListUsersResponse gresponse = getGrpcChannel().getBlockingStub().listUsers(grequest.build());
	    return UserModelConverter.asApiUsers(gresponse.getUserList());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:listUsers failed.", t);
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
	    GDeleteUserRequest.Builder grequest = GDeleteUserRequest.newBuilder();
	    grequest.setUsername(username);
	    grequest.setForce(force);
	    GDeleteUserResponse gresponse = getGrpcChannel().getBlockingStub().deleteUser(grequest.build());
	    return UserModelConverter.asApiUser(gresponse.getUser());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:deleteUser failed.", t);
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
	    GCreateGrantedAuthorityRequest.Builder grequest = GCreateGrantedAuthorityRequest.newBuilder();
	    grequest.setRequest(UserModelConverter.asGrpcGrantedAuthorityCreateRequest(request));
	    GCreateGrantedAuthorityResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createGrantedAuthority(grequest.build());
	    return UserModelConverter.asApiGrantedAuthority(gresponse.getAuthority());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:createGrantedAuthority failed.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.user.IUserManagement#getGrantedAuthorityByName(java.
     * lang.String)
     */
    @Override
    public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
	try {
	    GGetGrantedAuthorityByNameRequest.Builder grequest = GGetGrantedAuthorityByNameRequest.newBuilder();
	    grequest.setName(name);
	    GGetGrantedAuthorityByNameResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getGrantedAuthorityByName(grequest.build());
	    return UserModelConverter.asApiGrantedAuthority(gresponse.getAuthority());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:getGrantedAuthorityByName failed.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.user.IUserManagement#updateGrantedAuthority(java.lang.
     * String, com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest)
     */
    @Override
    public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException {
	try {
	    GUpdateGrantedAuthorityRequest.Builder grequest = GUpdateGrantedAuthorityRequest.newBuilder();
	    grequest.setName(name);
	    grequest.setRequest(UserModelConverter.asGrpcGrantedAuthorityCreateRequest(request));
	    GUpdateGrantedAuthorityResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateGrantedAuthority(grequest.build());
	    return UserModelConverter.asApiGrantedAuthority(gresponse.getAuthority());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:updateGrantedAuthority failed.", t);
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
	    GListGrantedAuthoritiesRequest.Builder grequest = GListGrantedAuthoritiesRequest.newBuilder();
	    GListGrantedAuthoritiesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listGrantedAuthorities(grequest.build());
	    return UserModelConverter.asApiGrantedAuthorities(gresponse.getAuthoritiesList());
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:listGrantedAuthorities failed.", t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.user.IUserManagement#deleteGrantedAuthority(java.lang.
     * String)
     */
    @Override
    public void deleteGrantedAuthority(String authority) throws SiteWhereException {
	try {
	    GDeleteGrantedAuthorityRequest.Builder grequest = GDeleteGrantedAuthorityRequest.newBuilder();
	    grequest.setName(authority);
	    GDeleteGrantedAuthorityResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteGrantedAuthority(grequest.build());
	    return;
	} catch (Throwable t) {
	    throw new SiteWhereException("Call to UserManagement:deleteGrantedAuthority failed.", t);
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

    public UserManagementGrpcChannel getGrpcChannel() {
	return grpcChannel;
    }

    public void setGrpcChannel(UserManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
    }
}