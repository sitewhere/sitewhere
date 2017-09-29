package com.sitewhere.grpc.model.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.converter.TenantModelConverter;
import com.sitewhere.grpc.model.spi.client.ITenantManagementApiChannel;
import com.sitewhere.grpc.service.GCreateTenantRequest;
import com.sitewhere.grpc.service.GCreateTenantResponse;
import com.sitewhere.grpc.service.GDeleteTenantRequest;
import com.sitewhere.grpc.service.GDeleteTenantResponse;
import com.sitewhere.grpc.service.GGetTenantByAuthenticationTokenRequest;
import com.sitewhere.grpc.service.GGetTenantByAuthenticationTokenResponse;
import com.sitewhere.grpc.service.GGetTenantByIdRequest;
import com.sitewhere.grpc.service.GGetTenantByIdResponse;
import com.sitewhere.grpc.service.GListTenantsRequest;
import com.sitewhere.grpc.service.GListTenantsResponse;
import com.sitewhere.grpc.service.GUpdateTenantRequest;
import com.sitewhere.grpc.service.GUpdateTenantResponse;
import com.sitewhere.grpc.service.TenantManagementGrpc;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

/**
 * Supports SiteWhere tenant management APIs on top of a
 * {@link TenantManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class TenantManagementApiChannel extends ApiChannel<TenantManagementGrpcChannel>
	implements ITenantManagementApiChannel {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant management GRPC channel */
    private TenantManagementGrpcChannel grpcChannel;

    public TenantManagementApiChannel(TenantManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#createTenant(com.sitewhere.spi
     * .tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant createTenant(ITenantCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(TenantManagementGrpc.METHOD_CREATE_TENANT);
	    GCreateTenantRequest.Builder grequest = GCreateTenantRequest.newBuilder();
	    grequest.setRequest(TenantModelConverter.asGrpcTenantCreateRequest(request));
	    GCreateTenantResponse gresponse = getGrpcChannel().getBlockingStub().createTenant(grequest.build());
	    ITenant response = TenantModelConverter.asApiTenant(gresponse.getTenant());
	    GrpcUtils.logClientMethodResponse(TenantManagementGrpc.METHOD_CREATE_TENANT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(TenantManagementGrpc.METHOD_CREATE_TENANT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#updateTenant(java.lang.String,
     * com.sitewhere.spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant updateTenant(String id, ITenantCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(TenantManagementGrpc.METHOD_UPDATE_TENANT);
	    GUpdateTenantRequest.Builder grequest = GUpdateTenantRequest.newBuilder();
	    grequest.setId(id);
	    grequest.setRequest(TenantModelConverter.asGrpcTenantCreateRequest(request));
	    GUpdateTenantResponse gresponse = getGrpcChannel().getBlockingStub().updateTenant(grequest.build());
	    ITenant response = TenantModelConverter.asApiTenant(gresponse.getTenant());
	    GrpcUtils.logClientMethodResponse(TenantManagementGrpc.METHOD_UPDATE_TENANT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(TenantManagementGrpc.METHOD_UPDATE_TENANT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.tenant.ITenantManagement#getTenantById(java.lang.
     * String)
     */
    @Override
    public ITenant getTenantById(String id) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(TenantManagementGrpc.METHOD_GET_TENANT_BY_ID);
	    GGetTenantByIdRequest.Builder grequest = GGetTenantByIdRequest.newBuilder();
	    grequest.setId(id);
	    GGetTenantByIdResponse gresponse = getGrpcChannel().getBlockingStub().getTenantById(grequest.build());
	    ITenant response = (gresponse.hasTenant()) ? TenantModelConverter.asApiTenant(gresponse.getTenant()) : null;
	    GrpcUtils.logClientMethodResponse(TenantManagementGrpc.METHOD_GET_TENANT_BY_ID, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(TenantManagementGrpc.METHOD_GET_TENANT_BY_ID, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#getTenantByAuthenticationToken
     * (java.lang.String)
     */
    @Override
    public ITenant getTenantByAuthenticationToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(TenantManagementGrpc.METHOD_GET_TENANT_BY_AUTHENTICATION_TOKEN);
	    GGetTenantByAuthenticationTokenRequest.Builder grequest = GGetTenantByAuthenticationTokenRequest
		    .newBuilder();
	    grequest.setToken(token);
	    GGetTenantByAuthenticationTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getTenantByAuthenticationToken(grequest.build());
	    ITenant response = (gresponse.hasTenant()) ? TenantModelConverter.asApiTenant(gresponse.getTenant()) : null;
	    GrpcUtils.logClientMethodResponse(TenantManagementGrpc.METHOD_GET_TENANT_BY_AUTHENTICATION_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(TenantManagementGrpc.METHOD_GET_TENANT_BY_AUTHENTICATION_TOKEN,
		    t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#listTenants(com.sitewhere.spi.
     * search.user.ITenantSearchCriteria)
     */
    @Override
    public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(TenantManagementGrpc.METHOD_LIST_TENANTS);
	    GListTenantsRequest.Builder grequest = GListTenantsRequest.newBuilder();
	    grequest.setCriteria(TenantModelConverter.asGrpcTenantSearchCriteria(criteria));
	    GListTenantsResponse gresponse = getGrpcChannel().getBlockingStub().listTenants(grequest.build());
	    ISearchResults<ITenant> results = TenantModelConverter.asApiTenantSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(TenantManagementGrpc.METHOD_LIST_TENANTS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(TenantManagementGrpc.METHOD_LIST_TENANTS, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#deleteTenant(java.lang.String,
     * boolean)
     */
    @Override
    public ITenant deleteTenant(String tenantId, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(TenantManagementGrpc.METHOD_DELETE_TENANT);
	    GDeleteTenantRequest.Builder grequest = GDeleteTenantRequest.newBuilder();
	    grequest.setId(tenantId);
	    grequest.setForce(force);
	    GDeleteTenantResponse gresponse = getGrpcChannel().getBlockingStub().deleteTenant(grequest.build());
	    ITenant response = TenantModelConverter.asApiTenant(gresponse.getTenant());
	    GrpcUtils.logClientMethodResponse(TenantManagementGrpc.METHOD_DELETE_TENANT, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(TenantManagementGrpc.METHOD_DELETE_TENANT, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.grpc.model.client.ApiChannel#getGrpcChannel()
     */
    @Override
    public TenantManagementGrpcChannel getGrpcChannel() {
	return grpcChannel;
    }

    public void setGrpcChannel(TenantManagementGrpcChannel grpcChannel) {
	this.grpcChannel = grpcChannel;
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