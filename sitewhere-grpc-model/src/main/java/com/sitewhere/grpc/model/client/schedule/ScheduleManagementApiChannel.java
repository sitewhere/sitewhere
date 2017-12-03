/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.client.schedule;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sitewhere.grpc.model.GrpcUtils;
import com.sitewhere.grpc.model.client.ApiChannel;
import com.sitewhere.grpc.model.client.GrpcChannel;
import com.sitewhere.grpc.model.converter.ScheduleModelConverter;
import com.sitewhere.grpc.model.spi.client.IScheduleManagementApiChannel;
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
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.microservice.IMicroservice;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;
import com.sitewhere.spi.tracing.ITracerProvider;

/**
 * Supports SiteWhere schedule management APIs on top of a
 * {@link ScheduleManagementGrpcChannel}.
 * 
 * @author Derek
 */
public class ScheduleManagementApiChannel extends ApiChannel<ScheduleManagementGrpcChannel>
	implements IScheduleManagementApiChannel {

    /** Static logger instance */
    private static Logger LOGGER = LogManager.getLogger();

    public ScheduleManagementApiChannel(IMicroservice microservice, String host) {
	super(microservice, host);
    }

    /*
     * @see
     * com.sitewhere.grpc.model.spi.IApiChannel#createGrpcChannel(com.sitewhere.spi.
     * tracing.ITracerProvider, java.lang.String)
     */
    @Override
    public GrpcChannel createGrpcChannel(ITracerProvider tracerProvider, String host) {
	return new ScheduleManagementGrpcChannel(tracerProvider, host,
		getMicroservice().getInstanceSettings().getGrpcPort());
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#createSchedule(com.sitewhere
     * .spi.scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public ISchedule createSchedule(IScheduleCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(ScheduleManagementGrpc.METHOD_CREATE_SCHEDULE);
	    GCreateScheduleRequest.Builder grequest = GCreateScheduleRequest.newBuilder();
	    grequest.setRequest(ScheduleModelConverter.asGrpcScheduleCreateRequest(request));
	    GCreateScheduleResponse gresponse = getGrpcChannel().getBlockingStub().createSchedule(grequest.build());
	    ISchedule response = (gresponse.hasSchedule())
		    ? ScheduleModelConverter.asApiSchedule(gresponse.getSchedule())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.METHOD_CREATE_SCHEDULE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.METHOD_CREATE_SCHEDULE, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#updateSchedule(java.lang.
     * String, com.sitewhere.spi.scheduling.request.IScheduleCreateRequest)
     */
    @Override
    public ISchedule updateSchedule(String token, IScheduleCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(ScheduleManagementGrpc.METHOD_UPDATE_SCHEDULE);
	    GUpdateScheduleRequest.Builder grequest = GUpdateScheduleRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setRequest(ScheduleModelConverter.asGrpcScheduleCreateRequest(request));
	    GUpdateScheduleResponse gresponse = getGrpcChannel().getBlockingStub().updateSchedule(grequest.build());
	    ISchedule response = (gresponse.hasSchedule())
		    ? ScheduleModelConverter.asApiSchedule(gresponse.getSchedule())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.METHOD_UPDATE_SCHEDULE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.METHOD_UPDATE_SCHEDULE, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#getScheduleByToken(java.lang
     * .String)
     */
    @Override
    public ISchedule getScheduleByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(ScheduleManagementGrpc.METHOD_GET_SCHEDULE_BY_TOKEN);
	    GGetScheduleByTokenRequest.Builder grequest = GGetScheduleByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetScheduleByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getScheduleByToken(grequest.build());
	    ISchedule response = (gresponse.hasSchedule())
		    ? ScheduleModelConverter.asApiSchedule(gresponse.getSchedule())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.METHOD_GET_SCHEDULE_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.METHOD_GET_SCHEDULE_BY_TOKEN, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#listSchedules(com.sitewhere.
     * spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<ISchedule> listSchedules(ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(ScheduleManagementGrpc.METHOD_LIST_SCHEDULES);
	    GListSchedulesRequest.Builder grequest = GListSchedulesRequest.newBuilder();
	    grequest.setCriteria(ScheduleModelConverter.asGrpcScheduleSearchCriteria(criteria));
	    GListSchedulesResponse gresponse = getGrpcChannel().getBlockingStub().listSchedules(grequest.build());
	    ISearchResults<ISchedule> results = ScheduleModelConverter
		    .asApiScheduleSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.METHOD_LIST_SCHEDULES, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.METHOD_LIST_SCHEDULES, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#deleteSchedule(java.lang.
     * String, boolean)
     */
    @Override
    public ISchedule deleteSchedule(String token, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(ScheduleManagementGrpc.METHOD_DELETE_SCHEDULE);
	    GDeleteScheduleRequest.Builder grequest = GDeleteScheduleRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setForce(force);
	    GDeleteScheduleResponse gresponse = getGrpcChannel().getBlockingStub().deleteSchedule(grequest.build());
	    ISchedule response = (gresponse.hasSchedule())
		    ? ScheduleModelConverter.asApiSchedule(gresponse.getSchedule())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.METHOD_DELETE_SCHEDULE, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.METHOD_DELETE_SCHEDULE, t);
	}
    }

    /*
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#createScheduledJob(com.
     * sitewhere.spi.scheduling.request.IScheduledJobCreateRequest)
     */
    @Override
    public IScheduledJob createScheduledJob(IScheduledJobCreateRequest request) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(ScheduleManagementGrpc.METHOD_CREATE_SCHEDULED_JOB);
	    GCreateScheduledJobRequest.Builder grequest = GCreateScheduledJobRequest.newBuilder();
	    grequest.setRequest(ScheduleModelConverter.asGrpcScheduledJobCreateRequest(request));
	    GCreateScheduledJobResponse gresponse = getGrpcChannel().getBlockingStub()
		    .createScheduledJob(grequest.build());
	    IScheduledJob response = (gresponse.hasScheduledJob())
		    ? ScheduleModelConverter.asApiScheduledJob(gresponse.getScheduledJob())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.METHOD_CREATE_SCHEDULED_JOB, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.METHOD_CREATE_SCHEDULED_JOB, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#updateScheduledJob(java.lang
     * .String, com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest)
     */
    @Override
    public IScheduledJob updateScheduledJob(String token, IScheduledJobCreateRequest request)
	    throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(ScheduleManagementGrpc.METHOD_UPDATE_SCHEDULED_JOB);
	    GUpdateScheduledJobRequest.Builder grequest = GUpdateScheduledJobRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setRequest(ScheduleModelConverter.asGrpcScheduledJobCreateRequest(request));
	    GUpdateScheduledJobResponse gresponse = getGrpcChannel().getBlockingStub()
		    .updateScheduledJob(grequest.build());
	    IScheduledJob response = (gresponse.hasScheduledJob())
		    ? ScheduleModelConverter.asApiScheduledJob(gresponse.getScheduledJob())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.METHOD_UPDATE_SCHEDULED_JOB, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.METHOD_UPDATE_SCHEDULED_JOB, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#getScheduledJobByToken(java.
     * lang.String)
     */
    @Override
    public IScheduledJob getScheduledJobByToken(String token) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(ScheduleManagementGrpc.METHOD_GET_SCHEDULED_JOB_BY_TOKEN);
	    GGetScheduledJobByTokenRequest.Builder grequest = GGetScheduledJobByTokenRequest.newBuilder();
	    grequest.setToken(token);
	    GGetScheduledJobByTokenResponse gresponse = getGrpcChannel().getBlockingStub()
		    .getScheduledJobByToken(grequest.build());
	    IScheduledJob response = (gresponse.hasScheduledJob())
		    ? ScheduleModelConverter.asApiScheduledJob(gresponse.getScheduledJob())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.METHOD_GET_SCHEDULED_JOB_BY_TOKEN, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.METHOD_GET_SCHEDULED_JOB_BY_TOKEN, t);
	}
    }

    /*
     * @see com.sitewhere.spi.scheduling.IScheduleManagement#listScheduledJobs(com.
     * sitewhere.spi.search.ISearchCriteria)
     */
    @Override
    public ISearchResults<IScheduledJob> listScheduledJobs(ISearchCriteria criteria) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(ScheduleManagementGrpc.METHOD_LIST_SCHEDULED_JOBS);
	    GListScheduledJobsRequest.Builder grequest = GListScheduledJobsRequest.newBuilder();
	    grequest.setCriteria(ScheduleModelConverter.asGrpcScheduledJobSearchCriteria(criteria));
	    GListScheduledJobsResponse gresponse = getGrpcChannel().getBlockingStub()
		    .listScheduledJobs(grequest.build());
	    ISearchResults<IScheduledJob> results = ScheduleModelConverter
		    .asApiScheduledJobSearchResults(gresponse.getResults());
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.METHOD_LIST_SCHEDULED_JOBS, results);
	    return results;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.METHOD_LIST_SCHEDULED_JOBS, t);
	}
    }

    /*
     * @see
     * com.sitewhere.spi.scheduling.IScheduleManagement#deleteScheduledJob(java.lang
     * .String, boolean)
     */
    @Override
    public IScheduledJob deleteScheduledJob(String token, boolean force) throws SiteWhereException {
	try {
	    GrpcUtils.logClientMethodEntry(ScheduleManagementGrpc.METHOD_DELETE_SCHEDULED_JOB);
	    GDeleteScheduledJobRequest.Builder grequest = GDeleteScheduledJobRequest.newBuilder();
	    grequest.setToken(token);
	    grequest.setForce(force);
	    GDeleteScheduledJobResponse gresponse = getGrpcChannel().getBlockingStub()
		    .deleteScheduledJob(grequest.build());
	    IScheduledJob response = (gresponse.hasScheduledJob())
		    ? ScheduleModelConverter.asApiScheduledJob(gresponse.getScheduledJob())
		    : null;
	    GrpcUtils.logClientMethodResponse(ScheduleManagementGrpc.METHOD_DELETE_SCHEDULED_JOB, response);
	    return response;
	} catch (Throwable t) {
	    throw GrpcUtils.handleClientMethodException(ScheduleManagementGrpc.METHOD_DELETE_SCHEDULED_JOB, t);
	}
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