/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.tenant.grpc;

import java.util.List;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.grpc.model.converter.CommonModelConverter;
import com.sitewhere.grpc.model.converter.TenantModelConverter;
import com.sitewhere.grpc.service.GCreateTenantRequest;
import com.sitewhere.grpc.service.GCreateTenantResponse;
import com.sitewhere.grpc.service.GDeleteTenantRequest;
import com.sitewhere.grpc.service.GDeleteTenantResponse;
import com.sitewhere.grpc.service.GGetDatasetTemplatesRequest;
import com.sitewhere.grpc.service.GGetDatasetTemplatesResponse;
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
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tenant.ITenant;
import com.sitewhere.spi.tenant.ITenantAdministration;
import com.sitewhere.spi.tenant.ITenantManagement;
import com.sitewhere.spi.tenant.request.ITenantCreateRequest;
import com.sitewhere.tenant.spi.microservice.ITenantManagementMicroservice;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for tenant management GRPC requests.
 * 
 * @author Derek
 */
public class TenantManagementImpl extends TenantManagementGrpc.TenantManagementImplBase
	implements IGrpcApiImplementation {

    /** Parent microservice */
    private ITenantManagementMicroservice<?> microservice;

    /** Tenant management persistence */
    private ITenantManagement tenantMangagement;

    /** Tenant administration */
    private ITenantAdministration tenantAdministration;

    public TenantManagementImpl(ITenantManagementMicroservice<?> microservice, ITenantManagement tenantManagement,
	    ITenantAdministration tenantAdministration) {
	this.microservice = microservice;
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
	    GrpcUtils.handleServerMethodEntry(this, TenantManagementGrpc.getCreateTenantMethod());
	    ITenantCreateRequest apiRequest = TenantModelConverter.asApiTenantCreateRequest(request.getRequest());
	    ITenant apiResult = getTenantMangagement().createTenant(apiRequest);
	    GCreateTenantResponse.Builder response = GCreateTenantResponse.newBuilder();
	    response.setTenant(TenantModelConverter.asGrpcTenant(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.getCreateTenantMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(TenantManagementGrpc.getCreateTenantMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, TenantManagementGrpc.getUpdateTenantMethod());
	    ITenantCreateRequest apiRequest = TenantModelConverter.asApiTenantCreateRequest(request.getRequest());
	    ITenant apiResult = getTenantMangagement().updateTenant(CommonModelConverter.asApiUuid(request.getId()),
		    apiRequest);
	    GUpdateTenantResponse.Builder response = GUpdateTenantResponse.newBuilder();
	    response.setTenant(TenantModelConverter.asGrpcTenant(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.getUpdateTenantMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(TenantManagementGrpc.getUpdateTenantMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, TenantManagementGrpc.getGetTenantByIdMethod());
	    ITenant apiResult = getTenantMangagement().getTenant(CommonModelConverter.asApiUuid(request.getId()));
	    GGetTenantByIdResponse.Builder response = GGetTenantByIdResponse.newBuilder();
	    if (apiResult != null) {
		response.setTenant(TenantModelConverter.asGrpcTenant(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.getGetTenantByIdMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(TenantManagementGrpc.getGetTenantByIdMethod());
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementImplBase#
     * getTenantByToken(com.sitewhere.grpc.service.GGetTenantByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getTenantByToken(GGetTenantByTokenRequest request,
	    StreamObserver<GGetTenantByTokenResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, TenantManagementGrpc.getGetTenantByTokenMethod());
	    ITenant apiResult = getTenantMangagement().getTenantByToken(request.getToken());
	    GGetTenantByTokenResponse.Builder response = GGetTenantByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setTenant(TenantModelConverter.asGrpcTenant(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.getGetTenantByTokenMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(TenantManagementGrpc.getGetTenantByTokenMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, TenantManagementGrpc.getListTenantsMethod());
	    ISearchResults<ITenant> apiResult = getTenantMangagement()
		    .listTenants(TenantModelConverter.asApiTenantSearchCriteria(request.getCriteria()));
	    GListTenantsResponse.Builder response = GListTenantsResponse.newBuilder();
	    response.setResults(TenantModelConverter.asGrpcTenantSearchResults(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.getListTenantsMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(TenantManagementGrpc.getListTenantsMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, TenantManagementGrpc.getDeleteTenantMethod());
	    ITenant apiResult = getTenantMangagement().deleteTenant(CommonModelConverter.asApiUuid(request.getId()));
	    GDeleteTenantResponse.Builder response = GDeleteTenantResponse.newBuilder();
	    response.setTenant(TenantModelConverter.asGrpcTenant(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.getDeleteTenantMethod(), e, responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(TenantManagementGrpc.getDeleteTenantMethod());
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
	    GrpcUtils.handleServerMethodEntry(this, TenantManagementGrpc.getGetTenantTemplatesMethod());
	    List<ITenantTemplate> apiResult = getTenantAdministration().getTenantTemplates();
	    GGetTenantTemplatesResponse.Builder response = GGetTenantTemplatesResponse.newBuilder();
	    response.addAllTemplate(TenantModelConverter.asGrpcTenantTemplateList(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.getGetTenantTemplatesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(TenantManagementGrpc.getGetTenantTemplatesMethod());
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.TenantManagementGrpc.TenantManagementImplBase#
     * getDatasetTemplates(com.sitewhere.grpc.service.GGetDatasetTemplatesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDatasetTemplates(GGetDatasetTemplatesRequest request,
	    StreamObserver<GGetDatasetTemplatesResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, TenantManagementGrpc.getGetDatasetTemplatesMethod());
	    List<IDatasetTemplate> apiResult = getTenantAdministration().getDatasetTemplates();
	    GGetDatasetTemplatesResponse.Builder response = GGetDatasetTemplatesResponse.newBuilder();
	    response.addAllTemplate(TenantModelConverter.asGrpcDatasetTemplateList(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(TenantManagementGrpc.getGetDatasetTemplatesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(TenantManagementGrpc.getGetDatasetTemplatesMethod());
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

    protected ITenantManagement getTenantMangagement() {
	return tenantMangagement;
    }

    protected ITenantAdministration getTenantAdministration() {
	return tenantAdministration;
    }
}