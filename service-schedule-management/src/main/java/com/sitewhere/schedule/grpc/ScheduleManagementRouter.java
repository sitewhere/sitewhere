/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.grpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.service.GCreateScheduleRequest;
import com.sitewhere.grpc.service.GCreateScheduleResponse;
import com.sitewhere.grpc.service.GCreateScheduledJobRequest;
import com.sitewhere.grpc.service.GCreateScheduledJobResponse;
import com.sitewhere.grpc.service.GDeleteScheduleRequest;
import com.sitewhere.grpc.service.GDeleteScheduleResponse;
import com.sitewhere.grpc.service.GDeleteScheduledJobRequest;
import com.sitewhere.grpc.service.GDeleteScheduledJobResponse;
import com.sitewhere.grpc.service.GGetScheduleByTokenRequest;
import com.sitewhere.grpc.service.GGetScheduleByTokenResponse;
import com.sitewhere.grpc.service.GGetScheduledJobByTokenRequest;
import com.sitewhere.grpc.service.GGetScheduledJobByTokenResponse;
import com.sitewhere.grpc.service.GListScheduledJobsRequest;
import com.sitewhere.grpc.service.GListScheduledJobsResponse;
import com.sitewhere.grpc.service.GListSchedulesRequest;
import com.sitewhere.grpc.service.GListSchedulesResponse;
import com.sitewhere.grpc.service.GUpdateScheduleRequest;
import com.sitewhere.grpc.service.GUpdateScheduleResponse;
import com.sitewhere.grpc.service.GUpdateScheduledJobRequest;
import com.sitewhere.grpc.service.GUpdateScheduledJobResponse;
import com.sitewhere.grpc.service.ScheduleManagementGrpc;
import com.sitewhere.microservice.grpc.GrpcTenantEngineProvider;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementMicroservice;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementTenantEngine;
import com.sitewhere.spi.microservice.grpc.ITenantEngineCallback;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 */
public class ScheduleManagementRouter extends ScheduleManagementGrpc.ScheduleManagementImplBase {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(ScheduleManagementRouter.class);

    /** Parent microservice */
    private IScheduleManagementMicroservice microservice;

    /** Tenant engine provider */
    private GrpcTenantEngineProvider<IScheduleManagementTenantEngine> grpcTenantEngineProvider;

