/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.grpc;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.model.converter.TenantModelConverter;
import com.sitewhere.grpc.service.GCreateTenantRequest;
import com.sitewhere.grpc.service.GCreateTenantResponse;
import com.sitewhere.grpc.service.GDeleteTenantRequest;
import com.sitewhere.grpc.service.GDeleteTenantResponse;
import com.sitewhere.grpc.service.GGetTenantByAuthenticationTokenRequest;
import com.sitewhere.grpc.service.GGetTenantByAuthenticationTokenResponse;
import com.sitewhere.grpc.service.GGetTenantByIdRequest;
import com.sitewhere.grpc.service.GGetTenantByIdResponse;
import com.sitewhere.grpc.service.GGetTenantTemplatesRequest;
import com.sitewhere.grpc.service.GGetTenantTemplatesResponse;
import com.sitewhere.grpc.service.GListTenantsRequest;
import com.sitewhere.grpc.service.GListTenantsResponse;
import com.sitewhere.grpc.service.GUpdateTenantRequest;
import com.sitewhere.grpc.service.GUpdateTenantResponse;
import com.sitewhere.grpc.service.TenantManagementGrpc;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantAdministration;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for tenant management GRPC requests.
 * 
 * @author Derek
 */
public class TenantManagementImpl extends TenantManagementGrpc.TenantManagementImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Logger LOGGER = LogManager.getLogger();

    /** Tenant management persistence */
    private ITenantManagement tenantMangagement;

    /** Tenant administration */
    private ITenantAdministration tenantAdministration;

    public TenantManagementImpl(ITenantManagement tenantManagement, ITenantAdministration tenantAdministration) {
	this.tenantMangagement = tenantManagement;
	this.tenantAdministration = tenantAdministration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementImplBase#
     * createTenant(com.sitewhere.grpc.service.GCreateTenantRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createTenant(GCreateTenantRequest request, StreamObserver<GCreateTenantResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(TenantManagementGrpc.METHOD_CREATE_TENANT);
	    ITenantCreateRequest apiRequest = TenantModelConverter.asApiTenantCreateRequest(request.getRequest());
	    ITenant apiResult = getTenantMangagement().createTenant(apiRequest);
	    GCreateTenantResponse.Builder response = GCreateTenantResponse.newBuilder();
	    response.setTenant(TenantModelConverter.asGrpcTenant(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.METHOD_CREATE_TENANT, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementImplBase#
     * updateTenant(com.sitewhere.grpc.service.GUpdateTenantRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateTenant(GUpdateTenantRequest request, StreamObserver<GUpdateTenantResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(TenantManagementGrpc.METHOD_UPDATE_TENANT);
	    ITenantCreateRequest apiRequest = TenantModelConverter.asApiTenantCreateRequest(request.getRequest());
	    ITenant apiResult = getTenantMangagement().updateTenant(request.getId(), apiRequest);
	    GUpdateTenantResponse.Builder response = GUpdateTenantResponse.newBuilder();
	    response.setTenant(TenantModelConverter.asGrpcTenant(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.METHOD_UPDATE_TENANT, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementImplBase#
     * getTenantById(com.sitewhere.grpc.service.GGetTenantByIdRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getTenantById(GGetTenantByIdRequest request, StreamObserver<GGetTenantByIdResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(TenantManagementGrpc.METHOD_GET_TENANT_BY_ID);
	    ITenant apiResult = getTenantMangagement().getTenantById(request.getId());
	    GGetTenantByIdResponse.Builder response = GGetTenantByIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setTenant(TenantModelConverter.asGrpcTenant(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.METHOD_GET_TENANT_BY_ID, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementImplBase#
     * getTenantByAuthenticationToken(com.sitewhere.grpc.service.
     * GGetTenantByAuthenticationTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getTenantByAuthenticationToken(GGetTenantByAuthenticationTokenRequest request,
	    StreamObserver<GGetTenantByAuthenticationTokenResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(TenantManagementGrpc.METHOD_GET_TENANT_BY_AUTHENTICATION_TOKEN);
	    ITenant apiResult = getTenantMangagement().getTenantByAuthenticationToken(request.getToken());
	    GGetTenantByAuthenticationTokenResponse.Builder response = GGetTenantByAuthenticationTokenResponse
		    .newBuilder();
	    if (apiResult != null) {
		response.setTenant(TenantModelConverter.asGrpcTenant(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.METHOD_GET_TENANT_BY_AUTHENTICATION_TOKEN, e,
		    responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementImplBase#
     * listTenants(com.sitewhere.grpc.service.GListTenantsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listTenants(GListTenantsRequest request, StreamObserver<GListTenantsResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(TenantManagementGrpc.METHOD_LIST_TENANTS);
	    ISearchResults<ITenant> apiResult = getTenantMangagement()
		    .listTenants(TenantModelConverter.asApiTenantSearchCriteria(request.getCriteria()));
	    GListTenantsResponse.Builder response = GListTenantsResponse.newBuilder();
	    response.setResults(TenantModelConverter.asGrpcTenantSearchResults(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.METHOD_LIST_TENANTS, e, responseObserver);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementImplBase#
     * deleteTenant(com.sitewhere.grpc.service.GDeleteTenantRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteTenant(GDeleteTenantRequest request, StreamObserver<GDeleteTenantResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(TenantManagementGrpc.METHOD_DELETE_TENANT);
	    ITenant apiResult = getTenantMangagement().deleteTenant(request.getId(), request.getForce());
	    GDeleteTenantResponse.Builder response = GDeleteTenantResponse.newBuilder();
	    response.setTenant(TenantModelConverter.asGrpcTenant(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.METHOD_DELETE_TENANT, e, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementImplBase#
     * getTenantTemplates(com.sitewhere.grpc.service.GGetTenantTemplatesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getTenantTemplates(GGetTenantTemplatesRequest request,
	    StreamObserver<GGetTenantTemplatesResponse> responseObserver) {
	try {
	    GrpcUtils.logServerMethodEntry(TenantManagementGrpc.METHOD_GET_TENANT_TEMPLATES);
	    List<ITenantTemplate> apiResult = getTenantAdministration().getTenantTemplates();
	    GGetTenantTemplatesResponse.Builder response = GGetTenantTemplatesResponse.newBuilder();
	    response.addAllTemplate(TenantModelConverter.asGrpcTenantTemplateList(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.METHOD_GET_TENANT_TEMPLATES, e,
		    responseObserver);
	}
    }

    public ITenantManagement getTenantMangagement() {
	return tenantMangagement;
    }

    public void setTenantMangagement(ITenantManagement tenantMangagement) {
	this.tenantMangagement = tenantMangagement;
    }

    public ITenantAdministration getTenantAdministration() {
	return tenantAdministration;
    }

    public void setTenantAdministration(ITenantAdministration tenantAdministration) {
	this.tenantAdministration = tenantAdministration;
    }
}