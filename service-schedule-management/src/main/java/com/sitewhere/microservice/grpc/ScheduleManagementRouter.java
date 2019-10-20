/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.microservice.grpc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitewhere.grpc.client.GrpcContextKeys;
import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.client.spi.server.IGrpcRouter;
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
import com.sitewhere.schedule.spi.microservice.IScheduleManagementMicroservice;
import com.sitewhere.schedule.spi.microservice.IScheduleManagementTenantEngine;
import com.sitewhere.spi.microservice.multitenant.TenantEngineNotAvailableException;

import io.grpc.stub.StreamObserver;

/**
 * Routes GRPC calls to service implementations in tenants.
 * 
 * @author Derek
 */
public class ScheduleManagementRouter extends ScheduleManagementGrpc.ScheduleManagementImplBase
	implements IGrpcRouter<ScheduleManagementGrpc.ScheduleManagementImplBase> {

    /** Static logger instance */
    @SuppressWarnings("unused")
    private static Log LOGGER = LogFactory.getLog(ScheduleManagementRouter.class);

    /** Parent microservice */
    private IScheduleManagementMicroservice microservice;

    public ScheduleManagementRouter(IScheduleManagementMicroservice microservice) {
	this.microservice = microservice;
    }

    /*
     * @see com.sitewhere.spi.grpc.IGrpcRouter#getTenantImplementation()
     */
    @Override
    public ScheduleManagementGrpc.ScheduleManagementImplBase getTenantImplementation(StreamObserver<?> observer) {
	String token = GrpcContextKeys.TENANT_TOKEN_KEY.get();
	if (token == null) {
	    throw new RuntimeException("Tenant token not found in request.");
	}
	try {
	    IScheduleManagementTenantEngine engine = getMicroservice().assureTenantEngineAvailable(token);
	    return engine.getScheduleManagementImpl();
	} catch (TenantEngineNotAvailableException e) {
	    observer.onError(GrpcUtils.convertServerException(e));
	    return null;
	}
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
	ScheduleManagementGrpc.ScheduleManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createSchedule(request, responseObserver);
	}
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
	ScheduleManagementGrpc.ScheduleManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateSchedule(request, responseObserver);
	}
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
	ScheduleManagementGrpc.ScheduleManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getScheduleByToken(request, responseObserver);
	}
    }

    /*
     * @see
     * com.sitewhere.grpc.service.ScheduleManagementGrpc.ScheduleManagementImplBase#
     * listSchedules(com.sitewhere.grpc.service.GListSchedulesRequest,
     * io.grpc.stub.StreamObserver)
     */
    @Override
    public void listSchedules(GListSchedulesRequest request, StreamObserver<GListSchedulesResponse> responseObserver) {
	ScheduleManagementGrpc.ScheduleManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listSchedules(request, responseObserver);
	}
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
	ScheduleManagementGrpc.ScheduleManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteSchedule(request, responseObserver);
	}
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
	ScheduleManagementGrpc.ScheduleManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.createScheduledJob(request, responseObserver);
	}
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
	ScheduleManagementGrpc.ScheduleManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.updateScheduledJob(request, responseObserver);
	}
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
	ScheduleManagementGrpc.ScheduleManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.getScheduledJobByToken(request, responseObserver);
	}
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
	ScheduleManagementGrpc.ScheduleManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.listScheduledJobs(request, responseObserver);
	}
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
	ScheduleManagementGrpc.ScheduleManagementImplBase engine = getTenantImplementation(responseObserver);
	if (engine != null) {
	    engine.deleteScheduledJob(request, responseObserver);
	}
    }

    protected IScheduleManagementMicroservice getMicroservice() {
	return microservice;
    }

    protected void setMicroservice(IScheduleManagementMicroservice microservice) {
	this.microservice = microservice;
    }
}
