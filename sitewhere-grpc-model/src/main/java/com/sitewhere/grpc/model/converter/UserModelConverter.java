package com.sitewhere.grpc.model.converter;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.grpc.model.CommonModel.GGrantedAuthorityReference;
import com.sitewhere.grpc.model.UserModel;
import com.sitewhere.grpc.model.UserModel.GGrantedAuthority;
import com.sitewhere.grpc.model.UserModel.GGrantedAuthorityCreateRequest;
import com.sitewhere.grpc.model.UserModel.GUser;
import com.sitewhere.grpc.model.UserModel.GUserAccountStatus;
import com.sitewhere.grpc.model.UserModel.GUserCreateRequest;
import com.sitewhere.grpc.model.UserModel.GUserSearchCriteria;
import com.sitewhere.rest.model.user.GrantedAuthority;
import com.sitewhere.rest.model.user.User;
import com.sitewhere.rest.model.user.request.GrantedAuthorityCreateRequest;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.AccountStatus;
import com.sitewhere.spi.user.IGrantedAuthority;
import com.sitewhere.spi.user.IUser;
import com.sitewhere.spi.user.IUserSearchCriteria;
import com.sitewhere.spi.user.request.IGrantedAuthorityCreateRequest;
import com.sitewhere.spi.user.request.IUserCreateRequest;

/**
 * Convert user entities between SiteWhere API model and GRPC model.
 * 
 * @author Derek
 */
public class UserModelConverter {

