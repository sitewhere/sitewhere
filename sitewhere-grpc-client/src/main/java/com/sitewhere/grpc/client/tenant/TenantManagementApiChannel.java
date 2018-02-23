/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.tenant;

import java.util.List;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.ApiChannel;
import com.sitewhere.grpc.client.GrpcChannel;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.IApiDemux;
import com.sitewhere.grpc.client.spi.client.ITenantManagementApiChannel;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.converter.TenantModelConverter;
import com.sitewhere.grpc.service.GCreateTenantRequest;
import com.sitewhere.grpc.service.GCreateTenantResponse;
import com.sitewhere.grpc.service.GDeleteTenantRequest;
import com.sitewhere.grpc.service.GDeleteTenantResponse;
import com.sitewhere.grpc.service.GGetTenantByIdRequest;
import com.sitewhere.grpc.service.GGetTenantByIdResponse;
import com.sitewhere.grpc.service.GGetTenantByTokenRequest;
import com.sitewhere.grpc.service.GGetTenantByTokenResponse;
import com.sitewhere.grpc.service.GGetTenantTemplatesRequest;
import com.sitewhere.grpc.service.GGetTenantTemplatesResponse;
import com.sitewhere.grpc.service.GListTenantsRequest;
import com.sitewhere.grpc.service.GListTenantsResponse;
import com.sitewhere.grpc.service.GUpdateTenantRequest;
import com.sitewhere.grpc.service.GUpdateTenantResponse;
import com.sitewhere.grpc.service.TenantManagementGrpc;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.search.user.ITenantSearchCriteria;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Supports SiteWhere tenant management APIs on top of a
 * {@link TenantManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class TenantManagementApiChannel extends ApiChannel<TenantManagementGrpcChannel>
	implements ITenantManagementApiChannel {

    /** Static logger instance */
    private static Log LOGGER = LogFactory.getLog(TenantManagementApiChannel.class);

    public TenantManagementApiChannel(IApiDemux<?> demux, IMicroservice microservice, String host) {
	super(demux, microservice, host);
    }

    /*
     * @see
     * com.sitewhere.grpc.client.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi
     * .tracing.ITracerProvider, java.lang.String, int)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public GrpcChannel createGrpcChannel(ITracerProvider tracerProvider, String host, int port) {
	return new TenantManagementGrpcChannel(tracerProvider, host, port);
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
	    GrpcUtils.logClientMethodEntry(this, TenantManagementGrpc.METHOD_CREATE_TENANT);
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
     * @see com.sitewhere.spi.tenant.ITenantManagement#updateTenant(java.util.UUID,
     * com.sitewhere.spi.tenant.request.ITenantCreateRequest)
     */
    @Override
    public ITenant updateTenant(UUID id, ITenantCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, TenantManagementGrpc.METHOD_UPDATE_TENANT);
	    GUpdateTenantRequest.Builder grequest = GUpdateTenantRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
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
     * @see com.sitewhere.spi.tenant.ITenantManagement#getTenant(java.util.UUID)
     */
    @Override
    public ITenant getTenant(UUID id) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, TenantManagementGrpc.METHOD_GET_TENANT_BY_ID);
	    GGetTenantByIdRequest.Builder grequest = GGetTenantByIdRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(id));
	    GGetTenantByIdResponse gresponse = getGrpcChannel().getBlockingStub().getTenantById(grequest.build());
	    ITenant response = (gresponse.hasTenant()) ? TenantModelConverter.asApiTenant(gresponse.getTenant()) : null;
	    GrpcUtils.logClientMethodResponse(TenantManagementGrpc.METHOD_GET_TENANT_BY_ID, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(TenantManagementGrpc.METHOD_GET_TENANT_BY_ID, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.tenant.ITenantManagement#getTenantByToken(java.lang.String)
     */
    @Override
    public ITenant getTenantByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, TenantManagementGrpc.METHOD_GET_TENANT_BY_TOKEN);
	    GGetTenantByTokenRequest.Builder grequest = GGetTenantByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetTenantByTokenResponse gresponse = getGrpcChannel().getBlockingStub().getTenantByToken(grequest.build());
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
     * com.sitewhere.spi.tenant.ITenantManagement#listTenants(com.sitewhere.spi.
     * search.user.ITenantSearchCriteria)
     */
    @Override
    public ISearchResults<ITenant> listTenants(ITenantSearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, TenantManagementGrpc.METHOD_LIST_TENANTS);
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
     * @see com.sitewhere.spi.tenant.ITenantManagement#deleteTenant(java.util.UUID,
     * boolean)
     */
    @Override
    public ITenant deleteTenant(UUID tenantId, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, TenantManagementGrpc.METHOD_DELETE_TENANT);
	    GDeleteTenantRequest.Builder grequest = GDeleteTenantRequest.newBuilder();
	    grequest.setId(CommonModelConverter.asGrpcUuid(tenantId));
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
     * @see com.sitewhere.spi.tenant.ITenantAdministration#getTenantTemplates()
     */
    @Override
    public List<ITenantTemplate> getTenantTemplates() throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(this, TenantManagementGrpc.METHOD_GET_TENANT_TEMPLATES);
	    GGetTenantTemplatesRequest.Builder grequest = GGetTenantTemplatesRequest.newBuilder();
	    GGetTenantTemplatesResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getTenantTemplates(grequest.build());
	    List<ITenantTemplate> response = TenantModelConverter.asApiTenantTemplateList(gresponse.getTemplateList());
	    GrpcUtils.logClientMethodResponse(TenantManagementGrpc.METHOD_GET_TENANT_TEMPLATES, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(TenantManagementGrpc.METHOD_GET_TENANT_TEMPLATES, t);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.sitewhere.spi.server.lifecycle.ILifecycleComponent#getLogger()
     */
    @Override
    public Log getLogger() {
	return LOGGER;
    }
}