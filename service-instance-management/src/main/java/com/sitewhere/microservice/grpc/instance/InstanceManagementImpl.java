/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc.instance;

import java.util.List;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.instance.InstanceModelConverter;
import com.sitewhere.grpc.client.spi.server.IGrpcApiImplementation;
import com.sitewhere.grpc.service.GGetDatasetTemplatesRequest;
import com.sitewhere.grpc.service.GGetDatasetTemplatesResponse;
import com.sitewhere.grpc.service.GGetTenantTemplatesRequest;
import com.sitewhere.grpc.service.GGetTenantTemplatesResponse;
import com.sitewhere.grpc.service.InstanceManagementGrpc;
import com.sitewhere.instance.spi.microservice.IInstanceManagementMicroservice;
import com.sitewhere.spi.instance.IInstanceManagement;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.microservice.multitenant.IDatasetTemplate;
import com.sitewhere.spi.microservice.multitenant.ITenantTemplate;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for instance management GRPC requests.
 */
public class InstanceManagementImpl extends InstanceManagementGrpc.InstanceManagementImplBase
	implements IGrpcApiImplementation {

    /** Parent microservice */
    private IInstanceManagementMicroservice<?> microservice;

    /** Instance management API */
    private IInstanceManagement instanceManagement;

    public InstanceManagementImpl(IInstanceManagementMicroservice<?> microservice,
	    IInstanceManagement instanceManagement) {
	this.microservice = microservice;
	this.instanceManagement = instanceManagement;
    }

    /*
     * @see
     * com.sitewhere.grpc.service.InstanceManagementGrpc.InstanceManagementImplBase#
     * getTenantTemplates(com.sitewhere.grpc.service.GGetTenantTemplatesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getTenantTemplates(GGetTenantTemplatesRequest request,
	    StreamObserver<GGetTenantTemplatesResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, InstanceManagementGrpc.getGetTenantTemplatesMethod());
	    List<ITenantTemplate> apiResult = getInstanceManagement().getTenantTemplates();
	    GGetTenantTemplatesResponse.Builder response = GGetTenantTemplatesResponse.newBuilder();
	    response.addAllTemplate(InstanceModelConverter.asGrpcTenantTemplateList(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(InstanceManagementGrpc.getGetTenantTemplatesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(InstanceManagementGrpc.getGetTenantTemplatesMethod());
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.InstanceManagementGrpc.InstanceManagementImplBase#
     * getDatasetTemplates(com.sitewhere.grpc.service.GGetDatasetTemplatesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDatasetTemplates(GGetDatasetTemplatesRequest request,
	    StreamObserver<GGetDatasetTemplatesResponse> responseObserver) {
	try {
	    GrpcUtils.handleServerMethodEntry(this, InstanceManagementGrpc.getGetDatasetTemplatesMethod());
	    List<IDatasetTemplate> apiResult = getInstanceManagement().getDatasetTemplates();
	    GGetDatasetTemplatesResponse.Builder response = GGetDatasetTemplatesResponse.newBuilder();
	    response.addAllTemplate(InstanceModelConverter.asGrpcDatasetTemplateList(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(InstanceManagementGrpc.getGetDatasetTemplatesMethod(), e,
		    responseObserver);
	} finally {
	    GrpcUtils.handleServerMethodExit(InstanceManagementGrpc.getGetDatasetTemplatesMethod());
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

    protected IInstanceManagement getInstanceManagement() {
	return instanceManagement;
    }
}
