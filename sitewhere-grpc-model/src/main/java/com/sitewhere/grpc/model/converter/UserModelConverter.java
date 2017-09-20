package com.sitewhere.grpc.model.converter;

import com.sitewhere.grpc.model.UserModel;
import com.sitewhere.grpc.model.UserModel.GUser;
import com.sitewhere.grpc.model.UserModel.GUserAccountStatus;
import com.sitewhere.grpc.model.UserModel.GUserCreateRequest;
import com.sitewhere.rest.model.user.request.UserCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.user.AccountStatus;
import com.sitewhere.spi.user.IUser;
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
	case ACTIVE:
	    return AccountStatus.Active;
	case EXPIRED:
	    return AccountStatus.Expired;
	case LOCKED:
	    return AccountStatus.Locked;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown account status: " + grpc.name());
	}
	return null;
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
	api.setMetadata(grpc.getMetadataMap());
	return api;
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
	    return GUserAccountStatus.ACTIVE;
	case Expired:
	    return GUserAccountStatus.EXPIRED;
	case Locked:
	    return GUserAccountStatus.LOCKED;
	}
	throw new SiteWhereException("Unknown account status: " + api.name());
    }

    /**
     * Convert an {@link IUser} to a {@link GUser}.
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
	builder.getMetadataMap().putAll(api.getMetadata());
	builder.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return builder.build();
    }
}