    /**
     * Convert account status from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static AccountStatus asApiAccountStatus(GUserAccountStatus grpc) throws SiteWhereException {
	switch (grpc) {
	case USER_STATUS_ACTIVE:
	    return AccountStatus.Active;
	case USER_STATUS_EXPIRED:
	    return AccountStatus.Expired;
	case USER_STATUS_LOCKED:
	    return AccountStatus.Locked;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown account status: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert account status from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GUserAccountStatus asGrpcAccountStatus(AccountStatus api) throws SiteWhereException {
	switch (api) {
	case Active:
	    return GUserAccountStatus.USER_STATUS_ACTIVE;
	case Expired:
	    return GUserAccountStatus.USER_STATUS_EXPIRED;
	case Locked:
	    return GUserAccountStatus.USER_STATUS_LOCKED;
	}
	throw new SiteWhereException("Unknown account status: " + api.name());
    }

    /**
     * Convert user create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IUserCreateRequest asApiUserCreateRequest(GUserCreateRequest grpc) throws SiteWhereException {
	UserCreateRequest api = new UserCreateRequest();
	api.setUsername(grpc.getUsername());
	api.setPassword(grpc.getPassword());
	api.setFirstName(grpc.getFirstName());
	api.setLastName(grpc.getLastName());
	api.setStatus(UserModelConverter.asApiAccountStatus(grpc.getStatus()));
	api.setAuthorities(grpc.getAuthoritiesList());
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert user create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GUserCreateRequest asGrpcUserCreateRequest(IUserCreateRequest api) throws SiteWhereException {
	GUserCreateRequest.Builder builder = GUserCreateRequest.newBuilder();
	builder.setUsername(api.getUsername());
	builder.setPassword(api.getPassword());
	builder.setFirstName(api.getFirstName());
	builder.setLastName(api.getLastName());
	builder.setStatus(UserModelConverter.asGrpcAccountStatus(api.getStatus()));
	builder.addAllAuthorities(api.getAuthorities());
	builder.putAllMetadata(api.getMetadata());
	return builder.build();
    }

    /**
     * Convert user from API to GRPC.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IUser asApiUser(GUser grpc) throws SiteWhereException {
	User api = new User();
	api.setUsername(grpc.getUsername());
	api.setHashedPassword(grpc.getHashedPassword());
	api.setFirstName(grpc.getFirstName());
	api.setLastName(grpc.getLastName());
	api.setStatus(UserModelConverter.asApiAccountStatus(grpc.getStatus()));
	api.setLastLogin((grpc.hasLastLogin()) ? CommonModelConverter.asDate(grpc.getLastLogin()) : null);
	api.setAuthorities(grpc.getAuthoritiesList());
	api.setMetadata(grpc.getMetadataMap());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert user from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GUser asGrpcUser(IUser api) throws SiteWhereException {
	GUser.Builder builder = UserModel.GUser.newBuilder();
	builder.setUsername(api.getUsername());
	builder.setHashedPassword(api.getHashedPassword());
	builder.setFirstName(api.getFirstName());
	builder.setLastName(api.getLastName());
	builder.setStatus(UserModelConverter.asGrpcAccountStatus(api.getStatus()));
	if (api.getLastLogin() != null) {
	    builder.setLastLogin(CommonModelConverter.asGrpcTimestamp(api.getLastLogin()));
	}
	if (api.getAuthorities() != null) {
	    builder.addAllAuthorities(api.getAuthorities());
	}
	if (api.getMetadata() != null) {
	    builder.putAllMetadata(api.getMetadata());
	}
	builder.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return builder.build();
    }

    /**
     * Convert user search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GUserSearchCriteria asGrpcUserSearchCriteria(IUserSearchCriteria api) throws SiteWhereException {
	GUserSearchCriteria.Builder builder = GUserSearchCriteria.newBuilder();
	builder.setIncludeDeleted(api.isIncludeDeleted());
	return builder.build();
    }

    /**
     * Convert a list of users from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<IUser> asApiUsers(List<GUser> grpcs) throws SiteWhereException {
	List<IUser> api = new ArrayList<IUser>();
	for (GUser guser : grpcs) {
	    api.add(UserModelConverter.asApiUser(guser));
	}
	return api;
    }

    /**
     * Convert granted authority create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IGrantedAuthorityCreateRequest asApiGrantedAuthorityCreateRequest(GGrantedAuthorityCreateRequest grpc)
	    throws SiteWhereException {
	GrantedAuthorityCreateRequest api = new GrantedAuthorityCreateRequest();
	api.setAuthority(grpc.getAuthority());
	api.setDescription(grpc.getDescription());
	api.setParent(grpc.hasParent() ? grpc.getParent().getAuthority() : null);
	api.setGroup(grpc.getGroup());
	return api;
    }

    /**
     * Convert granted authority create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GGrantedAuthorityCreateRequest asGrpcGrantedAuthorityCreateRequest(IGrantedAuthorityCreateRequest api)
	    throws SiteWhereException {
	GGrantedAuthorityCreateRequest.Builder builder = GGrantedAuthorityCreateRequest.newBuilder();
	builder.setAuthority(api.getAuthority());
	builder.setDescription(api.getDescription());
	if (api.getParent() != null) {
	    GGrantedAuthorityReference.Builder parent = GGrantedAuthorityReference.newBuilder();
	    parent.setAuthority(api.getParent());
	    builder.setParent(parent);
	}
	builder.setGroup(api.isGroup());
	return builder.build();
    }

    /**
     * Convert granted authority from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static IGrantedAuthority asApiGrantedAuthority(GGrantedAuthority grpc) throws SiteWhereException {
	GrantedAuthority api = new GrantedAuthority();
	api.setAuthority(grpc.getAuthority());
	api.setDescription(grpc.getDescription());
	api.setParent(grpc.hasParent() ? grpc.getParent().getAuthority() : null);
	api.setGroup(grpc.getGroup());
	return api;
    }

    /**
     * Convert a list of granted authorities from GRPC to API.
     * 
     * @param grpcs
     * @return
     * @throws SiteWhereException
     */
    public static List<IGrantedAuthority> asApiGrantedAuthorities(List<GGrantedAuthority> grpcs)
	    throws SiteWhereException {
	List<IGrantedAuthority> api = new ArrayList<IGrantedAuthority>();
	for (GGrantedAuthority gauth : grpcs) {
	    api.add(UserModelConverter.asApiGrantedAuthority(gauth));
	}
	return api;
    }

    /**
     * Convert a granted authority from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GGrantedAuthority asGrpcGrantedAuthority(IGrantedAuthority api) throws SiteWhereException {
	GGrantedAuthority.Builder builder = UserModel.GGrantedAuthority.newBuilder();
	builder.setAuthority(api.getAuthority());
	builder.setDescription(api.getDescription());
	if (api.getParent() != null) {
	    GGrantedAuthorityReference.Builder parent = GGrantedAuthorityReference.newBuilder();
	    parent.setAuthority(api.getParent());
	    builder.setParent(parent);
	}
	builder.setGroup(api.isGroup());
	return builder.build();
    }
}