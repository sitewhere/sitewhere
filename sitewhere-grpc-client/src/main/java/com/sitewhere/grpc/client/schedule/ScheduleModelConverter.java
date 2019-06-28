/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.client.schedule;

import java.util.ArrayList;
import java.util.List;

import com.sitewhere.grpc.client.common.converter.CommonModelConverter;
import com.sitewhere.grpc.model.CommonModel.GOptionalString;
import com.sitewhere.grpc.model.ScheduleModel.GSchedule;
import com.sitewhere.grpc.model.ScheduleModel.GScheduleCreateRequest;
import com.sitewhere.grpc.model.ScheduleModel.GScheduleSearchCriteria;
import com.sitewhere.grpc.model.ScheduleModel.GScheduleSearchResults;
import com.sitewhere.grpc.model.ScheduleModel.GScheduledJob;
import com.sitewhere.grpc.model.ScheduleModel.GScheduledJobCreateRequest;
import com.sitewhere.grpc.model.ScheduleModel.GScheduledJobSearchCriteria;
import com.sitewhere.grpc.model.ScheduleModel.GScheduledJobSearchResults;
import com.sitewhere.grpc.model.ScheduleModel.GScheduledJobState;
import com.sitewhere.grpc.model.ScheduleModel.GScheduledJobType;
import com.sitewhere.grpc.model.ScheduleModel.GTriggerType;
import com.sitewhere.rest.model.scheduling.Schedule;
import com.sitewhere.rest.model.scheduling.ScheduledJob;
import com.sitewhere.rest.model.scheduling.request.ScheduleCreateRequest;
import com.sitewhere.rest.model.scheduling.request.ScheduledJobCreateRequest;
import com.sitewhere.rest.model.search.SearchResults;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.ISchedule;
import com.sitewhere.spi.scheduling.IScheduledJob;
import com.sitewhere.spi.scheduling.ScheduledJobState;
import com.sitewhere.spi.scheduling.ScheduledJobType;
import com.sitewhere.spi.scheduling.TriggerType;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;
import com.sitewhere.spi.scheduling.request.IScheduledJobCreateRequest;
import com.sitewhere.spi.search.ISearchCriteria;
import com.sitewhere.spi.search.ISearchResults;

/**
 * Convert schedule entities between SiteWhere API model and GRPC model.
 * 
 * @author Derek
 */
public class ScheduleModelConverter {

