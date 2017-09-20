package com.sitewhere.grpc.model.client;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.converter.UserModelConverter;
import com.sitewhere.grpc.service.GCreateUserRequest;
import com.sitewhere.grpc.service.GCreateUserResponse;
import com.sitewhere.server.lifecycle.LifecycleComponent;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IGrantedAuthoritySearchCriteria;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserManagement;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * Supports SiteWhere user management APIs on top of a
 * {@link UserManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class UserManagementApiChannel extends LifecycleComponent implements IUserManagement {

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
     * @see
     * com.sitewhere.spi.user.IUserManagement#createUser(com.sitewhere.spi.user.
     * request.IUserCreateRequest, boolean)
     */
    @Override
    public IUser createUser(IUserCreateRequest request, boolean encodePassword) throws SiteWhereException {
	GCreateUserRequest.Builder grequest = GCreateUserRequest.newBuilder();
	grequest.setRequest(UserModelConverter.asGrpcUserCreateRequest(request));
	GCreateUserResponse gresponse = getGrpcChannel().getBlockingStub().createUser(grequest.build());
	return UserModelConverter.asApiUser(gresponse.getUser());
    }

    @Override
    public IUser importUser(IUser user, boolean overwrite) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IUser authenticate(String username, String password, boolean updateLastLogin) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IUser updateUser(String username, IUserCreateRequest request, boolean encodePassword)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IUser getUserByUsername(String username) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<IGrantedAuthority> getGrantedAuthorities(String username) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<IGrantedAuthority> addGrantedAuthorities(String username, List<String> authorities)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<IGrantedAuthority> removeGrantedAuthorities(String username, List<String> authorities)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<IUser> listUsers(IUserSearchCriteria criteria) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IUser deleteUser(String username, boolean force) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IGrantedAuthority createGrantedAuthority(IGrantedAuthorityCreateRequest request) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IGrantedAuthority getGrantedAuthorityByName(String name) throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public IGrantedAuthority updateGrantedAuthority(String name, IGrantedAuthorityCreateRequest request)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<IGrantedAuthority> listGrantedAuthorities(IGrantedAuthoritySearchCriteria criteria)
	    throws SiteWhereException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void deleteGrantedAuthority(String authority) throws SiteWhereException {
	// TODO Auto-generated method stub

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