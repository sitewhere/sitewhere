/*
 * Copyright (c) SiteWhere, LLC. All rights reserved. http://www.sitewhere.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.sitewhere.grpc.model.converter;

import com.sitewhere.grpc.model.ScheduleModel.GScheduleCreateRequest;
import com.sitewhere.grpc.model.ScheduleModel.GTriggerType;
import com.sitewhere.rest.model.scheduling.request.ScheduleCreateRequest;
import com.sitewhere.spi.SiteWhereException;
import com.sitewhere.spi.scheduling.TriggerType;
import com.sitewhere.spi.scheduling.request.IScheduleCreateRequest;

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
	api.setToken(grpc.getToken());
	api.setName(grpc.getName());
	api.setTriggerType(ScheduleModelConverter.asApiTriggerType(grpc.getTriggerType()));
	api.setStartDate(grpc.hasStartDate() ? CommonModelConverter.asDate(grpc.getStartDate()) : null);
	api.setEndDate(grpc.hasEndDate() ? CommonModelConverter.asDate(grpc.getEndDate()) : null);
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
	grpc.setToken(api.getToken());
	grpc.setName(api.getName());
	grpc.setTriggerType(ScheduleModelConverter.asGrpcTriggerType(api.getTriggerType()));
	if (api.getStartDate() != null) {
	    grpc.setStartDate(CommonModelConverter.asGrpcTimestamp(api.getStartDate()));
	}
	if (api.getEndDate() != null) {
	    grpc.setEndDate(CommonModelConverter.asGrpcTimestamp(api.getEndDate()));
	}
	grpc.putAllTriggerConfiguration(api.getTriggerConfiguration());
	grpc.putAllMetadata(api.getMetadata());
	return grpc.build();
    }
}