    /**
     * Convert trigger type from API to GRPC.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static TriggerType asApiTriggerType(GTriggerType grpc) throws SiteWhereException {
	switch (grpc) {
	case TRIGGER_TYPE_CRON:
	    return TriggerType.CronTrigger;
	case TRIGGER_TYPE_SIMPLE:
	    return TriggerType.SimpleTrigger;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown trigger type: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert trigger type from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GTriggerType asGrpcTriggerType(TriggerType api) throws SiteWhereException {
	switch (api) {
	case CronTrigger:
	    return GTriggerType.TRIGGER_TYPE_CRON;
	case SimpleTrigger:
	    return GTriggerType.TRIGGER_TYPE_SIMPLE;
	}
	throw new SiteWhereException("Unknown trigger type: " + api.name());
    }

    /**
     * Convert schedule create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ScheduleCreateRequest asApiScheduleCreateRequest(GScheduleCreateRequest grpc)
	    throws SiteWhereException {
	ScheduleCreateRequest api = new ScheduleCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setName(grpc.hasName() ? grpc.getName().getValue() : null);
	api.setTriggerType(ScheduleModelConverter.asApiTriggerType(grpc.getTriggerType()));
	api.setStartDate(CommonModelConverter.asApiDate(grpc.getStartDate()));
	api.setEndDate(CommonModelConverter.asApiDate(grpc.getEndDate()));
	api.setTriggerConfiguration(grpc.getTriggerConfigurationMap());
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert schedule create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GScheduleCreateRequest asGrpcScheduleCreateRequest(IScheduleCreateRequest api)
	    throws SiteWhereException {
	GScheduleCreateRequest.Builder grpc = GScheduleCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	if (api.getName() != null) {
	    grpc.setName(GOptionalString.newBuilder().setValue(api.getName()));
	}
	grpc.setTriggerType(ScheduleModelConverter.asGrpcTriggerType(api.getTriggerType()));
	grpc.setStartDate(CommonModelConverter.asGrpcDate(api.getStartDate()));
	grpc.setEndDate(CommonModelConverter.asGrpcDate(api.getEndDate()));
	grpc.putAllTriggerConfiguration(api.getTriggerConfiguration());
	grpc.putAllMetadata(api.getMetadata());
	return grpc.build();
    }

    /**
     * Convert schedule search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ISearchCriteria asApiScheduleSearchCrtieria(GScheduleSearchCriteria grpc) throws SiteWhereException {
	return CommonModelConverter.asApiSearchCriteria(grpc.getPaging());
    }

    /**
     * Convert schedule search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GScheduleSearchCriteria asGrpcScheduleSearchCriteria(ISearchCriteria api) throws SiteWhereException {
	GScheduleSearchCriteria.Builder grpc = GScheduleSearchCriteria.newBuilder();
	grpc.setPaging(CommonModelConverter.asGrpcPaging(api));
	return grpc.build();
    }

    /**
     * Convert schedule search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<ISchedule> asApiScheduleSearchResults(GScheduleSearchResults response)
	    throws SiteWhereException {
	List<ISchedule> results = new ArrayList<ISchedule>();
	for (GSchedule grpc : response.getSchedulesList()) {
	    results.add(ScheduleModelConverter.asApiSchedule(grpc));
	}
	return new SearchResults<ISchedule>(results, response.getCount());
    }

    /**
     * Convert schedule from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static Schedule asApiSchedule(GSchedule grpc) throws SiteWhereException {
	Schedule api = new Schedule();
	api.setName(grpc.getName());
	api.setTriggerType(ScheduleModelConverter.asApiTriggerType(grpc.getTriggerType()));
	api.setStartDate(CommonModelConverter.asApiDate(grpc.getStartDate()));
	api.setEndDate(CommonModelConverter.asApiDate(grpc.getEndDate()));
	api.setTriggerConfiguration(grpc.getTriggerConfigurationMap());
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert schedule from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GSchedule asGrpcSchedule(ISchedule api) throws SiteWhereException {
	GSchedule.Builder grpc = GSchedule.newBuilder();
	grpc.setName(api.getName());
	grpc.setTriggerType(ScheduleModelConverter.asGrpcTriggerType(api.getTriggerType()));
	grpc.setStartDate(CommonModelConverter.asGrpcDate(api.getStartDate()));
	grpc.setEndDate(CommonModelConverter.asGrpcDate(api.getEndDate()));
	grpc.putAllTriggerConfiguration(api.getTriggerConfiguration());
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }

    /**
     * Convert scheduled job type from API to GRPC.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ScheduledJobType asApiScheduledJobType(GScheduledJobType grpc) throws SiteWhereException {
	switch (grpc) {
	case SCHEDULED_JOB_TYPE_BATCH_COMMAND_INVOCATION:
	    return ScheduledJobType.BatchCommandInvocation;
	case SCHEDULED_JOB_TYPE_COMMAND_INVOCATION:
	    return ScheduledJobType.CommandInvocation;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown scheduled job type: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert schedule job type from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GScheduledJobType asGrpcScheduledJobType(ScheduledJobType api) throws SiteWhereException {
	switch (api) {
	case BatchCommandInvocation:
	    return GScheduledJobType.SCHEDULED_JOB_TYPE_BATCH_COMMAND_INVOCATION;
	case CommandInvocation:
	    return GScheduledJobType.SCHEDULED_JOB_TYPE_COMMAND_INVOCATION;
	}
	throw new SiteWhereException("Unknown scheduled job type: " + api.name());
    }

    /**
     * Convert scheduled job state from API to GRPC.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ScheduledJobState asApiScheduledJobState(GScheduledJobState grpc) throws SiteWhereException {
	switch (grpc) {
	case SCHEDULED_JOB_STATE_ACTIVE:
	    return ScheduledJobState.Active;
	case SCHEDULED_JOB_STATE_COMPLETE:
	    return ScheduledJobState.Complete;
	case SCHEDULED_JOB_STATE_UNSUBMITTED:
	    return ScheduledJobState.Unsubmitted;
	case UNRECOGNIZED:
	    throw new SiteWhereException("Unknown scheduled job state: " + grpc.name());
	}
	return null;
    }

    /**
     * Convert schedule job state from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GScheduledJobState asGrpcScheduledJobState(ScheduledJobState api) throws SiteWhereException {
	switch (api) {
	case Active:
	    return GScheduledJobState.SCHEDULED_JOB_STATE_ACTIVE;
	case Complete:
	    return GScheduledJobState.SCHEDULED_JOB_STATE_COMPLETE;
	case Unsubmitted:
	    return GScheduledJobState.SCHEDULED_JOB_STATE_UNSUBMITTED;
	}
	throw new SiteWhereException("Unknown scheduled job state: " + api.name());
    }

    /**
     * Convert scheduled job create request from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ScheduledJobCreateRequest asApiScheduledJobCreateRequest(GScheduledJobCreateRequest grpc)
	    throws SiteWhereException {
	ScheduledJobCreateRequest api = new ScheduledJobCreateRequest();
	api.setToken(grpc.hasToken() ? grpc.getToken().getValue() : null);
	api.setScheduleToken(grpc.getScheduleToken());
	api.setJobType(ScheduleModelConverter.asApiScheduledJobType(grpc.getJobType()));
	api.setJobConfiguration(grpc.getJobConfigurationMap());
	api.setJobState(ScheduleModelConverter.asApiScheduledJobState(grpc.getJobState()));
	api.setMetadata(grpc.getMetadataMap());
	return api;
    }

    /**
     * Convert scheduled job create request from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GScheduledJobCreateRequest asGrpcScheduledJobCreateRequest(IScheduledJobCreateRequest api)
	    throws SiteWhereException {
	GScheduledJobCreateRequest.Builder grpc = GScheduledJobCreateRequest.newBuilder();
	if (api.getToken() != null) {
	    grpc.setToken(GOptionalString.newBuilder().setValue(api.getToken()));
	}
	grpc.setScheduleToken(api.getScheduleToken());
	grpc.setJobType(ScheduleModelConverter.asGrpcScheduledJobType(api.getJobType()));
	grpc.putAllJobConfiguration(api.getJobConfiguration());
	grpc.setJobState(ScheduleModelConverter.asGrpcScheduledJobState(api.getJobState()));
	if (api.getMetadata() != null) {
	    grpc.putAllMetadata(api.getMetadata());
	}
	return grpc.build();
    }

    /**
     * Convert scheduled job search criteria from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ISearchCriteria asApiScheduledJobSearchCrtieria(GScheduledJobSearchCriteria grpc)
	    throws SiteWhereException {
	return CommonModelConverter.asApiSearchCriteria(grpc.getPaging());
    }

    /**
     * Convert scheduled job search criteria from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GScheduledJobSearchCriteria asGrpcScheduledJobSearchCriteria(ISearchCriteria api)
	    throws SiteWhereException {
	GScheduledJobSearchCriteria.Builder grpc = GScheduledJobSearchCriteria.newBuilder();
	grpc.setPaging(CommonModelConverter.asGrpcPaging(api));
	return grpc.build();
    }

    /**
     * Convert scheduled job search results from GRPC to API.
     * 
     * @param response
     * @return
     * @throws SiteWhereException
     */
    public static ISearchResults<IScheduledJob> asApiScheduledJobSearchResults(GScheduledJobSearchResults response)
	    throws SiteWhereException {
	List<IScheduledJob> results = new ArrayList<IScheduledJob>();
	for (GScheduledJob grpc : response.getScheduledJobsList()) {
	    results.add(ScheduleModelConverter.asApiScheduledJob(grpc));
	}
	return new SearchResults<IScheduledJob>(results, response.getCount());
    }

