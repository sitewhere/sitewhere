/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.schedule.grpc;

import com.sitewhere.grpc.client.GrpcUtils;
import com.sitewhere.grpc.model.ScheduleModel.GScheduleSearchResults;
import com.sitewhere.grpc.model.ScheduleModel.GScheduledJobSearchResults;
import com.sitewhere.grpc.model.converter.ScheduleModelConverter;
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
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduleManagement;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchResults;

import io.grpc.stub.StreamObserver;

/**
 * Implements server logic for schedule management GRPC requests.
 * 
 * @author Derek
 */
public class ScheduleManagementImpl extends ScheduleManagementGrpc.ScheduleManagementImplBase {

    /** Schedule management persistence */
    private IScheduleManagement scheduleManagement;

    public ScheduleManagementImpl(IScheduleManagement scheduleManagement) {
	this.scheduleManagement = scheduleManagement;
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
	try {
	    GrpcUtils.logServerMethodEntry(ScheduleManagementGrpc.METHOD_CREATE_SCHEDULE);
	    IScheduleCreateRequest apiRequest = ScheduleModelConverter.asApiScheduleCreateRequest(request.getRequest());
	    ISchedule apiResult = getScheduleManagement().createSchedule(apiRequest);
	    GCreateScheduleResponse.Builder response = GCreateScheduleResponse.newBuilder();
	    response.setSchedule(ScheduleModelConverter.asGrpcSchedule(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(ScheduleManagementGrpc.METHOD_CREATE_SCHEDULE, e, responseObserver);
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
	try {
	    GrpcUtils.logServerMethodEntry(ScheduleManagementGrpc.METHOD_UPDATE_SCHEDULE);
	    IScheduleCreateRequest apiRequest = ScheduleModelConverter.asApiScheduleCreateRequest(request.getRequest());
	    ISchedule apiResult = getScheduleManagement().updateSchedule(request.getToken(), apiRequest);
	    GUpdateScheduleResponse.Builder response = GUpdateScheduleResponse.newBuilder();
	    response.setSchedule(ScheduleModelConverter.asGrpcSchedule(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(ScheduleManagementGrpc.METHOD_UPDATE_SCHEDULE, e, responseObserver);
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
	try {
	    GrpcUtils.logServerMethodEntry(ScheduleManagementGrpc.METHOD_GET_SCHEDULE_BY_TOKEN);
	    ISchedule apiResult = getScheduleManagement().getScheduleByToken(request.getToken());
	    GGetScheduleByTokenResponse.Builder response = GGetScheduleByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setSchedule(ScheduleModelConverter.asGrpcSchedule(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(ScheduleManagementGrpc.METHOD_GET_SCHEDULE_BY_TOKEN, e,
		    responseObserver);
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
	try {
	    GrpcUtils.logServerMethodEntry(ScheduleManagementGrpc.METHOD_LIST_SCHEDULES);
	    ISearchResults<ISchedule> apiResult = getScheduleManagement()
		    .listSchedules(ScheduleModelConverter.asApiScheduleSearchCrtieria(request.getCriteria()));
	    GListSchedulesResponse.Builder response = GListSchedulesResponse.newBuilder();
	    GScheduleSearchResults.Builder results = GScheduleSearchResults.newBuilder();
	    for (ISchedule api : apiResult.getResults()) {
		results.addSchedules(ScheduleModelConverter.asGrpcSchedule(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(ScheduleManagementGrpc.METHOD_LIST_SCHEDULES, e, responseObserver);
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
	try {
	    GrpcUtils.logServerMethodEntry(ScheduleManagementGrpc.METHOD_DELETE_SCHEDULE);
	    ISchedule apiResult = getScheduleManagement().deleteSchedule(request.getToken(), request.getForce());
	    GDeleteScheduleResponse.Builder response = GDeleteScheduleResponse.newBuilder();
	    response.setSchedule(ScheduleModelConverter.asGrpcSchedule(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(ScheduleManagementGrpc.METHOD_DELETE_SCHEDULE, e, responseObserver);
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
	try {
	    GrpcUtils.logServerMethodEntry(ScheduleManagementGrpc.METHOD_CREATE_SCHEDULED_JOB);
	    IScheduledJobCreateRequest apiRequest = ScheduleModelConverter
		    .asApiScheduledJobCreateRequest(request.getRequest());
	    IScheduledJob apiResult = getScheduleManagement().createScheduledJob(apiRequest);
	    GCreateScheduledJobResponse.Builder response = GCreateScheduledJobResponse.newBuilder();
	    response.setScheduledJob(ScheduleModelConverter.asGrpcScheduledJob(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(ScheduleManagementGrpc.METHOD_CREATE_SCHEDULED_JOB, e,
		    responseObserver);
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
	try {
	    GrpcUtils.logServerMethodEntry(ScheduleManagementGrpc.METHOD_UPDATE_SCHEDULED_JOB);
	    IScheduledJobCreateRequest apiRequest = ScheduleModelConverter
		    .asApiScheduledJobCreateRequest(request.getRequest());
	    IScheduledJob apiResult = getScheduleManagement().updateScheduledJob(request.getToken(), apiRequest);
	    GUpdateScheduledJobResponse.Builder response = GUpdateScheduledJobResponse.newBuilder();
	    response.setScheduledJob(ScheduleModelConverter.asGrpcScheduledJob(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(ScheduleManagementGrpc.METHOD_UPDATE_SCHEDULED_JOB, e,
		    responseObserver);
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
	try {
	    GrpcUtils.logServerMethodEntry(ScheduleManagementGrpc.METHOD_GET_SCHEDULED_JOB_BY_TOKEN);
	    IScheduledJob apiResult = getScheduleManagement().getScheduledJobByToken(request.getToken());
	    GGetScheduledJobByTokenResponse.Builder response = GGetScheduledJobByTokenResponse.newBuilder();
	    if (apiResult != null) {
		response.setScheduledJob(ScheduleModelConverter.asGrpcScheduledJob(apiResult));
	    }
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(ScheduleManagementGrpc.METHOD_GET_SCHEDULED_JOB_BY_TOKEN, e,
		    responseObserver);
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
	try {
	    GrpcUtils.logServerMethodEntry(ScheduleManagementGrpc.METHOD_LIST_SCHEDULED_JOBS);
	    ISearchResults<IScheduledJob> apiResult = getScheduleManagement()
		    .listScheduledJobs(ScheduleModelConverter.asApiScheduledJobSearchCrtieria(request.getCriteria()));
	    GListScheduledJobsResponse.Builder response = GListScheduledJobsResponse.newBuilder();
	    GScheduledJobSearchResults.Builder results = GScheduledJobSearchResults.newBuilder();
	    for (IScheduledJob api : apiResult.getResults()) {
		results.addScheduledJobs(ScheduleModelConverter.asGrpcScheduledJob(api));
	    }
	    results.setCount(apiResult.getNumResults());
	    response.setResults(results.build());
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(ScheduleManagementGrpc.METHOD_LIST_SCHEDULED_JOBS, e,
		    responseObserver);
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
	try {
	    GrpcUtils.logServerMethodEntry(ScheduleManagementGrpc.METHOD_DELETE_SCHEDULED_JOB);
	    IScheduledJob apiResult = getScheduleManagement().deleteScheduledJob(request.getToken(),
		    request.getForce());
	    GDeleteScheduledJobResponse.Builder response = GDeleteScheduledJobResponse.newBuilder();
	    response.setScheduledJob(ScheduleModelConverter.asGrpcScheduledJob(apiResult));
	    responseObserver.onNext(response.build());
	    responseObserver.onCompleted();
	} catch (Throwable e) {
	    GrpcUtils.handleServerMethodException(ScheduleManagementGrpc.METHOD_DELETE_SCHEDULED_JOB, e,
		    responseObserver);
	}
    }

    protected IScheduleManagement getScheduleManagement() {
	return scheduleManagement;
    }

    protected void setScheduleManagement(IScheduleManagement scheduleManagement) {
	this.scheduleManagement = scheduleManagement;
    }
}