    public ScheduleManagementRouter(IScheduleManagementMicroservice microservice) {
	this.microservice = microservice;
	this.grpcTenantEngineProvider = new GrpcTenantEngineProvider<>(microservice);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.ScheduleManagementGrpc.ScheduleManagementImplBase#
     * createSchedule(com.sitewhere.grpc.service.GCreateScheduleRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createSchedule(GCreateScheduleRequest request,
	    StreamObserver<GCreateScheduleResponse> responseObserver) {
	getGrpcTenantEngineProvider()
		.executeInTenantEngine(new ITenantEngineCallback<IScheduleManagementTenantEngine>() {

		    @Override
		    public void executeInTenantEngine(IScheduleManagementTenantEngine tenantEngine) {
			tenantEngine.getScheduleManagementImpl().createSchedule(request, responseObserver);
		    }
		}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.ScheduleManagementGrpc.ScheduleManagementImplBase#
     * updateSchedule(com.sitewhere.grpc.service.GUpdateScheduleRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateSchedule(GUpdateScheduleRequest request,
	    StreamObserver<GUpdateScheduleResponse> responseObserver) {
	getGrpcTenantEngineProvider()
		.executeInTenantEngine(new ITenantEngineCallback<IScheduleManagementTenantEngine>() {

		    @Override
		    public void executeInTenantEngine(IScheduleManagementTenantEngine tenantEngine) {
			tenantEngine.getScheduleManagementImpl().updateSchedule(request, responseObserver);
		    }
		}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.ScheduleManagementGrpc.ScheduleManagementImplBase#
     * getScheduleByToken(com.sitewhere.grpc.service.GGetScheduleByTokenRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void getScheduleByToken(GGetScheduleByTokenRequest request,
	    StreamObserver<GGetScheduleByTokenResponse> responseObserver) {
	getGrpcTenantEngineProvider()
		.executeInTenantEngine(new ITenantEngineCallback<IScheduleManagementTenantEngine>() {

		    @Override
		    public void executeInTenantEngine(IScheduleManagementTenantEngine tenantEngine) {
			tenantEngine.getScheduleManagementImpl().getScheduleByToken(request, responseObserver);
		    }
		}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.ScheduleManagementGrpc.ScheduleManagementImplBase#
     * listSchedules(com.sitewhere.grpc.service.GListSchedulesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listSchedules(GListSchedulesRequest request, StreamObserver<GListSchedulesResponse> responseObserver) {
	getGrpcTenantEngineProvider()
		.executeInTenantEngine(new ITenantEngineCallback<IScheduleManagementTenantEngine>() {

		    @Override
		    public void executeInTenantEngine(IScheduleManagementTenantEngine tenantEngine) {
			tenantEngine.getScheduleManagementImpl().listSchedules(request, responseObserver);
		    }
		}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.ScheduleManagementGrpc.ScheduleManagementImplBase#
     * deleteSchedule(com.sitewhere.grpc.service.GDeleteScheduleRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteSchedule(GDeleteScheduleRequest request,
	    StreamObserver<GDeleteScheduleResponse> responseObserver) {
	getGrpcTenantEngineProvider()
		.executeInTenantEngine(new ITenantEngineCallback<IScheduleManagementTenantEngine>() {

		    @Override
		    public void executeInTenantEngine(IScheduleManagementTenantEngine tenantEngine) {
			tenantEngine.getScheduleManagementImpl().deleteSchedule(request, responseObserver);
		    }
		}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.ScheduleManagementGrpc.ScheduleManagementImplBase#
     * createScheduledJob(com.sitewhere.grpc.service.GCreateScheduledJobRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void createScheduledJob(GCreateScheduledJobRequest request,
	    StreamObserver<GCreateScheduledJobResponse> responseObserver) {
	getGrpcTenantEngineProvider()
		.executeInTenantEngine(new ITenantEngineCallback<IScheduleManagementTenantEngine>() {

		    @Override
		    public void executeInTenantEngine(IScheduleManagementTenantEngine tenantEngine) {
			tenantEngine.getScheduleManagementImpl().createScheduledJob(request, responseObserver);
		    }
		}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.ScheduleManagementGrpc.ScheduleManagementImplBase#
     * updateScheduledJob(com.sitewhere.grpc.service.GUpdateScheduledJobRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void updateScheduledJob(GUpdateScheduledJobRequest request,
	    StreamObserver<GUpdateScheduledJobResponse> responseObserver) {
	getGrpcTenantEngineProvider()
		.executeInTenantEngine(new ITenantEngineCallback<IScheduleManagementTenantEngine>() {

		    @Override
		    public void executeInTenantEngine(IScheduleManagementTenantEngine tenantEngine) {
			tenantEngine.getScheduleManagementImpl().updateScheduledJob(request, responseObserver);
		    }
		}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.ScheduleManagementGrpc.ScheduleManagementImplBase#
     * getScheduledJobByToken(com.sitewhere.grpc.service.
     * GGetScheduledJobByTokenRequest, io.grpc.stub.StreamObserver)
     */
    @Override
    public void getScheduledJobByToken(GGetScheduledJobByTokenRequest request,
	    StreamObserver<GGetScheduledJobByTokenResponse> responseObserver) {
	getGrpcTenantEngineProvider()
		.executeInTenantEngine(new ITenantEngineCallback<IScheduleManagementTenantEngine>() {

		    @Override
		    public void executeInTenantEngine(IScheduleManagementTenantEngine tenantEngine) {
			tenantEngine.getScheduleManagementImpl().getScheduledJobByToken(request, responseObserver);
		    }
		}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.ScheduleManagementGrpc.ScheduleManagementImplBase#
     * listScheduledJobs(com.sitewhere.grpc.service.GListScheduledJobsRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listScheduledJobs(GListScheduledJobsRequest request,
	    StreamObserver<GListScheduledJobsResponse> responseObserver) {
	getGrpcTenantEngineProvider()
		.executeInTenantEngine(new ITenantEngineCallback<IScheduleManagementTenantEngine>() {

		    @Override
		    public void executeInTenantEngine(IScheduleManagementTenantEngine tenantEngine) {
			tenantEngine.getScheduleManagementImpl().listScheduledJobs(request, responseObserver);
		    }
		}, responseObserver);
    }

    /*
     * @see
     * com.sitewhere.grpc.service.ScheduleManagementGrpc.ScheduleManagementImplBase#
     * deleteScheduledJob(com.sitewhere.grpc.service.GDeleteScheduledJobRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void deleteScheduledJob(GDeleteScheduledJobRequest request,
	    StreamObserver<GDeleteScheduledJobResponse> responseObserver) {
	getGrpcTenantEngineProvider()
		.executeInTenantEngine(new ITenantEngineCallback<IScheduleManagementTenantEngine>() {

		    @Override
		    public void executeInTenantEngine(IScheduleManagementTenantEngine tenantEngine) {
			tenantEngine.getScheduleManagementImpl().deleteScheduledJob(request, responseObserver);
		    }
		}, responseObserver);
    }

    protected IScheduleManagementMicroservice getMicroservice() {
	return microservice;
    }

    protected void setMicroservice(IScheduleManagementMicroservice microservice) {
	this.microservice = microservice;
    }

    protected GrpcTenantEngineProvider<IScheduleManagementTenantEngine> getGrpcTenantEngineProvider() {
	return grpcTenantEngineProvider;
    }
}