    /**
     * Convert scheduled job from GRPC to API.
     * 
     * @param grpc
     * @return
     * @throws SiteWhereException
     */
    public static ScheduledJob asApiScheduledJob(GScheduledJob grpc) throws SiteWhereException {
	ScheduledJob api = new ScheduledJob();
	api.setScheduleToken(grpc.getScheduleToken());
	api.setJobType(ScheduleModelConverter.asApiScheduledJobType(grpc.getJobType()));
	api.setJobConfiguration(grpc.getJobConfigurationMap());
	api.setJobState(ScheduleModelConverter.asApiScheduledJobState(grpc.getJobState()));
	CommonModelConverter.setEntityInformation(api, grpc.getEntityInformation());
	return api;
    }

    /**
     * Convert scheduled job from API to GRPC.
     * 
     * @param api
     * @return
     * @throws SiteWhereException
     */
    public static GScheduledJob asGrpcScheduledJob(IScheduledJob api) throws SiteWhereException {
	GScheduledJob.Builder grpc = GScheduledJob.newBuilder();
	grpc.setScheduleToken(api.getScheduleToken());
	grpc.setJobType(ScheduleModelConverter.asGrpcScheduledJobType(api.getJobType()));
	grpc.putAllJobConfiguration(api.getJobConfiguration());
	grpc.setJobState(ScheduleModelConverter.asGrpcScheduledJobState(api.getJobState()));
	grpc.setEntityInformation(CommonModelConverter.asGrpcEntityInformation(api));
	return grpc.build();
    }
}