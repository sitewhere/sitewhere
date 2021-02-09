/**
 * Copyright © 2014-2021 The SiteWhere Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sitewhere.labels.grpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.service.GGetAreaLabelRequest;
import com.sitewhere.grpc.service.GGetAreaLabelResponse;
import com.sitewhere.grpc.service.GGetAreaTypeLabelRequest;
import com.sitewhere.grpc.service.GGetAreaTypeLabelResponse;
import com.sitewhere.grpc.service.GGetAssetLabelRequest;
import com.sitewhere.grpc.service.GGetAssetLabelResponse;
import com.sitewhere.grpc.service.GGetAssetTypeLabelRequest;
import com.sitewhere.grpc.service.GGetAssetTypeLabelResponse;
import com.sitewhere.grpc.service.GGetCustomerLabelRequest;
import com.sitewhere.grpc.service.GGetCustomerLabelResponse;
import com.sitewhere.grpc.service.GGetCustomerTypeLabelRequest;
import com.sitewhere.grpc.service.GGetCustomerTypeLabelResponse;
import com.sitewhere.grpc.service.GGetDeviceAssignmentLabelRequest;
import com.sitewhere.grpc.service.GGetDeviceAssignmentLabelResponse;
import com.sitewhere.grpc.service.GGetDeviceGroupLabelRequest;
import com.sitewhere.grpc.service.GGetDeviceGroupLabelResponse;
import com.sitewhere.grpc.service.GGetDeviceLabelRequest;
import com.sitewhere.grpc.service.GGetDeviceLabelResponse;
import com.sitewhere.grpc.service.GGetDeviceTypeLabelRequest;
import com.sitewhere.grpc.service.GGetDeviceTypeLabelResponse;
import com.sitewhere.grpc.service.LabelGenerationGrpc;
import com.sitewhere.labels.spi.microservice.ILabelGenerationMicroservice;
import com.sitewhere.labels.spi.microservice.ILabelGenerationTenantEngine;
import com.sitewhere.microservice.grpc.GrpcTenantEngineProvider;
import com.sitewhere.spi.microservice.grpc.ITenantEngineCallback;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 */
public class LabelGenerationRouter extends LabelGenerationGrpc.LabelGenerationImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(LabelGenerationRouter.class);

    /** Parent microservice */
    private ILabelGenerationMicroservice microservice;

    /** Tenant engine provider */
    private GrpcTenantEngineProvider<ILabelGenerationTenantEngine> grpcTenantEngineProvider;

    public LabelGenerationRouter(ILabelGenerationMicroservice microservice) {
	this.microservice = microservice;
	this.grpcTenantEngineProvider = new GrpcTenantEngineProvider<>(microservice);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getCustomerTypeLabel(com.sitewhere.grpc.service.GGetCustomerTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCustomerTypeLabel(GGetCustomerTypeLabelRequest request,
	    StreamObserver<GGetCustomerTypeLabelResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<ILabelGenerationTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(ILabelGenerationTenantEngine tenantEngine) {
		tenantEngine.getLabelGenerationImpl().getCustomerTypeLabel(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getCustomerLabel(com.sitewhere.grpc.service.GGetCustomerLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getCustomerLabel(GGetCustomerLabelRequest request,
	    StreamObserver<GGetCustomerLabelResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<ILabelGenerationTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(ILabelGenerationTenantEngine tenantEngine) {
		tenantEngine.getLabelGenerationImpl().getCustomerLabel(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAreaTypeLabel(com.sitewhere.grpc.service.GGetAreaTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreaTypeLabel(GGetAreaTypeLabelRequest request,
	    StreamObserver<GGetAreaTypeLabelResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<ILabelGenerationTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(ILabelGenerationTenantEngine tenantEngine) {
		tenantEngine.getLabelGenerationImpl().getAreaTypeLabel(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAreaLabel(com.sitewhere.grpc.service.GGetAreaLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAreaLabel(GGetAreaLabelRequest request, StreamObserver<GGetAreaLabelResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<ILabelGenerationTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(ILabelGenerationTenantEngine tenantEngine) {
		tenantEngine.getLabelGenerationImpl().getAreaLabel(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceTypeLabel(com.sitewhere.grpc.service.GGetDeviceTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceTypeLabel(GGetDeviceTypeLabelRequest request,
	    StreamObserver<GGetDeviceTypeLabelResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<ILabelGenerationTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(ILabelGenerationTenantEngine tenantEngine) {
		tenantEngine.getLabelGenerationImpl().getDeviceTypeLabel(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceLabel(com.sitewhere.grpc.service.GGetDeviceLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceLabel(GGetDeviceLabelRequest request,
	    StreamObserver<GGetDeviceLabelResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<ILabelGenerationTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(ILabelGenerationTenantEngine tenantEngine) {
		tenantEngine.getLabelGenerationImpl().getDeviceLabel(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceGroupLabel(com.sitewhere.grpc.service.GGetDeviceGroupLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceGroupLabel(GGetDeviceGroupLabelRequest request,
	    StreamObserver<GGetDeviceGroupLabelResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<ILabelGenerationTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(ILabelGenerationTenantEngine tenantEngine) {
		tenantEngine.getLabelGenerationImpl().getDeviceGroupLabel(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getDeviceAssignmentLabel(com.sitewhere.grpc.service.
     * GGetDeviceAssignmentLabelRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getDeviceAssignmentLabel(GGetDeviceAssignmentLabelRequest request,
	    StreamObserver<GGetDeviceAssignmentLabelResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<ILabelGenerationTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(ILabelGenerationTenantEngine tenantEngine) {
		tenantEngine.getLabelGenerationImpl().getDeviceAssignmentLabel(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAssetTypeLabel(com.sitewhere.grpc.service.GGetAssetTypeLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetTypeLabel(GGetAssetTypeLabelRequest request,
	    StreamObserver<GGetAssetTypeLabelResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<ILabelGenerationTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(ILabelGenerationTenantEngine tenantEngine) {
		tenantEngine.getLabelGenerationImpl().getAssetTypeLabel(request, responseObserver);
	    }
	}, responseObserver);
    }

    /*
     * @see com.sitewhere.grpc.service.LabelGenerationGrpc.LabelGenerationImplBase#
     * getAssetLabel(com.sitewhere.grpc.service.GGetAssetLabelRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getAssetLabel(GGetAssetLabelRequest request, StreamObserver<GGetAssetLabelResponse> responseObserver) {
	getGrpcTenantEngineProvider().executeInTenantEngine(new ITenantEngineCallback<ILabelGenerationTenantEngine>() {

	    @Override
	    public void executeInTenantEngine(ILabelGenerationTenantEngine tenantEngine) {
		tenantEngine.getLabelGenerationImpl().getAssetLabel(request, responseObserver);
	    }
	}, responseObserver);
    }

    protected ILabelGenerationMicroservice getMicroservice() {
	return microservice;
    }

    protected GrpcTenantEngineProvider<ILabelGenerationTenantEngine> getGrpcTenantEngineProvider() {
	return grpcTenantEngineProvider;
    }
}